package wyj.speak_weake;


import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.hjq.bar.OnTitleBarListener;
import com.hjq.bar.TitleBar;



public class Register extends AppCompatActivity {
    private TitleBar myBar;
    private EditText username_edt,repassword_edt,password_edt;//repassword_edt和password_edt反了
    private Button register_btn;
    private ContactInfoDao dao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
        dao = new ContactInfoDao(this);
        username_edt=(EditText)findViewById(R.id.username_edt);
        repassword_edt=(EditText)findViewById(R.id.repassword_edt);//输入密码
        password_edt=(EditText)findViewById(R.id.password_edt);//确认输入密码
        register_btn=(Button)findViewById(R.id.register_btn);//注册

        String username_edt_value=username_edt.getText().toString().trim();//取值
        String repassword_edt_value=repassword_edt.getText().toString().trim();//取输入密码的值
        String password_edt_value=password_edt.getText().toString().trim();//取再次输入密码的值

        register_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (username_edt.getText().toString().trim().equals("")
                        || repassword_edt.getText().toString().trim().equals("")
                        || password_edt.getText().toString().trim().equals(""))
                         {
                    Toast.makeText(Register.this, "信息不能空",
                            Toast.LENGTH_SHORT).show();
                } else {
                    if (repassword_edt.getText().toString().trim()
                            .equals(password_edt.getText().toString().trim())) {

                        dao.add(username_edt.getText().toString().trim(), repassword_edt.getText().toString().trim());
                        Log.e("hy","添加技术");
                        Toast.makeText(Register.this, "添加成功",
                                Toast.LENGTH_SHORT).show();
                        Intent intent=new Intent(Register.this,LoginActivity.class);
                        startActivity(intent);

                    } else {
                        Toast.makeText(Register.this, "两次密码输入不一样",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }
}
