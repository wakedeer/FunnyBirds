package com.example.katya.funny_birds;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;


public class SpriteBonus {

    private Bitmap bitmap;
    private Rect frame;
    private int frameWidth;
    private int frameHeight;;

    private double x;
    private double y;
    private double velocityX;
    private double velocityY;
    private int padding;


    public SpriteBonus(double x, double y, double velocityX, double velocityY, Rect initialFrame, Bitmap bitmap) {
        this.x = x;
        this.y = y;
        this.velocityX = velocityX;
        this.velocityY = velocityY;
        this.bitmap = bitmap;
        this.frame = initialFrame;
        this.bitmap = bitmap;
        this.frameWidth = initialFrame.width();
        this.frameHeight = initialFrame.height();
        this.padding = 20;
    }

    public boolean intersect(Sprite s) {
        return getBoundingBoxRect().intersect(s.getBoundingBoxRect());
    }

    public Rect getBoundingBoxRect() {
        return new Rect((int) x + padding,
                (int) y + padding,
                (int) (x + frameWidth - 2 * padding),
                (int) (y + frameHeight - 2 * padding));
    }

    public void draw(Canvas canvas) {
        Paint p = new Paint();
        Rect destination = new Rect((int) x, (int) y, (int) (x + frameWidth), (int) (y + frameHeight));
        canvas.drawBitmap(bitmap, frame, destination, p);
    }

    public void update(int ms) {
        x = x + velocityX * ms / 1000.0;
        y = y + velocityY * ms / 1000.0;
    }

    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getX() {
        return x;
    }

    public int getFrameHeight() {
        return frameHeight;
    }

    public int getFrameWidth() {
        return frameWidth;
    }
}
