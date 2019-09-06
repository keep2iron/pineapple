package io.github.keep2iron.pineapple.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool;
import com.bumptech.glide.load.resource.bitmap.TransformationUtils;
import java.security.MessageDigest;

public class CropCircleWithBorderTransformation extends BitmapTransformation {


  private static final int VERSION = 1;
  private static final String ID = "jp.wasabeef.glide.transformations.CropCircleWithBorderTransformation." + VERSION;

  private int borderSize;
  private int borderColor;

  public CropCircleWithBorderTransformation(int borderSize, @ColorInt int borderColor) {
    this.borderSize = borderSize;
    this.borderColor = borderColor;
  }

  @Override
  protected Bitmap transform(@NonNull Context context, @NonNull BitmapPool pool,
                             @NonNull Bitmap toTransform, int outWidth, int outHeight) {

    Bitmap bitmap = TransformationUtils.circleCrop(pool, toTransform, outWidth, outHeight);

    setCanvasBitmapDensity(toTransform, bitmap);

    Paint paint = new Paint();
    paint.setColor(borderColor);
    paint.setStyle(Paint.Style.STROKE);
    paint.setStrokeWidth(borderSize);
    paint.setAntiAlias(true);

    Canvas canvas = new Canvas(bitmap);
    canvas.drawCircle(
        outWidth / 2f,
        outHeight / 2f,
        Math.max(outWidth, outHeight) / 2f - borderSize / 2f,
        paint
    );

    return bitmap;
  }

  @Override
  public void updateDiskCacheKey(@NonNull MessageDigest messageDigest) {
    messageDigest.update((ID + borderSize + borderColor).getBytes(CHARSET));
  }

  @Override
  public boolean equals(Object o) {
    return o instanceof CropCircleWithBorderTransformation &&
        ((CropCircleWithBorderTransformation) o).borderSize == borderSize &&
        ((CropCircleWithBorderTransformation) o).borderColor == borderColor;
  }

  @Override
  public int hashCode() {
    return ID.hashCode() + borderSize * 100 + borderColor + 10;
  }
}