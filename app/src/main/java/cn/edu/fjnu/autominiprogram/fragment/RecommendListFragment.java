package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.adapter.NotifactionAdapter;
import cn.edu.fjnu.autominiprogram.adapter.RecommendAdapter;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.NotifactionInfo;
import cn.edu.fjnu.autominiprogram.bean.RecommendUserInfo;
import cn.edu.fjnu.autominiprogram.bean.UserInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.data.ServiceManager;
import cn.edu.fjnu.autominiprogram.data.UrlService;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.JsonUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * Created by gaofei on 2018/3/31.
 * 推荐列表页面
 */
@ContentView(R.layout.fragment_recommend_list)
public class RecommendListFragment extends AppBaseFragment {
    private static final String TAG = RecommendListFragment.class.getSimpleName();
    @ViewInject(R.id.list_recommend)
    private ListView mListRecommend;
    private UserInfo mUserInfo;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
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
        Observable.just(new Object()).map(new Function<Object, List<RecommendUserInfo>>() {
            @Override
            public List<RecommendUserInfo> apply(Object o) throws Exception {
                UrlService urlService = ServiceManager.getInstance().getUrlService();
                JSONObject reqObject = new JSONObject();
                reqObject.put("user_id", mUserInfo.getUserId());
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
                ResponseBody responseBody = urlService.getRecommendList(body).execute().body();
                List<RecommendUserInfo> recommendUserInfos = new ArrayList<>();
                if(responseBody != null){
                    String result = responseBody.string();
                    JSONArray resultArray = new JSONArray(result);
                    if(resultArray.length() > 0){
                        for(int i = 0; i < resultArray.length(); ++i){
                            //no body
                            JSONObject itemObject = resultArray.getJSONObject(i);
                            RecommendUserInfo recommendUserInfo = new RecommendUserInfo();
                            recommendUserInfo.setUserId(itemObject.getInt("user_id"));
                            recommendUserInfo.setPhone(itemObject.getString("user_phone"));
                            recommendUserInfo.setNickName(itemObject.getString("user_nickname"));
                            recommendUserInfo.setType(itemObject.getInt("user_type"));
                            //Timestamp timestamp = (Timestamp) itemObject.get("user_restime");
                            //Log.i(TAG, "msg_time:" + timestamp.getTime());
                            recommendUserInfo.setRestime(itemObject.getString("user_restime"));
                            recommendUserInfo.setCanGet(itemObject.getDouble("canGet"));
                            recommendUserInfos.add(recommendUserInfo);
                        }
                    }
                }
                return recommendUserInfos;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<RecommendUserInfo>>() {
            @Override
            public void accept(List<RecommendUserInfo> result) throws Exception {
                if(result.size() > 0){
                    RecommendAdapter adapter = new RecommendAdapter(getContext(), R.layout.adapter_recommend, 0, result);
                    mListRecommend.setAdapter(adapter);
                }else{
                    //获取失败
                    ToastUtils.showToast(R.string.no_recommend_user);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ToastUtils.showToast(R.string.get_recommend_list_failed);
            }
        });
    }
}
