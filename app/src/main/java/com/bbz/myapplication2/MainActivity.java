package com.bbz.myapplication2;

import android.content.Intent;
import android.view.MotionEvent;
import android.widget.LinearLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import java.util.Arrays;
import java.util.Random;

public class MainActivity extends AppCompatActivity {

    private final TextView[] textMap = new TextView[16];
    private TextView currentScoreText, maxScoreText;
    private final int[] gameMap = new int[16];
    private float DENSITY;
    private int maxScore = 0, currentScore = 0;
    private float preX = 0, preY = 0, nowX = 0, nowY = 0;
    private final Random random = new Random();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //屏幕密度
        DENSITY = getResources().getDisplayMetrics().density;

        currentScoreText = findViewById(R.id.current_score);
        maxScoreText = findViewById(R.id.max_score);

        //将TextView填入到LinearLayout中
        GameBoard game_board = findViewById(R.id.game_board);
        for (int i = 0; i < game_board.getChildCount(); ++i) {
            LinearLayout column = (LinearLayout) game_board.getChildAt(i);
            for (int j = 0; j < 4; ++j) {
                //从xml实例化一个TextView
                TextView text = (TextView) getLayoutInflater().inflate(R.layout.block_2048, null);
                textMap[i * 4 + j] = text;
                //设置宽高
                text.setHeight(changeDpToPixel(80));
                text.setWidth(changeDpToPixel(80));
                //添加到LinearLayout中
                column.addView(text);
                //设置Margin
                if (j != 3) {
                    LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) text.getLayoutParams();
                    params.setMarginEnd(changeDpToPixel(10));
                    text.setLayoutParams(params);
                }
            }
        }
        //手势
        game_board.setOnTouchListener((v, event) -> {
            v.performClick();
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    preX = event.getX();
                    preY = event.getY();
                    break;
                case MotionEvent.ACTION_UP:
                    nowX = event.getX();
                    nowY = event.getY();
                    float dx = Math.abs(nowX - preX);
                    float dy = Math.abs(nowY - preY);
                    if (nowY - preY > 0 && dy / dx > 1.7 && dy > 100) {//下
                        perform(3);
                    } else if (nowY - preY < 0 && dy / dx > 1.7 && dy > 100) {//上
                        perform(1);
                    } else if (nowX - preX < 0 && dx / dy > 1.7 && dx > 100) {//左
                        perform(0);
                    } else if (nowX - preX > 0 && dx / dy > 1.7 && dx > 100) {//右
                        perform(2);
                    }
                    break;
            }
            return true;
        });
        //退出按钮
        findViewById(R.id.button_finish).setOnClickListener(v -> {
            finish();
        });
        //重置按钮
        findViewById(R.id.button_reset).setOnClickListener(v -> {
            reset();
        });
        //初始化
        reset();
    }

    /**
     * 刷新面板
     */
    private void refresh() {
        for (int i = 0; i < 16; ++i) {
            textMap[i].setText(gameMap[i] == 0 ? "" : "" + gameMap[i]);
        }
        currentScoreText.setText("" + currentScore);
        maxScoreText.setText("" + maxScore);
    }

    /**
     * 重置
     */
    private void reset() {
        currentScore = 0;
        Arrays.fill(gameMap, 0);
        int preDex = (random.nextInt() % 16 + 16) % 16;
        int otherDex = (random.nextInt() % 16 + 16) % 16;
        while (preDex == otherDex) {
            otherDex = (random.nextInt() % 16 + 16) % 16;
        }
        gameMap[preDex] = 2;
        gameMap[otherDex] = 2;
        refresh();
    }

    /**
     * 移动
     * @param t 0-左,1-上,2-右,3-下
     */
    private void perform(int t) {
        int[] dx = new int[]{1, 0, -1, 0};
        int[] dy = new int[]{0, 1, 0, -1};
        int[] bx = new int[]{0, 0, 3, 0};
        int[] by = new int[]{0, 0, 0, 3};
        int[] dbx = new int[]{0, 1, 0, 1};
        int[] dby = new int[]{1, 0, 1, 0};
        boolean move = false;
        //第a列第b行
        for (int a = bx[t], b = by[t], i = 0; i < 4;a += dbx[t], b += dby[t], ++i) {
            //第j个元素
            for (int j = 0; j < 4; ++j) {
                int pre = (b + j * dy[t]) * 4 + a + j * dx[t];
                if (gameMap[pre] != 0) {
                    for (int k = j + 1; k < 4; ++k) {
                        int next = (b + k * dy[t]) * 4 + a + k * dx[t];
                        if (gameMap[pre] == gameMap[next]) {
                            gameMap[pre] *= 2;
                            gameMap[next] = 0;
                            currentScore += gameMap[pre];
                            maxScore = Math.max(maxScore, currentScore);
                            move = true;
                        }
                    }
                    for (int k = 0; k < j; ++k) {
                        int next = (b + k * dy[t]) * 4 + a + k * dx[t];
                        if (gameMap[next] == 0) {
                            gameMap[next] = gameMap[pre];
                            gameMap[pre] = 0;
                            move = true;
                        }
                    }
                }
            }
        }
        if (!move && check()) {
            lose();
        } else {
            addNewBlock();
            refresh();
        }
    }

    /**
     * 失败后跳转到另一个Activity
     * 注意新的Activity要在AndroidManifest里面注册
     */
    private void lose() {
        Intent intent = new Intent();
        intent.setClass(this, LoseActivity.class);
        startActivity(intent);
        refresh();
    }

    /**
     * 移动后添加一个新的2
     */
    private void addNewBlock() {
        int dex = (random.nextInt() % 16 + 16) % 16;
        while (gameMap[dex] != 0) {
            dex = (random.nextInt() % 16 + 16) % 16;
        }
        gameMap[dex] = 2;
    }

    /**
     * 检查是否无路可走
     * @return 若无路可走返回true
     */
    private boolean check() {
        for (int i = 0; i < 16; ++i) {
            if (gameMap[i] == 0) {
                return false;
            }
            if (i % 4 != 0) {
                if (gameMap[i - 1] == gameMap[i]) {
                    return false;
                }
            }
            if (i % 4 != 3) {
                if (gameMap[i + 1] == gameMap[i]) {
                    return false;
                }
            }
            if (i > 3) {
                if (gameMap[i - 4] == gameMap[i]) {
                    return false;
                }
            }
            if (i < 12) {
                if (gameMap[i + 4] == gameMap[i]) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 将以dp为单位的数值转化为以像素为单位
     * @param dp 多少dp
     * @return 转化成多少像素
     */
    private int changeDpToPixel(int dp) {
        return (int) (dp * DENSITY);
    }
}