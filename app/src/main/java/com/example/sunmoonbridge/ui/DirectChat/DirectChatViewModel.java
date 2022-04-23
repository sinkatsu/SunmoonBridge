package com.example.sunmoonbridge.ui.DirectChat;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class DirectChatViewModel extends ViewModel {
    private MutableLiveData<String> dText;

    public DirectChatViewModel(){
        dText = new MutableLiveData<>();
        dText.setValue("This is DirectChat Fragment!!!!!");
    }

    public LiveData<String> getText() {
        return dText;
    }
}