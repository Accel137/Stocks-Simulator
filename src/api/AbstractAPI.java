package api;

import java.util.List;
import java.util.Map;

abstract class AbstractAPI<T> implements API {
  protected String url;


  public AbstractAPI(String url) {
    this.url = url;
  }

  public abstract List<String> queryDaily(String symbol, Map<String, String> optional);

  public abstract List<String> querySearch(String symbol, Map<String, String> parameters);

}
