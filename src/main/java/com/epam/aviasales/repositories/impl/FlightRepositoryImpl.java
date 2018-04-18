package com.epam.aviasales.repositories.impl;

import com.epam.aviasales.domain.Flight;
import com.epam.aviasales.repositories.FlightRepository;
import com.epam.aviasales.util.HibernateUtil;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;

import java.util.List;
import org.hibernate.criterion.Restrictions;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FlightRepositoryImpl implements FlightRepository {

  private static volatile FlightRepository instance;

  public static FlightRepository getInstance() {
    FlightRepository localInstance = instance;
    if (localInstance == null) {
      synchronized (FlightRepositoryImpl.class) {
        localInstance = instance;
        if (localInstance == null) {
          instance = localInstance = new FlightRepositoryImpl();
        }
      }
    }

    return localInstance;
  }

  @Override
  public void addFlight(Flight flight) {
    Session session = HibernateUtil.getSessionFactory().openSession();

    session.beginTransaction();

    session.save(flight);

    session.getTransaction().commit();
    session.close();
  }

  @Override
  public boolean isExist(Long id) {
    return getFlightById(id) != null;
  }

  @Override
  public void deleteFlight(Long id) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    session.beginTransaction();

    session.delete(getFlightById(id));

    session.getTransaction().commit();
    session.close();
  }

  @Override
  public Flight getFlightById(Long id) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    session.beginTransaction();

    Query query = session.createQuery("from Flight where id=:id");
    query.setParameter("id", id);
    List list = query.list();

    session.getTransaction().commit();
    session.close();

    return list.size() > 0 ? (Flight) list.get(0) : null;
  }

  @Override
  public List<Flight> getFlights(Long fromId, Long toId) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    session.beginTransaction();

    Query query = session.createQuery("from Flight where id>=:from and id<=:to");
    query.setParameter("from", fromId);
    query.setParameter("to", toId);
    List list = query.list();

    session.getTransaction().commit();
    session.close();

    return list.size() > 0 ? (List<Flight>) list : null;
  }

  @Override
  public void updateFlight(Long id, Flight updatedFlight) {
    Session session = HibernateUtil.getSessionFactory().openSession();
    session.beginTransaction();

    if (id != updatedFlight.getId()) {
      updatedFlight.setId(id);
    }
    session.update(updatedFlight);

    session.getTransaction().commit();
    session.close();
  }

  @Override
  public List<Flight> getFlightsLike(Flight seekingFlight, int page, int size) {
    if (size <= 0) {
      return new ArrayList<>();
    }
    if (seekingFlight == null) {
      return getFlightsPage(page, size);
    }
    if (page < 1) {
      page = 1;
    }
    // pages start from 1
    page -= 1;

    Session session = HibernateUtil.getSessionFactory().openSession();
    session.beginTransaction();
    Criteria criteria = session.createCriteria(Flight.class);
    if (seekingFlight.getId() != null) {
      criteria.add(Restrictions.eq("id", seekingFlight.getId()));
    }
    if (seekingFlight.getFromAirport() != null) {
      criteria.add(Restrictions.eq("fromAirport", seekingFlight.getFromAirport()));
    }
    if (seekingFlight.getToAirport() != null) {
      criteria.add(Restrictions.eq("toAirport", seekingFlight.getToAirport()));
    }
    if (seekingFlight.getAirplane() != null) {
      criteria.add(Restrictions.eq("airplane", seekingFlight.getAirplane()));
    }
    if (seekingFlight.getDepartureTime() != null) {
      criteria.add(Restrictions.eq("departureTime", seekingFlight.getDepartureTime()));
    }
    if (seekingFlight.getArrivalTime() != null) {
      criteria.add(Restrictions.eq("arrivalTime", seekingFlight.getArrivalTime()));
    }
    if (seekingFlight.getBaseTicketPrice() != null) {
      criteria.add(Restrictions.eq("baseTicketPrice", seekingFlight.getBaseTicketPrice()));
    }
    if (seekingFlight.getExtraBaggagePrice() != null) {
      criteria.add(Restrictions.eq("extraBaggagePrice", seekingFlight.getExtraBaggagePrice()));
    }
    if (seekingFlight.getFreeSeatEconomy() != null) {
      criteria.add(Restrictions.eq("freeSeatEconomy", seekingFlight.getFreeSeatEconomy()));
    }
    if (seekingFlight.getFreeSeatBusiness() != null) {
      criteria.add(Restrictions.eq("freeSeatBusiness", seekingFlight.getFreeSeatBusiness()));
    }
    criteria.setFirstResult(page * size);
    criteria.setMaxResults(size);

    List<Flight> result = (List<Flight>) criteria.list();
    session.getTransaction().commit();
    session.close();

    return result;
  }

  @Override
  public List<Flight> getFlights() {
    Session session = HibernateUtil.getSessionFactory().openSession();
    session.beginTransaction();

    Query query = session.createQuery("from Flight");
    List list = query.list();

    session.getTransaction().commit();
    session.close();

    return list.size() > 0 ? (List<Flight>) list : null;
  }

  @Override
  public List<Flight> getFlightsPage(int page, int count) {
    throw new UnsupportedOperationException();
  }
}
