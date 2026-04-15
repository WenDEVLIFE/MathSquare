package com.happym.mathsquare.Animation;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.view.View;

public class VignetteEffect {

    public static void apply(Context context, View targetView) {
        apply(context, targetView, 0f);
    }

    public static void apply(Context context, View targetView, float cornerRadiusDp) {
        final Context resourceContext = (context != null) ? context : targetView.getContext();

        targetView.post(() -> {
            int width = targetView.getWidth();
            int height = targetView.getHeight();

            if (width == 0 || height == 0) return;

            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);

            int[] standardColors = new int[]{
                    Color.TRANSPARENT,
                    Color.parseColor("#30D4A017"),
                    Color.parseColor("#908B4513")
            };

            int[] lightColors = new int[]{
                    Color.TRANSPARENT,
                    Color.parseColor("#15D4A017"),
                    Color.parseColor("#458B4513")
            };

            int[] activeColors = (cornerRadiusDp > 0) ? lightColors : standardColors;

            RadialGradient gradient = new RadialGradient(
                    width / 2f, height / 2f,
                    Math.max(width, height) * 0.7f,
                    activeColors,
                    new float[]{0.0f, 0.4f, 1.0f},
                    Shader.TileMode.CLAMP
            );

            Paint paint = new Paint();
            paint.setAntiAlias(true);
            paint.setShader(gradient);

            RectF rect = new RectF(0, 0, width, height);

            if (cornerRadiusDp > 0) {
                float radiusPx = cornerRadiusDp * resourceContext.getResources().getDisplayMetrics().density;
                canvas.drawRoundRect(rect, radiusPx, radiusPx, paint);
            } else {
                canvas.drawRect(rect, paint);
            }

            BitmapDrawable vignetteDrawable = new BitmapDrawable(resourceContext.getResources(), bitmap);
            Drawable currentBg = targetView.getBackground();

            if (currentBg != null) {
                LayerDrawable combinedLayer = new LayerDrawable(new Drawable[]{currentBg, vignetteDrawable});
                targetView.setBackground(combinedLayer);
            } else {
                targetView.setBackground(vignetteDrawable);
            }
            targetView.setForeground(null);
        });
    }
}