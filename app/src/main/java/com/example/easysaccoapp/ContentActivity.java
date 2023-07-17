package com.example.easysaccoapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.easysaccoapp.Model.LoanShareResp;
import com.example.easysaccoapp.Model.LoanShareTypes;
import com.example.easysaccoapp.Model.SynchData;
import com.example.easysaccoapp.Rest.ApiClient;
import com.example.easysaccoapp.Rest.ApiInterface;
import com.example.easysaccoapp.util.AppConstants;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ContentActivity extends AppCompatActivity {
    Button btn_loans, btn_shares, btn_daily_Report;
    ApiInterface apiService;
    static SQLiteDatabase db;
    ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        btn_loans = (Button) findViewById(R.id.loans);
        btn_shares = (Button) findViewById(R.id.shares);
        btn_daily_Report = (Button) findViewById(R.id.daily_report);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        myToolbar.setTitle("TRANSACTIONS");
        setSupportActionBar(myToolbar);

        db = openOrCreateDatabase("BosaDb", Context.MODE_PRIVATE, null);
        db.execSQL("CREATE TABLE IF NOT EXISTS loanRepay(memberNo VARCHAR,amount VARCHAR, loanShareType VARCHAR, date DATETIME, auditId VARCHAR,status VARCHAR, transdate DATETIME, printed VARCHAR);");
        db.execSQL("CREATE TABLE IF NOT EXISTS sharesContrib(memberNo VARCHAR,amount VARCHAR, loanShareType VARCHAR, date DATETIME, auditId VARCHAR,status VARCHAR, transdate DATETIME, printed VARCHAR);");
        myToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btn_loans.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this, LoanActivity.class);
                startActivity(intent);
            }
        });

        btn_shares.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this, SharesActivity.class);
                startActivity(intent);
            }
        });

        btn_daily_Report.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ContentActivity.this, DailyReportsActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.option_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // action with ID action_refresh was selected

            case R.id.action_synch:
                if (isOnline()) {
                    getLoanShareTypes();
                    sendToDB();
                } else {
                    Toast.makeText(ContentActivity.this, "Check your Internet Connection and try again", Toast.LENGTH_LONG).show();
                }
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void sendToDB() {
        dialog = ProgressDialog.show(ContentActivity.this, "",
                " Detecting new collection, please wait...", true);
        dialog.setCancelable(true);
        Cursor loansCursor = db.rawQuery("SELECT * FROM loanRepay WHERE status='0'", null);
        Cursor sharesCursor = db.rawQuery("SELECT * FROM sharesContrib WHERE status='0'", null);
        if (loansCursor.getCount() == 0 && sharesCursor.getCount() == 0) {
            showMessage("Collection Message", "No new collection found");
            return;
        }

        ArrayList<SynchData> payments = new ArrayList<SynchData>();
        while (loansCursor.moveToNext()) {
            String memberNo = loansCursor.getString(0);
            String amount = loansCursor.getString(1);
            String loanShareType = loansCursor.getString(2);
            String date = loansCursor.getString(3);
            String auditId = loansCursor.getString(4);
            int transType = AppConstants.LOANREPAY;

            payments.add(new SynchData(memberNo, amount, loanShareType, date, auditId, transType));
        }

        while (sharesCursor.moveToNext()) {
            String memberNo = sharesCursor.getString(0);
            String amount = sharesCursor.getString(1);
            String loanShareType = sharesCursor.getString(2);
            String date = sharesCursor.getString(3);
            String auditId = sharesCursor.getString(4);
            int transType = AppConstants.SHARESCONTRIB;

            payments.add(new SynchData(memberNo, amount, loanShareType, date, auditId, transType));
        }

        try {
            apiService = ApiClient.getClient().create(ApiInterface.class);
            Call<com.example.easysaccoapp.Model.Response>call= apiService.makePayments(payments);
            call.enqueue(new Callback<com.example.easysaccoapp.Model.Response>() {
                @Override
                public void onResponse(Call<com.example.easysaccoapp.Model.Response> call, Response<com.example.easysaccoapp.Model.Response> response) {
                    com.example.easysaccoapp.Model.Response responseData = response.body();
                    assert responseData != null;
                    dialog.dismiss();
                    String status = responseData.getMessage();
                    Toast.makeText(getApplicationContext(), status, Toast.LENGTH_LONG).show();
                    if (responseData.isSuccess()){
                        db.execSQL("UPDATE loanRepay set status='1' where status='0';");
                        db.execSQL("UPDATE sharesContrib set status='1' where status='0';");
                    }

                }

                @Override
                public void onFailure(Call<com.example.easysaccoapp.Model.Response> call, Throwable t) {
                    dialog.dismiss();
                    Toast.makeText(getApplicationContext(), "An Error occurred", Toast.LENGTH_LONG).show();
                }
            });
        } catch (Exception e) {
            dialog.dismiss();
            System.out.println("Exception :" + e.getMessage());
        }
    }

    private void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }

    private void getLoanShareTypes() {
        apiService = ApiClient.getClient().create(ApiInterface.class);
        Call<LoanShareResp> call= apiService.getItems();

        call.enqueue(new Callback<LoanShareResp>() {
            @Override
            public void onResponse(Call<LoanShareResp> call, Response<LoanShareResp> response) {
                LoanShareResp responseData = response.body();
                boolean val = responseData.isSuccess();
                String mes = responseData.getMessage();
                LoanShareTypes vals = responseData.getLoanShareTypes();
                if (responseData.isSuccess()){
                    LoanShareTypes types = responseData.getLoanShareTypes();
                    ArrayList<String> loanTypeList = new ArrayList<String>(responseData.getLoanShareTypes().getLoanTypes());
                    ArrayList<String> loanTypes = new ArrayList<String>();
                    for (String loanType: loanTypeList) {
                        loanTypes.add("('"+loanType+"')");
                    }

                    String strLoanTypes = TextUtils.join(", ", loanTypes);
                    db.execSQL("CREATE TABLE IF NOT EXISTS loanTypes(type VARCHAR);");
                    db.execSQL("DELETE FROM loanTypes");
                    db.execSQL("INSERT INTO loanTypes VALUES "+strLoanTypes+";");

                    ArrayList<String> shareTypeList = new ArrayList<String>(responseData.getLoanShareTypes().getShareTypes());
                    ArrayList<String> shareTypes = new ArrayList<String>();
                    for (String loanType: shareTypeList) {
                        shareTypes.add("('"+loanType+"')");
                    }

                    String strShareTypes = TextUtils.join(", ", shareTypes);
                    db.execSQL("CREATE TABLE IF NOT EXISTS shareTypes(type VARCHAR);");
                    db.execSQL("DELETE FROM shareTypes");
                    db.execSQL("INSERT INTO shareTypes VALUES "+strShareTypes+";");
                }
            }

            @Override
            public void onFailure(Call<LoanShareResp> call, Throwable t) {
                showToast("Sorry, An error occurred");
            }
        });
    }

    private void showToast(String message) {
        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
}