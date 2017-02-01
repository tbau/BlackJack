package com.bau_hornick.blackjack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class PlayerActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //listener for hit & stand
        //5 card max
        //after dealer's turn over, determine who wins
        //start DealerActivity
    }
}
