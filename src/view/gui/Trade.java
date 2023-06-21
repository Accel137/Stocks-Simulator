package view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import model.FlexPortfolio;

/**
 * Trade Class handles buy and sell.
 */
public class Trade implements ActionListener, MouseListener {

  private final JTextField searchBar = new JTextField();
  private final JLabel searchLbl = new JLabel("Search By Name");
  private final JButton searchBtn = new JButton("Search");
  private final MainView mainView;

  StockTableModel leftTableModel;
  JTable leftTable;

  StockTableModel rightTableModel;

  JTable rightTable;

  Vector<Object> selectedStock = null;
  Vector<Object> selectedPortfolio = null;

  String currentPortfolio;


  public Trade(MainView mainView) {
    this.mainView = mainView;
  }

  /**
   * Render the page.
   * @param portfolio name
   */
  public void render(String portfolio) {
    currentPortfolio = portfolio;
    mainView.mainPanel.removeAll();
    SpringLayout leftSpring = new SpringLayout();
    JPanel leftPanel = new JPanel(leftSpring);
    leftPanel.setPreferredSize(new Dimension(mainView.mainPanel.getWidth() / 2, 0));

    searchBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    mainView.formatButton(searchBtn, 100, 20);
    leftPanel.add(searchLbl);
    leftPanel.add(searchBar);
    leftPanel.add(searchBtn);
    searchBtn.addActionListener(this);
    mainView.mainPanel.add(leftPanel, BorderLayout.WEST);

    leftSpring.putConstraint(
        SpringLayout.WEST, searchLbl, 10, SpringLayout.WEST, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.NORTH, searchLbl, 5, SpringLayout.NORTH, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.WEST, searchBar, 5, SpringLayout.EAST, searchLbl
    );

    leftSpring.putConstraint(
        SpringLayout.NORTH, searchBar, 2, SpringLayout.NORTH, searchLbl
    );
    leftSpring.putConstraint(
        SpringLayout.EAST, searchBar, -5, SpringLayout.WEST, searchBtn
    );
    leftSpring.putConstraint(
        SpringLayout.EAST, searchBtn, -20, SpringLayout.EAST, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.NORTH, searchBtn, -2, SpringLayout.NORTH, searchBar
    );

    leftTableModel = StockTableModel.updateData(null, Handler.STOCK_COL, false);
    leftTable = new JTable(leftTableModel);

    JScrollPane leftJScrollPane = mainView.adjustTable(leftTable);

    leftPanel.add(leftJScrollPane);

    leftSpring.putConstraint(
        SpringLayout.NORTH, leftJScrollPane, 5, SpringLayout.SOUTH, searchBar
    );
    leftSpring.putConstraint(
        SpringLayout.WEST, leftJScrollPane, 5, SpringLayout.WEST, leftPanel
    );

    SpringLayout rightSpring = new SpringLayout();
    JPanel rightPanel = new JPanel(rightSpring);
    rightPanel.setPreferredSize(new Dimension(mainView.mainPanel.getWidth() / 2, 0));

    JButton buy = new JButton("Buy");
    buy.addActionListener(this);
    JButton sell = new JButton("Sell");
    JButton save = new JButton("Save");
    mainView.formatButton(buy, 80, 20);
    mainView.formatButton(sell, 80, 20);
    mainView.formatButton(save, 80, 20);
    rightPanel.add(buy);
    rightPanel.add(sell);
    rightPanel.add(save);
    mainView.mainPanel.add(rightPanel, BorderLayout.EAST);

    rightSpring.putConstraint(
        SpringLayout.EAST, save, -10, SpringLayout.EAST, rightPanel
    );
    rightSpring.putConstraint(
        SpringLayout.NORTH, save, 5, SpringLayout.NORTH, rightPanel
    );
    rightSpring.putConstraint(
        SpringLayout.EAST, sell, -10, SpringLayout.WEST, save
    );

    rightSpring.putConstraint(
        SpringLayout.NORTH, sell, 0, SpringLayout.NORTH, save
    );
    rightSpring.putConstraint(
        SpringLayout.EAST, buy, -10, SpringLayout.WEST, sell
    );
    rightSpring.putConstraint(
        SpringLayout.NORTH, buy, 0, SpringLayout.NORTH, sell
    );

    rightTableModel = StockTableModel.updateData(null, Handler.PORTFOLIO_COL, false);
    rightTable = new JTable(rightTableModel);

    JScrollPane rightJScrollPane = mainView.adjustTable(rightTable);
    rightPanel.add(rightJScrollPane);

    rightSpring.putConstraint(
        SpringLayout.NORTH, rightJScrollPane, 5, SpringLayout.SOUTH, buy
    );
    rightSpring.putConstraint(
        SpringLayout.WEST, rightJScrollPane, 5, SpringLayout.WEST, rightPanel
    );
    mainView.updateTable(rightTableModel, portfolio);
    leftTable.addMouseListener(this);
    rightTable.addMouseListener(this);
    save.addActionListener(this);
    sell.addActionListener(this);
    mainView.mainPanel.revalidate();
    mainView.mainPanel.repaint();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JButton jButton = (JButton) e.getSource();
    String text = jButton.getText();
    switch (text) {
      case "Search":
        String keyword = searchBar.getText();
        Map<String, String> optional = new HashMap<>();
        optional.put("datatype", "csv");
        List<String> results = Handler.API.queryDaily(keyword, optional);
        leftTableModel.setRowCount(0);
        results.remove(0);
        for (String line : results) {
          Vector<Object> stock = new Vector<>();
          String[] split = line.split(",");
          stock.addElement(keyword);
          stock.addElement(split[0]);
          stock.addElement(split[4]);
          leftTableModel.addRow(stock);
        }
        break;
      case "Buy":
        if (selectedStock == null) {
          JOptionPane.showMessageDialog(mainView, "Please select a stock first.");
        } else {
          try {
            int shares = Integer.parseInt(
                JOptionPane.showInputDialog("Please enter a share number:"));
            if (shares <= 0) {
              JOptionPane.showMessageDialog(mainView, "Share number must greater than 0");
            } else {
              double fees = Double.parseDouble(JOptionPane.showInputDialog("Please enter fees:"));
              String symbol = (String) selectedStock.get(0);
              String timestamp = (String) selectedStock.get(1);
              Date date = Handler.parseDate(timestamp);
              double price = Double.parseDouble((String) selectedStock.get(2));
              ((FlexPortfolio) Handler
                  .CONTROLLER
                  .getCurrentUser()
                  .getPortfolios(currentPortfolio)).buyStock(
                  symbol, shares, date, price, fees
              );
              mainView.updateTable(rightTableModel, currentPortfolio);
            }
          } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(mainView, "Please enter a valid number.");
          }
        }
        break;
      case "Sell":
        if (selectedPortfolio == null) {
          JOptionPane.showMessageDialog(mainView, "Please select a stock first.");
        } else {
          try {
            int shares = Integer.parseInt(
                JOptionPane.showInputDialog("Please enter a share number:"));
            if (shares <= 0) {
              JOptionPane.showMessageDialog(mainView, "Share number must greater than 0");
            } else {
              System.out.println(selectedPortfolio);
              double totalShares = (double) selectedPortfolio.get(3);
              if (totalShares < shares) {
                JOptionPane.showMessageDialog(mainView, "No enough shares");
              } else {
                double fees = Double.parseDouble(JOptionPane.showInputDialog("Please enter fees:"));
                String symbol = (String) selectedPortfolio.get(0);
                String timestamp = (String) selectedPortfolio.get(1);
                Date date = Handler.parseDate(timestamp);
                ((FlexPortfolio) Handler
                    .CONTROLLER
                    .getCurrentUser()
                    .getPortfolios(currentPortfolio)).sellStock(
                    symbol, shares, date, fees
                );
                mainView.updateTable(rightTableModel, currentPortfolio);
              }
            }
          } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(mainView, "Please enter a valid number.");
          }
        }
        break;
      case "Save":
        Handler.CONTROLLER.getFlexPortfolio().persistFile();
        JOptionPane.showMessageDialog(mainView, "Saved.");
        break;
      default:
        break;
    }

  }

  @Override
  public void mouseClicked(MouseEvent e) {
    if (e.getButton() == MouseEvent.BUTTON1) {
      if (leftTable.getSelectedRow() != -1) {
        selectedStock = leftTableModel.getDataVector().get(leftTable.getSelectedRow());
        System.out.println(selectedStock);
      }
      if (rightTable.getSelectedRow() != -1) {
        selectedPortfolio = rightTableModel.getDataVector().get(rightTable.getSelectedRow());
        System.out.println(selectedPortfolio);
      }
    }

  }

  @Override
  public void mousePressed(MouseEvent e) {
    //
  }

  @Override
  public void mouseReleased(MouseEvent e) {
    //
  }

  @Override
  public void mouseEntered(MouseEvent e) {
    //
  }

  @Override
  public void mouseExited(MouseEvent e) {
    //
  }
}

