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

public class VoiceMessageView extends CoordinatorLayout {


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

    Vibrator vibrator;
    VibrationEffect vibrationEffect = VibrationEffect.createOneShot(100, 128);

    final private float DRAG_EDGE_LEFT = -150f;
    final private float DRAG_EDGE_TOP = -120f;

    private AppCompatEditText messageInput;
    private LinearLayoutCompat recordInfo;
    private AppCompatTextView recordTime;
    private AppCompatImageView recordButton;
    private AppCompatImageView lockButton;
    private AppCompatTextView swipeLeftHint;
    private String messageInputHint;

    private MovementEnum movement = MovementEnum.NONE;
    private VoiceMessageViewState currentState = VoiceMessageViewState.IDLE;

    private float recordButtonX1, recordButtonY1, recordButtonX2, recordButtonY2;

    private float x1, y1, x2, y2, dx, dy;

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
        lockButton = findViewById(R.id.aciv__f_pserve__voice_message_view__lock_button);
        recordButton = findViewById(R.id.aciv__f_pserve__voice_message_view__record_button);
        swipeLeftHint = findViewById(R.id.actv__f_pserve__voice_message_view__swipe_left_hint);

        messageInputHint = messageInput.getHint().toString();

        recordInfo.setVisibility(View.GONE);
        swipeLeftHint.setVisibility(View.INVISIBLE);
        lockButton.setVisibility(View.INVISIBLE);

        recordButton.setOnLongClickListener(v -> {
            int[] location  = new int[2];
            recordButton.getLocationOnScreen(location);
            recordButtonX1 = location[0];
            recordButtonX2 = recordButtonX1 + recordButton.getWidth();
            recordButtonY1 = location[1];
            recordButtonY2 = recordButtonY1 + recordButton.getHeight();

            swipeLeftHint.setTranslationX(0);
            recordButton.setTranslationX(0);
            swipeLeftHint.setTranslationY(0);
            recordButton.setTranslationY(0);

            recordButton.animate().scaleX(1.8f).scaleY(1.8f).setDuration(200).start();
            vibrator.vibrate(vibrationEffect);

            recordInfo.setVisibility(View.VISIBLE);
            messageInput.setVisibility(View.INVISIBLE);
            swipeLeftHint.setVisibility(View.VISIBLE);
            swipeLeftHint.setVisibility(View.VISIBLE);
            lockButton.setVisibility(View.VISIBLE);

            currentState = VoiceMessageViewState.RECORDING;
            return false;
        });

        recordButton.setOnTouchListener((v, event) -> {

            switch(event.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    x1 = event.getX();
                    y1 = event.getY();
                    Log.d("test :::", "INIT: x1: " + x1 + "; y1: " + y1);
                    return false;

                case MotionEvent.ACTION_UP:
                    recordButton.animate().scaleX(1f).scaleY(1f).translationX(0).translationY(0).setDuration(200).start();
                    recordInfo.setVisibility(View.GONE);
                    swipeLeftHint.setVisibility(View.INVISIBLE);
                    messageInput.setVisibility(View.VISIBLE);
                    lockButton.setVisibility(View.INVISIBLE);
                    movement = MovementEnum.NONE;
                    return false;

                case MotionEvent.ACTION_MOVE:
                    if(currentState == VoiceMessageViewState.RECORDING) {
                        x2 = event.getX();
                        y2 = event.getY();
                        dx = x2 - x1;
                        dy = y2 - y1;

                        if(dx > -30f && dy > -30f) {
                            return false;
                        }

                        float rawX = event.getRawX();
                        float rawY = event.getRawY();

                        Log.d("test :::", "event X: " + x2);
                        Log.d("test :::", "event Y: " + y2);
                        Log.d("test :::", "event raw X: " + rawX);
                        Log.d("test :::", "event raw Y: " + rawY);
                        Log.d("test :::", "recordButton X: " + recordButton.getX());
                        Log.d("test :::", "recordButton Y: " + recordButton.getY());

                        // left
                        if(rawY > recordButtonY1 && rawY < recordButtonY2 && rawX < recordButtonX1) {
                            Log.d("TEST :::", "left");
                            recordButton.setTranslationX(dx);
                            recordButton.setTranslationY(0);
                        }
                        // top
                        else if (rawX > recordButtonX1 && rawX < recordButtonX2 && rawY < recordButtonY1) {
                            Log.d("TEST :::", "top");
                            recordButton.setTranslationY(dy);
                            recordButton.setTranslationX(0);
                        }

//                        if(movement == MovementEnum.LEFT) {
//                            lockButton.setVisibility(INVISIBLE);
//
//                            if(dx > -30f) {
//                                Log.d("TEST :::", "SET MOVEMENT: NONE (FROM LEFT)");
//                                movement = MovementEnum.NONE;
//                                lockButton.setVisibility(VISIBLE);
//                                recordButton.animate().translationX(0).start();
//                                return false;
//                                // TODO
//                            }
//
//                            if(dx < DRAG_EDGE_LEFT) {
//                                Log.d("TEST :::", "SWIPED LEFT");
//                                vibrator.vibrate(vibrationEffect);
//                                currentState = VoiceMessageViewState.LOCK;
//                                MotionEvent cancelEvent = MotionEvent.obtain(event);
//                                cancelEvent.setAction(MotionEvent.ACTION_UP);
//                                recordButton.dispatchTouchEvent(cancelEvent);
//                                return false;
//                            }
//
//                            recordButton.setTranslationX(dx);
//                        }
//
//                        if(movement == MovementEnum.TOP) {
//                            swipeLeftHint.setVisibility(INVISIBLE);
//                            if(dy > -30f) {
//                                Log.d("TEST :::", "SET MOVEMENT: NONE (FROM TOP)");
//                                movement = MovementEnum.NONE;
//                                swipeLeftHint.setVisibility(VISIBLE);
//                                recordButton.animate().translationY(0).start();
//                                return false;
//                                // TODO
//                            } else {
//                                if(dy < DRAG_EDGE_TOP) {
//                                    Log.d("TEST :::", "SWIPED TOP");
//                                    vibrator.vibrate(vibrationEffect);
//                                    currentState = VoiceMessageViewState.LOCK;
//                                    MotionEvent cancelEvent = MotionEvent.obtain(event);
//                                    cancelEvent.setAction(MotionEvent.ACTION_UP);
//                                    recordButton.dispatchTouchEvent(cancelEvent);
//                                    return false;
//                                }
//                            }
//
//                            recordButton.setTranslationY(dy);
//                        }

                    }
                    return false;
                }
            return false;
        });


    }

}
