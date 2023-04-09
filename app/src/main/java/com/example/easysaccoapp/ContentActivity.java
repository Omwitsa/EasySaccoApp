package com.example.easysaccoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class ContentActivity extends AppCompatActivity {
    Button btn_loans, btn_shares;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        btn_loans = (Button) findViewById(R.id.loans);
        btn_shares = (Button) findViewById(R.id.shares);

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
    }
}