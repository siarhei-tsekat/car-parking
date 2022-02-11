package net.tsekot.filter;

import io.jsonwebtoken.*;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.tsekot.config.ApplicationProperties;
import net.tsekot.controller.LoginController;
import net.tsekot.util.ObjectUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.time.Instant;
import java.util.Date;

public class JWTAuthenticationFilter extends HttpFilter {

    private final static Logger logger = Logger.getLogger(LoginController.class);

    public static final String secret = ApplicationProperties.instance().getSecret();

    @Override
    protected void doFilter(HttpServletRequest req, HttpServletResponse res, FilterChain chain) throws IOException, ServletException {

        String path = req.getRequestURI();
        if (path.startsWith("/parking/login")) {
            chain.doFilter(req, res);
        } else {

            String authorization = req.getHeader("Authorization");

            if (ObjectUtils.notBlank(authorization) && authorization.startsWith("Bearer ")) {
                String jwtToken = authorization.substring(7);
                if (!tokenExpired(jwtToken)) {
                    chain.doFilter(req, res);
                } else {
                    res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
                }
            } else {
                res.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
        }

    }

    private boolean tokenExpired(String jwtToken) {

        try {
            Claims body = Jwts.parser().setSigningKey(secret).parseClaimsJws(jwtToken).getBody();
            Date expiration = body.getExpiration();
            return expiration.before(Date.from(Instant.now()));
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException | IllegalArgumentException e) {
            logger.error(e.getMessage());
        }
        return true;
    }
}
