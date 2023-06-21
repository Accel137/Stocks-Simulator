package view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Calendar;
import java.util.Date;
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
 * DollarCost Class handles that dollar-cost investment.
 */
public class DollarCost implements ActionListener {

  private final JTextField startBar = new JTextField(10);
  private final JLabel startDate = new JLabel("Start Date");
  private final JTextField endBar = new JTextField(10);
  private final JLabel endDate = new JLabel("End Date");
  private final JTextField freqBar = new JTextField(10);
  private final JLabel freq = new JLabel("Frequency(Days)");
  private final JTextField totalBar = new JTextField(10);
  private final JLabel total = new JLabel("Total Dollar");
  private final MainView mainView;

  StockTableModel leftTableModel;
  JTable leftTable;

  StockTableModel rightTableModel;

  JTable rightTable;
  String currentPortfolio;


  public DollarCost(MainView mainView) {
    this.mainView = mainView;
  }

  /**
   * Render the page.
   * @param portfolio portfolio name
   */
  public void render(String portfolio) {
    currentPortfolio = portfolio;
    mainView.mainPanel.removeAll();
    SpringLayout leftSpring = new SpringLayout();
    JPanel leftPanel = new JPanel(leftSpring);
    leftPanel.setPreferredSize(new Dimension(mainView.mainPanel.getWidth() / 2, 0));
    startDate.setPreferredSize(new Dimension(70, 20));
    endDate.setPreferredSize(new Dimension(70, 20));
    freq.setPreferredSize(new Dimension(70, 20));
    total.setPreferredSize(new Dimension(70, 20));

    startBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    endBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    freqBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    totalBar.setBorder(BorderFactory.createLineBorder(Color.BLACK));

    JPanel options = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));

    options.add(startDate);
    options.add(startBar);
    options.add(endDate);
    options.add(endBar);
    options.add(freq);
    options.add(freqBar);
    options.add(total);
    options.add(totalBar);
    mainView.mainPanel.add(options, BorderLayout.NORTH);

    mainView.mainPanel.add(leftPanel, BorderLayout.WEST);
    JButton add = new JButton("Add");
    mainView.formatButton(add, 100, 20);
    leftPanel.add(add);
    add.addActionListener(this);

    leftTableModel = StockTableModel.updateData(null, Handler.DOLLAR_COST_COL, true);
    leftTable = new JTable(leftTableModel);

    JScrollPane leftJScrollPane = mainView.adjustTable(leftTable);

    leftPanel.add(leftJScrollPane);

    leftSpring.putConstraint(
        SpringLayout.SOUTH, leftJScrollPane, -2, SpringLayout.NORTH, add
    );
    leftSpring.putConstraint(
        SpringLayout.NORTH, leftJScrollPane, 0, SpringLayout.NORTH, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.WEST, leftJScrollPane, 5, SpringLayout.WEST, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.SOUTH, add, -10, SpringLayout.SOUTH, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.EAST, add, -10, SpringLayout.EAST, leftPanel
    );

    SpringLayout rightSpring = new SpringLayout();
    JPanel rightPanel = new JPanel(rightSpring);
    rightPanel.setPreferredSize(new Dimension(mainView.mainPanel.getWidth() / 2, 0));

    JButton invest = new JButton("Invest");
    invest.addActionListener(this);
    JButton save = new JButton("Save");
    mainView.formatButton(invest, 80, 20);
    mainView.formatButton(save, 80, 20);
    options.add(invest);
    options.add(save);
    mainView.mainPanel.add(rightPanel, BorderLayout.EAST);

    rightTableModel = StockTableModel.updateData(null, Handler.PORTFOLIO_COL, false);
    rightTable = new JTable(rightTableModel);

    JScrollPane rightJScrollPane = mainView.adjustTable(rightTable);
    rightPanel.add(rightJScrollPane);
    rightSpring.putConstraint(
        SpringLayout.WEST, rightJScrollPane, 5, SpringLayout.WEST, rightPanel
    );
    mainView.updateTable(rightTableModel, portfolio);
    save.addActionListener(this);
    mainView.mainPanel.revalidate();
    mainView.mainPanel.repaint();
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JButton jButton = (JButton) e.getSource();
    String text = jButton.getText();
    switch (text) {
      case "Invest":
        try {
          String startDate = this.startBar.getText();
          String endDate = this.endBar.getText();
          Date start = Handler.parseDate(startDate);
          int freqDay = Integer.parseInt(freqBar.getText());

          System.out.printf("%tF", start);
          Date end = null;
          if (!endDate.isEmpty()) {
            end = Handler.parseDate(endDate);
          } else {
            end = new Date();
          }

          double dollar = Double.parseDouble(this.totalBar.getText());
          int totalPercent = 0;
          for (Vector info : leftTableModel.getDataVector()) {
            if (info.size() != 0 && info.size() != 3) {
              JOptionPane.showMessageDialog(mainView, "Uncompleted form");
              return;
            } else if (info.size() == 3) {
              totalPercent += Integer.parseInt((String) info.get(1));
            }
          }
          if (totalPercent != 100) {
            JOptionPane.showMessageDialog(mainView, "Total percentage must be 100");
          } else {
            while (end.after(start)) {
              Vector<Vector<Object>> priceList = new Vector<>();
              for (Vector info : leftTableModel.getDataVector()) {
                String symbol = (String) info.get(0);
                int percentage = Integer.parseInt((String) info.get(1));
                double fees = Double.parseDouble((String) info.get(2));
                double price = Handler.getStockPriceByDate(symbol, start);
                if (price == 0) {
                  JOptionPane.showMessageDialog(
                      mainView, "Stock " + symbol + " was not found"
                  );
                  return;
                } else {
                  Vector<Object> data = new Vector<>();
                  data.addElement(symbol);
                  data.addElement(percentage);
                  data.addElement(fees);
                  data.addElement(price);
                  priceList.addElement(data);
                }
              }
              for (Vector<Object> data : priceList) {
                double shares = dollar * (int) data.get(1) / 100 / (double) data.get(3);
                ((FlexPortfolio) Handler
                    .CONTROLLER
                    .getCurrentUser()
                    .getPortfolios(currentPortfolio)
                ).buyStock(
                    (String) data.get(0), shares, start, (double) data.get(3), (double) data.get(2)
                );
              }
              Calendar cal = Calendar.getInstance();
              cal.setTime(start);
              cal.add(Calendar.DATE, freqDay);
              start = cal.getTime();
            }
            mainView.updateTable(rightTableModel, currentPortfolio);
          }
        } catch (IllegalArgumentException exception) {
          JOptionPane.showMessageDialog(mainView, "Invalid Input");
        }
        break;
      case "Save":
        Handler.CONTROLLER.getFlexPortfolio().persistFile();
        JOptionPane.showMessageDialog(mainView, "Saved.");
        break;
      case "Add":
        Vector<Object> row = new Vector<>();
        leftTableModel.addRow(row);
        break;
      default:
        break;
    }

  }
}

