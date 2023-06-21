package model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * User Class.
 */
public class User {

  private final String username;

  private final String password;

  private final Map<String, Portfolio> portfolios;

  /**
   * Constructor.
   *
   * @param username username.
   * @param password password.
   */
  public User(String username, String password) {
    this.username = username;
    this.password = password;
    this.portfolios = new HashMap<>();
    loadPortfolio();
  }

  public String getUsername() {
    return username;
  }

  private void loadPortfolio() {
    List<Portfolio> inflex = PersistInflexPortfolios.PORTFOLIO.get(username);
    if (inflex != null) {
      for (Portfolio portfolio : inflex) {
        portfolios.put(portfolio.getName(), portfolio);
      }
      List<Portfolio> flex = PersistFlexPortfolios.PORTFOLIO.get(username);
      for (Portfolio portfolio : flex) {
        portfolios.put(portfolio.getName(), portfolio);
      }
    }
  }


  public Portfolio getPortfolios(String name) {
    return portfolios.get(name);
  }

  public Map<String, Portfolio> getPortfolioMap() {
    return portfolios;
  }

  /**
   * Get all names of the porfolio of current user.
   * @param flex flex
   * @param inflex inflex
   */
  public void showPortfoliosNames(boolean flex, boolean inflex) {
    System.out.println("All Portfolios Names:");
    StringJoiner joiner = new StringJoiner(",");

    if (flex) {
      for (Portfolio portfolio : portfolios.values()) {
        if (portfolio instanceof FlexPortfolio) {
          joiner.add(portfolio.getName());
        }
      }
      System.out.printf("Flex: %s\n", joiner);
    }
    if (inflex) {
      joiner = new StringJoiner(",");
      for (Portfolio portfolio : portfolios.values()) {
        if (portfolio instanceof InflexPortfolio) {
          joiner.add(portfolio.getName());
        }
      }
      System.out.printf("Inflex: %s\n", joiner);
    }
  }

  /**
   * Verify if the username and the password are correct.
   *
   * @param username username.
   * @param password password.
   * @return true if username and password are the same.
   */
  public boolean verify(String username, String password) {
    return this.username.equals(username) && this.password.equals(password);
  }

  /**
   * Check if the portfolio name is duplicate.
   *
   * @param name portfolio name.
   * @return true if duplicate
   */
  public boolean checkDuplicatedPortfolio(String name) {
    return this.portfolios.containsKey(name);
  }

  /**
   * Add a new portfolio to the current user.
   *
   * @param portfolio portfolio.
   */
  public void addPortfolio(Portfolio portfolio) {
    portfolios.put(portfolio.getName(), portfolio);

  }

  @Override
  public String toString() {
    return username + "," + password;
  }


}
