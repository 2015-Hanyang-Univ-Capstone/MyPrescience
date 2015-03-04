package com.myprescience.application.myprescience;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import java.util.Vector;


public class RecommendActivity extends Activity{
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

        rightButton = (ImageButton) findViewById(R.id.nextButton);
        rightButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RecommendActivity.this, SongListActivity.class);
                startActivity(intent);
            }
        });


        // 장르 리스트
        int pixels = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 170, getResources().getDisplayMetrics());
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        textView = (TextView) findViewById(R.id.top);
        rightButton.setVisibility(ImageButton.INVISIBLE);
        rightButton.invalidate();

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
        checkBoxVector = new Vector<>();
        for(int i=0; i<10; i++){
            CheckBox mCB = new CheckBox(this);
            mCB.setWidth(pixels);
            mCB.setHeight(pixels);
            mCB.setText(i + "");
            mCB.setBackgroundColor(0xB2EBF4);
            mCB.setOnCheckedChangeListener(listener);
            checkBoxVector.add(mCB);
        }

        // 그리드 뷰에 장르 추가
        gridLayout = (GridLayout) findViewById(R.id.gridLayout);
        for(CheckBox cb : checkBoxVector)
            gridLayout.addView(cb);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_recommend, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
