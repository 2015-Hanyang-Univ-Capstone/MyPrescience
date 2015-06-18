package com.myprescience.util;

import android.app.Activity;
import android.widget.Toast;

public class BackPressCloseHandler {

    private int MAIN = 0, PLAYER = 1;
    private long backKeyPressedTime = 0;
    private Toast toast;

    private Activity activity;

    public BackPressCloseHandler(Activity context) {
        this.activity = context;
    }

    public void onBackPressed(int mode) {
        if (System.currentTimeMillis() > backKeyPressedTime + 2000) {
            backKeyPressedTime = System.currentTimeMillis();
            if(mode == MAIN)
                showGuide();
            else if(mode == PLAYER)
                showGuide2();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2000) {
            activity.finish();
            toast.cancel();
        }
    }

    public void showGuide() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 종료됩니다.", Toast.LENGTH_SHORT);
        toast.show();
    }

    public void showGuide2() {
        toast = Toast.makeText(activity,
                "\'뒤로\'버튼을 한번 더 누르시면 음악 선택화면으로 돌아갑니다.", Toast.LENGTH_SHORT);
        toast.show();
    }
}