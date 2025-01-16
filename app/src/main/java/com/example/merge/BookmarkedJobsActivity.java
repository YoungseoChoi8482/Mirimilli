package com.example.merge;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class BookmarkedJobsActivity extends AppCompatActivity {

    private RecyclerView bookmarkedRecyclerView;
    private JobAdapter jobAdapter;
    private List<Job> bookmarkedJobs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bookmarked_jobs);

        bookmarkedRecyclerView = findViewById(R.id.bookmarkedRecyclerView);
        bookmarkedRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        bookmarkedJobs = new ArrayList<>();
        jobAdapter = new JobAdapter(this, job -> {
            Intent intent = new Intent(this, DetailActivity.class);
            intent.putExtra("jobId", job.getId());
            startActivity(intent);
        });

        bookmarkedRecyclerView.setAdapter(jobAdapter);

        loadBookmarkedJobs();
    }

    private void loadBookmarkedJobs() {
        String userId = FirebaseAuth.getInstance().getCurrentUser() != null
                ? FirebaseAuth.getInstance().getCurrentUser().getUid()
                : null;

        if (userId == null) {
            Toast.makeText(this, R.string.error_not_logged_in, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        FirebaseFirestore firestore = FirebaseFirestore.getInstance();

        firestore.collection("bookmarkedJobs").document(userId).collection("jobs")
                .get()
                .addOnSuccessListener(querySnapshot -> {
                    bookmarkedJobs.clear();
                    for (DocumentSnapshot document : querySnapshot.getDocuments()) {
                        Job job = document.toObject(Job.class);
                        if (job != null) {
                            job.setId(document.getId());
                            bookmarkedJobs.add(job);
                        }
                    }
                    jobAdapter.setJobs(bookmarkedJobs);
                })
                .addOnFailureListener(e -> {
                    Log.e("BookmarkedJobs", "Failed to load bookmarked jobs", e);
                    Toast.makeText(this, R.string.error_loading_data, Toast.LENGTH_SHORT).show();
                });
    }
}