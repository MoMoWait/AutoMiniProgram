package cn.edu.fjnu.autominiprogram.activity;

import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.SuggestionReplyFragment;

/**
 * Created by gaofei on 2018/4/2.
 * 意见回复页面
 */

public class SuggestionReplyActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new SuggestionReplyFragment();
    }
}
