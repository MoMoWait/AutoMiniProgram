package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.data.ServiceManager;
import cn.edu.fjnu.autominiprogram.data.UrlService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
/**
 * Created by gaofei on 2018/3/29.
 * 忘记密码页面
 */
@ContentView(R.layout.fragment_forget_password)
public class ForgetPasswordFragment extends AppBaseFragment {

    @ViewInject(R.id.edit_code)
    private EditText mEditCode;
    @ViewInject(R.id.btn_send_code)
    private Button mBtnSendCode;
    @ViewInject(R.id.btn_next_step)
    private Button mBtnNextStep;
    @ViewInject(R.id.edit_phone_num)
    private EditText mEditPhoneNum;


    private final int MAX_TIME = 60;
    private int mCurrentTime = MAX_TIME;
    private Handler mTimeHandler;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        mTimeHandler = new Handler();
        initEvent();
    }

    private void initEvent(){
        mBtnSendCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String phoneNum = mEditPhoneNum.getText().toString().trim();
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
                        retrofit2.Response<ResponseBody> response = urlService.getCode(body).execute();
                        String sessionId = response.headers().get("Set-Cookie");
                        //Log.i(TAG, "sessionId:" + sessionId);
                        if(sessionId != null){
                            StorageUtils.saveDataToSharedPreference(ConstData.IntentKey.SESSION_ID, sessionId);
                        }
                        ResponseBody responseBody = response.body();
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
                                                mBtnSendCode.setText(mCurrentTime + "s" + "后重新发送");
                                                mCurrentTime--;
                                                mTimeHandler.postDelayed(this, 1000);
                                            }else{
                                                mTimeHandler.removeCallbacks(this);
                                                mBtnSendCode.setEnabled(true);
                                                mBtnSendCode.setText(R.string.send_code);
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

        mBtnNextStep.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                final String phoneNum = mEditPhoneNum.getText().toString().trim();
                final String code = mEditCode.getText().toString().trim();
                if(TextUtils.isEmpty(phoneNum)){
                    ToastUtils.showToast(R.string.input_user_name);
                    return;
                }
                if(TextUtils.isEmpty(code)){
                    ToastUtils.showToast(R.string.enter_vaild_code);
                    return;
                }
                //调用接口
                Observable.just(phoneNum).map(new Function<String, Integer>() {
                    @Override
                    public Integer apply(String s) throws Exception {
                        UrlService urlService = ServiceManager.getInstance().getUrlService();
                        JSONObject reqObject = new JSONObject();
                        reqObject.put("phone", phoneNum);
                        reqObject.put("code", code);
                        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
                        String sessionID = StorageUtils.getDataFromSharedPreference(ConstData.IntentKey.SESSION_ID);
                        ResponseBody responseBody = urlService.checkCode(body, sessionID).execute().body();
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
                                    StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.FORGET_PASSWORD_PHONE, phoneNum);
                                    getFragmentManager().beginTransaction().replace(android.R.id.content, new PasswordChangeFragment()).commit();
                                }else{
                                    ToastUtils.showToast(R.string.check_code_failed);
                                }
                            }
                        }, new Consumer<Throwable>() {
                            @Override
                            public void accept(Throwable throwable) throws Exception {
                                ToastUtils.showToast(R.string.check_code_failed);
                            }
                        });
            }
        });
    }
}
