package wyj.speak_weake;


import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.AlertDialog;
import android.app.TimePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;


import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import wyj.speak_weake.Board.CallAlarm;
import wyj.speak_weake.Util.OkgoUtils;
import wyj.speak_weake.Util.OnRequestResult;

import static android.content.Context.ALARM_SERVICE;

public class OneFragment extends Fragment implements OnRequestResult {


    BluetoothDevice mmDevice;//蓝牙设备
    BluetoothAdapter mmBluetoothAdapter;//蓝牙适配器
    BluetoothSocket mmBluetoothSocket;//客户端之类的
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    Calendar c= Calendar.getInstance();//使用日历类


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_one_fragment, container, false);
        final String[] shuju = new String[200];
        findBT();//发现蓝牙
        //连接蓝牙
        try {
            openBT();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //使用这个进行数据绑定等等
        View key_tupian,key_zi,qianbao_tupian,qianbao_zi,yanjing_tupian,yanjing_zi;//   语音寻物 钥匙钱包眼镜
        View kaideng_tupian,kaideng_zi,guandeng_tupian,guandeng_zi;// 家居灯之类的
         final EditText city;//final好用

        Button select;//天气
        Button time;//定时


        //语音寻物部分
        key_tupian=view.findViewById(R.id.key_tupian);
        key_zi=view.findViewById(R.id.key_zi);
        qianbao_tupian=view.findViewById(R.id.qianbao_tupian);
        qianbao_zi=view.findViewById(R.id.qianbao_zi);
        yanjing_tupian=view.findViewById(R.id.yanjing_tupian);
        yanjing_zi=view.findViewById(R.id.yanjing_zi);

        //家居灯之类的
        kaideng_tupian=view.findViewById(R.id.kaideng_tupian);
        kaideng_zi=view.findViewById(R.id.kaideng_zi);
        guandeng_tupian=view.findViewById(R.id.guandeng_tupian);
        guandeng_zi=view.findViewById(R.id.guandeng_zi);

        //天气
        city=(EditText)view.findViewById(R.id.city);
        select=view.findViewById(R.id.select);


        //定时提醒
        time=view.findViewById(R.id.time);

        //

        key_tupian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuju[40] = "{d07,01}";
                try {
                    mmOutputStream.write(shuju[40].getBytes());
                    ToastUtils.showShort("发送指令给钥匙");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        key_zi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuju[40] = "{d07,01}";
                try {
                    mmOutputStream.write(shuju[40].getBytes());
                    ToastUtils.showShort("发送指令给钥匙");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        qianbao_tupian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuju[47] = "{d07,03}";
                try {
                    mmOutputStream.write(shuju[47].getBytes());
                    ToastUtils.showShort("发送指令给钱包");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        qianbao_zi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuju[47] = "{d07,03}";
                try {
                    mmOutputStream.write(shuju[47].getBytes());
                    ToastUtils.showShort("发送指令给钱包");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        yanjing_tupian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuju[46] = "{d07,02}";
                try {
                    mmOutputStream.write(shuju[46].getBytes());
                    ToastUtils.showShort("发送指令给眼镜");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        yanjing_zi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuju[46] = "{d07,02}";
                try {
                    mmOutputStream.write(shuju[46].getBytes());
                    ToastUtils.showShort("发送指令给眼镜");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        kaideng_tupian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuju[55] = "{d04,01}";
                try {
                    mmOutputStream.write(shuju[55].getBytes());
                    ToastUtils.showShort("发送指令给开灯");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        kaideng_zi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuju[55] = "{d04,01}";
                try {
                    mmOutputStream.write(shuju[55].getBytes());
                    ToastUtils.showShort("发送指令给开灯");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });


        guandeng_tupian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuju[56] = "{d04,02}";
                try {
                    mmOutputStream.write(shuju[56].getBytes());
                    ToastUtils.showShort("发送指令给关灯");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        guandeng_zi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                shuju[56] = "{d04,02}";
                try {
                    mmOutputStream.write(shuju[56].getBytes());
                    ToastUtils.showShort("发送指令给关灯");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        select.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String address=city.getText().toString().trim();//必须要写到里面
                Map<String, String> params1 = new HashMap<>();//使用Map函数进行天气测试 键值对
                LogUtils.e("hy",address);
                OkgoUtils.getInstance().get("http://v.juhe.cn/weather/index?cityname="+address+"&dtype=json&format=1&key=75c6c0a4c52e3a2dd7b614cd91237ced", params1, 33, OneFragment.this);

            }
        });

        time.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                c.setTimeInMillis(System.currentTimeMillis());
                int mHour = c.get(Calendar.HOUR_OF_DAY);
                int mMinute = c.get(Calendar.MINUTE);
                new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {
                                c.setTimeInMillis(System.currentTimeMillis());
                                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                                c.set(Calendar.MINUTE, minute);
                                c.set(Calendar.SECOND, 0);
                                c.set(Calendar.MILLISECOND, 0);
                                Intent intent = new Intent(getActivity(), CallAlarm.class);


                                PendingIntent sendIntent = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
                                AlarmManager am = (AlarmManager) getActivity().getSystemService(Context.ALARM_SERVICE);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sendIntent);
                                }

//                                PendingIntent sender = PendingIntent.getBroadcast(getActivity(), 0, intent, 0);
//                                AlarmManager am;
//                                am = (AlarmManager)getSystemService(ALARM_SERVICE);
//                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
//                                    am.setExact(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), sender);
//                                }
                                String tmpS = format(hourOfDay) + "：" + format(minute);
                                ToastUtils.showShort("设置闹钟时间为" + tmpS);
                            }
                        }, mHour, mMinute, true).show();
            }
        });

        return view;
    }
    private String format(int x)
    {
        String s=""+x;
        if(s.length()==1) s="0"+s;
        return s;
    }
    private void openBT() throws IOException {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
        if (mmDevice != null) {
            mmBluetoothSocket = mmDevice.createInsecureRfcommSocketToServiceRecord(uuid);
            mmBluetoothSocket.connect();
            mmOutputStream = mmBluetoothSocket.getOutputStream();
            mmInputStream = mmBluetoothSocket.getInputStream();
            ToastUtils.showShort("我已经连上蓝牙了");
            //蓝牙连接上去了
        } else {

        }
    }

    private void findBT() {
        mmBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();//获得蓝牙适配器
        if (mmBluetoothAdapter == null) {
           return;
        }
        new Thread() {
            public void run() {
                if (mmBluetoothAdapter.isEnabled() == false) {
                    mmBluetoothAdapter.enable();
                } else {
                    mmBluetoothAdapter.enable();//修改
                }
            }
        }.start();
        Set<BluetoothDevice> paireDevices = mmBluetoothAdapter.getBondedDevices();
        ArrayList list = new ArrayList();
        if (paireDevices.size() > 0) {
            for (BluetoothDevice device : paireDevices) {
                mmDevice = device;
                break;
            }
        }
    }

    @Override
    public void onSuccess(int code, String json) {
        switch (code){
            case 33:
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

                //取值
                final String[] items = {"天气是"+weather_value+"天","风向是"+wind_value,"温度是"+temperature_value,"建议"+finalRes_idzhi};
                AlertDialog.Builder listDialog =
                        new AlertDialog.Builder(getActivity());
                listDialog.setTitle(city_value);
                listDialog.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                listDialog.show();
                break;
        }
    }

    @Override
    public void onFailed(int code, String msg) {

    }
}

