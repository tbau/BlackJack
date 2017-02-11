package com.bau_hornick.blackjack;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Timer;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener, View.OnTouchListener {

    private int bet=0;
    private int money = 5000;

    private Deck deck;
    private Deck playerHand;
    private Deck dealerHand;

    private int playerScore = 0;
    private int dealerScore=0;

    private boolean hasBlackJack;

    private ArrayList<ImageView> playerImages;
    private ArrayList<ImageView> dealerImages;

    //Image views to hold 10 cards for dealer hand
    private int[] dealerImageViews = {R.id.dealer_card1_imageView, R.id.dealer_card2_imageView,
            R.id.dealer_card3_imageView, R.id.dealer_card4_imageView,
            R.id.dealer_card5_imageView, R.id.dealer_card6_imageView,
            R.id.dealer_card7_imageView, R.id.dealer_card8_imageView,
            R.id.dealer_card9_imageView, R.id.dealer_card10_imageView};

    //Image views to hold 10 cards for player hand
    private int[] playerImageViews = {R.id.player_card1_imageView, R.id.player_card2_imageView,
            R.id.player_card3_imageView,R.id.player_card4_imageView,
            R.id.player_card5_imageView, R.id.player_card6_imageView, R.id.player_card7_imageView,
            R.id.player_card8_imageView, R.id.player_card9_imageView, R.id.player_card10_imageView};

    enum STATE{BEFORE, PLAYER,DEALER,LOSE}
    private STATE state=STATE.BEFORE;

    enum OutputContext{TIE,DEALER,PLAYER,STAND,BUSTED,NATURAL,BLACKJACK}

    private Timer timer1;
    private Timer timer2;

    private TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        ImageView im = (ImageView) findViewById(R.id.poker_chip1_imageView);
        im.setOnTouchListener(this);

        im = (ImageView) findViewById(R.id.poker_chip5_imageView);
        im.setOnTouchListener(this);

        im = (ImageView) findViewById(R.id.poker_chip10_imageView);
        im.setOnTouchListener(this);

        im = (ImageView) findViewById(R.id.poker_chip25_imageView);
        im.setOnTouchListener(this);

        im = (ImageView) findViewById(R.id.poker_chip50_imageView);
        im.setOnTouchListener(this);

        im = (ImageView) findViewById(R.id.poker_chip100_imageView);
        im.setOnTouchListener(this);

        im.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                bet=money;
                tv = (TextView) findViewById(R.id.bet_textView);
                tv.setText("$"+String.valueOf(bet));
                return false;
            }
        });

        Button b;

        //set onClickListeners for buttons
        b = (Button) findViewById(R.id.start_button);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.hit_button);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.stand_button);
        b.setOnClickListener(this);

        //When app restarts/resets, grab data from savedInstanceState
        if(savedInstanceState!=null){
            money = savedInstanceState.getInt("money"); //get player's money amount
            bet=savedInstanceState.getInt("bet");
            deck=(Deck)savedInstanceState.getSerializable("deck"); //Get deck object as a serializable

            tv = (TextView) findViewById(R.id.current_money_textView); //setting textViews
            tv.setText("$"+String.valueOf(money));
            tv = (TextView) findViewById(R.id.bet_textView);
            tv.setText("$"+String.valueOf(bet));}

        if(deck==null){ //if game is new and deck object empty, get a new deck.
            deck=new Deck(1);}

        if(playerHand==null){ //if playerHand has no cards, get a new deck
            playerHand=new Deck(0);}

        if(dealerHand==null) { //if dealerHand has no cards, get a new Deck
            dealerHand = new Deck(0);}

        if(playerImages==null){ //getting playerImages
            playerImages = new ArrayList<ImageView>();}

        if(dealerImages==null){ //getting dealerImages
            dealerImages = new ArrayList<ImageView>();}

        //Go through each image view resource id, and add it to dealerImages and playerImages ArrayLists
        for(int i = 0; i < dealerImageViews.length; i++)
        {
            ImageView dealerImg = (ImageView) findViewById(dealerImageViews[i]);
            dealerImages.add(dealerImg);
            dealerImages.get(i).setVisibility(View.INVISIBLE); //set to invisible

            ImageView playerImg = (ImageView) findViewById(playerImageViews[i]);
            playerImages.add(playerImg);
            playerImages.get(i).setVisibility(View.INVISIBLE); //set to invisible
        }

        Intent intent = getIntent(); //If user selects 'Resume' get data from previous session from file

        if(intent.hasExtra("resume")){
            if(intent.getBooleanExtra("resume", false)){
                getFile();}}

        if(money==0){
            state=STATE.LOSE;
        }
        //Display number of cards in deck
        tv = (TextView) findViewById(R.id.deck_count_textView);
        tv.setText(String.valueOf(deck.getDeck().size()));

        //Display bet and total amount of money in parentheses
        tv = (TextView) findViewById(R.id.bet_textView);
        tv.setText("$"+String.valueOf(bet));

        //Display total money earned next to bet - button
        tv = (TextView) findViewById(R.id.current_money_textView);
        tv.setText("$"+String.valueOf(money));

    }

    // OnTouchListener for poker chips
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:{

                tv = (TextView) findViewById(R.id.bet_textView);
                if (state == STATE.BEFORE && v.getId() == R.id.poker_chip1_imageView&&event.getX()>40&&event.getX()<v.getWidth()-40
                        &&event.getY()>40&&event.getY()<v.getHeight()-40) {
                    bet += 1;
                }
                if (state == STATE.BEFORE && v.getId() == R.id.poker_chip5_imageView&&event.getX()>40&&event.getX()<v.getWidth()-40
                        &&event.getY()>40&&event.getY()<v.getHeight()-40){
                    bet += 5;
                }
                if (state == STATE.BEFORE && v.getId() == R.id.poker_chip10_imageView&&event.getX()>40&&event.getX()<v.getWidth()-40
                        &&event.getY()>40&&event.getY()<v.getHeight()-40) {
                    bet += 10;
                }
                if (state == STATE.BEFORE && v.getId() == R.id.poker_chip25_imageView&&event.getX()>40&&event.getX()<v.getWidth()-40
                        &&event.getY()>40&&event.getY()<v.getHeight()-40) {
                    bet += 25;
                }
                if (state == STATE.BEFORE && v.getId() == R.id.poker_chip50_imageView&&event.getX()>40&&event.getX()<v.getWidth()-40
                        &&event.getY()>40&&event.getY()<v.getHeight()-40) {
                    bet += 50;
                }
                if (state == STATE.BEFORE && v.getId() == R.id.poker_chip100_imageView&&event.getX()>40&&event.getX()<v.getWidth()-40
                        &&event.getY()>40&&event.getY()<v.getHeight()-40) {
                    bet += 100;
                }
                if (bet > money)
                    bet = money;
                if (bet < 0)
                    bet = 0;

                tv.setText("$" + String.valueOf(bet));

                //Display total money earned next to bet - button
                tv = (TextView) findViewById(R.id.current_money_textView);
                tv.setText("$" + String.valueOf(money));
            }}
        return false;
    }


    @Override
    public void onClick(View v) {
        final Handler h = new Handler();

        //When start button is pressed, and the state is BEFORE, start the game
        if (v.getId() == R.id.start_button && state==STATE.BEFORE&&bet!=0){

            getTotalScore();  //Checking to see if deck has enough values, if not then reset deck.
            state = STATE.PLAYER; //update state to PLAYER, player turn starts.
            checkPlayer();//start player's turn

        }else if(v.getId()==R.id.hit_button&&state==STATE.PLAYER) {//When 'HIT' button is clicked after game is started.
        Button b = (Button) findViewById(R.id.hit_button);
            b.setClickable(false);
            b = (Button) findViewById(R.id.stand_button);
            b.setClickable(false);
            if (playerHand.getDeck().size() < 10) { //if number of cards in player hand is less than 5
                playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size() - 1)); //add card to player hand from deck
                deck.getDeck().remove(deck.getDeck().size() - 1); //remove the card that was added to player from deck

                //display image for card, set to visible
                playerImages.get(playerHand.getDeck().size() - 1).setImageResource(playerHand.getDeck().get(playerHand.getDeck().size() - 1).getImage());
                playerImages.get(playerHand.getDeck().size() - 1).setVisibility(View.VISIBLE);

                countPlayerScore(); //Get player's current score after they get a new card

                tv = (TextView) findViewById(R.id.deck_count_textView);
                tv.setText(String.valueOf(deck.getDeck().size())); //update deck size in TextView

                if (playerHand.getDeck().size() == 10 && playerScore < 21) { //Player can't get more than 10 cards

                    Toast.makeText(getApplicationContext(), "You have reached the ten card limit!", Toast.LENGTH_SHORT).show();

                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            state = STATE.DEALER; //change state to DEALER when player reaches 10 cards and not 21
                            checkDealer();
                        }
                    };
                    h.postDelayed(r1, 1000);}
                else if (playerScore == 21) {
                    outputMessage(OutputContext.NATURAL,0,STATE.DEALER,"DEALER"); //Player has natural, dealer's turn starts
                } else if (playerScore > 21) {
                    outputMessage(OutputContext.BUSTED,-bet,STATE.BEFORE,"BEFORE"); //Player busts
                }
                b = (Button) findViewById(R.id.hit_button);
                b.setClickable(true);
                b = (Button) findViewById(R.id.stand_button);
                b.setClickable(true);

            }}else if (v.getId() == R.id.stand_button && state == STATE.PLAYER) { //Player stands, dealer's turn starts

            countPlayerScore(); //Count player score

            if (playerScore == 21) {
                outputMessage(OutputContext.NATURAL,0,STATE.DEALER,"DEALER"); //Player has natural, dealer's turn starts
            } else {
                outputMessage(OutputContext.STAND,0,STATE.DEALER,"DEALER");
            }
        }
    }
    public void getTotalScore(){
        int totalValues=0;
        for(int i=0;i<deck.getDeck().size();i++){
            if(deck.getDeck().get(i).getValue()==11)
                totalValues+=1;
            else
                totalValues+=deck.getDeck().get(i).getValue();
        }
        if(totalValues<=50)
            deck.reset();
    }
    public void checkBefore(){
         //Before the game starts, reset everything
            dealerScore=0;
            playerScore=0;

            //for each player card, set visibility to invisible
            for(int i=0;i<playerHand.getDeck().size();i++){
                playerImages.get(i).setVisibility(View.INVISIBLE);}

            //for each dealer card, set visibility to invisible
            for(int i=0;i<dealerHand.getDeck().size();i++){
                dealerImages.get(i).setVisibility(View.INVISIBLE);}

            //Clear dealer and player deck objects
            dealerHand.getDeck().clear();
            playerHand.getDeck().clear();

            if(bet>money)
                bet=money;
            if(money==0){ //Reset money amount when user reaches 0
                Toast.makeText(getApplicationContext(),"You are out of money! You lose",Toast.LENGTH_LONG).show();
                }
            bet=0;

            tv = (TextView) findViewById(R.id.bet_textView);
            tv.setText("$"+String.valueOf(bet));
            tv = (TextView) findViewById(R.id.current_money_textView);
            tv.setText("$"+String.valueOf(money));

            writeToFile();

    }
    public void checkPlayer(){

        if(state==STATE.PLAYER){
            playerScore=0;
            hasBlackJack=false;

            Button b = (Button) findViewById(R.id.stand_button);  //Prevent standing or hitting until cards are drawn
            b.setClickable(false);
            b = (Button) findViewById(R.id.hit_button);
            b.setClickable(false);

            //First two cards
            dealerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
            deck.getDeck().remove(deck.getDeck().size()-1);

            //Set first dealer card to the back
            dealerImages.get(0).setImageResource(R.drawable.red_card_back3);
            dealerImages.get(0).setVisibility(View.VISIBLE);

            //display Deck count
            tv = (TextView) findViewById(R.id.deck_count_textView);
            tv.setText(String.valueOf(deck.getDeck().size()));

            //Display bet/money
            tv = (TextView) findViewById(R.id.bet_textView);
            tv.setText("$"+String.valueOf(bet));

            final Handler h = new Handler();

            Runnable r1 = new Runnable() {

                @Override
                public void run() {
                    //adding to playerHand
                   try {
                       playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size() - 1));
                       deck.getDeck().remove(deck.getDeck().size() - 1);

                       //adding and displaying first card to playerHand
                       playerImages.get(0).setImageResource(playerHand.getDeck().get(0).getImage());
                       playerImages.get(0).setVisibility(View.VISIBLE);

                       tv = (TextView) findViewById(R.id.deck_count_textView);
                       tv.setText(String.valueOf(deck.getDeck().size()));
                   }catch (Exception e){}
                }
            };
            Runnable r2 = new Runnable() {

                @Override
                public void run() {
                    //adding to dealerHand
                  try {
                      dealerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size() - 1));
                      deck.getDeck().remove(deck.getDeck().size() - 1);

                      //adding and displaying first card to dealerHand
                      dealerImages.get(1).setImageResource(dealerHand.getDeck().get(1).getImage());
                      dealerImages.get(1).setVisibility(View.VISIBLE);

                      tv = (TextView) findViewById(R.id.deck_count_textView);
                      tv.setText(String.valueOf(deck.getDeck().size()));
                  }catch (Exception e){}
                }
            };
            Runnable r3 = new Runnable() {

                @Override
                public void run() {
                    //Get next card off deck and add to player hand
                    playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
                    deck.getDeck().remove(deck.getDeck().size()-1); //remove from deck

                    //display 2nd player card
                    playerImages.get(1).setImageResource(playerHand.getDeck().get(1).getImage());
                    playerImages.get(1).setVisibility(View.VISIBLE);

                    //display updated deck count
                    tv = (TextView) findViewById(R.id.deck_count_textView);
                    tv.setText(String.valueOf(deck.getDeck().size()));

                    //Now that player has 2 cards, count their score
                    countPlayerScore();

                    if(playerScore == 21) //check if player already has blackjack.
                    {
                        hasBlackJack=true;
                        outputMessage(OutputContext.BLACKJACK,0,STATE.DEALER,"DEALER");}

                    Button b = (Button) findViewById(R.id.stand_button);
                    b.setClickable(true);
                    b = (Button) findViewById(R.id.hit_button);
                    b.setClickable(true);
                }
            };

            //Handler delays for r1, r2, and r3
            h.postDelayed(r1, 250);
            h.postDelayed(r2, 500);
            h.postDelayed(r3, 750);
        }}

    public void checkDealer(){
        if(state==STATE.DEALER){
            Button b = (Button) findViewById(R.id.hit_button);
            b.setClickable(false);
            b = (Button) findViewById(R.id.stand_button);
            b.setClickable(false);

            //Count both scores when State is Dealer
            countDealerScore();
            countPlayerScore();

            final Handler h = new Handler();

            try {
                //display dealer card that was originally displayed as the card back
                dealerImages.get(0).setImageResource(dealerHand.getDeck().get(0).getImage());

                Runnable r1 = new Runnable() {
                    @Override
                    public void run() {
                        if (dealerScore == 21) {
                            //dealer wins
                            outputMessage(OutputContext.DEALER, -bet, STATE.BEFORE, "BEFORE");
                        } else if (hasBlackJack) {
                            //player Wins, display message
                            outputMessage(OutputContext.PLAYER, bet, STATE.BEFORE, "BEFORE");
                        } else if (dealerScore < 21 && dealerScore >= 17) {
                            if (dealerScore == playerScore) {
                                outputMessage(OutputContext.TIE, 0, STATE.BEFORE, "BEFORE"); //tie
                            } else if (dealerScore > playerScore) {
                                outputMessage(OutputContext.DEALER, -bet, STATE.BEFORE, "BEFORE"); //dealer wins
                            } else {
                                outputMessage(OutputContext.PLAYER, bet, STATE.BEFORE, "BEFORE"); //player wins
                            }
                        } else {
                            while (dealerScore < 17 && dealerHand.getDeck().size() < 10) {
                                //keep adding cards to deck while score isn't 17 and they haven't reach 10 card limit
                                dealerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size() - 1));
                                deck.getDeck().remove(deck.getDeck().size() - 1);

                                //display new count of cards in Deck
                                tv = (TextView) findViewById(R.id.deck_count_textView);
                                tv.setText(String.valueOf(deck.getDeck().size()));

                                //display image from dealerHand
                                dealerImages.get(dealerHand.getDeck().size() - 1).setImageResource(dealerHand.getDeck().get(dealerHand.getDeck().size() - 1).getImage());
                                dealerImages.get(dealerHand.getDeck().size() - 1).setVisibility(View.VISIBLE);

                                countDealerScore(); //check score again
                            }
                            Runnable r2 = new Runnable() {
                                @Override
                                public void run() {
                                    //Check if it's a win, tie, or lose for player
                                    if (dealerScore == playerScore)
                                        outputMessage(OutputContext.TIE, 0, STATE.BEFORE, "BEFORE");
                                    else if (dealerScore > playerScore && dealerScore <= 21)
                                        outputMessage(OutputContext.DEALER, -bet, STATE.BEFORE, "BEFORE");
                                    else
                                        outputMessage(OutputContext.PLAYER, bet, STATE.BEFORE, "BEFORE");

                                }
                            };
                            h.postDelayed(r2, 1000);
                        }
                    }
                };
                h.postDelayed(r1, 1000);
            }catch (Exception e){}

        }
    }

    //counts score for dealer hand
    public void countDealerScore(){

        int numOfAces=0;
        dealerScore = 0;

        //go through dealerHand and add to dealer score using the card's value
        for(int i = 0; i < dealerHand.getDeck().size(); i++) {
            dealerScore += dealerHand.getDeck().get(i).getValue();}

        //When score is over 21, check to see how many aces there are
        if(dealerScore>21){
            for(int j = 0; j < dealerHand.getDeck().size(); j++){
                if(dealerHand.getDeck().get(j).getValue()==11)
                {
                    numOfAces++;
                }
            }
            //For the number of aces in the hand when score is over 21, subtract 10 from dealerscore so that Ace is now a 1
            while(numOfAces>0&&dealerScore>21){
                dealerScore-=10;
                numOfAces--;}
        }
    }

    //counts Player Score
    public void countPlayerScore()
    {
        int numOfAces=0;
        playerScore = 0;

        //go through playerHand and add to player score using the card's value
        for(int i = 0; i < playerHand.getDeck().size(); i++) {
            playerScore += playerHand.getDeck().get(i).getValue();
        }

        //When score is over 21, check to see how many aces there are
        if(playerScore>21){
            for(int j = 0; j < playerHand.getDeck().size(); j++){
                if(playerHand.getDeck().get(j).getValue()==11)
                    numOfAces++;
            }

            //For the number of aces in the hand when score is over 21, subtract 10 from playerScore so that Ace is now a 1
            while(numOfAces>0&&playerScore>21){
                playerScore-=10;
                numOfAces--;
            }
        }
    }

    //Output Toast message depending on OutputContext
    public void outputMessage(OutputContext context, final int amount, final STATE mystate, final String startNext){

        if(context.equals(OutputContext.DEALER)) //Dealer Wins
            Toast.makeText(getApplicationContext(), "Dealer scored "+dealerScore+". Dealer Wins!", Toast.LENGTH_SHORT).show();

        else if(context.equals(OutputContext.PLAYER)){ //You Win
            if(dealerScore>21)
                Toast.makeText(getApplicationContext(), "Dealer scored "+dealerScore+". Dealer Busted!", Toast.LENGTH_SHORT).show();
            else
                Toast.makeText(getApplicationContext(), "Dealer scored "+dealerScore+". You Win!", Toast.LENGTH_SHORT).show();
        }

        else if(context.equals(OutputContext.TIE)) //Tie
            Toast.makeText(getApplicationContext(), "Tie, you have a push!", Toast.LENGTH_SHORT).show();

        else if(context.equals(OutputContext.STAND)) //Stand
            Toast.makeText(getApplicationContext(), "You scored " + playerScore, Toast.LENGTH_SHORT).show();

        else if(context.equals(OutputContext.BUSTED)) //Busted
            Toast.makeText(getApplicationContext(), "You scored " + playerScore + ". You have busted!", Toast.LENGTH_SHORT).show();

        else if(context.equals(OutputContext.NATURAL)) //Natural
            Toast.makeText(getApplicationContext(), "You have a natural!", Toast.LENGTH_SHORT).show();

        else if(context.equals(OutputContext.BLACKJACK)) //BlackJack
            Toast.makeText(getApplicationContext(), "You have a Blackjack!", Toast.LENGTH_SHORT).show();

        money+=amount;
        if(mystate==STATE.PLAYER||mystate==STATE.DEALER)
        state=mystate;

        Button b = (Button) findViewById(R.id.hit_button);
        b.setClickable(true);
        b = (Button) findViewById(R.id.stand_button);
        b.setClickable(true);

        final Handler h=new Handler();

        //after output message, check what to start next.
        Runnable r1 = new Runnable() {
            @Override
            public void run() {
                // do first thing

                state=mystate;
                switch (startNext) {
                    case "BEFORE":
                        checkBefore();
                        break;
                    case "PLAYER":
                        checkPlayer();
                        break;
                    default:
                        checkDealer();
                        break;
                }
            }
        };
        h.postDelayed(r1,2000);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putInt("money",money);
        outState.putInt("bet",bet);

        getTotalScore();        //Make sure there are still cards left

        deck.getDeck().remove(deck.getDeck().size()-1);    //Remove top 4 cards to prevent cheating
        deck.getDeck().remove(deck.getDeck().size()-1);
        deck.getDeck().remove(deck.getDeck().size()-1);
        deck.getDeck().remove(deck.getDeck().size()-1);

        writeToFile();
        outState.putSerializable("deck",deck);

        super.onSaveInstanceState(outState);
    }

    //collect data from File to 'Resume' game
    protected void getFile(){
        try {
            FileInputStream fis = openFileInput("blackjack.txt");
            Scanner s = new Scanner(fis);

            money = s.nextInt();
            bet=s.nextInt();
            int deckSize=s.nextInt();
            deck.getDeck().clear();

            for(int i=0;i<deckSize;i++){
                // File there is still stuff to read

                Card temp=new Card(0,0,"",false);
                temp.setImage(s.nextInt());
                temp.setValue(s.nextInt());
                temp.setSuit(s.next());
                temp.setVisible(s.nextBoolean());

                deck.getDeck().add(temp);     // Puts the Card into my list
            }

            s.close();

        } catch (FileNotFoundException e) {
            Log.i("ReadData", "no input file found");
        }
    }

    //Write data to file to save information
    protected void writeToFile(){
        try {
            FileOutputStream fos = openFileOutput("blackjack.txt", Context.MODE_PRIVATE);     // Setup writing to file

            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println(money);
            pw.println(bet);
            pw.println(deck.getDeck().size());

            for(int i=0;i<deck.getDeck().size();i++)           // Writes Deck to file
            {
                pw.println(deck.getDeck().get(i).getImage());
                pw.println(deck.getDeck().get(i).getValue());
                pw.println(deck.getDeck().get(i).getSuit());
                pw.println(deck.getDeck().get(i).isVisible());
            }

            pw.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Toast.makeText(this,"Can't write to file", Toast.LENGTH_LONG).show();
        }
    }
}