package com.bau_hornick.blackjack;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Created by ahornick on 1/30/2017.
 */

public class Deck implements Serializable {
    private ArrayList<Card> deck; //vector of 52 cards
    private Random rand;
    private String suits[] = {"clubs", "diamonds", "hearts", "spades"};
    private int imageIds[]=
    {R.drawable.white_deck_2_of_clubs,R.drawable.white_deck_2_of_diamonds,R.drawable.white_deck_2_of_hearts,
     R.drawable.white_deck_2_of_spades,R.drawable.white_deck_3_of_clubs,R.drawable.white_deck_3_of_diamonds,
     R.drawable.white_deck_3_of_hearts,R.drawable.white_deck_3_of_spades,R.drawable.white_deck_4_of_clubs,
     R.drawable.white_deck_4_of_diamonds,R.drawable.white_deck_4_of_hearts,R.drawable.white_deck_4_of_spades,
     R.drawable.white_deck_5_of_clubs,R.drawable.white_deck_5_of_diamonds,R.drawable.white_deck_5_of_hearts,
     R.drawable.white_deck_5_of_spades,R.drawable.white_deck_6_of_clubs,R.drawable.white_deck_6_of_diamonds,
     R.drawable.white_deck_6_of_hearts,R.drawable.white_deck_6_of_spades,R.drawable.white_deck_7_of_clubs,
     R.drawable.white_deck_7_of_diamonds,R.drawable.white_deck_7_of_hearts,R.drawable.white_deck_7_of_spades,
     R.drawable.white_deck_8_of_clubs,R.drawable.white_deck_8_of_diamonds,R.drawable.white_deck_8_of_hearts,
     R.drawable.white_deck_8_of_spades,R.drawable.white_deck_9_of_clubs,R.drawable.white_deck_9_of_diamonds,
     R.drawable.white_deck_9_of_hearts,R.drawable.white_deck_9_of_spades, R.drawable.white_deck_10_of_clubs,
     R.drawable.white_deck_10_of_diamonds, R.drawable.white_deck_10_of_hearts, R.drawable.white_deck_10_of_spades, R.drawable.white_deck_ace_of_clubs,
     R.drawable.white_deck_ace_of_diamonds,R.drawable.white_deck_ace_of_hearts,R.drawable.white_deck_ace_of_spades,
     R.drawable.white_deck_jack_of_clubs,R.drawable.white_deck_jack_of_diamonds,R.drawable.white_deck_jack_of_hearts,
     R.drawable.white_deck_jack_of_spades,R.drawable.white_deck_queen_of_clubs,R.drawable.white_deck_queen_of_diamonds,
     R.drawable.white_deck_queen_of_hearts,R.drawable.white_deck_queen_of_spades,R.drawable.white_deck_king_of_clubs,
     R.drawable.white_deck_king_of_diamonds,R.drawable.white_deck_king_of_hearts,R.drawable.white_deck_king_of_spades};

    private String suit[] = {"clubs","diamonds","hearts","spades"};

    public ArrayList<Card> getDeck() {
        return deck;
    }

    public Deck(int fill) {
        if(fill==1){
            populate();
        }
        rand = new Random();
    }

    public void shuffle() //shuffle deck
    {
    ArrayList<Card> cards = deck;
    int randomNumber;
    Card temp;
    for(int i=0;i<cards.size();i++){
        randomNumber=rand.nextInt(51);
        temp=cards.get(i);
        cards.remove(i);
        cards.add(i,cards.get(randomNumber));
        cards.remove(randomNumber);
        cards.add(randomNumber,temp);
        }
    }
    public void reset(){

        deck.clear();
        populate();
    }

    public void populate(){

        int value; //value of card to be added to Card object
        for(int i = 2; i <= 14; i++)
         {
            for(int j = 0; j < 4; j++)
            {
                if(i > 11)
                {
                    value = 10; //face cards
                }

                else {
                    value = i; //Cards 2-11 keep their value
                }

                deck.add(new Card(imageIds[j+4*(i-2)], value, suits[j], true));
            }
           
        }
    }
}
