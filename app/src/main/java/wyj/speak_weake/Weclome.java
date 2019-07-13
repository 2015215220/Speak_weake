package wyj.speak_weake;

import android.Manifest;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hjq.bar.TitleBar;

public class Weclome extends BaseActivity {
    private TitleBar myBar;
    private Button btn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weclome);

        //android:theme="@style/Theme.AppCompat.Light.NoActionBar" 可以隐藏标题栏
        myBar = findViewById(R.id.myBar);
        myBar.setTitle(R.string.weclome);
        myBar.setTitleColor(0xFFFFFFFF);
        myBar.setTitleSize(TypedValue.COMPLEX_UNIT_SP, 16);
        myBar.setBackgroundColor(0xFF2CA0F3);
        TextView textView = myBar.getLeftView();
        textView.setVisibility(View.GONE);

        btn1=(Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(Weclome.this,LoginActivity.class);
                startActivity(intent);
            }
        });

        performCodeWithPermission("申请所有权限",new PermissionCallback(){
                    @Override
                    public void hasPermission(){
                    }
                    @Override
                    public void noPermission(){
                    }
                }, Manifest.permission.CALL_PHONE,Manifest.permission.RECORD_AUDIO,Manifest.permission.SEND_SMS,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.CHANGE_WIFI_STATE,Manifest.permission.CHANGE_NETWORK_STATE,Manifest.permission.READ_CONTACTS,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_SETTINGS,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.BLUETOOTH_ADMIN,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.BLUETOOTH,Manifest.permission.VIBRATE,Manifest.permission.EXPAND_STATUS_BAR,Manifest.permission.INTERNET,Manifest.permission.MOUNT_UNMOUNT_FILESYSTEMS,Manifest.permission.CHANGE_NETWORK_STATE,Manifest.permission.WAKE_LOCK,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest .permission.READ_PHONE_STATE);

    }
}
