package co.gdgbogota.zenflow;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SwitchCompat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static co.gdgbogota.zenflow.Utils.enableChangingTransition;
import static co.gdgbogota.zenflow.Utils.isJellyBeanMr1OrBetter;


public class MainActivity extends ActionBarActivity implements CompoundButton.OnCheckedChangeListener, Model.OnFlowChangeListener {
  private Model mModel;
  private SwitchCompat mAirplaneModeSwitch;
  private TextView mMainText;
  private TextView mSecondaryText;
  private TextView mHighestText;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    mModel = new Model(this);

    setContentView(R.layout.activity_main);

    ViewGroup container = (ViewGroup) findViewById(R.id.container);
    enableChangingTransition(container);

    mAirplaneModeSwitch = (SwitchCompat) findViewById(R.id.airplane_mode_switch);
    mMainText = (TextView) findViewById(R.id.main_text);
    mSecondaryText = (TextView) findViewById(R.id.secondary_text);
    mHighestText = (TextView) findViewById(R.id.highest_text);

    syncViews();
    mAirplaneModeSwitch.setOnCheckedChangeListener(this);
    mAirplaneModeSwitch.setVisibility(isJellyBeanMr1OrBetter() ? GONE : VISIBLE);
  }

  @Override
  protected void onResume() {
    super.onResume();
    mModel.listen(this);
  }

  @Override
  protected void onPause() {
    super.onPause();

    mModel.stopListening();
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();

    mModel.destroy();
  }

  private void syncViews() {
    boolean airplaneMode = mModel.isAirplaneModeEnabled();
    mAirplaneModeSwitch.setChecked(airplaneMode);

    if (airplaneMode) {
      mMainText.setText(R.string.you_are_offline_get_some_work_done);
      mSecondaryText.setText(R.string.you_are_in_the_zen_flow);
      mHighestText.setVisibility(GONE);
    } else if (mModel.wasFlowEnabled()) {
      mModel.resetWasFlowEnabled();
      final long lastFlowDelta = mModel.getLatestTimestamp();
      setDeltaTemplate(mMainText, R.string.template_you_were_offline_for, lastFlowDelta);
      mSecondaryText.setText(R.string.welcome_back);

      final long highestDelta = mModel.getHighestEnabledTimeMs();
      setDeltaTemplate(mHighestText, R.string.template_highest_time_offline, highestDelta);
      mHighestText.setVisibility(VISIBLE);
    } else {
      mMainText.setText(R.string.enable_airplane_mode_to_begin);
      mSecondaryText.setText(R.string.lets_get_in_the_zen_flow);
      mHighestText.setVisibility(GONE);
    }
  }

  private void setDeltaTemplate(TextView textView, int templateResId, long delta) {
    final String deltaStr = Utils.prettyTime(this, delta);
    final String text = getString(templateResId, deltaStr);
    textView.setText(text);
  }

  private void updateModel(boolean enabled) {
    mModel.setAirplaneMode(enabled);
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.menu_main, menu);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    int id = item.getItemId();

    switch (id) {
      case R.id.action_about:
        // TODO show about here
        return true;
      default:
        return super.onOptionsItemSelected(item);
    }

  }

  @Override
  public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    updateModel(isChecked);
  }

  @Override
  public void onFlowChange(boolean enabled) {
    syncViews();
  }

}
