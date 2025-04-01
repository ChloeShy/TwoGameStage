package com.example.mygamestage;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.DragEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;

public class PlantTreeActivity extends AppCompatActivity {
    private ImageView exeggutor, wateringCan, axe, fire;
    private Button btnReset;
    private ImageButton btnMusicToggle;
    private int waterCount = 0;
    private MediaPlayer backgroundMusic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plant_tree);

        // Initialize background music (same as before)
        backgroundMusic = MainActivity.backgroundMusic;
        if (backgroundMusic == null) {
            backgroundMusic = MediaPlayer.create(this, R.raw.background_music);
            backgroundMusic.setLooping(true);
            backgroundMusic.setVolume(0.5f, 0.5f);
        }

        // Initialize views
        exeggutor = findViewById(R.id.exeggutor);
        wateringCan = findViewById(R.id.watering);
        axe = findViewById(R.id.cutting);
        fire = findViewById(R.id.firing);
        btnReset = findViewById(R.id.btnReset);
        btnMusicToggle = findViewById(R.id.btnMusicToggle);

        // Start with Exeggcute image
        exeggutor.setImageResource(R.drawable.exeggcute);
        exeggutor.setVisibility(View.VISIBLE);

        // Initially disable all interactive elements
        setDraggableItemsEnabled(false);
        btnReset.setEnabled(false);

        // Play evolution animation
        playEntryEvolutionAnimation();

        // 设置拖拽监听器
        setupDragListeners();

        // 重置按钮
        btnReset.setOnClickListener(v -> {
            resetStage();
        });
    }

    private void playEntryEvolutionAnimation() {
        // First stage: Exeggcute pulse animation
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.pulse);
        pulseAnimation.setDuration(1000);

        // Second stage: Transform to Exeggutor with grow animation
        ScaleAnimation growAnimation = new ScaleAnimation(
                1.0f, 1.2f,
                1.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        growAnimation.setDuration(1500);
        growAnimation.setStartOffset(1000);

        // Third stage: Return to normal size
        ScaleAnimation normalizeAnimation = new ScaleAnimation(
                1.2f, 1.0f,
                1.2f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        normalizeAnimation.setDuration(1000);
        normalizeAnimation.setStartOffset(2500);

        AnimationSet animationSet = new AnimationSet(false);
        animationSet.addAnimation(pulseAnimation);
        animationSet.addAnimation(growAnimation);
        animationSet.addAnimation(normalizeAnimation);

        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                Toast.makeText(PlantTreeActivity.this,
                        "Exeggcute is evolving!",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}

            @Override
            public void onAnimationEnd(Animation animation) {
                // Change to Exeggutor image when growth animation starts
                new Handler().postDelayed(() -> {
                    exeggutor.setImageResource(R.drawable.exeggutor);
                    Toast.makeText(PlantTreeActivity.this,
                            "Exeggcute evolved into Exeggutor!",
                            Toast.LENGTH_SHORT).show();

                    // Enable all interactive elements
                    setDraggableItemsEnabled(true);
                    btnReset.setEnabled(true);

                    // Setup drag listeners now that animation is complete
                    setupDragListeners();
                }, 1000); // Delay matches growAnimation start offset
            }
        });

        exeggutor.startAnimation(animationSet);
    }

    private void setupDragListeners() {
        // Only set up listeners if animation is complete
        if (!wateringCan.isEnabled()) return;
        // 浇水壶拖拽
        wateringCan.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDragAndDrop(null, shadowBuilder, v, 0);
                    return true;
                }
                return false;
            }
        });

        // 斧头拖拽
        axe.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDragAndDrop(null, shadowBuilder, v, 0);
                    return true;
                }
                return false;
            }
        });

        // 火焰拖拽
        fire.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    View.DragShadowBuilder shadowBuilder = new View.DragShadowBuilder(v);
                    v.startDragAndDrop(null, shadowBuilder, v, 0);
                    return true;
                }
                return false;
            }
        });

        // Exeggutor接收拖拽
        exeggutor.setOnDragListener(new View.OnDragListener() {
            @Override
            public boolean onDrag(View v, DragEvent event) {
                switch (event.getAction()) {
                    case DragEvent.ACTION_DROP:
                        View draggedView = (View) event.getLocalState();

                        // 修改 ACTION_DROP 中的浇水处理部分
                        if (draggedView.getId() == R.id.watering) {
                            // 浇水处理
                            waterCount++;
                            if (waterCount >= 10) {
                                exeggutor.setImageResource(R.drawable.alolan_exeggutor);
                                Toast.makeText(PlantTreeActivity.this,
                                        "Congratulations! Exeggutor becomes Alolan Exeggutor!",
                                        Toast.LENGTH_LONG).show();

                                // 添加进化动画
                                animateEvolution();
                            } else {
                                Toast.makeText(PlantTreeActivity.this,
                                        "Watered Exeggutor (" + waterCount + "/10)",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                        else if (draggedView.getId() == R.id.cutting) {
                            // 斧头处理
                            exeggutor.setImageResource(R.drawable.exeggcute);
                            Toast.makeText(PlantTreeActivity.this,
                                    "Exeggutor was cut down and devolved to Exeggcute!",
                                    Toast.LENGTH_SHORT).show();
                            waterCount = 0;
                        }
                        else if (draggedView.getId() == R.id.firing) {
                            // 火焰处理
                            exeggutor.setImageResource(R.drawable.exeggcute);
                            Toast.makeText(PlantTreeActivity.this,
                                    "Exeggutor was burned and devolved to Exeggcute!",
                                    Toast.LENGTH_SHORT).show();
                            waterCount = 0;
                        }
                        return true;
                    default:
                        return true;
                }
            }
        });
    }

    // 添加新的动画方法
    private void animateEvolution() {
        // 初始缩放动画 (变大变高)
        ScaleAnimation growAnimation = new ScaleAnimation(
                1.0f, 1.2f, // X轴从1倍到1.2倍
                1.0f, 1.5f, // Y轴从1倍到1.5倍
                Animation.RELATIVE_TO_SELF, 0.5f, // 缩放中心X
                Animation.RELATIVE_TO_SELF, 1.0f); // 缩放中心Y(底部)

        growAnimation.setDuration(2500); // 2.5秒
        growAnimation.setFillAfter(true); // 保持动画结束状态

        // 恢复原状的动画
        ScaleAnimation shrinkAnimation = new ScaleAnimation(
                1.2f, 1.0f, // X轴从1.2倍回到1倍
                1.5f, 1.0f, // Y轴从1.5倍回到1倍
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 1.0f);

        shrinkAnimation.setDuration(2500); // 2.5秒
        shrinkAnimation.setStartOffset(2500); // 在grow动画结束后开始

        // 组合动画
        AnimationSet animationSet = new AnimationSet(true);
        animationSet.addAnimation(growAnimation);
        animationSet.addAnimation(shrinkAnimation);

        // 设置动画监听器
        animationSet.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                // 动画开始时可以禁用交互
                setDraggableItemsEnabled(false);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                // 动画结束后恢复交互
                setDraggableItemsEnabled(true);
                // 显示感谢消息
                Toast.makeText(PlantTreeActivity.this,
                        "Alolan Exeggutor: Thank you!",
                        Toast.LENGTH_LONG).show();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });

        exeggutor.startAnimation(animationSet);
    }

    // 添加控制拖拽项可用状态的方法
    private void setDraggableItemsEnabled(boolean enabled) {
        wateringCan.setEnabled(enabled);
        axe.setEnabled(enabled);
        fire.setEnabled(enabled);
        btnReset.setEnabled(enabled);
    }

    private void resetStage() {
        // Reset Exeggutor to its original form
        exeggutor.setImageResource(R.drawable.exeggutor);
        waterCount = 0;

        // Clear any ongoing animations
        exeggutor.clearAnimation();

        // Reset the scale and position in case they were modified
        exeggutor.setScaleX(1.0f);
        exeggutor.setScaleY(1.0f);
        exeggutor.setTranslationX(0);
        exeggutor.setTranslationY(0);

        // Re-enable all interactive elements
        setDraggableItemsEnabled(true);
        btnReset.setEnabled(true);

        // Play reset animation
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        exeggutor.startAnimation(shake);

        Toast.makeText(this, "Exeggutor is restored!", Toast.LENGTH_SHORT).show();
    }

    private void updateMusicButtonIcon() {
        if (backgroundMusic.isPlaying()) {
            btnMusicToggle.setImageResource(R.drawable.ic_volume_on);
        } else {
            btnMusicToggle.setImageResource(R.drawable.ic_volume_off);
        }
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
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            backgroundMusic.release();
        }
    }
}