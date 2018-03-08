package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.HistoryOpenFragment;

/**
 * Created by gaofei on 2018/2/3.
 * 历史开奖页面
 */

public class HistoryOpenActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return new HistoryOpenFragment();
    }
}
