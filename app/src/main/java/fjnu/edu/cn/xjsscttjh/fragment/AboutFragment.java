package fjnu.edu.cn.xjsscttjh.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.xutils.view.annotation.ContentView;
import org.xutils.x;

import fjnu.edu.cn.xjsscttjh.R;
import fjnu.edu.cn.xjsscttjh.base.AppBaseFragment;
import momo.cn.edu.fjnu.androidutils.base.BaseFragment;

/**
 * Created by Administrator on 2017\9\4 0004.
 * 关于我们
 */
@ContentView(R.layout.fragment_about)
public class AboutFragment extends AppBaseFragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
    }
}
