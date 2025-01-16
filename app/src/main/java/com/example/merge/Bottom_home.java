package com.example.merge;

import static android.app.Activity.RESULT_OK;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
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
    private String department;
    private String certificatesText;
    private String recommended1;
    private String recommended2;
    private int change = 0;

    private final ActivityResultLauncher<Intent> recommendJobLauncher =
            registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Intent data = result.getData();
                    department = data.getStringExtra("department");
                    ArrayList<String> certificates = data.getStringArrayListExtra("certificates");
                    recommended1 = data.getStringExtra("recommended1");
                    recommended2 = data.getStringExtra("recommended2");

                    // Convert certificates list to a string
                    certificatesText = certificates != null ? String.join(" , ", certificates) : "";

                    // Update JobRecommendSection
                    if (binding != null) {
                        updateJobRecommendSection();
                    }
                }
            });

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // RecyclerView setup
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
                        adapter.notifyDataSetChanged();
                    });
        }

        // 상위 3개의 평점 높은 보직 가져오기
        loadTop3Jobs();

        binding.InputInformationButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecommendJobActivity.class);
            recommendJobLauncher.launch(intent);
        });

        binding.JobRecommendMoreButton.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), RecommendJobActivity2.class);
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

        // Update JobRecommendSection if necessary
        if (change == 1) {
            updateJobRecommendSection();
        }

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (department != null && certificatesText != null && recommended1 != null && recommended2 != null) {
            change = 1;
            updateJobRecommendSection();
        }
    }

    private void updateJobRecommendSection() {
        if (binding == null) return;

        binding.JobRecommendSection.removeAllViews();

        TextView headerTextView = new TextView(getContext());
        headerTextView.setText(department + "에 잘 어울리는 보직들이에요!");
        headerTextView.setTextSize(18);
        headerTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        headerTextView.setPadding(0, 16, 0, 16);
        binding.JobRecommendSection.addView(headerTextView);

        SharedPreferences sharedPref1 = requireContext().getSharedPreferences("certificate1", Context.MODE_PRIVATE);
        SharedPreferences sharedPref2 = requireContext().getSharedPreferences("certificate2", Context.MODE_PRIVATE);

        String data1 = sharedPref1.getString("key1", "none");
        String data2 = sharedPref2.getString("key2", "");

        if(data2 != ""){
            data1 = data1 + "";
        }

        TextView certificatesTextView = new TextView(getContext());
        certificatesTextView.setText("(보유 자격증: " + data1 + data2 + ")");
        certificatesTextView.setTextSize(14);
        certificatesTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        certificatesTextView.setPadding(16, 8, 16, 16);
        binding.JobRecommendSection.addView(certificatesTextView);

        addPositionView("첫번째 : ", recommended1, android.R.color.white);
        addPositionView("두번째 : ", recommended2, android.R.color.white);
    }

    private void addPositionView(String rank, String position, int backgroundColor) {
        TextView positionView = new TextView(getContext());
        positionView.setText(rank + ": " + position);
        positionView.setTextSize(16);
        positionView.setPadding(16, 16, 16, 16);
        positionView.setBackgroundColor(getResources().getColor(backgroundColor));
        binding.JobRecommendSection.addView(positionView);
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
                        binding.top1TextView.setText(
                                "1위) " + top3.get(0).getName() + " " + String.format("%.2f", top3.get(0).getRating()) + "점"
                        );
                    }
                    if (top3.size() > 1) {
                        binding.top2TextView.setText(
                                "2위) " + top3.get(1).getName() + " " + String.format("%.2f", top3.get(1).getRating()) + "점"
                        );
                    }
                    if (top3.size() > 2) {
                        binding.top3TextView.setText(
                                "3위) " + top3.get(2).getName() + " " + String.format("%.2f", top3.get(2).getRating()) + "점"
                        );
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
