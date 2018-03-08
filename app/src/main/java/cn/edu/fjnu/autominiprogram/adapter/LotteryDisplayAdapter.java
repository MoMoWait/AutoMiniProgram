package cn.edu.fjnu.autominiprogram.adapter;

import android.content.Context;
import android.support.annotation.IdRes;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.List;

import cn.edu.fjnu.autominiprogram.R;
import cn.edu.fjnu.autominiprogram.bean.ColorInfo;
import momo.cn.edu.fjnu.androidutils.utils.SizeUtils;

/**
 * Created by Administrator on 2017\9\5 0005.
 * 开奖列表适配器
 */

public class LotteryDisplayAdapter extends ArrayAdapter<ColorInfo> {
    //private List<AllColorInfo> mObjects;
    private LayoutInflater mInflater;
    private int mResourceId;
    private Context mContext;
    public LotteryDisplayAdapter(@NonNull Context context, @LayoutRes int resource, @IdRes int textViewResourceId, @NonNull List<ColorInfo> objects) {
        super(context, resource, textViewResourceId, objects);
       // mObjects = objects;
        mResourceId = resource;
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View itemView = null;
        if(convertView != null){
            itemView = convertView;
        }else{
            itemView = mInflater.inflate(mResourceId, parent, false);
        }
        ColorInfo itemColorInfo = getItem(position);
        TextView oneColorText = (TextView) itemView.findViewById(R.id.text_time);
        TextView twoColorText = (TextView) itemView.findViewById(R.id.text_issueno);
        LinearLayout layoutLuckyNumber = (LinearLayout) itemView.findViewById(R.id.layout_lucky_number);
        if(itemColorInfo != null){
            oneColorText.setText(itemColorInfo.getOpenDate());
            twoColorText.setText(itemColorInfo.getIssueNo());
            oneColorText.setText("第" + itemColorInfo.getIssueNo() + "期" + " " + itemColorInfo.getOpenDate());
            //itemColorInfo.getOpenDate()
            //threeColorText.setText("开奖号：" + itemColorInfo.getNumber());
            layoutLuckyNumber.removeAllViews();
            String[] numbers = itemColorInfo.getNumber().split("\\s+");
            for(String itemNumber: numbers){
                TextView luckyNumberView = new TextView(mContext);
                if(position == 0) {
                    luckyNumberView.setBackgroundResource(R.drawable.luck_number_back_red);
                    luckyNumberView.setTextColor(mContext.getResources().getColor(R.color.white));
                }else{
                    luckyNumberView.setBackgroundResource(R.drawable.luck_number_back_red_ring);
                    luckyNumberView.setTextColor(mContext.getResources().getColor(R.color.black));
                }
                luckyNumberView.setGravity(Gravity.CENTER);
                luckyNumberView.setText(itemNumber);
                luckyNumberView.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 20);

                layoutLuckyNumber.addView(luckyNumberView);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)luckyNumberView.getLayoutParams();
                params.leftMargin = SizeUtils.dp2px(10);
            }
        }

        return itemView;

    }
}
