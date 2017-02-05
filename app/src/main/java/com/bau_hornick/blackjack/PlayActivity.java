package com.bau_hornick.blackjack;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    int bet=1;
    int money = 100;

    Deck deck;
    Deck playerHand;
    Deck dealerHand;

    int playerScore = 0;
    int dealerScore=0;

    ArrayList<ImageView> playerImages;
    ArrayList<ImageView> dealerImages;

    int dealerImageViews[] = {R.id.dealer_card1_imageView, R.id.dealer_card2_imageView,
            R.id.dealer_card3_imageView, R.id.dealer_card4_imageView,
            R.id.dealer_card5_imageView};
    int playerImageViews[] = {R.id.player_card1_imageView, R.id.player_card2_imageView,
            R.id.player_card3_imageView,R.id.player_card4_imageView,
            R.id.player_card5_imageView};


    enum STATE{BEFORE, PLAYER,DEALER};
    STATE state=STATE.BEFORE;


    int pause=1000;
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

        b = (Button) findViewById(R.id.hit_button);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.stand_button);
        b.setOnClickListener(this);

        if(deck!=null)
        {

        }
        else{
            deck=new Deck(1);
        }
        if(playerHand!=null){

        }else{
            playerHand=new Deck(0);
        }

        if(dealerHand!=null) {

        }
        else{
            dealerHand = new Deck(0);
        }
        if(playerImages!=null){

        }else{

            playerImages = new ArrayList<ImageView>();
        }
        if(dealerImages!=null){

        }else{

            dealerImages = new ArrayList<ImageView>();
        }

        for(int i = 0; i < dealerImageViews.length; i++)
        {
            ImageView dealerImg = (ImageView) findViewById(dealerImageViews[i]);
            dealerImages.add(dealerImg);

            ImageView playerImg = (ImageView) findViewById(playerImageViews[i]);
            playerImages.add(playerImg);
        }


        TextView tv = (TextView) findViewById(R.id.deck_count_textView);
        tv.setText(String.valueOf(deck.getDeck().size()));

    }

    @Override
    protected void onResume() {
        super.onResume();
        TextView tv = (TextView) findViewById(R.id.deck_count_textView);
        tv.setText(String.valueOf(deck.getDeck().size()));

        dealerHand.getDeck().clear();
        playerHand.getDeck().clear();
    }

    @Override
    public void onClick(View v) {
        final Handler h = new Handler();
        if (v.getId() == R.id.decrease_bet_button && state == STATE.BEFORE) {
            bet--;
            if(bet<1)
                bet++;
            TextView tv = (TextView) findViewById(R.id.bet_textView);
            tv.setText(bet + "(" + (money - bet) + ")");
        } else if (v.getId() == R.id.increase_bet_button && state==STATE.BEFORE) {
            bet++;
            if(bet>money)
                bet--;
            TextView betView = (TextView) findViewById(R.id.bet_textView);
            betView.setText(bet + "(" + (money - bet) + ")");
        } else if (v.getId() == R.id.start_button && state==STATE.BEFORE){

            if(deck.getDeck().size()<=10)
            {
                deck.reset();
            }

//First two cards
            dealerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
            deck.getDeck().remove(deck.getDeck().size()-1);

            dealerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
            deck.getDeck().remove(deck.getDeck().size()-1);

            playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
            deck.getDeck().remove(deck.getDeck().size()-1);

            playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
            deck.getDeck().remove(deck.getDeck().size()-1);

            state = STATE.PLAYER;
            checkPlayer();
        }else if(v.getId()==R.id.hit_button&&state==STATE.PLAYER) {

            if (playerHand.getDeck().size() < 5) {
                playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size() - 1));
                deck.getDeck().remove(deck.getDeck().size() - 1);

                playerImages.get(playerHand.getDeck().size() - 1).setImageResource(playerHand.getDeck().get(playerHand.getDeck().size() - 1).getImage());
                playerImages.get(playerHand.getDeck().size() - 1).setVisibility(View.VISIBLE);
                countPlayerScore();

                    if (playerHand.getDeck().size() == 5 && playerScore < 21) {

                    Toast.makeText(getApplicationContext(), "You have reached the five card limit!", Toast.LENGTH_SHORT).show();

                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            state = STATE.DEALER;
                            checkDealer();
                        }
                    };
                    h.postDelayed(r1, 1000);
                } else if (playerScore == 21) {
                    Toast.makeText(getApplicationContext(), "You have a natural!", Toast.LENGTH_SHORT).show(); //Already added sleep function

                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            state = STATE.DEALER;
                            checkDealer();
                        }
                    };

                    h.postDelayed(r1, 1000);
                } else if (playerScore > 21) {
                    Toast.makeText(getApplicationContext(), "You scored " + playerScore + ". You have busted!", Toast.LENGTH_SHORT).show(); //Already added sleep function

                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            state = STATE.BEFORE;
                            money -= bet;
                            checkBefore();
                        }
                    };

                    h.postDelayed(r1, 1000);

                }
            } }else if (v.getId() == R.id.stand_button && state == STATE.PLAYER) {

                countPlayerScore();

                if (playerScore == 21) {
                    Toast.makeText(getApplicationContext(), "You have a natural!", Toast.LENGTH_SHORT).show(); //Already added sleep function

                    Runnable r1 = new Runnable() {
                       @Override
                       public void run() {
                           state = STATE.DEALER;
                           checkDealer();
                       }
                   };
                    h.postDelayed(r1,1000);


                } else {
                    Toast.makeText(getApplicationContext(), "You scored " + playerScore, Toast.LENGTH_SHORT).show(); //Already added sleep function

                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            state = STATE.DEALER;
                            checkDealer();
                        }
                    };
                    h.postDelayed(r1,1000);
               }
            }
        }

    public void checkBefore(){
        if(state==STATE.BEFORE){
            dealerScore=0;
            playerScore=0;

            for(int i=0;i<playerHand.getDeck().size();i++){
                playerImages.get(i).setVisibility(View.INVISIBLE);
            }

            for(int i=0;i<dealerHand.getDeck().size();i++){
                dealerImages.get(i).setVisibility(View.INVISIBLE);
            }

            dealerHand.getDeck().clear();
            playerHand.getDeck().clear();

            TextView betView = (TextView) findViewById(R.id.bet_textView);
            betView.setText(bet + "(" + (money - bet) + ")");
            betView = (TextView) findViewById(R.id.current_money_textView);
            betView.setText(String.valueOf(money));
        }
    }
    public void checkPlayer(){

        if(state==STATE.PLAYER){
            playerScore=0;
            TextView tv = (TextView) findViewById(R.id.deck_count_textView);
            tv.setText(String.valueOf(deck.getDeck().size()));

            dealerImages.get(0).setImageResource(R.drawable.red_card_back);
            dealerImages.get(0).setVisibility(View.VISIBLE);

            tv = (TextView) findViewById(R.id.bet_textView);
            tv.setText(bet + "(" + (money - bet) + ")");

            final Handler h = new Handler();

            Runnable r1 = new Runnable() {

                @Override
                public void run() {
                    // do first thing

                    playerImages.get(0).setImageResource(playerHand.getDeck().get(0).getImage());
                    playerImages.get(0).setVisibility(View.VISIBLE);

                }
            };
            Runnable r2 = new Runnable() {

                @Override
                public void run() {
                    // do first thing
                    dealerImages.get(1).setImageResource(dealerHand.getDeck().get(1).getImage());
                    dealerImages.get(1).setVisibility(View.VISIBLE);

                }
            };
            Runnable r3 = new Runnable() {

                @Override
                public void run() {
                    // do first thing

                    playerImages.get(1).setImageResource(playerHand.getDeck().get(1).getImage());
                    playerImages.get(1).setVisibility(View.VISIBLE);

                    countPlayerScore();

                    if(playerScore == 21)
                    {
                        Toast.makeText(getApplicationContext(), "You have a Blackjack!", Toast.LENGTH_SHORT).show(); //add a sleep function
                        state=STATE.DEALER;
                        checkDealer();
                    }

                }
            };
            h.postDelayed(r1, 500);
            h.postDelayed(r2, 1000);
            h.postDelayed(r3, 1500);

        }}

    public void checkDealer(){
        if(state==STATE.DEALER){
            countDealerScore();

            final Handler h = new Handler();

            dealerImages.get(0).setImageResource(dealerHand.getDeck().get(0).getImage());

            if(dealerScore == 21)
            {
                Toast.makeText(getApplicationContext(), "Tie, you have a push!", Toast.LENGTH_SHORT).show(); //add a sleep function

                if(playerScore==21) {
                    Runnable r1 = new Runnable() {

                        @Override
                        public void run() {
                            // do first thing
                            state = STATE.BEFORE;
                            checkBefore();

                        }
                    };
                    h.postDelayed(r1, pause);
                    pause += 1000;
                }
                else{

                    Toast.makeText(getApplicationContext(), "Dealer Wins!", Toast.LENGTH_SHORT).show(); //add a sleep function

                    Runnable r1 = new Runnable() {

                        @Override
                        public void run() {
                            // do first thing
                            state=STATE.BEFORE;
                            money-=bet;
                            checkBefore();

                        }
                    };
                    h.postDelayed(r1,pause);
                    pause+=1000;
                  }
            }
            else if(dealerScore<21&&dealerScore>=17){
                if(dealerScore==playerScore){
                    Toast.makeText(getApplicationContext(), "Tie, you have a push!", Toast.LENGTH_SHORT).show(); //add a sleep function

                    Runnable r1 = new Runnable() {

                        @Override
                        public void run() {
                            // do first thing
                            state=STATE.BEFORE;
                            checkBefore();

                        }
                    };
                    h.postDelayed(r1,pause);
                    pause+=1000;
                }
                else if(dealerScore>playerScore){
                    Toast.makeText(getApplicationContext(), "Dealer Wins!", Toast.LENGTH_SHORT).show(); //add a sleep function

                    Runnable r1 = new Runnable() {

                        @Override
                        public void run() {
                            // do first thing
                            money-=bet;
                            state=STATE.BEFORE;
                            checkBefore();
                        }
                    };
                    h.postDelayed(r1,pause);
                    pause+=1000;
                }
                else{
                    Toast.makeText(getApplicationContext(), "You Win!", Toast.LENGTH_SHORT).show(); //add a sleep function

                    Runnable r1 = new Runnable() {

                        @Override
                        public void run() {
                            // do first thing
                            money+=bet;
                            state=STATE.BEFORE;
                            checkBefore();
                        }
                    };
                    h.postDelayed(r1,pause);
                    pause+=1000;
                }
            }else{
                while(dealerScore<17){
                    dealerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
                    deck.getDeck().remove(deck.getDeck().size()-1);

                    TextView tv = (TextView) findViewById(R.id.deck_count_textView);
                    tv.setText(String.valueOf(deck.getDeck().size()));


                    Runnable r1 = new Runnable() {

                        @Override
                        public void run() {
                            // do first thing

                            dealerImages.get(dealerHand.getDeck().size()-1).setImageResource(dealerHand.getDeck().get(dealerHand.getDeck().size()-1).getImage());
                            dealerImages.get(dealerHand.getDeck().size()-1).setVisibility(View.VISIBLE);

                        }
                    };

                    h.postDelayed(r1,pause);
                    pause+=1000;
                    dealerScore+=dealerHand.getDeck().get(dealerHand.getDeck().size()-1).getValue();
                }
                if(dealerScore==playerScore&&dealerScore!=21){
                    Toast.makeText(getApplicationContext(), "Tie, you have a push!", Toast.LENGTH_SHORT).show(); //add a sleep function

                    Runnable r1 = new Runnable() {

                        @Override
                        public void run() {
                            // do first thing
                            state=STATE.BEFORE;
                            checkBefore();
                        }
                    };
                    h.postDelayed(r1,pause);
                    pause+=1000;
                }
                else if(dealerScore>playerScore){
                    Toast.makeText(getApplicationContext(), "Dealer Wins!", Toast.LENGTH_SHORT).show(); //add a sleep function

                    Runnable r1 = new Runnable() {

                        @Override
                        public void run() {
                            // do first thing
                            money-=bet;
                            state=STATE.BEFORE;
                            checkBefore();
                        }
                    };
                    h.postDelayed(r1,pause);
                    pause+=1000;
                }
                else{
                    Toast.makeText(getApplicationContext(), "You Win!", Toast.LENGTH_SHORT).show(); //add a sleep function

                    Runnable r1 = new Runnable() {

                        @Override
                        public void run() {
                            // do first thing

                            money+=bet;
                            state=STATE.BEFORE;
                            checkBefore();
                        }
                    };
                    h.postDelayed(r1,pause);
                    pause+=1000;
                }
            }
        }
    }

    public void countDealerScore(){

        dealerScore = 0;
        for(int i = 0; i < dealerHand.getDeck().size(); i++) {
            dealerScore += dealerHand.getDeck().get(i).getValue();
            if(dealerScore>21){
                for(int j = 0; j < dealerHand.getDeck().size(); j++){
                    if(dealerHand.getDeck().get(i).getValue()==11)
                       dealerScore-=10;
                }
            }
        }
    }
    public void countPlayerScore()
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
    public void outputMessage(int context, int amount, String myState,String startNext){

      //  if()
    }
}