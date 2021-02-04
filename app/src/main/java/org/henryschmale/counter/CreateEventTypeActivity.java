package org.henryschmale.counter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.util.concurrent.ListenableFuture;

import java.util.concurrent.ExecutionException;

public class CreateEventTypeActivity extends AppCompatActivity {
    public static final int CREATE_EVENT_REQUEST_CODE = 1;
    public static final String TAG = "CreateEventTypeActivity";

    Button submitButton;
    EditText nameField;
    EditText descriptionField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_type);

        Log.i(TAG, "Created the activity");

        CountedEventTypeDao dao = CountedEventDatabase.getInstance(getApplicationContext()).countedEventTypeDao();

        nameField = findViewById(R.id.event_type_name);
        descriptionField = findViewById(R.id.event_type_description);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String requestName = nameField.getText().toString();

                Log.i(TAG, "Clicked submit button");

                try {
                    ListenableFuture<Integer> future = dao.countEventTypeName(requestName);

                    if (future.get() > 0) {
                        nameField.setError("The requested event type name already exists.");
                        return;
                    } else {
                        Log.i(TAG, "Future count = " + future.get());
                    }

                    String description = descriptionField.getText().toString();

                    CountedEventType cet = new CountedEventType();
                    cet.eventTypeName = requestName;
                    cet.eventTypeDescription = description;

                    Log.d(TAG, cet.eventTypeName);

                    long id = dao.addCountedEventType(cet);
                    Intent intent = new Intent();
                    intent.putExtra("newItemId", id);


                    setResult(RESULT_OK, intent);
                    finish();
                } catch (ExecutionException|InterruptedException fuckYou) {
                    Log.w(TAG, fuckYou.getLocalizedMessage());
                }
            }
        });
    }
}