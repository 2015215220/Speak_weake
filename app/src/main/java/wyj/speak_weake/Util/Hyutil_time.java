package wyj.speak_weake.Util;

import android.util.Log;

import com.blankj.utilcode.util.LogUtils;

/**
 * Created by Administrator on 2019/7/4.
 */

public class Hyutil_time {
    public static String result(String time_value) {//time_value是一个字符串  例如   ，，，15分钟，，，
        String str2 = "";
        if (time_value != null && !"".equals(time_value)) {
            for (int i = 0; i < time_value.length(); i++) {
                                         /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
                String temp = time_value.substring(i, i + 1);
                if (temp.equals("一")) {

                    str2 = str2 + 1;
                }
                if (temp.equals("二")) {
                    str2 = str2 + 2;
                }
                if (temp.equals("三")) {
                    str2 = str2 + 3;
                }
                if (temp.equals("四")) {
                    str2 = str2 + 4;
                }
                if (temp.equals("五")) {
                    str2 = str2 + 5;
                }
                if (temp.equals("六")) {
                    str2 = str2 + 6;
                }
                if (temp.equals("七")) {
                    str2 = str2 + 7;
                }
                if (temp.equals("八")) {
                    str2 = str2 + 4;
                }
                if (temp.equals("九")) {
                    str2 = str2 + 9;
                }
                if (temp.equals("十")) {
                    if (time_value.substring(i + 1, i + 2).equals("分")) {
                        str2 = str2 + 10;
                    }
//                    else if(time_value.substring(i -1, i ).equals("二")) {
//                        str2 = str2 + 2;
//                    }
                    else {
                        str2 = str2 + 1;
                    }
                }
            }
        }
        return str2;
    }
    public static String result_city(String city_value){
        String str2 = "";
        if (city_value != null && !"".equals(city_value)) {
            for (int i = 0; i < city_value.length(); i++) {
                                         /* 获取字段值的长度，如果含中文字符，则每个中文字符长度为2，否则为1 */
                String temp = city_value.substring(i, i + 2);
                if(temp.equals("天气")){
                    str2=str2+city_value.substring(i-2, i );

                    break;
                }
            }
        }

        return str2;
    }
}
