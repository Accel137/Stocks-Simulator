package view.gui;

import api.AlphaVantageAPI;
import controller.Controller;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

/**
 * GUI Handler.
 */
public class Handler {

  public static Controller CONTROLLER = new Controller();
  public static AlphaVantageAPI API = new AlphaVantageAPI("QT71J4DHBCXGTVFW");

  public static Vector<String> STOCK_COL = new Vector<>();
  public static Vector<String> PORTFOLIO_COL = new Vector<>();
  public static Vector<String> DOLLAR_COST_COL = new Vector<>();

  /**
   * Parse the string to the date.
   * @param date string
   * @return Date
   */
  public static Date parseDate(String date) {
    Calendar cal = Calendar.getInstance();
    String[] time = date.split("-");
    int year = Integer.parseInt(time[0]);
    int month = Integer.parseInt(time[1]);
    int day = Integer.parseInt(time[2]);
    cal.set(year, month - 1, day);
    cal.set(Calendar.MILLISECOND, 0);
    cal.set(Calendar.SECOND, 0);
    cal.set(Calendar.MINUTE, 0);
    cal.set(Calendar.HOUR, 0);
    return cal.getTime();
  }

  /**
   * Get online price.
   * @param symbol stock symbol
   * @param date date
   * @return price
   */
  public static double getStockPriceByDate(String symbol, Date date) {
    String currentDate = String.format("%tF", date);
    AlphaVantageAPI api = new AlphaVantageAPI("QT71J4DHBCXGTVFW");
    Map<String, String> optional = new HashMap<>();
    optional.put("datatype", "csv");
    List<String> results = Handler.API.queryDaily(symbol, optional);
    System.out.println(results);
    if (results.isEmpty()) {
      return 0;
    }
    results.remove(0);
    double result = 0;
    for (String line : results) {
      String[] split = line.split(",");
      String timestamp = split[0];
      double price = Double.parseDouble(split[4]);
      if (timestamp.equals(currentDate)) {
        result = price;
      }
    }
    if (result == 0) {
      result = Double.parseDouble(results.get(0).split(",")[4]);
    }
    System.out.println(result);
    return result;
  }
}
