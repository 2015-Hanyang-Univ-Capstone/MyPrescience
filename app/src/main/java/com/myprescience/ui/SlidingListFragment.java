package com.myprescience.ui;

import android.app.ListFragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.myprescience.R;

/**
 * Created by paveld on 4/17/14.
 */
public class SlidingListFragment extends ListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.sliding_fragment_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListAdapter(new MyListAdapter());
    }

    private class MyListAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return 5;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView result = (TextView) convertView;
            if (result == null) {
                result = (TextView) LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.list_view_item, parent, false);
            }
            result.setText("Apply Filter #" + position);

            return result;
        }
    }
}
