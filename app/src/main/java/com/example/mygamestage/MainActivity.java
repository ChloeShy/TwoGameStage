package com.example.mygamestage;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;


public class MainActivity extends AppCompatActivity {
    public static MediaPlayer backgroundMusic;
    private TextView question;
    private TextView numberTextView;
    private Button btnIncrease, btnDeduct, btnConfirm, btnNextLevel;
    private ImageView redCross, exeggcute, eggBasket, threeEggs, anEgg, brokenEgg;
    private int currentNumber = 0;
    private Vibrator vibrator;
    private Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 初始化背景音乐
        backgroundMusic = MediaPlayer.create(this, R.raw.background_music);
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.5f, 0.5f);

        // 初始化视图
        numberTextView = findViewById(R.id.number);
        btnIncrease = findViewById(R.id.increase);
        btnDeduct = findViewById(R.id.deduct);
        btnConfirm = findViewById(R.id.btnConfirm);
        redCross = findViewById(R.id.redCross);
        exeggcute = findViewById(R.id.exeggcute);
        eggBasket = findViewById(R.id.eggBasket);
        threeEggs = findViewById(R.id.threeEggs);
        anEgg = findViewById(R.id.anEgg);
        brokenEgg = findViewById(R.id.imageView4);
        question = findViewById(R.id.question);

        // 初始化震动服务
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);

        // +1 按钮
        btnIncrease.setOnClickListener(v -> {
            currentNumber++;
            updateNumberText();
        });

        // -1 按钮
        btnDeduct.setOnClickListener(v -> {
            if (currentNumber > 0) {
                currentNumber--;
                updateNumberText();
            }
        });

        // 确认按钮
        btnConfirm.setOnClickListener(v -> {
            if (currentNumber == 11) {
                correctAnswer();
            } else if (currentNumber >= 12 && currentNumber <= 17) {
                Toast.makeText(MainActivity.this, "Exeggcute is not egg.", Toast.LENGTH_SHORT).show();
                vibratePhone();
                showRedCrossTemporarily();
            } else {
                Toast.makeText(MainActivity.this, "Wrong! Try again.", Toast.LENGTH_SHORT).show();
                vibratePhone();
                showRedCrossTemporarily();
            }
        });

        // 音乐控制按钮
        ImageButton btnMusicToggle = findViewById(R.id.btnMusicToggle);
        btnMusicToggle.setOnClickListener(v -> {
            if (backgroundMusic.isPlaying()) {
                backgroundMusic.pause();
                btnMusicToggle.setImageResource(R.drawable.ic_volume_off);
            } else {
                backgroundMusic.start();
                btnMusicToggle.setImageResource(R.drawable.ic_volume_on);
            }
        });

        // 初始化下一关按钮
        btnNextLevel = findViewById(R.id.btnNextLevel);
        btnNextLevel.setVisibility(View.INVISIBLE); // 初始隐藏
        btnNextLevel.setOnClickListener(v -> {
            // 使用Intent跳转到第二关
            Intent intent = new Intent(MainActivity.this, PlantTreeActivity.class);
            startActivity(intent);
            finish(); // 结束当前Activity
        });
    }

    // 正确答案处理
    private void correctAnswer() {
        Toast.makeText(MainActivity.this, "Correct! You win!", Toast.LENGTH_SHORT).show();
        hideRedCross();

        // 隐藏所有鸡蛋和UI元素
        eggBasket.setVisibility(View.INVISIBLE);
        threeEggs.setVisibility(View.INVISIBLE);
        anEgg.setVisibility(View.INVISIBLE);
        brokenEgg.setVisibility(View.INVISIBLE);
        numberTextView.setVisibility(View.INVISIBLE);
        btnIncrease.setVisibility(View.INVISIBLE);
        btnDeduct.setVisibility(View.INVISIBLE);
        btnConfirm.setVisibility(View.INVISIBLE);
        question.setVisibility(View.INVISIBLE);

        // 显示Exeggcute并添加弹出动画
        exeggcute.setVisibility(View.VISIBLE);
        Animation popUp = AnimationUtils.loadAnimation(this, R.anim.pop_up);
        exeggcute.startAnimation(popUp);

        // 2秒后显示介绍文本并开始下降动画
        handler.postDelayed(() -> {
            Toast.makeText(MainActivity.this, "Exeggcute: Goodbye!", Toast.LENGTH_SHORT).show();
            Toast.makeText(MainActivity.this, "Exeggcute disappeared.", Toast.LENGTH_SHORT).show();

            // 开始下降动画
            Animation dropDown = AnimationUtils.loadAnimation(this, R.anim.drop_down);
            exeggcute.startAnimation(dropDown);

            // 下降动画结束后隐藏Exeggcute并显示下一关按钮
            dropDown.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {}

                @Override
                public void onAnimationEnd(Animation animation) {
                    exeggcute.setVisibility(View.INVISIBLE);
                    btnNextLevel.setVisibility(View.VISIBLE);

                    // 添加下一关按钮的淡入效果
                    Animation fadeIn = AnimationUtils.loadAnimation(
                            MainActivity.this, android.R.anim.fade_in);
                    btnNextLevel.startAnimation(fadeIn);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {}
            });
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (backgroundMusic != null && !backgroundMusic.isPlaying()) {
            backgroundMusic.start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (backgroundMusic != null && backgroundMusic.isPlaying()) {
            backgroundMusic.pause();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //if (backgroundMusic != null) {
        //    backgroundMusic.stop();
        //    backgroundMusic.release();
        //    backgroundMusic = null;
        //}
        handler.removeCallbacksAndMessages(null);
    }

    private void updateNumberText() {
        numberTextView.setText(String.valueOf(currentNumber));
    }

    private void vibratePhone() {
        if (vibrator != null && vibrator.hasVibrator()) {
            vibrator.vibrate(500);
        }
    }

    private void showRedCrossTemporarily() {
        redCross.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> hideRedCross(), 2000);
    }

    private void hideRedCross() {
        redCross.setVisibility(View.INVISIBLE);
    }
}