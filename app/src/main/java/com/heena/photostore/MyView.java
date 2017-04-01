package com.heena.photostore;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Bitmap.Config;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

/**
 * Created by fdsh on 07/02/2016.
 */
@SuppressLint("DrawAllocation")
public class MyView extends View {

    private GestureDetector gestureDetector;
    Bitmap bitmap,bitmap2,bitmapDisplay;
    int width,height,newWidth,newHieght;
    private RelativeLayout relImg;
    
    private Matrix transform = new Matrix();
    private Matrix just = new Matrix();
    private Vector2D position = new Vector2D();
    private float scale = 1;
    private float angle = 0;
    private TouchManager touchManager = new TouchManager(2);
    private boolean isInitialized = false;
    private Canvas can;

    private Vector2D vca = null;
    private Vector2D vcb = null;
    private Vector2D vpa = null;
    private Vector2D vpb = null;
    private boolean isCeck = false;
    public MyView(Context context,Bitmap bmp1,Bitmap bmp2,int w,int h,RelativeLayout img){
        super(context);
        gestureDetector = new GestureDetector(MyView.this.getContext(),
                new GestureList());
        this.bitmapDisplay=bmp2;
        this.bitmap=bmp1;
        this.bitmap2=bmp2;
        this.width=bmp1.getWidth();
        this.height=bmp1.getHeight();
        this.newHieght=h;
        this.newWidth=w;
        this.relImg=img;    
        this.can=new Canvas(bmp2);
    }

    private static float getDegreesFromRadians(float angle) {
        return (float) (angle * 180.0 / Math.PI);
    }

    @SuppressLint("DrawAllocation")
//	@Override
//    protected void onDraw(Canvas canvas) {
//        super.onDraw(canvas);
//        if(!isInitialized){
//            int w = getWidth();
//            int h = getHeight();
//            position.set(w / 2, h / 2);
//            isInitialized = true;
//        }
//        just.postScale(1, 1);
//        bitmapDisplay = Bitmap.createBitmap(newWidth, newHieght,
//                Bitmap.Config.ARGB_8888);
//        canvas=new Canvas(bitmapDisplay);
//        this.can = new Canvas(bitmapDisplay);
//        can.drawColor(Color.WHITE);
//        canvas.drawColor(Color.WHITE);
//        Paint paint = new Paint();
//
//        transform.reset();
//        transform.postTranslate(-width / 2.0f, -height / 2.0f);
//        transform.postRotate(getDegreesFromRadians(angle));
//        transform.postScale(scale, scale);
//        transform.postTranslate(position.getX(), position.getY());
//
//        canvas.drawBitmap(bitmap, transform, paint);
//        can.drawBitmap(bitmap, transform, paint);
//        // canvas.drawBitmap(bitmap2, just, paint);
//        float ratioX = newWidth / (float) bitmap2.getWidth();
//        float ratioY = newHieght / (float) bitmap2.getHeight();
//        float middleX = newWidth / 2.0f;
//        float middleY = newHieght / 2.0f;
//
//        Matrix scaleMatrix = new Matrix();
//        scaleMatrix.setScale(ratioX, ratioY);
//
//        canvas.drawBitmap(bitmap2, scaleMatrix, new Paint(
//                Paint.FILTER_BITMAP_FLAG));
//        can.drawBitmap(bitmap2,scaleMatrix,new Paint(Paint.FILTER_BITMAP_FLAG));
//    }

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		//bitmapDisplay=Bitmap.createBitmap(newWidth, newHieght, Config.ARGB_8888);
		Paint paint=new Paint();
		can=new Canvas(bitmap2.copy(Config.ARGB_8888, true));
		//canvas=new Canvas(bitmapDisplay);
		transform.reset();
		transform.postTranslate(width / 2.0f, height / 2.0f);
		transform.postRotate(getDegreesFromRadians(angle));
		transform.postScale(scale, scale);
		transform.postTranslate(position.getX(), position.getY());

		canvas.drawBitmap(bitmap, transform, paint);
		can.drawBitmap(bitmap, transform, paint);
		float ratioX = newWidth / (float) bitmap2.getWidth();
      float ratioY = newHieght / (float) bitmap2.getHeight();
      float middleX = newWidth / 2.0f;
      float middleY = newHieght / 2.0f;
      Matrix scaleMatrix = new Matrix();
      scaleMatrix.setScale(ratioX, ratioY);
		
		can.drawBitmap(bitmap2, scaleMatrix, new Paint(Paint.FILTER_BITMAP_FLAG));
		can.drawColor(Color.BLUE);
		canvas.drawBitmap(bitmap2, scaleMatrix, new Paint(Paint.FILTER_BITMAP_FLAG));
	}
    public Bitmap getBitmap() {
    	Paint paint=new Paint();
    	bitmapDisplay=Bitmap.createBitmap(newWidth, newHieght, Config.ARGB_8888);
    	Canvas newCanvas=new Canvas(bitmapDisplay);
    	transform.reset();
		transform.postTranslate(width / 2.0f, height / 2.0f);
		transform.postRotate(getDegreesFromRadians(angle));
		transform.postScale(scale, scale);
		transform.postTranslate(position.getX(), position.getY());
    	newCanvas.drawBitmap(bitmap, transform,paint);
    	float ratioX = newWidth / (float) bitmap2.getWidth();
        float ratioY = newHieght / (float) bitmap2.getHeight();
        float middleX = newWidth / 2.0f;
        float middleY = newHieght / 2.0f;
        Matrix scaleMatrix = new Matrix();
        scaleMatrix.setScale(ratioX, ratioY);
        newCanvas.drawBitmap(bitmap2, scaleMatrix, new Paint(Paint.FILTER_BITMAP_FLAG));
        return bitmapDisplay;
    }

    public void setFrameBitmap(Bitmap bit) {
        bitmap2 = bit;
        invalidate();
    }

    public void setBackBitmap(Bitmap bit) {
        bitmap = bit;
        this.width = bit.getWidth();
        this.height = bit.getHeight();
        invalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {

        vca = null;
        vcb = null;
        vpa = null;
        vpb = null;

        try {
            touchManager.update(e);

            if (touchManager.getPressCount() == 1) {
                // System.out.println("in the first click");
                vca = touchManager.getPoint(0);
                vpa = touchManager.getPreviousPoint(0);
                position.add(touchManager.moveDelta(0));
            } else {
                // System.out.println("in the second");
                if (touchManager.getPressCount() == 2) {
                    // System.out.println("in the second if ");
                    vca = touchManager.getPoint(0);
                    vpa = touchManager.getPreviousPoint(0);
                    vcb = touchManager.getPoint(1);
                    vpb = touchManager.getPreviousPoint(1);

                    Vector2D current = touchManager.getVector(0, 1);
                    Vector2D previous = touchManager.getPreviousVector(0, 1);
                    float currentDistance = current.getLength();
                    float previousDistance = previous.getLength();

                    if (previousDistance != 0) {
                        scale *= currentDistance / previousDistance;
                    }

                    angle -= Vector2D.getSignedAngleBetween(current, previous);
                } else {
                    // System.out.println("in the else");
                    vca = touchManager.getPoint(0);
                    vpa = touchManager.getPreviousPoint(0);
                    position.add(touchManager.moveDelta(0));
                }
            }

            invalidate();
        } catch (Throwable t) {
            // So lazy...
        }
        return gestureDetector.onTouchEvent(e);
    }

    private class GestureList extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {

            return true;
        }

        // event when double tap occurs
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            float x = e.getX();
            float y = e.getY();

            if (isCeck) {
                isCeck = false;
                //layoutBottom.setVisibility(View.VISIBLE);
                //layoutTop.setVisibility(View.VISIBLE);
                //relImg.setVisibility(View.VISIBLE);
                
            } else {
                //layoutBottom.setVisibility(View.GONE);
                //layoutTop.setVisibility(View.GONE);
                //layoutColor.setVisibility(View.GONE);
                isCeck = true;
            }
            System.out.println("In Double Tap Event ::::");
            return true;
        }
    }
}
