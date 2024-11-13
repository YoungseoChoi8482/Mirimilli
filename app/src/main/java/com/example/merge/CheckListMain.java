package com.example.merge;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.merge.databinding.ActivityCheckListMainBinding;


public class CheckListMain extends AppCompatActivity {

    private ActivityCheckListMainBinding binding;
    private boolean isEditMode = false; // Check if in edit mode

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckListMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 기본 체크박스 리스너 설정
        setCheckBoxClickListener(binding.checkBox, null);

        // 수정 버튼 클릭 리스너 설정
        binding.edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    // 수정 모드 비활성화: + 버튼과 삭제 버튼들을 숨기기
                    binding.addButton.setVisibility(View.GONE);
                    setDeleteButtonVisibility(View.GONE);
                    isEditMode = false;
                    Toast.makeText(CheckListMain.this, "수정 모드 비활성화", Toast.LENGTH_SHORT).show();
                } else {
                    // 수정 모드 활성화: + 버튼과 삭제 버튼들을 보이기
                    binding.addButton.setVisibility(View.VISIBLE);
                    setDeleteButtonVisibility(View.VISIBLE);
                    isEditMode = true;
                    Toast.makeText(CheckListMain.this, "수정 모드 활성화", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // + 버튼 클릭 리스너 설정 (새로운 체크박스 추가)
        binding.addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 새로운 체크박스와 삭제 버튼이 있는 레이아웃 생성
                LinearLayout newLayout = new LinearLayout(CheckListMain.this);
                newLayout.setOrientation(LinearLayout.HORIZONTAL);

                CheckBox newCheckBox = new CheckBox(CheckListMain.this);
                newCheckBox.setText("설정해주세요");
                newLayout.addView(newCheckBox);

                Button deleteButton = new Button(CheckListMain.this);
                deleteButton.setText("X");
                deleteButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
                newLayout.addView(deleteButton);

                // 체크박스와 삭제 버튼 리스너 설정
                setCheckBoxClickListener(newCheckBox, deleteButton);

                // 새로운 레이아웃을 checkBoxContainer에 추가
                binding.checkBoxContainer.addView(newLayout);
            }
        });
    }

    // 체크박스와 삭제 버튼 클릭 리스너 설정 메서드
    private void setCheckBoxClickListener(CheckBox checkBox, Button deleteButton) {
        // 체크박스 클릭 리스너 설정
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isEditMode) {
                    // 텍스트 수정 다이얼로그
                    AlertDialog.Builder builder = new AlertDialog.Builder(CheckListMain.this);
                    builder.setTitle("준비물 수정");

                    final EditText input = new EditText(CheckListMain.this);
                    input.setText(checkBox.getText().toString());
                    builder.setView(input);

                    builder.setPositiveButton("확인", (dialog, which) -> {
                        String newText = input.getText().toString();
                        checkBox.setText(newText);
                        Toast.makeText(CheckListMain.this, "준비물이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                    });

                    builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
                    builder.show();
                }
            }
        });

        // 삭제 버튼 클릭 리스너 설정
        if (deleteButton != null) {
            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    LinearLayout parent = (LinearLayout) checkBox.getParent();
                    binding.checkBoxContainer.removeView(parent);
                    Toast.makeText(CheckListMain.this, "체크박스가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    // 모든 삭제 버튼의 가시성 설정 메서드
    private void setDeleteButtonVisibility(int visibility) {
        for (int i = 0; i < binding.checkBoxContainer.getChildCount(); i++) {
            View child = binding.checkBoxContainer.getChildAt(i);
            if (child instanceof LinearLayout) {
                LinearLayout layout = (LinearLayout) child;
                for (int j = 0; j < layout.getChildCount(); j++) {
                    View view = layout.getChildAt(j);
                    if (view instanceof Button && ((Button) view).getText().equals("X")) {
                        view.setVisibility(visibility);
                    }
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null; // 뷰 바인딩 메모리 해제
    }
}
