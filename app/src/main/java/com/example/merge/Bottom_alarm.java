package com.example.merge;

import static android.content.Context.MODE_PRIVATE;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class Bottom_alarm extends Fragment {

    private RecyclerView recyclerView;
    private TextView emptyTextView;
    private FloatingActionButton fabAddDday;
    private DdayAdapter adapter;
    private ArrayList<Dday> ddayList;
    private DatabaseReference ddayRef;
    private boolean isUpdating = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_alarm, container, false);

        // UI 초기화
        recyclerView = view.findViewById(R.id.rv_dday_list);
        emptyTextView = view.findViewById(R.id.empty_text_view);
        fabAddDday = view.findViewById(R.id.fab_add_dday);

        // RecyclerView 설정
        ddayList = new ArrayList<>();
        adapter = new DdayAdapter(ddayList, new DdayAdapter.OnItemClickListener() {
            @Override
            public void onItemDelete(int position) {
                showDeleteDialog(position);
            }

            @Override
            public void onItemEdit(int position) {
                showEditDialog(position);
            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));

        // Firebase 초기화
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid() : null;
        if (userId != null) {
            ddayRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("ddays");
        } else {
            Toast.makeText(requireContext(), "로그인이 필요합니다.", Toast.LENGTH_SHORT).show();
            return view;
        }

        // 데이터 로드
        loadDdayData();

        // FloatingActionButton 클릭 리스너
        fabAddDday.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddDday.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadDdayData() {
        if (ddayRef == null) return;

        ddayRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isUpdating) return;

                ddayList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Dday dday = data.getValue(Dday.class);
                    if (dday != null) {
                        ddayList.add(dday);

                        // D+0 알림 처리
                        handleDdayNotification(dday);
                    }
                }

                // RecyclerView 가시성 업데이트
                updateRecyclerViewVisibility();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "데이터 로드 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateRecyclerViewVisibility() {
        if (ddayList.isEmpty()) {
            recyclerView.setVisibility(View.GONE);
            emptyTextView.setVisibility(View.VISIBLE);
        } else {
            recyclerView.setVisibility(View.VISIBLE);
            emptyTextView.setVisibility(View.GONE);
        }
    }

    private void handleDdayNotification(Dday dday) {
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences("DdaySettings", MODE_PRIVATE);
        boolean isNotificationOn = sharedPreferences.getBoolean("dday_notification", true);

        if (dday.getDdayValue() == 0 && isNotificationOn) {
            showDdayNotification(dday);
        }
    }

    private void showDdayNotification(Dday dday) {
        new AlertDialog.Builder(requireActivity())
                .setTitle("D-day 알림")
                .setMessage("'" + dday.getTitle() + "' 날짜가 되었습니다!")
                .setPositiveButton("확인", (dialog, which) -> dialog.dismiss())
                .setNegativeButton("삭제", (dialog, which) -> removeDdayById(dday.getId()))
                .show();
    }

    private void showDeleteDialog(int position) {
        new AlertDialog.Builder(requireActivity())
                .setTitle("삭제 확인")
                .setMessage("이 D-day를 삭제하시겠습니까?")
                .setPositiveButton("삭제", (dialog, which) -> removeDday(position))
                .setNegativeButton("취소", null)
                .show();
    }

    private void removeDday(int position) {
        if (position < 0 || position >= ddayList.size() || ddayRef == null) return;

        String ddayId = ddayList.get(position).getId();
        if (ddayId == null) return;

        isUpdating = true;
        ddayRef.child(ddayId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                ddayList.remove(position);
                adapter.notifyItemRemoved(position);
                updateRecyclerViewVisibility();
                Toast.makeText(requireContext(), "D-day가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "D-day 삭제 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
            isUpdating = false;
        });
    }

    private void removeDdayById(String ddayId) {
        if (ddayId == null || ddayRef == null) return;

        isUpdating = true;
        ddayRef.child(ddayId).removeValue().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                loadDdayData();
                Toast.makeText(requireContext(), "D-day가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(requireContext(), "D-day 삭제 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
            }
            isUpdating = false;
        });
    }

    private void showEditDialog(int position) {
        if (position < 0 || position >= ddayList.size()) return;

        Dday dday = ddayList.get(position);
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        builder.setTitle("D-day 수정");

        final EditText input = new EditText(requireActivity());
        input.setText(dday.getTitle());
        builder.setView(input);

        builder.setPositiveButton("저장", (dialog, which) -> {
            String newTitle = input.getText().toString().trim();
            if (!newTitle.isEmpty() && ddayRef != null) {
                ddayRef.child(dday.getId()).child("title").setValue(newTitle).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        dday.setTitle(newTitle);
                        adapter.notifyItemChanged(position);
                        Toast.makeText(requireContext(), "D-day가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(requireContext(), "D-day 수정 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
            } else {
                Toast.makeText(requireContext(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
            }
        });

        builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
        builder.show();
    }
}
