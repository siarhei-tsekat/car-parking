package net.tsekot.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.tsekot.persistence.dao.reservation.ReservationException;
import net.tsekot.persistence.entity.Reservation;
import net.tsekot.service.ReservationNotFoundException;
import net.tsekot.service.ReservationService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ReservationController extends HttpServlet {

    private final static Logger logger = Logger.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    public ReservationController() {
        this.reservationService = new ReservationService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("userId");
        if (userId != null && !userId.isBlank()) {
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

        if (userId != null && !userId.isBlank() && reservationId != null && !reservationId.isBlank()) {
            cancelSpot(userId, reservationId, req, resp);
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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

    private void cancelSpot(String userId, String reservationId, HttpServletRequest req, HttpServletResponse resp) {
        try {
            boolean res = reservationService.cancelReservation(userId, reservationId);

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
        String endTimeString = req.getParameter("endTime");

        if (spotIdString != null && startTimeString != null && endTimeString != null && userId != null) {
            try {
                LocalDateTime startTime = parseTime(startTimeString);
                LocalDateTime endTime = parseTime(endTimeString);

                String reservationId = reservationService.reserveSpot(userId, spotIdString, startTime, endTime);

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
