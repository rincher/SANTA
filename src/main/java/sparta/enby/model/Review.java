package sparta.enby.model;

import lombok.*;

import javax.persistence.*;

@Getter
@Setter
@Entity
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(of = {"id", "review_imgUrl", "contents"})

public class Review extends BaseEntity{
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "review_id")
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String review_imgUrl;

    @Column(nullable = false)
    private String contents;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public void addBoardAndAccount(Board board, Account account){
        this.board = board;
        this.account = account;
        board.getReviews().add(this);
        account.getReviews().add(this);
    }
    public void removeBoardAndAccount(Board board, Account account){
        board.getReviews().remove(this);
        account.getReviews().remove(this);
        this.board = null;
        this.account = null;
    }
    public void editReview(String title, String contents, String review_imgUrl){
        this.title = title;
        this.contents = contents;
        this.review_imgUrl = review_imgUrl;
    }
}
