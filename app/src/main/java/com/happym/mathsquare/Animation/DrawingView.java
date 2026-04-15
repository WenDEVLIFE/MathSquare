package com.happym.mathsquare.Animation;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.PorterDuff;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import com.google.mlkit.vision.digitalink.recognition.Ink;

public class DrawingView extends View {
    private Path drawPath;

    private float handX = -100; // Start off-screen
    private float handY = -100;
    private Paint handPaint;
    private Paint drawPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    private int paintColor = Color.BLACK;
    private OnDrawingListener listener;
    private Ink.Builder inkBuilder = Ink.builder();
    private Ink.Stroke.Builder strokeBuilder;
    private float lastX, lastY;
    private static final float TOUCH_TOLERANCE = 4;

    private Path hintPath;
    private Paint hintPaint;
    private Path animatedHintPath;
    private PathMeasure pathMeasure;

    private boolean isDrawingEnabled = true;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupDrawing();
        setupHintDrawing();
    }

    public void setDrawingEnabled(boolean enabled) {
        this.isDrawingEnabled = enabled;
    }

    public interface OnDrawingListener {
        void onStrokeStart();
        void onDrawingFinished();
    }

    public void setOnDrawingListener(OnDrawingListener listener) {
        this.listener = listener;
    }

    private void setupDrawing() {
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setDither(true);
        drawPaint.setStrokeWidth(35f);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void setupHintDrawing() {
        hintPaint = new Paint();
        hintPaint.setColor(Color.LTGRAY);
        hintPaint.setAlpha(80); // faint gray
        hintPaint.setAntiAlias(true);
        hintPaint.setStrokeWidth(40f);
        hintPaint.setStyle(Paint.Style.STROKE);
        hintPaint.setStrokeJoin(Paint.Join.ROUND);
        hintPaint.setStrokeCap(Paint.Cap.ROUND);

        hintPath = new Path();
        animatedHintPath = new Path();
        handPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        handPaint.setColor(Color.parseColor("#FFD700")); // Gold
        handPaint.setStyle(Paint.Style.FILL);
        handPaint.setShadowLayer(15, 0, 0, Color.YELLOW);
    }
    public void showAnimatedHint(String number) {
        if (getWidth() == 0 || getHeight() == 0) {
            post(() -> showAnimatedHint(number));
            return;
        }
        if (hintPath == null || hintPaint == null) setupHintDrawing();

        hintPath.reset();
        animatedHintPath.reset();

        float w = getWidth();
        float h = getHeight();

        char[] digits = number.toCharArray();
        int count = digits.length;

        // --- Digit height is the anchor: use 65% of the view height ---
        float digitHeight = h * 0.65f;

        // --- Maintain a natural aspect ratio per digit (width = 60% of height) ---
        float digitWidth = digitHeight * 0.60f;

        // --- Gap between digits ---
        float gap = digitWidth * 0.20f;

        // --- Cap total width so it never overflows the canvas ---
        float totalWidth = (digitWidth * count) + (gap * (count - 1));
        if (totalWidth > w * 0.85f) {
            float scale = (w * 0.85f) / totalWidth;
            digitWidth  *= scale;
            digitHeight *= scale;
            gap         *= scale;
            totalWidth   = (digitWidth * count) + (gap * (count - 1));
        }

        // --- Center horizontally and vertically ---
        float startX       = (w - totalWidth) / 2f;
        float top          = (h - digitHeight) / 2f;
        float bottom       = top + digitHeight;

        for (int i = 0; i < count; i++) {
            float left    = startX + (i * (digitWidth + gap));
            float right   = left + digitWidth;
            float centerX = (left + right) / 2f;
            float middleY = (top + bottom) / 2f;

            appendDigitPath(String.valueOf(digits[i]),
                    left, right, top, bottom, centerX, middleY, w, h);
        }

        pathMeasure = new PathMeasure(hintPath, false);
        startAnimation();
    }

    private void appendDigitPath(String digit, float left, float right, float top, float bottom, float centerX, float middleY, float w, float h) {
        switch (digit) {
            case "0":
                // Start at top-center and draw a single continuous ellipse
                hintPath.moveTo(centerX, top);
                hintPath.cubicTo(right, top, right, bottom, centerX, bottom);
                hintPath.cubicTo(left, bottom, left, top, centerX, top);
                break;

            case "1":
                hintPath.moveTo(centerX - (w * 0.05f), top + (h * 0.05f));
                hintPath.lineTo(centerX, top);
                hintPath.lineTo(centerX, bottom);
                break;

            case "2":
                hintPath.moveTo(left, top + (h * 0.1f));
                hintPath.cubicTo(left, top - (h * 0.05f), right + (w * 0.05f), top, right, middleY);
                hintPath.lineTo(left, bottom);
                hintPath.lineTo(right, bottom);
                break;

            case "3":
                hintPath.moveTo(left, top);
                hintPath.lineTo(right, top);
                hintPath.lineTo(centerX, middleY);
                hintPath.quadTo(right, middleY, right, bottom - (h * 0.1f));
                hintPath.quadTo(right, bottom, centerX, bottom);
                hintPath.quadTo(left, bottom, left, bottom - (h * 0.05f));
                break;

            case "4":
                hintPath.moveTo(centerX + (w * 0.05f), top);
                hintPath.lineTo(left, middleY);
                hintPath.lineTo(right, middleY);

                hintPath.moveTo(right - (w * 0.1f), top);
                hintPath.lineTo(right - (w * 0.1f), bottom);
                break;

            case "5":
                hintPath.moveTo(right, top);
                hintPath.lineTo(left, top);
                hintPath.lineTo(left, middleY);
                hintPath.quadTo(right, middleY, right, bottom - (h * 0.1f));
                hintPath.quadTo(right, bottom, left, bottom);
                break;

            case "6":
                // Spiral-in style 6
                hintPath.moveTo(right - (w * 0.05f), top);
                hintPath.quadTo(left, top, left, middleY + (h * 0.1f));
                hintPath.cubicTo(left, bottom, right, bottom, right, middleY + (h * 0.1f));
                hintPath.cubicTo(right, middleY - (h * 0.1f), left, middleY - (h * 0.1f), left, middleY + (h * 0.1f));
                break;

            case "7":
                hintPath.moveTo(left, top);
                hintPath.lineTo(right, top);
                hintPath.lineTo(centerX, bottom);
                break;

            case "8":
                hintPath.moveTo(centerX, middleY);
                hintPath.cubicTo(left, middleY, left, top, centerX, top);
                hintPath.cubicTo(right, top, right, middleY, centerX, middleY);
                hintPath.cubicTo(left, middleY, left, bottom, centerX, bottom);
                hintPath.cubicTo(right, bottom, right, middleY, centerX, middleY);
                break;

            case "9":
                float smallCircleWidth = (right - left) * 0.60f;
                float circleRight = right;
                float circleLeft = right - smallCircleWidth;
                float circleBottom = top + smallCircleWidth;

                hintPath.moveTo(circleRight, (top + circleBottom) / 2f);
                hintPath.arcTo(circleLeft, top, circleRight, circleBottom, 0, -359, false);

                hintPath.lineTo(circleRight, bottom);
                break;
        }
    }

    private void startAnimation() {
        ValueAnimator animator = ValueAnimator.ofFloat(0, 1);
        animator.setDuration(5000);
        animator.setInterpolator(new android.view.animation.LinearInterpolator());

        animator.addUpdateListener(animation -> {
            float animatedValue = (float) animation.getAnimatedValue();
            animatedHintPath.reset();

            PathMeasure measure = new PathMeasure(hintPath, false);
            float totalLength = 0;
            do { totalLength += measure.getLength(); } while (measure.nextContour());

            float distanceToDraw = totalLength * animatedValue;
            float accumulatedLength = 0;
            measure = new PathMeasure(hintPath, false);

            do {
                float segmentLength = measure.getLength();
                if (distanceToDraw <= accumulatedLength + segmentLength) {
                    float segmentDistance = distanceToDraw - accumulatedLength;
                    measure.getSegment(0, segmentDistance, animatedHintPath, true);

                    float[] pos = new float[2];
                    measure.getPosTan(segmentDistance, pos, null);
                    handX = pos[0];
                    handY = pos[1];
                    break;
                } else {
                    measure.getSegment(0, segmentLength, animatedHintPath, true);
                    accumulatedLength += segmentLength;
                }
            } while (measure.nextContour());

            invalidate();
        });

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                postDelayed(() -> {
                    handX = -100;
                    handY = -100;
                    invalidate();
                }, 1000);
            }
        });
        animator.start();
    }
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (hintPath != null) canvas.drawPath(hintPath, hintPaint);
        if (animatedHintPath != null) {
            Paint tracePaint = new Paint(hintPaint);
            tracePaint.setAlpha(180);
            canvas.drawPath(animatedHintPath, tracePaint);
        }

        if (handX > 0) {
            canvas.drawCircle(handX, handY, 25f, handPaint);
        }
        if (canvasBitmap != null) canvas.drawBitmap(canvasBitmap, 0, 0, null);
        if (drawPath != null) canvas.drawPath(drawPath, drawPaint);
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isDrawingEnabled) return false;

        float x = event.getX();
        float y = event.getY();
        long t = System.currentTimeMillis();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (listener != null) listener.onStrokeStart();
                drawPath.reset(); // Reset path for a fresh stroke
                drawPath.moveTo(x, y);
                lastX = x;
                lastY = y;
                strokeBuilder = Ink.Stroke.builder();
                strokeBuilder.addPoint(Ink.Point.create(x, y, t));
                break;

            case MotionEvent.ACTION_MOVE:
                float dx = Math.abs(x - lastX);
                float dy = Math.abs(y - lastY);
                if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
                    float midX = (x + lastX) / 2;
                    float midY = (y + lastY) / 2;
                    drawPath.quadTo(lastX, lastY, midX, midY);

                    lastX = x;
                    lastY = y;
                    strokeBuilder.addPoint(Ink.Point.create(x, y, t));
                }
                break;

            case MotionEvent.ACTION_UP:
                drawPath.lineTo(lastX, lastY);

                if (drawCanvas != null) {
                    drawCanvas.drawPath(drawPath, drawPaint);
                }

                if (strokeBuilder != null) {
                    strokeBuilder.addPoint(Ink.Point.create(x, y, t));
                    inkBuilder.addStroke(strokeBuilder.build());
                }

                // Reset the temporary drawing path
                drawPath.reset();
                performClick();
                if (listener != null) listener.onDrawingFinished();
                break;
        }
        invalidate();
        return true;
    }
    public Ink getInk() {
        return inkBuilder.build();
    }

    public void clearCanvas() {
        drawPath.reset();
        if (canvasBitmap != null) canvasBitmap.eraseColor(Color.TRANSPARENT);
        clearInk();
        invalidate();
    }

    public void setFeedbackColor(boolean isCorrect) {
        drawPaint.setColor(isCorrect ? Color.GREEN : Color.RED);
        // Clear hints
        if (hintPath != null) hintPath.reset();
        if (animatedHintPath != null) animatedHintPath.reset();
        if (pathMeasure != null) pathMeasure = null;
        invalidate();

    }

    public void resetMarkerColor() {
        // Reset paint color
        paintColor = Color.BLACK;
        if (drawPaint != null) {
            drawPaint.setColor(paintColor);
        }

        // Clear hints
        if (hintPath != null) hintPath.reset();
        if (animatedHintPath != null) animatedHintPath.reset();
        if (pathMeasure != null) pathMeasure = null;

        invalidate();
    }

    public void clearInk() {
        inkBuilder = Ink.builder();
    }
}
