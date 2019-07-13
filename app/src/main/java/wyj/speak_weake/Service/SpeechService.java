package wyj.speak_weake.Service;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.annotation.Nullable;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttPersistenceException;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.TimeZone;

import wyj.speak_weake.Board.CallAlarm;
import wyj.speak_weake.ContactInfoDao;
import wyj.speak_weake.SpeechConstant;
import wyj.speak_weake.Util.Hyutil_time;
import wyj.speak_weake.Util.OkgoUtils;
import wyj.speak_weake.Util.OnRequestResult;
import wyj.speak_weake.Util.ThirdJumpUtils;

import static com.lzy.okgo.utils.HttpUtils.runOnUiThread;
import static wyj.speak_weake.constant.RequestUrl.BASIC_URL;
import static wyj.speak_weake.constant.RequestUrl.SRORY_URL;

public class SpeechService extends Service implements RecognitionListener,OnRequestResult {
    EventManager asr;
    EventManager wp;
    final Random RANDOM = new Random();
    final int MAX_RANGE=12;//故事个数
    private SpeechSynthesizer mSpeechSynthesizer;
    private ContactInfoDao dao;
    Calendar c=Calendar.getInstance();//使用日历类

    //搭建MQTTAndroid 端连接数据库
    //demo主要是按照空调的来，操作步骤

    String TelephonyIMEI="";
    private MqttClient client;//client
    private MqttConnectOptions options;//配置
    MqttConnectThread mqttConnectThread = new MqttConnectThread();//连接服务器任务


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @SuppressLint("MissingPermission")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //speakTTS("欢迎进入语音识别与合成的老人陪护系统");
        // LogUtils.e("origin".contains("or"));
        initWp();
        dao=new ContactInfoDao(this);

        //mqtt
        TelephonyManager mTm = (TelephonyManager)this.getSystemService(TELEPHONY_SERVICE);
        TelephonyIMEI = mTm.getDeviceId();
        MyMqttInit();
        mqttConnectThread.start();


        LogUtils.e("speechService Start");
        return super.onStartCommand(intent, flags, startId);
    }

    private void MyMqttInit() {
        try
        {
            client = new MqttClient("tcp://47.100.99.68:1883",TelephonyIMEI,new MemoryPersistence());
        } catch (MqttException e) {
            e.printStackTrace();
        }
        options = new MqttConnectOptions();//MQTT的连接设置
        options.setUserName("test01");
        options.setPassword("test01".toCharArray());
        options.setCleanSession(true);//设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
        options.setConnectionTimeout(10);// 设置连接超时时间 单位为秒
        options.setKeepAliveInterval(20);// 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
    }

    private void initWp() {
        wp = EventManagerFactory.create(this, "wp");
        EventListener wpListener = new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int
                    offset, int length) {
                Log.d("tata", String.format("event: name=%s, params=%s", name, params));
                //唤醒事件
                if (name.equals("wp.data")) {
                    try {
                        JSONObject json = new JSONObject(params);
                        int errorCode = json.getInt("errorCode");
                        if (errorCode == 0) {
                            //唤醒成功
                            Log.i("tata", "唤醒成功");
                            wp.send(SpeechConstant.WAKEUP_STOP, null, null, 0, 0);
                            wp = null;
                            speakTTS("请问有什么可以帮到您？");
                            try {
                                Thread.sleep(2000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            initAsr();
                        } else {
                            //唤醒失败
                            Log.i("tata", "唤醒失败");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                } else if ("wp.exit".equals(name)) {
                    //唤醒已停止
                    Log.i("tata", "唤醒停止");
                }
            }
        };
        wp.registerListener(wpListener);
        HashMap map = new HashMap();
        map.put(SpeechConstant.WP_WORDS_FILE, "assets://WakeUp.bin");
        wp.send(SpeechConstant.WAKEUP_START, com.alibaba.fastjson.JSONObject.toJSONString(map), null, 0, 0);
    }

    //mqtt
        /*连接服务器任务*/
    class MqttConnectThread extends Thread
    {
        public void run()
        {
            try
            {
                client.connect(options);//连接服务器,连接不上会阻塞在这
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接成功", Toast.LENGTH_LONG).show();
                    }
                });
            }
            catch (MqttSecurityException e)
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接失败，安全问题", Toast.LENGTH_LONG).show();
                    }
                });
            }
            catch (MqttException e)
            {
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(getApplicationContext(), "连接失败，网络问题", Toast.LENGTH_LONG).show();
                    }
                });
            }
        }
    }


    private void initAsr() {
        asr = EventManagerFactory.create(this, "asr");
        EventListener asrListener = new EventListener() {
            @Override
            public void onEvent(String name, String params, byte[] data, int offset, int length) {
                Log.d("tata", String.format("event: name=%s, params=%s", name, params));
                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_READY)) {
                    // 引擎就绪，可以说话，一般在收到此事件后通过UI通知用户可以说话了
                    Log.i("tata", "可以说话了");

                }
                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_FINISH)) {
                    // 识别结束
                    Log.i("tata", "识别结束");
                    asr.unregisterListener(this);
                    asr = null;
                    initWp();
                }
                if (name.equals(SpeechConstant.CALLBACK_EVENT_ASR_PARTIAL)) {   //dao.add();
                    if (params != null && params.contains("\"final_result\"")) {
                        com.alibaba.fastjson.JSONObject res = com.alibaba.fastjson.JSONObject.parseObject(params);
                        String finalRes = res.getString("best_result");
                        LogUtils.e("tata",params);
                        //speakTTS("识别成功，你说的是" + finalRes);


                        final String[] shuju = new String[200];//数组


                        if(finalRes.contains("美团")){
                            try{
                                ThirdJumpUtils.Jump(SpeechService.this,"com.sankuai.meituan.takeoutnew");
                                dao.addevent("hy","打开美团");//添加数据
                                speakTTS("正在打开美团");
                            }catch (Exception e){
                                speakTTS("当前没有安装美团");
                                dao.addevent("hy","打开美团");//添加数据
                            }
                        }
                        else if(finalRes.contains("饿了")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "me.ele");
                                dao.addevent("hy","打开饿了吗软件");//添加数据
                                speakTTS("正在打开饿了吗");
                            }catch (Exception e){
                                speakTTS("当前没有安装饿了吗");
                                dao.addevent("hy","打开饿了吗软件");//添加数据
                            }
                        }
                        else if(finalRes.contains("美颜相机")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "com.meitu.meiyancamera");
                                dao.addevent("hy","打开美颜相机");//添加数据
                                speakTTS("正在打开美颜相机");
                            }catch (Exception e){
                                speakTTS("当前没有安装美颜相机");
                                dao.addevent("hy","打开美颜相机");//添加数据
                            }
                        }
                        else if(finalRes.contains("携程")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "ctrip.android.view");
                                dao.addevent("hy","打开携程");//添加数据
                                speakTTS("正在打开携程");

                            }catch (Exception e){
                                dao.addevent("hy","打开携程");//添加数据
                                speakTTS("当前没有安装携程");
                            }
                        }
                        else if(finalRes.contains("优酷")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "com.youku.phone");
                                dao.addevent("hy","打开优酷");//添加数据
                                speakTTS("正在打开优酷");
                            }catch (Exception e){
                                speakTTS("当前没有安装优酷");
                            }
                        }
                        else if(finalRes.contains("爱奇艺")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "com.qiyi.video");
                                dao.addevent("hy","打开爱奇艺");//添加数据
                                speakTTS("正在打开爱奇艺");
                            }catch (Exception e){
                                dao.addevent("hy","打开爱奇艺");//添加数据
                                speakTTS("当前没有安装爱奇艺");
                            }
                        }
                        else  if(finalRes.contains("支付宝")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "com.eg.android.AlipayGphone");
                                dao.addevent("hy","打开支付宝");//添加数据
                                speakTTS("正在打开支付宝");
                            }catch (Exception e){
                                dao.addevent("hy","打开支付宝");//添加数据
                                speakTTS("当前没有安装支付宝");
                            }
                        }
                        else if(finalRes.contains("淘宝")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "com.taobao.taobao");
                                dao.addevent("hy","打开淘宝");//添加数据
                                speakTTS("正在打开淘宝");
                            }catch (Exception e){
                                dao.addevent("hy","打开淘宝");//添加数据
                                speakTTS("当前没有安装淘宝");
                            }
                        }
                        else if(finalRes.contains("微信")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "com.tencent.mm");
                                speakTTS("正在打开微信");
                            }catch (Exception e){
                                speakTTS("当前没有安装微信");
                            }
                        }
                        else if(finalRes.contains("QQ")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "com.tencent.mobileqq");
                                speakTTS("正在打开QQ");
                            }catch (Exception e){
                                speakTTS("当前没有安装QQ");
                            }
                        }
                        else if(finalRes.contains("滴滴")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "com.sdu.didi.psnger");
                                speakTTS("正在打开滴滴出行");
                            }catch (Exception e){
                                speakTTS("当前没有安装滴滴出行");
                            }
                        }
                        else if(finalRes.contains("京东")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "com.jingdong.app.mall");
                                speakTTS("正在打开京东");
                            }catch (Exception e){
                                speakTTS("当前没有安装京东");
                            }
                        }
                        else if(finalRes.contains("高德") ){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "com.autonavi.minimap");
                                speakTTS("正在打开高德地图");
                            }catch (Exception e){
                                speakTTS("当前没有安装高德地图");
                            }
                        }
                        else if(finalRes.contains("网易云")){
                            try {
                                ThirdJumpUtils.Jump(SpeechService.this, "com.netease.cloudmusic");
                                speakTTS("正在打开网易云音乐");
                            }catch (Exception e){
                                speakTTS("当前没有安装网易云音乐");
                            }
                        }
                        else if(finalRes.contains("返回")){
                            try {
                                ThirdJumpUtils.openApp(SpeechService.this);
                                speakTTS("主人，你回来了");
                            }catch (Exception e){
                                e.printStackTrace();
                                speakTTS("返回错误");
                            }
                        }
                        else if(finalRes.contains("日期")){
                            try {
                                final Calendar c = Calendar.getInstance();
                                c.setTimeZone(TimeZone.getTimeZone("GMT+8:00"));
                                String mYear = String.valueOf(c.get(Calendar.YEAR)); // 获取当前年份
                                String mMonth = String.valueOf(c.get(Calendar.MONTH) + 1);// 获取当前月份
                                String mDay = String.valueOf(c.get(Calendar.DAY_OF_MONTH));// 获取当前月份的日期号码
                                String mWay = String.valueOf(c.get(Calendar.DAY_OF_WEEK));
                                if("1".equals(mWay)){
                                    mWay ="天";
                                }else if("2".equals(mWay)){
                                    mWay ="一";
                                }else if("3".equals(mWay)){
                                    mWay ="二";
                                }else if("4".equals(mWay)){
                                    mWay ="三";
                                }else if("5".equals(mWay)){
                                    mWay ="四";
                                }else if("6".equals(mWay)){
                                    mWay ="五";
                                }else if("7".equals(mWay)){
                                    mWay ="六";
                                }
                                speakTTS("今天是"+mYear+"年，"+mMonth+"月"+mDay+"日,"+"星期"+mWay);
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }
                        //怎么检测地名
                        else if(finalRes.contains("分钟"))
                            try {
                            //时间处理过程
                            String time_value = finalRes;//进入分钟界面
                            time_value = time_value.trim();
                            String str1 = Hyutil_time.result(time_value);//使用静态的方法，使用集合类
                            int str1_value=Integer.valueOf(str1);
                            speakTTS("正在为您设置:"+str1+"分钟的闹钟");
                            c.setTimeInMillis(System.currentTimeMillis());
                            int mHour = c.get(Calendar.HOUR_OF_DAY);
                            int mMinute = c.get(Calendar.MINUTE);
                             c.setTimeInMillis(System.currentTimeMillis());
                             c.set(Calendar.HOUR_OF_DAY, mHour);
                             c.set(Calendar.MINUTE, mMinute+str1_value);
//                             LogUtils.e("tata",str1_value+"分的闹钟");
//                             LogUtils.e("tata",mMinute+str1_value+"分的闹钟");
                             c.set(Calendar.SECOND, 0);
                             c.set(Calendar.MILLISECOND, 0);
                             Intent intent = new Intent(SpeechService.this, CallAlarm.class);
                             PendingIntent sender = PendingIntent.getBroadcast(
                                     SpeechService.this, 0, intent, 0);
                             AlarmManager am;
                             am = (AlarmManager) getSystemService(ALARM_SERVICE);
                             if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                 am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
                             }
                        } catch (Exception e) {
                            e.printStackTrace();
                            speakTTS("错误");
                        }

                        else if(finalRes.contains("天气")){//怎么检测地名
                            //取城市名字
                            String city_value = finalRes;//进入分钟界面
                            LogUtils.e("weather",finalRes);
                            city_value = city_value.trim();
                            String str2 = Hyutil_time.result_city(city_value);
                            //天气信息处理
                            Map<String, String> params1 = new HashMap<>();//使用Map函数进行天气测试 键值对
                            OkgoUtils.getInstance().get("http://v.juhe.cn/weather/index?cityname=" + str2 + "&dtype=json&format=1&key=75c6c0a4c52e3a2dd7b614cd91237ced", params1, 33, SpeechService.this);

                        }

                        else if(finalRes.contains("故事") || finalRes.contains("笑话")){
                            Map<String, String> params2 = new HashMap<>();//使用Map函数进行天气测试 键值对
                            OkgoUtils.getInstance().get(SRORY_URL, params2, 34, SpeechService.this);
                        }
                        else if(finalRes.contains("开") && finalRes.contains("灯")){
                            MqttMessage msgMessage = null;//通过MQTT远程语音
                            shuju[55] = "{d04,01}";
                            msgMessage = new MqttMessage( shuju[55].getBytes());
                            try
                            {
                                client.publish("esp/test",msgMessage);
                            } catch (MqttPersistenceException e) {
                                e.printStackTrace();
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            catch (Exception e) {
                            }
                        }
                        else if(finalRes.contains("关") && finalRes.contains("灯")){
                            MqttMessage msgMessage = null;//通过MQTT远程语音
                            shuju[56] = "{d04,02}";
                            msgMessage = new MqttMessage( shuju[56].getBytes());
                            try
                            {
                                client.publish("esp/test",msgMessage);
                            } catch (MqttPersistenceException e) {
                                e.printStackTrace();
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            catch (Exception e) {
                            }
                        }
                    }
                }
            }
        };

        asr.registerListener(asrListener);
        String json = "{\"accept-audio-data\":false,\"disable-punctuation\":false,\"accept-audio-volume\":true,\"pid\":1536}";
        asr.send(SpeechConstant.ASR_START, json, null, 0, 0);
    }



    @Override
    public void onSuccess(int code, String json) {
        switch (code) {
            case 33:
                //LogUtils.e("tata",json);
                com.alibaba.fastjson.JSONObject res1 = com.alibaba.fastjson.JSONObject.parseObject(json);//解析json数据
                String finalRes = res1.getString("result");
                LogUtils.e("tata",finalRes);//显示null 说明没有值
                com.alibaba.fastjson.JSONObject gradeParse = com.alibaba.fastjson.JSONObject.parseObject(finalRes);
                String finalRes_id=gradeParse.getString("today");
                com.alibaba.fastjson.JSONObject gradeParse1 = com.alibaba.fastjson.JSONObject.parseObject(finalRes_id);
                String finalRes_idzhi=gradeParse1.getString("dressing_advice");//可以了  建议着长袖T恤、衬衫加单裤等服装。年老体弱者宜着针织长袖衬衫、马甲和长裤。
                String temperature_value=gradeParse1.getString("temperature");//temperature温度
                String weather_value=gradeParse1.getString("weather");//天气
                String wind_value=gradeParse1.getString("wind");//风向
                String city_value=gradeParse1.getString("city");//城市名字
                //天气说明需要  温度  天气：阴  风向  时间 地名 建议
                LogUtils.e("tata",city_value+"天气是"+weather_value+"天"+"，风向是"+wind_value+"，温度是"+temperature_value+"，建议"+finalRes_idzhi);
                speakTTS(city_value+"天气是"+weather_value+"天"+"，风向是"+wind_value+"，温度是"+temperature_value+"，建议"+finalRes_idzhi);//语音播报
                break;
            case 34:
                com.alibaba.fastjson.JSONObject res2 = com.alibaba.fastjson.JSONObject.parseObject(json);//解析json数据
                com.alibaba.fastjson.JSONArray array=res2.getJSONArray("result");
//                for (int i = 0; i < array.size(); i++) {
//                    com.alibaba.fastjson.JSONObject jo = (com.alibaba.fastjson.JSONObject) array.get(i);
//                    String finalRes_story1 = jo.getString("content");//LogUtils.e(finalRes_story1);
//                }
                switch (random()) {
                    case 0:
                        com.alibaba.fastjson.JSONObject jo0 = (com.alibaba.fastjson.JSONObject) array.get(0);
                        String finalRes_story0 = jo0.getString("content");//LogUtils.e(finalRes_story1);
                        speakTTS(finalRes_story0);
                        break;
                    case 1:
                        com.alibaba.fastjson.JSONObject jo1 = (com.alibaba.fastjson.JSONObject) array.get(1);
                        String finalRes_story1 = jo1.getString("content");//LogUtils.e(finalRes_story1);
                        speakTTS(finalRes_story1);
                        break;
                    case 2:
                        com.alibaba.fastjson.JSONObject jo2 = (com.alibaba.fastjson.JSONObject) array.get(2);
                        String finalRes_story2 = jo2.getString("content");//LogUtils.e(finalRes_story1);
                        speakTTS(finalRes_story2);
                        break;
                    case 3:
                        com.alibaba.fastjson.JSONObject jo3 = (com.alibaba.fastjson.JSONObject) array.get(3);
                        String finalRes_story3 = jo3.getString("content");//LogUtils.e(finalRes_story1);
                        speakTTS(finalRes_story3);
                        break;
                    case 4:
                        com.alibaba.fastjson.JSONObject jo4 = (com.alibaba.fastjson.JSONObject) array.get(4);
                        String finalRes_story4 = jo4.getString("content");//LogUtils.e(finalRes_story1);
                        speakTTS(finalRes_story4);
                        break;
                    case 5:
                        com.alibaba.fastjson.JSONObject jo5= (com.alibaba.fastjson.JSONObject) array.get(5);
                        String finalRes_story5 = jo5.getString("content");//LogUtils.e(finalRes_story1);
                        speakTTS(finalRes_story5);
                        break;
                    case 6:
                        com.alibaba.fastjson.JSONObject jo6 = (com.alibaba.fastjson.JSONObject) array.get(6);
                        String finalRes_story6 = jo6.getString("content");//LogUtils.e(finalRes_story1);
                        speakTTS(finalRes_story6);
                        break;
                    case 7:
                        com.alibaba.fastjson.JSONObject jo7 = (com.alibaba.fastjson.JSONObject) array.get(7);
                        String finalRes_story7 = jo7.getString("content");//LogUtils.e(finalRes_story1);
                        speakTTS(finalRes_story7);
                        break;
                    case 8:
                        com.alibaba.fastjson.JSONObject jo8 = (com.alibaba.fastjson.JSONObject) array.get(8);
                        String finalRes_story8 = jo8.getString("content");//LogUtils.e(finalRes_story1);
                        speakTTS(finalRes_story8);
                        break;
                    case 9:
                        com.alibaba.fastjson.JSONObject jo9 = (com.alibaba.fastjson.JSONObject) array.get(9);
                        String finalRes_story9 = jo9.getString("content");//LogUtils.e(finalRes_story1);
                        speakTTS(finalRes_story9);
                        break;
//                    case 10:
//                        com.alibaba.fastjson.JSONObject jo10 = (com.alibaba.fastjson.JSONObject) array.get(10);
//                        String finalRes_story10 = jo10.getString("content");//LogUtils.e(finalRes_story1);
//                        speakTTS(finalRes_story10);
//                        break;
//                    case 11:
//                        com.alibaba.fastjson.JSONObject jo11 = (com.alibaba.fastjson.JSONObject) array.get(11);
//                        String finalRes_story11 = jo11.getString("content");//LogUtils.e(finalRes_story1);
//                        speakTTS(finalRes_story11);
//                        break;

                }
                LogUtils.e(array.size());

                break;
        }
    }

    private int random() {
        return RANDOM.nextInt(MAX_RANGE);
    }

    @Override
    public void onFailed(int code, String msg) {

    }

    public void speakTTS(String s) {
        String AppId = "16608080";
        String AppKey = "mbpYmuKU2bFEorfCCDagOK5G";
        String AppSecret = "PtjSqvUGR5ntmjtpNxKQOxh31GXipxdH";

        mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        mSpeechSynthesizer.setContext(this);

        mSpeechSynthesizer.setAppId(AppId);
        mSpeechSynthesizer.setApiKey(AppKey, AppSecret);
        mSpeechSynthesizer.setSpeechSynthesizerListener(new SpeechSynthesizerListener() {//SpeechSynthesizerListener接口
            @Override
            public void onSynthesizeStart(String s) {
                Log.i("tata", "hy语音合成开始 ---- " + s);
            }

            @Override
            public void onSynthesizeDataArrived(String s, byte[] bytes, int i) {

            }

            @Override
            public void onSynthesizeFinish(String s) {
                Log.i("tata", "语音合成完成 ---- " + s);
            }

            @Override
            public void onSpeechStart(String s) {
                Log.i("tata", "hy开始播放 ---- " + s);
            }

            @Override
            public void onSpeechProgressChanged(String s, int i) {

            }

            @Override
            public void onSpeechFinish(String s) {
                Log.i("tata", "hy播放完成 ---- " + s);
                mSpeechSynthesizer.release();//这个地方释放
            }

            @Override
            public void onError(String s, SpeechError speechError) {
                Log.e("tata", "语音合成出错---- " + s);
            }
        });
        mSpeechSynthesizer.auth(TtsMode.ONLINE); // 离在线混合
        mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0"); // 设置发声的人声音，在线生效
        mSpeechSynthesizer.initTts(TtsMode.ONLINE); // 初始化离在线混合模式，如果只需要在线合成功能，使用 TtsMode.ONLINE
        mSpeechSynthesizer.speak(s);

    }

    /**
     * 准备就绪回调
     *
     * @param params
     */
    @Override
    public void onReadyForSpeech(Bundle params) {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onReadyForSpeech=====:" + "");
    }

    /**
     * 开始说话回调
     */
    @Override
    public void onBeginningOfSpeech() {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onBeginningOfSpeech=====:" + "");
    }

    /**
     * 音量变化处理
     *
     * @param rmsdB 音量
     */
    @Override
    public void onRmsChanged(float rmsdB) {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onRmsChanged=====:" + rmsdB);
    }

    /**
     * 录音数据传出处理
     *
     * @param buffer
     */
    @Override
    public void onBufferReceived(byte[] buffer) {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onBufferReceived=====:" + "");
    }

    /**
     * 说话结束回调
     */
    @Override
    public void onEndOfSpeech() {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onEndOfSpeech=====:" + "");
    }

    /**
     * 出错回调
     *
     * @param error 错误代码
     */
    @Override
    public void onError(int error) {
        StringBuilder sb = new StringBuilder();
        switch (error) {
            case SpeechRecognizer.ERROR_AUDIO:
                sb.append("音频问题");
                ToastUtils.showShort("音频问题");
                speakTTS("音频问题");
                break;
            case SpeechRecognizer.ERROR_SPEECH_TIMEOUT:
                sb.append("没有语音输入");
                ToastUtils.showShort("没有语音输入");
                speakTTS("没有语音输入");
                break;
            case SpeechRecognizer.ERROR_CLIENT:
                sb.append("其它客户端错误");
                ToastUtils.showShort("其它客户端错误");
                speakTTS("其它客户端错误");
                break;
            case SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS:
                sb.append("权限不足");
                ToastUtils.showShort("权限不足");
                speakTTS("权限不足");
                break;
            case SpeechRecognizer.ERROR_NETWORK:
                sb.append("网络问题");
                ToastUtils.showShort("网络问题");
                speakTTS("网络问题");
                break;
            case SpeechRecognizer.ERROR_NO_MATCH:
                sb.append("没有匹配的识别结果");
                ToastUtils.showShort("没有匹配的识别结果");
                speakTTS("没有匹配的识别结果");
                break;
            case SpeechRecognizer.ERROR_RECOGNIZER_BUSY:
                sb.append("引擎忙");
                ToastUtils.showShort("引擎忙");
                speakTTS("引擎忙");
                break;
            case SpeechRecognizer.ERROR_SERVER:
                sb.append("服务端错误");
                ToastUtils.showShort("服务端错误");
                speakTTS("服务端错误");
                break;
            case SpeechRecognizer.ERROR_NETWORK_TIMEOUT:
                sb.append("连接超时");
                ToastUtils.showShort("连接超时");
                speakTTS("连接超时");
                break;
        }
    }

    /**
     * 最终结果回调
     *
     * @param results 一些回调的信息
     */
    @Override
    public void onResults(Bundle results) {
        // 获取截取到的词的集合
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest != null) {
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onResults=====nbest:" + nbest);
        }

        float[] array = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        if (array != null) {
            String str = "";
            for (float anArray : array) {
                str += anArray;
            }
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onResults=====array:" + str);
        } else {
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onResults=====array:" + "为空");
        }

        // 获取到Json数据
        String json = results.getString("origin_result");
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onResults=====:" + json);

    }

    /**
     * 临时结果处理,这里可以截取到一些关键词
     *
     * @param results 这里保存着一些说话的关键词
     */
    @Override
    public void onPartialResults(Bundle results) {
        // 获取截取到的词的集合
        ArrayList<String> nbest = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
        if (nbest != null) {
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onPartialResults=====nbest:" + nbest);
        }

        // 获取到不知道干嘛的东西，好像是认证的分数，但是一直为空
        float[] array = results.getFloatArray(SpeechRecognizer.CONFIDENCE_SCORES);
        if (array != null) {
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onPartialResults=====array:" + array.toString());
        } else {
            Log.e("自定义标签", "类名==MainActivity" + "方法名==onPartialResults=====array:" + "为空");
        }

        // 获取到Json数据
        String json = results.getString("origin_result");
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onPartialResults=====:" + json);
    }

    /**
     * 处理事件回调,为将来的一些事件保留的一些东西
     *
     * @param eventType 事件类型
     * @param params    这个可能和上面回调结果的一样，用同样的key去获取
     */
    @Override
    public void onEvent(int eventType, Bundle params) {
        Log.e("自定义标签", "类名==MainActivity" + "方法名==onEvent=====:" + eventType);
    }

}
