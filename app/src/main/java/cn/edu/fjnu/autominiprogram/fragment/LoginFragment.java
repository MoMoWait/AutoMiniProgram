package cn.edu.fjnu.autominiprogram.fragment;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.edu.fjnu.autominiprogram.R;
import android.app.ActivityManager.RunningServiceInfo;

import cn.edu.fjnu.autominiprogram.activity.ForgetPasswordActivity;
import cn.edu.fjnu.autominiprogram.activity.MainActivity;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.UserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.service.FloatingwindowService;
import cn.edu.fjnu.autominiprogram.task.LoginTask;
import cn.edu.fjnu.autominiprogram.view.LoginActionBarView;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.DialogUtils;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;


/**
 * 登录页面及相关处理
 * Created by GaoFei on 2016/3/7.
 */
@ContentView(R.layout.fragment_login)
public class LoginFragment extends AppBaseFragment{

    public final String TAG = "LoginFragment";
    public static final String FLOATING_WINDOW_SERVICE = "cn.edu.fjnu.autominiprogram.service.FloatingwindowService";

    /**登陆按钮*/
    @ViewInject(R.id.btn_login)
    private TextView mBtnLogin;
    @ViewInject(R.id.edt_user_name)
    private EditText mEdtUserName;
    @ViewInject(R.id.edt_password)
    private EditText mEdtPassword;
    @ViewInject(R.id.img_user_head)
    private ImageView mImgUserHead;
    @ViewInject(R.id.text_forget_passwd)
    private TextView mTextForgetPassword;
    private String mUserName;
    private String mPasswd;
    private LoginTask mLoginTask;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        ActionBar actionBar = ((AppCompatActivity)getActivity()).getSupportActionBar();
        if(actionBar != null){
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setCustomView(new LoginActionBarView(getContext()));
        }
        if(Build.VERSION.SDK_INT >= 23){
            if (!Settings.canDrawOverlays(getContext())) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getContext().getPackageName()));
                ToastUtils.showToast("请打开悬浮窗权限");
                startActivity(intent);
            }
        }

        String userName = StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.CURR_PHONE);
        String password = StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.CURR_PASSWORD);
        if(!TextUtils.isEmpty(userName) && !TextUtils.isEmpty(password)){
            mUserName = userName;
            mPasswd = password;
            //mUserName = null;
            //mUserName.equals("heoolo");
            mEdtUserName.setText(userName);
            mEdtPassword.setText(password);
            //new Handler().postDelayed(new Runnable() {
            //    @Override
           //     public void run() {
            //        login();
            //    }
            //}, 100);

        }

        mBtnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //getContext().startActivity(new Intent(getContext(), MainActivity.class));
                //getActivity().finish();

                String userName = mEdtUserName.getText().toString();
                String passwd = mEdtPassword.getText().toString();
                mUserName = userName;
                mPasswd = passwd;
                if(android.text.TextUtils.isEmpty(userName)){
                    ToastUtils.showToast("请输入用户名");
                    return;
                }
                if(android.text.TextUtils.isEmpty(passwd)){
                    ToastUtils.showToast("请输入密码");
                    return;
                }
                login();
            }
        });

        mTextForgetPassword.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ForgetPasswordActivity.class);
                getContext().startActivity(intent);
            }
        });
    }

    private boolean isServiceRunning() {
        ActivityManager manager = (ActivityManager) getContext().getSystemService(getContext().ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager
                .getRunningServices(Integer.MAX_VALUE)) {
            if (FLOATING_WINDOW_SERVICE.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
    public void login(){
        DialogUtils.showLoadingDialog(getActivity(), false);
        mLoginTask = new LoginTask();
        UserInfo info = new UserInfo();
        info.setUserName(mUserName);
        info.setPasswd(mPasswd);
        Observable.just(info).map(new Function<UserInfo, UserInfo>() {
            @Override
            public UserInfo apply(@NonNull UserInfo userInfo) throws Exception {
                mLoginTask = new LoginTask();
                return mLoginTask.login(userInfo.getUserName(), userInfo.getPasswd());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<UserInfo>() {
            @Override
            public void accept(UserInfo userInfo) throws Exception {
                DialogUtils.closeLoadingDialog();
                Log.i(TAG, "login->userInfo:" + userInfo);
                if(userInfo == null){
                    ToastUtils.showToast(getString(R.string.login_failed));
                }else if(userInfo.getState() == ConstData.UserState.NORMAL){
                    //此处保存用户信息
                    StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.CURR_USER_INFO, JsonUtils.objectToJson(userInfo).toString());
                    StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.CURR_PHONE, userInfo.getPhone());
                    StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.CURR_PASSWORD, userInfo.getPasswd());
                    Intent intent = new Intent(getContext(), FloatingwindowService.class);
                    if (isServiceRunning()) {
                        getContext().stopService(intent);
                    }
                    getContext().startService(intent);
                    getActivity().finish();
                }else if(userInfo.getState() == ConstData.UserState.DISABLE){
                    ToastUtils.showToast(R.string.account_disable);
                }else if(userInfo.getType() == 3){
                    ToastUtils.showToast(R.string.no_vaild_account);
                }else{
                    ToastUtils.showToast(getString(R.string.login_failed));
                }

            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                DialogUtils.closeLoadingDialog();
                ToastUtils.showToast(getString(R.string.login_failed));
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        //makeActionBarCenterText();
        //mTextRegister = addActionBarRightText(getString(R.string.register));
    }

    @Override
    public void onPause() {
        super.onPause();
        //removeActionBarText(mTextRegister);
    }
}
