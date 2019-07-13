package wyj.speak_weake.Board;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.media.MediaPlayer;
import android.os.Bundle;

import wyj.speak_weake.R;

/**
 * Created by Administrator on 2019/7/10.
 */

public class AlarmAlert extends Activity

    {
        private MediaPlayer mediaPlayer;
        @Override
        protected void onCreate(Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);
            mediaPlayer = MediaPlayer.create(this, R.raw.nvfuma);
            mediaPlayer.start();
            new AlertDialog.Builder(AlarmAlert.this)
                    .setTitle("温馨提示")
                    .setMessage("吃药时间到了")
                    .setPositiveButton("关闭"
                            , new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    AlarmAlert.this.finish();
                                    mediaPlayer.stop();
                                }
                            }).show();
        }
    }

