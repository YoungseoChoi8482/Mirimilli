package com.example.merge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.merge.databinding.ActivityCheckListMainBinding;

import java.util.HashSet;
import java.util.Set;

public class CheckListMain extends AppCompatActivity {

    private ActivityCheckListMainBinding binding;
    private boolean isEditMode = false; // 수정 모드 활성화 여부
    private static final String TAG = "CheckListMain";
    private static final String PREFS_NAME = "CheckListPrefs";
    private static final String CHECKLIST_KEY = "checklist";
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckListMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Log.d("CurrentActivity", "This is CheckListMain Activity");

        // SharedPreferences 초기화
        sharedPreferences = getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // 내부 저장소에서 체크리스트 데이터 불러오기
        loadChecklist();

        // 수정 버튼 클릭 이벤트
        binding.edit.setOnClickListener(v -> toggleEditMode());

        // + 버튼 클릭 이벤트
        binding.addButton.setOnClickListener(v -> addNewCheckBox());

        // 커뮤니티 버튼 클릭 이벤트
        binding.community.setOnClickListener(v -> {
            Intent intent = new Intent(CheckListMain.this, ChecklistCommunity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 액티비티가 중지될 때 체크리스트 저장
        saveChecklist();
    }

    // 내부 저장소에서 체크리스트 데이터 불러오기
    private void loadChecklist() {
        Log.d(TAG, "Attempting to load checklist from internal storage...");
        binding.checkBoxContainer.removeAllViews(); // 이전 뷰 제거

        Set<String> checklistSet = sharedPreferences.getStringSet(CHECKLIST_KEY, new HashSet<>());
        for (String item : checklistSet) {
            String[] parts = item.split(";;");
            if (parts.length == 2) {
                String itemName = parts[0];
                boolean isChecked = Boolean.parseBoolean(parts[1]);
                addCheckBoxToView(itemName, isChecked);
            }
        }
        Log.d(TAG, "Checklist successfully loaded from internal storage");
    }

    // 수정 모드 활성화/비활성화
    private void toggleEditMode() {
        isEditMode = !isEditMode;
        binding.addButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        setDeleteButtonVisibility(isEditMode ? View.VISIBLE : View.GONE);

        String mode = isEditMode ? "활성화" : "비활성화";
        Toast.makeText(this, "수정 모드 " + mode, Toast.LENGTH_SHORT).show();

        // 수정 모드 비활성화 시 체크리스트 저장
        if (!isEditMode) {
            saveChecklist();
        }
    }

    // 새로운 체크박스 추가
    private void addNewCheckBox() {
        showAddItemDialog();
    }

    // 체크박스를 뷰에 추가하는 메서드
    private void addCheckBoxToView(String itemName, boolean isChecked) {
        // checklist_item.xml에서 뷰를 inflate
        LayoutInflater inflater = LayoutInflater.from(this);
        View newItem = inflater.inflate(R.layout.checkbox_item, binding.checkBoxContainer, false);

        // 체크박스와 삭제 버튼 초기화
        CheckBox newCheckBox = newItem.findViewById(R.id.checkBox);

        // 만약 itemName이 null이거나 비어있다면 기본 텍스트 설정 방지
        if (itemName == null || itemName.trim().isEmpty()) {
            itemName = ""; // 빈 문자열로 설정하여 기본값이 표시되지 않도록 함
        }
        newCheckBox.setText(itemName);
        newCheckBox.setChecked(isChecked);

        Button deleteButton = newItem.findViewById(R.id.delete_real_button); // 올바른 ID 사용

        // 체크박스 및 삭제 버튼 리스너 설정
        initializeCheckBox(newCheckBox, deleteButton);

        // 뷰를 컨테이너에 추가
        binding.checkBoxContainer.addView(newItem);
    }


    // 체크박스와 삭제 버튼 초기화
    private void initializeCheckBox(CheckBox checkBox, Button deleteButton) {
        // 체크박스 클릭 이벤트
        checkBox.setOnClickListener(v -> {
            if (isEditMode) {
                showEditDialog(checkBox);
            } else {
                saveChecklist();
            }
        });

        // 삭제 버튼 클릭 이벤트
        deleteButton.setOnClickListener(v -> {
            LinearLayout parent = (LinearLayout) checkBox.getParent();
            if (parent != null) {
                binding.checkBoxContainer.removeView(parent);
                Toast.makeText(CheckListMain.this, "체크박스가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                saveChecklist();
            }
        });

        // 삭제 버튼 가시성 초기화
        deleteButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
    }

    // 모든 삭제 버튼 가시성 설정
    private void setDeleteButtonVisibility(int visibility) {
        for (int i = 0; i < binding.checkBoxContainer.getChildCount(); i++) {
            View child = binding.checkBoxContainer.getChildAt(i);
            View deleteButton = child.findViewById(R.id.delete_real_button);
            if (deleteButton != null) {
                deleteButton.setVisibility(visibility);
            }
        }
    }

    // 체크박스 텍스트 수정 다이얼로그
    private void showEditDialog(CheckBox checkBox) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("준비물 수정");

        final EditText input = new EditText(this);
        input.setText(checkBox.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("확인", (dialog, which) -> {
            String newText = input.getText().toString().trim();
            if (!newText.isEmpty()) {
                checkBox.setText(newText);
                Toast.makeText(this, "준비물이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                saveChecklist();
            } else {
                Toast.makeText(this, "항목 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // 새로운 아이템 추가 다이얼로그
    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("새 준비물 추가");

        final EditText input = new EditText(this);
        builder.setView(input);

        builder.setPositiveButton("추가", (dialog, which) -> {
            String newItemName = input.getText().toString().trim();
            if (!newItemName.isEmpty()) {
                addCheckBoxToView(newItemName, false);
                saveChecklist();
            } else {
                Toast.makeText(this, "항목 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // 내부 저장소에 체크리스트 저장
    private void saveChecklist() {
        Log.d(TAG, "Attempting to save checklist to internal storage...");
        Set<String> checklistSet = new HashSet<>();
        for (int i = 0; i < binding.checkBoxContainer.getChildCount(); i++) {
            View child = binding.checkBoxContainer.getChildAt(i);
            CheckBox checkBox = child.findViewById(R.id.checkBox);
            if (checkBox != null) {
                String item = checkBox.getText().toString() + ";;" + checkBox.isChecked();
                checklistSet.add(item);
            }
        }

        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putStringSet(CHECKLIST_KEY, checklistSet);
        editor.apply();

        Log.d(TAG, "Checklist successfully saved to internal storage");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // 뷰 바인딩 메모리 해제
    }
}
