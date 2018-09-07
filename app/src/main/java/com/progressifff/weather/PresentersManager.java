package com.progressifff.weather;

import android.os.Bundle;
import android.util.LongSparseArray;
import com.progressifff.weather.presenters.BasePresenter;
import java.util.concurrent.atomic.AtomicLong;

class PresentersManager {
    private static final String PRESENTER_ID_KEY = "presenter_id";
    private static final PresentersManager ourInstance = new PresentersManager();

    private LongSparseArray<BasePresenter> mPresenters;
    private AtomicLong mPresenterId;

    static PresentersManager getInstance() {
        return ourInstance;
    }

    private PresentersManager() {
        mPresenterId = new AtomicLong(0L);
        mPresenters = new LongSparseArray<>();
    }

    BasePresenter restorePresenter(Bundle savedInstanceState){
        long presenterId = savedInstanceState.getLong(PRESENTER_ID_KEY);
        BasePresenter presenter = mPresenters.get(presenterId);
        if(presenter != null){
            mPresenters.remove(presenterId);
        }
        return presenter;
    }

    void savePresenter(BasePresenter presenter, Bundle outState){
        presenter.saveState(outState);
        long presenterId = mPresenterId.incrementAndGet();
        mPresenters.put(presenterId, presenter);
        outState.putLong(PRESENTER_ID_KEY, presenterId);
    }
}
