package com.example.easysaccoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.easysaccoapp.util.AppConstants;
import com.example.easysaccoapp.util.DateUtil;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class SharesActivity extends AppCompatActivity {
    SQLiteDatabase db;
    String shares;
    Button btn_save;
    EditText et_memberNo, et_amount;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shares);

        btn_save = (Button) findViewById(R.id.save);
        et_memberNo = (EditText) findViewById(R.id.memberNo);
        et_amount = (EditText) findViewById(R.id.amount);

        sharedPreferences = getSharedPreferences("preferences", MODE_PRIVATE);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("SHARES CONTRIBUTION");
        setSupportActionBar(myToolbar);

        db = openOrCreateDatabase("BosaDb", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS sharesContrib(memberNo VARCHAR,amount VARCHAR, loanShareType VARCHAR, date DATETIME, auditId VARCHAR,status VARCHAR, transdate DATETIME, printed VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS shareTypes(type VARCHAR);");
        Cursor c = db.rawQuery("SELECT type FROM shareTypes", null);
        ArrayList sharesTypeList = new ArrayList<>();
        try{
            while (c.moveToNext()) {
                String sharesType = c.getString(0);
                sharesTypeList.add(sharesType);
            }
        }
        catch (Exception e){
            System.out.println("Exception :" + e.getMessage());
        }

        ArrayAdapter<String> sharesAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, sharesTypeList);
        sharesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        final Spinner sharesTypeSpinner = (Spinner) findViewById(R.id.shareType);
        sharesTypeSpinner.setAdapter(sharesAdapter);

        sharesTypeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                shares = sharesTypeSpinner.getSelectedItem().toString();
            }
            @Override
            public void onNothingSelected(AdapterView <?> parent) {
            }
        });

        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (et_memberNo.getText().toString().trim().length() == 0 ||
                        et_amount.getText().toString().trim().length() == 0) {
                    showMessage("Error", "Please enter all values");
                    return;
                } else{
                    dialog();
                }
            }
        });
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void dialog() {
        StringBuffer buffer = new StringBuffer();
        String member_no = et_memberNo.getText().toString();
        String amount = et_amount.getText().toString();
        AlertDialog.Builder build = new AlertDialog.Builder(SharesActivity.this);
        build.setTitle("Confirmation :MemberNo=" + member_no + " and repay amount =" + amount + "  using:Main");

        build.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                String user = null;
                long milis1 = System.currentTimeMillis();
                String date_print = DateUtil.timeMilisToString(milis1, "yyyy-MM-dd");

                Calendar c = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String date1 = sdf.format(c.getTime());
                String loggenInUser = sharedPreferences.getString("loggedInUser", "");

                db.execSQL("INSERT INTO sharesContrib  VALUES('" + member_no + "', '" + amount + "', '" + shares +"', '" + date1 +"','" + loggenInUser + "','0', '"+date_print +"', '0');");
                showMessage("Success", "Record added");
                et_memberNo.setText("");
                et_amount.setText("");

                Intent i = new Intent(getApplicationContext(), PrintActivity.class);
                Bundle bundle = new Bundle();
                bundle.putInt("operation", AppConstants.SHARESCONTRIB);
                bundle.putString("memberNo", member_no);
                bundle.putString("transType", shares);
                i.putExtras(bundle);
                startActivity(i);
            }
        });

        build.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                dialog.dismiss();
            }
        });
        build.create().show();
    }
}