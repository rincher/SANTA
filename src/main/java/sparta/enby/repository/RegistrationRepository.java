package sparta.enby.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.enby.model.Board;
import sparta.enby.model.Registration;

import java.util.List;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    void deleteAllByBoard(Board board);
    Registration findAllByBoardIdAndCreatedBy(Long board_id, String name);
    List<Registration> findAllByCreatedBy(String name);
    List<Registration>findAllByAcceptedTrueAndBoardDeadlineStatusAndCreatedBy(Boolean x, String name);
    List<Registration> findAllByAcceptedTrueAndCreatedBy(String name);
    boolean existsByBoardId(Long board_id);
}
