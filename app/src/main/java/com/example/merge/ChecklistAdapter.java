//ChecklistAdapter.java

package com.example.merge;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.merge.databinding.CommunityTalkBinding;

import java.util.List;

public class ChecklistAdapter extends RecyclerView.Adapter<ChecklistAdapter.ViewHolder> {

    private List<ChecklistItem> checklistItems;
    private Context context;





    public ChecklistAdapter( Context context, List<ChecklistItem> checklistItems) {
        this.context = context;
        this.checklistItems = checklistItems;
    }


    //onCreateViewHolder가 레이아웃 XML파일의 inflate를 담당한다.
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // View Binding 초기화
        CommunityTalkBinding binding = CommunityTalkBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ChecklistItem item = checklistItems.get(position);
        holder.binding.postTitle.setText(item.getTitle());
        holder.binding.postContent.setText(item.getContent());
        holder.binding.postTime.setText(item.getTime());
        holder.binding.starIcon.setOnClickListener(v -> {
            ImageView bookmarkIcon = holder.binding.starIcon;
            boolean isSelected = (boolean) bookmarkIcon.getTag(); // 현재 상태 가져오기 (기본값은 false)

            if (isSelected) {
                // 비워진 북마크 이미지로 변경
                bookmarkIcon.setImageResource(R.drawable.unfilled_star);
            } else {
                // 채워진 북마크 이미지로 변경
                bookmarkIcon.setImageResource(R.drawable.star);
            }

            bookmarkIcon.setTag(!isSelected); // 상태 업데이트
        });



        holder.binding.someTextView.setOnClickListener(v -> {
            // 클릭 이벤트 처리. 현재 클릭이 잘 되긴함.
            Log.d("ChecklistAdapter", "Post clicked: " + item.getTitle());
            Intent intent = new Intent(context.getApplicationContext(), CheckListCommunityComment.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("postId", item.getId());
            Log.d("ChecklistAdapter", "Passing postId: " + item.getId()); // postId 확인 로그 추가
            try {
                if (!(context instanceof AppCompatActivity)) {
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 새로운 태스크로 액티비티 시작 플래그 추가
                }
                context.startActivity(intent);
            } catch (Exception e) {
                Toast.makeText(context, "Activity를 시작할 수 없습니다: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ChecklistAdapter", "Failed to start activity", e);
            }
        });


        // 초기 상태 설정 (tag를 통해 초기 값 설정)
        holder.binding.starIcon.setTag(false);


    }


    @Override
    public int getItemCount() {
        return checklistItems.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CommunityTalkBinding binding; // View Binding 객체

        public ViewHolder(@NonNull CommunityTalkBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }


} // 체크리스트 커뮤니티 어댑터