package org.aquasensus.identity.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.aquasensus.identity.application.IdentiteJeton;
import org.aquasensus.identity.application.LecteurJetons;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import io.jsonwebtoken.JwtException;

@Component
public class FiltreJwt extends OncePerRequestFilter {

    private final LecteurJetons jetons;

    public FiltreJwt(LecteurJetons jetons) {
        this.jetons = jetons;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String uri = request.getRequestURI();
        return uri != null && uri.startsWith("/internal/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String entete = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (entete == null || !entete.startsWith("Bearer ")) {
            var existant = SecurityContextHolder.getContext().getAuthentication();
            if (existant instanceof UsernamePasswordAuthenticationToken) {
                SecurityContextHolder.clearContext();
            }
            filterChain.doFilter(request, response);
            return;
        }
        try {
            IdentiteJeton identite = jetons.lireAcces(entete.substring(7));
            UtilisateurCourant courant =
                    new UtilisateurCourant(identite.id(), identite.identifiant(), identite.roles());
            UsernamePasswordAuthenticationToken auth =
                    new UsernamePasswordAuthenticationToken(courant, null, courant.autorites());
            SecurityContextHolder.getContext().setAuthentication(auth);
        } catch (JwtException | IllegalArgumentException | NullPointerException ignore) {
            SecurityContextHolder.clearContext();
        }
        filterChain.doFilter(request, response);
    }
}
