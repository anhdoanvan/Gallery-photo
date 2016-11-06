package com.example.admin.photogallary;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.Handler;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
/**
 * Created by admin on 11/3/16.
 */

public class NativeImageLoader {

        private static final String TAG = NativeImageLoader.class.getSimpleName();
        private static NativeImageLoader mInstance = new NativeImageLoader();
        private static LruCache<String, Bitmap> mMemoryCache;
        private ExecutorService mImageThreadPool = Executors.newFixedThreadPool(1);


        private NativeImageLoader() {
            //Maximum memory access applications
            final int maxMemory = (int) (Runtime.getRuntime().maxMemory());

            //Using the maximum memory 1/8 to store pictures
            final int cacheSize = maxMemory / 8;
            mMemoryCache = new LruCache<String, Bitmap>(cacheSize) {

                //For each image bytes
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getRowBytes() * bitmap.getHeight();
                }

            };
        }

        /**
         * Through the examples of this method to obtain the NativeImageLoader
         *
         * @return
         */
        public static NativeImageLoader getInstance() {
            return mInstance;
        }


        /**
         * Load the local picture, the picture is not cut
         *
         * @param path
         * @param mCallBack
         * @return
         */
        public Bitmap loadNativeImage(final String path, final NativeImageCallBack mCallBack) {
            return this.loadNativeImage(path, null, mCallBack);
        }

        /**
         * This method to load the local picture, here mPoint is used to encapsulate ImageView wide and high, we can according to the size of the ImageView control to cutting Bitmap
         * If you don't want to cut out picture, call loadNativeImage (final String path, final NativeImageCallBack mCallBack) to load
         *
         * @param path
         * @param mPoint
         * @param mCallBack
         * @return
         */
        public Bitmap loadNativeImage(final String path, final Point mPoint, final NativeImageCallBack mCallBack) {
            //To obtain the Bitmap in memory
            Bitmap bitmap = getBitmapFromMemCache(path);

            final Handler mHander = new Handler() {

                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    mCallBack.onImageLoader((Bitmap) msg.obj, path);
                }

            };

            //If the Bitmap is not in the memory cache is enabled, thread to load the local picture, and added Bitmap to mMemoryCache
            if (bitmap == null) {
                mImageThreadPool.execute(new Runnable() {

                    @Override
                    public void run() {
                        //To obtain the image thumbnail
                        Bitmap mBitmap = decodeThumbBitmapForFile(path, mPoint == null ? 0 : mPoint.x, mPoint == null ? 0 : mPoint.y);
                        Message msg = mHander.obtainMessage();
                        msg.obj = mBitmap;
                        mHander.sendMessage(msg);

                        //Pictures will be added to the in memory cache
                        addBitmapToMemoryCache(path, mBitmap);
                    }
                });
            }
            return bitmap;

        }


        /**
         * Add the Bitmap to the memory cache
         *
         * @param key
         * @param bitmap
         */
        private void addBitmapToMemoryCache(String key, Bitmap bitmap) {
            if (getBitmapFromMemCache(key) == null && bitmap != null) {
                mMemoryCache.put(key, bitmap);
            }
        }

        /**
         * To obtain the memory image based on key
         *
         * @param key
         * @return
         */
        private Bitmap getBitmapFromMemCache(String key) {
            Bitmap bitmap = mMemoryCache.get(key);

            if (bitmap != null) {
                Log.i(TAG, "get image for LRUCache , path = " + key);
            }
            return bitmap;
        }

        /**
         * Removal of LruCache in bitmap
         */
        public void trimMemCache() {
            mMemoryCache.evictAll();
        }


        /**
         * According to the View (mainly ImageView) wide and high to get a picture thumbnail
         *
         * @param path
         * @param viewWidth
         * @param viewHeight
         * @return
         */
        private Bitmap decodeThumbBitmapForFile(String path, int viewWidth, int viewHeight) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            //Set to true, analytic representation of Bitmap object, the object does not account for memory
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, options);
            //Set the zoom ratio
            options.inSampleSize = computeScale(options, viewWidth, viewHeight);

            //Set to false, parse the Bitmap object into memory
            options.inJustDecodeBounds = false;


            Log.e(TAG, "get Iamge form file,  path = " + path);

            return BitmapFactory.decodeFile(path, options);
        }


        /**
         * According to the View (mainly ImageView) width and height to calculate the Bitmap scaling. The default is not China "
         *
         * @param options
         * @param width
         * @param height
         */
        private int computeScale(BitmapFactory.Options options, int viewWidth, int viewHeight) {
            int inSampleSize = 1;
            if (viewWidth == 0 || viewWidth == 0) {
                return inSampleSize;
            }
            int bitmapWidth = options.outWidth;
            int bitmapHeight = options.outHeight;

            //If Bitmap is greater than the width or height we set pictures of View's width and height, the calculation scale
            if (bitmapWidth > viewWidth || bitmapHeight > viewWidth) {
                int widthScale = Math.round((float) bitmapWidth / (float) viewWidth);
                int heightScale = Math.round((float) bitmapHeight / (float) viewWidth);

                //In order to ensure the picture not scaling deformation, we take the smallest proportion of wide high
                inSampleSize = widthScale < heightScale ? widthScale : heightScale;
            }
            return inSampleSize;
        }


        /**
         * Callback interface loading local picture
         *
         * @author xiaanming
         */
        public interface NativeImageCallBack {
            /**
             * When a thread finished loading the local picture, Bitmap and image path correction in this method
             *
             * @param bitmap
             * @param path
             */
            public void onImageLoader(Bitmap bitmap, String path);
        }

}
