package com.wimetro.qrcode.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.wimetro.qrcode.R;
import com.otech.yoda.widget.BaseListAdapter;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.greendao.entity.StationCache;

/**
 * jwyuan on 2017/5/19 14:56.
 */

public class PullableAdapter extends BaseListAdapter<StationCache> {

    private Context mContext;

    public PullableAdapter(Context context) {
        super(context);
        this.mContext = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            convertView= LayoutInflater.from(mContext).inflate(R.layout.home_listview_item, null);
            holder = new ViewHolder();

            holder.type_item = (TextView) convertView.findViewById(R.id.trade_item_type);
            holder.time_item = (TextView) convertView.findViewById(R.id.trade_item_time);
            holder.amount_item = (TextView) convertView.findViewById(R.id.trade_item_amount);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        final StationCache gt = mList.get(position);
        WLog.e("PullableAdapter","station = " + gt);
        if(gt == null) return convertView;

        holder.type_item.setText(getTradeType(gt.getDeal_type(),gt.getDeal_station()));
        holder.time_item.setText(getTradeTime(gt.getDeal_time()));
        holder.amount_item.setText(getTradeAmount(gt.getDeal_amount()));

        return convertView;
    }

    private String getTradeType(String type,String station) {
        if(type.equals("26")) {
            return "进站\n(" + station + ")";
        } else if(type.equals("27")) {
            return "出站\n(" + station + ")";
        } else if(type.equals("17")) {
            return "更新\n(" + station + ")";
        } else {
            return "";
        }
    }

    private String getTradeTime(String time) {
        if(time == null || time.equals("") || time.length() != 14)
            return "";
        else
            return time.substring(0, 4)+"-"+time.substring(4, 6)+"-"+time.substring(6, 8)+
                    " "+time.substring(8, 10)+":"+time.substring(10, 12)+":"+time.substring(12, 14);
    }

    private String getTradeAmount(String amount) {
        String balanceStr = "";
        try {
            int balanceInt = Integer.parseInt(amount.trim());
            balanceStr = String.format("%d.%02d", balanceInt/100, balanceInt%100);
        }catch(Exception e) {
            System.out.println("getBalance got exception!");
            e.printStackTrace();
        }
        if(TextUtils.isEmpty(balanceStr)) {
            return "未知";
        } else {
            return balanceStr + "元";
        }
    }

    public class ViewHolder {
        TextView type_item;
        TextView time_item;
        TextView amount_item;
    }

}


