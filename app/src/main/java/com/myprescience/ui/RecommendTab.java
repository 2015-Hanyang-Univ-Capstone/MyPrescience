package com.myprescience.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.myprescience.R;

@SuppressLint("ValidFragment")
public class RecommendTab extends Fragment {
		Context mContext;
		
		public RecommendTab(Context context) {
			mContext = context;
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater,
				ViewGroup container, Bundle savedInstanceState) {
			View view = inflater.inflate(R.layout.tab_activity_recommend, null);

	    	return view;
		}

}