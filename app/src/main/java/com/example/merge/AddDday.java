package com.example.merge;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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

        // UI 요소 초기화
        editTextTitle = findViewById(R.id.dday_title_edit_text);
        btnSelectDate = findViewById(R.id.select_date_button);
        btnSave = findViewById(R.id.save_button);
        btnCancel = findViewById(R.id.cancel_button);
        selectedDateTextView = findViewById(R.id.selected_date_text_view);
        selectedDate = Calendar.getInstance();

        // 날짜 선택 버튼 이벤트
        btnSelectDate.setOnClickListener(v -> showDatePickerDialog());

        // 저장 버튼 이벤트
        btnSave.setOnClickListener(v -> saveDday());

        // 취소 버튼 이벤트
        btnCancel.setOnClickListener(v -> finish());
    }

    // 날짜 선택 다이얼로그
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

    // 선택된 날짜 표시 업데이트
    private void updateSelectedDateTextView() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(selectedDate.getTime());
        selectedDateTextView.setText(date);
    }

    // D-day 값 계산
    private long calculateDdayValue() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 0);
        today.set(Calendar.MINUTE, 0);
        today.set(Calendar.SECOND, 0);
        today.set(Calendar.MILLISECOND, 0);

        selectedDate.set(Calendar.HOUR_OF_DAY, 0);
        selectedDate.set(Calendar.MINUTE, 0);
        selectedDate.set(Calendar.SECOND, 0);
        selectedDate.set(Calendar.MILLISECOND, 0);

        long diffMillis = selectedDate.getTimeInMillis() - today.getTimeInMillis();
        return diffMillis / (24 * 60 * 60 * 1000); // 밀리초 -> 일 단위로 변환
    }

    // D-day 저장
    private void saveDday() {
        String title = editTextTitle.getText().toString();
        if (title.isEmpty()) {
            Toast.makeText(this, "D-day 제목을 입력하세요.", Toast.LENGTH_SHORT).show();
            return;
        }


        // D-day 데이터 생성
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        String date = sdf.format(selectedDate.getTime());
        long dDayValue = calculateDdayValue();

        // Firebase에 저장
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        if (userId == null) {
            Toast.makeText(this, "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference ddayRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("ddays");

        // 고유 ID 생성
        String ddayId = ddayRef.push().getKey();
        if (ddayId != null) {
            Dday newDday = new Dday(ddayId, title, date, dDayValue, R.drawable.ic_launcher_background);
            ddayRef.child(ddayId).setValue(newDday).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    Toast.makeText(this, "D-day가 추가되었습니다.", Toast.LENGTH_SHORT).show();
                    finish();
                } else {
                    Toast.makeText(this, "D-day 추가 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
