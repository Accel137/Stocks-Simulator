package model;

import java.util.Date;

/**
 * Stock Utility.
 */
public class Stock {

  private final String symbol;
  private double share;
  private final double price;

  private final Date date;

  private final double fee;

  /**
   * Stock Class.
   * @param symbol stock symbol
   * @param share stock share
   * @param price stock price
   * @param date buying date
   * @param fee fee
   */
  public Stock(String symbol, double share, double price, Date date, double fee) {
    this.symbol = symbol;
    this.share = share;
    this.price = price;
    this.date = date;
    this.fee = fee;
  }

  public String getSymbol() {
    return this.symbol;
  }

  public double getShare() {
    return this.share;
  }

  public double getPrice() {
    return price;
  }

  public double getCostPrice() {
    return this.price;
  }

  /**
   * Get price of the stock.
   * @param date date
   * @return price
   */
  public double getCostPriceByDate(Date date) {
    if (this.date.after(date)) {
      return 0;
    } else {
      return this.price * this.share + fee;
    }
  }

  public double getFee() {
    return fee;
  }

  public Date getDate() {
    return this.date;
  }

  public void buyShare(double share) {
    this.share += share;
  }

  /**
   * Sell specific number of shares.
   * @param share share
   * @return true if success
   */
  public boolean sellShare(int share) {
    if (this.share < share) {
      System.out.println("No enough shares.");
      return false;
    }
    this.share -= share;
    return true;
  }

  /**
   * To string.
   * @return string
   */
  public String toString() {
    return String.format("%s,%.2f,%tF,%.2f,%.2f",
        this.symbol, this.share, this.date, this.price, this.fee);
  }

  /**
   * To string.
   * @param hasShare if include shares
   * @return string
   */
  public String toString(boolean hasShare) {
    if (hasShare) {
      return String.format("%s,%.2f,%tF,%.2f,%.2f",
          this.symbol, this.share, this.date, this.price, this.fee);
    } else {
      return String.format("%s,%tF,%.2f,%.2f",
          this.symbol, this.date, this.price, this.fee);
    }
  }
}
