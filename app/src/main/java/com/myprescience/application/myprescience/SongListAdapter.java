package com.myprescience.application.myprescience;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by hyeon-seob on 15. 3. 4..
 */
public class SongListAdapter extends BaseAdapter{

    private Context mContext = null;
    private ArrayList<SongData> mListData = new ArrayList<>();

    public SongListAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void addItem(Drawable _album, String _title, String _artist, int _rating){
        SongData temp = new SongData();
        temp.albumArt = _album;
        temp.title = _title;
        temp.artist = _artist;
        temp.rating = _rating;
        mListData.add(temp);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.song_list_item, null);

            holder.albumImageView = (ImageView) convertView.findViewById(R.id.albumArtView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            holder.artistTextView = (TextView) convertView.findViewById(R.id.artistTextView);
            holder.ratingBar = (RatingBar) convertView.findViewById(R.id.ratingBar);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        SongData mData = mListData.get(position);

        if (mData.albumArt != null) {
            holder.albumImageView.setVisibility(View.VISIBLE);
            holder.albumImageView.setImageDrawable(mData.albumArt);
        }else{
            holder.albumImageView.setVisibility(View.GONE);
        }

        holder.titleTextView.setText(mData.title);
        holder.artistTextView.setText(mData.artist);
        holder.ratingBar.setProgress(mData.rating);
        holder.ratingBar.setTag(position);

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(!fromUser) return;
                mListData.get((Integer)ratingBar.getTag()).rating = (int)(rating*2);
            }
        });
        return convertView;
    }

    @Override
    public int getCount() {
        return mListData.size();
    }

    @Override
    public SongData getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
}
