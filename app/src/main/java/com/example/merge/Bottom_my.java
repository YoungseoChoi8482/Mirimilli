package com.example.merge;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.appcompat.app.AppCompatActivity;

import com.example.merge.databinding.BottomMyBinding;


public class Bottom_my extends Fragment {

    private BottomMyBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = BottomMyBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        // 툴바 설정
        ((AppCompatActivity) requireActivity()).setSupportActionBar(binding.toolbar);
        ((AppCompatActivity) requireActivity()).getSupportActionBar().setTitle(null);

        // 나에게 쓰는 편지 화면으로 넘어가기
        binding.letterToMe.setOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), LetterToMe.class);
            startActivity(intent);
        });

        // 즐겨찾기 목록 화면으로 넘어가기
        binding.favoriteList.setOnClickListener(v->{
            Intent intent = new Intent(getActivity(), ListOfFavorite.class);
            startActivity(intent);
        });

        // 로그아웃 버튼 클릭 리스너 설정
        binding.btnLogout.setOnClickListener(v -> {
            // SharedPreferences에서 로그인 상태를 false로 설정하여 로그아웃 처리
            SharedPreferences preferences = requireActivity().getSharedPreferences("userPrefs", requireActivity().MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putBoolean("isLoggedIn", false);
            editor.apply();

            // 로그아웃 메시지
            Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

            // FirstPage로 이동
            Intent intent = new Intent(getActivity(), FirstPage.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;  // 뷰 바인딩 메모리 해제
    }
}
