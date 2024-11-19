package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.merge.databinding.BottomHomeBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class Bottom_home extends Fragment {

    private BottomHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        List<BranchData> branchDataList = new ArrayList<>();
        PopularJobsAdapter adapter = new PopularJobsAdapter(branchDataList, getContext());
        binding.popularJobsRecyclerView.setAdapter(adapter);
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

                            switch (document.getId()) {
                                case "top1": branchData.setTop1(jobData); break;
                                case "top2": branchData.setTop2(jobData); break;
                                case "top3": branchData.setTop3(jobData); break;
                            }
                        }

                        branchDataList.add(branchData);
                        adapter.notifyDataSetChanged();
                    });
        }

        // Set up button click listeners for navigating to activities
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

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
