package com.wimetro.qrcode.ui.view;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wimetro.qrcode.R;
import com.otech.yoda.ui.TopBar;

public class MyTopBar extends TopBar {

    public static final int RIGHT_VIEW_ID = R.id.top_right_view;
    public static final int CENTER_VIEW_ID = R.id.top_center_view;
    public static final int LEFT_VIEW_ID = R.id.top_left_btn;

    private Activity activity;

    public MyTopBar(Activity activity) {
        super(activity, RIGHT_VIEW_ID, CENTER_VIEW_ID, LEFT_VIEW_ID);
        this.activity = activity;
        setBacklistener();
    }

    private void setBacklistener() {
        LinearLayout ll =(LinearLayout) getLeftView().findViewById(R.id.top_left_btn);
        ll.setOnClickListener(new OnClickListener(){
            @Override
            public void onClick(View view) {
                MyTopBar.this.activity.finish();
            }
        });
    }

    public void setLeftViewVisible(boolean visible) {
        LinearLayout ll =(LinearLayout) getLeftView().findViewById(R.id.top_left_btn);
        if(visible) {
            ll.setVisibility(View.VISIBLE);
        } else {
            ll.setVisibility(View.GONE);
        }
    }

    public void setHeaderBgColor(int headerColor) {
        RelativeLayout rl = (RelativeLayout) activity.findViewById(R.id.header_rl);
        rl.setBackgroundColor(headerColor);
    }

    public void setHeaderTitleColor(int titleColor) {
        TextView view = (TextView) getCenterView();
        view.setTextColor(titleColor);
    }

    public void setupRightView(int iconResId, OnClickListener l) {
        View view = getRightView();
        if (view != null && view instanceof ImageButton) {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(l);
            ((ImageButton) view).setImageResource(iconResId);
           
        } else  if (view != null && view instanceof Button) {
            view.setVisibility(View.VISIBLE);
            view.setOnClickListener(l);          
        }
    }

}
