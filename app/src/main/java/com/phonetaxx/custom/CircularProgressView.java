package com.phonetaxx.custom;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.View;
import com.phonetaxx.R;

public class CircularProgressView extends View {
    int BITMAP_WIDTH = 50;
    int BITMAP_HEIGHT = 50;
    int strock = 100;
    float bussinessStartAngle = 0;
    float businessSweepAngle = 120;
    float personalStartAngle = 120;
    float personalSweepAngle = 120;
    float uncategoryStartAngle = 240;
    float uncategorySweepAngle = 120;

    public CircularProgressView(Context context) {
        super(context);
    }

    public CircularProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public CircularProgressView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCircle(canvas);
//        drawBitmap(canvas);
    }

    private void drawBitmap(Canvas canvas) {
        RectF rect = new RectF(strock / 2, strock / 2, canvas.getWidth() - (strock / 2), canvas.getHeight() - (strock / 2));

        float centerX = (rect.left + rect.right) / 2;
        float centerY = (rect.top + rect.bottom) / 2;
        float radius = (rect.right - rect.left) / 2;


        float medianAngle = (90 + (180 / 2f)) * (float) Math.PI / 180f; // this angle will place the text in the center of the arc.

        float x = centerX + (float) (radius * Math.cos(medianAngle));
        float y = centerY + (float) (radius * Math.sin(medianAngle));

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.social_1);
        canvas.drawCircle(x, y, 5, getPaintWithColor(R.color.yellow));


        radius *= 0.5;
    }

    private void drawCircle(Canvas canvas) {
        RectF rect = new RectF(strock / 2, strock / 2, canvas.getWidth() - (strock / 2), canvas.getHeight() - (strock / 2));

        canvas.drawArc(rect, uncategoryStartAngle, uncategorySweepAngle, false, getPaintWithColor(R.color.circle_uncate_color));
        if (uncategorySweepAngle > 40) {
            drawBitmap(canvas, rect, uncategoryStartAngle, uncategorySweepAngle, R.drawable.ic_uncategory_circle);
        }
        canvas.drawArc(rect, personalStartAngle, personalSweepAngle, false, getPaintWithColor(R.color.circle_personal_color));
        if (personalSweepAngle > 40) {
            drawBitmap(canvas, rect, personalStartAngle, personalSweepAngle, R.drawable.ic_personal_circle);
        }
        canvas.drawArc(rect, bussinessStartAngle, businessSweepAngle, false, getPaintWithColor(R.color.circle_business_color));
        if (businessSweepAngle > 40) {
            drawBitmap(canvas, rect, bussinessStartAngle, businessSweepAngle, R.drawable.ic_business_for_circle);
        }

    }

    private void drawBitmap(Canvas canvas, RectF rect, float startAngle, float sweepAngle, int drawable) {

        float centerX = (rect.left + rect.right) / 2;
        float centerY = (rect.top + rect.bottom) / 2;
        float radius = (rect.right - rect.left) / 2;


        float medianAngle = (startAngle + (sweepAngle / 2f)) * (float) Math.PI / 180f; // this angle will place the text in the center of the arc.

        float x = centerX + (float) (radius * Math.cos(medianAngle));
        float y = centerY + (float) (radius * Math.sin(medianAngle));

        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), drawable);
        canvas.drawBitmap(getResizedBitmap(bitmap, BITMAP_WIDTH, BITMAP_HEIGHT), x - (BITMAP_HEIGHT / 2), y - (BITMAP_WIDTH / 2), null);

    }

    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {
        int width = bm.getWidth();
        int height = bm.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                bm, 0, 0, width, height, matrix, false);
        bm.recycle();
        return resizedBitmap;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    private Paint getPaintWithColor(int colorId) {
        Paint paint = new Paint();
        paint.setAntiAlias(false);
        paint.setDither(false);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(strock);
        paint.setColor(getResources().getColor(colorId));
        paint.setStrokeCap(Paint.Cap.ROUND);
        return paint;
    }

    public void setPercentage(float businessPercentage, float personalPercentage, float uncategoryPercentage) {
        businessSweepAngle = ((360 * businessPercentage) / 100);

        personalStartAngle = businessSweepAngle;

        personalSweepAngle = ((360 * personalPercentage) / 100);

        uncategoryStartAngle = personalSweepAngle + businessSweepAngle;

        uncategorySweepAngle = 360 - businessSweepAngle - personalSweepAngle;

        invalidate();

    }
}
