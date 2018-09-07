package com.progressifff.weather.presenters;

import android.os.Bundle;
import android.support.annotation.NonNull;

import java.lang.ref.WeakReference;

public abstract class BasePresenter<M, V> {
    private static final String TAG = "BasePresenter";
    private WeakReference<V> mViewRef = new WeakReference<>(null);
    M mModel;
    private boolean isInitialViewBind = true;

    public void bindView(@NonNull V v){
        mViewRef = new WeakReference<>(v);
        if(isInitialViewBind){
            onInitialViewBind();
            isInitialViewBind = false;
        }
    }

    public void unbindView(){
        mViewRef.clear();
    }

    protected V getView() { return mViewRef.get(); }

    public boolean isViewAttached() { return getView() != null; }

    protected abstract void updateView();

    protected void onInitialViewBind() {}

    public abstract void saveState(Bundle outState);
}
