//Bottom_backpack
package com.example.merge;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;

import com.example.merge.databinding.BottomBackpackBinding;

import java.util.HashSet;
import java.util.Set;

public class Bottom_backpack extends Fragment {

    private BottomBackpackBinding binding;
    private boolean isEditMode = false;
    private static final String PREFS_NAME = "CheckListPrefs";
    private static final String CHECKLIST_KEY = "checklist";
    private SharedPreferences sharedPreferences;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomBackpackBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // SharedPreferences 초기화
        sharedPreferences = requireActivity().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        // 내부 저장소에서 체크리스트 데이터 불러오기
        loadChecklist();

        // 수정 버튼 클릭 이벤트
        binding.edit.setOnClickListener(v -> toggleEditMode());

        // + 버튼 클릭 이벤트
        binding.addButton.setOnClickListener(v -> addNewCheckBox());

        // 커뮤니티 버튼 클릭 이벤트
        binding.community.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChecklistCommunity.class);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onStop() {
        super.onStop();
        // 프래그먼트가 중지될 때 체크리스트 저장
        saveChecklist();
    }

    // 내부 저장소에서 체크리스트 데이터 불러오기
    private void loadChecklist() {
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
    }

    // 수정 모드 활성화/비활성화
    private void toggleEditMode() {
        isEditMode = !isEditMode;
        binding.addButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
        setDeleteButtonVisibility(isEditMode ? View.VISIBLE : View.GONE);

        String mode = isEditMode ? "활성화" : "비활성화";
        Toast.makeText(getActivity(), "수정 모드 " + mode, Toast.LENGTH_SHORT).show();

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
        LayoutInflater inflater = LayoutInflater.from(getActivity());
        View newItem = inflater.inflate(R.layout.checkbox_item, binding.checkBoxContainer, false);

        // 체크박스와 삭제 버튼 초기화
        CheckBox newCheckBox = newItem.findViewById(R.id.checkBox);

        if (itemName == null || itemName.trim().isEmpty()) {
            itemName = ""; // 빈 문자열로 설정하여 기본값이 표시되지 않도록 함
        }
        newCheckBox.setText(itemName);
        newCheckBox.setChecked(isChecked);

        // 체크박스 상태 저장을 위해 리스너 설정
        newCheckBox.setOnCheckedChangeListener((buttonView, isChecked1) -> saveChecklist());

        Button deleteButton = newItem.findViewById(R.id.delete_real_button); // 올바른 ID 사용

        initializeCheckBox(newCheckBox, deleteButton);
        binding.checkBoxContainer.addView(newItem);
    }

    // 체크박스와 삭제 버튼 초기화
    private void initializeCheckBox(CheckBox checkBox, Button deleteButton) {
        // 체크박스 클릭 이벤트 및 체크 상태 변화 리스너 설정
        checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> saveChecklist());

        checkBox.setOnClickListener(v -> {
            if (isEditMode) {
                showEditDialog(checkBox);
            }
        });

        // 삭제 버튼 클릭 이벤트
        deleteButton.setOnClickListener(v -> {
            LinearLayout parent = (LinearLayout) checkBox.getParent();
            if (parent != null) {
                binding.checkBoxContainer.removeView(parent);
                Toast.makeText(getActivity(), "체크박스가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
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
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("준비물 수정");

        final EditText input = new EditText(requireActivity());
        input.setText(checkBox.getText().toString());
        builder.setView(input);

        builder.setPositiveButton("확인", (dialog, which) -> {
            String newText = input.getText().toString().trim();
            if (!newText.isEmpty()) {
                checkBox.setText(newText);
                Toast.makeText(getActivity(), "준비물이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                saveChecklist();
            } else {
                Toast.makeText(getActivity(), "항목 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // 새로운 아이템 추가 다이얼로그
    private void showAddItemDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("새 준비물 추가");

        final EditText input = new EditText(requireActivity());
        builder.setView(input);

        builder.setPositiveButton("추가", (dialog, which) -> {
            String newItemName = input.getText().toString().trim();
            if (!newItemName.isEmpty()) {
                addCheckBoxToView(newItemName, false);
                saveChecklist();
            } else {
                Toast.makeText(getActivity(), "항목 이름을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
        builder.show();
    }

    // 내부 저장소에 체크리스트 저장
    private void saveChecklist() {
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
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}