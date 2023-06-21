package api;

import java.util.List;
import java.util.Map;

/**
 * Generic API interface.
 */
public interface API {
  public List<String> queryDaily(String symbol, Map<String, String> optional);

  public List<String> querySearch(String symbol, Map<String, String> optional);


}
