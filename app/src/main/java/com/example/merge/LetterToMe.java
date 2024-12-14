package com.example.merge;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.example.merge.databinding.ActivityLetterToMeBinding;

import java.util.Calendar;

public class LetterToMe extends AppCompatActivity implements OnFragmentInteractionListener {

    private ActivityLetterToMeBinding binding;

    // SharedPreferences 파일 이름과 키 정의
    private static final String PREFS_NAME = "LetterToMePrefs";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_DATE = "date";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLetterToMeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setTitle(null);



        binding.etDate.setOnClickListener(v -> showDatePickerDialog());

        binding.btnSaved.setOnClickListener(v ->{
            saveData();
            // 저장 완료 토스트 메시지
            Toast.makeText(LetterToMe.this,"저장완료!",Toast.LENGTH_LONG).show();

            // 저장 완료 프래그먼트 표시
            showSaveCompletedFragment();
        });

        loadData();

        // 백 스택 변경 리스너 추가
        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
                // 프래그먼트가 없을 때 FrameLayout 숨기기
                binding.letterToMeFrame.setVisibility(View.GONE);
            }
        });

        if (isDataSaved()) {
            showSaveCompletedFragment();
        } else {
            // 데이터가 없으면 폼을 보여줌
            binding.letterToMeFrame.setVisibility(View.GONE);
        }

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





    /**
     * SharedPreferences에 데이터 저장
     */
    private void saveData() {
        // SharedPreferences 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        // EditText에서 텍스트 가져오기
        String title = binding.etTitle.getText().toString();
        String content = binding.etContent.getText().toString();
        String date = binding.etDate.getText().toString();
        // 데이터 저장
        editor.putString(KEY_TITLE, title);
        editor.putString(KEY_CONTENT, content);
        editor.putString(KEY_DATE,date);

        // 변경사항 커밋
        editor.apply();
    }

    // sharedPreference 에서 데이터 불러오기.
    private void loadData() {
        // SharedPreferences 가져오기
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // 저장된 데이터 읽기 (없을 경우 빈 문자열 반환)
        String title = sharedPreferences.getString(KEY_TITLE, "");
        String content = sharedPreferences.getString(KEY_CONTENT, "");
        String date = sharedPreferences.getString(KEY_DATE, "");

        // EditText에 데이터 설정
        binding.etTitle.setText(title);
        binding.etContent.setText(content);
        binding.etDate.setText(date);
    }

    /**
     * 저장 완료 프래그먼트 표시
     */
    private void showSaveCompletedFragment() {
        // 프래그먼트 인스턴스 생성
        LetterToMeFragment fragment = LetterToMeFragment.newInstance();

        // 프래그먼트 트랜잭션 시작
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        // 애니메이션 설정 (선택 사항)
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);

        // 프래그먼트를 컨테이너에 추가
        transaction.replace(R.id.letter_to_me_frame, fragment);

//        // 백 스택에 추가 (뒤로가기 시 프래그먼트 제거)
//        transaction.addToBackStack(null);

        // 트랜잭션 커밋
        transaction.commit();

        // 프래그먼트 컨테이너를 보이도록 설정
        binding.letterToMeFrame.setVisibility(View.VISIBLE);
    }

    private boolean isDataSaved() {
        SharedPreferences sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String title = sharedPreferences.getString(KEY_TITLE, "");
        String content = sharedPreferences.getString(KEY_CONTENT, "");
        String date = sharedPreferences.getString(KEY_DATE, "");
        return !title.isEmpty() && !content.isEmpty() && !date.isEmpty();
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() > 0) {
            getSupportFragmentManager().popBackStack();
            binding.letterToMeFrame.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }


    }
    public void onEditButtonClicked() {
        // LetterToMeFragment를 숨기고 폼을 다시 표시
        binding.letterToMeFrame.setVisibility(View.GONE);
    }

}