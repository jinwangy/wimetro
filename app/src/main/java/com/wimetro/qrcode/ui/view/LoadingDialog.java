package com.wimetro.qrcode.ui.view;

import android.app.Activity;
import android.app.Dialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import com.wimetro.qrcode.R;


/**
 * 正在载入对话框，like ProgressDialog
 *
 * @author lds
 */
public class LoadingDialog {

    public static Dialog show(Activity context) {
        final Dialog dialog = new Dialog(context,
                android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.core_center_loading);
        if (context != null && !context.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

    public static Dialog show(Activity context, int text) {
        return show(context, context.getString(text));
    }

    public static Dialog show(Activity context, String text) {
        final Dialog dialog = new Dialog(context,
                android.R.style.Theme_Translucent);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.core_center_loading, null);
        TextView tv = (TextView) view
                .findViewById(R.id.core_center_loading_text);
        tv.setText(text);
        dialog.setContentView(view);
        if (context != null && !context.isFinishing()) {
            dialog.show();
        }
        return dialog;
    }

}
