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

            String message = "<html><body>"
                            +"<h2>About BlackJack</h2>"
                            +"<p>Image Credits</p>"
                            +"<p><b>Source: </b>nicubunu<br>"
                            +"<b>Creator:</b> Nicu Buculei<br>"
                            +"<b>Link: <a href='https://openclipart.org/search/?query=white%20deck&page=2'>"
                            +"https://openclipart.org/search/?query=white%20deck&page=2</a></b><br>"
                            +"<b>License:</b> CC BY 3.0"
                            +"</p></body></html>";

            AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());
            builder.setMessage(Html.fromHtml(message));
            builder.setPositiveButton("OK",null);

            AlertDialog dialog = builder.create();
            dialog.show();

            TextView tv = (TextView) dialog.findViewById(android.R.id.message);
            tv.setMovementMethod(LinkMovementMethod.getInstance());
            }

    }
}
