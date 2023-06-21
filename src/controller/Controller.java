package controller;

import model.PersistFlexPortfolios;
import model.PersistInflexPortfolios;
import model.PersistStocks;
import model.PersistUsers;
import model.User;

/**
 * Controller Class.
 */
public class Controller implements GenericController {

  private final PersistInflexPortfolios inflexPortfolio;
  private final PersistFlexPortfolios flexPortfolio;
  private final PersistUsers users;
  private final PersistStocks stocks;
  private User currentUser;


  /**
   * Controller of the program.
   */
  public Controller() {
    this.currentUser = null;

    inflexPortfolio = new PersistInflexPortfolios();
    flexPortfolio = new PersistFlexPortfolios();
    users = new PersistUsers();
    stocks = new PersistStocks();

    inflexPortfolio.readFile();
    flexPortfolio.readFile();
    users.readFile();
    stocks.readFile();
  }

  public User getCurrentUser() {
    return currentUser;
  }

  public PersistInflexPortfolios getInflexPortfolio() {
    return inflexPortfolio;
  }

  public PersistFlexPortfolios getFlexPortfolio() {
    return flexPortfolio;
  }

  public PersistStocks getStocks() {
    return stocks;
  }

  public boolean isLogin() {
    return currentUser != null;
  }

  /**
   * Validate user login.
   * @param username username
   * @param password password
   * @return true if success
   */
  public boolean userLogin(String username, String password) {
    if (
        users.getUsers().containsKey(username)
            && users.getUsers().get(username).verify(username, password)
    ) {
      currentUser = users.getUsers().get(username);
      return true;
    } else {
      System.out.println("Invalid username or password.");
      return false;
    }
  }

  public void userLogout() {
    currentUser = null;
  }

  /**
   * Validate user registration.
   * @param username username
   * @param password password
   * @return true if success
   */
  public boolean userRegister(String username, String password) {
    if (username.isEmpty() || password.isEmpty()) {
      System.out.println("Invalid username or password.");
      return false;
    }
    if (users.getUsers().containsKey(username)) {
      System.out.println("User already existed.");
      return false;
    }
    User user = new User(username, password);
    users.getUsers().put(username, user);
    users.persistFile();

    currentUser = user;
    return true;
  }



}
