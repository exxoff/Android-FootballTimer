package se.exxoff.footballtimer;

import android.annotation.TargetApi;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.content.LocalBroadcastManager;

public class TimerService extends Service
{
    static int TIMER_ONE_SECOND = 1000;

    private HandlerThread mHandlerThread;
    private Handler mHandler;
    private static Runnable mTimerRunnable;
    private static Handler mTimerHandler;
    protected int mMinutes;
    protected int mSeconds;
    private PowerManager pm;
    private PowerManager.WakeLock wakeLock;
    private boolean wakeUpFlag;
    private LocalBroadcastManager broadcaster;

    public static final String UPDATEUI = "se.exxoff.FotballTimer.UPDATEUI";

    public TimerService()
    {
    }

    @Override
    public void onCreate()
    {

        LogHelper.WriteToLog("TimerService onCreate");
        if (broadcaster == null)
        {
            broadcaster = LocalBroadcastManager.getInstance(this);
        }


        mHandlerThread = new HandlerThread("Whatever");
        mHandlerThread.start();
        mHandler = new Handler(mHandlerThread.getLooper())
        {
            @Override
            public void handleMessage(Message msg)
            {
                Intent intent = (Intent) msg.obj;

                doIncrement(intent); // Kör doWork på en annan tråd
//                stopSelfResult(startId);
            }
        };
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {

        wakeUpFlag = false;

        pm = (PowerManager)getSystemService(Context.POWER_SERVICE);
        wakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "wakeLockTag");
        LogHelper.WriteToLog("Screen is off, acquire Wake Lock");
        wakeLock.acquire();

        Message msg = mHandler.obtainMessage();
        msg.obj = intent;

        msg.sendToTarget();

        return 0;
    }

    @Override
    public void onDestroy()
    {
        wakeLock.release();
        LogHelper.WriteToLog("Wake lock released.");
        broadcaster = null;

        LogHelper.WriteToLog("Destroyed!");
        mHandler.removeCallbacks(mTimerRunnable);

    }


    @Override
    public boolean stopService(Intent intent)
    {
        LogHelper.WriteToLog("stopService");
        mHandlerThread.quit();
        mHandlerThread = null;
        mHandler = null;
//        if (wakeLock != null)
//        {
//        }
        return super.stopService(intent);
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

//            mTimerHandler = new Handler();
            mTimerRunnable = new Runnable()
            {
                @Override
                public void run()
                {
                    if (mSeconds > 58)
                    {
                        mMinutes++;
                        mSeconds = 0;
                    } else
                    {
                        mSeconds++;
                    }

                    sendResult(mMinutes,mSeconds);
                    LogHelper.WriteToLog(mMinutes + ":" + mSeconds);
                    mHandler.postDelayed(this, TIMER_ONE_SECOND);
                }
            };

            mHandler.postDelayed(mTimerRunnable, 0);


    }

    public void sendResult(int Minutes,int Seconds) {
        Intent intent = new Intent(UPDATEUI);

        intent.putExtra("MINUTES", Minutes);
        intent.putExtra("SECONDS", Seconds);
        if (broadcaster != null)
        {
            broadcaster.sendBroadcast(intent);
        }

    }

}


