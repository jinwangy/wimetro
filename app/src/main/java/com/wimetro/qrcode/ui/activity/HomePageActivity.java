package com.wimetro.qrcode.ui.activity;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTabHost;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TabWidget;
import android.widget.TextView;

import com.wimetro.qrcode.R;
import com.wimetro.qrcode.adapter.TabFragmentPagerAdapter;
import com.wimetro.qrcode.common.utils.ActivityCollector;
import com.wimetro.qrcode.common.utils.WLog;
import com.wimetro.qrcode.ui.fragment.BlankFragment;
import com.wimetro.qrcode.ui.fragment.HomeFragment;
import com.wimetro.qrcode.ui.fragment.MeFragment;
import com.wimetro.qrcode.ui.fragment.RideFragment;
import com.wimetro.qrcode.ui.view.MyTopBar;

import java.util.ArrayList;
import java.util.List;

/**
 * jwyuan on 2018-3-8 16:18.
 */

public class HomePageActivity extends FragmentActivity implements
        ViewPager.OnPageChangeListener, FragmentTabHost.OnTabChangeListener {
    private String TAG = HomePageActivity.class.getSimpleName();

    public FragmentTabHost mTabHost;
    public ViewPager mViewPager;

    private Class fragmentArray[] = { HomeFragment.class, RideFragment.class,MeFragment.class };
    private int imageViewArray[] = { R.drawable.tab_homepage_shape, R.drawable.tab_ride_shape,R.drawable.tab_my_shape };
    private String textViewArray[] = { "首页", "乘车","我的"};
    private String titleArray[] = { "Metro新时代", "乘车","个人中心"};
    private List<Fragment> list = new ArrayList<Fragment>();

    private MyTopBar mTopBar;
    private static final int LOGOUT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WLog.e(TAG,"onCreate");

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.home_page);

        if(getRequestedOrientation() != ActivityInfo.SCREEN_ORIENTATION_PORTRAIT){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        ActivityCollector.addActivity(this);

        mTopBar = new MyTopBar(this);
        mTopBar.setLeftViewVisible(false);
        mTopBar.setupRightView(R.drawable.sz_1,new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(HomePageActivity.this,SettingsActivity.class),LOGOUT);
            }
        });

        initViewPager();
        initTabHost();
    }

    @Override
    protected void onStart() {
        super.onStart();
        WLog.e(TAG,"onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        WLog.e(TAG,"onResume");
    }

    private void initTabHost() {
        mTabHost = (FragmentTabHost)findViewById(android.R.id.tabhost);
        mTabHost.setup(this,getSupportFragmentManager(), R.id.viewpager);
        mTabHost.setOnTabChangedListener(this);

        int count = textViewArray.length;
        /*新建Tabspec选项卡并设置Tab菜单栏的内容和绑定对应的Fragment*/
        for (int i = 0; i < count; i++) {
            // 给每个Tab按钮设置标签、图标和文字
            TabHost.TabSpec tabSpec = mTabHost.newTabSpec("tab" + (i + 1)).setIndicator(getTabItemView(textViewArray[i],imageViewArray[i]));
            // 将Tab按钮添加进Tab选项卡中，并绑定Fragment
            mTabHost.addTab(tabSpec, BlankFragment.class, null);
        }
    }

    private void initViewPager() {
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mViewPager.addOnPageChangeListener(this);//设置页面切换时的监听器

        list.add(new HomeFragment());
        list.add(new RideFragment());
        list.add(new MeFragment());

        //绑定Fragment适配器
        mViewPager.setAdapter(new TabFragmentPagerAdapter(getSupportFragmentManager(), list));
    }

    private View getTabItemView(String text,int image_id) {
        View tabView=(View) LayoutInflater.from(this).inflate(R.layout.tab_item, null);

        ImageView imageView = (ImageView) tabView.findViewById(R.id.tab_icon);
        imageView.setImageResource(image_id);
        TextView textView = (TextView) tabView.findViewById(R.id.tab_text);
        textView.setText(text);

        return tabView;
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {//arg0 ==1的时候表示正在滑动，arg0==2的时候表示滑动完毕了，arg0==0的时候表示什么都没做，就是停在那
    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {//表示在前一个页面滑动到后一个页面的时候，在前一个页面滑动前调用的方法
    }

    @Override
    public void onPageSelected(int arg0) {//arg0是表示你当前选中的页面位置Postion，这事件是在你页面跳转完毕的时候调用的。
        TabWidget widget = mTabHost.getTabWidget();
        widget.setDescendantFocusability(ViewGroup.FOCUS_BLOCK_DESCENDANTS);//设置View覆盖子类控件而直接获得焦点
        mTabHost.setCurrentTab(arg0);//根据位置Postion设置当前的Tab
        mTopBar.setTitleText(titleArray[arg0]);
        //mTopBar.setHeaderBgColor(arg0 == 0 ? Color.WHITE:Color.parseColor("#00a2e7"));
        //mTopBar.setHeaderTitleColor(arg0 == 0 ? Color.BLACK:Color.WHITE);
        mTopBar.setHeaderBgColor(arg0 == 0 ? Color.parseColor("#00a2e7"):Color.parseColor("#00a2e7"));
        mTopBar.setHeaderTitleColor(arg0 == 0 ? Color.WHITE:Color.WHITE);
        mTopBar.getRightView().setVisibility(arg0 == 2 ?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    public void onTabChanged(String tabId) {//Tab改变的时候调用
        int position = mTabHost.getCurrentTab();
        mViewPager.setCurrentItem(position);//把选中的Tab的位置赋给适配器，让它控制页面切换
        mTopBar.setTitleText(titleArray[position]);
        //mTopBar.setHeaderBgColor(position == 0 ? Color.WHITE:Color.parseColor("#00a2e7"));
        //mTopBar.setHeaderTitleColor(position == 0 ? Color.BLACK:Color.WHITE);
        mTopBar.setHeaderBgColor(position == 0 ? Color.parseColor("#00a2e7"):Color.parseColor("#00a2e7"));
        mTopBar.setHeaderTitleColor(position == 0 ? Color.WHITE:Color.WHITE);
        mTopBar.getRightView().setVisibility(position == 2 ?View.VISIBLE:View.INVISIBLE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        WLog.e(TAG,"onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        WLog.e(TAG,"onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        WLog.e(TAG,"onDestroy");
        if(mTopBar != null) {
            mTopBar = null;
        }
        ActivityCollector.removeActivity(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // 根据上面发送过去的请求吗来区别
        switch (resultCode) {
            case LOGOUT:
                LoginActivity.startThisAct(this);
                finish();
                break;
            default:
                break;
        }
    }
}
