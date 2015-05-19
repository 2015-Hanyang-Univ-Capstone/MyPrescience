package com.myprescience.ui.album;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.myprescience.R;
import com.myprescience.dto.AlbumData;
import com.myprescience.util.ViewHolder;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * Created by dongjun on 15. 4. 6..
 */
public class MyAlbumListAdapter extends BaseAdapter {

    private int userId;
    private Context mContext = null;
    private ArrayList<AlbumData> mListData = new ArrayList<>();
    private ViewHolder holder;

    public MyAlbumListAdapter(Context mContext, int _userId) {
        super();
        this.mContext = mContext;
        this.userId = _userId;
    }

    public void addItem(String _id, String _name, String _artist, String _release_date, String _image_300){
        AlbumData temp = new AlbumData();
        temp.id = _id;
        temp.name = _name;
        temp.artist = _artist;
        temp.release_date = _release_date;
        temp.image_300 = _image_300;
        mListData.add(temp);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            holder = new ViewHolder();

            LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.list_item_myalbum_artist, null);

            holder.albumImageView = (ImageView) convertView.findViewById(R.id.albumArtView);
            holder.titleTextView = (TextView) convertView.findViewById(R.id.titleTextView);
            holder.artistTextView = (TextView) convertView.findViewById(R.id.artistTextView);

            convertView.setTag(holder);
        }else{
            holder = (ViewHolder) convertView.getTag();
        }

        final AlbumData mData = mListData.get(position);

        holder.titleTextView.setText(mData.name);
        if(mData.artist != null)
            holder.artistTextView.setText(mData.artist);
        else
            holder.artistTextView.setText("Various Artist");
        holder.position = position;

        if(mData.image_300 != null) {
            if(mData.albumArt == null) {
                holder.albumImageView.setImageResource(R.drawable.image_loading);
                try {
                    new LoadAlbumArt(position, holder, mData).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mData.image_300);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                holder.albumImageView.setImageBitmap(mData.albumArt);
            }
        } else {
            holder.albumImageView.setImageResource(R.drawable.image_not_exist_300);
        }
        holder.albumImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(mData.id.equals("")) {
                    AlertDialog.Builder alert = new AlertDialog.Builder(mContext);
                    alert.setPositiveButton("확인", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();     //닫기
                        }
                    });
                    alert.setMessage("앨범 정보가 없습니다.");
                    alert.show();
                } else {
                    Intent intent = new Intent(v.getContext(), AlbumActivity.class);
                    intent.putExtra("spotifyAlbumID", "albums/" + mData.id);
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
    public AlbumData getItem(int position) {
        return mListData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    class LoadAlbumArt extends AsyncTask<String, String, Bitmap> {

        private int mPosition;
        private ViewHolder mHolder = null;
        private AlbumData albumData;

        public LoadAlbumArt(int positon, ViewHolder holder, AlbumData mAlbumData){
            this.mPosition = positon;
            this.mHolder = holder;
            this.albumData = mAlbumData;
        }

        @Override
        protected Bitmap doInBackground(String... url) {
            // Image 역시 UI Thread에서 바로 작업 불가.
            Bitmap myBitmap = null;
            try {
                URL urlConnection = new URL(url[0]);
                HttpURLConnection connection = (HttpURLConnection) urlConnection
                        .openConnection();
                connection.setDoInput(true);
                connection.connect();
                InputStream input = connection.getInputStream();
                myBitmap = BitmapFactory.decodeStream(input);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return myBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap mAlbumArt) {
            super.onPostExecute(mAlbumArt);
            albumData.setAlbumArt(mAlbumArt);
            if (mHolder.position == mPosition) {
                mHolder.albumImageView.setImageBitmap(mAlbumArt);
            }
        }
    }


}
