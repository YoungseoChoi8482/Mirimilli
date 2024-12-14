package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.merge.databinding.BottomHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Bottom_home extends Fragment {

    private BottomHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        binding = BottomHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        List<BranchData> branchDataList = new ArrayList<>();
        PopularJobsAdapter popularAdapter = new PopularJobsAdapter(branchDataList, getContext());
        binding.popularJobsRecyclerView.setAdapter(popularAdapter);
        binding.popularJobsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String[] branches = {"army", "navy", "airForce", "rokmc"};
        for (String branch : branches) {
            db.collection("FavoriteMilitaryBranches").document(branch).collection("popularJobs")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        BranchData branchData = new BranchData();
                        branchData.setBranchName(branch);

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            JobData jobData = document.toObject(JobData.class);
                            jobData.setName(document.getId());

                            switch (document.getId()) {
                                case "top1":
                                    branchData.setTop1(jobData);
                                    break;
                                case "top2":
                                    branchData.setTop2(jobData);
                                    break;
                                case "top3":
                                    branchData.setTop3(jobData);
                                    break;
                            }
                        }

                        branchDataList.add(branchData);
                        popularAdapter.notifyDataSetChanged();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(getContext(), "보직 데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("Bottom_home", "Error fetching popularJobs for branch: " + branch, e);
                    });
        }

        // 상위 3개의 평점 높은 보직 가져오기
        loadTop3Jobs();

        binding.JobRecommendMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecommendJobActivity.class);
            startActivity(intent);
        });

        binding.JobEvaluationMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), JobEvaluationActivity.class);
            startActivity(intent);
        });

        binding.PopularJobMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), FavoriteWork.class);
            startActivity(intent);
        });

        return view;
    }

    // 상위 3개 평점 높은 보직 불러오기
    private void loadTop3Jobs() {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db.collection("jobs")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    List<Job> allJobs = new ArrayList<>();
                    for (QueryDocumentSnapshot doc : querySnapshot) {
                        Job job = doc.toObject(Job.class);
                        job.setId(doc.getId());
                        allJobs.add(job);
                    }

                    // 평점 내림차순 정렬
                    Collections.sort(allJobs, new Comparator<Job>() {
                        @Override
                        public int compare(Job o1, Job o2) {
                            return Float.compare(o2.getRating(), o1.getRating());
                        }
                    });

                    List<Job> top3 = allJobs.size() > 3 ? allJobs.subList(0, 3) : allJobs;

                    // bottom_home.xml 레이아웃에 top1TextView, top2TextView, top3TextView가 있다고 가정
                    if (top3.size() > 0) {
                        binding.top1TextView.setText("1위) " + top3.get(0).getName() + " " + top3.get(0).getRating() + "점");
                    }
                    if (top3.size() > 1) {
                        binding.top2TextView.setText("2위) " + top3.get(1).getName() + " " + top3.get(1).getRating() + "점");
                    }
                    if (top3.size() > 2) {
                        binding.top3TextView.setText("3위) " + top3.get(2).getName() + " " + top3.get(2).getRating() + "점");
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "상위 보직 데이터를 불러오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                    Log.e("Bottom_home", "Error fetching top jobs", e);
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}


//// Bottom_home.java
//package com.example.merge;
//
//import android.content.Intent;
//import android.os.Bundle;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.Toast;
//
//import androidx.annotation.NonNull;
//import androidx.annotation.Nullable;
//import androidx.fragment.app.Fragment;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.example.merge.databinding.BottomHomeBinding;
//import com.google.firebase.firestore.FirebaseFirestore;
//import com.google.firebase.firestore.QueryDocumentSnapshot;
//
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.List;
//
//public class Bottom_home extends Fragment {
//
//    private BottomHomeBinding binding;
//
//    @Nullable
//    @Override
//    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
//                             @Nullable Bundle savedInstanceState) {
//        binding = BottomHomeBinding.inflate(inflater, container, false);
//        View view = binding.getRoot();
//
//        // 인기 보직 RecyclerView 설정
//        List<BranchData> branchDataList = new ArrayList<>();
//        PopularJobsAdapter popularAdapter = new PopularJobsAdapter(branchDataList, getContext());
//        binding.popularJobsRecyclerView.setAdapter(popularAdapter);
//        binding.popularJobsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
//
//
//        FirebaseFirestore db = FirebaseFirestore.getInstance();
//        String[] branches = {"army", "navy", "airForce", "rokmc"};
//        final int totalBranches = branches.length;
//        final int[] processedBranches = {0}; // 완료된 브랜치 수
//
//        // 모든 인기 보직을 가져와 BranchDataList에 추가
//        for (String branch : branches) {
//            db.collection("FavoriteMilitaryBranches").document(branch).collection("popularJobs")
//                    .get()
//                    .addOnSuccessListener(querySnapshot -> {
//                        BranchData branchData = new BranchData();
//                        branchData.setBranchName(branch);
//
//                        for (QueryDocumentSnapshot document : querySnapshot) {
//                            JobData jobData = document.toObject(JobData.class);
//                            jobData.setName(document.getId()); // 보직 ID 설정
//
//                            switch (document.getId()) {
//                                case "top1":
//                                    branchData.setTop1(jobData);
//                                    break;
//                                case "top2":
//                                    branchData.setTop2(jobData);
//                                    break;
//                                case "top3":
//                                    branchData.setTop3(jobData);
//                                    break;
//                            }
//                        }
//
//                        branchDataList.add(branchData);
//                        popularAdapter.notifyDataSetChanged();
//
//                    });
//        }
//
//        // 버튼 클릭 리스너 설정
//        binding.JobRecommendMoreButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), RecommendJobActivity.class);
//            startActivity(intent);
//        });
//
//        binding.JobEvaluationMoreButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), JobEvaluationActivity.class);
//            startActivity(intent);
//        });
//
//        binding.PopularJobMoreButton.setOnClickListener(v -> {
//            Intent intent = new Intent(getActivity(), FavoriteWork.class);
//            startActivity(intent);
//        });
//
//        return view;
//    }
//
//    @Override
//    public void onDestroyView() {
//        super.onDestroyView();
//        binding = null;
//    }
//}