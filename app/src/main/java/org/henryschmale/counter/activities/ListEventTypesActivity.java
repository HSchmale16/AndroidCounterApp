package org.henryschmale.counter.activities;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.CountedEventTypeDao;
import org.henryschmale.counter.CountedEventTypeListAdapter;
import org.henryschmale.counter.LongClickRecyclerHandler;
import org.henryschmale.counter.R;

import static org.henryschmale.counter.CountedEventTypeListAdapter.SortOrder.BY_NAME_A_Z;
import static org.henryschmale.counter.CountedEventTypeListAdapter.SortOrder.BY_NAME_Z_A;
import static org.henryschmale.counter.CountedEventTypeListAdapter.SortOrder.NEWEST_CREATED;
import static org.henryschmale.counter.CountedEventTypeListAdapter.SortOrder.OLDEST_CREATED;
import static org.henryschmale.counter.activities.CreateEventTypeActivity.CREATE_EVENT_REQUEST_CODE;

public class ListEventTypesActivity extends AppCompatActivity implements LongClickRecyclerHandler {
    public static final String TAG = "ListEventTypesActivity";
    public static final int EXPORT_REQUEST_CODE = 2;
    public static final int EVENT_DETAIL_REQUEST_CODE = 3;
    CountedEventTypeListAdapter eventTypeAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_event_types);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setTitle("Event Counting App");

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {
            Intent intent = new Intent(ListEventTypesActivity.this, CreateEventTypeActivity.class);
            startActivityForResult(intent, CREATE_EVENT_REQUEST_CODE);
        });

        CountedEventTypeDao dao = CountedEventDatabase.getInstance(getApplicationContext()).countedEventTypeDao();

        RecyclerView list = findViewById(R.id.event_type_list);

        eventTypeAdapter = new CountedEventTypeListAdapter(dao, this);

        list.setAdapter(eventTypeAdapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CREATE_EVENT_REQUEST_CODE && data != null) {
            eventTypeAdapter.notifyItemChanged(data.getIntExtra("newItemId", 50));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;

        if (item.isCheckable()) {
            item.setChecked(true);
        }

        int menuItemId = item.getItemId();
        if (menuItemId == R.id.menu_sort_by_created_at_newest) {
            this.eventTypeAdapter.setSortOrder(NEWEST_CREATED);
        } else if (menuItemId == R.id.menu_sort_by_created_at_oldest)
            this.eventTypeAdapter.setSortOrder(OLDEST_CREATED);
        else if (menuItemId == R.id.menu_sort_by_name_asc) {
            this.eventTypeAdapter.setSortOrder(BY_NAME_A_Z);
        } else if (menuItemId == R.id.menu_sort_by_name_desc) {
            this.eventTypeAdapter.setSortOrder(BY_NAME_Z_A);
        } else if (menuItemId == R.id.menuAbout) {
            try {
                PackageInfo pInfo = this.getPackageManager().getPackageInfo(this.getPackageName(), 0);
                String aboutText = getResources().getString(R.string.about_toast, pInfo.versionCode);
                Toast.makeText(this, aboutText, Toast.LENGTH_LONG).show();
            } catch (PackageManager.NameNotFoundException e) {
                Log.e(TAG, "Failed to get package info", e);
                Toast.makeText(this, "Failed to get package info. Still made by Henry Schmale though", Toast.LENGTH_SHORT).show();
            }
        } else if (menuItemId == R.id.menuExport) {
            intent = new Intent(this, ExportActivity.class);
            startActivityForResult(intent, EXPORT_REQUEST_CODE);
        } else if (menuItemId == R.id.menuSettings) {
            Toast.makeText(this, R.string.to_be_implemented, Toast.LENGTH_LONG).show();
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
