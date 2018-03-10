package cn.edu.fjnu.autominiprogram.service;

import java.util.Timer;


import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.activity.MainActivity;


public class FloatingwindowService extends Service {
    public static final String TAG = "MainTestService";

    private Timer mTimer = null;
    private int mCurrentX = 0;
    private int mCurrentY = 0;
    private int mStartX = 0;
    private int mStartY = 0;

    private WindowManager mWm = null;
    private WindowManager.LayoutParams mWmParams = null;

    private View mView = null;

    private TextView mMainItemTestView = null;

    private Button mStartStopBtn = null;
    private WakeLock mWakeLock;
    private int[] mColor = {Color.RED, Color.WHITE, Color.YELLOW};

    @ViewInject(R.id.btn_setting)
    private Button mBtnSetting;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void holdWakeLock() {
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        mWakeLock = powerManager.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK,
                getClass().getCanonicalName());
        mWakeLock.acquire();
    }

    private void releaseWakeLock() {
        if (mWakeLock != null && mWakeLock.isHeld()) {
            mWakeLock.release();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            Log.e(TAG, "onStartCommand(): intent = null");
            return -1;
        }
        createFloatingView(LayoutInflater.from(this));
        Message message = new Message();
        message.what = REFRESH_VIEW;
        mHandler.sendMessage(message);
        return super.onStartCommand(intent, flags, startId);
    }

    private void createFloatingView(LayoutInflater inflater) {
        mWm = (WindowManager) getApplicationContext()
                .getSystemService("window");

        View view = inflater.inflate(R.layout.floatingwindow, null);
        x.view().inject(this, view);
        mView = view.findViewById(R.id.floating_view);
        mView.setOnTouchListener(mTouchListener);
        initWmParams();
        mView.setBackgroundColor(Color.RED);
        mWm.addView(mView, mWmParams);
        setupViews();
    }

    private void initWmParams() {
        if (mWmParams == null) {
            mWmParams = new WindowManager.LayoutParams();
            mWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                    | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;// 2002|WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            // ;
            // mWmParams.format = PixelFormat.TRANSLUCENT;
            mWmParams.flags |= 8;
            // mWmParams.flags |=
            // WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
            mWmParams.gravity = Gravity.LEFT | Gravity.TOP;

            // int mWidth = VIEW_WIDTH;
            // int mHeight = VIEW_HEIGHT;
            int mWidth = getResources().getDimensionPixelSize(
                    R.dimen.main_test_view_width);

            int mHeight = getResources().getDimensionPixelSize(
                    R.dimen.main_test_view__height);
            // DisplayMetrics d = getResources().getDisplayMetrics();
            // wmParams.x = (d.widthPixels - mWidth) / 2;
            // wmParams.y = (d.heightPixels - mHeight) / 2;
            mWmParams.x = 0;
            mWmParams.y = 0;

            mWmParams.width = mWidth;
            mWmParams.height = mHeight;
        }
    }

    private void setupViews() {
        mStartStopBtn = (Button) mView.findViewById(R.id.main_item_test_stop);
        mStartStopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearData();
            }
        });
        mBtnSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(FloatingwindowService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
    }

    private void ClearData() {
        //stopTest();
        //stopSelf();
    }

    private void refreshView() {
        // mView.setBackgroundColor(mColor[mCurrentCount%mColor.length]);
        mView.invalidate();

        if (mView != null && mView.getParent() != null) {
            mWm.updateViewLayout(mView, mWmParams);
        }
        // mTestView.postInvalidate();
    }

    private boolean isDoubleClick = false;
    OnTouchListener mTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction() & MotionEvent.ACTION_MASK) {
                case MotionEvent.ACTION_DOWN:
                    isDoubleClick = false;
                    break;
                case MotionEvent.ACTION_POINTER_DOWN:
                    isDoubleClick = true;
                    break;
                case MotionEvent.ACTION_MOVE:
                    break;
                case MotionEvent.ACTION_POINTER_UP:
                    break;
                case MotionEvent.ACTION_UP:
                    break;
            }
            if (!isDoubleClick) {
                tarckFlinger(event);
                return true;
            }
            return true;
        }
    };

    private boolean tarckFlinger(MotionEvent event) {
        /*
		 * wmParams.width+=50; wmParams.height+=50;
		 * mVideoView.setVideoMeasure(wmParams.width, wmParams.height);
		 * wm.updateViewLayout(mRootView, wmParams);
		 */
        mCurrentX = (int) event.getRawX();
        mCurrentY = (int) event.getRawY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                updateWindowParams();
                break;
            case MotionEvent.ACTION_UP:
                mStartX = 0;
                mStartY = 0;
                break;
        }
        return true;
    }

    private void updateWindowParams() {
        mWmParams.x = mCurrentX - mStartX;
        mWmParams.y = mCurrentY - mStartY;
        mWm.updateViewLayout(mView, mWmParams);
    }

    private void startTest() {

    }

    private void stopTest() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mWm != null)
            mWm.removeView(mView);

    }

    private static final int REFRESH_VIEW = 0;

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            Message message = new Message();
            switch (msg.what) {
                case REFRESH_VIEW:
                    //Log.d(TAG, "-------->REFRESH_VIEW1-------------");
                    refreshView();

                    message.what = REFRESH_VIEW;
                    mHandler.sendMessageDelayed(message, 1000);
                    break;
                default:
                    break;
            }
        }
    };


    private String getRunningActivity() {
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        String name = manager.getRunningTasks(2).get(0).topActivity
                .getClassName();
        Log.e(TAG, "RUNNING: " + name);
        return name;
    }

    private Boolean isTesting() {
        String name = getRunningActivity();

//        if (name.equals("com.android.camera.XXXActivity")
//                || name.equals("com.android.camera.VideoCamera")
//                || name.equals("com.android.camera.CameraLauncher")) {
//            return true;
//        } else
        return false;
    }

}
