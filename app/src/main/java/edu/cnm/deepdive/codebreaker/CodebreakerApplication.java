package edu.cnm.deepdive.codebreaker;

import android.app.Application;
import com.facebook.stetho.Stetho;
import edu.cnm.deepdive.codebreaker.service.CodebreakerDatabase;
import io.reactivex.schedulers.Schedulers;

public class CodebreakerApplication extends Application {

  @Override
  public void onCreate() {
    super.onCreate();
    Stetho.initializeWithDefaults(this);
    CodebreakerDatabase.setContext(this);
    CodebreakerDatabase
        .getInstance()
        .getGameDao()
        .delete()
        .subscribeOn(Schedulers.io())
        .subscribe();
  }

}
