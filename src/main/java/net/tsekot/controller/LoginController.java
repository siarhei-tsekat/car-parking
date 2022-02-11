package net.tsekot.controller;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.impl.TextCodec;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.tsekot.config.ApplicationProperties;
import net.tsekot.persistence.dao.user.UserNotFoundException;
import net.tsekot.persistence.entity.User;
import net.tsekot.service.UserService;
import net.tsekot.util.ObjectUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

public class LoginController extends HttpServlet {

    private final static Logger logger = Logger.getLogger(LoginController.class);
    public static final String secret = ApplicationProperties.instance().getSecret();

    private UserService userService = new UserService();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String username = req.getParameter("username");
        String password = req.getParameter("password");

        if (ObjectUtils.notBlank(username, password)) {
            try {
                User dbUser = userService.getUserByUserName(username);
                if (password.equals(dbUser.getPassword())) {
                    String token = generateToken(dbUser.getUserName());
                    resp.addHeader("Token", token);
                    resp.setStatus(HttpServletResponse.SC_OK);
                } else {
                    logger.debug("User wasn't recognized");
                }
            } catch (UserNotFoundException e) {
                logger.info("User with name: " + username + " wasn't found");
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            }
        } else {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String generateToken(String issuer) {
        int hours = ApplicationProperties.instance().getTokenExpirationHours();
        try {
            return Jwts.builder()
                    .setIssuer(issuer)
                    .setIssuedAt(Date.from(Instant.now()))
                    .setExpiration(Date.from(Instant.now().plus(hours, ChronoUnit.HOURS)))
                    .signWith(SignatureAlgorithm.HS256, TextCodec.BASE64.decode(secret))
                    .compact();
        } catch (Exception e) {
            logger.error("Jwt generating exception: " + e.getMessage());
            throw e;
        }
    }
}
