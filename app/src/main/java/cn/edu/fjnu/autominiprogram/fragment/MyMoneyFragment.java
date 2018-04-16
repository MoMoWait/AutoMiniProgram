package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.UserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

/**
 * Created by gaofei on 2018/3/31.
 * 我的赏金页面
 */
@ContentView(R.layout.fragment_my_money)
public class MyMoneyFragment extends AppBaseFragment {

    @ViewInject(R.id.btn_request_money)
    private Button mBtnRequestMoney;
    @ViewInject(R.id.text_money)
    private TextView mTextMoney;
    private UserInfo mUserInfo;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater,container);
    }

    @Override
    public void init() {
        super.init();
        try{
            mUserInfo = (UserInfo) JsonUtils.jsonToObject(UserInfo.class, new JSONObject(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.CURR_USER_INFO)));
        }catch (Exception e){
           e.printStackTrace();
        }

        if(mUserInfo == null){
            ToastUtils.showToast(R.string.app_exception);
            getActivity().finish();
            return;
        }

        mTextMoney.setText("" + mUserInfo.getCanGet());
        mBtnRequestMoney.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(mUserInfo.getCanGet() < 0.1){
                    ToastUtils.showToast(R.string.litte_money);
                    return;
                }
                getFragmentManager().beginTransaction().replace(android.R.id.content, new RequestMoneyFragment()).commit();
            }

        });
    }


}
