package sparta.enby.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import sparta.enby.model.Board;
import sparta.enby.model.Review;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {
    void deleteAllByBoard(Board board);
    List<Review> findAllById(Long review_id);
    List<Review> findAllByBoard(Board board);
    Boolean existsByBoard(Board board);
    List<Review> findAllByTitleContaining(String keyword);
    List<Review> findAllByContentsContaining(String keyword);
    List<Review> findAllByBoardAndCreatedBy(Board board, String name);

}
