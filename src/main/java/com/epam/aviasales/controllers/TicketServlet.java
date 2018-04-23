package com.epam.aviasales.controllers;

import com.epam.aviasales.domain.Account;
import com.epam.aviasales.domain.Flight;
import com.epam.aviasales.domain.PersonalData;
import com.epam.aviasales.domain.Ticket;
import com.epam.aviasales.exceptions.NoAvailableSeatsForTheFlight;
import com.epam.aviasales.exceptions.PersonalDataAlreadyExists;
import com.epam.aviasales.services.PersonalDataService;
import com.epam.aviasales.services.TicketService;
import com.epam.aviasales.services.FlightService;
import com.epam.aviasales.services.impl.FlightServiceImpl;
import com.epam.aviasales.services.impl.PersonalDataServiceImpl;
import com.epam.aviasales.services.impl.TicketServiceImpl;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;

public class TicketServlet extends HttpServlet {

  private TicketService ticketService;
  private FlightService flightsService;
  private PersonalDataService personalDataService;

  @Override
  public void init() throws ServletException {
    ticketService = TicketServiceImpl.getInstance();
    flightsService = FlightServiceImpl.getInstance();
    personalDataService = PersonalDataServiceImpl.getInstance();
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (req.getSession().getAttribute("account") == null) {
      resp.sendError(403);
    } else {
      doPost(req, resp);
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp)
      throws ServletException, IOException {
    if (req.getSession().getAttribute("flight").equals("")) {
      if (req.getParameter("selected_flight") == null ||
          req.getParameter("selected_flight").equals("")) {
        resp.sendRedirect("/flights");
        return;
      } else {
        Long flightNumber = Long.valueOf(req.getParameter("selected_flight"));
        Flight flight = flightsService.getFlightById(flightNumber);
        req.getSession().setAttribute("flight", flight);
      }
    }

    if (req.getParameter("first_name") == null) {
      req.getRequestDispatcher("/WEB-INF/ticket.jsp").forward(req, resp);
      return;
    }

    String firstName = req.getParameter("first_name").trim();
    String lastName = req.getParameter("last_name").trim();
    String passport = req.getParameter("passport").trim();
    if (firstName.equals("") || lastName.equals("") || passport.equals("") ||
        req.getParameter("birthday").trim().equals("")) {
      req.setAttribute("error", "error.type_personal_data");
      req.getRequestDispatcher("/WEB-INF/ticket.jsp").forward(req, resp);
      return;
    }
    LocalDate birthday = LocalDate.parse(req.getParameter("birthday"));
    if (birthday.toString().compareTo(LocalDate.now().toString()) >= 0) {
      req.setAttribute("error", "error.birthday_after_now");
      req.getRequestDispatcher("/WEB-INF/ticket.jsp").forward(req, resp);
      return;
    }
    if ((req.getParameter("isBusiness") == null)) {
      req.setAttribute("error", "error.choose_class");
      req.getRequestDispatcher("/WEB-INF/ticket.jsp").forward(req, resp);
      return;
    }
    Boolean isBusiness = Boolean.parseBoolean(req.getParameter("isBusiness"));
    PersonalData personalData = PersonalData.builder().name(firstName + " " + lastName)
        .passport(passport).dateOfBirth(birthday).build();
    try {
      personalDataService.addPersonalData(personalData);
    } catch (PersonalDataAlreadyExists e) {
      req.setAttribute("error", "error.such_person_is_exist");
      req.getRequestDispatcher("/WEB-INF/ticket.jsp").forward(req, resp);
      return;
    }
    personalData = personalDataService.getPersonalDataByPassport(passport);
    Flight flight = (Flight) req.getSession().getAttribute("flight");
    Integer basePrice = Integer.parseInt(req.getSession().getAttribute("multiplier").toString());
    Integer price = isBusiness ? (new Double(basePrice * 1.4)).intValue() : basePrice;
    Account account = (Account) req.getSession().getAttribute("account");
    Ticket ticket = Ticket.builder().personalData(personalData).flight(flight).account(account)
        .price(price).isBusiness(isBusiness).build();
    try {
      ticketService.addTicket(ticket);
    } catch (NoAvailableSeatsForTheFlight e) {
      req.setAttribute("error", "error.bought");
      flight = flightsService
          .getFlightById(((Flight) req.getSession().getAttribute("flight")).getId());
      req.getSession().setAttribute("flight", flight);
      req.getRequestDispatcher("/WEB-INF/ticket.jsp").forward(req, resp);
      return;
    }
    req.getSession().setAttribute("ticket", ticket);
    resp.sendRedirect("/confirm");
  }
}
