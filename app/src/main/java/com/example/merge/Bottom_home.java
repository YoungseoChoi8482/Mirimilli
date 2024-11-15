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

import com.example.merge.databinding.BottomHomeBinding;

public class Bottom_home extends Fragment {

    private BottomHomeBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

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
            Intent intent = new Intent(getActivity(), PopularJobActivity.class);
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
