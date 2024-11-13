package com.example.merge;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class WriteReviewActivity extends AppCompatActivity {

    private RatingBar ratingBarVacation, ratingBarWorkload, ratingBarFatigue;
    private RatingBar ratingBarWorkingConditions, ratingBarTraining, ratingBarAutonomy;
    private EditText editTextContent;
    private Button submitReviewButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write_review);

        ratingBarVacation = findViewById(R.id.ratingBarVacation);
        ratingBarWorkload = findViewById(R.id.ratingBarWorkload);
        ratingBarFatigue = findViewById(R.id.ratingBarFatigue);
        ratingBarWorkingConditions = findViewById(R.id.ratingBarWorkingConditions);
        ratingBarTraining = findViewById(R.id.ratingBarTraining);
        ratingBarAutonomy = findViewById(R.id.ratingBarAutonomy);
        submitReviewButton = findViewById(R.id.submitReviewButton);
        editTextContent = findViewById(R.id.editTextContent); // 추가된 필드


        submitReviewButton.setOnClickListener(view -> {
            float vacation = ratingBarVacation.getRating();
            float workload = ratingBarWorkload.getRating();
            float fatigue = ratingBarFatigue.getRating();
            float workingConditions = ratingBarWorkingConditions.getRating();
            float training = ratingBarTraining.getRating();
            float autonomy = ratingBarAutonomy.getRating();
            String content = editTextContent.getText().toString().trim();
            String date = getCurrentDate(); // 현재 날짜를 가져오는 메서드 구현


            Job selectedJob = SelectedJobManager.getInstance().getSelectedJob();
            if (selectedJob == null) {
                Toast.makeText(WriteReviewActivity.this, "선택된 보직이 없습니다.", Toast.LENGTH_SHORT).show();
                return;
            }

            Review review = new Review(selectedJob.getName(), vacation, workload, fatigue, workingConditions, training, autonomy);
            review.setContent(content);
            review.setDate(date);

            // 비동기 작업으로 데이터베이스에 리뷰 저장
            new InsertReviewTask().execute(review);
        });
    }

    private class InsertReviewTask extends AsyncTask<Review, Void, Void> {
        @Override
        protected Void doInBackground(Review... reviews) {
            AppDatabase db = AppDatabase.getInstance(getApplicationContext());
            db.reviewDao().insert(reviews[0]);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(WriteReviewActivity.this, "리뷰가 성공적으로 작성되었습니다.", Toast.LENGTH_SHORT).show();

            // 리뷰 데이터를 로드하고 레이더 차트를 업데이트하기 위해 DetailActivity로 결과 전달
            Intent resultIntent = new Intent();
            resultIntent.putExtra("reviewAdded", true);
            setResult(RESULT_OK, resultIntent);
            finish();
        }
    }

    // 현재 날짜를 반환하는 메서드 구현
    private String getCurrentDate() {
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
        return sdf.format(new java.util.Date());
    }
}