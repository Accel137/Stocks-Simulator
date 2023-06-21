package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

abstract class AbstractPortfolio<T> implements Portfolio {
  protected final String name;
  protected final Map<String, StockList> stocks;

  protected AbstractPortfolio(String name) {
    this.name = name;
    this.stocks = new HashMap<>();
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public Map<String, StockList> getStocks() {
    return stocks;
  }

  @Override
  public double getSellingValue(Date date) {
    return getTotal(false, date);
  }

  @Override
  public double getTotal(boolean isCost, Date date) {
    double total = 0;
    for (String symbol : stocks.keySet()) {
      StockList stockList = stocks.get(symbol);
      total += isCost ? stockList.getTotalCost(date) : stockList.getTotalValue(date);
    }
    return total;
  }

  @Override
  public List<Stock> getComposition(String sort, boolean order) {
    List<Stock> totalStockList = new ArrayList<>();
    for (StockList stockList : stocks.values()) {
      totalStockList.addAll(stockList.getStockList());
    }
    switch (sort) {
      case "share":
        if (order) {
          totalStockList.sort(Comparator.comparing(Stock::getShare));
        } else {
          totalStockList.sort(Comparator.comparing(Stock::getShare).reversed());
        }
        break;
      case "symbol":
        if (order) {
          totalStockList.sort(Comparator.comparing(Stock::getSymbol));
        } else {
          totalStockList.sort(Comparator.comparing(Stock::getSymbol).reversed());
        }
        break;
      case "price":
        if (order) {
          totalStockList.sort(Comparator.comparing(Stock::getCostPrice));
        } else {
          totalStockList.sort(Comparator.comparing(Stock::getCostPrice).reversed());
        }
        break;
      default:
        if (order) {
          totalStockList.sort(Comparator.comparing(Stock::getDate));
        } else {
          totalStockList.sort(Comparator.comparing(Stock::getDate).reversed());
        }
        break;
    }
    return totalStockList;
  }
}
