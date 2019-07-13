package wyj.speak_weake.Util;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.NetworkUtils;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.HttpHeaders;
import com.lzy.okgo.model.Response;

import java.util.HashMap;
import java.util.Map;

public class OkgoUtils {

    public static OkgoUtils instance;

    public static OkgoUtils getInstance() {
        if (instance == null) {
            instance = new OkgoUtils();
        }

        return instance;
    }


    /**
    *@describe post请求
     *
     * @param url 请求链接
     * @param params 请求参数
     * @param requestCode 请求代码用于区分同一个界面不同的请求
     * @param callback 请求回调
     *
    */


    public void post(String url, Map<String, String> params, HttpHeaders headers, int requestCode, OnRequestResult callback){
        if(!NetworkUtils.isConnected()){
            ToastUtils.showShort("网络异常");
            return;
        }
        if(StringUtils.isEmpty(url)){
            LogUtils.e("请求链接不能为空");
            return;
        }
        if(params == null){
            params = new HashMap<>();
        }

        final int reqCode = requestCode;
        final OnRequestResult cb = callback;

        String info = "发送请求，url === " + url + "\n参数 === " + getParamString(params);
        LogUtils.i(info);

        OkGo.<String>post(url).headers(headers)
                .params(params)
                .tag(url)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        if(JsonUtils.isJson(json)){
                            cb.onSuccess(reqCode,json);
                        }else {
                            cb.onFailed(reqCode,"网络请求错误");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        cb.onFailed(reqCode,response.message());
                    }
                });

    }

    public void post(String url, Map<String, String> params, int requestCode, OnRequestResult callback){
        if(!NetworkUtils.isConnected()){
            ToastUtils.showShort("网络异常");
            return;
        }
        if(StringUtils.isEmpty(url)){
            LogUtils.e("请求链接不能为空");
            return;
        }
        if(params == null){
            params = new HashMap<>();
        }

        final int reqCode = requestCode;
        final OnRequestResult cb = callback;

        String info = "发送请求，url === " + url + "\n参数 === " + getParamString(params);
        LogUtils.i(info);

        OkGo.<String>post(url)
                .params(params)
                .tag(url)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        if(JsonUtils.isJson(json)){
                            cb.onSuccess(reqCode,json);
                        }else {
                            cb.onFailed(reqCode,"网络请求错误");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        cb.onFailed(reqCode,response.message());
                    }
                });

    }


    private String getParamString(Map<String, String> params) {
        String p = "";
        for(String key :params.keySet()){
            p += key + " : "+ params.get(key) + ",\n";
        }
        return p;
    }


    /**
     *@describe get请求
     *
     * @param url 请求链接
     * @param params 请求参数
     * @param requestCode 请求代码用于区分同一个界面不同的请求
     * @param callback 请求回调
     *
     */
    public void get(String url, Map<String, String> params, int requestCode, OnRequestResult callback) {
        if(!NetworkUtils.isConnected()){
            ToastUtils.showShort("网络异常");
            return;
        }
        if(StringUtils.isEmpty(url)){
            LogUtils.e("请求链接不能为空");
            return;
        }
        if(params == null){
            params = new HashMap<>();
        }

        final int reqCode = requestCode;
        final OnRequestResult cb = callback;

        OkGo.<String>get(url)
                .params(params)
                .tag(url)
                .execute(new StringCallback() {
                    @Override
                    public void onSuccess(Response<String> response) {
                        String json = response.body();
                        if(JsonUtils.isJson(json)){
                            cb.onSuccess(reqCode,json);
                        }else {
                            cb.onFailed(reqCode,"网络请求错误");
                        }
                    }

                    @Override
                    public void onError(Response<String> response) {
                        super.onError(response);
                        cb.onFailed(reqCode,response.message());
                    }
                });

    }



}
