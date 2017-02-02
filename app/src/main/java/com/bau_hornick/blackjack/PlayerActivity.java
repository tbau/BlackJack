package com.bau_hornick.blackjack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity {

    int bet;
    int money;
    Deck deck;
    Deck playerHand;
    Deck dealerHand;
    ArrayList<ImageView> playerImages;
    ArrayList<ImageView> dealerImages;

    int dealerImageViews[] = {R.id.dealer_card1_imageView, R.id.dealer_card2_imageView,
            R.id.dealer_card3_imageView, R.id.dealer_card4_imageView,
            R.id.dealer_card5_imageView};
    int playerImageViews[] = {R.id.player_card1_imageView, R.id.player_card2_imageView,
            R.id.player_card3_imageView,R.id.player_card4_imageView,
            R.id.player_card5_imageView};

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
        //dealerImages =(ArrayList<ImageView>) intent.getSerializableExtra("dealerImages");
        //playerImages = (ArrayList<ImageView>) intent.getSerializableExtra("playerImages");

        playerImages = new ArrayList<ImageView>();
        dealerImages = new ArrayList<ImageView>();

        bet = intent.getIntExtra("bet",bet);
        money = intent.getIntExtra("money",money);
        deck = (Deck) intent.getSerializableExtra("deck");


        //set Image Resources in Arraylist of ImageViews for both dealer and player
        for(int i = 0; i < dealerImageViews.length; i++)
        {
            ImageView dealerImg = (ImageView) findViewById(dealerImageViews[i]);
            dealerImages.add(dealerImg);

            ImageView playerImg = (ImageView) findViewById(playerImageViews[i]);
            playerImages.add(playerImg);
        }

        //Set 1 of dealer cards to the back card. Add to card class.
        dealerImages.get(0).setImageResource(R.drawable.red_card_back);
        dealerImages.get(0).setVisibility(View.VISIBLE);

        dealerImages.get(1).setImageResource(dealerHand.getDeck().get(1).getImage());
        dealerImages.get(1).setVisibility(View.VISIBLE);

        playerImages.get(0).setImageResource(playerHand.getDeck().get(0).getImage());
        playerImages.get(0).setVisibility(View.VISIBLE);

        playerImages.get(1).setImageResource(playerHand.getDeck().get(1).getImage());
        playerImages.get(1).setVisibility(View.VISIBLE);



        dealerImages.get(2).setImageResource(dealerHand.getDeck().get(2).getImage());
        dealerImages.get(2).setVisibility(View.VISIBLE);

        playerImages.get(2).setImageResource(playerHand.getDeck().get(2).getImage());
        playerImages.get(2).setVisibility(View.VISIBLE);

        dealerImages.get(3).setImageResource(dealerHand.getDeck().get(3).getImage());
        dealerImages.get(3).setVisibility(View.VISIBLE);

        playerImages.get(3).setImageResource(playerHand.getDeck().get(3).getImage());
        playerImages.get(3).setVisibility(View.VISIBLE);

        TextView tv = (TextView) findViewById(R.id.bet_textView);
        tv.setText(bet + "(" + (money - bet) + ")");

        tv = (TextView) findViewById(R.id.deck_count_textView);
        tv.setText(String.valueOf(deck.getDeck().size()));

    }
}
