package view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.plaf.basic.BasicScrollBarUI;
import model.FlexPortfolio;
import model.Stock;

/**
 * Main GUI.
 */
public class MainView extends JFrame implements ActionListener {

  final Container container = getContentPane();
  final JButton load = new JButton("Load");

  final JButton create = new JButton("My Stock");
  final JButton trade = new JButton("Buy/Sell");
  final JButton dollarCostInvest = new JButton("Dollar-Cost");
  final JButton logout = new JButton("Log out");
  final JLabel user = new JLabel(Handler.CONTROLLER.getCurrentUser().getUsername(), JLabel.LEFT);
  final JPanel mainPanel = new JPanel(new BorderLayout());
  final JPanel menu = new JPanel();


  final Trade tradePage = new Trade(this);
  final MyStock myStock = new MyStock(this);
  final DollarCost dollarCost = new DollarCost(this);

  /**
   * Render the page.
   */
  public MainView() {
    super("Stocks Investment Simulator");
    setSize(960, 540);
    setResizable(false);
    setBackground(Color.WHITE);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    Handler.STOCK_COL.addElement("Symbol");
    Handler.STOCK_COL.addElement("Timestamp");
    Handler.STOCK_COL.addElement("Price");
    Handler.PORTFOLIO_COL.addElement("Symbol");
    Handler.PORTFOLIO_COL.addElement("Timestamp");
    Handler.PORTFOLIO_COL.addElement("Price");
    Handler.PORTFOLIO_COL.addElement("Shares");
    Handler.PORTFOLIO_COL.addElement("Fees");
    Handler.DOLLAR_COST_COL.addElement("Symbol");
    Handler.DOLLAR_COST_COL.addElement("Percentage");
    Handler.DOLLAR_COST_COL.addElement("Fees");

    SpringLayout springLayout = new SpringLayout();
    menu.setLayout(springLayout);
    menu.setBackground(Color.LIGHT_GRAY);
    menu.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    menu.setPreferredSize(new Dimension(0, 32));
    formatButton(create, 100, 30);
    formatButton(load, 100, 30);

    formatButton(trade, 100, 30);
    formatButton(dollarCostInvest, 100, 30);
    formatButton(logout, 100, 30);
    create.addActionListener(this);
    load.addActionListener(myStock);
    trade.addActionListener(this);
    dollarCostInvest.addActionListener(this);
    logout.addActionListener(this);

    menu.add(create);
    menu.add(load);
    menu.add(trade);
    menu.add(dollarCostInvest);
    menu.add(logout);

    user.setFont(new Font(null, Font.BOLD, 20));
    user.setBackground(Color.LIGHT_GRAY);
    menu.add(user);

    springLayout.putConstraint(
        SpringLayout.WEST, create, 0, SpringLayout.WEST, container
    );
    springLayout.putConstraint(
        SpringLayout.WEST, trade, 0, SpringLayout.EAST, create
    );
    springLayout.putConstraint(
        SpringLayout.WEST, dollarCostInvest, 0, SpringLayout.EAST, trade
    );
    springLayout.putConstraint(
        SpringLayout.WEST, load, 0, SpringLayout.EAST, dollarCostInvest
    );
    springLayout.putConstraint(
        SpringLayout.WEST, logout, 0, SpringLayout.EAST, load
    );
    springLayout.putConstraint(
        SpringLayout.WEST, user, 10, SpringLayout.EAST, logout
    );
    container.add(menu, BorderLayout.NORTH);
    myStock.render();
    setVisible(true);
  }

  JScrollPane adjustTable(JTable jTable) {
    jTable.getTableHeader().setFont(new Font(null, Font.BOLD, 14));
    jTable.setGridColor(Color.BLACK);
    JScrollPane jScrollPane = new JScrollPane(jTable);
    jTable.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    jTable.setBorder(BorderFactory.createEmptyBorder());
    jTable.getSelectionModel().setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    jScrollPane.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    jScrollPane.getVerticalScrollBar().setBackground(Color.WHITE);
    jScrollPane.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
      @Override
      protected void configureScrollBarColors() {
        this.thumbColor = Color.LIGHT_GRAY;
      }
    });
    jScrollPane.getVerticalScrollBar().getComponent(0).setBackground(Color.WHITE);
    jScrollPane.getVerticalScrollBar().getComponent(1).setBackground(Color.WHITE);
    return jScrollPane;
  }

  void formatButton(JButton button, int width, int height) {
    button.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    button.setBackground(Color.LIGHT_GRAY);
    button.setFont(new Font(null, Font.BOLD, 15));
    button.setFocusPainted(false);
    button.setPreferredSize(new Dimension(width, height));
  }

  public static void main(String[] args) {
    new MainView();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JButton jButton = (JButton) e.getSource();
    String text = jButton.getText();
    switch (text) {
      case "My Stock":
        myStock.render();
        break;
      case "Buy/Sell":
        String name2 = JOptionPane.showInputDialog("Please enter the portfolio name.");
        if (name2 == null || name2.isEmpty()) {
          JOptionPane.showMessageDialog(this, "Portfolio name cannot be empty!");
        } else if (Handler.CONTROLLER.getCurrentUser().getPortfolios(name2) == null) {
          JOptionPane.showMessageDialog(this, "Portfolio name does not exist!");
        } else {
          tradePage.render(name2);
        }

        break;
      case "Dollar-Cost":
        String name3 = JOptionPane.showInputDialog("Please enter the portfolio name.");
        if (name3 == null || name3.isEmpty()) {
          JOptionPane.showMessageDialog(this, "Portfolio name cannot be empty!");
        } else if (Handler.CONTROLLER.getCurrentUser().getPortfolios(name3) == null) {
          JOptionPane.showMessageDialog(this, "Portfolio name does not exist!");
        } else {
          dollarCost.render(name3);
        }
        break;
      case "Log out":
        Handler.CONTROLLER.userLogout();
        new LoginView();
        dispose();
        break;
      default:
        break;
    }
  }

  /**
   * Update rendered table.
   * @param table table
   * @param name name
   */
  public void updateTable(StockTableModel table, String name) {
    table.setRowCount(0);
    FlexPortfolio portfolio = (FlexPortfolio) Handler.CONTROLLER.getCurrentUser()
        .getPortfolios(name);
    List<Stock> stockList = portfolio.getComposition("date", false);
    for (Stock stock : stockList) {
      Vector<Object> info = new Vector<>();
      info.add(stock.getSymbol());
      info.add(String.format("%tF", stock.getDate()));
      info.add(stock.getPrice());
      info.add(stock.getShare());
      info.add(stock.getFee());
      table.addRow(info);
    }
  }
}

