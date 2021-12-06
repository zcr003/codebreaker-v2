package edu.cnm.deepdive.codebreaker.model.dto;

import com.google.gson.annotations.Expose;


public class RankedUser {

  //No setters needed for these fields; Gson doesn't use them.
  @Expose
  private String displayName;

  @Expose
  private double averageGuessCount;

  @Expose
  private double averageTime;

  public String getDisplayName() {
    return displayName;
  }

  public double getAverageGuessCount() {
    return averageGuessCount;
  }

  public double getAverageTime() {
    return averageTime;
  }
}
