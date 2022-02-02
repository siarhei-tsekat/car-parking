package net.tsekot.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.tsekot.domain.PaymentUserDetails;
import net.tsekot.persistence.dao.reservation.ReservationException;
import net.tsekot.persistence.entity.Reservation;
import net.tsekot.service.*;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static net.tsekot.util.ObjectUtils.notBlank;

public class ReservationController extends HttpServlet {

    private final static Logger logger = Logger.getLogger(ReservationController.class);
    private final ReservationService reservationService;
    private final PriceService priceService;
    private final UserService userService;
    private final PaymentService paymentService;

    public ReservationController() {
        this.reservationService = new ReservationService();
        this.priceService = new PriceService();
        this.userService = new UserService();
        this.paymentService = new PaymentService();
    }

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if ("PATCH".equals(req.getMethod())) {
            doPatch(req, resp);
        } else {
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("userId");
        String reservationId = req.getParameter("reservationId");
        String endTime = req.getParameter("endTime");

        if (notBlank(reservationId, endTime)) {
            calculatePriceForReservation(reservationId, endTime, req, resp);
        } else if (notBlank(userId)) {
            getReservationsByUser(userId, req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        reserveSpot(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("userId");
        String reservationId = req.getParameter("reservationId");

        if (notBlank(userId, reservationId)) {
            undoReservation(userId, reservationId, req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void doPatch(HttpServletRequest req, HttpServletResponse resp) {
        String reservationId = req.getParameter("reservationId");
        String userId = req.getParameter("userId");
        String startTime = req.getParameter("startTime");
        String endTime = req.getParameter("endTime");
        String totalCost = req.getParameter("totalCost");

        if (notBlank(reservationId, userId, startTime, endTime, totalCost)) {
            closeReservation(reservationId, userId, startTime, endTime, totalCost, req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }

    }

    private void closeReservation(String reservationId, String userId, String startTime, String endTime, String totalCost, HttpServletRequest req, HttpServletResponse resp) {
        try {
            PaymentUserDetails paymentUserDetails = userService.getUserPaymentDetails(userId);
            boolean paymentResult = paymentService.pay(paymentUserDetails, totalCost);

            if (paymentResult) {
                reservationService.cancelReservation(reservationId, userId, startTime, endTime, totalCost);
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().println("Payment failed");
            }

        } catch (Exception e) {
            logger.error(e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void calculatePriceForReservation(String reservationId, String endTime, HttpServletRequest req, HttpServletResponse resp) {

        try {
            Reservation reservation = reservationService.getReservation(reservationId);

            Double price = priceService.calculateCost(reservation.getStartTime(), parseTime(endTime), reservation.getPrice());
            resp.getWriter().println("Price: " + price);
        } catch (Exception e) {
            logger.error(e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void getReservationsByUser(String userId, HttpServletRequest req, HttpServletResponse resp) {
        try {
            List<Reservation> reserved = reservationService.getReservationsByUser(userId);
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().println(reserved);
        } catch (Exception e) {
            logger.error(e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void undoReservation(String userId, String reservationId, HttpServletRequest req, HttpServletResponse resp) {
        try {
            boolean res = reservationService.removeReservation(userId, reservationId);

            if (res) {
                resp.setStatus(HttpServletResponse.SC_OK);
            } else {
                logger.error("Reservation cancel operation didn't happen. UserId: " + userId + ", reservationId: " + reservationId);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        } catch (ReservationNotFoundException e) {
            logger.info(e);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            logger.error(e);
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void reserveSpot(HttpServletRequest req, HttpServletResponse resp) {
        String userId = req.getParameter("userId");
        String spotIdString = req.getParameter("spotId");
        String startTimeString = req.getParameter("startTime");

        if (notBlank(spotIdString, startTimeString, userId)) {
            try {
                LocalDateTime startTime = parseTime(startTimeString);

                String reservationId = reservationService.reserveSpot(userId, spotIdString, startTime);

                resp.getWriter().print("ReservationId: " + reservationId);
                resp.setStatus(HttpServletResponse.SC_OK);

            } catch (ReservationException e) {
                logger.error(e);
                resp.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
            } catch (Exception e) {
                logger.error(e);
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private LocalDateTime parseTime(String start_time) {
        return LocalDateTime.parse(start_time, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
    }
}
