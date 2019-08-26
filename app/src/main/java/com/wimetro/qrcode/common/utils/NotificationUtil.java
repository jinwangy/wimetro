package com.wimetro.qrcode.common.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;

import com.wimetro.qrcode.R;

/**
 * jwyuan on 2017-12-4 15:19.
 */

public class NotificationUtil {
    private Context context;
    private NotificationManager notificationManager;

    public NotificationUtil(Context context) {
        this.context = context;
        notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }

    /**
     * 普通的Notification
     */
    public void postNotification(String title,String content) {
        Notification.Builder builder = new Notification.Builder(context);
//        Intent intent = new Intent(context, MainActivity.class);  //需要跳转指定的页面
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        builder.setContentIntent(pendingIntent);
        builder.setSmallIcon(R.mipmap.ic_launcher);// 设置图标
        //builder.setLargeIcon(R.drawable.cloud_pay);
        builder.setContentTitle(title);// 设置通知的标题
        builder.setContentText(content);// 设置通知的内容
        builder.setWhen(System.currentTimeMillis());// 设置通知来到的时间
        builder.setAutoCancel(true); //自己维护通知的消失
        builder.setTicker("您有新的消息,请注意查收!");// 第一次提示消失的时候显示在通知栏上的
        //builder.setOngoing(true);
        builder.setDefaults(Notification.DEFAULT_ALL);

        Notification notification = builder.build();
        notification.flags = Notification.FLAG_AUTO_CANCEL;
        notificationManager.notify(0,notification);
    }

    /**
     * 使用下载的Notification,在4.0以后才能使用<p></p>
     * Notification.Builder类中提供一个setProgress(int max,int progress,boolean indeterminate)方法用于设置进度条，
     * max用于设定进度的最大数，progress用于设定当前的进度，indeterminate用于设定是否是一个确定进度的进度条。
     * 通过indeterminate的设置，可以实现两种不同样式的进度条，一种是有进度刻度的（true）,一种是循环流动的（false）。
     */
    public void postDownloadNotification() {
        final Notification.Builder builder = new Notification.Builder(context);
        builder.setSmallIcon(R.mipmap.ic_launcher)
                .setTicker("showProgressBar").setContentInfo("contentInfo")
                .setOngoing(true).setContentTitle("ContentTitle")
                .setContentText("ContentText");
        // 模拟下载过程
        new Thread(new Runnable() {
            @Override
            public void run() {
                int progress ;
                for (progress = 0; progress < 100; progress += 5) {
                    // 将setProgress的第三个参数设为true即可显示为无明确进度的进度条样式
                    builder.setProgress(100, progress, false);
                    notificationManager.notify(0, builder.build());
                    try {
                        Thread.sleep(1 * 1000);
                    } catch (InterruptedException e) {
                        System.out.println("sleep failure");
                    }
                }
                builder.setContentTitle("Download complete")
                        .setProgress(0, 0, false).setOngoing(false);
                notificationManager.notify(0, builder.build());
            }
        }).start();
    }

    /**
     * 大视图通知在4.1以后才能使用，BigTextStyle<p></p>
     * ****************************************************<p></p>
     * Helper class for generating large-format notifications that include a lot of text.
     *
     * Here's how you'd set the <code>BigTextStyle</code> on a notification:
     * <pre class="prettyprint">
     * Notification notif = new Notification.Builder(mContext)
     *     .setContentTitle(&quot;New mail from &quot; + sender.toString())
     *     .setContentText(subject)
     *     .setSmallIcon(R.drawable.new_mail)
     *     .setLargeIcon(aBitmap)
     *     .setStyle(new Notification.BigTextStyle()
     *         .bigText(aVeryLongString))
     *     .build();
     * </pre>
     *
     * @see Notification#bigContentView
     */
    public void postBigTextNotification() {
        Notification.BigTextStyle textStyle = new Notification.BigTextStyle();
        textStyle.setBigContentTitle("大标题")
                // 标题
                .setSummaryText("SummaryText")
                .bigText("Helper class for generating large-format notifications" +
                        " that include a lot of text;  !!!!!!!!!!!" +
                        "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
        Notification.Builder builder2 = new Notification.Builder(
                context);
        builder2.setSmallIcon(R.mipmap.ic_launcher);// 小图标
        // 大图标
        builder2.setLargeIcon(BitmapFactory.decodeResource(
                context.getResources(), R.mipmap.ic_launcher));  //R.mipmap.close
        builder2.setTicker("showBigView_Text")
                .setContentInfo("contentInfo");
        builder2.setStyle(textStyle);
        builder2.setAutoCancel(true);

        notificationManager.notify(0, builder2.build());
    }

    /**
     * 大布局通知在4.1以后才能使用,大布局图片
     */
    public void postBigPictureNotification() {
//        Notification.BigPictureStyle bigPictureStyle = new Notification.BigPictureStyle();
//        bigPictureStyle.bigPicture(BitmapFactory.decodeResource(context.getResources(),
//                R.mipmap.ic_launcher));  //R.drawable.back
//
//        Notification.Builder builder = new Notification.Builder(
//                context);
//        builder.setSmallIcon(R.mipmap.ic_launcher);// 小图标
//        // 大图标
//        builder.setLargeIcon(BitmapFactory.decodeResource(
//                context.getResources(), R.drawable.ic_stop));
//        builder.setTicker("showBigView_Picture")
//                .setContentInfo("contentInfo");
//        builder.setStyle(bigPictureStyle);
//        builder.setAutoCancel(true);
//
//        notificationManager.notify(0, builder.build());
    }

    /**
     * 大布局通知在4.1以后才能使用，InboxStyle
     */
    public void postInboxNotification() {
//        Notification.InboxStyle inboxStyle = new Notification.InboxStyle();
//        inboxStyle.setBigContentTitle("InboxStyle");
//        inboxStyle.setSummaryText("Test");
//        for(int i = 0 ; i < 10; i++){
//            inboxStyle.addLine("new:" + i);
//        }
//
//        Notification.Builder builder5 = new Notification.Builder(
//                context);
//        builder5.setSmallIcon(R.mipmap.ic_launcher);// 小图标
//        // 大图标
//        builder5.setLargeIcon(BitmapFactory.decodeResource(
//                context.getResources(), R.drawable.ic_stop));
//        builder5.setTicker("showBigView_InboxStyle")
//                .setContentInfo("contentInfo");
//        builder5.setStyle(inboxStyle);
//        builder5.setAutoCancel(true);
//
//        notificationManager.notify(0, builder5.build());
    }

    /**
     * 自定义通知<p></p>
     *
     * 不设置notification.contentIntent = pendingIntent;则报如下异常：
     * android.app.RemoteServiceException:
     * Bad notification posted from package com.test.testandroid: Couldn't expand RemoteViews for: StatusBarNotification(
     * pkg=com.test.testandroid user=UserHandle{0} id=0 tag=null score=0 key=0|com.test.testandroid|0|null|10168|0: Notification
     * (pri=0 contentView=com.test.testandroid/0x7f040038 vibrate=null sound=null defaults=0x0 flags=0x10 color=0xff00aeff vis=PRIVATE))
     */
    public void postCustomNotification() {
//        RemoteViews contentViews = new RemoteViews(context.getPackageName(),
//                R.layout.mynotification);
//        contentViews.setImageViewResource(R.id.imageNotifi,R.mipmap.ic_launcher);
//        contentViews.setTextViewText(R.id.titleTV,"自定义通知标题");
//        contentViews.setTextViewText(R.id.textTV,"自定义通知内容");
//
//        Intent intent = new Intent(context, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        contentViews.setOnClickPendingIntent(R.id.titleTV, pendingIntent);
//        contentViews.setOnClickPendingIntent(R.id.textTV, PendingIntent.getActivity(context, 0,
//                new Intent(context, ScrollingActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
//
//        Notification.Builder builder = new Notification.Builder(context);
//        builder.setSmallIcon(R.mipmap.ic_launcher);
//        builder.setContentTitle("custom notification");
//        builder.setContentText("custom test");
//        builder.setTicker("custom ticker");
//        builder.setAutoCancel(true);
//        builder.setContent(contentViews);
//
//        notificationManager.notify(0,builder.build());
    }

    public void cancelById() {
        notificationManager.cancel(0);  //对应NotificationManager.notify(id,notification);第一个参数
    }

    public void cancelAllNotification() {
        notificationManager.cancelAll();
    }
}
