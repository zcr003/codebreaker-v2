package edu.cnm.deepdive.codebreaker.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class ScoresViewModel extends ViewModel {

  private MutableLiveData<String> mText;

  public ScoresViewModel() {
    mText = new MutableLiveData<>();
    mText.setValue("This is gallery fragment");
  }

  public LiveData<String> getText() {
    return mText;
  }
}