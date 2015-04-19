package com.myprescience.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import static com.myprescience.util.Server.USER_ID;

import com.myprescience.R;

@SuppressLint("ValidFragment")
public class RecommendTab extends Fragment {

    Context mContext;

    private ListView mRecommendListView;
    private RecommendSongListAdapter mRecommendSongListAdapter;

    public RecommendTab(Context context) {
        mContext = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.tab_activity_recommend, null);

        mRecommendListView = (ListView) view.findViewById(R.id.recommendSongListView);
        mRecommendSongListAdapter = new RecommendSongListAdapter(mContext, USER_ID);
        mRecommendListView.setAdapter(mRecommendSongListAdapter);

        mRecommendSongListAdapter.addItem("test", "albums/6gSCxFtdSR8Ig3VvntCBPE", "test", "test", 10);
        mRecommendSongListAdapter.addItem("test", "albums/6gSCxFtdSR8Ig3VvntCBPE", "test", "test", 10);
        mRecommendSongListAdapter.addItem("test", "albums/6gSCxFtdSR8Ig3VvntCBPE", "test", "test", 10);

        return view;
    }

}