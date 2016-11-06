package com.example.admin.photogallary;

import android.annotation.SuppressLint;
import android.util.Log;
import android.widget.BaseAdapter;

import com.example.admin.photogallary.stickygridheaders.StickyGridHeadersSimpleAdapter;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.photogallary.MyImageView.OnMeasureListener;
import com.example.admin.photogallary.NativeImageLoader.NativeImageCallBack;
import com.example.admin.photogallary.stickygridheaders.StickyGridHeadersSimpleAdapter;
/**
 * Created by admin on 11/3/16.
 */

public class StickyGridAdapter extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {
    private List<GridItem> hasHeaderIdList;
    private LayoutInflater mInflater;
    private GridView mGridView;
    private Point mPoint = new Point(0, 0);//Used to package ImageView the width and height of the object

    public StickyGridAdapter(Context context, List<GridItem> hasHeaderIdList,
                             GridView mGridView) {
        mInflater = LayoutInflater.from(context);
        this.mGridView = mGridView;
        this.hasHeaderIdList = hasHeaderIdList;
    }


    @Override
    public int getCount() {
        return hasHeaderIdList.size();
    }

    @Override
    public Object getItem(int position) {
        return hasHeaderIdList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_item, parent, false);
            mViewHolder.mImageView = (MyImageView) convertView
                    .findViewById(R.id.grid_item);
            convertView.setTag(mViewHolder);

            //To monitor ImageView width and height
            mViewHolder.mImageView.setOnMeasureListener(new OnMeasureListener() {

                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        String path = hasHeaderIdList.get(position).getPath();
        mViewHolder.mImageView.setTag(path);

        Bitmap bitmap = NativeImageLoader.getInstance().loadNativeImage(path, mPoint,
                new NativeImageLoader.NativeImageCallBack() {

                    @Override
                    public void onImageLoader(Bitmap bitmap, String path) {
                        ImageView mImageView = (ImageView) mGridView
                                .findViewWithTag(path);
                        if (bitmap != null && mImageView != null) {
                            mImageView.setImageBitmap(bitmap);
                        }
                    }
                });

        if (bitmap != null) {
            mViewHolder.mImageView.setImageBitmap(bitmap);
        } else {
            mViewHolder.mImageView.setImageResource(R.drawable.friends_sends_pictures_no);
        }

        return convertView;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder mHeaderHolder;

        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.header, parent, false);
            mHeaderHolder.mTextView = (TextView) convertView
                    .findViewById(R.id.header);
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }
        String time=hasHeaderIdList.get(position).getTime();


        int month=Integer.valueOf(time.substring(0,2))-1;
        Log.d("time:",time+"as"+month);
        mHeaderHolder.mTextView.setText(""+theMonth(month)+" " +time.substring(2));

        return convertView;
    }

    /**
     * Access to HeaderId, if HeaderId is not equal to add a Header
     */
    @Override
    public long getHeaderId(int position) {
        return hasHeaderIdList.get(position).getHeaderId();
    }


    public static class ViewHolder {
        public MyImageView mImageView;
    }

    public static class HeaderViewHolder {
        public TextView mTextView;
    }
    public static String theMonth(int month){
        String[] monthNames = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
        return monthNames[month];
    }
}
