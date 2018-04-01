package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.SendingGroupFragment;

/**
 * Created by gaofei on 2018/4/1.
 * 发单群数
 */

public class SettingSendGroupActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new SendingGroupFragment();
    }
}
