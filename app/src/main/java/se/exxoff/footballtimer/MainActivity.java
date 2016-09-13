package se.exxoff.footballtimer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
{

    private Intent mStartIntent;
    private StatusReceiver mReceiver;
    private BroadcastReceiver receiver;




    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        mReceiver = new StatusReceiver();

        receiver = new BroadcastReceiver()
        {
            @Override
            public void onReceive(Context context, Intent intent)
            {
                LogHelper.WriteToLog("Receiving message");
                TextView lblMinutes = (TextView) findViewById(R.id.lblMinutes);
                TextView lblSeconds = (TextView) findViewById(R.id.lblSeconds);

                String _Minutes = String.valueOf(intent.getIntExtra("MINUTES",0)).length() < 2
                        ? "0" + String.valueOf(intent.getIntExtra("MINUTES",0)) :
                        String.valueOf(intent.getIntExtra("MINUTES",0));

                String _Seconds = String.valueOf(intent.getIntExtra("SECONDS",0)).length() < 2
                        ? "0" + String.valueOf(intent.getIntExtra("SECONDS",0)) :
                        String.valueOf(intent.getIntExtra("SECONDS",0));



                lblMinutes.setText(_Minutes);
                lblSeconds.setText(_Seconds);
            }
        };


        if (savedInstanceState != null)
        {

            Button startStopButton = (Button) findViewById(R.id.buttonStartStop);
            startStopButton.setText(savedInstanceState.getString("STARTBUTTONTEXT"));
//            if (savedInstanceState.getString("STARTBUTTONTEXT").toLowerCase() == "start")
//            {

                LogHelper.WriteToLog(savedInstanceState.toString());
                TextView lblMinutes = (TextView) findViewById(R.id.lblMinutes);
                TextView lblSeconds = (TextView) findViewById(R.id.lblSeconds);

                lblMinutes.setText(savedInstanceState.getCharSequence("MINUTESTEXT"));
                lblSeconds.setText(savedInstanceState.getCharSequence("SECONDSTEXT"));
//            }

        }
    }

    @Override
    protected void onStart()
    {
        super.onStart();
        IntentFilter filter = new IntentFilter();
        filter.addAction(getString(R.string.ACTION_UPDATE_CHRONO));
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        registerReceiver(mReceiver,filter);
        LogHelper.WriteToLog("onStart");

        setupUI();

        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
                new IntentFilter(TimerService.UPDATEUI));
    }

    private void setupUI()
    {
        Button startButton = (Button) findViewById(R.id.buttonStartStop);

        startButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LogHelper.WriteToLog("Start/Stop button pressed");
                onButtonStartClick((Button) v);
            }
        });

        Button secondHalfButton = (Button) findViewById(R.id.button2ndHalf);

        secondHalfButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LogHelper.WriteToLog("Second half");
                onSecondHalfClick((Button) v);
        }
    });

        Button firstHalfButton = (Button) findViewById(R.id.button1stHalf);

        firstHalfButton.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                LogHelper.WriteToLog("First half");
                onFirstHalfClick((Button) v);
            }
        });
    }

    private void onFirstHalfClick(Button v)
    {
        TextView lblMinutes = (TextView) findViewById(R.id.lblMinutes);
        TextView lblSeconds = (TextView) findViewById(R.id.lblSeconds);
        lblMinutes.setText("00");
        lblSeconds.setText("00");

//        LocalBroadcastManager.getInstance(this).registerReceiver((receiver),
//            new IntentFilter(TimerService.UPDATEUI)
//        );
    }

    private void onSecondHalfClick(Button v)
    {
        TextView lblMinutes = (TextView) findViewById(R.id.lblMinutes);
        TextView lblSeconds = (TextView) findViewById(R.id.lblSeconds);
        lblMinutes.setText("45");
        lblSeconds.setText("00");
    }

    private void onButtonStartClick(Button button)
    {

        if(mStartIntent == null)
        {
            mStartIntent = new Intent(this,TimerService.class);
        }


        if(button.getText() == getString(R.string._start))
        {
            LogHelper.WriteToLog("Starting service...");
            TextView txtMinutes = (TextView) findViewById(R.id.lblMinutes);
            TextView txtSeconds = (TextView) findViewById(R.id.lblSeconds);
            int minutes = Integer.parseInt((String) txtMinutes.getText());
            int seconds = Integer.parseInt((String) txtSeconds.getText());


            mStartIntent.putExtra("Minutes",minutes);
            mStartIntent.putExtra("Seconds",seconds);
            button.setText(R.string._stop);
            startService(mStartIntent);
        }
        else
        {
            LogHelper.WriteToLog("Stopping service...");
            this.stopService(mStartIntent);
            button.setText(R.string._start);
            mStartIntent = null;
        }

    }


    @Override
    protected void onStop()
    {
        super.onStop();

        unregisterReceiver(mReceiver);

        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);

    }



    @Override
    protected void onSaveInstanceState(Bundle outState)
    {

        Button startStopButton = (Button) findViewById(R.id.buttonStartStop);
        TextView lblMinutes = (TextView) findViewById(R.id.lblMinutes);
        TextView lblSeconds = (TextView) findViewById(R.id.lblSeconds);

        outState.putString("STARTBUTTONTEXT",startStopButton.getText().toString());
        outState.putCharSequence("MINUTESTEXT",lblMinutes.getText());
        outState.putCharSequence("SECONDSTEXT",lblSeconds.getText());
    }
}

