package wyj.speak_weake.Board;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;

/**
 * Created by Administrator on 2019/7/4.
 */

public class CallAlarm extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent intent1 = new Intent(context,AlarmAlert.class);
        context.startActivity(intent1);

    }
}
