package com.example.katya.funny_birds;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.CountDownTimer;
import android.view.MotionEvent;
import android.view.View;

public class GameView extends View {


    public static final int QUANTITY_ENEMY_BIRDS = 3;

    private int points = 0;
    private int level = 1;
    private boolean mPaused = false;
    private boolean isCancel = false;

    private final int timerInterval = 30;//изменение игровой модели

    private Sprite playerBird;
    private Sprite[] enemyBird = new Sprite[QUANTITY_ENEMY_BIRDS];
    private SpriteBonus spriteBonus; //добавляем новый Sprite для бонусов
    private PauseBtn pauseBtn;
    private String strPause = "";

    private int viewWidth;
    private int viewHeight;

    Timer t;

    public GameView(Context context) {
        super(context);

        /*создание птицы игорка*/
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.player);
        int w = b.getWidth() / 5;
        int h = b.getHeight() / 3;
        Rect firstFrame = new Rect(0, 0, w, h);
        playerBird = new Sprite(10, 0, 0, 100, firstFrame, b);

        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 4; j++) {
                if (i == 0 && j == 0) {
                    continue;
                }
                if (i == 2 && j == 3) {
                    continue;
                }
                playerBird.addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));

            }
        }
        /*создание птиц противника*/

        for (int index = 0; index < QUANTITY_ENEMY_BIRDS; index++) {

            b = BitmapFactory.decodeResource(getResources(), R.drawable.enemy);
            w = b.getWidth() / 5;
            h = b.getHeight() / 3;
            firstFrame = new Rect(4 * w, 0, 5 * w, h);


            enemyBird[index] = new Sprite(2000, 250 + 200 * (Math.random() - 0.5), -300 + 100 * (Math.random() - 0.5), 0, firstFrame, b);

            for (int i = 0; i < 3; i++) {
                for (int j = 4; j >= 0; j--) {
                    if (i == 0 && j == 4) {
                        continue;
                    }
                    if (i == 2 && j == 0) {
                        continue;
                    }
                    enemyBird[index].addFrame(new Rect(j * w, i * h, j * w + w, i * w + w));
                }
            }
        }


        // БОНУС
        b = BitmapFactory.decodeResource(getResources(), R.drawable.bonus);
        w = b.getWidth(); //118
        h = b.getHeight(); //118
        firstFrame = new Rect(0, 0, w, h);
        spriteBonus = new SpriteBonus(2000, 300, -250, 0, firstFrame, b);
        b = BitmapFactory.decodeResource(getResources(), R.drawable.ic_pause_white_48dp);
        w = b.getWidth();
        h = b.getHeight();
        Rect pause = new Rect(0, 0, w, h);
        pauseBtn = new PauseBtn(10, 10, pause, b);


        t = new Timer();
        //t.start();
        t.start();

    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        viewWidth = w;
        viewHeight = h;
    }

    protected void update() {
        playerBird.update(timerInterval);

        spriteBonus.update(timerInterval);
        invalidate();

        for (int i = 0; i < QUANTITY_ENEMY_BIRDS; i++) {
            enemyBird[i].update(timerInterval); //OnUpdate


            if (enemyBird[i].getX() < -enemyBird[i].getFrameWidth()) {
                teleportEnemy(i);
                points += 10;
            }
            // Проверка столкновений
            if (enemyBird[i].intersect(playerBird)) {
                teleportEnemy(i);
                points -= 40;
            }
            if (enemyBird[i].getX() < -enemyBird[i].getFrameWidth()) {
                teleportEnemy(i);
                points += 10;
            }
            if (spriteBonus.getX() < -enemyBird[i].getFrameWidth()) {
                teleportBonus();
            }

            if (points > 100) {
                points = 0;
                level++;
                enemyBird[i].setVelocityX(enemyBird[i].getVelocityX() - 30);
            }
        }

        if (playerBird.getY() + playerBird.getFrameHeight() > viewHeight) {
            playerBird.setY(viewHeight - playerBird.getFrameHeight());
            playerBird.setVy(-playerBird.getVy());
            points--;
        } else if (playerBird.getY() < 0) {
            playerBird.setY(0);
            playerBird.setVy(-playerBird.getVy());
            points--;
        }

        if (spriteBonus.intersect(playerBird)) {
            teleportBonus();
            points += 40;
        }

        if (points <= -200) {
            strPause = "Вы проиграли";
            isCancel = true;
            t.cancel();
        }

        if (mPaused) {
            strPause = "Пауза";
            t.cancel();
        }
    }


    private void teleportEnemy(int i) {
        enemyBird[i].setX(viewWidth + Math.random() * 500);
        enemyBird[i].setY(Math.random() * (viewHeight - enemyBird[i].getFrameHeight()));
    }

    private void teleportBonus() {
        spriteBonus.setX(viewWidth + Math.random() * 500);
        spriteBonus.setY(Math.random() * (viewHeight - spriteBonus.getFrameHeight()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int eventAction = event.getAction();
        if (eventAction == MotionEvent.ACTION_DOWN) { // Движение вверх
            if (event.getY() < playerBird.getBoundingBoxRect().top) {
                playerBird.setVy(-100);
                points--;
            } else if (event.getY() > (playerBird.getBoundingBoxRect().bottom)) {
                playerBird.setVy(100);
                points--;
            }

            for (int i = 0; i < QUANTITY_ENEMY_BIRDS; i++) {
                if (enemyBird[i].getBoundingBoxRect().bottom >= event.getY() &&
                        enemyBird[i].getBoundingBoxRect().top <= event.getY() &&
                        enemyBird[i].getBoundingBoxRect().left <= event.getX() &&
                        enemyBird[i].getBoundingBoxRect().right >= event.getX()) {

                    teleportEnemy(i);
                }
            }
        }
        if (eventAction == MotionEvent.ACTION_DOWN) {
            if (mPaused == true) {
                t.start();
                mPaused = false;
                strPause = "";
            }
        }

        if (eventAction == MotionEvent.ACTION_DOWN && mPaused == false) {
            if (pauseBtn.getBoundingBoxRect().bottom >= event.getY() &&
                    pauseBtn.getBoundingBoxRect().top <= event.getY() &&
                    pauseBtn.getBoundingBoxRect().left <= event.getX() &&
                    pauseBtn.getBoundingBoxRect().right >= event.getX()) {
                mPaused = true;
            }
        }

        return true;

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawARGB(250, 127, 199, 255); // заливаем цветом
        Paint p = new Paint();
        p.setAntiAlias(true);
        p.setTextSize(55.0f);
        p.setColor(Color.WHITE);

        canvas.drawText("level " + level + "", viewWidth - 250, 70, p); //уровень игры
        canvas.drawText("points " + points + "", viewWidth - 250, 150, p);

        canvas.drawText(strPause, viewWidth / 2 - 50, viewHeight / 2 - 50, p);


        playerBird.draw(canvas);

        for (int i = 0; i < QUANTITY_ENEMY_BIRDS; i++) {
            enemyBird[i].draw(canvas);
        }
        spriteBonus.draw(canvas);

        pauseBtn.draw(canvas);


    }


    class Timer extends CountDownTimer {
        public Timer() {
            super(Integer.MAX_VALUE, timerInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            update();
        }

        @Override
        public void onFinish() {

        }
    }

    class PauseBtn {
        Bitmap bitmap;
        Rect frame;
        double x;
        double y;
        int frameWidth;
        int frameHeight;
        int padding;
        Canvas canvas;

        public PauseBtn(double x, double y, Rect initialFrame, Bitmap bitmap) {
            this.x = x;
            this.y = y;
            this.bitmap = bitmap;
            this.frame = initialFrame;
            this.bitmap = bitmap;
            this.frameWidth = initialFrame.width();
            this.frameHeight = initialFrame.height();
            this.padding = 5;

        }

        public Rect getBoundingBoxRect() {
            return new Rect((int) x + padding, (int) y + padding, (int) (x + frameWidth - 2 * padding), (int) (y + frameHeight - 2 * padding));
        }

        public void draw(Canvas canvas) {
            Paint p = new Paint();
            Rect destination = new Rect((int) x, (int) y, (int) (x + frameWidth), (int) (y + frameHeight));
            canvas.drawBitmap(bitmap, frame, destination, p);
            this.canvas = canvas;

        }
    }

}