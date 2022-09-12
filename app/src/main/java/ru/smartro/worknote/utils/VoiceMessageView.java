package ru.smartro.worknote.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatEditText;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatTextView;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import ru.smartro.worknote.App;
import ru.smartro.worknote.R;

enum VoiceMessageViewState {
    IDLE,
    RECORDING,
    LOCK,
    CANCEL,
}

enum MovementEnum {
    LEFT,
    RIGHT,
    TOP,
    DOWN,
    NONE
}

public class VoiceMessageView extends CoordinatorLayout {

    Vibrator vibrator;
    VibrationEffect vibrationEffect = VibrationEffect.createOneShot(100, 128);

    final private float DRAG_EDGE = -120f;

    private AppCompatEditText messageInput;
    private LinearLayoutCompat recordInfo;
    private AppCompatTextView recordTime;
    private AppCompatImageView recordButton;
    private AppCompatTextView swipeLeftHint;
    private String messageInputHint;

    private MovementEnum movement = MovementEnum.NONE;

    float x1, x2, y1, y2, dx, dy;

    float initX, initY;

    private VoiceMessageViewState currentState = VoiceMessageViewState.IDLE;

    private int recordButtonInitSize = 0;
    private int diffPos = 0;

    public VoiceMessageView(Context context) {
        this(context,null);
    }

    public VoiceMessageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public VoiceMessageView(Context context, AttributeSet attrs, int defStyleAttrs) {
        super(context, attrs, defStyleAttrs);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        inflate(getContext(), R.layout.f_pserve__voice_message_view, this);
        vibrator = (Vibrator) getContext().getApplicationContext().getSystemService(Context.VIBRATOR_SERVICE);
        messageInput = findViewById(R.id.acet__f_pserve__voice_message_view__message_input);
        recordInfo = findViewById(R.id.llc__f_pserve__voice_message_view__record_info);
        recordTime = findViewById(R.id.actv__f_pserve__voice_message_view__record_time);
        recordButton = findViewById(R.id.aciv__f_pserve__voice_message_view__record_button);
        swipeLeftHint = findViewById(R.id.actv__f_pserve__voice_message_view__swipe_left_hint);

        initX = recordButton.getTranslationX();
        initY = recordButton.getTranslationY();

        messageInputHint = messageInput.getHint().toString();

        recordInfo.setVisibility(View.GONE);
        swipeLeftHint.setVisibility(View.GONE);

        recordButton.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(recordButtonInitSize == 0) {
                    recordButtonInitSize = recordButton.getLayoutParams().width;
                }
                recordButton.animate().scaleX(2.5f).scaleY(2.5f).setDuration(200).start();
                vibrator.vibrate(vibrationEffect);
                recordInfo.setVisibility(View.VISIBLE);
                messageInput.setVisibility(View.INVISIBLE);
                swipeLeftHint.setVisibility(View.VISIBLE);
                currentState = VoiceMessageViewState.RECORDING;
                return false;
            }
        });

        recordButton.setOnTouchListener((v, event) -> {

            switch(event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    y1 = event.getY();
                    Log.d("test :::", "INIT: x1: " + x1 + "; y1: " + y1);
                    return false;

                case MotionEvent.ACTION_UP:
                    recordButton.animate().scaleX(1f).scaleY(1f).translationX(initX).translationY(initY).setDuration(200).start();
                    recordInfo.setVisibility(View.GONE);
                    swipeLeftHint.setVisibility(View.GONE);
                    messageInput.setVisibility(View.VISIBLE);
                    movement = MovementEnum.NONE;
                    return false;

                case MotionEvent.ACTION_MOVE:
                    if(currentState == VoiceMessageViewState.RECORDING) {
                        x2 = event.getX();
                        y2 = event.getY();
                        dx = x2-x1;
                        dy = y2-y1;

                        if(movement == MovementEnum.NONE) {
                            if(Math.abs(dx) > Math.abs(dy) && dx < -25f) {
                                Log.d("TEST :::", "SET MOVEMENT: LEFT");
                                movement = MovementEnum.LEFT;
                                return false;
                            }
                            if(Math.abs(dx) < Math.abs(dy) && dy < -25f) {
                                Log.d("TEST :::", "SET MOVEMENT: TOP");
                                movement = MovementEnum.TOP;
                                return false;
                            }
                        }

                        if(movement == MovementEnum.LEFT) {

                            Log.d("TEST :::", "LEFT: x1: " + x1 + ", x2: " + x2 + ", y1: " + y1 + ", y2: " + y2 + ", dx: " + dx + ", dy: " + dy);
                            recordButton.setTranslationX(x2 + dx);
                            recordButton.setTranslationY(0);

                            if(swipeLeftHint.getVisibility() == View.GONE) {
                                swipeLeftHint.setVisibility(View.VISIBLE);
                            }

                            if(dx < DRAG_EDGE) {
                                vibrator.vibrate(vibrationEffect);
                                currentState = VoiceMessageViewState.CANCEL;
                                MotionEvent cancelEvent = MotionEvent.obtain(event);
                                cancelEvent.setAction(MotionEvent.ACTION_UP);
                                recordButton.dispatchTouchEvent(cancelEvent);
                                Toast.makeText(getContext(), "SWIPED LEFT", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            if(dx > -40f) {
                                Log.d("TEST :::", "SET MOVEMENT: NONE 1");
                                movement = MovementEnum.NONE;
                                return false;
                            }

                            return false;
                        }

                        if(movement == MovementEnum.TOP) {

                            Log.d("TEST :::", "TOP: x1: " + x1 + ", x2: " + x2 + ", y1: " + y1 + ", y2: " + y2 + ", dx: " + dx + ", dy: " + dy);
                            recordButton.setTranslationY(y2 + dy);
                            recordButton.setTranslationX(0);

                            if(swipeLeftHint.getVisibility() == View.VISIBLE) {
                                swipeLeftHint.setVisibility(View.GONE);
                            }

                            if(dy < DRAG_EDGE) {
                                vibrator.vibrate(vibrationEffect);
                                currentState = VoiceMessageViewState.LOCK;
                                MotionEvent cancelEvent = MotionEvent.obtain(event);
                                cancelEvent.setAction(MotionEvent.ACTION_UP);
                                recordButton.dispatchTouchEvent(cancelEvent);
                                Toast.makeText(getContext(), "SWIPED TOP", Toast.LENGTH_SHORT).show();
                                return false;
                            }

                            if(dy > -40f) {
                                Log.d("TEST :::", "SET MOVEMENT: NONE 2");
                                movement = MovementEnum.NONE;
                                swipeLeftHint.setVisibility(View.VISIBLE);
                                return false;
                            }

                            return false;
                        }
                    }
                    return false;
                }
            return false;
        });


    }

}
