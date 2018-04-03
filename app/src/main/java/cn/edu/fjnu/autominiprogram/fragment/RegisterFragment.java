package cn.edu.fjnu.autominiprogram.fragment;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.UserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.data.ServiceManager;
import cn.edu.fjnu.autominiprogram.data.UrlService;
import cn.edu.fjnu.autominiprogram.task.RegisterUserTask;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.BitmapUtils;
import momo.cn.edu.fjnu.androidutils.utils.DialogUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Administrator on 2017\9\14 0014.
 * 注册页面
 */

@ContentView(R.layout.fragment_register)
public class RegisterFragment extends AppBaseFragment {
    private static final String TAG = "RegisterFragment";
    @ViewInject(R.id.edit_phone_num)
    private EditText mEditPhoneNum;
    @ViewInject(R.id.edit_password)
    private EditText mEditPassword;
    @ViewInject(R.id.edit_confirm_password)
    private EditText mEditConfirmPassword;
    @ViewInject(R.id.btn_register)
    private Button mBtnRegister;
    @ViewInject(R.id.edit_recommand_code)
    private EditText mEditRecommandCode;
    @ViewInject(R.id.edit_wechat_nickname)
    private EditText mEditWechatNickName;
    @ViewInject(R.id.btn_upload_wechat_screenshot)
    private Button mBtnUploadWechatScreenshot;
    @ViewInject(R.id.img_wechat_screenshot)
    private ImageView mImgWechatScreenshot;
    @ViewInject(R.id.btn_send_code)
    private Button mBtnSendCode;
    @ViewInject(R.id.edit_code)
    private EditText mEditCode;

    private RegisterUserTask mRegisterTask;
    private String mSelectPath;
    private final int MAX_TIME = 30;
    private int mCurrentTime = MAX_TIME;
    private Handler mTimeHandler = new Handler();
    private boolean mIsCheckCodeSucc = false;
    private String mUserPhone;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        ActivityCompat.requestPermissions(getActivity(), new String[]{"android.permission.READ_EXTERNAL_STORAGE"}, 25);
        mRegisterTask = new RegisterUserTask();
        mBtnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String userName = mEditPhoneNum.getText().toString().trim();
                final String password = mEditPassword.getText().toString();
                String confirmPassword = mEditConfirmPassword.getText().toString();
                final String nickeName = mEditWechatNickName.getText().toString().trim();
                final String recommandCode = mEditRecommandCode.getText().toString().trim();
                final String code = mEditCode.getText().toString().trim();
                if(TextUtils.isEmpty(userName) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword) || TextUtils.isEmpty(nickeName)){
                    ToastUtils.showToast(getString(R.string.input_all));
                    return;
                }
                if(!password.equals(confirmPassword)){
                    ToastUtils.showToast(getString(R.string.password_not_same));
                    return;
                }
                if(TextUtils.isEmpty(code)){
                    ToastUtils.showToast(R.string.enter_vaild_code);
                    return;
                }

                if(TextUtils.isEmpty(mSelectPath)){
                    ToastUtils.showToast(R.string.upload_wechat_photo_tip);
                    return;
                }
                DialogUtils.showLoadingDialog(getContext(), false);
                mIsCheckCodeSucc = false;
                //调用接口
                Observable.just(userName).map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {
                        UrlService urlService = ServiceManager.getInstance().getUrlService();
                        JSONObject reqObject = new JSONObject();
                        reqObject.put("phone", userName);
                        reqObject.put("code", code);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
                        String sessionID = StorageUtils.getDataFromSharedPreference(ConstData.IntentKey.SESSION_ID);
                        Call<ResponseBody> response = urlService.checkCode(body, sessionID);
                        Response<ResponseBody> responseBodyResponse = response.execute();
                        ResponseBody responseBody = responseBodyResponse.body();
                        if(responseBody != null){
                            String result = responseBody.string();
                            String msgResult = new JSONObject(result).getString("msg");
                            if(ConstData.MsgResult.SUCC.equals(msgResult)){
                                return ConstData.ErrorInfo.NO_ERR;
                            }else{
                                return ConstData.ErrorInfo.UNKNOW_ERR;
                            }
                            //new JSONObject(result).
                        }

                        return ConstData.ErrorInfo.NET_ERR;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer errrorCode) throws Exception {
                                DialogUtils.closeLoadingDialog();
                                if(errrorCode == ConstData.ErrorInfo.NO_ERR){
                                    mIsCheckCodeSucc = true;
                                    register(userName, password, nickeName, recommandCode);
                                }else{
                                    mIsCheckCodeSucc = false;
                                    ToastUtils.showToast(R.string.check_code_failed);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                DialogUtils.closeLoadingDialog();
                                mIsCheckCodeSucc = false;
                                ToastUtils.showToast(R.string.check_code_failed);
                            }
                        });
                Log.i(TAG, "mIsCheckCodeSucc->mIsCheckCodeSucc:" + mIsCheckCodeSucc);


            }
        });
        mBtnUploadWechatScreenshot.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startSelectPhoto();
            }
        });

        mBtnSendCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneNum = mEditPhoneNum.getText().toString().trim();
                phoneNum = null;
                phoneNum.equals("");
                if(TextUtils.isEmpty(phoneNum)){
                    ToastUtils.showToast(R.string.input_user_name);
                    return;
                }
                //调用接口
                Observable.just(phoneNum).map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {
                        UrlService urlService = ServiceManager.getInstance().getUrlService();
                        JSONObject reqObject = new JSONObject();
                        reqObject.put("phone", s);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
                        Response<ResponseBody> response = urlService.getCode(body).execute();
                        ResponseBody responseBody = response.body();
                        String sessionId = response.headers().get("Set-Cookie");
                        Log.i(TAG, "sessionId:" + sessionId);
                        if(sessionId != null){
                            StorageUtils.saveDataToSharedPreference(ConstData.IntentKey.SESSION_ID, sessionId);
                        }
                        if(responseBody != null){
                            String result = responseBody.string();
                            String msgResult = new JSONObject(result).getString("msg");
                            if(ConstData.MsgResult.SUCC.equals(msgResult)){
                                return ConstData.ErrorInfo.NO_ERR;
                            }else{
                                return ConstData.ErrorInfo.UNKNOW_ERR;
                            }
                            //new JSONObject(result).
                        }

                        return ConstData.ErrorInfo.NET_ERR;
                    }
                }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Consumer<Integer>() {
                            @Override
                            public void accept(Integer errrorCode) throws Exception {
                                if(errrorCode == ConstData.ErrorInfo.NO_ERR){
                                    mBtnSendCode.setEnabled(false);
                                    mCurrentTime = MAX_TIME;
                                    //启动定时器
                                    mTimeHandler.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(mCurrentTime > 0){
                                                if(isCanUpadteUI())
                                                    mBtnSendCode.setText(mCurrentTime + "s" + "后重新发送");
                                                mCurrentTime--;
                                                mTimeHandler.postDelayed(this, 1000);
                                            }else{
                                                mTimeHandler.removeCallbacks(this);
                                                if(isCanUpadteUI()){
                                                    mBtnSendCode.setEnabled(true);
                                                    mBtnSendCode.setText(R.string.send_code);
                                                }

                                            }

                                        }
                                    });
                                }else{
                                    ToastUtils.showToast(R.string.send_code_failed);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ToastUtils.showToast(R.string.send_code_failed);
                            }
                        });
            }
        });

    }

    @Override
    public void onSelectPhoto(Uri uri) {
        if(uri == null){
            ToastUtils.showToast(R.string.sel_photo_failed);
        }else{
            //显示图片
            mImgWechatScreenshot.setVisibility(View.VISIBLE);
            Log.i(TAG, "onSelect->uri:" + uri);
            if(uri.toString().startsWith("content://com.android.providers") && Build.VERSION.SDK_INT >= 19){
                String wholeID = DocumentsContract.getDocumentId(uri);
                String id = wholeID.split(":")[1];
                String[] column = { MediaStore.Images.Media.DATA };
                String sel = MediaStore.Images.Media._ID + "=?";
                Cursor cursor = getActivity().getContentResolver().
                        query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                column, sel, new String[]{ id }, null);
                String filePath = "";
                if(cursor == null)
                    return;
                int columnIndex = cursor.getColumnIndex(column[0]);

                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(columnIndex);
                }
                if(TextUtils.isEmpty(filePath))
                    return;
                mSelectPath = filePath;
                cursor.close();
            }else if(uri.toString().startsWith("content://media/external") && Build.VERSION.SDK_INT >= 26){
                getActivity().getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,new String[]{MediaStore.Images.Media.DATA},null, null);
            }else{
                String[] proj = { MediaStore.Images.Media.DATA };

                // 好像是android多媒体数据库的封装接口，具体的看Android文档
                Cursor cursor = getActivity().managedQuery(uri, proj, null, null, null);

                // 按我个人理解 这个是获得用户选择的图片的索引值
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                // 将光标移至开头 ，这个很重要，不小心很容易引起越界
                cursor.moveToFirst();
                // 最后根据索引值获取图片路径
                mSelectPath = cursor.getString(column_index);
            }
            Log.i(TAG, "mSelectPath:" + mSelectPath);
            x.image().bind(mImgWechatScreenshot, mSelectPath);
            //mSelectPath = uri.getPath();
        }
    }

    private void register(String phone, String password, final String nickName, final String recommendCode){
        UserInfo info = new UserInfo();
        info.setUserName(phone);
        info.setPasswd(password);
        Observable.just(info).map(new Function<UserInfo, Integer>() {
            @Override
            public Integer apply(@NonNull UserInfo userInfo) throws Exception {
                Bitmap bitmap = BitmapUtils.getScaledBitmapFromFile(mSelectPath, 540, 960);
                String photoBase64 = BitmapUtils.getBase64String(bitmap);
                return  mRegisterTask.register(userInfo.getUserName(), userInfo.getPasswd(), nickName, recommendCode, photoBase64);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Integer>() {
                    @Override
                    public void accept(Integer status) throws Exception {
                        DialogUtils.closeLoadingDialog();
                        if(status == ConstData.ErrorInfo.NO_ERR){
                            ToastUtils.showToast(getString(R.string.register_succ));
                            getActivity().finish();
                        }else if(status == ConstData.ErrorInfo.ACCOUNT_EXIST){
                            ToastUtils.showToast(getString(R.string.account_exist));
                        }else if(status == ConstData.ErrorInfo.UNKNOW_ERR){
                            ToastUtils.showToast(getString(R.string.register_failed));
                        }
                    }
                });
    }
}
