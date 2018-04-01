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
import java.util.ArrayList;
import java.util.List;
import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.adapter.NotifactionAdapter;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.NotifactionInfo;
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
 * 系统通知页面
 */
@ContentView(R.layout.fragment_system_notification)
public class SystemNotificationFragment extends AppBaseFragment {

    private UserInfo mUserInfo;
    private static final String TAG = SystemNotificationFragment.class.getSimpleName();
    @ViewInject(R.id.list_notification)
    private ListView mListNotification;

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

        Observable.just(new Object()).map(new Function<Object, List<NotifactionInfo>>() {
            @Override
            public List<NotifactionInfo> apply(Object o) throws Exception {
                UrlService urlService = ServiceManager.getInstance().getUrlService();
                JSONObject reqObject = new JSONObject();
                reqObject.put("user_id", mUserInfo.getUserId());
                //reqObject.put("user_id", "10000");
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
                ResponseBody responseBody = urlService.showSystemMsg(body).execute().body();
                List<NotifactionInfo> notifactionInfos = new ArrayList<>();
                if(responseBody != null){
                    String result = responseBody.string();
                    JSONArray resultArray = new JSONArray(result);
                    if(resultArray.length() > 0){
                        for(int i = 0; i < resultArray.length(); ++i){
                            //no body
                            JSONObject itemObject = resultArray.getJSONObject(i);
                            NotifactionInfo notifactionInfo = new NotifactionInfo();
                            notifactionInfo.setContent(itemObject.getString("systemmsg_msg"));
                            notifactionInfo.setDateTime(itemObject.getString("systemmsg_time"));
                            notifactionInfos.add(notifactionInfo);
                        }
                    }
                   return notifactionInfos;
                }
                return notifactionInfos;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<NotifactionInfo>>() {
            @Override
            public void accept(List<NotifactionInfo> result) throws Exception {
                if(result.size() > 0){
                    NotifactionAdapter adapter = new NotifactionAdapter(getContext(), R.layout.adapter_notifaction, 0, result);
                    mListNotification.setAdapter(adapter);
                }else{
                    //获取失败
                    ToastUtils.showToast(R.string.no_notifaction);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ToastUtils.showToast(R.string.get_notifaction_failed);
            }
        });
    }
}
