package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;
import cn.edu.fjnu.autominiprogram.fragment.LotteryDisplayFragment;

/**
 * Created by gaofei on 2017/11/30.
 * 开奖显示页面
 */

public class LotteryDisplayActivity extends SingleFragmentActivity{
    @Override
    public Fragment createFragment() {
        return new LotteryDisplayFragment();
    }
}
