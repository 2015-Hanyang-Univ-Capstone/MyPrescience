package com.myprescience.ui;

/**
 * Created by dongjun on 15. 4. 17..
 */
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myprescience.R;

public class NaviAdapter extends BaseAdapter {

    private Context context;
    private String[] titles;
    private int[] images;
    private LayoutInflater inflater;
    private int[] selectedposition;

    public NaviAdapter(Context context, String[] titles, int[] images,
                           int[] selectedposition) {
        // TODO Auto-generated constructor stub
        this.context = context;
        this.titles = titles;
        this.images = images;
        this.inflater = LayoutInflater.from(this.context);
        this.selectedposition = selectedposition;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return titles.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        ViewHolder mViewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_navi, null);
            mViewHolder = new ViewHolder();
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.tvTitle = (TextView) convertView.findViewById(R.id.navi_text);
        mViewHolder.ivIcon = (ImageView) convertView.findViewById(R.id.navi_icon);

        mViewHolder.tvTitle.setText(titles[position]);
        mViewHolder.ivIcon.setImageResource(images[position]);

        //Highlight the selected list item
        if (position == selectedposition[0]) {
            convertView.setBackgroundResource(R.color.color_base_light_blue);
            mViewHolder.tvTitle.setTextColor(Color.WHITE);
        } else {
            convertView.setBackgroundColor(Color.TRANSPARENT);
            mViewHolder.tvTitle.setTextColor(convertView.getResources().getColorStateList(R.color.color_base_blue));
        }

        return convertView;
    }

    private class ViewHolder {
        TextView tvTitle;
        ImageView ivIcon;
    }
}