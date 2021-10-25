package edu.cnm.deepdive.codebreaker.service;

import androidx.annotation.NonNull;
import edu.cnm.deepdive.codebreaker.model.Game;
import edu.cnm.deepdive.codebreaker.model.Guess;
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

  public Single<Game> submitGuess(Game game, String text) {
    return Single
        .fromCallable(() -> {
          Guess guess = new Guess();
          guess.setText(text);
          return guess;
        })
        .flatMap((guess) -> proxy.submitGuess(guess, game.getId()))
        .map((guess) -> {
          game.getGuesses().add(guess);
          return game;
        })
        .subscribeOn(Schedulers.io());
  }

}
