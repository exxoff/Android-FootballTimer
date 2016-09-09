package se.exxoff.footballtimer;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity
{

    private Intent mStartIntent;
private StatusReceiver mReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mReceiver = new StatusReceiver();
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
    }

    @Override
    protected void onStop()
    {
        super.onStop();

        unregisterReceiver(mReceiver);
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
            stopService(mStartIntent);
            button.setText(R.string._start);
            mStartIntent = null;
        }



    }


}

