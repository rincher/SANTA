package sparta.enby.model;

import lombok.*;
import sparta.enby.dto.RegisterRequestDto;

import javax.persistence.*;

@Builder
@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@ToString(of = {"id", "accepted", "contents"})
public class Registration extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "regisitration_id")
    private Long id;

    private boolean accepted;

    private String contents;

    private String kakao_id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "account_id")
    private Account account;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "board_id")
    private Board board;

    public void addBoardAndAccount(Board board, Account account){
        this.board = board;
        this.account = account;
        board.getRegistrations().add(this);
        account.getRegistrations().add(this);
    }
    public void update(RegisterRequestDto registerRequestDto){
        this.accepted = registerRequestDto.isAccepted();
    }
}
