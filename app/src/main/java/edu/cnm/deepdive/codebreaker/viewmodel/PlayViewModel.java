package edu.cnm.deepdive.codebreaker.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.Lifecycle.Event;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.OnLifecycleEvent;
import edu.cnm.deepdive.codebreaker.R;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import edu.cnm.deepdive.codebreaker.service.GameRepository;
import edu.cnm.deepdive.codebreaker.service.SettingsRepository;
import io.reactivex.disposables.CompositeDisposable;

public class PlayViewModel extends AndroidViewModel implements LifecycleObserver {

  private final GameRepository gameRepository;
  private final SettingsRepository settingsRepository;
  private final MutableLiveData<Game> game;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  private int codeLength;
  private int poolSize;
  private String basePool;

  public PlayViewModel(@NonNull Application application) {
    super(application);
    gameRepository = new GameRepository();
    settingsRepository = new SettingsRepository(application);
    game = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    String[] emojis = application.getResources().getStringArray(R.array.emojis);
    basePool = String.join("", emojis);
    subscribeToSettings();
  }

  public LiveData<Game> getGame() {
    return game;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void startGame() {
    if (codeLength > 0 && poolSize > 0) {
      throwable.postValue(null);
      int[] poolCodePoints = basePool
          .codePoints()
          .limit(poolSize)
          .toArray();
      Game game = new Game();
      game.setPool(new String(poolCodePoints, 0, poolCodePoints.length));
      game.setLength(codeLength);
      pending.add(
          gameRepository
              .save(game)
              .subscribe(
                  this.game::postValue,
                  this::postThrowable
              )
      );
    }
  }

  public void submitGuess(String text) {
    throwable.postValue(null);
    Guess guess = new Guess();
    guess.setText(text);
    //noinspection ConstantConditions
    pending.add(
        gameRepository
            .save(game.getValue(), guess)
            .subscribe(
                game::postValue,
                this::postThrowable
            )
    );
  }

  @OnLifecycleEvent(Event.ON_STOP)
  private void clearPending() {
    pending.clear();
  }

  private void postThrowable(Throwable throwable) {
    Log.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

  private void subscribeToSettings() {
    pending.add(
        settingsRepository
            .getCodeLengthPreference()
            .subscribe(
                (codeLength) -> {
                  this.codeLength = codeLength;
                  startGame();
                },
                this::postThrowable
            )
    );
    pending.add(
        settingsRepository
            .getPoolSizePreference()
            .subscribe(
                (poolSize) -> {
                  this.poolSize = poolSize;
                  startGame();
                },
                this::postThrowable
            )
    );
  }

}
