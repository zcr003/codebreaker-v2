package edu.cnm.deepdive.codebreaker.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.view.GameSummary;
import edu.cnm.deepdive.codebreaker.service.GameRepository;
import java.util.List;

public class ScoresViewModel extends AndroidViewModel {

  private final GameRepository repository;

  public ScoresViewModel(@NonNull Application application) {
    super(application);
    repository = new GameRepository();
  }

  public LiveData<List<GameSummary>> getGames() {
    return repository.getOrderedByGuessCount(6, 3); // TODO Take values from shared preferences.
  }

}