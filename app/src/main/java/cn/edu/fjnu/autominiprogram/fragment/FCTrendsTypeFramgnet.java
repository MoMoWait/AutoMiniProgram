package cn.edu.fjnu.autominiprogram.fragment;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.activity.FcTrendsInfoActivity;
import cn.edu.fjnu.autominiprogram.adapter.FcTrendTypeAdapter;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.TrendInfo;
import cn.edu.fjnu.autominiprogram.data.ConstData;

/**
 * Created by gaofei on 2018/1/30.
 * 发彩网走势类型
 */
@ContentView(R.layout.fragment_fc_trends_type)
public class FCTrendsTypeFramgnet extends AppBaseFragment{
    @ViewInject(R.id.grid_trends_type)
    GridView mGridTrendsType;

    @ViewInject(R.id.progress_load)
    ProgressBar mProgressLoad;


    private TrendInfoLoadTask mLoadTask;

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
        mLoadTask = new TrendInfoLoadTask(this);
        mProgressLoad.setVisibility(View.VISIBLE);
        mLoadTask.execute();
    }

    private static class TrendInfoLoadTask extends AsyncTask<String, Integer, List<TrendInfo>>{

        private WeakReference<FCTrendsTypeFramgnet> mFragment;

        public TrendInfoLoadTask(FCTrendsTypeFramgnet framgnet){
            mFragment = new WeakReference<>(framgnet);
        }

        @Override
        protected List<TrendInfo> doInBackground(String... strings) {
            List<TrendInfo> trendInfos = new ArrayList<>();
            Set<String> titles = ConstData.FC_LOTTY_IMG_URLS.keySet();
            for(String title : titles){
                TrendInfo trendInfo = new TrendInfo();
                trendInfo.setName(title);
                trendInfo.setImgUrl(ConstData.FC_LOTTY_IMG_URLS.get(title));
                trendInfos.add(trendInfo);
            }
            return trendInfos;
        }

        @Override
        protected void onPostExecute(final List<TrendInfo> trendInfos) {
           final FCTrendsTypeFramgnet fragment = mFragment.get();
           if(fragment != null && fragment.getContext() != null && fragment.isCanUpadteUI()){
               fragment.mProgressLoad.setVisibility(View.GONE);
               FcTrendTypeAdapter fcTrendTypeAdapter = new FcTrendTypeAdapter(fragment.getContext(), R.layout.adapter_all_lottery, trendInfos);
               fragment.mGridTrendsType.setAdapter(fcTrendTypeAdapter);
               fragment.mGridTrendsType.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                   @Override
                   public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                       Intent intent = new Intent(fragment.getContext(), FcTrendsInfoActivity.class);
                       intent.putExtra(ConstData.IntentKey.LOTTERY_NAME, trendInfos.get(position).getName());
                       fragment.startActivity(intent);
                   }
               });
           }
        }
    }
}
