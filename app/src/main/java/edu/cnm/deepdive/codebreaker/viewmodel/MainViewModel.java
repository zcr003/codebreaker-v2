package edu.cnm.deepdive.codebreaker.viewmodel;

import android.app.Application;
import android.util.Log;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.service.GameRepository;
import io.reactivex.disposables.CompositeDisposable;

public class MainViewModel extends AndroidViewModel {

  private final GameRepository repository;
  private final MutableLiveData<Game> game;
  private final MutableLiveData<Throwable> throwable;
  private final CompositeDisposable pending;

  public MainViewModel(@NonNull Application application) {
    super(application);
    repository = new GameRepository();
    game = new MutableLiveData<>();
    throwable = new MutableLiveData<>();
    pending = new CompositeDisposable();
    startGame();
  }

  public LiveData<Game> getGame() {
    return game;
  }

  public LiveData<Throwable> getThrowable() {
    return throwable;
  }

  public void startGame() {
    throwable.postValue(null);
    pending.add(
        repository
            .startGame("ABCDEF", 3)
            .subscribe(
                game::postValue,
                this::postThrowable
            )
    );
  }

  public void submitGuess(String text) {
    throwable.postValue(null);
    pending.add(
        repository
            .submitGuess(game.getValue(), text)
            .subscribe(
                game::postValue,
                this::postThrowable
            )
    );
  }

  private void postThrowable(Throwable throwable) {
    Log.e(getClass().getSimpleName(), throwable.getMessage(), throwable);
    this.throwable.postValue(throwable);
  }

}
