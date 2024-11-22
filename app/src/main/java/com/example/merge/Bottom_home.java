// Bottom_home.java
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

        // 인기 보직 RecyclerView 설정
        List<BranchData> branchDataList = new ArrayList<>();
        PopularJobsAdapter popularAdapter = new PopularJobsAdapter(branchDataList, getContext());
        binding.popularJobsRecyclerView.setAdapter(popularAdapter);
        binding.popularJobsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

//        // 상위 3개 보직 RecyclerView 설정
//        List<Job> topRatedJobsList = new ArrayList<>();
//        TopRatedJobsAdapter topRatedAdapter = new TopRatedJobsAdapter(topRatedJobsList, getContext());
//        binding.topRatedJobsRecyclerView.setAdapter(topRatedAdapter);
//        binding.topRatedJobsRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FirebaseFirestore db = FirebaseFirestore.getInstance();
        String[] branches = {"army", "navy", "airForce", "rokmc"};
        final int totalBranches = branches.length;
        final int[] processedBranches = {0}; // 완료된 브랜치 수

        // 모든 인기 보직을 가져와 BranchDataList에 추가
        for (String branch : branches) {
            db.collection("FavoriteMilitaryBranches").document(branch).collection("popularJobs")
                    .get()
                    .addOnSuccessListener(querySnapshot -> {
                        BranchData branchData = new BranchData();
                        branchData.setBranchName(branch);

                        for (QueryDocumentSnapshot document : querySnapshot) {
                            JobData jobData = document.toObject(JobData.class);
                            jobData.setName(document.getId()); // 보직 ID 설정

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

//                        // 상위 3개 보직 리스트에 추가
//                        synchronized (this) {
//                            if (branchData.getTop1() != null) topRatedJobsList.add(branchData.getTop1());
//                            if (branchData.getTop2() != null) topRatedJobsList.add(branchData.getTop2());
//                            if (branchData.getTop3() != null) topRatedJobsList.add(branchData.getTop3());
//                        }
//
//                        // 모든 브랜치 처리 완료 시 정렬 및 상위 3개 추출
//                        synchronized (processedBranches) {
//                            processedBranches[0]++;
//                            if (processedBranches[0] == totalBranches) {
//                                processTopRatedJobs(topRatedJobsList, topRatedAdapter);
//                            }
//                        }
//
//                    })
//                    .addOnFailureListener(e -> {
//                        Toast.makeText(getContext(), "보직 데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
//                        Log.e("Bottom_home", "Error fetching popularJobs for branch: " + branch, e);
//
//                        synchronized (processedBranches) {
//                            processedBranches[0]++;
//                            if (processedBranches[0] == totalBranches) {
//                                processTopRatedJobs(topRatedJobsList, topRatedAdapter);
//                            }
//                        }
                    });
        }

        // 버튼 클릭 리스너 설정
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

    /**
     * 상위 3개의 평점 높은 보직을 추출하여 어댑터에 설정하는 메서드
     */
//    private void processTopRatedJobs(List<JobData> allJobs, TopRatedJobsAdapter adapter) {
//        // 중복 제거
//        List<JobData> uniqueJobs = new ArrayList<>();
//        for (JobData job : allJobs) {
//            boolean exists = false;
//            for (JobData uj : uniqueJobs) {
//                if (uj.getId().equals(job.getId())) {
//                    exists = true;
//                    break;
//                }
//            }
//            if (!exists) {
//                uniqueJobs.add(job);
//            }
//        }
//
//        // 평점 내림차순으로 정렬
//        Collections.sort(uniqueJobs, new Comparator<JobData>() {
//            @Override
//            public int compare(JobData o1, JobData o2) {
//                return Float.compare(o2.getRating(), o1.getRating());
//            }
//        });
//
//        // 상위 3개 보직만 추출
//        List<JobData> top3Jobs = uniqueJobs.size() > 3 ? uniqueJobs.subList(0, 3) : uniqueJobs;
//        adapter.updateJobs(top3Jobs);
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}