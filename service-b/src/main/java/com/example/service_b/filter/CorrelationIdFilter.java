package com.example.service_b.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class CorrelationIdFilter extends OncePerRequestFilter {

    private static final String CORRELATION_ID_HEADER = "X-Correlation-ID";
    private final static String CORRELATION_ID_MDC_KEY = "correlationId";

    public void setCorrelationId(String correlationId){
        MDC.put(CORRELATION_ID_MDC_KEY, correlationId);
    }

    public void clearCorrelationId(){
        MDC.remove(CORRELATION_ID_MDC_KEY);
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
        throws ServletException, IOException{

        String correlationId = request.getHeader(CORRELATION_ID_HEADER);

        try{
            if(correlationId != null && !correlationId.isBlank()){
                setCorrelationId(correlationId);
            }

            filterChain.doFilter(request, response);
        } finally {
            clearCorrelationId();
        }
    }
}
