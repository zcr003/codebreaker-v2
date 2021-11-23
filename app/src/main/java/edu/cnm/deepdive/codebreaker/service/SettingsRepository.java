package edu.cnm.deepdive.codebreaker.service;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.content.res.Resources;
import androidx.preference.PreferenceManager;
import edu.cnm.deepdive.codebreaker.R;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;

public class SettingsRepository {

  private final SharedPreferences preferences;
  private final String codeLengthPrefKey;
  private final String poolSizePrefKey;
  private final int codeLengthPrefDefault;
  private final int poolSizePrefDefault;
  private final OnSharedPreferenceChangeListener listener;

  private ObservableEmitter<Integer> codeLengthPrefEmitter;
  private ObservableEmitter<Integer> poolSizePrefEmitter;

  public SettingsRepository(Context context) {
    Resources resources = context.getResources();
    codeLengthPrefKey = resources.getString(R.string.code_length_pref_key);
    poolSizePrefKey = resources.getString(R.string.pool_size_pref_key);
    codeLengthPrefDefault = resources.getInteger(R.integer.code_length_pref_default);
    poolSizePrefDefault = resources.getInteger(R.integer.pool_size_pref_default);
    preferences = PreferenceManager.getDefaultSharedPreferences(context);
    listener = this::emitChangedPreferences;
    preferences.registerOnSharedPreferenceChangeListener(listener);
  }

  public Observable<Integer> getCodeLengthPreference() {
    return Observable.create((emitter) -> {
      codeLengthPrefEmitter = emitter;
      emitChangedPreferences(preferences, codeLengthPrefKey);
    });
  }

  public Observable<Integer> getPoolSizePreference() {
    return Observable.create((emitter) -> {
      poolSizePrefEmitter = emitter;
      emitChangedPreferences(preferences, poolSizePrefKey);
    });
  }

  private void emitChangedPreferences(SharedPreferences prefs, String key) {
    if (key.equals(codeLengthPrefKey)) {
      if (codeLengthPrefEmitter != null && !codeLengthPrefEmitter.isDisposed()) {
        codeLengthPrefEmitter.onNext(prefs.getInt(key, codeLengthPrefDefault));
      }
    } else if (key.equals(poolSizePrefKey)) {
      if (poolSizePrefEmitter != null && !poolSizePrefEmitter.isDisposed()) {
        poolSizePrefEmitter.onNext(prefs.getInt(key, poolSizePrefDefault));
      }
    }
  }

}
