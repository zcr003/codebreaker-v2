package edu.cnm.deepdive.codebreaker.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cnm.deepdive.codebreaker.BuildConfig;
import edu.cnm.deepdive.codebreaker.model.entity.Game;
import edu.cnm.deepdive.codebreaker.model.entity.Guess;
import io.reactivex.Single;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import okhttp3.logging.HttpLoggingInterceptor.Level;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface WebServiceProxy {

  @POST("codes")
  Single<Game> startGame(@Body Game game);

  @POST("codes/{gameId}/guesses")
  Single<Guess> submitGuess(@Body Guess guess, @Path("gameId") String gameId);

  static WebServiceProxy getInstance() {
    return InstanceHolder.INSTANCE;
  }

  class InstanceHolder {

    private static final WebServiceProxy INSTANCE;

    static {
      Gson gson = new GsonBuilder()
          .excludeFieldsWithoutExposeAnnotation()
          .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
          .create();
      HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
      interceptor.setLevel(Level.BODY);
      OkHttpClient client = new OkHttpClient.Builder()
          .addInterceptor(interceptor)
          .build();
      Retrofit retrofit = new Retrofit.Builder()
          .baseUrl(BuildConfig.BASE_URL)
          .addConverterFactory(GsonConverterFactory.create(gson))
          .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
          .client(client)
          .build();
      INSTANCE = retrofit.create(WebServiceProxy.class);
    }

  }

}
