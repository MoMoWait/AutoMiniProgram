package cn.edu.fjnu.autominiprogram.task;

import android.os.AsyncTask;

import java.util.List;

import cn.edu.fjnu.autominiprogram.bean.TrendInfo;

/**
 * Created by gaofei on 2018/1/31.
 * 获取走势信息
 */

public class TrendInfoLoadTask extends AsyncTask<String, Integer, List<TrendInfo>> {
    @Override
    protected List<TrendInfo> doInBackground(String... strings) {
        return null;
    }

    @Override
    protected void onPostExecute(List<TrendInfo> trendInfos) {
        super.onPostExecute(trendInfos);
    }
}
