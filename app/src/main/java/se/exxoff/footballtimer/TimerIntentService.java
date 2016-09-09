package se.exxoff.footballtimer;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;

public class TimerIntentService extends IntentService
{

    private Handler mHandler;
    private Runnable mTimerRunnable;
    protected int mMinutes;
    protected int mSeconds;
    public TimerIntentService()
    {
        super("TimerIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent)
    {
        if (mHandler == null)
        {


            LogHelper.WriteToLog("onHandleIntent");
            mSeconds = intent.getIntExtra("Seconds",0);
            mMinutes = intent.getIntExtra("Minutes", 0);

            mHandler = new Handler();
            mTimerRunnable = new Runnable()
            {
                @Override
                public void run()
                {
                    if(mSeconds > 59)
                    {
                        mMinutes++;
                        mSeconds = 0;
                    }
                    else
                    {
                        mSeconds++;
                    }

                    LogHelper.WriteToLog(mMinutes + ":" + mSeconds);
                    mHandler.postDelayed(this,1000);
                }
            };

            mHandler.postDelayed(mTimerRunnable,0);

        }
        else
        {
            LogHelper.WriteToLog("Service already running");
        }

    }


    @Override
    public void onDestroy()
    {

        LogHelper.WriteToLog("Destroyed");
        this.stopSelf();
    }
}
