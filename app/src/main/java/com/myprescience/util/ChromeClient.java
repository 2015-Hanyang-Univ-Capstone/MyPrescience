package com.myprescience.util;

import android.app.Activity;
import android.content.Context;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.FrameLayout;

/**
 * Created by dongjun on 15. 4. 12..
 */

public final class ChromeClient extends WebChromeClient {
    private View mCustomView;
    private Activity mActivity;

    public ChromeClient(Activity activity) {
        this.mActivity = activity;
    }

    @Override
    public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
        result.confirm();
        return super.onJsAlert(view, url, message, result);
    }

    private int mOriginalOrientation;
    private FullscreenHolder mFullscreenContainer;
    private CustomViewCallback mCustomViewCollback;

    @Override
    public void onShowCustomView(View view, CustomViewCallback callback) {

        if (mCustomView != null) {
            callback.onCustomViewHidden();
            return;
        }

        mOriginalOrientation = mActivity.getRequestedOrientation();

        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();

        mFullscreenContainer = new FullscreenHolder(mActivity);
        mFullscreenContainer.addView(view, ViewGroup.LayoutParams.MATCH_PARENT);
        decor.addView(mFullscreenContainer, ViewGroup.LayoutParams.MATCH_PARENT);
        mCustomView = view;
        mCustomViewCollback = callback;
        mActivity.setRequestedOrientation(mOriginalOrientation);

    }

    @Override
    public void onHideCustomView() {
        if (mCustomView == null) {
            return;
        }

        FrameLayout decor = (FrameLayout) mActivity.getWindow().getDecorView();
        decor.removeView(mFullscreenContainer);
        mFullscreenContainer = null;
        mCustomView = null;
        mCustomViewCollback.onCustomViewHidden();

        mActivity.setRequestedOrientation(mOriginalOrientation);
    }


    static class FullscreenHolder extends FrameLayout {

        public FullscreenHolder(Context ctx) {
            super(ctx);
            setBackgroundColor(ctx.getResources().getColor(android.R.color.black));
        }

        @Override
        public boolean onTouchEvent(MotionEvent evt) {
            return true;
        }
    }
}