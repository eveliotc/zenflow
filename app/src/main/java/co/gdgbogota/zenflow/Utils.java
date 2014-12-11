package co.gdgbogota.zenflow;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.provider.Settings;
import android.view.ViewGroup;

import static android.animation.LayoutTransition.CHANGING;
import static android.content.Intent.ACTION_AIRPLANE_MODE_CHANGED;
import static android.os.Build.VERSION.SDK_INT;
import static android.os.Build.VERSION_CODES.JELLY_BEAN;
import static android.os.Build.VERSION_CODES.JELLY_BEAN_MR1;
import static android.provider.Settings.Global.AIRPLANE_MODE_ON;

public class Utils {

  public static boolean isJellyBeanMr1OrBetter() {
    return SDK_INT >= JELLY_BEAN_MR1;
  }

  public static boolean isJellyBeanOrBetter() {
    return SDK_INT >= JELLY_BEAN;
  }

  public static boolean isAirplaneModeEnabled(Context context) {
    return Settings.System.getInt(context.getContentResolver(), AIRPLANE_MODE_ON, 0) != 0;
  }

  public static void setAirplaneMode(Context context, boolean enabled) {
    if (isJellyBeanMr1OrBetter()) {
      return;
    }

    Settings.System.putInt(context.getContentResolver(), AIRPLANE_MODE_ON, enabled ? 1 : 0);

    Intent intent = new Intent(ACTION_AIRPLANE_MODE_CHANGED);
    intent.putExtra("state", isAirplaneModeEnabled(context));
    context.sendBroadcast(intent);
  }


  private static final long MS_SECOND = 1000;
  private static final long MS_MINUTE= 60 * MS_SECOND;
  private static final long MS_HOUR= 60 * MS_MINUTE;
  private static final long MS_DAY = 24 * MS_HOUR;
  public static String prettyTime(Context context, long ms) {
    final Resources res =  context.getResources();

    final long days = ms / MS_DAY;
    long rest = ms % MS_DAY;
    final StringBuilder builder = new StringBuilder();
    if (days > 0) {
      appendUnit(builder, res, R.plurals.days, days);
    }

    final long hours = rest / MS_HOUR;
    rest = rest % MS_HOUR;
    if (hours > 0) {
      appendUnit(builder, res, R.plurals.hours, hours);
    }

    final long minutes = rest / MS_MINUTE;
    rest = rest % MS_MINUTE;
    if (minutes > 0) {
      appendUnit(builder, res, R.plurals.minutes, minutes);
    }

    final long seconds = rest / MS_SECOND;
    if (seconds > 0) {
      appendUnit(builder, res, R.plurals.seconds, seconds);
    }

    return builder.toString();
  }

  private static void appendUnit(StringBuilder builder, Resources res, int pluralId, long value) {
    final String space = res.getString(R.string.space);

    if (builder.length() > 0) {
      builder.append(space).append(res.getString(R.string.and)).append(space);
    }

    builder.append(value).append(space).append(res.getQuantityString(pluralId, (int) value));
  }

  @TargetApi(JELLY_BEAN)
  public static void enableChangingTransition(ViewGroup viewGroup) {
    if (!isJellyBeanOrBetter() || viewGroup == null || viewGroup.getLayoutTransition() == null) {
      return;
    }
    viewGroup.getLayoutTransition().enableTransitionType(CHANGING);
  }
}
