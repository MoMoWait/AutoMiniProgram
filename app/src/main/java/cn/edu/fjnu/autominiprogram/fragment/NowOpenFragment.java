package cn.edu.fjnu.autominiprogram.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.List;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.activity.HistoryOpenActivity;
import cn.edu.fjnu.autominiprogram.adapter.NowOpenAdapter;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.NowOpenInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.utils.LottyDataGetUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

/**
 * Created by gaofei on 2018/2/1.
 * 彩票开奖
 */
@ContentView(R.layout.fragment_now_open)
public class NowOpenFragment extends AppBaseFragment{

    @ViewInject(R.id.list_now_open)
    private ListView mListNowOpen;
    @ViewInject(R.id.progress_load)
    private ProgressBar mProgressLoad;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        loadData();
    }

    private void loadData(){
        mProgressLoad.setVisibility(View.VISIBLE);
        Observable.create(new ObservableOnSubscribe<List<NowOpenInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<NowOpenInfo>> e) throws Exception {
                e.onNext(LottyDataGetUtils.getNowOpenInfosByFc());
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<NowOpenInfo>>() {
            @Override
            public void accept(final List<NowOpenInfo> nowOpenInfos) throws Exception {
                mProgressLoad.setVisibility(View.GONE);
                NowOpenAdapter adapter = new NowOpenAdapter(getContext(), R.layout.adapter_now_open, nowOpenInfos);
                mListNowOpen.setAdapter(adapter);
                mListNowOpen.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        NowOpenInfo itemInfo = nowOpenInfos.get(position);
                        Intent intent = new Intent(getContext(), HistoryOpenActivity.class);
                        intent.putExtra(ConstData.IntentKey.LOTTERY_NAME, itemInfo.getTitle());
                        intent.putExtra(ConstData.IntentKey.LOTTY_HISTORY_OPEN_URL, ConstData.FC_LOTTY_HISTORY_URLS.get(itemInfo.getTitle()));
                        intent.putExtra(ConstData.IntentKey.TARGET_ACTIVITY_LABEL, itemInfo.getTitle() + "-" + "历史开奖");
                        startActivity(intent);
                    }
                });
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mProgressLoad.setVisibility(View.GONE);
                ToastUtils.showToast("发生异常，请重试");
            }
        });
    }
}
