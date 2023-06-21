package view.textbased;

import api.AlphaVantageAPI;
import controller.Controller;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import model.FlexPortfolio;
import model.InflexPortfolio;
import model.PersistFlexPortfolios;
import model.PersistInflexPortfolios;
import model.Portfolio;
import model.StockList;

/**
 * Handler Class.
 */
public class View {

  private final Scanner scanner;
  private final Controller controller;


  private final Calendar cal;

  /**
   * Constructor.
   */
  public View() {
    cal = Calendar.getInstance();
    cal.setLenient(false);
    controller = new Controller();
    scanner = new Scanner(System.in);
  }

  /**
   * run program.
   */
  public void run() {
    if (controller.isLogin()) {
      printFullMenu();
    } else {
      printLoginMenu();
    }
    while (true) {
      if (!controller.isLogin()) {
        beforeLoginProcess(getInput("Please input selected id: "));
      } else {
        afterLoginProcess(getInput("Please input selected id: "));
      }
    }
  }

  /**
   * beforeLoginProcess.
   *
   * @param input selected idx.
   */
  private void beforeLoginProcess(String input) {
    int selected = parseInput(input);
    if (selected == 3) {
      System.exit(0);
    }
    if (selected == 1) {
      boolean success = controller.userLogin(
          getInput("Please enter your username: "),
          getInput("Please enter your password: ")
      );
      if (success) {
        printFullMenu();
      }
    } else if (selected == 2) {
      boolean success = controller.userRegister(
          getInput("Please enter your username: "),
          getInput("Please enter your password: ")
      );
      if (success) {
        printFullMenu();
      }
    } else {
      System.out.println("Invalid selected: " + input);
    }
  }

  /**
   * afterLoginProcess.
   *
   * @param input selected index.
   */
  private void afterLoginProcess(String input) {
    int selected = parseInput(input);
    if (selected == 13) {
      System.exit(0);
    }

    if (selected == 1) {
      viewPortfolioComposition();

    } else if (selected == 2) {
      if (controller.getCurrentUser().getPortfolioMap().isEmpty()) {
        System.out.println("No portfolio found.");
        return;
      }
      viewTotalValue();

    } else if (selected == 3) {
      if (controller.getCurrentUser().getPortfolioMap().isEmpty()) {
        System.out.println("No portfolio found");
        return;
      }
      viewTotalCost();

    } else if (selected == 4) {
      createFlexPortfolio();

    } else if (selected == 5) {
      createInflexPortfolio();
    } else if (selected == 6) {
      persistPortfolio();
    } else if (selected == 7) {
      if (controller.getCurrentUser().getPortfolioMap().isEmpty()) {
        System.out.println("No portfolio found");
      }
      buyStocks();

    } else if (selected == 8) {
      if (controller.getCurrentUser().getPortfolioMap().isEmpty()) {
        System.out.println("No portfolio found");
      }
      sellStocks();
    } else if (selected == 9) {
      controller.userLogout();
      printLoginMenu();
    } else if (selected == 10) {
      viewPortfolioPerformance();
    } else if (selected == 11) {
      viewOnlinePrice();
    } else if (selected == 12) {
      searchSymbol();
    } else {
      System.out.println("Invalid selected: " + input);
    }
  }

  private void searchSymbol() {
    String symbol = getInput("Please enter the stock symbol: ");
    AlphaVantageAPI api = new AlphaVantageAPI("QT71J4DHBCXGTVFW");
    Map<String, String> optional = new HashMap<>();
    optional.put("datatype", "csv");
    api.querySearch(symbol, optional).forEach(System.out::println);
  }

  private void viewOnlinePrice() {
    String symbol = getInput("Please enter the stock symbol: ");

    AlphaVantageAPI api = new AlphaVantageAPI("QT71J4DHBCXGTVFW");
    Map<String, String> optional = new HashMap<>();
    optional.put("datatype", "csv");
    api.queryDaily(symbol, optional).forEach(System.out::println);
  }

  private void viewPortfolioComposition() {
    this.controller.getCurrentUser().showPortfoliosNames(true, true);
    String name = getInput("Please input portfolio name: ");
    Portfolio portfolio = controller.getCurrentUser().getPortfolios(name);
    if (portfolio == null) {
      System.out.println("Portfolio not found.");
    } else {
      System.out.printf("Portfolio %s.%n", name);
      System.out.println("Symbol,Shares,Date,Price,Fees");
      portfolio.getComposition("symbol", true).forEach(System.out::println);
    }
  }

  private void viewPortfolioPerformance() {
    controller.getCurrentUser().showPortfoliosNames(true, false);
    String name = getInput("Please input portfolio name: ");
    Portfolio portfolio = controller.getCurrentUser().getPortfolios(name);
    if (portfolio == null) {
      System.out.println("Portfolio not found");
    } else {
      System.out.printf("Performance of portfolio %s at recent 10 available days(at most)%n", name);
      String performance = ((FlexPortfolio) portfolio).getPerformance();
      System.out.println(performance);
    }
  }

  private void viewTotalValue() {
    controller.getCurrentUser().showPortfoliosNames(true, true);
    String name = getInput("Please input a portfolio name: ");
    Portfolio portfolio = controller.getCurrentUser().getPortfolios(name);
    if (portfolio == null) {
      System.out.println("Portfolio not found");
    } else {
      String dateStr = getInput("Please input date (like 2022-10-11): ");
      Date date = parseDate(dateStr);
      if (date != null) {
        double totalValue = portfolio.getSellingValue(date);
        System.out.println("Total value of portfolio " + name + " is : " + totalValue);
      }
    }
  }

  private void viewTotalCost() {
    controller.getCurrentUser().showPortfoliosNames(true, false);
    String name = getInput("Please input portfolio name: ");
    Portfolio portfolio = controller.getCurrentUser().getPortfolios(name);
    if (portfolio == null) {
      System.out.println("Portfolio not found");
    } else {
      String dateStr = getInput("Please input date (like 2022-10-11): ");
      Date date = parseDate(dateStr);
      if (portfolio instanceof InflexPortfolio) {
        System.out.println("Cannot view the total cost of inflexible portfolios.");
      } else {
        double totalCost = ((FlexPortfolio) portfolio).getCostBasis(date);
        System.out.println("Total cost of portfolio " + name + " is : " + totalCost);
      }

    }
  }

  private void persistPortfolio() {
    controller.getCurrentUser().showPortfoliosNames(true, true);
    String name = getInput("Please input portfolio name: ");
    Portfolio portfolio = controller.getCurrentUser().getPortfolios(name);
    if (portfolio == null) {
      System.out.println("Portfolio not found");
    } else {
      if (portfolio instanceof FlexPortfolio) {
        controller.getFlexPortfolio().persistFile();
      } else {
        controller.getInflexPortfolio().persistFile();
      }
    }
  }

  private void buyStocks() {
    controller.getCurrentUser().showPortfoliosNames(true, true);
    String name = getInput("Please input portfolio name: ");
    Portfolio portfolio = controller.getCurrentUser().getPortfolios(name);
    if (portfolio == null) {
      System.out.println("Portfolio not found");
    } else {
      while (true) {
        String symbol = getInput("Please input stock symbol"
            + "(like AAPL for apple), input . to break: ");
        if (symbol.equals(".")) {
          break;
        }
        if (!controller.getStocks().getStocks().containsKey(symbol)) {
          System.out.println("Stock not found.");
          return;
        }

        String date = getInput("Please input date(format: 2022-11-12): ");
        if (date == null) {
          System.out.println("Date is invalid");
          return;
        }

        double price = controller.getStocks().getStockExactPrice(symbol, parseDate(date));
        if (price == -1) {
          System.out.printf("Price of stock %s on %s not found.\n", symbol, date);
        } else {
          System.out.println("Price on " + date + " is " + price);

          int share = parseInput(getInput("Please input share number: "));
          if (share <= 0) {
            System.out.println("Invalid share number");
            return;
          }
          if (portfolio instanceof FlexPortfolio) {
            double fee = parseInput(getInput("Please input fees:"));
            ((FlexPortfolio) portfolio).buyStock(symbol, share, parseDate(date), price, fee);
          } else {
            ((InflexPortfolio) portfolio).buyStock(symbol, share, parseDate(date), price);
          }

        }
      }
    }
  }

  private void sellStocks() {
    controller.getCurrentUser().showPortfoliosNames(true, false);
    String name = getInput("Please input portfolio name: ");
    Portfolio portfolio = controller.getCurrentUser().getPortfolios(name);
    if (portfolio == null) {
      System.out.println("Portfolio not found");
    } else {
      Map<String, StockList> stockBuyMap = portfolio.getStocks();
      while (true) {
        System.out.println("All exist stocks: " + stockBuyMap.keySet());
        String symbol = getInput("Please input stock symbol to sell: ");
        if (symbol.equals(".")) {
          return;
        }
        if (!stockBuyMap.containsKey(symbol)) {
          System.out.println("Stock not exist in portfolio");
          continue;
        }

        StockList stockList = stockBuyMap.get(symbol);
        Date date = parseDate(getInput("Please input date to sell(format: 2022-11-11): "));
        if (date == null) {
          return;
        }
        int share = parseInput(getInput("Please input share number to sell: "));
        stockList.sellShares(share, date);
      }
    }
  }

  /**
   * create flexible portfolio.
   */
  private void createFlexPortfolio() {
    String name = getInput("Please enter new portfolio name: ");
    if (controller.getCurrentUser().checkDuplicatedPortfolio(name)) {
      System.out.println("Portfolio already existed.");
      return;
    }

    FlexPortfolio portfolio = new FlexPortfolio(name);
    while (true) {
      String symbol = getInput("Please input stock symbol"
          + "(like AAPL for apple), input . to break: ");
      if (symbol.equals(".")) {
        break;
      }

      String date = getInput("Please input date(format: 2022-11-12): ");

      double price = controller.getStocks().getStockExactPrice(symbol, parseDate(date));
      if (price == -1) {
        System.out.printf("Price of stock %s on %s not found.\n", symbol, date);
      } else {
        System.out.println("Price on " + date + " is " + price);

        int share = parseInput(getInput(("Please input share number: ")));
        if (share <= 0) {
          System.out.println("Invalid share number");
          return;
        }
        double fee = parseInput(getInput("Please input fees:"));
        portfolio.buyStock(symbol, share, parseDate(date), price, fee);
      }
    }
    controller.getCurrentUser().addPortfolio(portfolio);
    PersistFlexPortfolios.PORTFOLIO.get(
        controller.getCurrentUser().getUsername()
    ).add(portfolio);
    System.out.println("Create success");
  }

  /**
   * create flexible portfolio.
   */
  private void createInflexPortfolio() {
    String name = getInput("Please enter new portfolio name: ");
    if (controller.getCurrentUser().checkDuplicatedPortfolio(name)) {
      System.out.println("Portfolio already existed.");
      return;
    }

    InflexPortfolio portfolio = new InflexPortfolio(name);
    while (true) {
      String symbol = getInput("Please input stock symbol"
          + "(like AAPL for apple), input . to break: ");
      if (symbol.equals(".")) {
        break;
      }

      String date = getInput("Please input date(format: 2022-11-12): ");

      double price = controller.getStocks().getStockExactPrice(symbol, parseDate(date));
      if (price == -1) {
        System.out.printf("Price of stock %s on %s not found.\n", symbol, date);
      } else {
        System.out.println("Price on " + date + " is " + price);

        int share = parseInput(getInput(("Please input share number: ")));
        if (share <= 0) {
          System.out.println("Invalid share number");
          return;
        }
        portfolio.buyStock(symbol, share, parseDate(date), price);
      }
    }
    controller.getCurrentUser().addPortfolio(portfolio);
    PersistInflexPortfolios.PORTFOLIO.get(
        controller.getCurrentUser().getUsername()
    ).add(portfolio);
    System.out.println("Create success");
  }

  /**
   * print menu before login.
   */
  private void printLoginMenu() {
    System.out.println("Welcome");
    System.out.println("1. Login");
    System.out.println("2. Register");
    System.out.println("3. Exit");
  }

  /**
   * print menu after login.
   */
  private void printFullMenu() {
    System.out.println("Welcome");
    System.out.println("1. View Portfolio Composition");
    System.out.println("2. View Portfolio Value");
    System.out.println("3. View Portfolio Cost");
    System.out.println("4. Create Flexible Portfolio");
    System.out.println("5. Create Inflexible Portfolio");
    System.out.println("6. Persist Portfolio");
    System.out.println("7. Buy Stocks");
    System.out.println("8. Sell Stocks");
    System.out.println("9. Logout");
    System.out.println("10. View Portfolio Performance");
    System.out.println("11. View Online Price");
    System.out.println("12. Search Stock Symbols");
    System.out.println("13. Exit");
  }


  /**
   * Get string input.
   *
   * @param message input message
   * @return input string
   */
  public String getInput(String message) {
    System.out.println(message);
    return this.scanner.nextLine();
  }

  /**
   * Get num input.
   *
   * @param input print message
   * @return int value
   */
  public int parseInput(String input) {
    try {
      return Integer.parseInt(input);
    } catch (Exception e) {
      return 0;
    }
  }

  private Date parseDate(String date) {
    String[] dateStr = date.split("-");
    if (dateStr.length == 3) {
      try {
        int year = Integer.parseInt(dateStr[0]);
        int month = Integer.parseInt(dateStr[1]);
        int day = Integer.parseInt(dateStr[2]);
        cal.set(year, month - 1, day);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        return cal.getTime();
      } catch (IllegalArgumentException e) {
        System.out.println("Invalid date.");
      }
    } else {
      System.out.println("Invalid date.");
    }
    return null;
  }

}
