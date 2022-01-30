package net.tsekot.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.tsekot.persistence.entity.Spot;
import net.tsekot.service.SpotService;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.List;

public class SpotController extends HttpServlet {

    private final static Logger logger = Logger.getLogger(SpotController.class);
    private final SpotService spotService;

    public SpotController() {
        this.spotService = new SpotService();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
            List<Spot> allAvailableSpots = spotService.getAllAvailableSpots();

            resp.getWriter().print(allAvailableSpots);
        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            logger.error(e);
        }
    }
}
