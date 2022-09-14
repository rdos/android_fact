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

    private float DRAG_EDGE_LEFT;
    private float DRAG_EDGE_TOP;

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

    private float dx, dy;

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

            DRAG_EDGE_LEFT = -(recordButton.getWidth() * 3.5f);
            DRAG_EDGE_TOP = -(recordButton.getWidth() * 2);

            currentState = VoiceMessageViewState.RECORDING;
            return false;
        });

        recordButton.setOnTouchListener((v, event) -> {

            switch(event.getAction()) {

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

                        float rawX = event.getRawX();
                        float rawY = event.getRawY();

                        if(movement == MovementEnum.NONE) {
                            if(rawY > recordButtonY1 && rawY < recordButtonY2 && rawX < recordButtonX1) {
                                movement = MovementEnum.LEFT;
                            }
                            else if (rawX > recordButtonX1 && rawX < recordButtonX2 && rawY < recordButtonY1) {
                                movement = MovementEnum.TOP;
                            }
                        }

                        if(movement == MovementEnum.LEFT) {
                            Log.d("TEST :::", "left");

                            dx = rawX - recordButtonX1;
                            Log.d("TEST :::", "" + dx);
                            if(dx < 10f) {
                                recordButton.setTranslationX(dx);
                            }

                            if(dx < DRAG_EDGE_LEFT) {
                                Log.d("TEST :::", "SWIPED LEFT");
                                vibrator.vibrate(vibrationEffect);
                                currentState = VoiceMessageViewState.LOCK;
                                MotionEvent cancelEvent = MotionEvent.obtain(event);
                                cancelEvent.setAction(MotionEvent.ACTION_UP);
                                recordButton.dispatchTouchEvent(cancelEvent);
                                return false;
                            }

                            if (rawX > recordButtonX1 && rawX < recordButtonX2 && rawY < recordButtonY1) {
                                movement = MovementEnum.TOP;
                                recordButton.setTranslationX(0);
                            }

                            return false;
                        }

                        if(movement == MovementEnum.TOP) {
                            Log.d("TEST :::", "top");

                            dy = rawY - recordButtonY1;
                            Log.d("TEST :::", "" + dy);
                            if(dy < 10f) {
                                recordButton.setTranslationY(dy);
                            }

                            if(dy < DRAG_EDGE_TOP) {
                                Log.d("TEST :::", "SWIPED TOP");
                                vibrator.vibrate(vibrationEffect);
                                currentState = VoiceMessageViewState.LOCK;
                                MotionEvent cancelEvent = MotionEvent.obtain(event);
                                cancelEvent.setAction(MotionEvent.ACTION_UP);
                                recordButton.dispatchTouchEvent(cancelEvent);
                                return false;
                            }

                            if(rawY > recordButtonY1 && rawY < recordButtonY2 && rawX < recordButtonX1) {
                                movement = MovementEnum.LEFT;
                                recordButton.setTranslationY(0);
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
