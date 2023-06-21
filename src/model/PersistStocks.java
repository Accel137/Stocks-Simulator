package model;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * PersistStocks Class.
 */
public class PersistStocks extends AbstractPersist {

  private static final String STOCK_FILE_NAME = "stocks.csv";

  public static final Map<String, List<Stock>> STOCK = new HashMap<>();


  public Map<String, List<Stock>> getStocks() {
    return STOCK;
  }


  /**
   * Get stock price on exact date.
   * @param symbol symbol
   * @param date date
   * @return price
   */
  public double getStockExactPrice(String symbol, Date date) {
    if (STOCK.containsKey(symbol)) {
      for (Stock stock : STOCK.get(symbol)) {
        String stockDate = String.format("%tF", stock.getDate());
        String givenDate = String.format("%tF", date);
        if (stockDate.equals(givenDate)) {
          return stock.getPrice();
        }
      }
    }
    return -1;
  }


  @Override
  public void readFile() {
    CSV file = new CSV(DATA_PATH);
    boolean success = file.readFile(STOCK_FILE_NAME);
    if (success) {
      List<String> stocks = file.getContents();
      stocks.remove(0);
      for (String line : stocks) {
        String[] info = line.split(",");
        String[] dateStr = info[1].split("-");
        int year = Integer.parseInt(dateStr[0]);
        int month = Integer.parseInt(dateStr[1]) - 1;
        int day = Integer.parseInt(dateStr[2]);
        Calendar cal = Calendar.getInstance();
        cal.set(year, month, day);
        cal.set(Calendar.MILLISECOND, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.HOUR, 0);
        Stock stock = new Stock(
            info[0],
            0,
            Double.parseDouble(info[2]),
            cal.getTime(),
            0
        );
        if (!PersistStocks.STOCK.containsKey(info[0])) {
          PersistStocks.STOCK.put(info[0], new ArrayList<>());
        }
        PersistStocks.STOCK.get(info[0]).add(stock);
      }
    }
  }

  @Override
  public void persistFile() {
    List<String> stocks = new ArrayList<>();
    stocks.add("symbol,timestamp,close");
    for (List<Stock> stockLst : PersistStocks.STOCK.values()) {
      for (Stock stock : stockLst) {
        stocks.add(stock.toString(false));
      }
    }
    CSV file = new CSV(DATA_PATH);
    file.setContents(stocks);
    boolean success = file.writeFile(STOCK_FILE_NAME);
  }
}
