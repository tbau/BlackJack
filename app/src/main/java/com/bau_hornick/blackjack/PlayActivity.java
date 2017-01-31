package com.bau_hornick.blackjack;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class PlayActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);

        Button addBet = (Button) findViewById(R.id.increase_bet_button);
        addBet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
      else if (v.getId() == R.id.increase_bet_button) {
            bet++;
            TextView betView = (TextView) findViewById(R.id.bet_textView);
            betView.setText(bet + "(" + (money - bet) + ")");
        }
    }
}
