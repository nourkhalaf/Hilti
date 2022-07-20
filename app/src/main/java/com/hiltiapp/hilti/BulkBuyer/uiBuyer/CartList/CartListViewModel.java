package com.hiltiapp.hilti.BulkBuyer.uiBuyer.CartList;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class CartListViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public CartListViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is dashboard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}