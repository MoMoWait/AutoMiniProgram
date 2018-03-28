package cn.edu.fjnu.autominiprogram.service;

import java.util.Timer;


import android.accessibilityservice.AccessibilityService;
import android.app.ActivityManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.accessibility.AccessibilityEvent;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.accessibility.AccessibilityOpenHelperActivity;
import cn.edu.fjnu.autominiprogram.accessibility.AccessibilityOperator;
import cn.edu.fjnu.autominiprogram.accessibility.OpenAccessibilitySettingHelper;
import cn.edu.fjnu.autominiprogram.activity.MainActivity;

import android.accessibilityservice.AccessibilityService;
import android.view.accessibility.AccessibilityEvent;
import cn.edu.fjnu.autominiprogram.accessibility.OpenAccessibilitySettingHelper;
import cn.edu.fjnu.autominiprogram.hkh.Constant;
import cn.edu.fjnu.autominiprogram.hkh.Main;
import cn.edu.fjnu.autominiprogram.hkh.Ocr;
import momo.cn.edu.fjnu.androidutils.utils.SizeUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

public class FloatingwindowService extends AccessibilityService {
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

    private Main mMain;
    /**
     * 当前是否正在定位
     */
    private boolean mIsSeekPosition;
    /**
     * 悬浮框坐标x
     */
    private int mViewX;
    /**
     * 悬浮框坐标y
     */
    private int mViewY;
    @ViewInject(R.id.btn_setting)
    private Button mBtnSetting;
    @ViewInject(R.id.btn_start_calibratio)
    private Button mBtnStartCalibratio;
    @ViewInject(R.id.btn_seek_position)
    private Button mBtnSeekPosition;
    @ViewInject(R.id.layout_btn_container)
    private LinearLayout mLayoutBtnContainer;
    /*
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
*/
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
        //f (intent == null) {
        ///    Log.e(TAG, "onStartCommand(): intent = null");
          //  return -1;
        //}
        createFloatingView(LayoutInflater.from(this));
        Message message = new Message();
        message.what = REFRESH_VIEW;
        mHandler.sendMessage(message);
        mMain = new Main(getApplicationContext());
        return super.onStartCommand(intent, flags, startId);
    }

    private void createFloatingView(LayoutInflater inflater) {
        mWm = (WindowManager) getApplicationContext()
                .getSystemService(Context.WINDOW_SERVICE);

        View view = inflater.inflate(R.layout.floatingwindow, null);
        x.view().inject(this, view);
        mView = view.findViewById(R.id.floating_view);
        mView.setOnTouchListener(mTouchListener);
        initWmParams();
        mWm.addView(view, mWmParams);
        setupViews();
    }

    private void initWmParams() {
        if (mWmParams == null) {
            mWmParams = new WindowManager.LayoutParams();
            mWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
                    | WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;// 2002|WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            // ;
            mWmParams.flags |= 8;
            mWmParams.format = PixelFormat.TRANSPARENT;
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

            mWmParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            mWmParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        }
    }

    private void setupViews() {
        mStartStopBtn = (Button) mView.findViewById(R.id.main_item_test_stop);
        mStartStopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                ClearData();
                Runnable mRunable = new Runnable(){
                    @Override
                    public void run() {
                        while(true) {
                            mMain.sale();
                            try {
                                Thread.sleep(10000);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                };
                Thread thread = new Thread(mRunable);
                thread.start();
                mWmParams.width=50;
                mWmParams.height=50;
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
        mBtnStartCalibratio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Log.e(TAG, "mBtnStartCalibratio click");
                Runnable mRunable = new Runnable() {
                    @Override
                    public void run() {
                        int count = 0;
                        //while(true) {
                            count++;
                            Constant.chat_status = false;
                            Constant.interval_chat = -1;
                            mMain.initRegularPosition(367, 75);
                            try {
                                Thread.sleep(5000);
                                Log.e(TAG, "count = " + count);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        //}
                    }
                };
                Thread thread = new Thread(mRunable);
                thread.start();

                //OpenAccessibilitySettingHelper.jumpToSettingPage(getApplicationContext());
            }
        });
        mBtnSeekPosition.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mIsSeekPosition = true;
                mLayoutBtnContainer.setVisibility(View.GONE);
                mView.setBackgroundResource(R.drawable.luck_number_back_red);
            }
        });
        mView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mIsSeekPosition){
                    mIsSeekPosition = false;
                    //获取点击的中心点坐标
                    Log.i(TAG, "click->position:" + "(" + (mViewX + SizeUtils.dp2px(15)) + "," + (mViewY + SizeUtils.dp2px(15)) + ")");
                    mView.setBackgroundColor(Color.TRANSPARENT);
                    mLayoutBtnContainer.setVisibility(View.VISIBLE);
                }
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
                return !mIsSeekPosition;
            }
            return !mIsSeekPosition;
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
        if(mIsSeekPosition){
            mViewX = mCurrentX;
            mViewY = mCurrentY;
        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mStartX = (int) event.getX();
                mStartY = (int) event.getY();
                Log.i(TAG, "tarckFlinger->mCurrentX:" + mCurrentX);
                Log.i(TAG, "tarckFlinger->mCurrentY:" + mCurrentY);
                Log.i(TAG, "tarckFlinger->mStartX:" + mStartX);
                Log.i(TAG, "tarckFlinger->mstartY:" + mStartY);
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

    @Override
    protected boolean onKeyEvent(KeyEvent event) {
        Log.d(TAG, "onKeyEvent");
        int key = event.getKeyCode();
        switch(key){
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                Intent downintent = new Intent("com.exmaple.broadcaster.KEYDOWN");
                downintent.putExtra("dtime", System.currentTimeMillis());
                sendBroadcast(downintent);
                Log.d(TAG, "KEYCODE_VOLUME_DOWN");
                break;
            case KeyEvent.KEYCODE_VOLUME_UP:
                Intent upintent = new Intent("com.exmaple.broadcaster.KEYUP");
                upintent.putExtra("utime", System.currentTimeMillis());
                sendBroadcast(upintent);
                Log.d(TAG, "KEYCODE_VOLUME_UP");
                break;
        }
        return super.onKeyEvent(event);
    }

    @Override
    public void onInterrupt() {

    }

    @Override
    public void onCreate() {
        Log.i(TAG, "RobMoney::onCreate");
        super.onCreate();

    }

    @Override
    public void onAccessibilityEvent(AccessibilityEvent event) {
        // 此方法是在主线程中回调过来的，所以消息是阻塞执行的
        // 获取包名
        String pkgName = event.getPackageName().toString();
        int eventType = event.getEventType();
        // AccessibilityOperator封装了辅助功能的界面查找与模拟点击事件等操作
        AccessibilityOperator.getInstance().updateEvent(this, event);
        Log.i(TAG, "onAccessibilityEvnent->event->className:" + event.getClass().getName());
        Log.d(TAG, "onAccessibilityEvent pkgName = "+ pkgName + " event = "+event);
        switch (eventType) {
            case AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED:
                break;
        }
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
    }



}
