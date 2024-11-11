package com.example.merge;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;


import com.example.merge.databinding.ActivityLetterToMeBinding;

import java.util.Calendar;

public class LetterToMe extends AppCompatActivity {

    private ActivityLetterToMeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLetterToMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(null);


        binding.etDate.setOnClickListener(v -> showDatePickerDialog());

        binding.btnSaved.setOnClickListener(v ->{
            // 저장 완료 토스트 메시지
            Toast.makeText(LetterToMe.this,"저장완료! 수정하고 싶으시다면 다시 저장해주시면 됩니다!",Toast.LENGTH_LONG).show();
        });

    }

    private void showDatePickerDialog() {
        // 현재 날짜 가져오기
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // DatePickerDialog 생성
        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, year1, monthOfYear, dayOfMonth) -> {
                    // 선택한 날짜를 EditText에 표시
                    String selectedDate = year1 + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                    binding.etDate.setText(selectedDate);
                }, year, month, day);

        datePickerDialog.show();
    }


}