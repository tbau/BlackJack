package com.bau_hornick.blackjack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    int bet;
    int money;
    Deck deck;
    Deck playerHand;
    Deck dealerHand;
    ArrayList<ImageView> playerImages;
    ArrayList<ImageView> dealerImages;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        //listener for hit & stand
        //5 card max
        //after dealer's turn over, determine who wins
        //start DealerActivity

        Intent intent = getIntent();
        playerHand = (Deck) intent.getSerializableExtra("playerHand");
        dealerHand = (Deck) intent.getSerializableExtra("dealerHand");
        dealerImages =(ArrayList<ImageView>) intent.getSerializableExtra("dealerImages");
        playerImages = (ArrayList<ImageView>) intent.getSerializableExtra("playerImages");
        bet = intent.getIntExtra("bet",bet);
        money = intent.getIntExtra("money",money);
        deck = (Deck) intent.getSerializableExtra("deck");
        
    }
}
