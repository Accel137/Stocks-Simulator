import controller.Controller;
import java.util.Date;
import model.FlexPortfolio;
import org.junit.Before;
import org.junit.Test;
import view.gui.Handler;
import static  org.junit.Assert.assertEquals;

/**
 * GUI View Test Class.
 */
public class GUITest {
  Controller controller;

  @Before
  public void start() {
    controller = new Controller();
    controller.userLogin("abc", "123");

  }


  @Test
  public void testCost() {
    Date date = Handler.parseDate("2022-11-18");
    double cost = controller.getCurrentUser()
        .getPortfolios("D1")
        .getTotal(true, date);
    assertEquals(6191.68, cost, 2);
  }

  @Test
  public void testNegativeSell() {
    ((FlexPortfolio)controller.getCurrentUser().getPortfolios("D1")
    ).sellStock(
        "GOOG", -100, Handler.parseDate("2022-11-18"), 0
    );
    Date date = Handler.parseDate("2022-11-18");
    double cost = controller.getCurrentUser()
        .getPortfolios("D1")
        .getTotal(true, date);
    assertEquals(6191.68, cost, 2);
  }

  @Test
  public void testNegativeBuy() {
    ((FlexPortfolio)controller.getCurrentUser().getPortfolios("D1")
    ).buyStock(
        "GOOG", -100, Handler.parseDate("2022-11-18"), 90, 0
    );
    Date date = Handler.parseDate("2022-11-18");
    double cost = controller.getCurrentUser()
        .getPortfolios("D1")
        .getTotal(true, date);
    assertEquals(6191.68, cost, 2);
  }
}
