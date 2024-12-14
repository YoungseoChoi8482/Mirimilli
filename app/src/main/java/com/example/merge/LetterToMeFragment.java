//LetterToMeFragment.java

// LetterToMeFragment.java

package com.example.merge;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LetterToMeFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LetterToMeFragment extends Fragment {

    // SharedPreferences 파일 이름과 키 정의
    private static final String PREFS_NAME = "LetterToMePrefs";
    private static final String KEY_TITLE = "title";
    private static final String KEY_CONTENT = "content";
    private static final String KEY_DATE = "date";

    private OnFragmentInteractionListener listener;

    public LetterToMeFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment.
     *
     * @return A new instance of fragment LetterToMeFragment.
     */
    public static LetterToMeFragment newInstance() {
        return new LetterToMeFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            listener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 추가 초기화가 필요하면 여기에 작성

        // 뒤로가기 버튼 동작을 처리하는 콜백 추가
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                // Back Press 시 Activity 종료
                navigateToFragment1();
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState){
        // 레이아웃 인플레이트
        return inflater.inflate(R.layout.fragment_letter_to_me, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        // 뷰 참조
        TextView titleFrame = view.findViewById(R.id.title_frame);
        TextView contextFrame = view.findViewById(R.id.context_frame);
        TextView contentFrame = view.findViewById(R.id.content_frame);
        TextView dateFrame = view.findViewById(R.id.content_date);
        Button btnClose = view.findViewById(R.id.btn_close);

        // SharedPreferences에서 데이터 불러오기
        Context context = getActivity();
        if(context != null){
            SharedPreferences sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

            String title = sharedPreferences.getString(KEY_TITLE, "제목을 입력하세요.");
            String content = sharedPreferences.getString(KEY_CONTENT, "내용을 입력하세요.");
            String date = sharedPreferences.getString(KEY_DATE,"날짜를 골라주세요.");
            // TextView에 데이터 설정
            contextFrame.setText(title);
            contentFrame.setText(content);
            dateFrame.setText(date);
        }

        // "수정!" 버튼 클릭 리스너
        btnClose.setOnClickListener(v -> {
            // Activity의 메서드를 호출하여 프래그먼트를 숨기고 폼을 다시 표시
            if (listener != null) {
                listener.onEditButtonClicked();
            }
        });

    }

    // Fragment 1로 돌아가는 메서드
    private void navigateToFragment1() {
        // Activity 2를 종료하여 Activity 1으로 돌아감
        requireActivity().finish();
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }
}