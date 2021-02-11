package org.henryschmale.counter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.henryschmale.counter.R;

public class ExportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_export);

        getActionBar().setTitle(R.string.export_your_data);
    }
}