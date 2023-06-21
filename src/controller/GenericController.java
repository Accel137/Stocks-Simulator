package controller;

/**
 * Generic Controller Interface.
 */
public interface GenericController {
  boolean isLogin();

  boolean userLogin(String username, String password);

  void userLogout();

  boolean userRegister(String username, String password);

}
