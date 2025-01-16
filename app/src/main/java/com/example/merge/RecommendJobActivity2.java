package com.example.merge;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class RecommendJobActivity2 extends AppCompatActivity {

    private LinearLayout parentLayout;
    private ArrayAdapter<String> certificateAdapter;
    private FirebaseFirestore db;

    private AutoCompleteTextView departmentAutoComplete;
    private List<String> certificates = new ArrayList<>();

    private TextView recommendedPosition1;
    private TextView recommendedPosition2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend_job2);

        // Initialize Firebase Firestore
        db = FirebaseFirestore.getInstance();

        // 학과 자동 완성
        departmentAutoComplete = findViewById(R.id.department_autocomplete);
        String[] departments = {"컴퓨터학과", "건축학과", "전기공학과", "기계공학과", "산업디자인", "computer", "정보보안학과", "전자정보공학과"};
        ArrayAdapter<String> departmentAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, departments);
        departmentAutoComplete.setAdapter(departmentAdapter);

        // 자격증 자동 완성
        AutoCompleteTextView certificateAutoComplete = findViewById(R.id.certificate_autocomplete);
        String[] certificatesArray = {"네트워크 관리사", "정보처리기사", "건축산업기사", "토목산업기사","전기기사",
                "건축설비산업기사","전자계산기기사", "공조냉동기계산업기사", "위험물관리산업기사", "기계설계기사",
                "열처리기능사", "전자계산기산업기사", "전기기능사","산업안전기사", "용접산업기사", "기계정비산업기사","information"};
        certificateAdapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, certificatesArray);
        certificateAutoComplete.setAdapter(certificateAdapter);

        // 자격증 추가 버튼
        Button addButton = findViewById(R.id.add_button);
        parentLayout = findViewById(R.id.department_autocomplete).getParent() instanceof LinearLayout ?
                (LinearLayout) departmentAutoComplete.getParent() : null;

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addCertificateField();
            }
        });

        // 입력 완료 버튼
        Button completeBtn = findViewById(R.id.completeBtn);
        recommendedPosition1 = findViewById(R.id.recommended_position1);
        recommendedPosition2 = findViewById(R.id.recommended_position2);

        completeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fetchRecommendedJobs();
            }
        });
    }

    private void addCertificateField() {
        if (parentLayout == null) return;

        // 새로운 AutoCompleteTextView 생성
        AutoCompleteTextView newCertificateField = new AutoCompleteTextView(this);

        // LayoutParams 설정
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        layoutParams.setMargins(0, 20, 0, 10); // 위쪽 간격 30dp

        newCertificateField.setLayoutParams(layoutParams);
        newCertificateField.setHint("자격증을 입력하세요");
        newCertificateField.setPadding(12, 12, 12, 12);
        newCertificateField.setTextColor(getResources().getColor(R.color.black));

        // AutoCompleteTextView에 어댑터 설정
        newCertificateField.setAdapter(certificateAdapter);

        // 새로운 AutoCompleteTextView를 parentLayout에 추가
        int index = parentLayout.indexOfChild(findViewById(R.id.certificate_autocomplete));
        parentLayout.addView(newCertificateField, index + 1);

        // Add the text from the new field to the list when user enters something
        newCertificateField.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && !newCertificateField.getText().toString().isEmpty()) {
                certificates.add(newCertificateField.getText().toString());
            }
        });
    }

    private void fetchRecommendedJobs() {
        String department = departmentAutoComplete.getText().toString();
        if (department.isEmpty()) {
            Toast.makeText(this, "학과를 입력하세요!", Toast.LENGTH_SHORT).show();
            return;
        }

        // 기본 자격증 입력값 추가
        AutoCompleteTextView certificateAutoComplete = findViewById(R.id.certificate_autocomplete);
        String baseCertificate = certificateAutoComplete.getText().toString();
        if (!baseCertificate.isEmpty()) {
            certificates.add(baseCertificate);
        }

        // Firestore 쿼리
        db.collection("milliJobs")
                .whereEqualTo("department", department)
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful() && task.getResult() != null) {
                        boolean matchFound = false;

                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<String> jobCertificates = (List<String>) document.get("certificates");
                            List<String> recommendedPositions = (List<String>) document.get("recommended");

                            if (jobCertificates != null && recommendedPositions != null) {
                                // 자격증 리스트에서 하나라도 일치하는지 확인
                                for (String cert : certificates) {
                                    if (jobCertificates.contains(cert)) {
                                        // 추천 보직 2개 확인
                                        if (recommendedPositions.size() >= 2) {
                                            recommendedPosition1.setText(recommendedPositions.get(0));
                                            recommendedPosition2.setText(recommendedPositions.get(1));
                                        } else {
                                            recommendedPosition1.setText("추천 보직 정보 부족");
                                            recommendedPosition2.setText("추천 보직 정보 부족");
                                        }
                                        matchFound = true;
                                        break; // 하나라도 일치하면 반복 종료
                                    }
                                }
                            }
                            if (matchFound) break;
                        }

                        if (!matchFound) {
                            Toast.makeText(this, "일치하는 추천 보직을 찾을 수 없습니다.", Toast.LENGTH_SHORT).show();
                            recommendedPosition1.setText("");
                            recommendedPosition2.setText("");
                        }
                    } else {
                        Toast.makeText(this, "데이터를 가져오는 데 실패했습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("FirebaseError", "Error getting documents: ", task.getException());
                    }
                });
    }

}
