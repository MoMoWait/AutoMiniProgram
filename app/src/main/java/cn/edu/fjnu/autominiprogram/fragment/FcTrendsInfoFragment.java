package cn.edu.fjnu.autominiprogram.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.activity.WyTrendChartActivity;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.TrendInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import cn.edu.fjnu.autominiprogram.utils.LottyDataGetUtils;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import momo.cn.edu.fjnu.androidutils.utils.SizeUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

/**
 * Created by gaofei on 2018/1/31.
 * 发彩网走势页面
 */
@ContentView(R.layout.fragment_trends_info)
public class FcTrendsInfoFragment extends AppBaseFragment implements View.OnClickListener{
    private static final String TAG = "FcTrendsInfoFragment";
    @ViewInject(R.id.container_lotty_title)
    private LinearLayout mContainerLottyTitle;
    @ViewInject(R.id.list_trendinfos)
    private ListView mListTrendInfos;
    private TextView mTextSelectTitle;
    private List<TrendInfo> mTrendInfos;
    @ViewInject(R.id.progress_load)
    ProgressBar mProgressLoad;
    @ViewInject(R.id.hs_container)
    private HorizontalScrollView mHsContainer;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        initView();
        loadData();
    }

    @Override
    public void onClick(View v) {
        int scrollWidth = mHsContainer.getWidth();
        int hsScrollX = mHsContainer.getScrollX();
        Log.i(TAG, "hsScrollx:" + hsScrollX);
        int vX = (int)v.getX();
        Log.i(TAG, "vX:" + vX);
        int diffScrollX = vX - hsScrollX - scrollWidth / 2 + v.getWidth() / 2;
        Log.i(TAG, "diffScrollX:" + diffScrollX);
        mHsContainer.setScrollX(hsScrollX + diffScrollX);
        int selectIndex = mContainerLottyTitle.indexOfChild(v);
        if(selectIndex >= 0){
            if(mTextSelectTitle != null){
                mTextSelectTitle.setTextColor(getResources().getColor(R.color.black));
                mTextSelectTitle.setTextSize(18);
            }
            TextView clickView = (TextView)v;
            clickView.setTextColor(getResources().getColor(R.color.red));
            clickView.setTextSize(20);
            mTextSelectTitle = clickView;
            update(mTrendInfos.get(selectIndex));
        }
    }

    private void update(final TrendInfo info){
        //走势名称
        final List<String> trendNames = new ArrayList<>();
        //走势URL
        final List<String> trendUrls = new ArrayList<>();
        Map<String, String> urls =info.getTrendUrl();
        Set<String> keys = urls.keySet();
        for(String key : keys){
            trendNames.add(key);
            trendUrls.add(urls.get(key));
        }
        ArrayAdapter<String> trendAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_list_item_1, android.R.id.text1, trendNames);
        mListTrendInfos.setAdapter(trendAdapter);
        mListTrendInfos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getContext(), WyTrendChartActivity.class);
                intent.putExtra(ConstData.IntentKey.TARGET_ACTIVITY_LABEL, info.getName() + "-" + trendNames.get(position));
                intent.putExtra(ConstData.IntentKey.WEB_LOAD_URL, trendUrls.get(position));
                startActivity(intent);
            }
        });
    }

    private void initView(){
    }

    private void loadData(){
        mProgressLoad.setVisibility(View.VISIBLE);
        Observable.create(new ObservableOnSubscribe<List<TrendInfo>>() {
            @Override
            public void subscribe(ObservableEmitter<List<TrendInfo>> e) throws Exception {
                List<TrendInfo> trendInfos = LottyDataGetUtils.getAllTrendInfoByWy();
                e.onNext(trendInfos);
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<TrendInfo>>() {
            @Override
            public void accept(List<TrendInfo> trendInfos) throws Exception {
                mProgressLoad.setVisibility(View.GONE);
                if (trendInfos == null || trendInfos.size() == 0) {
                    //检查网络
                    ToastUtils.showToast("请检查网络");
                } else {
                    //获取当前选中Title
                    mTrendInfos = trendInfos;
                    String selectTitle = getActivity().getIntent().getStringExtra(ConstData.IntentKey.LOTTERY_NAME);
                    boolean isFind = false;
                    for (TrendInfo info : trendInfos) {
                        TextView lottyTitleTextView = new TextView(getContext());
                        lottyTitleTextView.setTextSize(18);
                        lottyTitleTextView.setTextColor(getResources().getColor(R.color.black));
                        lottyTitleTextView.setGravity(Gravity.CENTER);
                        lottyTitleTextView.setPadding(SizeUtils.dp2px(10), 0, SizeUtils.dp2px(10), 0);
                        lottyTitleTextView.setText(info.getName());
                        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
                        mContainerLottyTitle.addView(lottyTitleTextView, layoutParams);
                        lottyTitleTextView.setOnClickListener(FcTrendsInfoFragment.this);
                        if (info.getName().equals(selectTitle)) {
                            isFind = true;
                            mTextSelectTitle = lottyTitleTextView;
                            lottyTitleTextView.setTextSize(20);
                            lottyTitleTextView.setTextColor(getResources().getColor(R.color.red));
                            update(info);
                        }
                    }
                    //设置选中第一项
                    if(!isFind){
                        mTextSelectTitle =  (TextView) mContainerLottyTitle.getChildAt(0);
                        mTextSelectTitle.setTextSize(20);
                        mTextSelectTitle.setTextColor(getResources().getColor(R.color.red));
                        update(trendInfos.get(0));
                    }

                }
            }
        }, new Consumer<Throwable>() {
            @Override
            public void accept(Throwable throwable) throws Exception {
                mProgressLoad.setVisibility(View.GONE);
                ToastUtils.showToast("发生错误，请重试");
            }
        });
    }
}
