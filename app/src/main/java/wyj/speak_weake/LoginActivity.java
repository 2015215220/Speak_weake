package wyj.speak_weake;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.blankj.utilcode.util.LogUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;
public class LoginActivity extends AppCompatActivity {
    private TitleBar myBar;
    private Button login_btn;
    private TextView wjmm_txt,register_txt;
    private EditText username_edt,password_edt;
    private ContactInfoDao dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        myBar = findViewById(R.id.myBar);
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
        login_btn=(Button)findViewById(R.id.login_btn);//登陆按钮
        wjmm_txt=(TextView)findViewById(R.id.wjmm_txt);//忘记密码
        register_txt=(TextView)findViewById(R.id.register_txt);//注册账号
        username_edt=(EditText)findViewById(R.id.username_edt);//输入用户名
        password_edt=(EditText)findViewById(R.id.password_edt);//输入密码
        dao = new ContactInfoDao(this);

        login_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //数据库操作的
               String username_value= username_edt.getText().toString().trim();
               String password_value=password_edt.getText().toString().trim();
                if (TextUtils.isEmpty(username_value) || TextUtils.isEmpty(password_value)) {
                    Toast.makeText(LoginActivity.this, "不能为空", Toast.LENGTH_SHORT).show();
                    return;
                } else {
                    try {
                        boolean phone = dao.query(username_value, password_value);
                        if(phone){
                            //Toast.makeText(LoginActivity.this, "zhengque：" + phone, Toast.LENGTH_SHORT).show();
                            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                            startActivity(intent);
                        }else{
                            Toast.makeText(LoginActivity.this, "密码错误" + phone, Toast.LENGTH_SHORT).show();
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                        Toast.makeText(LoginActivity.this,"密码错误",Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
        register_txt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,Register.class);
                startActivity(intent);
            }
        });
    }
}
