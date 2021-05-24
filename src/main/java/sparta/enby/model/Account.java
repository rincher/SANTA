package sparta.enby.model;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@ToString(of = {"id","nickname", "password", "profile_img"})
public class Account extends BaseTimeEntity{

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "account_id")
    private Long id;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String profile_img;

    @ElementCollection(fetch = FetchType.EAGER)
    @Builder.Default
    private List<String> roles = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    @Builder.Default
    List<Board>boards = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    @Builder.Default
    List<Review>reviews = new ArrayList<>();

    @OneToMany(mappedBy = "account")
    @Builder.Default
    List<Registration>registrations = new ArrayList<>();

    public void update(String nickname, String profile_img) {
        this.nickname = nickname;
        this.profile_img = profile_img;
    }

}
