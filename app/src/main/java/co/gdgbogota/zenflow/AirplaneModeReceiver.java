package co.gdgbogota.zenflow;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import static android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED;

public class AirplaneModeReceiver extends BroadcastReceiver {
  @Override
  public void onReceive(Context context, Intent intent) {
    if (intent != null && !ACTION_AIRPLANE_MODE_CHANGED.equals(intent.getAction())) {
      return;
    }

    new Model(context).detectAirplaneMode().destroy();
  }
}
