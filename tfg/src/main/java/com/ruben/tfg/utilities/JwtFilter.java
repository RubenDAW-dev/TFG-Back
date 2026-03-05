package com.ruben.tfg.utilities;

import java.io.IOException;

import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ruben.tfg.services.JwtService;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;

	@Override
	protected void doFilterInternal(HttpServletRequest req, HttpServletResponse res, FilterChain chain)
			throws ServletException, IOException {

		String header = req.getHeader("Authorization");
		if (header == null || !header.startsWith("Bearer ")) {
			chain.doFilter(req, res);
			return;
		}

		String token = header.substring(7);

		try {
			Claims claims = jwtService.parseToken(token);
			req.setAttribute("userId", claims.get("id"));
			req.setAttribute("rol", claims.get("rol"));
		} catch (Exception e) {
			res.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido");
			return;
		}

		chain.doFilter(req, res);
	}
}