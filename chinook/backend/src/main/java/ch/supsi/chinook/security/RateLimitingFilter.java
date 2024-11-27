package ch.supsi.chinook.security;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;


@Component
public class RateLimitingFilter implements Filter {
    private static final int MAX_REQUESTS = 1000; // Limite per IP
    private static final long TIME_WINDOW = TimeUnit.MINUTES.toMillis(1); // 1 minuto

    private final ConcurrentHashMap<String, AccessData> requestMap = new ConcurrentHashMap<>();

    @Override
    public void init(FilterConfig filterConfig) {}

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        String clientIp = request.getRemoteAddr();
        long currentTime = System.currentTimeMillis();

        if ("127.0.0.1".equals(clientIp) || "localhost".equals(clientIp)) {
            // Logga e salta la logica per localhost
            System.out.println("Accesso permesso per localhost: " + clientIp);
            chain.doFilter(request, response);
            return;
        }

        // Ottieni o inizializza i dati dell'IP
        requestMap.compute(clientIp, (ip, accessData) -> {
            if (accessData == null || currentTime - accessData.startTime > TIME_WINDOW) {
                // Reset dati se la finestra di tempo è scaduta
                return new AccessData(1, currentTime);
            } else {
                // Incrementa il conteggio delle richieste
                accessData.requests.incrementAndGet();
                return accessData;
            }
        });

        // Ottieni il conteggio delle richieste per l'IP
        AccessData accessData = requestMap.get(clientIp);

        if (accessData.requests.get() > MAX_REQUESTS) {
            response.getWriter().write("Rate limit exceeded. Try again later.");
            response.setContentType("text/plain");
            response.setCharacterEncoding("UTF-8");
            return;
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {}

    // Classe per memorizzare i dati di accesso
    private static class AccessData {
        final AtomicInteger requests;
        final long startTime;

        AccessData(int initialRequests, long startTime) {
            this.requests = new AtomicInteger(initialRequests);
            this.startTime = startTime;
        }
    }
}
