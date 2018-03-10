package cn.edu.fjnu.autominiprogram.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;

import org.json.JSONArray;
import org.json.JSONObject;
import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.adapter.ColorAdapter;
import cn.edu.fjnu.autominiprogram.adapter.ColorTypeAdapter;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.bean.ColorInfo;
import cn.edu.fjnu.autominiprogram.bean.ColorType;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by gaofei on 2017/9/9.
 * 首页
 */
@ContentView(R.layout.fragment_home)
public class HomeFragment extends AppBaseFragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }


}
