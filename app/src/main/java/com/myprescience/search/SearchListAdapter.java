package com.myprescience.search;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.dto.SearchData;
import com.myprescience.ui.song.SongActivity;
import com.myprescience.util.ViewHolder;

import java.util.ArrayList;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class SearchListAdapter extends BaseAdapter {

    private int userId;
    private Context mContext = null;
    private ArrayList<SearchData> mListData = new ArrayList<>();
    private ViewHolder holder;

    public SearchListAdapter(Context mContext, int _userId) {
        super();
        this.mContext = mContext;
        this.userId = _userId;
    }

    public void addItem(String _id, String _name, String _artist){
        SearchData temp = new SearchData();
        temp.id = _id;
        temp.name = _name;
        temp.artist = _artist;
        mListData.add(temp);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_searchresult, null);

            holder.resultLayout = (LinearLayout) convertView.findViewById(R.id.resultLayout);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            holder.artistTextView = (TextView) convertView.findViewById(R.id.artistTextView);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final SearchData mData = mListData.get(position);

        holder.titleTextView.setText(mData.name);
        if(mData.artist != null)
            holder.artistTextView.setText(mData.artist);
        else
            holder.artistTextView.setText("Various Artist");
        holder.position = position;

        holder.resultLayout.setTag(mData.id);
        holder.resultLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(v.getTag() != null) {
                    Intent intent = new Intent(v.getContext(), SongActivity.class);
                    intent.putExtra("song_id", v.getTag() + "");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    v.getContext().startActivity(intent);
                }
            }
        });

        return convertView;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public SearchData getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

}
