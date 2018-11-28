package com.city.list.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


/**
 * 注册页面
 * @author xiaojia
 */
public class register extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Button goback = (Button) findViewById(R.id.register_goback);

        Button selectCountry = (Button) findViewById(R.id.se_selectCountry);

        Button success = (Button) findViewById(R.id.se_button);
        final TextView country_view = (TextView) findViewById(R.id.se_country3);

        final EditText ed_phone = (EditText) findViewById(R.id.re_phone2);
        final EditText ed_password = (EditText) findViewById(R.id.re_password2);
        final EditText ed_yzm = (EditText) findViewById(R.id.re_yzm2);
        final EditText ed_repassword = (EditText) findViewById(R.id.re_repassword2);

        goback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        selectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(register.this,area.class);
                startActivityForResult(intent,1);
            }
        });

        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = ed_phone.getText().toString();
                String password = ed_password.getText().toString();
                String repassword = ed_repassword.getText().toString();
                String yzm = ed_yzm.getText().toString();
                String country = country_view.getText().toString();

                //判断值是否为空,未写
                //ishaveEmpty(phone,password,repassword,yzm,country);

                //判断password和repassword是否相等
                //password_ok(password,repassword);

                //将数据写入数据库中
                //putDB(phone,password,yzm,country);

                String all_data="手机号为:"+phone+"\n密码为:"+password+"\n地区为"+country;
                Toast.makeText(register.this,all_data , Toast.LENGTH_SHORT).show();

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case 1:
                if(resultCode == RESULT_OK)
                {
                    String country = data.getStringExtra("all_data");
                    TextView country_view = (TextView) findViewById(R.id.se_country3);
                    country_view.setText(country);
                }
                break;
            default:
        }
    }
}
