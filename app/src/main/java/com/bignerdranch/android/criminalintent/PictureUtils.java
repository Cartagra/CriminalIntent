package com.bignerdranch.android.criminalintent;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;

/**
 * @author Ken
 * @version 1.0.0
 */

public class PictureUtils {
    public static Bitmap getScaledBitmap(String path, int destWidth, int destHeight) {
        // 读取磁盘上的图片尺寸
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        float srcWidth = options.outWidth;
        float srcHeight = options.outHeight;

        // 按比例计算缩小多少
        int inSampleSize = 1;
        if (srcHeight > destHeight || srcWidth > destWidth) {
            if (srcWidth > srcHeight) {
                inSampleSize = Math.round(srcHeight / destHeight);
            } else {
                inSampleSize = Math.round(srcWidth / destWidth);
            }
        }

        //图片超过4096x4096无法输出
        //W/OpenGLRenderer: Bitmap too large to be uploaded into a texture (4128x2322, max=4096x4096)
        if ((srcWidth > 4096 || srcHeight > 4096) && inSampleSize == 1) {
            float max = srcWidth > srcHeight ? srcWidth : srcHeight;
            inSampleSize = Double.valueOf(Math.ceil(max / 4096)).intValue();
        }


        options = new BitmapFactory.Options();
        options.inSampleSize = inSampleSize;

        // 读取和创建调整完成的bitmap
        return BitmapFactory.decodeFile(path, options);

    }

    public static Bitmap getScaledBitmap(String path, Activity activity) {
        Point size = new Point();
        activity.getWindowManager().getDefaultDisplay().getSize(size);
        return getScaledBitmap(path, size.x, size.y);
    }
}
