/*
 * Copyright (C) 2016 The yuhaiyang Android Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ishow.common.app.activity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;

import com.ishow.common.R;
import com.ishow.common.mvp.base.IViewStatus;
import com.ishow.common.utils.ToastUtils;
import com.ishow.common.utils.http.rest.Http;
import com.ishow.common.utils.permission.PermissionManager;
import com.ishow.common.widget.StatusView;
import com.ishow.common.widget.TopBar;
import com.ishow.common.widget.dialog.BaseDialog;
import com.ishow.common.widget.loading.LoadingDialog;


public abstract class BaseActivity extends AppCompatActivity implements
        StatusView.CallBack,
        IViewStatus,
        TopBar.OnTopBarListener {
    /**
     * Activity的TYPE
     */
    public static final String KEY_TYPE = "activity_type";
    /**
     * Loading的Dialog
     */
    protected LoadingDialog mLoadingDialog;
    /**
     * 状态的View
     */
    protected StatusView mStatusView;
    /**
     * 用来回收的Handler
     */
    protected Handler mHandler;

    private boolean isPaused;
    private boolean isResumed;

    //************************ 生命周期 区域*********************** //
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        resetStatusBar();
    }


    @Override
    protected void onResume() {
        super.onResume();
        isResumed = true;
        isPaused = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        isResumed = false;
        isPaused = true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 取消请求接口
        Http.cancel(this);
        // 清除Handler预防内存泄露
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
            mHandler = null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        PermissionManager.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    //************************ 初始化 区域*********************** //
    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        initNecessaryData();
        initViews();
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        initNecessaryData();
        initViews(view);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        initNecessaryData();
        initViews(view, params);
    }

    protected void initViews() {
        // 主动设置TopBar
        View topBarView = findViewById(R.id.top_bar);
        if (topBarView != null && topBarView instanceof TopBar) {
            TopBar topBar = (TopBar) topBarView;
            topBar.setOnTopBarListener(this);
        }

        // 主动设置statusView
        View statusView = findViewById(R.id.status_view);
        if (statusView != null && statusView instanceof StatusView) {
            mStatusView = (StatusView) statusView;
            mStatusView.setCallBack(this);
        }
    }

    @SuppressWarnings("unused")
    protected void initViews(View view) {
    }

    @SuppressWarnings("unused")
    protected void initViews(View view, ViewGroup.LayoutParams params) {
    }

    /**
     * 有一些数据要在initViews之前处理的在这个方法中处理
     * <p>
     * 注意：尽量少用
     */
    protected void initNecessaryData() {
    }

    /**
     * TopBar的左侧点击事件
     */
    @Override
    public void onLeftClick(View v) {
        onBackPressed();
    }

    /**
     * TopBar的右侧点击事件
     */
    @Override
    public void onRightClick(View v) {

    }

    /**
     * TopBar的中间Title点击事件
     */
    @Override
    public void onTitleClick(View v) {

    }

    /**
     * 用来设置全屏样式
     */
    protected void resetStatusBar() {
    }

    /**
     * Activity
     * isResumed 已经被占用..
     */
    public boolean isActivityResumed() {
        return isResumed;
    }

    protected boolean isActivityPaused() {
        return isPaused;
    }



    // ******************** 提示区域 ***************************//

    /**
     * 提示 Toast简易封装操作
     */
    public void toast(String toast) {
        ToastUtils.show(this, toast);
    }

    /**
     * 提示 Toast简易封装操作
     */
    @SuppressWarnings("unused")
    public void toast(int toast) {
        ToastUtils.show(this, toast);
    }

    /**
     * 显示Dialog
     *
     * @param message 显示的内容
     */
    protected Dialog dialog(@StringRes int message) {
        return dialog(message, false, false);
    }

    /**
     * 显示Dialog
     *
     * @param message 显示的内容
     */
    protected Dialog dialog(@StringRes int title, @StringRes int message) {
        return dialog(title, message, false, false);
    }

    /**
     * @param message    显示的内容
     * @param finishSelf 是否关闭acitivity
     */
    protected Dialog dialog(@StringRes int message, boolean finishSelf) {
        return dialog(message, finishSelf, false);
    }

    /**
     * @param title      标题
     * @param message    显示的内容
     * @param finishSelf 是否关闭acitivity
     */
    protected Dialog dialog(@StringRes int title, @StringRes int message, boolean finishSelf) {
        return dialog(title, message, finishSelf, false);
    }

    /**
     * @param message    显示的内容
     * @param finishSelf 是否关闭acitivity
     * @param cancelable 是否可以点击取消
     */
    protected Dialog dialog(@StringRes int message, final boolean finishSelf, boolean cancelable) {
        return dialog(0, message, finishSelf, cancelable);
    }


    /**
     * @param title      标题
     * @param message    显示的内容
     * @param finishSelf 是否关闭acitivity
     * @param cancelable 是否可以点击取消
     */
    protected Dialog dialog(@StringRes int title, @StringRes int message, final boolean finishSelf, boolean cancelable) {

        String titleString;
        try {
            titleString = getText(title).toString();
        } catch (Resources.NotFoundException e) {
            titleString = null;
        }

        String messageString;
        try {
            messageString = getText(message).toString();
        } catch (Resources.NotFoundException e) {
            messageString = null;
        }
        return dialog(titleString, messageString, finishSelf, cancelable);
    }

    /**
     * 显示Dialog
     *
     * @param message 显示的内容
     */
    protected Dialog dialog(String message) {
        return dialog(message, false, false);
    }

    /**
     * 显示Dialog
     *
     * @param title   标题
     * @param message 显示的内容
     */
    protected Dialog dialog(String title, String message) {
        return dialog(title, message, false, false);
    }

    /**
     * @param message    显示的内容
     * @param finishSelf 是否关闭acitivity
     */
    protected Dialog dialog(String message, boolean finishSelf) {
        return dialog(message, finishSelf, false);
    }


    /**
     * 显示Dialog
     *
     * @param title      标题
     * @param message    显示的内容
     * @param finishSelf 是否关闭acitivity
     */
    protected Dialog dialog(String title, String message, boolean finishSelf) {
        return dialog(title, message, finishSelf, false);
    }

    /**
     * @param message    显示的内容
     * @param finishSelf 是否关闭acitivity
     * @param cancelable 是否可以点击取消
     */
    protected Dialog dialog(String message, final boolean finishSelf, boolean cancelable) {
        return dialog(null, message, finishSelf, cancelable);
    }


    /**
     * @param title      标题
     * @param message    显示的内容
     * @param finishSelf 是否关闭acitivity
     * @param cancelable 是否可以点击取消
     */
    protected Dialog dialog(String title, String message, final boolean finishSelf, boolean cancelable) {
        BaseDialog.Builder bulider = new BaseDialog.Builder(this);
        // 设置标题
        if (!TextUtils.isEmpty(title)) {
            bulider.setTitle(title);
        }
        // 设置message
        bulider.setMessage(message);
        bulider.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (finishSelf) {
                    BaseActivity.this.finish();
                }
            }
        });
        bulider.setCancelable(cancelable);

        BaseDialog dialog = bulider.create();
        dialog.show();
        return dialog;
    }


    @Override
    public void showLoading(String message, boolean dialog) {
        if (dialog) {
            mLoadingDialog = LoadingDialog.show(this, mLoadingDialog);
        } else if (mStatusView != null) {
            mStatusView.showLoading();
        }
    }

    @Override
    public void dismissLoading(boolean dialog) {
        if (dialog) {
            LoadingDialog.dismiss(mLoadingDialog);
        } else if (mStatusView != null) {
            mStatusView.dismiss();
        }
    }

    @Override
    public void showError(String message, boolean dialog, int errorType) {
        if (dialog) {
            dialog(message);
        } else if (mStatusView != null) {
            mStatusView.showError();
        }
    }

    @Override
    public void showSuccess(String message) {

    }

    @Override
    public void showEmpty(String message) {
        if (mStatusView != null) {
            mStatusView.showEmpty();
        }
    }

    @Override
    public void onReload(View v) {

    }
}
