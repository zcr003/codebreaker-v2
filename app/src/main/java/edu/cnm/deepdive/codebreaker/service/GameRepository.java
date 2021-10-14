package edu.cnm.deepdive.codebreaker.service;

import androidx.annotation.NonNull;
import edu.cnm.deepdive.codebreaker.model.Game;
import io.reactivex.Single;
import io.reactivex.SingleSource;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import java.util.concurrent.Callable;

public class GameRepository {

  private final WebServiceProxy proxy;

  public GameRepository() {
    proxy = WebServiceProxy.getInstance();
  }

  public Single<Game> startGame(String pool, int length) {
    return Single
        .fromCallable(() -> {
          Game game = new Game();
          game.setPool(pool);
          game.setLength(length);
          return game;
        })
        .flatMap(proxy::startGame)
        .subscribeOn(Schedulers.io());
  }

}
