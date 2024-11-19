package com.example.merge;

import android.content.Intent;
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
    private DdayAdapter adapter;
    private ArrayList<Dday> ddayList;
    private FloatingActionButton fabAddDday;
    private boolean isUpdating = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_alarm, container, false);

        recyclerView = view.findViewById(R.id.rv_dday_list);
        fabAddDday = view.findViewById(R.id.fab_add_dday);
        emptyTextView = view.findViewById(R.id.empty_text_view);

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

        loadDdayData();

        fabAddDday.setOnClickListener(v -> {
            Intent intent = new Intent(requireActivity(), AddDday.class);
            startActivity(intent);
        });

        return view;
    }

    private void loadDdayData() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference ddayRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("ddays");

        ddayRef.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (isUpdating) return;

                ddayList.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Dday dday = data.getValue(Dday.class);
                    if (dday != null) {
                        ddayList.add(dday);
                    }
                }

                if (ddayList.isEmpty()) {
                    recyclerView.setVisibility(View.GONE);
                    emptyTextView.setVisibility(View.VISIBLE);
                } else {
                    recyclerView.setVisibility(View.VISIBLE);
                    emptyTextView.setVisibility(View.GONE);
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(requireContext(), "데이터 불러오기 실패: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
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
        if (position >= 0 && position < ddayList.size()) {
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            DatabaseReference ddayRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("ddays");

            String ddayId = ddayList.get(position).getId();
            if (ddayId != null) {
                isUpdating = true;
                ddayRef.child(ddayId).removeValue().addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        ddayList.remove(position);
                        adapter.notifyItemRemoved(position);

                        if (ddayList.isEmpty()) {
                            recyclerView.setVisibility(View.GONE);
                            emptyTextView.setVisibility(View.VISIBLE);
                        }

                        isUpdating = false;
                        Toast.makeText(requireContext(), "D-day가 삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    } else {
                        isUpdating = false;
                        Toast.makeText(requireContext(), "D-day 삭제 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(e -> {
                    isUpdating = false;
                    Toast.makeText(requireContext(), "삭제 중 오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
            }
        }
    }

    private void showEditDialog(int position) {
        if (position >= 0 && position < ddayList.size()) {
            Dday dday = ddayList.get(position);

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle("D-day 수정");

            final EditText input = new EditText(getActivity());
            input.setText(dday.getTitle());
            builder.setView(input);

            builder.setPositiveButton("저장", (dialog, which) -> {
                String newTitle = input.getText().toString().trim();
                if (!newTitle.isEmpty()) {
                    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    DatabaseReference ddayRef = FirebaseDatabase.getInstance().getReference("users").child(userId).child("ddays");

                    ddayRef.child(dday.getId()).child("title").setValue(newTitle).addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            dday.setTitle(newTitle);
                            adapter.notifyItemChanged(position);
                            Toast.makeText(getActivity(), "D-day가 수정되었습니다.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "D-day 수정 실패: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                } else {
                    Toast.makeText(getActivity(), "제목을 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            });

            builder.setNegativeButton("취소", (dialog, which) -> dialog.dismiss());
            builder.show();
        }
    }
}
