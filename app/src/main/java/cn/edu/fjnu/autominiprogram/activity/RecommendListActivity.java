package cn.edu.fjnu.autominiprogram.activity;
import android.support.v4.app.Fragment;

import cn.edu.fjnu.autominiprogram.fragment.RecommendListFragment;

public class RecommendListActivity extends SingleFragmentActivity {
    @Override
    public Fragment createFragment() {
        return new RecommendListFragment();
    }
}
