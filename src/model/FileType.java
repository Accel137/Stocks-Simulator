package model;

/**
 * General File class.
 */
public interface FileType {

  /**
   * Read files from disk.
   * @param filename filename
   * @return true if success
   */
  boolean readFile(String filename);

  /**
   * Write files to disk.
   * @param filename filename
   * @return true if success
   */
  boolean writeFile(String filename);
}
