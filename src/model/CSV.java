package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CSV File class.
 */
public class CSV implements FileType {

  private final String DATA_PATH;
  private List<String> contents;

  public CSV(String path) {
    this.DATA_PATH = path;
    this.contents = new ArrayList<>();
  }

  public List<String> getContents() {
    return contents;
  }

  public void setContents(List<String> contents) {
    this.contents = contents;
  }

  @Override
  public boolean readFile(String filename) {
    try (BufferedReader reader = new BufferedReader(
        new FileReader(DATA_PATH + File.separator + filename)
    )) {
      String line;
      while ((line = reader.readLine()) != null) {
        if (line.isEmpty()) {
          continue;
        }
        contents.add(line);
      }
      return true;
    } catch (IOException e) {
      System.out.printf("Error on loading the file %s, %s.\n", filename, e.getMessage());
      return false;
    }
  }

  @Override
  public boolean writeFile(String filename) {
    try (BufferedWriter writer = new BufferedWriter(
        new FileWriter(DATA_PATH + File.separator + filename)
    )) {
      for (String line : contents) {
        writer.write(line + '\n');
      }
      return true;
    } catch (IOException e) {
      System.out.printf("Error on writing the file %s, %s.\n", filename, e.getMessage());
      return false;
    }
  }

}
