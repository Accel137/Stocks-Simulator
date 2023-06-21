package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import view.gui.Handler;

/**
 * Stock List Class.
 */
public class StockList {

  private final String symbol;
  private final List<Stock> stockList;

  /**
   * StockList Class.
   * @param symbol stock symbol
   */
  public StockList(String symbol) {
    this.symbol = symbol;
    this.stockList = new ArrayList<>();
  }

  public List<Stock> getStockList() {
    return stockList;
  }

  /**
   * Buy a specific number of shares.
   * @param share share
   * @param price price
   * @param date date
   * @param fee fee
   */
  public void buyShares(double share, double price, Date date, double fee) {
    int index = findIndex(date);
    if (index != -1) {
      stockList.get(index).buyShare(share);
    } else {
      stockList.add(new Stock(symbol, share, price, date, fee));
    }
  }

  /**
   * Sell a specific number of shares.
   * @param share share
   * @param date date
   * @return true if success
   */
  public boolean sellShares(int share, Date date) {
    int index = findIndex(date);
    if (index != -1) {
      return stockList.get(index).sellShare(share);
    } else {
      System.out.println("Cannot find the date.");
      return false;
    }
  }

  /**
   * Find if the stock list has the given date.
   * @param date date
   * @return index
   */
  public int findIndex(Date date) {
    for (int i = 0; i < stockList.size(); i++) {
      if (stockList.get(i).getDate().getTime() == date.getTime()) {
        return i;
      }
    }
    return -1;
  }

  /**
   * Get value of the portfolio.
   * @param date date
   * @return value
   */
  public double getTotalValue(Date date) {
    double totalShares = 0;
    for (Stock stock : stockList) {
      if (!stock.getDate().after(date)) {
        totalShares += stock.getShare();
      }
    }
    return Handler.getStockPriceByDate(symbol, date) * totalShares;
  }

  public double getTotalCost(Date date) {
    return getTotal(date);
  }

  private double getTotal(Date date) {
    double total = 0;
    for (Stock stock : stockList) {
      total += stock.getCostPriceByDate(date);
    }
    return total;
  }

  /**
   * To string.
   * @return string
   */
  public String toString() {
    StringBuilder builder = new StringBuilder();
    for (Stock stock : stockList) {
      builder.append(stock.toString(true)).append('\n');
    }
    return builder.toString();
  }
}
