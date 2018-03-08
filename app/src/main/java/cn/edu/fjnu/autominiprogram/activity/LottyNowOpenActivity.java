package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.NowOpenFragment;

/**
 * Created by gaofei on 2018/2/7.
 * 彩票最新开奖页面
 */

public class LottyNowOpenActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new NowOpenFragment();
    }
}
