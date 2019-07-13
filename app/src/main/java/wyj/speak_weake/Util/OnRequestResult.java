package wyj.speak_weake.Util;

public interface OnRequestResult {

    void onSuccess(int code, String json);

    void onFailed(int code, String msg);
}
