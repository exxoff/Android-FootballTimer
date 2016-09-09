package se.exxoff.footballtimer;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class StatusReceiver extends BroadcastReceiver
{
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if( action.equals(R.string.ACTION_UPDATE_CHRONO) ) {

        }
    }
}
