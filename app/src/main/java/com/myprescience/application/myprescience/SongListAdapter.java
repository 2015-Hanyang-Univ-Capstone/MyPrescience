package com.myprescience.application.myprescience;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by hyeon-seob on 15. 3. 4..
 * 리스트 뷰에 곡 정보를 추가하기 위한 어댑터 클래스
 * addItem()으로 리스트에 곡 추가
 */

public class SongListAdapter extends BaseAdapter{

    private Context mContext = null;
    private ArrayList<SongData> mListData = new ArrayList<>();
    private ViewHolder holder;
    private ProgressBar progressBar;
    private int ratingCount;
    private TextView textView;
    private ImageButton rightButton;

    public SongListAdapter(Context mContext, int _ratingCount, ProgressBar _progressBar, TextView _textView, ImageButton _rightButton) {
        super();
        this.mContext = mContext;
        progressBar = _progressBar;
        ratingCount = _ratingCount;
        textView = _textView;
        rightButton = _rightButton;
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

        holder.albumImageView.setVisibility(View.VISIBLE);
        if (mData.albumArt != null){
            holder.albumImageView.setImageDrawable(mData.albumArt);
        }

        holder.titleTextView.setText(mData.title);
        holder.artistTextView.setText(mData.artist);
        holder.ratingBar.setProgress(mData.rating);
        holder.ratingBar.setTag(position);

        holder.ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                if(!fromUser) return;

                int index = (Integer)ratingBar.getTag();
                if(mListData.get(index).rating == 0) {
                    ratingCount++;
                    progressBar.setProgress((int)(Math.min(1, ratingCount/(double)SongListActivity.MIN_SELECTED_SONG)*100));
                    progressBar.invalidate();

                    textView.setText(ratingCount + "곡을 평가했습니다.");
                    textView.invalidate();

                    if(ratingCount >= SongListActivity.MIN_SELECTED_SONG)
                        rightButton.setVisibility(ImageButton.VISIBLE);
                }

                mListData.get(index).rating = (int)(rating*2);
                Toast toast = Toast.makeText(mContext, "평가되었습니다!", Toast.LENGTH_SHORT);
                toast.show();
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
