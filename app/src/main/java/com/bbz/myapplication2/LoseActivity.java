package com.bbz.myapplication2;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class LoseActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lose);

        findViewById(R.id.back).setOnClickListener(v -> {
            finish();
        });
    }

}
