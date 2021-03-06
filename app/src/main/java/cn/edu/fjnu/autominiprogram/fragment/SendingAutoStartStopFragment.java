package cn.edu.fjnu.autominiprogram.fragment;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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
import cn.edu.fjnu.autominiprogram.utils.CommonUtils;
import momo.cn.edu.fjnu.androidutils.utils.StorageUtils;
import momo.cn.edu.fjnu.androidutils.utils.TextUtils;
import momo.cn.edu.fjnu.androidutils.utils.ToastUtils;

/**
 * Created by gaofei on 2018/4/1.
 * 自动启停页面
 */
@ContentView(R.layout.fragment_sending_auto_start_stop)
public class SendingAutoStartStopFragment extends AppBaseFragment implements CheckBox.OnCheckedChangeListener{

    @ViewInject(R.id.check_auto_start_stop)
    private CheckBox mCheckAutoStartStop;

    @ViewInject(R.id.edit_start_time)
    private EditText mEditStartTime;

    @ViewInject(R.id.edit_end_time)
    private EditText mEditEndTime;

    @ViewInject(R.id.btn_ok)
    private Button mBtnOK;
    /**
     * 之前是否开启自动启停
     */
    private boolean mIsOldOpenAutoStartStop;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return x.view().inject(this, inflater, container);
    }

    @Override
    public void init() {
        super.init();
        mIsOldOpenAutoStartStop = "true".equals(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.IS_AUTO_START_STOP));
        mCheckAutoStartStop.setChecked(mIsOldOpenAutoStartStop);
        mEditStartTime.setText(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.AUTO_SEND_START_TIME));
        mEditEndTime.setText(StorageUtils.getDataFromSharedPreference(ConstData.SharedKey.AUTO_SEND_END_TIME));

        mCheckAutoStartStop.setOnCheckedChangeListener(this);

        mEditStartTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(mEditStartTime);
            }
        });

        mEditEndTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(mEditEndTime);
            }
        });

        mBtnOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取当前是否勾选
                boolean isChecked = mCheckAutoStartStop.isChecked();
                boolean isNeedSave = isChecked == !mIsOldOpenAutoStartStop;
                if(isNeedSave){
                    //保存至数据库
                    StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.IS_AUTO_START_STOP, "" + isChecked);
                }
                if(isChecked){
                    String startTime = mEditStartTime.getText().toString();
                    String endTime = mEditEndTime.getText().toString();
                    if(TextUtils.isEmpty(startTime)){
                        ToastUtils.showToast(R.string.enter_start_time);
                        return;
                    }

                    if(TextUtils.isEmpty(endTime)){
                        ToastUtils.showToast(R.string.enter_stop_time);
                        return;
                    }
                    int startHour = Integer.parseInt(startTime.split(":")[0]);
                    int startMinute = Integer.parseInt(startTime.split(":")[1]);
                    int endHour = Integer.parseInt(endTime.split(":")[0]);
                    int endMinute = Integer.parseInt(endTime.split(":")[1]);
                    if((startHour * 60 + startMinute) >= (endHour * 60 + endMinute)){
                        ToastUtils.showToast(R.string.endtime_must_large_starttime);
                        return;
                    }
                    StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.AUTO_SEND_START_TIME, startTime);
                    StorageUtils.saveDataToSharedPreference(ConstData.SharedKey.AUTO_SEND_END_TIME, endTime);
                }
                ToastUtils.showToast(R.string.setting_success);
                getActivity().finish();
                if(isNeedSave){
                    //重启服务
                    CommonUtils.restartFloatingWindowService();
                }

            }
        });
        refreshView();
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

    private void refreshView(){
       // boolean isAutoStartStop = StorageUtils.getDataFromSharedPreference(ConstData.IntentKey.IS_AUTO_START_STOP, "" + )
        //mCheckAutoStartStop.setChecked();
        if(mCheckAutoStartStop.isChecked()){
            mEditStartTime.setEnabled(true);
            mEditEndTime.setEnabled(true);
        }else{
            mEditEndTime.setEnabled(false);
            mEditStartTime.setEnabled(false);
        }
    }


    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        switch (buttonView.getId()){
            case R.id.check_auto_start_stop:
                refreshView();
                break;
        }
    }
}
