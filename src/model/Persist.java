package model;

/**
 * Persist Class Interface.
 */
public interface Persist {

  /**
   * Read files from disk.
   */
  void readFile();

  /**
   * Write files to disk.
   */
  void persistFile();
}
