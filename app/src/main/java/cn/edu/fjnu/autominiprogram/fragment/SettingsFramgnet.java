package cn.edu.fjnu.autominiprogram.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
/**
 * Created by gaofei on 2018/3/8.
 * 设置页面
 */

@ContentView(R.layout.fragment_settings)
public class SettingsFramgnet extends AppBaseFragment {

    @ViewInject(R.id.img_head_photo)
    private ImageView mImgHeadPhoto;

    @ViewInject(R.id.layout_about)
    private LinearLayout mLayoutAbout;

    @ViewInject(R.id.layout_suggestion)
    private LinearLayout mLayoutSuggestion;

    @ViewInject(R.id.layout_information)
    private LinearLayout mLayoutInformation;

    @ViewInject(R.id.layout_update)
    private LinearLayout mLayoutUpdate;

    @ViewInject(R.id.text_login)
    private TextView mTextLogin;

    private boolean mIsLogin;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }


    @Override
    public void init(){

    }


}
