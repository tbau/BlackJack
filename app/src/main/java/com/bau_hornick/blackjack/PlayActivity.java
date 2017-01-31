package com.bau_hornick.blackjack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    int bet=1;
    int money = 100;
    int numberInDeck=52;

    Deck deck;
    Deck playerHand;
    Deck dealerHand;

    int startGame=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Button b = (Button) findViewById(R.id.increase_bet_button);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.decrease_bet_button);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.start_button);
        b.setOnClickListener(this);

        deck=new Deck(1);
        playerHand=new Deck(0);
        dealerHand = new Deck(0);

        if(startGame==0)
            deck.shuffle();
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.decrease_bet_button) {
            bet--;
            if(bet<1)
                bet++;
            TextView tv = (TextView) findViewById(R.id.bet_textView);
            tv.setText(bet + "(" + (money - bet) + ")");
        } else if (v.getId() == R.id.increase_bet_button) {
            bet++;
            if(bet>money)
                bet--;
            TextView betView = (TextView) findViewById(R.id.bet_textView);
            betView.setText(bet + "(" + (money - bet) + ")");
        } else if (v.getId() == R.id.start_button) {
            ImageView im = (ImageView) findViewById(R.id.dealer_card1_imageView);
            im.setVisibility(View.VISIBLE);

            if(deck.getDeck().size()<=10)
            {
                deck.reset();
            }

            dealerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));  //Add card from deck to
            deck.getDeck().remove(deck.getDeck().size()-1);

            dealerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
            deck.getDeck().remove(deck.getDeck().size()-1);

            playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
            deck.getDeck().remove(deck.getDeck().size()-1);

            playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
            deck.getDeck().remove(deck.getDeck().size()-1);

            numberInDeck-=4;

            TextView tv = (TextView) findViewById(R.id.deck_count_textView);
            tv.setText(String.valueOf(money));

            Intent intent = new Intent(this,PlayerActivity.class);
            intent.putExtra("bet",bet);
            intent.putExtra("money",money);
            intent.putExtra("deck",deck);
            startActivity(intent);
        }
    }

}