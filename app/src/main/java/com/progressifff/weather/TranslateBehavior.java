package com.progressifff.weather;

import android.content.Context;
import android.support.design.widget.CoordinatorLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

class TranslateBehavior extends CoordinatorLayout.Behavior<FrameLayout> {
    private final static String TAG = "TranslateBehavior";

    public TranslateBehavior(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FrameLayout child, View dependency) {
        return Utils.hasBottomSheetBehavior(dependency);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FrameLayout child, View dependency) {
        if(dependency != null && Utils.hasBottomSheetBehavior(dependency)){
            child.setPadding(0, 0, 0, parent.getHeight() - dependency.getTop());
        }
        else {
            return super.onDependentViewChanged(parent, child, dependency);
        }
        return true;
    }
}