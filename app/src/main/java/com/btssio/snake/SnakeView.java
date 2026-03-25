package com.btssio.snake;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.Random;

public class SnakeView extends View {

    private static final int COLS     = 20;
    private static final int ROWS     = 20;
    private static final int SPEED_MS = 150;

    public static final int DIR_UP    = 0;
    public static final int DIR_DOWN  = 1;
    public static final int DIR_LEFT  = 2;
    public static final int DIR_RIGHT = 3;

    // Taille d'une cellule calculée dynamiquement selon la taille réelle de la vue
    private int cellSize = 40;

    private ArrayList<int[]> snake = new ArrayList<>();
    private int[] apple = new int[2];
    private int direction = DIR_RIGHT;
    private int nextDir   = DIR_RIGHT;
    private int score     = 0;
    private boolean running  = false;
    private boolean gameOver = false;

    private float touchStartX, touchStartY;

    private final Paint paintBg    = new Paint();
    private final Paint paintGrid  = new Paint();
    private final Paint paintSnake = new Paint();
    private final Paint paintHead  = new Paint();
    private final Paint paintApple = new Paint();
    private final Paint paintText  = new Paint();

    private final Handler handler = new Handler(Looper.getMainLooper());
    private Runnable loop; // assigné dans init() pour éviter self-reference

    public interface GameListener {
        void onScoreChanged(int score);
        void onGameOver(int score);
    }
    private GameListener listener;
    public void setGameListener(GameListener l) { this.listener = l; }

    public SnakeView(Context context) { super(context); init(); }
    public SnakeView(Context context, AttributeSet attrs) { super(context, attrs); init(); }

    private void init() {
        loop = () -> {
            if (running) {
                update();
                invalidate();
                handler.postDelayed(loop, SPEED_MS);
            }
        };

        paintBg.setColor(Color.parseColor("#0d0d0d"));
        paintGrid.setColor(Color.parseColor("#222222"));
        paintGrid.setStyle(Paint.Style.STROKE);
        paintGrid.setStrokeWidth(0.5f);
        paintSnake.setColor(Color.parseColor("#00ff88"));
        paintSnake.setAntiAlias(true);
        paintHead.setColor(Color.WHITE);
        paintHead.setAntiAlias(true);
        paintApple.setColor(Color.parseColor("#ff4466"));
        paintApple.setAntiAlias(true);
        paintText.setColor(Color.parseColor("#00ff88"));
        paintText.setTextAlign(Paint.Align.CENTER);
        paintText.setAntiAlias(true);
        paintText.setFakeBoldText(true);
    }

    // Appelé par Android quand la vue connaît sa taille réelle à l'écran
    @Override
    protected void onSizeChanged(int w, int h, int oldW, int oldH) {
        super.onSizeChanged(w, h, oldW, oldH);
        // On calcule la taille d'une cellule pour remplir exactement la vue
        cellSize = Math.min(w / COLS, h / ROWS);
        paintText.setTextSize(cellSize * 2f);
    }

    public void startGame() {
        snake.clear();
        snake.add(new int[]{10, 10});
        snake.add(new int[]{ 9, 10});
        snake.add(new int[]{ 8, 10});
        direction = DIR_RIGHT;
        nextDir   = DIR_RIGHT;
        score     = 0;
        gameOver  = false;
        running   = true;
        placeApple();
        handler.removeCallbacks(loop);
        handler.postDelayed(loop, SPEED_MS);
        invalidate();
    }

    public void stopGame() {
        running = false;
        handler.removeCallbacks(loop);
    }

    private void placeApple() {
        Random rnd = new Random();
        int col, row;
        do {
            col = rnd.nextInt(COLS);
            row = rnd.nextInt(ROWS);
        } while (isOnSnake(col, row));
        apple[0] = col;
        apple[1] = row;
    }

    private boolean isOnSnake(int col, int row) {
        for (int[] seg : snake)
            if (seg[0] == col && seg[1] == row) return true;
        return false;
    }

    private void update() {
        direction = nextDir;
        int[] head = snake.get(0);
        int col = head[0];
        int row = head[1];

        switch (direction) {
            case DIR_UP:    row--; break;
            case DIR_DOWN:  row++; break;
            case DIR_LEFT:  col--; break;
            case DIR_RIGHT: col++; break;
        }

        if (col < 0 || col >= COLS || row < 0 || row >= ROWS) { endGame(); return; }

        for (int i = 0; i < snake.size() - 1; i++) {
            if (snake.get(i)[0] == col && snake.get(i)[1] == row) { endGame(); return; }
        }

        snake.add(0, new int[]{col, row});

        if (col == apple[0] && row == apple[1]) {
            score++;
            if (listener != null) listener.onScoreChanged(score);
            placeApple();
        } else {
            snake.remove(snake.size() - 1);
        }
    }

    private void endGame() {
        running  = false;
        gameOver = true;
        handler.removeCallbacks(loop);
        if (listener != null) listener.onGameOver(score);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawRect(0, 0, getWidth(), getHeight(), paintBg);

        // Grille
        for (int c = 0; c <= COLS; c++)
            canvas.drawLine(c * cellSize, 0, c * cellSize, ROWS * cellSize, paintGrid);
        for (int r = 0; r <= ROWS; r++)
            canvas.drawLine(0, r * cellSize, COLS * cellSize, r * cellSize, paintGrid);

        if (!gameOver && !snake.isEmpty()) {
            drawCell(canvas, apple[0], apple[1], paintApple);
            for (int i = snake.size() - 1; i >= 0; i--)
                drawCell(canvas, snake.get(i)[0], snake.get(i)[1], i == 0 ? paintHead : paintSnake);
        }

        // Overlay start / game over
        if (!running) {
            Paint overlay = new Paint();
            overlay.setColor(Color.argb(190, 13, 13, 13));
            canvas.drawRect(0, 0, getWidth(), getHeight(), overlay);

            float cx = getWidth() / 2f;
            float cy = getHeight() / 2f;

            if (gameOver) {
                canvas.drawText("GAME OVER", cx, cy - cellSize, paintText);
                paintText.setTextSize(cellSize * 1.2f);
                canvas.drawText("Score : " + score, cx, cy + cellSize / 2f, paintText);
                paintText.setTextSize(cellSize * 2f);
            } else {
                canvas.drawText("SNAKE", cx, cy, paintText);
            }
        }
    }

    private void drawCell(Canvas canvas, int col, int row, Paint paint) {
        float l = col * cellSize + 1f;
        float t = row * cellSize + 1f;
        float s = cellSize - 2f;
        canvas.drawRoundRect(new RectF(l, t, l + s, t + s), 5, 5, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (e.getAction() == MotionEvent.ACTION_DOWN) {
            touchStartX = e.getX(); touchStartY = e.getY(); return true;
        }
        if (e.getAction() == MotionEvent.ACTION_UP) {
            float dx = e.getX() - touchStartX;
            float dy = e.getY() - touchStartY;
            if (Math.abs(dx) > Math.abs(dy)) {
                if (dx > 30)  changeDirection(DIR_RIGHT);
                if (dx < -30) changeDirection(DIR_LEFT);
            } else {
                if (dy > 30)  changeDirection(DIR_DOWN);
                if (dy < -30) changeDirection(DIR_UP);
            }
            return true;
        }
        return super.onTouchEvent(e);
    }

    public void changeDirection(int d) {
        if (d == DIR_UP    && direction != DIR_DOWN)  nextDir = d;
        if (d == DIR_DOWN  && direction != DIR_UP)    nextDir = d;
        if (d == DIR_LEFT  && direction != DIR_RIGHT) nextDir = d;
        if (d == DIR_RIGHT && direction != DIR_LEFT)  nextDir = d;
    }

    public boolean isRunning()  { return running; }
    public boolean isGameOver() { return gameOver; }
}
