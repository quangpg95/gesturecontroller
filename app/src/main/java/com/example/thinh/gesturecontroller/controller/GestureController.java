package com.example.thinh.gesturecontroller.controller;

import android.accessibilityservice.AccessibilityService;
import android.annotation.SuppressLint;
import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.Rect;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraManager;
import android.os.Build;
import android.os.Handler;
import android.os.PowerManager;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.RequiresApi;
import android.support.v7.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityNodeInfo;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

import com.example.thinh.gesturecontroller.R;
import com.example.thinh.gesturecontroller.service.GestureService;
import com.example.thinh.gesturecontroller.util.GestureUtil;
import com.example.thinh.gesturecontroller.util.PrefUtil;

import static android.content.Context.WINDOW_SERVICE;
import static com.example.thinh.gesturecontroller.ui.activity.MainActivity.KEY_COUNT_EVENT;

public class GestureController implements SharedPreferences.OnSharedPreferenceChangeListener {
    private static final String TAG = "GestureController";

    private static final double MAGIC_ANGLE = Math.PI / 6;

    private static final int VELOCITY_FLING_SIDE = 300; // thinhavb: gia toc phay tay cac canh

    private static final int DISTANCE_FLING_SIDE = 20; // chieu dai phay tay cac canh

    //: up ma xa qua 300ms thi thoi khong coi la fling nua
    private static final long FLING_TIMEOUT = 300;

    private static final long LONG_PRESS_TIMEOUT = 400;

    private static final long VIBRATE_TIME = 40;

    private static final int VIBRATE_STRENGTH = 10;

    private static final double FLING_MINIMUM_VELOCITY = 2f;

    private static final int TIME_ANIMATION = 750;

    private static final long LONG_PRESS_VIBRATE_TIME = 100;

    private static final int NONE_STATE = 0;

    private static final int UNCERTAIN_STATE = 1;

    private static final int DRAGGING_STATE = 2;

    private static final int DRAGGING_DIAGONALLY = 3;

    private static final int DRAGGING_HORIZONTALLY = 4;

    private static final int LONG_PRESSED_EDGE_STATE = 5;

    private boolean mIsSentDragDiagonallyDownAction;

    private float mFakeDownY, mFakeDownX;

    private int mState;

    private int mTouchSlop;

    private double mTouchLimit;

    private int mMinDistanceToDrag;

    private boolean mTouchLeft;

    private boolean mTouchRight;

    private boolean mTouchBottom;

    private boolean mPendingSpringBack = false;

    private int mScreenWidth;

    private int mScreenHeight;

    private int mMinimumFlingVelocity;

    private int mMaximumFlingVelocity;

    private float mDownX;

    private float mDownY;

    private VelocityTracker mVelocityTracker;

    private Vibrator mVibrator;

    private Handler mHandler = new Handler();

    private float mMinMoveX;

    private float mMaxMoveX;

    // thinhavb: them vao 2 bien dung cho vuot nhanh tu day man hinh
    private float mMinMoveY;

    private int mMinMoveToTurnOffScreen;

    private int mMaxMoveUpToTurnOffScreen;

    private long mScreenShotTimeStarted;

    private Context mContext;

    private GestureViewController mGestureViewController;

    private GestureService mGestureService;

    private GestureDetector mGestureDetector;

    private boolean mIsGestureDetector; // GestureDetector da xu ly su kien

    private BrightnessViewController mBrightnessSeekBar;

    private VolumeViewController mVolumeSeekBar;

    private int mMarginSideSeekbar;

    private boolean mIsOnFlash;

    private int mFlingBottomPref;
    private int mHoldBottomPref;
    private int mDoubleClickBottomPref;
    private int mSwipeHoldBottomPref;

    private int mFlingLeftPref;
    private int mHoldLeftPref;
    private int mDoubleClickLeftPref;
    private int mSwipeHoldLeftPref;

    private int mFlingRightPref;
    private int mHoldRightPref;
    private int mDoubleClickRightPref;
    private int mSwipeHoldRightPref;

    private long mTouchDownMs;
    private int mCountTouchDown = 0;
    private long mLastTouchDown;

    private float mDistanceSwipeHold;

    private boolean mIsEventSwipe;


    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public GestureController(Context context, GestureViewController gestureController, GestureService gestureService) {
        mContext = context;
        mGestureViewController = gestureController;
        mGestureService = gestureService;
        mGestureDetector = new GestureDetector(context, new GestureListener());
        initPref();
        registerChangePref();
        initView(context);

        mPref = mContext.getSharedPreferences(KEY_COUNT_EVENT, Context.MODE_PRIVATE);
        mEditor = mPref.edit();
    }

    private void initPref() {
        mFlingBottomPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_swipe_up_key), 0);
        mHoldBottomPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_hold_indicator_key), 0);
        mDoubleClickBottomPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_double_click_home_indicator_key), 0);
        mSwipeHoldBottomPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_swipe_up_and_hold_key), 0);

        mFlingRightPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_left_swipe_key), 0);
        mHoldRightPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_hold_right_indicator_key), 0);
        mDoubleClickRightPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_double_click_right_indicator_key), 0);
        mSwipeHoldRightPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_left_swipe_and_hold_key), 0);

        mFlingLeftPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_right_swipe_key), 0);
        mHoldLeftPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_hold_left_indicator_key), 0);
        mDoubleClickLeftPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_double_click_left_indicator_key), 0);
        mSwipeHoldLeftPref = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_right_swipe_and_hold_key), 0);

    }

    private void initView(Context context) {
        updateResources(context);
        reset();

        mBrightnessSeekBar = new BrightnessViewController(mContext);
        mVolumeSeekBar = new VolumeViewController(mContext);

        mVibrator = (Vibrator) context.getSystemService(Service.VIBRATOR_SERVICE);

        mDistanceSwipeHold = mContext.getResources().getDimension(R.dimen.distance_swipe_hold);
    }

    private void reset() {
        mState = NONE_STATE;

        mTouchLeft = false;
        mTouchRight = false;
        mTouchBottom = false;

        mDownX = -1f;
        mDownY = -1f;

        if (mVelocityTracker != null) {
            mVelocityTracker.recycle();
            mVelocityTracker = null;
        }

        mIsSentDragDiagonallyDownAction = false;
        mFakeDownX = -1;
        mFakeDownY = -1;

        mMarginSideSeekbar = (int) mContext.getResources().getDimension(R.dimen.margin_side_verical_seekbar);
        mIsEventSwipe = false;
    }

    @SuppressLint("NewApi")
    public void updateResources(Context context) {
        Display display = ((WindowManager) mContext.getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        mScreenWidth = size.x;
        mScreenHeight = size.y - 144;

        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mMinimumFlingVelocity = ViewConfiguration.get(context).getScaledMinimumFlingVelocity();
        mMaximumFlingVelocity = ViewConfiguration.get(context).getScaledMaximumFlingVelocity();

        //mTouchLimit = PrefUtil.getInt(mContext, mContext.getString(R.string.pref_width_window_key), 25);
        mTouchLimit = 90;
        // launcher
        mMinDistanceToDrag = mTouchSlop * 5; // 40dp

        mMinMoveToTurnOffScreen = context.getResources()
                .getDimensionPixelSize(R.dimen.dashboard_min_move_to_turn_off_screen);
        mMaxMoveUpToTurnOffScreen = context.getResources()
                .getDimensionPixelSize(R.dimen.dashboard_max_move_up_to_turn_off_screen);
    }

    private float mInitialX; // vi tri x cua view
    private float mInitialY; // vi tri y cua view cham vao

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();

        float touchX = event.getRawX();
        float touchY = event.getRawY();

        if (action == MotionEvent.ACTION_DOWN) {
            clickAtPosition((int) touchX, (int) touchY, mGestureService.getRootInActiveWindow());
            mTouchDownMs = System.currentTimeMillis();
            // khi dashboard dang o day man hinh ma co su kien down thi dau tien se
            // bi coi la down o tren dashboard (luc nay chieu cao dashboard la 0). Vay, neu tu
            // -72px toi 0px thi cung coi la cham day.
            //
            if (touchY > mScreenHeight - mTouchLimit) {
                mTouchBottom = true;
            }

            mTouchLeft = touchX < mTouchLimit;
            mTouchRight = touchX > mScreenWidth - mTouchLimit;


            if (mTouchLeft || mTouchRight || mTouchBottom) {
                mState = UNCERTAIN_STATE;
            }

            mDownX = touchX;
            mDownY = touchY;

            mMinMoveX = mScreenWidth;
            mMaxMoveX = 0;
            mMinMoveY = mScreenHeight;

            if (mTouchLeft) {
                mInitialX = mGestureViewController.getParamX(GestureUtil.LEFT_SIDE_WINDOW);
                mInitialY = mGestureViewController.getParamY(GestureUtil.LEFT_SIDE_WINDOW);
            } else if (mTouchRight) {
                mInitialX = mGestureViewController.getParamX(GestureUtil.RIGHT_SIDE_WINDOW);
                mInitialY = mGestureViewController.getParamY(GestureUtil.RIGHT_SIDE_WINDOW);
            } else if (mTouchBottom) {
                mInitialX = mGestureViewController.getParamX(GestureUtil.BOTTOM_SIDE_WINDOW);
                mInitialY = mGestureViewController.getParamY(GestureUtil.BOTTOM_SIDE_WINDOW);
            }
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        // nhan biet cac su kien double click, long press truoc
        mGestureDetector.onTouchEvent(event);
        if (mIsGestureDetector) {
            mIsGestureDetector = false;
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE:
                double distanceX = getDistance(touchX, 0, mDownX, 0);
                double distanceY = getDistance(0, touchY, 0, mDownY);
                long moveDuration = event.getEventTime() - event.getDownTime();

                if (touchX > mMaxMoveX) {
                    mMaxMoveX = touchX;
                }

                if (touchX < mMinMoveX) {
                    mMinMoveX = touchX;
                }

                if (touchY < mMinMoveY) {
                    mMinMoveY = touchY;
                }

                if (mState != NONE_STATE) {
                    if ((moveDuration > FLING_TIMEOUT) && (mState < DRAGGING_STATE)) {
                        // : con lai, la vuot ra giua man hinh
                        mState = DRAGGING_STATE;
                        springBackIfNeeded();
                    }

                    if (mState == DRAGGING_STATE && !mIsEventSwipe) {
                        if (touchY > mDownY && distanceX > mDistanceSwipeHold) {
                            if (mTouchLeft){
                                onEvent(mSwipeHoldLeftPref);
                                mIsEventSwipe = true;
                            } else if(mTouchRight){
                                onEvent(mSwipeHoldRightPref);
                                mIsEventSwipe = true;
                            }
                        } else if(distanceY > mDistanceSwipeHold && mTouchBottom){
                            onEvent(mSwipeHoldBottomPref);
                            mIsEventSwipe = true;
                        }
                    }

                    if ((mTouchBottom || mTouchRight || mTouchLeft) && mScreenShotTimeStarted == 0) {
                        mScreenShotTimeStarted = System.currentTimeMillis();
                    }

                    if (mState == LONG_PRESSED_EDGE_STATE) {
                        onDraggingEdgeAfterLongPressing(mInitialX, mInitialY, mDownX, mDownY, touchX, touchY);
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mState != NONE_STATE) {
                    distanceX = getDistance(touchX, 0, mDownX, 0);
                    distanceY = getDistance(0, touchY, 0, mDownY);

                    boolean handled = false;
                    if (mState == UNCERTAIN_STATE
                            && (distanceX > DISTANCE_FLING_SIDE || distanceY > DISTANCE_FLING_SIDE)) {
                        // A fling must travel the minimum tap distance
                        final VelocityTracker velocityTracker = mVelocityTracker;
                        final int pointerId = event.getPointerId(0);
                        velocityTracker.computeCurrentVelocity(1000, mMaximumFlingVelocity);
                        final float velocityY = velocityTracker.getYVelocity(pointerId);
                        final float velocityX = velocityTracker.getXVelocity(pointerId);

                        float deltaX = Math.abs(touchX - mDownX);
                        float deltaY = Math.abs(touchY - mDownY);
                        boolean flingVertical = deltaY > deltaX;

                        if (Math.abs(velocityY) > mMinimumFlingVelocity
                                || Math.abs(velocityX) > mMinimumFlingVelocity) {
                            handled = onFling(deltaX, deltaY);
                        }
                    }

                    if (!handled) {
                        if (mState == DRAGGING_HORIZONTALLY) {
                            handled = true;
                        } else if (mState == DRAGGING_STATE
                                && (distanceY > mMinDistanceToDrag && mTouchBottom)) {
                            handled = onDraggingLong();
                        } else if (mState == LONG_PRESSED_EDGE_STATE) {
                            handled = onDraggingEdgeAfterLongPressing(mInitialX, mInitialY, mDownX, mDownY, touchX, touchY);
                        } else if (mState == DRAGGING_DIAGONALLY) {
                            handled = true;
                        }
                    }

                    mHandler.removeCallbacksAndMessages(null);
                    if ((System.currentTimeMillis() - mTouchDownMs) > ViewConfiguration.getTapTimeout()) {
                        //ko tinh la su kien tap
                        mCountTouchDown = 0;
                        mLastTouchDown = 0;
                        handled = true;
                    }

                    if (!handled) {
                        if (mCountTouchDown > 0
                                && (System.currentTimeMillis() - mLastTouchDown) < ViewConfiguration.getDoubleTapTimeout()) {
                            mCountTouchDown += 1;
                        } else {
                            mCountTouchDown = 1;
                        }
                        mLastTouchDown = System.currentTimeMillis();

                        if (mCountTouchDown == 3) {
                            if (mTouchLeft) {
                                onEvent(mHoldLeftPref);
                            } else if (mTouchRight) {
                                onEvent(mHoldRightPref);
                            } else if (mTouchBottom) {
                                onEvent(mHoldBottomPref);
                            }
                        } else if (mCountTouchDown == 2) {
                            mHandler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //handle double tap
                                    if (mTouchLeft) {
                                        onEvent(mDoubleClickLeftPref);
                                    } else if (mTouchRight) {
                                        onEvent(mDoubleClickRightPref);
                                    } else if (mTouchBottom) {
                                        onEvent(mDoubleClickBottomPref);
                                    }
                                }
                            }, ViewConfiguration.getDoubleTapTimeout());
                        }
                    }
                }

                // ThinhAVb: bat su kien phay len xuong
                if (mTouchLeft) {
                    long timeEnd = System.currentTimeMillis();
                    if ((timeEnd - mScreenShotTimeStarted) < 500) {
                        float maxDeltaX = mMaxMoveX;
                        float springBackDeltaX = mMaxMoveX - touchX;
                        if (maxDeltaX < mMaxMoveUpToTurnOffScreen
                                && springBackDeltaX > mMinMoveToTurnOffScreen) {
                            turnOnFlash();
                        }
                    }
                } else if (mTouchRight) {
                    long timeEnd = System.currentTimeMillis();
                    if ((timeEnd - mScreenShotTimeStarted) < 500) {
                        float maxDeltaX = mScreenWidth - mMinMoveX;
                        float springBackDeltaX = touchX - mMinMoveX;
                        if (maxDeltaX < mMaxMoveUpToTurnOffScreen
                                && springBackDeltaX > mMinMoveToTurnOffScreen) {
                            lockScreen();
                        }
                    }
                } else if (mTouchBottom) {
                    // thinhavb: bat chuoc doan tat man hinh, thay x bang y
                    // chi dung trong dashboard hoac man hinh khoa
                    //: lam de vuot chup anh kho hon (*2).
                    long timeEnd = System.currentTimeMillis();
                    if (mMinMoveY > (mScreenHeight - mMaxMoveUpToTurnOffScreen)
                            && (touchX - mMinMoveY) > mMinMoveToTurnOffScreen) {
                        mGestureService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);
                    }
                }

                mScreenShotTimeStarted = 0;

                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (!mPendingSpringBack) {
                            reset();
                        }
                    }
                }, ViewConfiguration.getDoubleTapTimeout());


                break;
            default:
                break;
        }
        return mState != NONE_STATE;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public void onLongPress(MotionEvent e) {
            boolean handled = false;
            if (mTouchLeft) {
                handled = true;
                // thinhavb: chay animation
                mGestureViewController.scaleView(GestureUtil.LEFT_SIDE_WINDOW);
            } else if (mTouchRight) {
                handled = true;
                mGestureViewController.scaleView(GestureUtil.RIGHT_SIDE_WINDOW);
            } else if (mTouchBottom) {
                handled = true;
                mGestureViewController.scaleView(GestureUtil.BOTTOM_SIDE_WINDOW);
            }
            mIsGestureDetector = true;
            if (handled) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrateInternal();
                }
                mState = LONG_PRESSED_EDGE_STATE;
            }
            return;
        }
    }

    /**
     * @return true neu can springback
     */
    private boolean springBackIfNeeded() {
        return false;
    }

    private double getDistance(float x1, float y1, float x2, float y2) {
        float deltaX = x1 - x2;
        float deltaY = y1 - y2;

        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void vibrateInternal() {
        //: rung 1 chut de bao. 100ms.
        VibrationEffect effect = VibrationEffect.createOneShot(VIBRATE_TIME,
                VIBRATE_STRENGTH);
        mVibrator.vibrate(effect, null);
    }

    private boolean onDraggingEdgeAfterLongPressing(float initialX, float initialY, float downX, float downY, float currentX, float currentY) {
        if (mTouchLeft) {
            mGestureViewController.updatePositionView(initialX, initialY, downX, downY, currentX, currentY, GestureUtil.LEFT_SIDE_WINDOW);
        } else if (mTouchRight) {
            mGestureViewController.updatePositionView(initialX, initialY, downX, downY, currentX, currentY, GestureUtil.RIGHT_SIDE_WINDOW);
        } else if (mTouchBottom) {
            mGestureViewController.updatePositionView(initialX, initialY, downX, downY, currentX, currentY, GestureUtil.BOTTOM_SIDE_WINDOW);
        }
        return true;
    }

    private boolean onDraggingLong() {
        return false;
    }

    private boolean onFinishDragingLeftOrRightEdge(boolean isDraggingDown) {
        return false;
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    private boolean onFling(float deltaX, float deltaY) {
        boolean handled = false;
        if (deltaX >= Math.tan(MAGIC_ANGLE) * deltaY) {
            if (mTouchLeft) {
                onEvent(mFlingLeftPref);
                handled = true;
            } else if (mTouchRight) {
                onEvent(mFlingRightPref);
                handled = true;
            }
        } else if (mTouchBottom && (deltaY >= Math.tan(MAGIC_ANGLE) * deltaX)) {
            onEvent(mFlingBottomPref);
            handled = true;
        }
        return handled;
    }

    private void registerChangePref() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(mContext);
        sharedPreferences.registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (mContext.getString(R.string.pref_height_window_key).equals(key)) {
            //mTouchLimit = sharedPreferences.getInt(key, 25);
        } else if (mContext.getString(R.string.pref_swipe_up_key).equals(key)) { // phay  bottom
            mFlingBottomPref = sharedPreferences.getInt(key, 0);
        } else if (mContext.getString(R.string.pref_hold_indicator_key).equals(key)) { // giu bottom
            mHoldBottomPref = sharedPreferences.getInt(key, 0);
        } else if (mContext.getString(R.string.pref_double_click_home_indicator_key).equals(key)) {
            mDoubleClickBottomPref = sharedPreferences.getInt(key, 0);
        } else if (mContext.getString(R.string.pref_swipe_up_and_hold_key).equals(key)) {
            mSwipeHoldBottomPref = sharedPreferences.getInt(key, 0);
        } else if (mContext.getString(R.string.pref_left_swipe_key).equals(key)) { // phay cot phai
            mFlingRightPref = sharedPreferences.getInt(key, 0);
        } else if (mContext.getString(R.string.pref_hold_right_indicator_key).equals(key)) {
            mHoldRightPref = sharedPreferences.getInt(key, 0);
        } else if (mContext.getString(R.string.pref_double_click_right_indicator_key).equals(key)) {
            mDoubleClickRightPref = sharedPreferences.getInt(key, 0);
        } else if (mContext.getString(R.string.pref_left_swipe_and_hold_key).equals(key)) {
            mSwipeHoldRightPref = sharedPreferences.getInt(key, 0);
        } else if (mContext.getString(R.string.pref_right_swipe_key).equals(key)) { // phay cot trai
            mFlingLeftPref = sharedPreferences.getInt(key, 0);
        } else if (mContext.getString(R.string.pref_hold_left_indicator_key).equals(key)) {
            mHoldLeftPref = sharedPreferences.getInt(key, 0);
        } else if (mContext.getString(R.string.pref_double_click_left_indicator_key).equals(key)) {
            mDoubleClickLeftPref = sharedPreferences.getInt(key, 0);
        } else if (mContext.getString(R.string.pref_right_swipe_and_hold_key).equals(key)) {
            mSwipeHoldLeftPref = sharedPreferences.getInt(key, 0);
        }
    }

    /********************** cac su kien ************************************/
    SharedPreferences mPref;
    SharedPreferences.Editor mEditor;
    private void onEvent(int action) {
        int count = mPref.getInt(KEY_COUNT_EVENT, -1) + 1;
        mEditor.putInt(KEY_COUNT_EVENT, count);
        mEditor.commit();
        switch (action) {
            case 1:
                mGestureService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_HOME);
                break;
            case 2:
                mGestureService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_BACK);
                break;
            case 3:
                mGestureService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_RECENTS);
                break;
            case 4:
                mGestureService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_NOTIFICATIONS);
                break;
            case 5:
                mGestureService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_QUICK_SETTINGS);
                break;
            case 6:
                // take screen shot
                break;
            case 7:
                mGestureService.performGlobalAction(AccessibilityService.GLOBAL_ACTION_TOGGLE_SPLIT_SCREEN);
                break;
            case 8:
                lockScreen();
                break;
            case 9:
                turnOnFlash();
                break;
            case 10:
                addBrightnessToWindow();
                break;
            case 11:
                addVolumeToWindow();
                break;
        }
    }

    private void addVolumeToWindow() {
        int y = (int) (mDownY - mBrightnessSeekBar.getHeightSeekbar() / 2);
        mBrightnessSeekBar.positionAddView(mMarginSideSeekbar, y);
        mBrightnessSeekBar.addViewToWindow();
    }

    private void addBrightnessToWindow() {
        int y = (int) (mDownY - mVolumeSeekBar.getHeightSeekbar() / 2);
        mVolumeSeekBar.positionAddView(mScreenWidth - mVolumeSeekBar.getWidthSeekbar() - mMarginSideSeekbar, y);
        mVolumeSeekBar.addViewToWindow();
    }

    private void turnOnFlash() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            CameraManager camManager = (CameraManager) mContext.getSystemService(Context.CAMERA_SERVICE);
            String cameraId = null; // Usually back camera is at 0 position.
            try {
                cameraId = camManager.getCameraIdList()[0];
                mIsOnFlash = !mIsOnFlash;
                camManager.setTorchMode(cameraId, mIsOnFlash);
            } catch (CameraAccessException e) {
                Toast.makeText(mContext, mContext.getResources().getString(R.string.support_android_6), Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            }
        }
    }

    // thinhavb: tat man hinh
    private void lockScreen() {
        PowerManager pm = (PowerManager) mContext.getSystemService(Context.POWER_SERVICE);
        if (pm.isScreenOn()) {
            DevicePolicyManager policy = (DevicePolicyManager)
                    mContext.getSystemService(Context.DEVICE_POLICY_SERVICE);
            try {
                policy.lockNow();
            } catch (SecurityException ex) {
                Toast.makeText(
                        mContext,
                        "must enable device administrator",
                        Toast.LENGTH_LONG).show();
//                Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
//                intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, AdminReceiver.class);
//                intent.putExtra(DevicePolicyManager.EXTRA_ADD_EXPLANATION,
//                        "ahihi");
//                mContext.startActivityForResult(intent, 1);
            }
        }
    }

    // thinhavb: fake click xuong view duoi
    public static void clickAtPosition(int x, int y, AccessibilityNodeInfo node) {
        if (node == null) return;

        if (node.getChildCount() == 0) {
            Rect buttonRect = new Rect();
            node.getBoundsInScreen(buttonRect);
            if (buttonRect.contains(x, y)) {
                // Maybe we need to think if a large view covers item?
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                System.out.println("1º - Node Information: " + node.toString());
            }
        } else {
            Rect buttonRect = new Rect();
            node.getBoundsInScreen(buttonRect);
            if (buttonRect.contains(x, y)) {
                // Maybe we need to think if a large view covers item?
                node.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                System.out.println("2º - Node Information: " + node.toString());
            }
            for (int i = 0; i < node.getChildCount(); i++) {
                clickAtPosition(x, y, node.getChild(i));
            }
        }
    }
}
