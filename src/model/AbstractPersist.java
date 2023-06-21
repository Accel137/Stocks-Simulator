package model;

abstract class AbstractPersist implements Persist {

  protected static final String DATA_PATH = "data";

  public abstract void readFile();

  public abstract void persistFile();

}
