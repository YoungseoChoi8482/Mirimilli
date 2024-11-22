package com.example.merge;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;


import com.example.merge.databinding.ActivityListOfJobBinding;

import java.util.ArrayList;
import java.util.List;

public class ListOfJob extends AppCompatActivity {

    private ActivityListOfJobBinding binding ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListOfJobBinding.inflate(getLayoutInflater());


        // RecyclerView 설정
        List<String> positions = new ArrayList<>();
        positions.add("수송병");
        positions.add("조리병");
        positions.add("의무병");

        PositionAdapter adapter = new PositionAdapter(positions);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);

        setContentView(binding.getRoot());
    }
}