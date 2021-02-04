package org.henryschmale.counter.activities;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.CountedEventTypeDao;
import org.henryschmale.counter.CountedEventTypeListAdapter;
import org.henryschmale.counter.LongClickRecyclerHandler;
import org.henryschmale.counter.R;

public class ListEventTypesActivity extends AppCompatActivity implements LongClickRecyclerHandler {
    CountedEventTypeListAdapter theStupidAdapter;
    public static final int CREATE_EVENT_REQUEST_CODE = 1;
    public static final int EXPORT_REQUEST_CODE = 2;
    public static final int EVENT_DETAIL_REQUEST_CODE = 3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_event_types);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Event Counting App");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListEventTypesActivity.this, CreateEventTypeActivity.class);
                startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
            }
        });

        CountedEventTypeDao dao = CountedEventDatabase.getInstance(getApplicationContext()).countedEventTypeDao();

        RecyclerView list = findViewById(R.id.event_type_list);

        theStupidAdapter = new CountedEventTypeListAdapter(dao, this);

        list.setAdapter(theStupidAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_EVENT_REQUEST_CODE) {
            theStupidAdapter.notifyItemChanged(data.getIntExtra("newItemId", 50));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            case R.id.menuAbout:
                Toast.makeText(this, R.string.about_toast, Toast.LENGTH_LONG).show();
                break;
            case R.id.menuExport:
                intent = new Intent(this, ExportActivity.class);
                startActivityForResult(intent, EXPORT_REQUEST_CODE);
                break;
            case R.id.menuSettings:
                Toast.makeText(this, R.string.to_be_implemented, Toast.LENGTH_LONG).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void handleLongClick(int id) {
        Intent intent = new Intent(this, EventDetailActivity.class);
        intent.putExtra(EventDetailActivity.EXTRA_EVENT_DETAIL_ID, id);
        startActivityForResult(intent, EVENT_DETAIL_REQUEST_CODE);
    }
}