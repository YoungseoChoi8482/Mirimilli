package com.example.merge;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;


public class FavoriteWork extends AppCompatActivity {

    private LinearLayout supportStatusList;
    private final String[][] armyRanks = {
            {"1위: 운전병", "지원률: 13%"},
            {"2위: 행정병", "지원률: 10%"},
            {"3위: 통신병", "지원률: 8%"}
    };

    private final String[][] navyRanks = {
            {"1위: 조리병", "지원률: 11.8%"},
            {"2위: 수송병", "지원률: 10%"},
            {"3위: 의무병", "지원률: 9.3%"}
    };

    private final String[][] airForceRanks = {
            {"1위: 군악병", "지원률: 9.7%"},
            {"2위: 행정병", "지원률: 8.9%"},
            {"3위: 정보보호병", "지원률: 7.5%"}
    };

    private final String[][] rokmcRanks = {
            {"1위: 포병", "지원률: 12%"},
            {"2위: 상륙병", "지원률: 9.5%"},
            {"3위: 지원병", "지원률: 7%"}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorite_work);

        supportStatusList = findViewById(R.id.supportStatusList);

        Button armyButton = findViewById(R.id.armyButton);
        Button navyButton = findViewById(R.id.navyButton);
        Button airForceButton = findViewById(R.id.airForceButton);
        Button rokmcButton = findViewById(R.id.rokmcButton);

        armyButton.setOnClickListener(view -> updateSupportStatus(armyRanks));
        navyButton.setOnClickListener(view -> updateSupportStatus(navyRanks));
        airForceButton.setOnClickListener(view -> updateSupportStatus(airForceRanks));
        rokmcButton.setOnClickListener(view -> updateSupportStatus(rokmcRanks));
    }

    private void updateSupportStatus(String[][] ranks) {
        supportStatusList.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(this);

        for (String[] rank : ranks) {
            View itemView = inflater.inflate(R.layout.support_status_item_favorite, supportStatusList, false);

            TextView positionText = itemView.findViewById(R.id.positionText);
            TextView rateText = itemView.findViewById(R.id.rateText);

            positionText.setText(rank[0]);
            rateText.setText(rank[1]);

            supportStatusList.addView(itemView);
        }
    }
}
