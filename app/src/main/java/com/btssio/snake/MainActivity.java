package com.btssio.snake;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements SnakeView.GameListener {

    private SnakeView snakeView;
    private TextView  tvScore, tvBest;
    private Button    btnStart;
    private Button    btnUp, btnDown, btnLeft, btnRight;

    private SharedPreferences prefs;
    private static final String KEY_BEST = "best_score";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        snakeView = findViewById(R.id.snakeView);
        tvScore   = findViewById(R.id.tvScore);
        tvBest    = findViewById(R.id.tvBest);
        btnStart  = findViewById(R.id.btnStart);
        btnUp     = findViewById(R.id.btnUp);
        btnDown   = findViewById(R.id.btnDown);
        btnLeft   = findViewById(R.id.btnLeft);
        btnRight  = findViewById(R.id.btnRight);

        snakeView.setGameListener(this);

        prefs = getSharedPreferences("SnakePrefs", MODE_PRIVATE);
        tvBest.setText("BEST: " + prefs.getInt(KEY_BEST, 0));

        btnStart.setOnClickListener(v -> {
            snakeView.startGame();
            btnStart.setVisibility(View.GONE);
        });

        btnUp.setOnClickListener(v    -> snakeView.changeDirection(SnakeView.DIR_UP));
        btnDown.setOnClickListener(v  -> snakeView.changeDirection(SnakeView.DIR_DOWN));
        btnLeft.setOnClickListener(v  -> snakeView.changeDirection(SnakeView.DIR_LEFT));
        btnRight.setOnClickListener(v -> snakeView.changeDirection(SnakeView.DIR_RIGHT));
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (snakeView.isRunning()) snakeView.stopGame();
    }

    @Override
    public void onScoreChanged(int score) {
        runOnUiThread(() -> tvScore.setText("SCORE: " + score));
    }

    @Override
    public void onGameOver(int score) {
        runOnUiThread(() -> {
            int best = prefs.getInt(KEY_BEST, 0);
            if (score > best) {
                best = score;
                prefs.edit().putInt(KEY_BEST, best).apply();
            }
            tvBest.setText("BEST: " + best);
            btnStart.setText("↺  REJOUER");
            btnStart.setVisibility(View.VISIBLE);
        });
    }
}
