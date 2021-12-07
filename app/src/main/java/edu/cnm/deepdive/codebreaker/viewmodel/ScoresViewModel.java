package edu.cnm.deepdive.codebreaker.viewmodel;

import android.app.Application;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.preference.PreferenceManager;
import edu.cnm.deepdive.codebreaker.R;
import edu.cnm.deepdive.codebreaker.model.dto.RankedUser;
import edu.cnm.deepdive.codebreaker.model.view.GameSummary;
import edu.cnm.deepdive.codebreaker.service.GameRepository;
import edu.cnm.deepdive.codebreaker.service.GameRepository.RankingOrder;
import io.reactivex.disposables.CompositeDisposable;
import java.util.List;

public class ScoresViewModel extends AndroidViewModel implements DefaultLifecycleObserver {

  private final GameRepository repository;
  private final MutableLiveData<Integer> codeLength;
  private final MutableLiveData<Integer> poolSize;
  private final MutableLiveData<Boolean> sortedByTime;
  private final LiveData<List<GameSummary>> scoreboard;
  private final LiveData<List<RankedUser>> rankings;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;


  public ScoresViewModel(@NonNull Application application) {
    super(application);

    SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(application);
    Resources resources = application.getResources();
    String codeLengthPrefKey = resources.getString(R.string.code_length_pref_key);
    String poolSizePrefKey = resources.getString(R.string.pool_size_pref_key);
    int codeLengthPrefDefault = resources.getInteger(R.integer.code_length_pref_default);
    int poolSizePrefDefault = resources.getInteger(R.integer.pool_size_pref_default);
    int codeLengthPref = preferences.getInt(codeLengthPrefKey, codeLengthPrefDefault);
    int poolSizePref = preferences.getInt(poolSizePrefKey, poolSizePrefDefault);

    repository = new GameRepository();
    codeLength = new MutableLiveData<>(codeLengthPref);
    poolSize = new MutableLiveData<>(poolSizePref);
    sortedByTime = new MutableLiveData<>(false);
    ScoreboardFilterLiveData trigger =
        new ScoreboardFilterLiveData(codeLength, poolSize, sortedByTime);
    scoreboard = Transformations.switchMap(trigger, (params) -> params.sortedByTime
        ? repository.getOrderedByTotalTime(params.poolSize, params.codeLength)
        : repository.getOrderedByGuessCount(params.poolSize, params.codeLength));
    rankings = new RankingLiveData(trigger);
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
  }

  public LiveData<List<GameSummary>> getGames() {
    return repository.getOrderedByGuessCount(6, 3); // TODO Take values from shared preferences.
  }

  public void setCodeLength(int codeLength) {
    this.codeLength.setValue(codeLength);
  }

  public void setPoolSize(int poolSize) {
    this.poolSize.setValue(poolSize);
  }

  public void setSortedByTime(boolean sortedByTime) {
    this.sortedByTime.setValue(sortedByTime);
  }

  public LiveData<Integer> getCodeLength() {
    return codeLength;
  }

  public LiveData<Integer> getPoolSize() {
    return poolSize;
  }

  public LiveData<Boolean> getSortedByTime() {
    return sortedByTime;
  }

  public LiveData<List<GameSummary>> getScoreboard() {
    return scoreboard;
  }

  public LiveData<List<RankedUser>> getRankings() {
    return rankings;
  }

  @Override
  public void onStop(@NonNull LifecycleOwner owner) {
    DefaultLifecycleObserver.super.onStop(owner);
    pending.clear();
  }


  private void postThrowable(Throwable throwable) {
    Log.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

  private static class ScoreboardParams {

    private final int codeLength;
    private final int poolSize;
    private final boolean sortedByTime;

    private ScoreboardParams(int codeLength, int poolSize, boolean sortedByTime) {
      this.codeLength = codeLength;
      this.poolSize = poolSize;
      this.sortedByTime = sortedByTime;
    }

  }

  private static class ScoreboardFilterLiveData extends MediatorLiveData<ScoreboardParams> {

    @SuppressWarnings("ConstantConditions")
    public ScoreboardFilterLiveData(
        @NonNull LiveData<Integer> codeLength,
        @NonNull LiveData<Integer> poolSize,
        @NonNull LiveData<Boolean> sortedByTime
    ) {
      addSource(codeLength, (length) -> setValue(
          new ScoreboardParams(length, poolSize.getValue(), sortedByTime.getValue())));
      addSource(poolSize, (size) -> setValue(
          new ScoreboardParams(codeLength.getValue(), size, sortedByTime.getValue())));
      addSource(sortedByTime, (sorted) -> setValue(
          new ScoreboardParams(codeLength.getValue(), poolSize.getValue(), sorted)));
    }

  }

  private class RankingLiveData extends MediatorLiveData<List<RankedUser>> {

    public RankingLiveData(@NonNull ScoreboardFilterLiveData liveParams) {
      addSource(liveParams, (params) ->
          pending.add(
              repository
                  .getRankings(params.codeLength, params.poolSize,
                      params.sortedByTime ? RankingOrder.TIME : RankingOrder.COUNT)
                  .subscribe(
                      this :: postValue,
                      ScoresViewModel.this::postThrowable
                  )
          )
      );
    }
  }

}