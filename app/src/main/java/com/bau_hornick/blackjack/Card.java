package com.bau_hornick.blackjack;

import java.io.Serializable;

/**
 * Created by ahornick on 1/30/2017.
 */

public class Card implements Serializable {
    private int image;
    private int value;
    private String suit;
    private boolean visible;

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setSuit(String suit) {
        this.suit = suit;
    }

    public void setValue(int value) {
        this.value = value;
    }

    public void setImage(int image) {
        this.image = image;
    }

    public Card(int image, int value, String suit, boolean visible) {
        this.image = image;
        this.value = value;
        this.suit = suit;

        this.visible=true;
    }

    public String getSuit()
    {
        return suit;
    }

    public int getValue()
    {
        return value;
    }

    public int getImage()
    {
        return image;
    }

    public boolean isVisible() { return visible; }

}
