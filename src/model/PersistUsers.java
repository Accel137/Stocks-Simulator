package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * PersistUsers Class.
 */
public class PersistUsers extends AbstractPersist {

  public static String USER_FILE_NAME = "users.csv";

  private final Map<String, User> users;

  public PersistUsers() {
    users = new HashMap<>();
  }

  public Map<String, User> getUsers() {
    return users;
  }

  @Override
  public void readFile() {
    CSV file = new CSV(DATA_PATH);
    boolean success = file.readFile(USER_FILE_NAME);
    if (success) {
      List<String> users = file.getContents();
      users.remove(0);
      for (String line : users) {
        String[] info = line.split(",");
        User user = new User(info[0], info[1]);
        this.users.put(info[0], user);
      }
    }
  }

  @Override
  public void persistFile() {
    List<String> users = new ArrayList<>();
    users.add("username,password");
    for (User user : this.users.values()) {
      users.add(user.toString());
    }
    CSV file = new CSV(DATA_PATH);
    file.setContents(users);
    boolean success = file.writeFile(USER_FILE_NAME);
  }
}
