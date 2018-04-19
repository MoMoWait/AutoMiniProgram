package cn.edu.fjnu.autominiprogram.service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.activity.MainActivity;

import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.hkh.Constant;
import cn.edu.fjnu.autominiprogram.hkh.Main;
import cn.edu.fjnu.autominiprogram.utils.CommonUtils;
import momo.cn.edu.fjnu.androidutils.data.CommonValues;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

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
    private boolean mIsSharing = false;
    private Main mMain;

    private ExecutorService singleThreadExecutor = Executors.newSingleThreadExecutor(); //单线程池
    private ScheduledExecutorService mScheduleExecutorService = Executors.newScheduledThreadPool(1); //定时执行线程池
    /**
     * 当前是否正在定位
     */
    private boolean mIsSeekPosition;
    /**
     * 窗口是否初始化
     */
    private boolean mIsInit = true;
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
    @ViewInject(R.id.btn_min_window)
    private Button mBtnMinWindow;
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


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //f (intent == null) {
        ///    Log.e(TAG, "onStartCommand(): intent = null");
          //  return -1;
        //}
        CommonUtils.weriteLogToFile("Service started");
        createFloatingView(LayoutInflater.from(this));
        Message message = new Message();
        message.what = REFRESH_VIEW;
        mHandler.sendMessage(message);
        mMain = new Main(getApplicationContext());
        ToastUtils.showToast("发单软件已成功启动");
        NotificationCompat.Builder mBuilder=new NotificationCompat.Builder(this);
        mBuilder.setSmallIcon(R.mipmap.ic_launcher);
        mBuilder.setContentTitle("小程序自动发单");
        mBuilder.setContentText("软件正在运行");
        Notification notification=mBuilder.build();
        startForeground(startId, notification);
        return Service.START_NOT_STICKY;
       // return super.onStartCommand(intent, flags, startId);
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
        reSetWindow();
    }

    private void initWmParams() {
        if (mWmParams == null) {
            mWmParams = new WindowManager.LayoutParams();
            mWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            //mWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;// 2002|WindowManager.LayoutParams.TYPE_SYSTEM_ALERT
            //8.0窗口
            if(Build.VERSION.SDK_INT >= 26)
                mWmParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            else {
                mWmParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;
            }
            // ;
            mWmParams.flags |= 8;
            mWmParams.format = PixelFormat.TRANSPARENT;
            // mWmParams.flags |=
            // WindowManager.LayoutParams.FLAG_HARDWARE_ACCELERATED;
            mWmParams.gravity = Gravity.LEFT | Gravity.TOP;
            //mWmParams.gravity = Gravity.CENTER;
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

    private Runnable mRunnable_tran = new Runnable(){
        @Override
        public void run() {
            //开始时间
            mMain.start_now();
            //自动停止
            while(!mMain.stop_now()) {
                mMain.sale();
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }


        }
    };

    private Runnable mRunnable_init = new Runnable() {
        @Override
        public void run() {
            //提示开始初始化
            mHandler.sendEmptyMessage(Toast_start_init);
            int count = 0;
            //while(true) {
            count++;
            Constant.chat_status = false;
            Constant.interval_chat = -1;
            //获取坐标
            int startX = Integer.parseInt(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.START_X));
            int startY = Integer.parseInt(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.START_Y));
            mMain.initRegularPosition(startX, startY);
            try {
                Thread.sleep(5000);
                Log.e(TAG, "count = " + count);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //}
            mHandler.sendEmptyMessage(Toast_stop_int);
            //提示已经初始化完成了
        }
    };

    private void setupViews() {
        mStartStopBtn = (Button) mView.findViewById(R.id.main_item_test_stop);
        mStartStopBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reSetWindow();
                //此处判断是否开启自动转发
                boolean isAutoStartStop = "true".equals(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.IS_AUTO_START_STOP));
                if(isAutoStartStop){
                    ToastUtils.showToast("定时启停已开启，无法手动转发");
                    return;
                }
                if(mIsSharing){
                    mIsSharing = false;
                    mStartStopBtn.setText(R.string.start_share);
                    //这里加入停止转发功能代码
                    Constant.exit_thread = true;
                    //singleThreadExecutor.shutdown();
                    //singleThreadExecutor.shutdownNow();
                    mHandler.sendEmptyMessage(Toast_stop_tran);
                    CommonUtils.restartFloatingWindowService();
                }else{
                    mHandler.sendEmptyMessage(Toast_start_tran);
                    mIsSharing = true;
                    mStartStopBtn.setText(R.string.stop_share);
                    ClearData();
                    mMain.getData();
                    singleThreadExecutor.execute(mRunnable_tran);

                }

            }
        });
        mBtnSetting.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reSetWindow();
                Intent intent = new Intent(FloatingwindowService.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        mBtnStartCalibratio.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                reSetWindow();
                Log.e(TAG, "mBtnStartCalibratio click");
                Thread thread = new Thread(mRunnable_init);
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
                mView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mIsSeekPosition){
                            mIsSeekPosition = false;
                            //获取点击的中心点坐标
                            Log.i(TAG, "click->position:" + "(" + (mViewX) + "," + (mViewY ) + ")");
                            CommonUtils.weriteLogToFile("定位坐标:" +  "(" + (mViewX) + "," + (mViewY ) + ")");
                            ToastUtils.showToast("已定位坐标:" +  "(" + (mViewX) + "," + (mViewY ) + ")");
                            StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.START_X, "" + mViewX);
                            StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.START_Y, "" + mViewY);
                            //mView.setBackgroundColor(Color.TRANSPARENT);
                            //mLayoutBtnContainer.setVisibility(View.VISIBLE);
                            reSetWindow();
                        }
                    }
                });
            }
        });

        mBtnMinWindow.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                reSetWindow();
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

                return  !mIsInit && !mIsSeekPosition;
            }
            return true;
            //return !mIsInit || !mIsSeekPosition;
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
        CommonUtils.weriteLogToFile("Service onDestory");
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
        }
        if (mWm != null)
            mWm.removeView(mView);

    }

    private static final int REFRESH_VIEW = 0;
    private static final int Toast_start_init = 1;
    private static final int Toast_stop_int = 2;
    private static final int Toast_start_tran = 3;
    private static final int Toast_stop_tran = 4;
    /**自动启停开始*/
    private static final int AUTO_START_SHARE = 5;
    /**自动启停结束*/
    private static final int AUTO_STOP_SHARE = 6;

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
                case Toast_start_init:
                    ToastUtils.showToast("开始初始化，请勿触碰", 1000);
                    break;

                case Toast_stop_int:
                    ToastUtils.showToast("初始化完成", 1000);
                    break;

                case Toast_start_tran:
                    ToastUtils.showToast("开始转发", 1000);
                    break;

                case Toast_stop_tran:
                    ToastUtils.showToast("即将退出转发，请等待几秒钟", 1000);
                case AUTO_START_SHARE:
                    ToastUtils.showToast("自动转发开始");
                    ClearData();
                    mMain.getData();
                    singleThreadExecutor.execute(mRunnable_tran);
                    break;
                case AUTO_STOP_SHARE:
                    ToastUtils.showToast("自动转发结束");
                    CommonUtils.restartFloatingWindowService();
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
    public void onCreate() {
        Log.i(TAG, "RobMoney::onCreate");
        CommonUtils.weriteLogToFile("Service onCreate");
        //开启定时任务,30s执行一次时间获取
        mScheduleExecutorService.scheduleWithFixedDelay(new Runnable() {
            @Override
            public void run() {
                SimpleDateFormat sdf = new SimpleDateFormat("HH:mm", Locale.CHINA);
                boolean isAutoStartStop = "true".equals(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.IS_AUTO_START_STOP));
                if(isAutoStartStop){
                    //获取当前时间
                    String currTime = sdf.format(new Date());
                    Log.i(TAG, "currTime:" + currTime);
                    String autoStartTime = StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.AUTO_SEND_START_TIME);
                    String autoEndTime = StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.AUTO_SEND_END_TIME);
                    boolean isAutoStarted = "true".equals(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.IS_AUTO_STARTED));
                    boolean isAutoStoped = "true".equals(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.IS_AUTO_STOPPED));
                    if(currTime.equals(autoStartTime) && !isAutoStarted){
                        //发送自动启停开始消息
                        StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.IS_AUTO_STARTED, "true");
                        StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.IS_AUTO_STOPPED, "false");
                        mHandler.sendEmptyMessage(AUTO_START_SHARE);
                    }

                    if(currTime.equals(autoEndTime) && !isAutoStoped){
                        StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.IS_AUTO_STARTED, "false");
                        StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.IS_AUTO_STOPPED, "true");
                        //发送自动启停停止消息
                        mHandler.sendEmptyMessage(AUTO_STOP_SHARE);
                    }
                }
            }
        },0, 30, TimeUnit.SECONDS);
    }


    /**
     * 重置窗口
     */
    private void reSetWindow(){
        CommonUtils.weriteLogToFile("Service->reSetWindow");
        mWmParams.x = 0;
        mWmParams.y = 0;
        refreshView();
        mIsInit = true;
        mIsSeekPosition = false;
        mLayoutBtnContainer.setVisibility(View.GONE);
        mView.setBackgroundResource(R.drawable.luck_number_back_yellow);
        //Log.i(TAG, "click->position:" + "(" + (mViewX + SizeUtils.dp2px(15)) + "," + (mViewY + SizeUtils.dp2px(15)) + ")");
        mView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mIsInit = false;
                Log.i(TAG, "restWindow->click");
                mView.setBackgroundColor(Color.TRANSPARENT);
                mLayoutBtnContainer.setVisibility(View.VISIBLE);
            }
        });
    }

}
