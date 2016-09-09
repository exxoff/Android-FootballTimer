package se.exxoff.footballtimer;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;

public class TimerService extends Service
{
    static int TIMER_ONE_SECOND = 1000;

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private Runnable mTimerRunnable;
    protected int mMinutes;
    protected int mSeconds;

    public TimerService()
    {
    }

    @Override
    public void onCreate()
    {
        mHandlerThread = new HandlerThread("Whatever");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                Intent intent = (Intent) msg.obj;
                int startId = msg.arg1;
                doIncrement(intent); // Kör doWork på en annan tråd
                stopSelfResult(startId);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        Message msg = mHandler.obtainMessage();
        msg.obj = intent;
        msg.arg1 = startId;
        msg.sendToTarget();

        return 0;
    }

    @Override
    public void onDestroy()
    {
        mHandlerThread.quit();
        mHandlerThread = null;
        mHandler = null;
    }


    @Override

    public IBinder onBind(Intent intent)
    {
        return null;
    }


    private void doIncrement(Intent intent)
    {
            LogHelper.WriteToLog("doIncrement");
            mSeconds = intent.getIntExtra("Seconds", 0);
            mMinutes = intent.getIntExtra("Minutes", 0);

            mHandler = new Handler();
            mTimerRunnable = new Runnable()
            {
                @Override
                public void run()
                {
                    if (mSeconds > 59)
                    {
                        mMinutes++;
                        mSeconds = 0;
                    } else
                    {
                        mSeconds++;
                    }

                    LogHelper.WriteToLog(mMinutes + ":" + mSeconds);
                    mHandler.postDelayed(this, 1000);
                }
            };

            mHandler.postDelayed(mTimerRunnable, 0);


    }
}


