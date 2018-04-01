package cn.edu.fjnu.autominiprogram.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.activity.MyMoneyActivity;
import cn.edu.fjnu.autominiprogram.activity.RecommendListActivity;
import cn.edu.fjnu.autominiprogram.adapter.ColorAdapter;
import cn.edu.fjnu.autominiprogram.adapter.ColorTypeAdapter;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.ColorInfo;
import cn.edu.fjnu.autominiprogram.bean.ColorType;
import cn.edu.fjnu.autominiprogram.bean.UserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;

/**
 * Created by gaofei on 2017/9/9.
 * 首页
 */
@ContentView(R.layout.fragment_home)
public class HomeFragment extends AppBaseFragment {
    @ViewInject(R.id.text_user_name)
    private TextView mTextUserName;
    @ViewInject(R.id.text_recommend_code)
    private TextView mTextRecommendCode;
    @ViewInject(R.id.text_money)
    private TextView mTextMoney;
    @ViewInject(R.id.layout_my_money)
    private RelativeLayout mlayoutMyMoney;
    @ViewInject(R.id.layout_request_money)
    private RelativeLayout mLayoutRequestMoney;
    @ViewInject(R.id.text_request_money)
    private TextView mTextRequestMoney;
    @ViewInject(R.id.layout_recommend_list)
    private LinearLayout mLayoutRecommendList;

    private UserInfo mUserInfo;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        //还原当前登陆的用户信息
        String strUserInfo = StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.CURR_USER_INFO);
        JSONObject userInfoObject = null;
        try{
            userInfoObject = new JSONObject(strUserInfo);
        }catch (Exception e){
            //no handle
        }
        mUserInfo = (UserInfo) JsonUtils.jsonToObject(UserInfo.class, userInfoObject);
        mTextUserName.setText(mUserInfo.getUserName());
        mTextRecommendCode.setText(mUserInfo.getPhone());
        mTextRequestMoney.setText(String.valueOf(mUserInfo.getCanGet()));

        initEvent();
    }


    private void initEvent(){
        mlayoutMyMoney.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                //Intent intent = new Intent(getContext(), MyMoneyActivity.class);
                //startActivity(intent);
            }
        });
        mLayoutRequestMoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MyMoneyActivity.class);
                startActivity(intent);
            }
        });
        mLayoutRecommendList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), RecommendListActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
