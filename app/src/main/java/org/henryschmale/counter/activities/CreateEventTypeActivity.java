package org.henryschmale.counter.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.common.util.concurrent.ListenableFuture;

import org.henryschmale.counter.CountedEventDatabase;
import org.henryschmale.counter.CountedEventTypeDao;
import org.henryschmale.counter.R;
import org.henryschmale.counter.models.CountedEventType;

import java.util.concurrent.ExecutionException;

public class CreateEventTypeActivity extends AppCompatActivity {
    public static final String TAG = "CreateEventTypeActivity";

    Button submitButton;
    EditText nameField;
    EditText descriptionField;
    CountedEventTypeDao dao;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event_type);

        Log.i(TAG, "Created the activity");

        dao = CountedEventDatabase.getInstance(getApplicationContext()).countedEventTypeDao();

        nameField = findViewById(R.id.event_type_name);

        nameField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String text = editable.toString();
                checkNameFieldValidity(text);
            }
        });

        descriptionField = findViewById(R.id.event_type_description);
        submitButton = findViewById(R.id.submit_button);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String requestName = nameField.getText().toString();

                Log.i(TAG, "Clicked submit button");

                checkNameFieldValidity(requestName);

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
            }
        });
    }

    public boolean checkNameFieldValidity(String text) {
        try {
            ListenableFuture<Integer> future = dao.countEventTypeName(text);

            if (future.get() > 0) {
                nameField.setError("The requested event type name already exists.");
                return false;
            } else {
                Log.i(TAG, "Future count = " + future.get());
            }
        } catch (ExecutionException|InterruptedException e) {
            Log.w(TAG, e.getLocalizedMessage(), e);
        }
        return true;
    }
}