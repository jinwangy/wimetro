package com.wimetro.qrcode.ui.fragment;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;

import com.ToxicBakery.viewpager.transforms.ABaseTransformer;
import com.ToxicBakery.viewpager.transforms.AccordionTransformer;
import com.bigkoo.convenientbanner.ConvenientBanner;
import com.bigkoo.convenientbanner.holder.CBViewHolderCreator;
import com.bigkoo.convenientbanner.holder.Holder;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.base.BaseFragment;
import com.wimetro.qrcode.common.utils.WLog;

import java.util.Arrays;
import java.util.List;

/**
 * jwyuan on 2018-3-9 12:58.
 */

public class HomeFragment extends BaseFragment {
    private String TAG = HomeFragment.class.getSimpleName();

    private Integer[] imgArr = new Integer[]{ R.drawable.baner_01, R.drawable.baner_02, R.drawable.baner_03};//3张轮播图;
    private List<Integer> localImages = Arrays.asList(imgArr);
    private ConvenientBanner convenientBanner;

    @Override
    protected String getLogTag() {
        return TAG;
    }

    @Override
    protected int setContentView() {
        return R.layout.fragment_home;
    }

    @Override
    protected void initData(View view) {
        if(view == null) return;
        convenientBanner = (ConvenientBanner) view.findViewById(R.id.convenientBanner);
        //开始自动翻页
        convenientBanner.setPages(new CBViewHolderCreator() {
            @Override
            public Object createHolder() {
                return new LocalImageHolderView();
            }
        },localImages)
                //设置指示器是否可见
                .setPointViewVisible(true)
                //设置两个点图片作为翻页指示器，不设置则没有指示器，可以根据自己需求自行配合自己的指示器,不需要圆点指示器可用不设
                .setPageIndicator(new int[]{R.drawable.dot_unselect_shape, R.drawable.dot_select_shape})
                //设置指示器的方向（左、中、右）
                .setPageIndicatorAlign(ConvenientBanner.PageIndicatorAlign.ALIGN_PARENT_RIGHT)
                //设置点击监听事件
                // .setOnItemClickListener(this)
                //设置手动影响（设置了该项无法手动切换）
                .setManualPageable(true);
        try {
            Class cls = Class.forName("com.ToxicBakery.viewpager.transforms." + AccordionTransformer.class.getSimpleName());
            ABaseTransformer transforemer= (ABaseTransformer)cls.newInstance();
            convenientBanner.getViewPager().setPageTransformer(true,transforemer);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (java.lang.InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private void startTurning() {
        if(convenientBanner != null && !convenientBanner.isTurning()) {
            WLog.e(TAG,"startTurning");
            convenientBanner.startTurning(5000);
        }
    }

    private void stopTurning() {
        if(convenientBanner != null && convenientBanner.isTurning()) {
            WLog.e(TAG,"stopTurning");
            convenientBanner.stopTurning();
        }
    }

    @Override
    protected void lazyLoad() {
        super.lazyLoad();
        startTurning();
    }

    @Override
    public void onResume() {
        super.onResume();
        startTurning();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopTurning();
    }

    @Override
    protected void onInvisible() {
        stopTurning();
    }

    public class LocalImageHolderView implements Holder<Integer> {
        private ImageView imageView;
        @Override
        public View createView(Context context) {
            imageView = new ImageView(context);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            return imageView;
        }

        @Override
        public void UpdateUI(Context context, final int position, Integer data) {
            imageView.setImageResource(data);
        }
    }
}
