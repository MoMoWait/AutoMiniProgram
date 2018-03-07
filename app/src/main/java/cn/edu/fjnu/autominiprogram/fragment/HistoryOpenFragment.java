package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.List;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.adapter.HistoryOpenDisplayAdapter;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.ColorInfo;
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
 * Created by gaofei on 2018/2/3.
 * 历史开奖
 */
@ContentView(R.layout.fragment_history_open)
public class HistoryOpenFragment extends AppBaseFragment{

    @ViewInject(R.id.list_now_open)
    private ListView mListNowOpen;
    @ViewInject(R.id.progress_load)
    private ProgressBar mProgressLoad;

    private String mTitle;
    private String mUrl;

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
        mTitle = getActivity().getIntent().getStringExtra(ConstData.IntentKey.LOTTERY_NAME);
        mUrl = getActivity().getIntent().getStringExtra(ConstData.IntentKey.LOTTY_HISTORY_OPEN_URL);
        mProgressLoad.setVisibility(View.VISIBLE);
        Observable.create(new ObservableOnSubscribe<List<ColorInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<ColorInfo>> e) throws Exception {
                e.onNext(LottyDataGetUtils.getFcHistoryOpenInfos(mTitle, mUrl));
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<ColorInfo>>() {
            @Override
            public void accept(final List<ColorInfo> colorInfos) throws Exception {
                mProgressLoad.setVisibility(View.GONE);
                HistoryOpenDisplayAdapter adapter = new HistoryOpenDisplayAdapter(getContext(), R.layout.adapter_lottery_display, 0, colorInfos);
                mListNowOpen.setAdapter(adapter);
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
