package co.gdgbogota.zenflow;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import static android.preference.PreferenceManager.getDefaultSharedPreferences;

public class Model {
  private static final long UNKNOWN = 0;
  private static final String KEY_AME_TIMESTAMP = "co.gdgbogota.zenflow.key.AME_TIME";
  private static final String KEY_HIGHEST_DELTA = "co.gdgbogota.zenflow.key.HIGHEST_DELTA";
  private static final String KEY_ENABLED = "co.gdgbogota.zenflow.key.ENABLED";
  private static final String KEY_WAS_ENABLED = "co.gdgbogota.zenflow.key.WAS_ENABLED";
  private static final String KEY_LATEST_DELTA = "co.gdgbogota.zenflow.key.LATEST_DELTA";
  private final SharedPreferences mPreferences;
  private final Context mContext;
  private OnFlowChangeListener mOnFlowChangeListener;
  private OnSharedPreferenceChangeListener mPreferencesListener = new OnSharedPreferenceChangeListener() {
    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
      onPreferenceChanged(key);
    }
  };

  public Model(Context context) {
    mContext = context.getApplicationContext();
    mPreferences = getDefaultSharedPreferences(mContext);
    mPreferences.registerOnSharedPreferenceChangeListener(mPreferencesListener);
  }

  public void destroy() {
    mPreferences.unregisterOnSharedPreferenceChangeListener(mPreferencesListener);
  }

  public Model listen(OnFlowChangeListener listener) {
    mOnFlowChangeListener = listener;
    return this;
  }

  public Model stopListening() {
    mOnFlowChangeListener = null;
    return this;
  }

  public long getTimestamp() {
    return getLong(KEY_AME_TIMESTAMP);
  }

  public long getLatestTimestamp() {
    return getLong(KEY_LATEST_DELTA);
  }


  public long getHighestEnabledTimeMs() {
    return getLong(KEY_HIGHEST_DELTA);
  }

  public Model detectAirplaneMode() {
    final boolean enabled = isAirplaneModeEnabled();
    setAirplaneMode(enabled, false);
    return this;
  }

  public boolean isAirplaneModeEnabled() {
    return Utils.isAirplaneModeEnabled(mContext);
  }

  public Model setAirplaneMode(boolean enabled) {
    setAirplaneMode(enabled, true);
    return this;
  }

  public Model enableFlow() {
    putBool(KEY_WAS_ENABLED, isFlowEnabled());
    putBool(KEY_ENABLED, true);
    return this;
  }

  public Model disableFlow() {
    putBool(KEY_WAS_ENABLED, isFlowEnabled());
    putBool(KEY_ENABLED, false);
    return this;
  }

  public Model resetWasFlowEnabled() {
    putBool(KEY_WAS_ENABLED, false);
    return this;
  }

  public boolean isFlowEnabled() {
    return getBool(KEY_ENABLED);
  }

  public boolean wasFlowEnabled() {
    return getBool(KEY_WAS_ENABLED);
  }

  private void onPreferenceChanged(String key) {
    if (KEY_ENABLED.equals(key)) {
      notifyListener(true);
    }
  }

  private void notifyListener(boolean enabled) {
    if (mOnFlowChangeListener != null) {
      mOnFlowChangeListener.onFlowChange(enabled);
    }
  }

  private void updateTimestamp(long timestamp) {

    final long enabledTimestamp = getTimestamp();
    final long highestEnabledTimeMs = getHighestEnabledTimeMs();

    final long delta = enabledTimestamp > 0 ? System.currentTimeMillis() - enabledTimestamp : 0;
    if (delta > highestEnabledTimeMs) {
      putLong(KEY_HIGHEST_DELTA, delta);
    }
    putLong(KEY_LATEST_DELTA, delta);
    putLong(KEY_AME_TIMESTAMP, timestamp);
  }

  private void setAirplaneMode(boolean enabled, boolean updateSettings) {
    if (updateSettings) {
      Utils.setAirplaneMode(mContext, enabled);
      return;
    }

    if (enabled) {
      updateTimestamp(System.currentTimeMillis());
      enableFlow();
    } else {
      updateTimestamp(UNKNOWN);

      disableFlow();
    }
  }

  private long getLong(String key) {
    return mPreferences.getLong(key, UNKNOWN);
  }

  private void putLong(String key, long value) {
    mPreferences.edit().putLong(key, value).commit();
  }

  private boolean getBool(String key) {
    return mPreferences.getBoolean(key, false);
  }

  private void putBool(String key, boolean value) {
    mPreferences.edit().putBoolean(key, value).commit();
  }

  public interface OnFlowChangeListener {
    void onFlowChange(boolean enabled);
  }
}
