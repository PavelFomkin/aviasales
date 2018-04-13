package com.epam.aviasales.services;

import com.epam.aviasales.domain.Account;
import com.epam.aviasales.repositories.AccountRepository;

public class AccountService {
  private static AccountService instance;

  private AccountService() {}

  public Account get(String login) {
    AccountRepository accountRepository = AccountRepository.getInstance();
    return accountRepository.getAccount(login);
  }

  public static synchronized AccountService getInstance() {
    if (instance == null) {
      instance = new AccountService();
    }
    return instance;
  }
/*ToDO add validation code*/
  public Account validate(String login, String password) {
    return new Account();
   // return "1@e.com".equals(login) && "1".equals(password);
  }

}
