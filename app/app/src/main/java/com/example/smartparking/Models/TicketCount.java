package com.example.smartparking.Models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class TicketCount extends ViewModel {
    private final MutableLiveData<Integer> ticketCount = new MutableLiveData<>();

    public LiveData<Integer> getTicketCount() {
        return ticketCount;
    }

    public void setTicketCount(int count) {
        ticketCount.setValue(count);
    }
}
