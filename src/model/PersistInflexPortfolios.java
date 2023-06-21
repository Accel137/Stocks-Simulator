package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import view.gui.Handler;

/**
 * PersistInflexPortfolios Class.
 */
public class PersistInflexPortfolios extends AbstractPersist {

  public static String FLEX_PORTFOLIO_FILE_NAME = "inflexportfolio_%s_%s.csv";

  public static final Map<String, List<Portfolio>> PORTFOLIO = new HashMap<>();

  @Override
  public void readFile() {
    File dir = new File(DATA_PATH);
    String[] portFolioFiles = dir.list((dir1, name) -> name.startsWith("inflexportfolio"));
    if (portFolioFiles == null) {
      System.out.println("PortFolio file is empty.");
      return;
    }
    for (String portfolioFileName : portFolioFiles) {
      String[] tmp = portfolioFileName.substring(
          0, portfolioFileName.indexOf(".")
      ).split("_");
      if (tmp.length != 3) {
        continue;
      }
      String username = tmp[1];
      String portfolioName = tmp[2];
      InflexPortfolio portfolio = new InflexPortfolio(portfolioName);
      try (BufferedReader br = new BufferedReader(
          new FileReader(DATA_PATH + File.separator + portfolioFileName))) {
        String line;
        boolean firstLine = true;
        while ((line = br.readLine()) != null) {
          if (line.isEmpty()) {
            continue;
          }
          if (firstLine) {
            firstLine = false;
            continue;
          }
          tmp = line.split(",");
          String symbol = tmp[0];
          int share = Integer.parseInt(tmp[1]);
          String dateStr = tmp[2];
          double price = Double.parseDouble(tmp[3]);
          Date date = Handler.parseDate(dateStr);
          portfolio.loadStock(
              symbol,
              share,
              date,
              price
          );
        }
        if (!PORTFOLIO.containsKey(username)) {
          PORTFOLIO.put(username, new ArrayList<>());
        }
        PORTFOLIO.get(username).add(portfolio);
      } catch (IOException e) {
        System.out.println("Error loading portfolio file, " + e.getMessage());
      }
    }
  }

  @Override
  public void persistFile() {
    for (String username : PORTFOLIO.keySet()) {
      for (Portfolio portfolio : PORTFOLIO.get(username)) {
        String filename = String.format(
            FLEX_PORTFOLIO_FILE_NAME,
            username,
            portfolio.getName()
        );
        List<String> portfolioList = new ArrayList<>();
        portfolioList.add("symbol,share,buydate,price");
        for (StockList stockList : portfolio.getStocks().values()) {
          for (Stock stock : stockList.getStockList()) {
            portfolioList.add(stock.toString(true).substring(0, stock.toString().length() - 5));
          }
        }
        CSV file = new CSV(DATA_PATH);
        file.setContents(portfolioList);
        boolean success = file.writeFile(filename);
      }


    }
    System.out.println("Persist portfolio to files.");
  }
}
