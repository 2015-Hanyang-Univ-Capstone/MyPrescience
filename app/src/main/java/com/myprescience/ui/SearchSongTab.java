package com.myprescience.ui;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.myprescience.R;
import com.myprescience.util.RangeSeekBar;

@SuppressLint("ValidFragment")
public class SearchSongTab extends Fragment {

    Context mContext;
    private Button mValenceRangeButton;
    private RangeSeekBar mValenceRangeSeekBar;

    public SearchSongTab(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_activity_search, null);

        mValenceRangeButton = (Button) view.findViewById(R.id.valanceRangeButton);
//        mValenceRangeSeekBar = (RangeSeekBar) view.findViewById(R.id.valanceRangeSeekBar);

        mValenceRangeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogSimple();
//                mValenceRangeSeekBar.setVisibility(View.VISIBLE);
            }
        });

        return view;
    }

    private void DialogSimple(){
        AlertDialog.Builder alt_bld = new AlertDialog.Builder(mContext);
        alt_bld.setMessage("Do you want to close this window ?").setCancelable(
                false).setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'Yes' Button
                    }
                }).setNegativeButton("No",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Action for 'NO' Button
                        dialog.cancel();
                    }
                });
        AlertDialog alert = alt_bld.create();
        // Title for AlertDialog
        alert.setTitle("Title");
        // Icon for AlertDialog
        alert.setIcon(R.drawable.logo);
        alert.show();
    }

}