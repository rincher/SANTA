package sparta.enby.model;

import com.sun.istack.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;
import sparta.enby.dto.ChangeDeadlineRequestDto;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Builder
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(of = {"id", "title", "contents", "location", "board_imgUrl"})
public class Board extends BaseEntity {
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "board_id")
    private Long id;

    private String title;

    @Column(columnDefinition = "TEXT")
    private String contents;

    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime meetTime;

    private String location;

    private String board_imgUrl;

    private int people_current;

    private int people_max;

    private Boolean deadlineStatus;

    @ManyToOne
    @JoinColumn(name = "account_id")
    private Account account;

    @OneToMany(mappedBy = "board")
    @Builder.Default
    List<Review> reviews = new ArrayList<>();

    @OneToMany(mappedBy = "board")
    @Builder.Default
    List<Registration> registrations = new ArrayList<>();

    public void addAccount(Account account) {
        this.account = account;
    }

    public void update(String board_imgUrl, String title, String contents, LocalDateTime meetTime, String location, int people_max, boolean deadlineStatus) {
        this.board_imgUrl = board_imgUrl;
        this.title = title;
        this.contents = contents;
        this.meetTime = meetTime;
        this.location = location;
        this.people_max = people_max;
        this.deadlineStatus = deadlineStatus;
    }
    public void deleteBoard(Board board) {
        board.getAccount().getBoards().remove(this);
        board.getReviews().removeAll(this.reviews);
        board.getRegistrations().removeAll(this.registrations);
        this.account = null;
        this.reviews.clear();
        this.registrations.clear();
    }

    public void changeDeadlineStatus(Boolean b){
        this.deadlineStatus = b;
    }
}
