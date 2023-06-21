package api;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

/**
 * AlphaVantageAPI class.
 */
public class AlphaVantageAPI extends AbstractAPI<AlphaVantageAPI> {
  private final String api;

  public AlphaVantageAPI(String api) {
    super("https://www.alphavantage.co/query?");
    this.api = api;
  }

  @Override
  public List<String> queryDaily(String symbol, Map<String, String> optional) {
    try {
      String url = this.url + String.format(
          "function=TIME_SERIES_DAILY_ADJUSTED&symbol=%s&apikey=%s",
          symbol, this.api
      );
      return query(optional, url);
    } catch (MalformedURLException e) {
      System.out.printf("Errors on URL, %s.\n", e.getMessage());
      return null;
    } catch (IOException e) {
      System.out.println("No price data found.");
      return null;
    }
  }

  private List<String> query(Map<String, String> optional, String url) throws IOException {
    URL query = new URL(makeURL(url, optional).toString());
    InputStream input;
    List<String> output = new ArrayList<>();
    input = query.openStream();
    int b;
    StringBuilder builder = new StringBuilder();
    while ((b = input.read()) != -1) {
      if ((char) b == '\n') {
        output.add(builder.toString());
        builder = new StringBuilder();
      } else {
        builder.append((char) b);
      }
    }
    if (output.get(0).equals("{")) {
      return new ArrayList<>();
    }

    return output;
  }

  @Override
  public List<String> querySearch(String symbol, Map<String, String> optional) {
    try {
      String url = this.url + String.format(
          "function=SYMBOL_SEARCH&keywords=%s&apikey=%s",
          symbol, this.api
      );
      return query(optional, url);
    } catch (MalformedURLException e) {
      System.out.printf("Errors on URL, %s.\n", e.getMessage());
      return null;
    } catch (IOException e) {
      System.out.println("No search results found.");
      return null;
    }
  }

  private StringJoiner makeURL(String url, Map<String, String> params) {
    StringJoiner joiner = new StringJoiner("&");
    joiner.add(url);
    for (String key : params.keySet()) {
      joiner.add(String.format("%s=%s", key, params.get(key)));
    }
    return joiner;
  }
}
