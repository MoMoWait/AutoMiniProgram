package cn.edu.fjnu.autominiprogram.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;

import org.xutils.view.annotation.ContentView;
import org.xutils.view.annotation.ViewInject;
import org.xutils.x;
import java.util.Calendar;
import java.util.Locale;
import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.base.AppBaseFragment;
import cn.edu.fjnu.autominiprogram.data.ConstData;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.TextUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

/**
 * Created by gaofei on 2018/4/2.
 * 问候语
 */
@ContentView(R.layout.fragment_hello)
public class HelloFragment extends AppBaseFragment {

    @ViewInject(R.id.edit_start_time)
    private EditText mEditStartTime;
    @ViewInject(R.id.edit_hello_content)
    private EditText mEditHelloContent;
    @ViewInject(R.id.btn_ok)
    private Button mBtnOK;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        mEditStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(mEditStartTime);
            }
        });

        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String helloContnet = mEditHelloContent.getText().toString().trim();
                String helloTime = mEditStartTime.getText().toString();
                if(TextUtils.isEmpty(helloTime)){
                    ToastUtils.showToast(R.string.enter_hello_time);
                    return;
                }
                if(TextUtils.isEmpty(helloContnet)){
                    ToastUtils.showToast(R.string.enter_hello_tip);
                    return;
                }
                StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.HELLO_TIME, helloTime);
                StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.HELLO_CONTENT, helloContnet);
                ToastUtils.showToast(R.string.setting_success);
                getActivity().finish();

            }
        });
    }

    private void showTimePickerDialog(final EditText editText){
        final Calendar c = Calendar.getInstance();
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                editText.setText(String.format(Locale.US, "%02d:%02d", hourOfDay, minute));
            }
        }, hour, minute, true).show();
    }
}
