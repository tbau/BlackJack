package com.bau_hornick.blackjack;

import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.play_button).setOnClickListener(this);
        findViewById(R.id.about_button).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if(v.getId()==R.id.play_button){
        Intent intent=new Intent(this,PlayActivity.class);
        startActivity(intent);}

        else if(v.getId()==R.id.about_button){
            Intent intent = new Intent(this, AboutActivity.class);
            startActivity(intent);

            // AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            //builder.setMessage(Html.fromHtml(message));
            //builder.setPositiveButton("OK",null);

            //AlertDialog dialog = builder.create();
            //dialog.show();

            //TextView tv = (TextView) dialog.findViewById(android.R.id.message);
            //tv.setMovementMethod(LinkMovementMethod.getInstance());
            }

    }
}
