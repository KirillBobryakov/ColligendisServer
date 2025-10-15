package bkv.colligendis.security;

import bkv.colligendis.rest.exceptions.InvalidTokenException;
import bkv.colligendis.rest.exceptions.TokenExpiredException;
import bkv.colligendis.rest.exceptions.UnauthorizedException;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    public static final String AUTHORIZATION_HEADER = "Authorization";
    public static final String BEARER_PREFIX = "Bearer ";

    private final JwtTokenProvider jwtTokenProvider;

    @Autowired
    public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = getTokenFromRequest(request);

            if (StringUtils.hasText(token)) {
                try {
                    if (jwtTokenProvider.validateToken(token)) {
                        Authentication authentication = jwtTokenProvider.getAuthentication(token);
                        if (authentication == null) {
                            throw new UnauthorizedException(
                                    "User authentication failed - user not found or token mismatch");
                        }
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                        logger.debug("Set Authentication to security context for '{}'", authentication.getName());
                    } else {
                        throw new InvalidTokenException("Token validation failed");
                    }
                } catch (ExpiredJwtException ex) {
                    throw new TokenExpiredException("Token has expired");
                } catch (JwtException ex) {
                    throw new InvalidTokenException("Invalid token format: " + ex.getMessage());
                }
            }
        } catch (InvalidTokenException | TokenExpiredException | UnauthorizedException ex) {
            logger.warn("Authentication error: {}", ex.getMessage());
            response.setStatus(ex.getHttpStatus());
            response.setContentType("application/json");
            response.getWriter().write(String.format(
                    "{\"errorCode\":\"%s\",\"message\":\"%s\",\"httpStatus\":%d,\"path\":\"%s\"}",
                    ex.getErrorCode(), ex.getMessage(), ex.getHttpStatus(), request.getRequestURI()));
            response.getWriter().flush();
            return;
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
            // Не прерываем цепочку - возможно, запрос к публичному эндпоинту
        }

        filterChain.doFilter(request, response);
    }

    /**
     * Извлекает JWT токен из заголовка Authorization
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION_HEADER);
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith(BEARER_PREFIX)) {
            return bearerToken.substring(BEARER_PREFIX.length());
        }
        return null;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        // Skip filter for public endpoints
        String path = request.getRequestURI();
        return path.startsWith("/auth/signin") || path.startsWith("/auth/register") || path.startsWith("/auth/refresh")
                || path.startsWith("/public/");
    }
}
