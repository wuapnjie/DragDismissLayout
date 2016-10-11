package com.xiaopo.flying.dragdismisslayout;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ViewDragHelper;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.FrameLayout;

/**
 * Created by snowbean on 16-10-10.
 */
public class DragDismissLayout extends FrameLayout {
    private static final String TAG = "DragDismissLayout";
    private View mCapturedView;

    private int mDragDirection = DIRECTION_UP;
    public static final int DIRECTION_UP = 0;
    public static final int DIRECTION_DOWN = 1;
    public static final int DEFAULT_THRESHOLD_VEL = 3000;
    public static final int DEFAULT_THRESHOLD_OFFSET = 300;

    private float mDownX;
    private float mDownY;

    private ViewDragHelper mDragHelper;

    private int mThresholdVel = DEFAULT_THRESHOLD_VEL;
    private int mThresholdOffset = DEFAULT_THRESHOLD_OFFSET;

    public DragDismissLayout.DragCallback mDragCallback;

    ViewDragHelper.Callback mCallback = new ViewDragHelper.Callback() {
        @Override
        public boolean tryCaptureView(View child, int pointerId) {
            mCapturedView = child;
            return true;
        }

        @Override
        public void onViewCaptured(View capturedChild, int activePointerId) {
            super.onViewCaptured(capturedChild, activePointerId);
            if (mCapturedView != capturedChild) {
                mCapturedView = capturedChild;
            }
        }

        @Override
        public void onViewPositionChanged(View changedView, int left, int top, int dx, int dy) {
            super.onViewPositionChanged(changedView, left, top, dx, dy);

            if (mDragCallback != null) {
                mDragCallback.onDrag(top);
            }

            if (Math.abs(top) > 300 && mDragCallback != null) {
                mDragCallback.onReadyDismiss();
            }
        }

        @Override
        public int clampViewPositionVertical(View child, int top, int dy) {
//            Log.d(TAG, "clampViewPositionVertical: top->" + top + ",direction->" + mDragDirection);
            if (mDragDirection == DIRECTION_DOWN) {
                return Math.max(top, 0);
            }
            if (mDragDirection == DIRECTION_UP) {
                return Math.min(top, 0);
            }

            return 0;
        }

        @Override
        public void onViewReleased(View releasedChild, float xvel, float yvel) {
            super.onViewReleased(releasedChild, xvel, yvel);

//            Log.d(TAG, "onViewReleased: top-->" + releasedChild.getTop());

            if ((mDragDirection == DIRECTION_DOWN && yvel > mThresholdVel) || (releasedChild.getTop() > mThresholdOffset)) {
                if (mDragCallback != null) {
                    mDragCallback.onDismiss(DIRECTION_DOWN);
                }
                mDragHelper.settleCapturedViewAt(0, getHeight());
            } else if ((mDragDirection == DIRECTION_UP && yvel < -mThresholdVel) || (releasedChild.getTop() < -mThresholdOffset)) {
                if (mDragCallback != null) {
                    mDragCallback.onDismiss(DIRECTION_UP);
                }
                mDragHelper.settleCapturedViewAt(0, -getHeight());
            } else {
                if (mDragCallback != null) {
                    mDragCallback.onDragCanceled();
                }
                mDragHelper.settleCapturedViewAt(0, 0);
            }
            invalidate();
        }


        @Override
        public int getViewVerticalDragRange(View child) {
            return 10;
        }
    };


    public DragDismissLayout(Context context) {
        super(context);
    }

    public DragDismissLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mDragHelper = ViewDragHelper.create(this, mCallback);
    }

    public DragDismissLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DragDismissLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0) {
            mCapturedView = getChildAt(0);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {

        mDragHelper.shouldInterceptTouchEvent(event);
//        Log.d(TAG, "onInterceptTouchEvent: shouldIntercept->" + shouldIntercept);
        int action = MotionEventCompat.getActionMasked(event);
//        Log.d(TAG, "onInterceptTouchEvent: action->" + action);
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getX();
                mDownY = event.getY();
                mDragHelper.processTouchEvent(event);
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = event.getX() - mDownX;
                float deltaY = event.getY() - mDownY;

                if (Math.abs(deltaX) > Math.abs(deltaY)) {
                    return false;
                }

                if (deltaY > 0 && !canChildScrollUp()) {
                    mDragDirection = DIRECTION_DOWN;
                    return true;
                }

                if (deltaY < 0 && !canChildScrollDown()) {
                    mDragDirection = DIRECTION_UP;
                    return true;
                }
                break;
        }

        return super.onInterceptTouchEvent(event);


    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDragHelper.processTouchEvent(event);
        return true;
    }

    @Override
    public void computeScroll() {
        if (mDragHelper.continueSettling(true)) {
            invalidate();
        } else {
            mDragHelper.smoothSlideViewTo(mCapturedView, 0, 0);
        }
    }

    protected boolean canChildScrollUp() {
        if (mCapturedView == null) {
            return false;
        }
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mCapturedView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mCapturedView;
                return absListView.getChildCount() > 0 && (absListView.getFirstVisiblePosition() > 0
                        || absListView.getChildAt(0).getTop() < absListView.getPaddingTop());
            } else {
                return ViewCompat.canScrollVertically(mCapturedView, -1) || mCapturedView.getScrollY() > 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mCapturedView, -1);
        }
    }

    protected boolean canChildScrollDown() {
        if (mCapturedView == null) return false;
        if (android.os.Build.VERSION.SDK_INT < 14) {
            if (mCapturedView instanceof AbsListView) {
                final AbsListView absListView = (AbsListView) mCapturedView;
                return absListView.getChildCount() > 0 && (absListView.getLastVisiblePosition()
                        < absListView.getChildCount() - 1
                        || absListView.getChildAt(absListView.getChildCount() - 1).getBottom()
                        > absListView.getPaddingBottom());
            } else {
                return ViewCompat.canScrollVertically(mCapturedView, 1) || mCapturedView.getScrollY() < 0;
            }
        } else {
            return ViewCompat.canScrollVertically(mCapturedView, 1);
        }
    }

    public void setDragCallback(DragCallback dragCallback) {
        mDragCallback = dragCallback;
    }

    public int getThresholdVel() {
        return mThresholdVel;
    }

    public void setThresholdVel(int thresholdVel) {
        mThresholdVel = thresholdVel;
    }

    public int getThresholdOffset() {
        return mThresholdOffset;
    }

    public void setThresholdOffset(int thresholdOffset) {
        mThresholdOffset = thresholdOffset;
    }

    public interface DragCallback {
        void onDrag(int offset);

        void onReadyDismiss();

        void onDismiss(int direction);

        void onDragCanceled();
    }
}
