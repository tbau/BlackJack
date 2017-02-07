package com.bau_hornick.blackjack;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //set up Listeners for 3 buttons
        findViewById(R.id.start_button).setOnClickListener(this);
        findViewById(R.id.about_button).setOnClickListener(this);
        findViewById(R.id.resume_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        //start activity based on button clicked
        if(v.getId()==R.id.start_button){
        Intent intent=new Intent(this,PlayActivity.class);
        intent.putExtra("resume",false);
        startActivity(intent);
        }

        else if(v.getId()==R.id.about_button){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);

            }
        else
        {
            Intent intent=new Intent(this,PlayActivity.class);
            intent.putExtra("resume",true);
            startActivity(intent);
        }

    }
}
