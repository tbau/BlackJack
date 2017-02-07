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
import java.util.TimerTask;

public class PlayActivity extends AppCompatActivity implements View.OnClickListener {

    private int bet=1;
    private int money = 100;
    private boolean betIncrease=false;
    private boolean betDecrease=false;

    private Deck deck;
    private Deck playerHand;
    private Deck dealerHand;

    private int playerScore = 0;
    private int dealerScore=0;

    private boolean hasBlackJack;

    private ArrayList<ImageView> playerImages;
    private ArrayList<ImageView> dealerImages;

    private int[] dealerImageViews = {R.id.dealer_card1_imageView, R.id.dealer_card2_imageView,
            R.id.dealer_card3_imageView, R.id.dealer_card4_imageView,
            R.id.dealer_card5_imageView, R.id.dealer_card6_imageView,
            R.id.dealer_card7_imageView, R.id.dealer_card8_imageView,
            R.id.dealer_card9_imageView, R.id.dealer_card10_imageView};
    private int[] playerImageViews = {R.id.player_card1_imageView, R.id.player_card2_imageView,
            R.id.player_card3_imageView,R.id.player_card4_imageView,
            R.id.player_card5_imageView, R.id.player_card6_imageView, R.id.player_card7_imageView,
            R.id.player_card8_imageView, R.id.player_card9_imageView, R.id.player_card10_imageView};


    enum STATE{BEFORE, PLAYER,DEALER}
    private STATE state=STATE.BEFORE;

    enum OutputContext{TIE,DEALER,PLAYER,STAND,BUSTED,NATURAL,BLACKJACK}

    private Timer timer1;
    private Timer timer2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Button b = (Button) findViewById(R.id.increase_bet_button);
        b.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        betIncrease=true;
                        return true;
                    }

                    case MotionEvent.ACTION_UP:
                    {
                        betIncrease=false;
                        return true;
                    }
                }
                return false;
            }
        });

        b = (Button) findViewById(R.id.decrease_bet_button);
        b.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction())
                {
                    case MotionEvent.ACTION_DOWN:
                    {
                        betDecrease=true;
                        return true;
                    }

                    case MotionEvent.ACTION_UP:
                    {
                        betDecrease=false;
                        return true;
                    }
                }
                return false;
            }
        });

        timer1 = new Timer();
        timer1.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (state == STATE.BEFORE&&betDecrease) {
                    bet--;
                    if (bet < 1&&money!=0)
                        bet++;
                    final TextView tv = (TextView) findViewById(R.id.bet_textView);
                    tv.post(new Runnable() {
                        @Override
                        public void run() {
                            tv.setText(bet + "(" + (money - bet) + ")");
                        }
                    });
                }}}, 0, 100);

        timer2 = new Timer();
        timer2.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (state==STATE.BEFORE&&betIncrease) {
                    bet++;
                    if(bet>money)
                        bet=money;
                    final TextView betView = (TextView) findViewById(R.id.bet_textView);
                    betView.post(new Runnable() {
                                     @Override
                                     public void run() {
                                         betView.setText(bet + "(" + (money - bet) + ")");
                                     }
                                 }
                    );
                }}

        },0,100);

        b = (Button) findViewById(R.id.start_button);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.hit_button);
        b.setOnClickListener(this);

        b = (Button) findViewById(R.id.stand_button);
        b.setOnClickListener(this);

        if(savedInstanceState!=null){
            bet = savedInstanceState.getInt("bet");
            money = savedInstanceState.getInt("money");
            deck=(Deck)savedInstanceState.getSerializable("deck");
            TextView tv = (TextView) findViewById(R.id.current_money_textView);
            tv.setText(String.valueOf(money));
            tv = (TextView) findViewById(R.id.bet_textView);
            tv.setText(bet + "(" + (money - bet) + ")");

        }
        if(deck==null){
            deck=new Deck(1);
        }
        if(playerHand==null){
            playerHand=new Deck(0);
        }

        if(dealerHand==null) {
            dealerHand = new Deck(0);
        }
        if(playerImages==null){
            playerImages = new ArrayList<ImageView>();
        }
        if(dealerImages==null){
            dealerImages = new ArrayList<ImageView>();
        }

        for(int i = 0; i < dealerImageViews.length; i++)
        {
            ImageView dealerImg = (ImageView) findViewById(dealerImageViews[i]);
            dealerImages.add(dealerImg);
            dealerImages.get(i).setVisibility(View.INVISIBLE);

            ImageView playerImg = (ImageView) findViewById(playerImageViews[i]);
            playerImages.add(playerImg);
            playerImages.get(i).setVisibility(View.INVISIBLE);
        }
        Intent intent = getIntent();
        if(intent.hasExtra("resume")){
            if(intent.getBooleanExtra("resume", false)){
                getFile();
            }
        }
        TextView tv = (TextView) findViewById(R.id.deck_count_textView);
        tv.setText(String.valueOf(deck.getDeck().size()));

        tv = (TextView) findViewById(R.id.bet_textView);
        tv.setText(bet + "(" + (money - bet) + ")");

        tv = (TextView) findViewById(R.id.current_money_textView);
        tv.setText(String.valueOf(money));
    }

    @Override
    public void onClick(View v) {
        final Handler h = new Handler();
        if (v.getId() == R.id.start_button && state==STATE.BEFORE){

            getTotalScore();
            state = STATE.PLAYER;
            checkPlayer();
        }else if(v.getId()==R.id.hit_button&&state==STATE.PLAYER) {

            if (playerHand.getDeck().size() < 5) {
                playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size() - 1));
                deck.getDeck().remove(deck.getDeck().size() - 1);

                playerImages.get(playerHand.getDeck().size() - 1).setImageResource(playerHand.getDeck().get(playerHand.getDeck().size() - 1).getImage());
                playerImages.get(playerHand.getDeck().size() - 1).setVisibility(View.VISIBLE);
                countPlayerScore();
                TextView tv = (TextView) findViewById(R.id.deck_count_textView);
                tv.setText(String.valueOf(deck.getDeck().size()));

                if (playerHand.getDeck().size() == 10 && playerScore < 21) {

                    Toast.makeText(getApplicationContext(), "You have reached the ten card limit!", Toast.LENGTH_SHORT).show();

                    Runnable r1 = new Runnable() {
                        @Override
                        public void run() {
                            state = STATE.DEALER;
                            checkDealer();
                        }
                    };
                    h.postDelayed(r1, 1000);
                } else if (playerScore == 21) {
                    outputMessage(OutputContext.NATURAL,0,STATE.DEALER,"DEALER");
                } else if (playerScore > 21) {
                    outputMessage(OutputContext.BUSTED,-bet,STATE.BEFORE,"BEFORE");

                }
            } }else if (v.getId() == R.id.stand_button && state == STATE.PLAYER) {

            countPlayerScore();

            if (playerScore == 21) {
                outputMessage(OutputContext.NATURAL,0,STATE.DEALER,"DEALER");
            } else {
                outputMessage(OutputContext.STAND,0,STATE.DEALER,"DEALER");
            }
        }
    }
    public void getTotalScore(){
        int totalValues=0;
        for(int i=0;i<deck.getDeck().size();i++){
            if(deck.getDeck().get(i).getValue()==11)
            {
                totalValues+=1;
            }
            else
                totalValues+=deck.getDeck().get(i).getValue();
        }

        if(totalValues<=50)
            deck.reset();
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

            if(bet>money)
                bet=money;
            if(money==0){
                Toast.makeText(getApplicationContext(),"You are out of money! Here is 100 to bet with",Toast.LENGTH_LONG).show();
                money=100;
                bet=1;
            }


            TextView betView = (TextView) findViewById(R.id.bet_textView);
            betView.setText(bet + "(" + (money - bet) + ")");
            betView = (TextView) findViewById(R.id.current_money_textView);
            betView.setText(String.valueOf(money));

            writeToFile();
        }
    }
    public void checkPlayer(){

        if(state==STATE.PLAYER){
            playerScore=0;
            hasBlackJack=false;

            Button b = (Button) findViewById(R.id.stand_button);    //Prevent standing or hitting until cards are drawn
            b.setEnabled(false);
            b = (Button) findViewById(R.id.hit_button);
            b.setEnabled(false);

            //First two cards
            dealerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
            deck.getDeck().remove(deck.getDeck().size()-1);

            dealerImages.get(0).setImageResource(R.drawable.red_card_back);
            dealerImages.get(0).setVisibility(View.VISIBLE);

            TextView tv = (TextView) findViewById(R.id.deck_count_textView);
            tv.setText(String.valueOf(deck.getDeck().size()));

            tv = (TextView) findViewById(R.id.bet_textView);
            tv.setText(bet + "(" + (money - bet) + ")");

            final Handler h = new Handler();

            Runnable r1 = new Runnable() {

                @Override
                public void run() {
                    // do first thing

                    playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
                    deck.getDeck().remove(deck.getDeck().size()-1);

                    playerImages.get(0).setImageResource(playerHand.getDeck().get(0).getImage());
                    playerImages.get(0).setVisibility(View.VISIBLE);

                    TextView tv = (TextView) findViewById(R.id.deck_count_textView);
                    tv.setText(String.valueOf(deck.getDeck().size()));

                }
            };
            Runnable r2 = new Runnable() {

                @Override
                public void run() {
                    // do first thing

                    dealerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
                    deck.getDeck().remove(deck.getDeck().size()-1);

                    dealerImages.get(1).setImageResource(dealerHand.getDeck().get(1).getImage());
                    dealerImages.get(1).setVisibility(View.VISIBLE);

                    TextView tv = (TextView) findViewById(R.id.deck_count_textView);
                    tv.setText(String.valueOf(deck.getDeck().size()));

                }
            };
            Runnable r3 = new Runnable() {

                @Override
                public void run() {
                    // do first thing


                    playerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
                    deck.getDeck().remove(deck.getDeck().size()-1);

                    playerImages.get(1).setImageResource(playerHand.getDeck().get(1).getImage());
                    playerImages.get(1).setVisibility(View.VISIBLE);

                    TextView tv = (TextView) findViewById(R.id.deck_count_textView);
                    tv.setText(String.valueOf(deck.getDeck().size()));

                    countPlayerScore();

                    if(playerScore == 21)
                    {
                        hasBlackJack=true;
                        outputMessage(OutputContext.BLACKJACK,0,STATE.DEALER,"DEALER");
                    }

                    Button b = (Button) findViewById(R.id.stand_button);
                    b.setEnabled(true);
                    b = (Button) findViewById(R.id.hit_button);
                    b.setEnabled(true);

                }
            };
            h.postDelayed(r1, 500);
            h.postDelayed(r2, 1000);
            h.postDelayed(r3, 1500);

        }}

    public void checkDealer(){
        if(state==STATE.DEALER){
            countDealerScore();
            countPlayerScore();

            final Handler h = new Handler();

            dealerImages.get(0).setImageResource(dealerHand.getDeck().get(0).getImage());

            Runnable r1 = new Runnable() {
                @Override
                public void run() {
                    if(dealerScore == 21)
                    {
                        if(playerScore==21)
                            outputMessage(OutputContext.TIE,0,STATE.BEFORE,"BEFORE");

                        else{
                            outputMessage(OutputContext.DEALER,-bet,STATE.BEFORE,"BEFORE");
                        }}
                    else if(hasBlackJack){
                        outputMessage(OutputContext.PLAYER,bet,STATE.BEFORE,"BEFORE");
                    }
                    else if(dealerScore<21&&dealerScore>=17){
                        if(dealerScore==playerScore){
                            outputMessage(OutputContext.TIE,0,STATE.BEFORE,"BEFORE");
                        }
                        else if(dealerScore>playerScore){
                            outputMessage(OutputContext.DEALER,-bet,STATE.BEFORE,"BEFORE");
                        }
                        else{
                            outputMessage(OutputContext.PLAYER,bet,STATE.BEFORE,"BEFORE");
                        }
                    }else{
                        while(dealerScore<17&&dealerHand.getDeck().size()<10){
                            dealerHand.getDeck().add(deck.getDeck().get(deck.getDeck().size()-1));
                            deck.getDeck().remove(deck.getDeck().size()-1);

                            TextView tv = (TextView) findViewById(R.id.deck_count_textView);
                            tv.setText(String.valueOf(deck.getDeck().size()));

                            dealerImages.get(dealerHand.getDeck().size()-1).setImageResource(dealerHand.getDeck().get(dealerHand.getDeck().size()-1).getImage());
                            dealerImages.get(dealerHand.getDeck().size()-1).setVisibility(View.VISIBLE);


                            countDealerScore();
                        }
                        Runnable r2 = new Runnable() {
                            @Override
                            public void run() {
                                if(dealerScore==playerScore){
                                    outputMessage(OutputContext.TIE,0,STATE.BEFORE,"BEFORE");

                                }
                                else if(dealerScore>playerScore&&dealerScore<=21){
                                    outputMessage(OutputContext.DEALER,-bet,STATE.BEFORE,"BEFORE");
                                }
                                else{
                                    outputMessage(OutputContext.PLAYER,bet,STATE.BEFORE,"BEFORE");
                                }
                            }
                        };
                        h.postDelayed(r2,1000);

                    }
                }

            };
            h.postDelayed(r1,1000);

        }}

    public void countDealerScore(){

        int numOfAces=0;
        dealerScore = 0;

        for(int i = 0; i < dealerHand.getDeck().size(); i++) {
            dealerScore += dealerHand.getDeck().get(i).getValue();

        }
        if(dealerScore>21){
            for(int j = 0; j < dealerHand.getDeck().size(); j++){
                if(dealerHand.getDeck().get(j).getValue()==11)
                {
                    numOfAces++;
                }
            }
            while(numOfAces>0&&dealerScore>21){
                dealerScore-=10;
                numOfAces--;
            }
        }
    }
    public void countPlayerScore()
    {
        int numOfAces=0;

        playerScore = 0;
        for(int i = 0; i < playerHand.getDeck().size(); i++) {
            playerScore += playerHand.getDeck().get(i).getValue();

        }
        if(playerScore>21){
            for(int j = 0; j < playerHand.getDeck().size(); j++){
                if(playerHand.getDeck().get(j).getValue()==11)
                {
                    numOfAces++;
                }
            }
            while(numOfAces>0&&playerScore>21){
                playerScore-=10;
                numOfAces--;
            }
        }
    }
    public void outputMessage(OutputContext context, final int amount, final STATE mystate, final String startNext){


        if(context.equals(OutputContext.DEALER)){ //Dealer Wins
            Toast.makeText(getApplicationContext(), "Dealer scored "+dealerScore+". Dealer Wins!", Toast.LENGTH_SHORT).show(); //add a sleep function
        }
        else if(context.equals(OutputContext.PLAYER)){ //You Win
            if(dealerScore>21)
                Toast.makeText(getApplicationContext(), "Dealer scored "+dealerScore+". Dealer Busted!", Toast.LENGTH_SHORT).show(); //add a sleep function
            else
                Toast.makeText(getApplicationContext(), "Dealer scored "+dealerScore+". You Win!", Toast.LENGTH_SHORT).show(); //add a sleep function
        }
        else if(context.equals(OutputContext.TIE)){ //Tie
            Toast.makeText(getApplicationContext(), "Tie, you have a push!", Toast.LENGTH_SHORT).show();
        }
        else if(context.equals(OutputContext.STAND)){ //Stand
            Toast.makeText(getApplicationContext(), "You scored " + playerScore, Toast.LENGTH_SHORT).show(); //Already added sleep function

        }
        else if(context.equals(OutputContext.BUSTED)){ //Busted
            Toast.makeText(getApplicationContext(), "You scored " + playerScore + ". You have busted!", Toast.LENGTH_SHORT).show(); //Already added sleep function

        }
        else if(context.equals(OutputContext.NATURAL)){ //Natural
            Toast.makeText(getApplicationContext(), "You have a natural!", Toast.LENGTH_SHORT).show(); //Already added sleep function
        }
        else if(context.equals(OutputContext.BLACKJACK)) { //BlackJack
            Toast.makeText(getApplicationContext(), "You have a Blackjack!", Toast.LENGTH_SHORT).show(); //add a sleep function
        }

        final Handler h=new Handler();
        Runnable r1 = new Runnable() {

            @Override
            public void run() {
                // do first thing
                money+=amount;
                state=mystate;
                if(startNext.equals("BEFORE"))
                    checkBefore();
                else if(startNext.equals("PLAYER"))
                    checkPlayer();
                else
                    checkDealer();
            }
        };
        h.postDelayed(r1,3000);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        timer1.cancel();
        timer2.cancel();

        outState.putInt("bet",bet);
        outState.putInt("money",money);

        getTotalScore();        //Make sure there are still cards left

        deck.getDeck().remove(deck.getDeck().size()-1);    //Remove top 4 cards to prevent cheating
        deck.getDeck().remove(deck.getDeck().size()-1);
        deck.getDeck().remove(deck.getDeck().size()-1);
        deck.getDeck().remove(deck.getDeck().size()-1);

        outState.putSerializable("deck",deck);

        super.onSaveInstanceState(outState);

    }
    protected void getFile(){

        try {
            FileInputStream fis = openFileInput("blackjack.txt");
            Scanner s = new Scanner(fis);

            money = s.nextInt();
            bet=s.nextInt();
            int deckSize=s.nextInt();
            deck.getDeck().clear();

            while(s.hasNext()){

                // File there is still stuff to read

                Card temp=new Card(0,0,"",false);
                temp.setImage(s.nextInt());
                temp.setValue(s.nextInt());
                temp.setSuit(s.next());
                temp.setVisible(s.nextBoolean());

                deck.getDeck().add(temp);             // Puts the Card into my list
            }
            s.close();

        } catch (FileNotFoundException e) {
            Log.i("ReadData", "no input file found");
        }
    }
    protected void writeToFile(){
        try {
            FileOutputStream fos = openFileOutput("blackjack.txt", Context.MODE_PRIVATE);     // Setup writing to file

            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter pw = new PrintWriter(bw);

            pw.println(money);
            pw.println(bet);
            pw.println(deck.getDeck().size());
            for(int i=0;i<deck.getDeck().size();i++)           // Writes Notes to file
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