package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;

public class Bottom_alarm extends Fragment {

    private RecyclerView recyclerView;
    private DdayAdapter adapter;
    private ArrayList<Dday> ddayList;
    private FloatingActionButton fabAddDday;

    // 액티비티 결과 처리용 런처 선언
    private ActivityResultLauncher<Intent> addDdayLauncher;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_alarm, container, false);
        recyclerView = view.findViewById(R.id.rv_dday_list);
        fabAddDday = view.findViewById(R.id.fab_add_dday);

        if (recyclerView == null || fabAddDday == null) {
            Toast.makeText(getContext(), "RecyclerView 또는 FloatingActionButton 초기화 실패", Toast.LENGTH_SHORT).show();
            return view;
        }

        ddayList = new ArrayList<>();
        adapter = new DdayAdapter(ddayList, new DdayAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(int position) {
                showDeleteDialog(position);
            }

            @Override
            public void onItemEdit(int position) {
                showEditDialog(position);  // 수정 다이얼로그 표시
            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // 샘플 데이터 추가
        ddayList.add(new Dday("입대일", "2022.05.22 (일)", 397, R.drawable.ic_launcher_background));

        // 액티비티 결과를 처리할 런처 등록
        addDdayLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == getActivity().RESULT_OK) {
                        Intent data = result.getData();
                        if (data != null) {
                            String title = data.getStringExtra("title");
                            String date = data.getStringExtra("date");
                            long dDayValue = data.getLongExtra("ddayValue", 0);
                            int imageResourceId = data.getIntExtra("imageResourceId", R.drawable.ic_launcher_background);

                            Dday newDday = new Dday(title, date, dDayValue, imageResourceId);
                            ddayList.add(newDday);
                            adapter.notifyItemInserted(ddayList.size() - 1);
                        } else {
                            Toast.makeText(getContext(), "D-day 추가 실패", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );

        // 플로팅 액션 버튼 클릭 시 D-day 추가
        fabAddDday.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), AddDday.class);
            try {
                addDdayLauncher.launch(intent);
            } catch (Exception e) {
                Toast.makeText(getContext(), "오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

        return view;
    }

    // 삭제 다이얼로그 표시
    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(getActivity())
                .setTitle("삭제 확인")
                .setMessage("이 디데이를 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> removeDday(position)) // 삭제 버튼 클릭 시
                .setNegativeButton("취소", null)
                .show();
    }

    // 수정 다이얼로그 표시
    private void showEditDialog(int position) {
        Dday dday = ddayList.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("디데이 이름 수정");

        // 입력 필드 생성
        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(dday.getTitle());  // 기존 디데이 이름을 입력 필드에 미리 채워넣기
        builder.setView(input);

        // 수정 버튼 클릭 시
        builder.setPositiveButton("수정", (dialog, which) -> {
            String newTitle = input.getText().toString();
            ddayList.get(position).setTitle(newTitle);  // 디데이 이름 변경
            adapter.notifyItemChanged(position);  // 리스트 업데이트
            Toast.makeText(getActivity(), "D-day 이름이 수정되었습니다.", Toast.LENGTH_SHORT).show();
        });

        // 취소 버튼
        builder.setNegativeButton("취소", (dialog, which) -> dialog.cancel());

        builder.show();
    }

    // D-day 리스트에서 항목 삭제
    private void removeDday(int position) {
        if (position >= 0 && position < ddayList.size()) {
            ddayList.remove(position);
            adapter.notifyItemRemoved(position);
            adapter.notifyItemRangeChanged(position, ddayList.size());
            Toast.makeText(getActivity(), "D-day가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
        }
    }
}
