package com.example.admin.photogallary;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
/**
 * Created by admin on 11/3/16.
 */

public class ImageScanner {
    private Context mContext;

    public ImageScanner(Context context){
        this.mContext = context;
    }

    /**
     * Using ContentProvider scanning of mobile phone in the picture, will scan the Cursor callback to ScanCompleteCallBack
     * ScanComplete interface, this method in the running thread in the sub
     */
    public void scanImages(final ScanCompleteCallBack callback) {
        final Handler mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                callback.scanComplete((Cursor)msg.obj);
            }
        };

        new Thread(new Runnable() {

            @Override
            public void run() {
                //Send broadcast scan the entire SD card
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                    Intent mediaScanIntent = new Intent(
                            Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    //Uri contentUri = Uri.parse("file://" + Environment.getExternalStorageDirectory());
                    //mediaScanIntent.setData(contentUri);
                    mContext.sendBroadcast(mediaScanIntent);
                } else {
                    mContext.sendBroadcast(new Intent(
                            Intent.ACTION_MEDIA_MOUNTED,
                            Uri.parse("file://"
                                    + Environment.getExternalStorageDirectory())));
                }
//                mContext.sendBroadcast(new Intent(
//                        Intent.ACTION_MEDIA_MOUNTED,
//                        Uri.parse("file://" + Environment.getExternalStorageDirectory())));

                Uri mImageUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                ContentResolver mContentResolver = mContext.getContentResolver();

                Cursor mCursor = mContentResolver.query(mImageUri, null, null, null, MediaStore.Images.Media.DATE_ADDED);

                //Using Handler to inform the calling thread
                Message msg = mHandler.obtainMessage();
                msg.obj = mCursor;
                mHandler.sendMessage(msg);
            }
        }).start();

    }

    /**
     * After the completion of the callback interface scanning
     *
     */
    public static interface ScanCompleteCallBack{
        public void scanComplete(Cursor cursor);
    }
}
