package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Arrays;

public class FavoriteWork extends AppCompatActivity {

    private FirebaseFirestore db;
    private String selectedDate = "2024-10"; // 기본 선택 날짜
    private String selectedBranch = "army"; // 기본 선택 군종

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_work);

        db = FirebaseFirestore.getInstance();

        // 날짜 선택 Spinner 설정
        Spinner dateSpinner = findViewById(R.id.dateSpinner);
        ArrayAdapter<String> dateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item,
                Arrays.asList("2024-10", "2024-09", "2024-08",  "2024-07", "2023-06"));
        dateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSpinner.setAdapter(dateAdapter);

        // Spinner 선택 이벤트
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedDate = (String) parent.getItemAtPosition(position);
                loadAndDisplayData(selectedBranch, selectedDate); // 날짜 변경 시 데이터 로드
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // 기본 값 유지
            }
        });

        // 군종 버튼 설정
        setBranchButtonListener(R.id.armyButton, "army");
        setBranchButtonListener(R.id.navyButton, "navy");
        setBranchButtonListener(R.id.airForceButton, "airForce");
        setBranchButtonListener(R.id.rokmcButton, "rokmc");

        // 뒤로가기 버튼 설정
        ImageButton backButton = findViewById(R.id.btn_back1);
        backButton.setOnClickListener(view -> {
            Intent intent = new Intent(FavoriteWork.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        // 초기 데이터 로드
        loadAndDisplayData(selectedBranch, selectedDate);
    }

    /**
     * 특정 군종 버튼에 리스너 설정
     */
    private void setBranchButtonListener(int buttonId, String branch) {
        Button button = findViewById(buttonId);
        button.setOnClickListener(view -> {
            selectedBranch = branch;
            loadAndDisplayData(selectedBranch, selectedDate); // 군종 변경 시 데이터 로드
        });
    }

    /**
     * Firestore에서 데이터를 가져오고 UI에 표시
     */
    private void loadAndDisplayData(String branch, String date) {
        db.collection("FavoriteMilitaryBranches")
                .document(branch)
                .collection(date)
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    // UI 초기화
                    clearUI();

                    // Firestore 데이터에서 각 순위를 가져와 UI에 추가
                    for (QueryDocumentSnapshot document : querySnapshot) {
                        String name = document.getString("name");
                        String rate = document.getString("rate");
                        String imageUrl = document.getString("imageUrl");

                        if (name != null && rate != null && imageUrl != null) {
                            updateRankInLayout(name, rate, imageUrl, document.getId());
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(this, "데이터를 불러오는 중 오류 발생: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    /**
     * 특정 순위의 데이터를 레이아웃에 업데이트
     */
    private void updateRankInLayout(String name, String rate, String imageUrl, String rankId) {
        // rankId에 따라 해당 뷰를 찾아 업데이트
        int imageViewId, topTextViewId, bottomTextViewId;
        switch (rankId) {
            case "top1":
                imageViewId = R.id.top1Image;
                topTextViewId = R.id.top1TopText;
                bottomTextViewId = R.id.top1BottomText;
                break;
            case "top2":
                imageViewId = R.id.top2Image;
                topTextViewId = R.id.top2TopText;
                bottomTextViewId = R.id.top2BottomText;
                break;
            case "top3":
                imageViewId = R.id.top3Image;
                topTextViewId = R.id.top3TopText;
                bottomTextViewId = R.id.top3BottomText;
                break;
            default:
                return; // 알 수 없는 rankId는 무시
        }

        // 뷰 참조
        ImageView imageView = findViewById(imageViewId);
        TextView topTextView = findViewById(topTextViewId);
        TextView bottomTextView = findViewById(bottomTextViewId);

        if (imageView != null && topTextView != null && bottomTextView != null) {
            // 이미지와 텍스트 업데이트
            topTextView.setText(rankId.toUpperCase());
            bottomTextView.setText(String.format("%s\n%s", name, rate));
            Glide.with(this)
                    .load(imageUrl)
                    .placeholder(R.drawable.ic_launcher_background) // 기본 이미지
                    .error(R.drawable.baseline_cancel_24) // 오류 시 이미지
                    .into(imageView);
        }
    }

    /**
     * UI 초기화
     */
    private void clearUI() {
        // Top 1
        clearRank(R.id.top1Image, R.id.top1TopText, R.id.top1BottomText);

        // Top 2
        clearRank(R.id.top2Image, R.id.top2TopText, R.id.top2BottomText);

        // Top 3
        clearRank(R.id.top3Image, R.id.top3TopText, R.id.top3BottomText);
    }

    /**
     * 특정 순위의 UI를 초기화
     */
    private void clearRank(int imageViewId, int topTextViewId, int bottomTextViewId) {
        ImageView imageView = findViewById(imageViewId);
        TextView topTextView = findViewById(topTextViewId);
        TextView bottomTextView = findViewById(bottomTextViewId);

        if (imageView != null) {
            imageView.setImageResource(R.drawable.ic_launcher_background); // 기본 이미지
        }
        if (topTextView != null) {
            topTextView.setText("");
        }
        if (bottomTextView != null) {
            bottomTextView.setText("");
        }
    }
}
