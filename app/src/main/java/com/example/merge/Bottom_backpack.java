package com.example.merge;

import android.content.Intent;
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


public class Bottom_backpack extends Fragment {

    private BottomBackpackBinding binding;
    private boolean isEditMode = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomBackpackBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // 기본 체크박스 리스너 설정
        setCheckBoxClickListener(binding.checkBox, null);

        // 커뮤니터 버튼 누르면 커뮤니티 화면으로
        binding.community.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), ChecklistCommunity.class);
            startActivity(intent);
        });

        // 수정 버튼 클릭 리스너 설정
        binding.edit.setOnClickListener(v -> {
            if (isEditMode) {
                binding.addButton.setVisibility(View.GONE);
                setDeleteButtonVisibility(View.GONE);
                isEditMode = false;
                Toast.makeText(getActivity(), "수정 모드 비활성화", Toast.LENGTH_SHORT).show();
            } else {
                binding.addButton.setVisibility(View.VISIBLE);
                setDeleteButtonVisibility(View.VISIBLE);
                isEditMode = true;
                Toast.makeText(getActivity(), "수정 모드 활성화", Toast.LENGTH_SHORT).show();
            }
        });

        // + 버튼 클릭 리스너 설정 (새로운 체크박스 추가)
        binding.addButton.setOnClickListener(v -> {
            LinearLayout newLayout = new LinearLayout(getActivity());
            newLayout.setOrientation(LinearLayout.HORIZONTAL);

            CheckBox newCheckBox = new CheckBox(getActivity());
            newCheckBox.setText("설정해주세요");
            newLayout.addView(newCheckBox);

            Button deleteButton = new Button(getActivity());
            deleteButton.setText("X");
            deleteButton.setVisibility(isEditMode ? View.VISIBLE : View.GONE);
            newLayout.addView(deleteButton);

            // 체크박스와 삭제 버튼 리스너 설정
            setCheckBoxClickListener(newCheckBox, deleteButton);

            // 새로운 레이아웃을 checkBoxContainer에 추가
            binding.checkBoxContainer.addView(newLayout);
        });

        return view;
    }

    // 체크박스와 삭제 버튼 클릭 리스너 설정 메서드
    private void setCheckBoxClickListener(CheckBox checkBox, Button deleteButton) {
        checkBox.setOnClickListener(v -> {
            if (isEditMode) {
                AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
                builder.setTitle("준비물 수정");

                final EditText input = new EditText(requireActivity());
                input.setText(checkBox.getText().toString());
                builder.setView(input);

                builder.setPositiveButton("확인", (dialog, which) -> {
                    String newText = input.getText().toString();
                    checkBox.setText(newText);
                    Toast.makeText(requireActivity(), "준비물이 변경되었습니다.", Toast.LENGTH_SHORT).show();
                });

                builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());
                builder.show();
            }
        });

        if (deleteButton != null) {
            deleteButton.setOnClickListener(v -> {
                LinearLayout parent = (LinearLayout) checkBox.getParent();
                binding.checkBoxContainer.removeView(parent);
                Toast.makeText(requireActivity(), "체크박스가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            });
        }
    }

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
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
