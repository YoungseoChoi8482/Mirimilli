package com.example.merge;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddDday extends AppCompatActivity {

    private EditText editTextTitle;
    private Button btnSelectDate, btnSave, btnCancel;
    private TextView selectedDateTextView;
    private Calendar selectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_dday);

        editTextTitle = findViewById(R.id.dday_title_edit_text);
        btnSelectDate = findViewById(R.id.select_date_button);
        btnSave = findViewById(R.id.save_button);
        btnCancel = findViewById(R.id.cancel_button);
        selectedDateTextView = findViewById(R.id.selected_date_text_view);
        selectedDate = Calendar.getInstance();

        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());

        btnSave.setOnClickListener(v -> {
            String title = editTextTitle.getText().toString();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
            String date = sdf.format(selectedDate.getTime());
            long dDayValue = calculateDdayValue();

            Intent resultIntent = new Intent();
            resultIntent.putExtra("title", title);
            resultIntent.putExtra("date", date);
            resultIntent.putExtra("ddayValue", dDayValue);
            setResult(RESULT_OK, resultIntent);
            Toast.makeText(getApplicationContext(), "디데이가 추가되었습니다.", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnCancel.setOnClickListener(v -> finish());
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    selectedDate.set(year, month, dayOfMonth);
                    updateSelectedDateTextView();
                },
                selectedDate.get(Calendar.YEAR),
                selectedDate.get(Calendar.MONTH),
                selectedDate.get(Calendar.DAY_OF_MONTH)
        );
        datePickerDialog.show();
    }

    private void updateSelectedDateTextView() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault());
        String date = sdf.format(selectedDate.getTime());
        selectedDateTextView.setText(date);
    }

    private long calculateDdayValue() {
        Calendar today = Calendar.getInstance();
        long diffMillis = selectedDate.getTimeInMillis() - today.getTimeInMillis();
        return diffMillis / (24 * 60 * 60 * 1000);
    }
}