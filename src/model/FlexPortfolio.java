package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Flex Portfolio Class.
 */
public class FlexPortfolio extends AbstractPortfolio<FlexPortfolio> {

  private double commissionFees;

  public FlexPortfolio(String name) {
    super(name);
    this.commissionFees = 0;
  }

  /**
   * Buy a stock.
   * @param symbol stock symbol
   * @param share share
   * @param date date
   * @param price price
   * @param fees fees
   */
  public void buyStock(String symbol, double share, Date date, double price, double fees) {
    if (share < 0) {
      return;
    }
    if (!stocks.containsKey(symbol)) {
      stocks.put(symbol, new StockList(symbol));
    }
    stocks.get(symbol).buyShares(share, price, date, fees);
    commissionFees += fees;
  }

  /**
   * Sell a stock.
   * @param symbol stock symbol
   * @param shares share
   * @param date date
   * @param fees fees
   */
  public void sellStock(String symbol, int shares, Date date, double fees) {
    if (!stocks.containsKey(symbol)) {
      System.out.println("Cannot find the stock.");
    } else {
      boolean sold = stocks.get(symbol).sellShares(shares, date);
      if (sold) {
        commissionFees += fees;
      }
    }
  }

  public double getCostBasis(Date date) {
    return getTotal(true, date) + commissionFees;
  }

  /**
   * Get performance of the portfolio.
   * @return string
   */
  public String getPerformance() {
    StringBuilder builder = new StringBuilder();
    List<Stock> totalStockList = new ArrayList<>();
    for (StockList stockList : stocks.values()) {
      totalStockList.addAll(stockList.getStockList());
    }
    totalStockList.sort(Comparator.comparing(Stock::getDate));
    Calendar cal = Calendar.getInstance();

    Date startDate = totalStockList.get(0).getDate();
    Date endDate = totalStockList.get(totalStockList.size() - 1).getDate();

    long different_days = (endDate.getTime() - startDate.getTime()) / 86400000;
    different_days++;
    builder.append(String.format(
        "Performance of portfolio %s from %tF to %tF\n\n",
        name,
        startDate,
        endDate
    ));
    cal.setTime(endDate);
    increaseDate(cal, different_days);
    endDate = cal.getTime();

    cal.setTime(startDate);
    increaseDate(cal, different_days);
    startDate = cal.getTime();
    Date currentDate = cal.getTime();
    List<Double> values = new ArrayList<>();
    while (currentDate.before(endDate) || currentDate.equals(endDate)) {
      values.add(getSellingValue(currentDate));
      increaseDate(cal, different_days);
      currentDate = cal.getTime();
    }
    double max = values.get(0);
    double min = max;
    for (double value: values) {
      if (value > max) {
        max = value;
      }
      if (value < min) {
        min = value;
      }
    }

    values.forEach(System.out::println);
    cal.setTime(startDate);
    currentDate = cal.getTime();
    int index = 0;
    int scale = 1;
    while (min > 50) {
      scale *= 10;
      min /= 10;
    }


    while (currentDate.before(endDate) || currentDate.equals(endDate)) {
      String month = "";
      int year = cal.get(Calendar.YEAR);
      int day = cal.get(Calendar.DAY_OF_MONTH);
      String stars = "*".repeat((int) Math.ceil(values.get(index) / scale));
      if (different_days < 30) {
        month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        builder.append(String.format("%s %d %d: %s\n", month, day - 1, year, stars));
      } else {
        cal.add(Calendar.MONTH, -1);
        month = cal.getDisplayName(Calendar.MONTH, Calendar.SHORT, Locale.getDefault());
        cal.add(Calendar.MONTH, 1);
        builder.append(String.format("%s %d: %s\n", month, year, stars));
      }
      increaseDate(cal, different_days);
      index++;
      currentDate = cal.getTime();
    }
    builder.append(String.format("Scale: * = $%d", scale));
    return builder.toString();
  }

  private void increaseDate(Calendar cal, long differentDays) {
    if (differentDays < 5) {
      cal.add(Calendar.HOUR, 4);
    } else if (5 <= differentDays && differentDays < 30) {
      cal.add(Calendar.DAY_OF_MONTH, 1);
    } else {
      cal.add(Calendar.MONTH, 1);
      cal.set(
          Calendar.DAY_OF_MONTH,
          cal.getActualMaximum(Calendar.DAY_OF_MONTH)
      );
    }
  }

}
