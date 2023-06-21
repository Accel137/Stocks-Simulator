package view.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SpringLayout;

/**
 * Log in Page.
 */
public class LoginView extends JFrame implements ActionListener {

  private final Container container = getContentPane();

  private final JLabel title = new JLabel("Stocks Investment Simulator", JLabel.CENTER);
  private final JPanel titlePanel = new JPanel(new BorderLayout());
  private final JPanel logPanel = new JPanel();

  private final JLabel usernameLbl = new JLabel("Username");
  private final JLabel passwordLbl = new JLabel("Password");
  private JTextField username = new JTextField(20);
  private JPasswordField password = new JPasswordField(20);
  private final JButton echoPw = new JButton("Show");
  private final JButton loginBtn = new JButton("Log in");
  private final JButton registerBtn = new JButton("Register");
  private final JLabel invalid = new JLabel();

  /**
   * Log in GUI.
   */
  public LoginView() {
    super("Stocks Investment Simulator");
    setSize(960, 540);
    setResizable(false);
    setBackground(Color.WHITE);
    setLocationRelativeTo(null);
    setDefaultCloseOperation(EXIT_ON_CLOSE);
    renderLogin();
    setVisible(true);
  }

  private void renderLogin() {
    title.setFont(new Font(Font.SERIF, Font.BOLD, 60));
    titlePanel.add(title, BorderLayout.CENTER);
    titlePanel.setPreferredSize(new Dimension(0, 200));
    SpringLayout springLayout = new SpringLayout();
    logPanel.setLayout(springLayout);
    usernameLbl.setFont(new Font(Font.SERIF, Font.BOLD, 20));
    passwordLbl.setFont(new Font(Font.SERIF, Font.BOLD, 20));
    username.setFont(new Font(null, Font.BOLD, 15));
    password.setFont(new Font(null, Font.BOLD, 15));
    echoPw.setFont(new Font(null, Font.BOLD, 15));
    echoPw.setPreferredSize(new Dimension(60, 20));
    echoPw.setBackground(Color.LIGHT_GRAY);
    echoPw.setBorder(null);
    echoPw.setFocusPainted(false);
    echoPw.addActionListener(this);

    logPanel.add(usernameLbl);
    logPanel.add(passwordLbl);
    logPanel.add(username);
    logPanel.add(password);
    logPanel.add(echoPw);
    username.setBorder(null);
    password.setBorder(null);
    logPanel.add(loginBtn);
    loginBtn.setPreferredSize(new Dimension(140, 30));
    loginBtn.setBackground(Color.lightGray);
    loginBtn.setFocusPainted(false);
    loginBtn.setBorder(null);
    loginBtn.addActionListener(this);

    logPanel.add(registerBtn);
    registerBtn.setBackground(Color.LIGHT_GRAY);
    registerBtn.setPreferredSize(new Dimension(140, 30));
    registerBtn.setFocusPainted(false);
    registerBtn.setBorder(null);
    registerBtn.addActionListener(this);

    invalid.setFont(new Font(null, Font.PLAIN, 15));
    invalid.setForeground(Color.RED);
    logPanel.add(invalid);
    invalid.setVisible(false);

    springLayout.putConstraint(
        SpringLayout.WEST, usernameLbl, 300, SpringLayout.WEST, container
    );
    springLayout.putConstraint(
        SpringLayout.NORTH, passwordLbl, 20, SpringLayout.SOUTH, usernameLbl
    );
    springLayout.putConstraint(
        SpringLayout.EAST, passwordLbl, 0, SpringLayout.EAST, usernameLbl
    );
    springLayout.putConstraint(
        SpringLayout.WEST, username, 20, SpringLayout.EAST, usernameLbl
    );
    springLayout.putConstraint(
        SpringLayout.NORTH, username, 5, SpringLayout.NORTH, usernameLbl
    );
    springLayout.putConstraint(
        SpringLayout.WEST, password, 20, SpringLayout.EAST, passwordLbl
    );
    springLayout.putConstraint(
        SpringLayout.NORTH, password, 5, SpringLayout.NORTH, passwordLbl
    );
    springLayout.putConstraint(
        SpringLayout.NORTH, loginBtn, 100, SpringLayout.SOUTH, password
    );
    springLayout.putConstraint(
        SpringLayout.WEST, loginBtn, 0, SpringLayout.WEST, passwordLbl
    );
    springLayout.putConstraint(
        SpringLayout.NORTH, registerBtn, 0, SpringLayout.NORTH, loginBtn
    );
    springLayout.putConstraint(
        SpringLayout.EAST, registerBtn, 0, SpringLayout.EAST, password
    );
    springLayout.putConstraint(
        SpringLayout.NORTH, invalid, 5, SpringLayout.SOUTH, password
    );
    springLayout.putConstraint(
        SpringLayout.EAST, invalid, 0, SpringLayout.EAST, password
    );
    springLayout.putConstraint(
        SpringLayout.WEST, echoPw, 10, SpringLayout.EAST, password
    );
    springLayout.putConstraint(
        SpringLayout.NORTH, echoPw, 0, SpringLayout.NORTH, password
    );

    container.add(titlePanel, BorderLayout.NORTH);
    container.add(logPanel);
  }

  public JTextField getUsername() {
    return username;
  }

  public void setUsername(JTextField username) {
    this.username = username;
  }

  public JPasswordField getPassword() {
    return password;
  }

  public void setPassword(JPasswordField password) {
    this.password = password;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    JButton jButton = (JButton) e.getSource();
    String text = jButton.getText();
    String username = getUsername().getText();
    char[] chars = getPassword().getPassword();
    String password = new String(chars);
    switch (text) {
      case "Log in":
        if (!Handler.CONTROLLER.userLogin(username, password)) {
          invalid.setText("Invalid username or password");
          invalid.setVisible(true);
        } else {
          new MainView();
          dispose();
        }
        break;
      case "Register":
        if (username.isEmpty() || password.isEmpty()) {
          invalid.setText("Username or password cannot be empty");
          invalid.setVisible(true);
        } else if (!Handler.CONTROLLER.userRegister(username, password)) {
          invalid.setText("Username already existed");
          invalid.setVisible(true);
        } else {
          new MainView();
          dispose();
        }

        break;
      case "Show":
        this.password.setEchoChar((char) 0);
        echoPw.setText("Hide");
        break;
      case "Hide":
        this.password.setEchoChar('\u2022');
        echoPw.setText("Show");
        break;
      default:
        break;
    }
  }
}
