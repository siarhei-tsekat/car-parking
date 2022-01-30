package net.tsekot.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.tsekot.persistence.C3PODataSource;
import net.tsekot.persistence.TransactionManager;
import net.tsekot.persistence.TransactionManagerImpl;
import net.tsekot.persistence.dao.reservation.ReservationException;
import net.tsekot.persistence.dao.spot.SpotException;
import net.tsekot.persistence.entity.Spot;
import net.tsekot.service.ReservationService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReservationController extends HttpServlet {

    private final static Logger logger = Logger.getLogger(ReservationController.class);
    private final ReservationService reservationService;

    public ReservationController() {
        this.reservationService = new ReservationService();
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String userId = req.getParameter("userId");
        String spotIdString = req.getParameter("spotId");
        String startTimeString = req.getParameter("startTime");
        String endTimeString = req.getParameter("endTime");

        if (spotIdString != null && startTimeString != null && endTimeString != null && userId != null) {
            try {
                Integer spotId = parseSpotId(spotIdString);
                LocalDateTime startTime = parseTime(startTimeString);
                LocalDateTime endTime = parseTime(endTimeString);

                String reservationId = reservationService.reserveSpot(userId, spotId, startTime, endTime);

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

    private Integer parseSpotId(String slot_id) {
        return Integer.parseInt(slot_id);
    }
}
