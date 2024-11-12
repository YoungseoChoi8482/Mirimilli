package com.example.merge;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = "DetailActivity";

    private ImageButton backButton;
    private TextView detailTitleTextView, jobDescriptionTextView;
    private Button writeReviewButton;
    private List<Review> reviewList;
    private RecyclerView reviewRecyclerView;
    private ReviewAdapter reviewAdapter;

    private Button sortByLikesButton, sortByNewestButton, sortByOldestButton;

    private Job selectedJob; // 클래스 레벨 변수 선언

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);


        backButton = findViewById(R.id.backButton);
        detailTitleTextView = findViewById(R.id.detailTitleTextView);
        jobDescriptionTextView = findViewById(R.id.jobDescriptionTextView);
        writeReviewButton = findViewById(R.id.writeReviewButton);

        sortByLikesButton = findViewById(R.id.sortByLikesButton);
        sortByNewestButton = findViewById(R.id.sortByNewestButton);
        sortByOldestButton = findViewById(R.id.sortByOldestButton);

        reviewRecyclerView = findViewById(R.id.reviewRecyclerView);

        // 선택된 보직 가져오기
        selectedJob = getIntent().getParcelableExtra("selectedJob");
        if (selectedJob == null) {
            selectedJob = SelectedJobManager.getInstance().getSelectedJob();
        }

        Log.d(TAG, "Selected Job: " + (selectedJob != null ? selectedJob.getName() : "null"));

        if (selectedJob != null) {
            detailTitleTextView.setText(selectedJob.getName());
            jobDescriptionTextView.setText(selectedJob.getDescription() + "\n\n평점: " + selectedJob.getRating() + "\n리뷰 수: " + selectedJob.getReviewCount());

            // 사용자의 보직 가져오기
            Job userJob = SelectedJobManager.getInstance().getUserJob();
            if (userJob != null && userJob.equals(selectedJob)) {
                writeReviewButton.setVisibility(Button.VISIBLE);
            } else {
                writeReviewButton.setVisibility(Button.GONE);
            }
        } else {
            writeReviewButton.setVisibility(Button.GONE);
        }

        backButton.setOnClickListener(view -> finish());

        writeReviewButton.setOnClickListener(view -> {
            if (selectedJob != null) { // Null 체크 추가
                Intent writeReviewIntent = new Intent(DetailActivity.this, WriteReviewActivity.class);
                writeReviewIntent.putExtra("selectedJob", selectedJob);
                startActivityForResult(writeReviewIntent, 2);
            } else {
                Toast.makeText(this, "보직 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            }
        });

        // RecyclerView 설정
        reviewList = new ArrayList<>();

        reviewAdapter = new ReviewAdapter(review -> {
            // 리뷰 클릭 시 상세 화면 이동
            Intent intent = new Intent(DetailActivity.this, ReviewDetailActivity.class);
            intent.putExtra("selectedReview", review);
            startActivity(intent);
        });
        reviewRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        reviewRecyclerView.setAdapter(reviewAdapter);

        // 정렬 버튼 설정
        sortByLikesButton.setOnClickListener(v -> sortReviewsByLikes());
        sortByNewestButton.setOnClickListener(v -> sortReviewsByNewest());
        sortByOldestButton.setOnClickListener(v -> sortReviewsByOldest());

        // 리뷰 데이터 로드
        loadReviewData();
    }

    private void sortReviewsByLikes() {
        if (reviewList == null) return; // Null 체크 추가
        List<Review> sortedList = new ArrayList<>(reviewList);
        Collections.sort(sortedList, (r1, r2) -> Integer.compare(r2.getLike(), r1.getLike()));
        reviewAdapter.setReviews(sortedList);
    }

    private void sortReviewsByNewest() {
        if (reviewList == null) return; // Null 체크 추가
        List<Review> sortedList = new ArrayList<>(reviewList);
        Collections.sort(sortedList, (r1, r2) -> r2.getDate().compareTo(r1.getDate()));
        reviewAdapter.setReviews(sortedList);
    }

    private void sortReviewsByOldest() {
        if (reviewList == null) return; // Null 체크 추가
        List<Review> sortedList = new ArrayList<>(reviewList);
        Collections.sort(sortedList, (r1, r2) -> r1.getDate().compareTo(r2.getDate()));
        reviewAdapter.setReviews(sortedList);
    }

    private void loadReviewData() {
        if (selectedJob == null) {
            Toast.makeText(this, "보직 정보가 없습니다.", Toast.LENGTH_SHORT).show();
            return;
        }

        new LoadReviewsTask().execute(selectedJob.getName());
    }

    private class LoadReviewsTask extends AsyncTask<String, Void, List<Review>> {
        @Override
        protected List<Review> doInBackground(String... strings) {
            String jobName = strings[0];
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            return db.reviewDao().getReviewsForJob(jobName);
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {
            super.onPostExecute(reviews);
            if (reviews != null && !reviews.isEmpty()) {
                reviewList.clear();
                reviewList.addAll(reviews);
                reviewAdapter.setReviews(new ArrayList<>(reviewList));

                // 각 질문에 대한 평균 별점 계산
                float avgVacation = 0, avgWorkload = 0, avgFatigue = 0;
                float avgWorkingConditions = 0, avgTraining = 0, avgAutonomy = 0;

                for (Review review : reviews) {
                    avgVacation += review.getVacation();
                    avgWorkload += review.getWorkload();
                    avgFatigue += review.getFatigue();
                    avgWorkingConditions += review.getWorkingConditions();
                    avgTraining += review.getTraining();
                    avgAutonomy += review.getAutonomy();
                }

                int count = reviews.size();
                avgVacation /= count;
                avgWorkload /= count;
                avgFatigue /= count;
                avgWorkingConditions /= count;
                avgTraining /= count;
                avgAutonomy /= count;

                List<Float> reviewRatings = new ArrayList<>();
                reviewRatings.add(avgVacation);
                reviewRatings.add(avgWorkload);
                reviewRatings.add(avgFatigue);
                reviewRatings.add(avgWorkingConditions);
                reviewRatings.add(avgTraining);
                reviewRatings.add(avgAutonomy);

                setupRadarChart(reviewRatings);
            } else {
                // 리뷰가 없는 경우 처리 (예: 빈 리스트 설정)
                reviewAdapter.setReviews(new ArrayList<>());
                Toast.makeText(DetailActivity.this, "리뷰가 없습니다.", Toast.LENGTH_SHORT).show();            }
        }
    }

    private void setupRadarChart(List<Float> reviewRatings) {
        // 레이더 차트 설정 코드 (현재 주석 처리됨)
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 2 && resultCode == RESULT_OK && data != null) {
            boolean reviewAdded = data.getBooleanExtra("reviewAdded", false);
            if (reviewAdded) {
                // 리뷰 추가 후 데이터 재로드
                loadReviewData();
            }
        }
    }
}