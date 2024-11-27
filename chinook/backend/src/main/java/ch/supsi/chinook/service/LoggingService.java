package ch.supsi.chinook.service;

import ch.supsi.chinook.model.Log;
import ch.supsi.chinook.repository.LoggingRepository;
import org.springframework.stereotype.Service;

@Service
public class LoggingService {
    private LoggingRepository loggingRepository;

    public LoggingService(LoggingRepository loggingRepository) {
        this.loggingRepository = loggingRepository;
    }

    public void logActivity(Log log){
        loggingRepository.save(log);
    }
}
