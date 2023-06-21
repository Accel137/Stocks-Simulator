package model;

import java.util.Date;

/**
 * Flex Portfolio Class.
 */
public class InflexPortfolio extends AbstractPortfolio<InflexPortfolio> {

  public InflexPortfolio(String name) {
    super(name);
  }

  /**
   * Buy a stock.
   * @param symbol stock symbol
   * @param share share
   * @param date date
   * @param price price
   */
  public void buyStock(String symbol, int share, Date date, double price) {
    if (!stocks.containsKey(symbol)) {
      stocks.put(symbol, new StockList(symbol));
      stocks.get(symbol).buyShares(share, price, date, 0);
    } else {
      System.out.println("The stock already exists, can't be modified.");
    }
  }

  /**
   * Sell a stock.
   * @param symbol stock symbol
   * @param share share
   * @param date date
   * @param price price
   */
  public void loadStock(String symbol, int share, Date date, double price) {
    if (!stocks.containsKey(symbol)) {
      stocks.put(symbol, new StockList(symbol));
    }
    stocks.get(symbol).buyShares(share, price, date, 0);
  }

}
