package edu.cnm.deepdive.codebreaker.model;

import com.google.gson.annotations.Expose;

public class Game {

  @Expose
  private String id;

  @Expose
  private String pool;

  @Expose
  private int length;

  private boolean solved;

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getPool() {
    return pool;
  }

  public void setPool(String pool) {
    this.pool = pool;
  }

  public int getLength() {
    return length;
  }

  public void setLength(int length) {
    this.length = length;
  }

  public boolean isSolved() {
    return solved;
  }

  public void setSolved(boolean solved) {
    this.solved = solved;
  }
}
