package view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;
import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SpringLayout;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import model.FlexPortfolio;
import model.PersistFlexPortfolios;
import model.Portfolio;

/**
 * MyStock Class.
 */
public class MyStock extends JFrame implements ActionListener, ListSelectionListener {

  private final MainView mainView;
  private final JButton create = new JButton("Create");
  private final JButton view = new JButton("View Cost/Value");

  JList<String> portfolioList;
  StockTableModel portfolioTableModel;

  public MyStock(MainView mainView) {
    this.mainView = mainView;
  }

  /**
   * Render the page.
   */
  public void render() {
    mainView.mainPanel.removeAll();
    JPanel contentPanel = new JPanel(new BorderLayout());
    contentPanel.setBackground(Color.RED);
    mainView.formatButton(create, 100, 20);

    mainView.mainPanel.add(contentPanel);
    mainView.mainPanel.setPreferredSize(new Dimension(0, 508));
    SpringLayout leftSpring = new SpringLayout();
    JPanel leftPanel = new JPanel(leftSpring);
    leftPanel.setPreferredSize(new Dimension(475, 480));
    leftPanel.add(create);
    create.addActionListener(this);
    contentPanel.add(leftPanel, BorderLayout.WEST);
    DefaultListModel<String> listModel = updateListModel();

    portfolioList = new JList<>(listModel);
    portfolioList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
    portfolioList.addListSelectionListener(this);
    leftPanel.add(portfolioList);
    JLabel myPortfolio = new JLabel("My Portfolios");
    myPortfolio.setFont(new Font(null, Font.BOLD, 20));
    leftPanel.add(myPortfolio);
    portfolioList.setBorder(BorderFactory.createLineBorder(Color.BLACK));
    mainView.formatButton(view, 150, 20);
    leftPanel.add(view);
    view.addActionListener(this);
    leftSpring.putConstraint(
        SpringLayout.WEST, portfolioList, 5, SpringLayout.WEST, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.EAST, portfolioList, -5, SpringLayout.EAST, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.WEST, myPortfolio, 5, SpringLayout.WEST, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.NORTH, myPortfolio, 5, SpringLayout.NORTH, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.NORTH, portfolioList, 5, SpringLayout.SOUTH, myPortfolio
    );
    leftSpring.putConstraint(
        SpringLayout.SOUTH, portfolioList, -5, SpringLayout.SOUTH, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.EAST, create, -5, SpringLayout.EAST, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.NORTH, create, 10, SpringLayout.NORTH, leftPanel
    );
    leftSpring.putConstraint(
        SpringLayout.NORTH, view, 0, SpringLayout.NORTH, create
    );
    leftSpring.putConstraint(
        SpringLayout.EAST, view, -5, SpringLayout.WEST, create
    );

    SpringLayout rightSpring = new SpringLayout();
    JPanel rightPanel = new JPanel(rightSpring);
    JLabel contents = new JLabel("Contents");
    contents.setFont(new Font(null, Font.BOLD, 20));

    rightPanel.add(contents);
    rightPanel.setPreferredSize(new Dimension(475, 480));
    contentPanel.add(rightPanel, BorderLayout.EAST);

    portfolioTableModel = StockTableModel.updateData(null, Handler.PORTFOLIO_COL, false);

    JScrollPane jScrollPane = mainView.adjustTable(new JTable(portfolioTableModel));
    rightPanel.add(jScrollPane);
    rightSpring.putConstraint(
        SpringLayout.NORTH, contents, 5, SpringLayout.NORTH, rightPanel
    );
    rightSpring.putConstraint(
        SpringLayout.WEST, contents, 5, SpringLayout.WEST, rightPanel
    );

    rightSpring.putConstraint(
        SpringLayout.NORTH, jScrollPane, 5, SpringLayout.SOUTH, contents
    );
    rightSpring.putConstraint(
        SpringLayout.WEST, jScrollPane, 5, SpringLayout.WEST, rightPanel
    );
    rightSpring.putConstraint(
        SpringLayout.EAST, jScrollPane, -5, SpringLayout.EAST, rightPanel
    );
    rightSpring.putConstraint(
        SpringLayout.SOUTH, jScrollPane, -5, SpringLayout.SOUTH, rightPanel
    );

    mainView.container.add(mainView.mainPanel);
  }

  private void createPortfolio(String name) {
    if (Handler.CONTROLLER.getCurrentUser().checkDuplicatedPortfolio(name)) {
      JOptionPane.showMessageDialog(this, "Name duplicated!");
      return;
    }
    FlexPortfolio portfolio = new FlexPortfolio(name);
    Handler.CONTROLLER.getCurrentUser().addPortfolio(portfolio);
    PersistFlexPortfolios.PORTFOLIO.get(
        Handler.CONTROLLER.getCurrentUser().getUsername()
    ).add(portfolio);
    Handler.CONTROLLER.getFlexPortfolio().persistFile();
  }

  DefaultListModel<String> updateListModel() {
    DefaultListModel<String> listModel = new DefaultListModel<>();
    Map<String, Portfolio> currentPortfolios =
        Handler.CONTROLLER.getCurrentUser().getPortfolioMap();

    for (String key : currentPortfolios.keySet()) {
      if (currentPortfolios.get(key) instanceof FlexPortfolio) {
        listModel.addElement(key);
      }
    }
    return listModel;
  }


  @Override
  public void actionPerformed(ActionEvent e) {
    JButton jButton = (JButton) e.getSource();
    String text = jButton.getText();
    switch (text) {
      case "Create":
        String name1 = JOptionPane.showInputDialog("Please enter the portfolio name.");
        if (name1.isEmpty()) {
          JOptionPane.showMessageDialog(this, "Portfolio name cannot be empty!");
        }
        createPortfolio(name1);
        portfolioList.setModel(updateListModel());
        break;
      case "View Cost/Value":
        if (portfolioTableModel.getDataVector().isEmpty()) {
          JOptionPane.showMessageDialog(this, "Please select a portfolio");
        } else {
          try {
            String dateStr = JOptionPane.showInputDialog("Please enter the date.");
            Date date = Handler.parseDate(dateStr);
            String portfolio = portfolioList.getSelectedValue();
            double cost = Handler.CONTROLLER.getCurrentUser()
                .getPortfolios(portfolio)
                .getTotal(true, date);
            double value = Handler.CONTROLLER.getCurrentUser()
                .getPortfolios(portfolio)
                .getTotal(false, date);
            JPanel jPanel = new JPanel();
            JLabel costLbl = new JLabel(String.format("Cost: %.2f$", cost));
            JLabel valueLbl = new JLabel(String.format("Value: %.2f$", value));
            jPanel.add(costLbl);
            jPanel.add(valueLbl);
            JOptionPane.showMessageDialog(null, jPanel);
          } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, "Invalid date");
          }
        }
        break;
      case "Load":
        final JFileChooser fchooser = new JFileChooser(".");
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
            "CSV or TXT", "csv", "txt");
        fchooser.setFileFilter(filter);
        int retvalue = fchooser.showOpenDialog(this);
        if (retvalue == JFileChooser.APPROVE_OPTION) {
          File file = fchooser.getSelectedFile();
          try {
            String filename = fchooser.getName(file);
            filename = filename.substring(0, filename.length() - 4);
            Scanner scanner = new Scanner(file);

            FlexPortfolio portfolio = new FlexPortfolio(filename);
            Handler.CONTROLLER.getCurrentUser().addPortfolio(portfolio);
            if (scanner.hasNextLine()) {
              scanner.nextLine();
            }
            while (scanner.hasNextLine()) {
              String line = scanner.nextLine();
              String[] split = line.split(",");
              if (split.length != 5) {
                JOptionPane.showMessageDialog(this, "File format is incorrect");
                return;
              }
              String symbol = split[0];
              double share = Double.parseDouble(split[1]);
              String dateStr = split[2];
              double price = Double.parseDouble(split[3]);
              double fee = Double.parseDouble(split[4]);
              Date date = Handler.parseDate(dateStr);
              ((FlexPortfolio) Handler.CONTROLLER.getCurrentUser().getPortfolios(filename))
                  .buyStock(symbol, share, date, price, fee);
            }
            PersistFlexPortfolios.PORTFOLIO.get(
                Handler.CONTROLLER.getCurrentUser().getUsername()
            ).add(portfolio);
            Handler.CONTROLLER.getFlexPortfolio().persistFile();
            portfolioList.setModel(updateListModel());

          } catch (IOException exception) {
            JOptionPane.showMessageDialog(this, "Reading file errors");
          } catch (IllegalArgumentException exception) {
            JOptionPane.showMessageDialog(this, "File contents error");
          }
        }
        break;
      default:
        break;
    }
  }

  @Override
  public void valueChanged(ListSelectionEvent e) {
    if (e.getValueIsAdjusting()) {
      String name = portfolioList.getSelectedValue();
      mainView.updateTable(portfolioTableModel, name);
    }
  }
}
