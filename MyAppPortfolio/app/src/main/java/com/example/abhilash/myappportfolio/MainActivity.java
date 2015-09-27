package com.example.abhilash.myappportfolio;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b1,b2,b3,b4,b5,b6;
        b1=(Button)findViewById(R.id.button1);
        b1.setOnClickListener(this);
        b2=(Button)findViewById(R.id.button2);
        b2.setOnClickListener(this);
        b3=(Button)findViewById(R.id.button3);
        b3.setOnClickListener(this);
        b4=(Button)findViewById(R.id.button4);
        b4.setOnClickListener(this);
        b5=(Button)findViewById(R.id.button5);
        b5.setOnClickListener(this);
        b6=(Button)findViewById(R.id.button6);
        b6.setOnClickListener(this);
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.button1:
                Toast.makeText(getBaseContext(), "This will open my Spotify Streamer App!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button2:
                Toast.makeText(getBaseContext(), "This will open my Football Scores App!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button3:
                Toast.makeText(getBaseContext(), "This will open my Library App!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button4:
                Toast.makeText(getBaseContext(), "This will open my Build It Bigger App!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button5:
                Toast.makeText(getBaseContext(), "This will open my XYZ Reader App!", Toast.LENGTH_SHORT).show();
                break;
            case R.id.button6:
                Toast.makeText(getBaseContext(), "This will open my Capstone App!", Toast.LENGTH_SHORT).show();
                break;
        }

    }





    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


}
