package com.epam.aviasales.repositories.impl;

import com.epam.aviasales.domain.Account;
import com.epam.aviasales.repositories.AccountRepository;
import com.epam.aviasales.util.HibernateUtil;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Query;
import org.hibernate.Session;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class AccountRepositoryImpl implements AccountRepository {

  private static volatile AccountRepository instance;

  public static AccountRepository getInstance() {
    AccountRepository localInstance = instance;
    if (localInstance == null) {
      synchronized (AccountRepositoryImpl.class) {
        localInstance = instance;
        if (localInstance == null) {
          instance = localInstance = new AccountRepositoryImpl();
        }
      }
    }

    return localInstance;
  }

  @Override
  public void addAccount(Account account) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    session.beginTransaction();
    session.save(account);
    session.getTransaction().commit();
    session.close();
  }

  @Override
  public Account getAccountById(Long id) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    Query query = session.createQuery("from Account WHERE id = :id");
    List list = query.setParameter("id", id).list();
    session.close();
    return list.size() > 0 ? (Account) list.get(0) : null;
  }

  @Override
  public List<Account> getAccountByLogin(String login) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    Query query = session.createQuery("from Account WHERE login = :login");
    query.setParameter("login", login);
    return query.list();
  }

  @Override
  public List<Account> getAccounts() {

    Session session = HibernateUtil.getSessionFactory().openSession();
    session.beginTransaction();

    Query query = session.createQuery("from Account");
    List list = query.list();

    session.getTransaction().commit();
    session.close();

    return (List<Account>) list;
  }

  @Override
  public List<Account> getAccounts(int page, int count) {
    throw new UnsupportedOperationException();
  }

  /*ToDo there is no check for row. Add it!, This is a bad way to choose the row*/
  public boolean isExist(String rowValue, String rowName) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    Query query = session.createQuery("from Account WHERE " + rowName + " = :parameter");
    query.setParameter("parameter", rowValue);
    List list = query.list();

    return !list.isEmpty();
  }
}
