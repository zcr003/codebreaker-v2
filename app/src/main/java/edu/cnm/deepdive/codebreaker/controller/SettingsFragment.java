package edu.cnm.deepdive.codebreaker.controller;

import android.os.Bundle;
import androidx.preference.PreferenceFragmentCompat;
import edu.cnm.deepdive.codebreaker.R;

public class SettingsFragment extends PreferenceFragmentCompat {

  @Override
  public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
    setPreferencesFromResource(R.xml.settings, rootKey);
  }

}
