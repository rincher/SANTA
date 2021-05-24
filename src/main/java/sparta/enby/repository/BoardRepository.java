package sparta.enby.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import sparta.enby.model.Board;
import sparta.enby.model.Registration;
import sparta.enby.model.Review;

import java.util.List;
import java.util.Optional;

public interface BoardRepository extends JpaRepository<Board, Long> {
    List<Board> findAllById(Long board_id);
    List<Board> findAllByCreatedBy(String name);
    Optional<Board> findByReviews(Review review);
    List<Board> findAllByTitleContaining(String keyword, Sort meetTime);
    List<Board> findAllByContentsContaining(String keyword, Sort meetTime);
    List<Board> findAllByRegistrations(Registration registration);
}
