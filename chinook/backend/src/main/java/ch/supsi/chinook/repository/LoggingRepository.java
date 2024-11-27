package ch.supsi.chinook.repository;

import ch.supsi.chinook.model.Log;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoggingRepository extends JpaRepository<Log, Long> {
}
