package org.henryschmale.counter;

import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;

import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;

public class ListEventTypesActivity extends AppCompatActivity {
    CountedEventTypeListAdapter theStupidAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_event_types);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ListEventTypesActivity.this, CreateEventTypeActivity.class);
                startActivityForResult(intent, CreateEventTypeActivity.CREATE_EVENT_REQUEST_CODE);
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
        if (requestCode == CreateEventTypeActivity.CREATE_EVENT_REQUEST_CODE) {
            theStupidAdapter.notifyItemChanged(data.getIntExtra("newItemId", 50));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu, menu);
        return true;
    }

}