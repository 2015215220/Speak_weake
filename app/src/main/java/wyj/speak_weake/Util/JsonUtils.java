package wyj.speak_weake.Util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.blankj.utilcode.util.StringUtils;

import java.util.List;

public class JsonUtils {

    public static boolean isJson(String res) {
        if(StringUtils.isEmpty(res)){
            return false;
        }
        try {
            JSONObject.parse(res);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

//    public static boolean getResult(String res) {
//        if (isJson(res) && "0".equals("" + getValue(res, "code"))) {
//            return true;
//        } else {
//            return false;
//        }
//    }

    public static String getValue(String json, String key) {
        if (isJson(json)) {
            return JSONObject.parseObject(json).getString(key);
        }
        return "";
    }

//    public static String getMsg(String res) {
//        return "" + getValue(res, "msg");
//    }


    public static <T> T getModel(String s, String key, Class<T> clazz) {
        return JSONObject.parseObject(getValue(s, key), clazz);
    }

    public static <T> T getModel(String s, Class<T> clazz) {
        return JSONObject.parseObject(s, clazz);
    }

    public static <T> List<T> getList(String s, Class<T> clazz) {
        return JSONArray.parseArray(s, clazz);
    }

    public static <T> List<T> getList(String s, String key, Class<T> clazz) {
        return JSONArray.parseArray(getValue(s, key), clazz);
    }
}
