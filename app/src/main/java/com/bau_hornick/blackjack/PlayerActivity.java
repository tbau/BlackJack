package com.bau_hornick.blackjack;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayerActivity extends AppCompatActivity implements View.OnClickListener {

    int bet;
    int money;
    int playerScore = 0;
    Deck deck;
    Deck playerHand;
    Deck dealerHand;
    ArrayList<ImageView> playerImages;
    ArrayList<ImageView> dealerImages;

    int dealerImageViews[] = {R.id.dealer_card2_imageView, R.id.dealer_card2_imageView,
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


        }

        public void startDealerActivity()
        {
            final Handler h = new Handler();

            Runnable r1 = new Runnable() {
                @Override
                public void run() {

                Intent intent=new Intent(PlayerActivity.this, DealerActivity.class);
                intent.putExtra("playerHand", playerHand);
                intent.putExtra("dealerHand", dealerHand);
                intent.putExtra("bet",bet);
                intent.putExtra("money",money);
                intent.putExtra("deck",deck);
                startActivity(intent);
                }
            };
            h.postDelayed(r1,2000);
        }

        public void countScore()
        {
            playerScore = 0;
            for(int i = 0; i < playerHand.getDeck().size(); i++) {
                playerScore += playerHand.getDeck().get(i).getValue();
                if(playerScore>21){
                    for(int j = 0; j < playerHand.getDeck().size(); j++){
                        if(playerHand.getDeck().get(i).getValue()==11)
                    playerScore-=10;
                    }
                }
            }
        }


        @Override
        public void onClick(View v) {
            if(v.getId() == R.id.hit_button)
            {
                if(playerHand.getDeck().size() < 5) {
                    playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size() - 1));
                    deck.getDeck().remove(deck.getDeck().size() - 1);
                    playerImages.get(playerHand.getDeck().size()-1).setImageResource(playerHand.getDeck().get(playerHand.getDeck().size()-1).getImage());
                    playerImages.get(playerHand.getDeck().size()-1).setVisibility(View.VISIBLE);
                    countScore();

                    if(playerHand.getDeck().size() == 5 && playerScore<21){

                        Toast.makeText(getApplicationContext(), "You have reached the five card limit!", Toast.LENGTH_SHORT).show();
                        startDealerActivity();
                    }
                    else if(playerScore == 21)
                    {
                        Toast.makeText(getApplicationContext(), "You have a natural!", Toast.LENGTH_SHORT).show(); //Already added sleep function
                        startDealerActivity();

                    }
                    else if(playerScore > 21)
                    {
                        Toast.makeText(getApplicationContext(), "You scored "+playerScore+". You have busted!", Toast.LENGTH_SHORT).show(); //Already added sleep function
                        startDealerActivity();
                    }
                }
            }

            else if(v.getId() == R.id.stand_button) {
                Toast.makeText(getApplicationContext(), "You have a total score of "+playerScore, Toast.LENGTH_SHORT).show(); //Already added sleep function
                startDealerActivity();
            }

        }
}
