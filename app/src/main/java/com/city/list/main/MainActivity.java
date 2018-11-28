package com.city.list.main;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;

/**
 * 主页面
 * @author xiaojia
 *
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button Bu_selectCountry = (Button) findViewById(R.id.selectCountry);
        Button Bu_forgetpassword = (Button) findViewById(R.id.forgetPwdBtn);
        Button Bu_login = (Button) findViewById(R.id.login);
        Button Bu_register = (Button) findViewById(R.id.register);
        final EditText ed_phone= (EditText) findViewById(R.id.phone2);
        final EditText ed_password = (EditText) findViewById(R.id.password2);
        final TextView country_view = (TextView) findViewById(R.id.country3);


        Bu_selectCountry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent  = new Intent(MainActivity.this,area.class);
                startActivityForResult(intent,1);
            }
        });

        Bu_forgetpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,forget.class);
                startActivity(intent);
            }
        });

        Bu_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phone = ed_phone.getText().toString();
                String password = ed_password.getText().toString();
                String country = country_view.getText().toString();

                //进入数据库判断用户信息
                //checkImformation(phone,password,country);

                String all_data="手机号为:"+phone+"\n密码为:"+password+"\n地区为"+country;
                Toast.makeText(MainActivity.this,all_data , Toast.LENGTH_SHORT).show();

                //根据数据进入登录后页面
                //Intent intent = new Intent(MainActivity.this,loginsuccess.class);
                //startActivity(intent);
            }
        });

        Bu_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,register.class);
                startActivity(intent);
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
                    TextView country_view = (TextView) findViewById(R.id.country3);
                    country_view.setText(country);
                }
                break;
            default:
        }
    }
}
