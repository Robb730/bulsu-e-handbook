package com.example.bulsustudenthandbook;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.HorizontalScrollView;

public class SyncHorizontalScrollView extends HorizontalScrollView {

    private SyncHorizontalScrollView mPartnerScrollView;

    public SyncHorizontalScrollView(Context context) {
        super(context);
    }

    public SyncHorizontalScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setPartnerScrollView(SyncHorizontalScrollView scrollView) {
        mPartnerScrollView = scrollView;
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mPartnerScrollView != null) {
            mPartnerScrollView.scrollTo(l, t);
        }
    }
}

