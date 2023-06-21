package model;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Portfolio Interface.
 */
public interface Portfolio {

  /**
   * Get portfolio name.
   * @return name
   */
  String getName();

  /**
   * Get portfolio stocks.
   * @return stock map
   */
  Map<String, StockList> getStocks();

  /**
   * Get selling value of the portfolio.
   * @param date date
   * @return value
   */
  double getSellingValue(Date date);


  /**
   * Get total value.
   * @param isCost if the value is the coast
   * @param date date
   * @return the total value
   */
  double getTotal(boolean isCost, Date date);

  /**
   * Get the composition of the portfolio.
   * @param sort sort method
   * @param order ascend or descend
   * @return list of stock
   */
  List<Stock> getComposition(String sort, boolean order);
}
