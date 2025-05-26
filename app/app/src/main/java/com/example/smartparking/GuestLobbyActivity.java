package com.example.smartparking;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class GuestLobbyActivity extends AppCompatActivity {

    String[] slotNames = {"BÃI 1", "BÃI 2", "BÃI 3", "BÃI 4", "BÃI 5"};
    int[] icons = {
            R.drawable.check_ok,
            R.drawable.check_error,
            R.drawable.check_repairing,
            R.drawable.check_ok,
            R.drawable.check_repairing
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guest_lobby);

        LinearLayout parkingList = findViewById(R.id.parkingList);
        LayoutInflater inflater = LayoutInflater.from(this);

//        for (int i = 0; i < slotNames.length; i++) {
//            View slotView = inflater.inflate(R.layout.activity_parking_slot, parkingList, false);
//            TextView tvName = slotView.findViewById(R.id.tvSlotName);
//            ImageView icon = slotView.findViewById(R.id.statusIcon);
//
//            tvName.setText(slotNames[i]);
//            icon.setImageResource(icons[i]);
//
//            parkingList.addView(slotView);
//        }
    }
}
