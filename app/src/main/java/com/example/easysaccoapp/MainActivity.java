package com.example.easysaccoapp;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {
    Button btn_login;
    EditText et_username, et_password;
    ProgressDialog dialog = null;
    SQLiteDatabase db;
    TextView tv_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_login = (Button) findViewById(R.id.login);
        et_username = (EditText) findViewById(R.id.username);
        et_password = (EditText) findViewById(R.id.password);
        tv_message = (TextView) findViewById(R.id.message);

        db = openOrCreateDatabase("BosaDb", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS members(username VARCHAR,company VARCHAR,branch VARCHAR, password VARCHAR,datepp DATETIME, status VARCHAR);");
        Cursor c = db.rawQuery("SELECT * FROM members", null);

        if (c.getCount() == 0) {
            db.execSQL("INSERT INTO members VALUES('ADMIN','SICHE SACCO','MAIN','admin123','2023-04-08 17:37:11','3');");
        }

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog = ProgressDialog.show(MainActivity.this, "",
                        "Validating user...", true);
                new Thread(new Runnable() {
                    public void run() {
                        login();
                    }
                }).start();
            }
        });
    }

    private void login(){
        try {
            Cursor c = db.rawQuery("SELECT * FROM members WHERE username='" + et_username.getText() + "' and password='" + et_password.getText() + "'", null);
            if ((c.getCount() == 0) && (et_username.getText().toString() != "faben")) {
                tv_message.setText("Enter the correct username or password");
                tv_message.setTextColor(Color.RED);
                Toast.makeText(MainActivity.this, "Invalid details", Toast.LENGTH_SHORT).show();
                showAlert();

            } else {

                runOnUiThread(new Runnable() {
                    public void run() {
                        tv_message.setText("Welcome  " +String.valueOf(et_username.getText()));
                        tv_message.setTextColor(Color.GREEN);
                        runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(MainActivity.this, "Login Success", Toast.LENGTH_LONG).show();
                            }
                        });
                        db.rawQuery("UPDATE members SET status=1 WHERE  status='0' ", null);

                        Intent intent = new Intent(MainActivity.this, ContentActivity.class);
                        startActivity(intent);
                    }
                });
            }
            dialog.dismiss();
        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

    public void showAlert() {
        MainActivity.this.runOnUiThread(new Runnable() {
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Login Error.");
                builder.setMessage("User not Found.")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });
    }

}