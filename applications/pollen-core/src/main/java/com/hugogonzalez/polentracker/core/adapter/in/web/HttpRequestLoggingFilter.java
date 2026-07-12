package com.hugogonzalez.polentracker.core.adapter.in.web;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.UUID;
import org.slf4j.*;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class HttpRequestLoggingFilter extends OncePerRequestFilter {
  public static final String CORRELATION_HEADER = "X-Correlation-Id";
  private static final Logger log = LoggerFactory.getLogger(HttpRequestLoggingFilter.class);

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    var supplied = request.getHeader(CORRELATION_HEADER);
    var correlationId =
        supplied == null || supplied.isBlank() || supplied.length() > 128
            ? UUID.randomUUID().toString()
            : supplied;
    var started = System.nanoTime();
    response.setHeader(CORRELATION_HEADER, correlationId);
    try (var ignored = MDC.putCloseable("correlationId", correlationId)) {
      log.info("HTTP request started method={} path={}", request.getMethod(), request.getRequestURI());
      var outcome = "success";
      try {
        chain.doFilter(request, response);
      } catch (ServletException | IOException | RuntimeException e) {
        outcome = "error";
        log.error("HTTP request failed method={} path={}", request.getMethod(), request.getRequestURI(), e);
        throw e;
      } finally {
        var durationMs = (System.nanoTime() - started) / 1_000_000;
        log.info(
            "HTTP request completed method={} path={} status={} outcome={} duration_ms={}",
            request.getMethod(),
            request.getRequestURI(),
            response.getStatus(),
            outcome,
            durationMs);
      }
    }
  }
}
