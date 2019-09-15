package com.example.alsist;

import android.app.Application;
import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText email, password;
    Button login_button;
    Boolean CheckEditText;
    com.android.volley.RequestQueue requestQueue;
    String EmailHolder, PWHolder;
    ProgressDialog progressDialog;
    String SeverURL = "http://ec2-52-90-187-218.compute-1.amazonaws.com/Login.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        email = (EditText) findViewById(R.id.login_email);
        password = (EditText) findViewById(R.id.login_pw);
        login_button = (Button) findViewById(R.id.login);
        requestQueue = Volley.newRequestQueue(MainActivity.this);
        progressDialog = new ProgressDialog(MainActivity.this);

        login_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckEditTextIsEmptyOrNot();
                if (!CheckEditText) {
                    Toast.makeText(MainActivity.this, "Please fill in the blank", Toast.LENGTH_LONG).show();
                    return;
                }
                progressDialog.setMessage("Please Wait, We are checking your ID");
                progressDialog.show();

                StringRequest stringRequest = new StringRequest(Request.Method.POST, SeverURL,
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String SeverResponse) {
                                progressDialog.dismiss();

                                Toast.makeText(MainActivity.this, SeverResponse, Toast.LENGTH_LONG).show();
                                if (SeverResponse.equals("Success")) {
                                    ((MyApplication) MainActivity.this.getApplication()).setUserEmail(EmailHolder);

                                    // 액티비티 이동하는데 사용자 정보를 저장할 수 있게 해야함
                                    Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError volleyError) {
                                progressDialog.dismiss();
                                Toast.makeText(MainActivity.this, volleyError.toString(), Toast.LENGTH_LONG).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() {
                        Map<String, String> params = new HashMap<String, String>();

                        params.put("email", EmailHolder);
                        params.put("password", PWHolder);

                        return params;
                    }
                };
                com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(MainActivity.this);
                requestQueue.add(stringRequest);
            }
        });
    }


    public void CheckEditTextIsEmptyOrNot() {
        EmailHolder = email.getText().toString();
        PWHolder = password.getText().toString();

        if(TextUtils.isEmpty(EmailHolder) || TextUtils.isEmpty(PWHolder))
            CheckEditText = false;
        else
            CheckEditText = true;
    }

    public void onClick_sign(View v) {
        Intent intent = new Intent(getApplicationContext(), SignIn.class);
        startActivity(intent);
    }

    // 위의 주석 제거시 얘도 제거
    /*
    public void onClick_login(View v) {
        Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
        startActivity(intent);
    }
    */
}
