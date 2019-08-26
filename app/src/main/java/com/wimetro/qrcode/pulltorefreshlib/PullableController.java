package com.wimetro.qrcode.pulltorefreshlib;

import android.app.Activity;
import android.os.AsyncTask;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.otech.yoda.utils.Pager;
import com.otech.yoda.widget.BaseListAdapter;
import com.wimetro.qrcode.R;
import com.wimetro.qrcode.common.utils.WLog;

import java.util.Collections;
import java.util.List;

/**
 * jwyuan on 2017-9-30 14:08.
 */

public class PullableController<T>  implements PullToRefreshLayout.OnRefreshListener {
    /**
     * 回调方法
     *
     * @param <T>
     *            数据类型
     */
    public interface Callback<T> {

        /**
         * 载入缓存（非UI线程）
         *
         * @param pager
         */
        List<T> onLoadCache(Pager pager);

        /**
         * 载入新数据
         *
         * @param pager
         */
        void onLoadData(Pager pager);

        /**
         * 缓存新数据（非UI线程）
         *
         * @param data
         */
        void onSaveData(List<T> data);

    }

    protected final Activity mActivity;
    protected final PullToRefreshLayout pullToRefreshLayout;
    protected final PullableListView mListView;
    protected final BaseListAdapter<T> mAdapter;
    protected final View mFooter;

    protected Callback<T> mCallback;
    protected Pager mPager = new Pager(1,6);

    protected LoadCacheTask mLoadCacheTask;
    protected SaveCacheTask mSaveCacheTask;

    private View mEmptyView;
    private boolean mOpenFooter;

    /**
     * 是否使用缓存（载入/保存缓存）
     */
    private boolean mUseCache = true;

    public PullableController(Activity activity, PullToRefreshLayout pullToRefreshLayout, PullableListView listView,BaseListAdapter<T> adapter,View footer,boolean openFooter) {
        if (activity == null) {
            throw new IllegalArgumentException("arg[0] activity cann't be null");
        }
        if (pullToRefreshLayout == null) {
            throw new IllegalArgumentException(
                    "arg[1] pullToRefreshLayout cann't be null");
        }
        if (listView == null) {
            throw new IllegalArgumentException(
                    "arg[2] pullableListView cann't be null");
        }
        if (adapter == null) {
            throw new IllegalArgumentException("arg[3] adapter cann't be null");
        }

        this.mActivity = activity;
        this.mAdapter = adapter;
        this.mListView = listView;
        this.mFooter = footer;
        this.pullToRefreshLayout = pullToRefreshLayout;
        this.pullToRefreshLayout.setOnRefreshListener(this);

        this.mOpenFooter = openFooter;

        setupEmptyView();
    }

    public void setPageSize(int size) {
        mPager = new Pager(1,size);
    }

    /**
     * 载入数据
     */
    public void initData() {
        initData(true);
    }

    /**
     * 载入数据
     *
     * @param showRefreshing
     *            是否显示下拉刷新条
     */
    public void initData(boolean showRefreshing) {
        onInitData(showRefreshing);
    }

    /**
     * Step 1. 开始加载
     */
    public void onInitData(boolean showRefreshing) {
        if (mUseCache) {
            mPager.resetPage();
            mLoadCacheTask = new LoadCacheTask();
            mLoadCacheTask.execute();
        } else { // 不使用缓存，是否请求数据
            onRefresh(null);
        }
    }

    /**
     * Step 2. 载入缓存 NOTE: 非UI线程
     */
    public List<T> onLoadCache() {
        if (mCallback != null) {
            return mCallback.onLoadCache(mPager);
        }
        return null;
    }

    /**
     * Step 3. 刷新页面
     *
     * @param list
     *            新数据
     */
    @SuppressWarnings("unchecked")
    public void onRefreshUI(List<T> list) {
        onRefreshUI(list, true);
    }

    /**
     * Step 3. 刷新页面
     *
     * @param list
     *            新数据
     * @param isNotCache
     *            true是网络数据，false是缓存数据
     */
    @SuppressWarnings("unchecked")
    public void onRefreshUI(List<T> list, boolean isNotCache) {
        if (list == null || list.size() == 0) {
            if (mPager.pageNumber == 1) {
                mListView.setBackgroundResource(R.drawable.no_records);
            }
            list = Collections.emptyList();
            if(mFooter != null) mFooter.setVisibility(View.GONE);
        } else {
            mListView.setBackgroundResource(0);
            if(list.size() >= 6 && mOpenFooter) {
                if(mFooter != null) mFooter.setVisibility(View.VISIBLE);
            } else {
                if(mFooter != null) mFooter.setVisibility(View.GONE);
            }
        }

        mAdapter.refresh(list, mPager.mAppend);

        // 缓存数据, 如果数据不为空(第一页除外，因为第一页可能因为删除数据而为空，本地因该也删除缓存）
        if (mUseCache && isNotCache && (!list.isEmpty() || isFirstPage())) {
            //mSaveCacheTask = new SaveCacheTask();
            //mSaveCacheTask.execute(list);
        }
    }

    private boolean isFirstPage() {
        return mPager.pageNumber == 1;
    }

    private void setPullMode(boolean hasMore) {
    }

    /**
     * setEmptyView for adapterView
     *
     * @see AdapterView#setEmptyView(View)
     * @param view
     */
    public void setEmptyView(View view) {
        mEmptyView = view;
    }

    /**
     * set/remove empty for for adapter view
     *
     * @param visiable
     *            true: set; false: remove
     */
    private void showEmptyView(boolean visiable) {
//        if (mEmptyView != null) {
//            if (visiable) {
//                Object targetView = mPullToRefreshView.getRefreshableView();
//                if (targetView != null) {
//                    if (targetView instanceof AdapterView) {
//                        ((AdapterView) targetView).setEmptyView(mEmptyView);
//                    }
//                }
//            } else {
//                mEmptyView.setVisibility(View.GONE);
//            }
//        }
    }

    // set default empty view
    private void setupEmptyView() {
//        TextView emptyView = new TextView(mActivity);
//        emptyView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                LinearLayout.LayoutParams.MATCH_PARENT));
//        emptyView.setGravity(Gravity.CENTER);
//        emptyView.setPadding(0, 20, 0, 0);
//        emptyView.setText("空内容");
//        emptyView.setTextColor(mActivity.getResources().getColor(
//                R.color.text_lite));
        //mListView.setEmptyView(emptyView);
    }

    /**
     * Step 4. 载入新数据
     */
    public void onLoadData() {
        setPullMode(true);
        showEmptyView(false);
        if (mCallback != null) {
            mCallback.onLoadData(mPager);
        }
    }

    /**
     * Step 5. 存入缓存 NOTE: 非UI线程
     *
     * @param list
     *            新数据
     */
    public void onSaveCache(List<T> list) {
        if (mCallback != null) {
            mCallback.onSaveData(list);
        }
    }

    public BaseListAdapter<T> getAdapter() {
        return mAdapter;
    }

    /**
     * 设置回调
     *
     * @param callback
     */
    public void setCallback(Callback<T> callback) {
        mCallback = callback;
    }

    @Override
    public void onRefresh(final PullToRefreshLayout pullToRefreshLayout) {
        mPager.resetPage();
        WLog.e("Log","onPullDownToRefresh");
        onLoadData();

        // 下拉刷新操作
        this.pullToRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                PullableController.this.pullToRefreshLayout.refreshFinish(PullToRefreshLayout.SUCCEED);
            }
        },3000);
    }

    public void onGetIntentRefresh() {
        mPager.resetPage();
        WLog.e("Log","onGetIntentRefresh");
        onLoadData();
    }

    @Override
    public void onLoadMore(final PullToRefreshLayout pullToRefreshLayout) {
        mPager.nextPage();
        WLog.e("Log","onPullUpToRefresh");
        onLoadData();

        this.pullToRefreshLayout.postDelayed(new Runnable() {
            @Override
            public void run() {
                PullableController.this.pullToRefreshLayout.loadmoreFinish(PullToRefreshLayout.SUCCEED);
            }
        },3000);
    }

    /**
     * 设置Pager
     *
     * @param pager
     */
    public void setPager(Pager pager) {
        mPager = pager;
    }

    /**
     * 获取当前Pager
     *
     * @return
     */
    public Pager getPager() {
        return mPager;
    }

    /**
     * 设置是否使用缓存(默认使用)
     *
     * @param isUseCache
     */
    public void setUseCache(boolean isUseCache) {
        mUseCache = isUseCache;
    }

    /**
     * 载入缓存
     */
    class LoadCacheTask extends AsyncTask<Void, Integer, List<T>> {

        @Override
        protected List<T> doInBackground(Void... params) {
            // 1. 载入缓存
            return onLoadCache();
        }

        @Override
        protected void onPostExecute(List<T> list) {
            super.onPostExecute(list);
            if(list != null) WLog.e("PullableController","LoadCache,size = " + list.size());
            // 2. 刷新缓存到UI
            //onRefreshUI(list, false);
            // 3. 获取新数据
            onRefresh(null);
        }

    }

    /**
     * 写入缓存
     */
    class SaveCacheTask extends AsyncTask<List<T>, Integer, Void> {

        @Override
        protected Void doInBackground(List<T>... params) {
            if (params != null && params.length > 0) {
                onSaveCache(params[0]);
            }
            return null;
        }

    }
}
