package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
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
import cn.edu.fjnu.autominiprogram.adapter.SuggestionReplyAdapter;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.SuggestionReplyInfo;
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
 * Created by gaofei on 2018/4/2.
 * 意见回复页面
 */
@ContentView(R.layout.fragment_suggestion_reply)
public class SuggestionReplyFragment extends AppBaseFragment {
    @ViewInject(R.id.list_suggestion_reply)
    private ListView mListSuggestionReply;
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
        Observable.just(new Object()).map(new Function<Object, List<SuggestionReplyInfo>>() {
            @Override
            public List<SuggestionReplyInfo> apply(Object o) throws Exception {
                UrlService urlService = ServiceManager.getInstance().getUrlService();
                JSONObject reqObject = new JSONObject();
                reqObject.put("user_id", mUserInfo.getUserId());
                RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"),reqObject.toString());
                ResponseBody responseBody = urlService.getSuggestionReplyList(body).execute().body();
                List<SuggestionReplyInfo> suggestionReplyInfos = new ArrayList<>();
                if(responseBody != null){
                    String result = responseBody.string();
                    JSONArray resultArray = new JSONArray(result);
                    if(resultArray.length() > 0){
                        for(int i = 0; i < resultArray.length(); ++i){
                            //no body
                            JSONObject itemObject = resultArray.getJSONObject(i);
                            SuggestionReplyInfo suggestionReplyInfo = new SuggestionReplyInfo();
                            suggestionReplyInfo.setMsg(itemObject.getString("suggestion_msg"));
                            suggestionReplyInfo.setReply(itemObject.getString("suggestion_backmsg"));
                            suggestionReplyInfo.setTime(itemObject.getString("suggestion_time"));
                            suggestionReplyInfos.add(suggestionReplyInfo);
                        }
                    }
                }
                return suggestionReplyInfos;
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<SuggestionReplyInfo>>() {
            @Override
            public void accept(List<SuggestionReplyInfo> result) throws Exception {
                if(result.size() > 0){
                    SuggestionReplyAdapter adapter = new SuggestionReplyAdapter(getContext(), R.layout.adapter_suggestion_reply, 0, result);
                    mListSuggestionReply.setAdapter(adapter);
                }else{
                    //获取失败
                    ToastUtils.showToast(R.string.no_suggestion_reply);
                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                ToastUtils.showToast(R.string.get_suggestion_reply_failed);
            }
        });
    }
}
