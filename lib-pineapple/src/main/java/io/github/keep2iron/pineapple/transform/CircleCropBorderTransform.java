package io.github.keep2iron.pineapple.transform;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Build;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;

import java.security.MessageDigest;

public class CircleCropBorderTransform extends BitmapTransformation {
    private int borderSize = 0;
    private int borderColor = 0;

    public CircleCropBorderTransform(int borderSize, @ColorInt int color) {
        if (borderSize > 0) {
            this.borderSize = borderSize;
        }
        if (color > 0) {
            this.borderColor = color;
        }
    }

    // The version of this transformation, incremented to correct an error in a previous version.
    // See #455.
    private static final int VERSION = 1;
    private static final String ID = "com.bumptech.glide.load.resource.bitmap.CircleCrop." + VERSION;
    private static final byte[] ID_BYTES = ID.getBytes(CHARSET);

    // Bitmap doesn't implement equals, so == and .equals are equivalent here.
    @SuppressWarnings("PMD.CompareObjectsWithEquals")
    @Override
    protected Bitmap transform(@NonNull BitmapPool pool, @NonNull Bitmap toTransform, int outWidth, int outHeight) {
        Bitmap circle = TransformationUtils.centerCrop(pool, toTransform, outWidth, outHeight);
        Bitmap mBitmap = addBorderToCircularBitmap(circle, borderSize, borderColor);
        return mBitmap;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof CircleCropBorderTransform;
    }

    @Override
    public int hashCode() {
        return ID.hashCode();
    }

    @Override
    public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
        messageDigest.update(ID_BYTES);
    }

    // Custom method to add a border around circular bitmap
    protected Bitmap addBorderToCircularBitmap(Bitmap srcBitmap, int borderWidth, int borderColor) {
        // Calculate the circular bitmap width with border
        int dstBitmapWidth = srcBitmap.getWidth() + borderWidth * 2;

        Bitmap dstBitmap = Bitmap.createBitmap(dstBitmapWidth, dstBitmapWidth, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(dstBitmap);
        canvas.drawBitmap(srcBitmap, borderWidth, borderWidth, null);

        Paint paint = new Paint();
        if (borderColor > 0) {
            paint.setColor(borderColor);
        }
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderWidth);
        paint.setAntiAlias(true);

        /*
            public void drawCircle (float cx, float cy, float radius, Paint paint)
                Draw the specified circle using the specified paint. If radius is <= 0, then nothing
                will be drawn. The circle will be filled or framed based on the Style in the paint.

            Parameters
                cx : The x-coordinate of the center of the cirle to be drawn
                cy : The y-coordinate of the center of the cirle to be drawn
                radius : The radius of the cirle to be drawn
                paint : The paint used to draw the circle
        */
        // Draw the circular border around circular bitmap
        canvas.drawCircle(
                canvas.getWidth() / 2, // cx
                canvas.getWidth() / 2, // cy
                canvas.getWidth() / 2 - borderWidth / 2, // Radius
                paint // Paint
        );

        // Free the native object associated with this bitmap.
        srcBitmap.recycle();

        // Return the bordered circular bitmap
        return dstBitmap;
    }
}