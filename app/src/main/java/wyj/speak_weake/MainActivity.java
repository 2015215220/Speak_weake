package wyj.speak_weake;

import android.Manifest;
import android.content.ComponentName;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;

import android.util.AndroidRuntimeException;
import android.util.Log;
import android.util.TypedValue;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.VoiceRecognitionService;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.support.v7.app.AppCompatActivity;
import android.util.AndroidRuntimeException;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.speech.EventListener;
import com.baidu.speech.EventManager;
import com.baidu.speech.EventManagerFactory;
import com.baidu.speech.VoiceRecognitionService;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;
import com.hjm.bottomtabbar.BottomTabBar;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import wyj.speak_weake.Service.SpeechService;

public class MainActivity extends BaseActivity {
    private TitleBar myBar;
    private BottomTabBar mb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        myBar = findViewById(R.id.myBar);
        mb=(BottomTabBar)findViewById(R.id.bottom_tab_bar);
        myBar.setTitle(R.string.weclome);
        myBar.setTitleColor(0xFFFFFFFF);
        myBar.setTitleSize(TypedValue.COMPLEX_UNIT_SP, 16);
        myBar.setBackgroundColor(0xFF2CA0F3);
        myBar.setOnTitleBarListener(new OnTitleBarListener() {
            @Override
            public void onLeftClick(View v) {
                finish();
            }
            @Override
            public void onTitleClick(View v) {
            }
            @Override
            public void onRightClick(View v) {
            }
        });
        Intent intent = new Intent(this,SpeechService.class);
        startService(intent);
        mb.init(getSupportFragmentManager())
                .setImgSize(80,80)
                .setFontSize(8)
                .setTabPadding(4,6,10)
                .setChangeColor(Color.RED, Color.DKGRAY)
                .addTabItem("生活&&安全",R.drawable.shenghuo, OneFragment.class)
                .addTabItem("健康&&紧急",R.drawable.health, TwoFragment.class)
                .addTabItem("娱乐&&慰藉",R.drawable.yule, ThreeFragment.class)
               //.addTabItem("我的",R.drawable.ic_lock_outline, FourFragment.class)
                .isShowDivider(false)
                .setOnTabChangeListener(new BottomTabBar.OnTabChangeListener() {
                    @Override
                    public void onTabChange(int position, String name) {
                    }
                });
         }
}

