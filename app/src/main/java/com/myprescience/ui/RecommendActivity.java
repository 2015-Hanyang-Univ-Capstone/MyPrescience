package com.myprescience.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.myprescience.R;

import java.util.Vector;

/**
 * 장르 선택 액티비티
 */

public class RecommendActivity extends Activity{
    public static Activity sRecommendActivity;
    private GridLayout gridLayout;
    private Vector<CheckBox> checkBoxVector;
    private ProgressBar progressBar;
    private TextView textView;
    private ImageButton rightButton;
    private int selectCount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);


        // 다음 버튼
        rightButton = (ImageButton) findViewById(R.id.nextButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sRecommendActivity = RecommendActivity.this;
                Intent intent = new Intent(RecommendActivity.this, SongListActivity.class);
                startActivity(intent);
            }
        });


        // 장르 리스트
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.top);
        rightButton.setVisibility(ImageButton.INVISIBLE);
        rightButton.invalidate();


        // 장르가 3개이상 선택되면 다음 버튼 나타남, 3개 미만일 떈 다시 사라짐
        CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) selectCount++;
                else selectCount--;

                int progress;
                if(selectCount >= 3) progress = 100;
                else progress = Math.min(selectCount, 3)*33;
                progressBar.setProgress(progress);
                progressBar.invalidate();

                if(selectCount > 2) {
                    textView.setText(selectCount + "개 선택되었습니다.");
                    rightButton.setVisibility(ImageButton.VISIBLE);
                }
                else {
                    textView.setText("장르를 3개 이상 선택하세요.");
                    rightButton.setVisibility(ImageButton.INVISIBLE);
                }
                textView.invalidate();
                rightButton.invalidate();
            }
        };

        // 장르 초기화
        int width = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 150, getResources().getDisplayMetrics());
        int height = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 180, getResources().getDisplayMetrics());
        int[] genreArray = {R.drawable.button_pop, R.drawable.button_hiphop, R.drawable.button_rnb, R.drawable.button_rock,
                R.drawable.button_dance, R.drawable.button_jazz, R.drawable.button_electro, R.drawable.button_classic};
        checkBoxVector = new Vector<>();
        for(int mGenre : genreArray){
            CheckBox mCB = new CheckBox(this);
            mCB.setButtonDrawable(mGenre);
            mCB.setWidth(width);
            mCB.setHeight(height);
            mCB.setOnCheckedChangeListener(listener);
            checkBoxVector.add(mCB);
        }

        // 그리드 뷰에 장르 추가
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        for(CheckBox cb : checkBoxVector)
            gridLayout.addView(cb);
    }
}
