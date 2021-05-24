package sparta.enby.dto;

import lombok.Data;
import sparta.enby.model.Account;

import java.time.LocalDateTime;

@Data
public class MyBoardResponseDto {
    private Long id;
    private String title;
    private String board_name;
    private LocalDateTime meetTime;
    private LocalDateTime createdAt;
    private String nickname;
    private String profile_imgUrl;


    public MyBoardResponseDto(Long id, String title, LocalDateTime meetTime, LocalDateTime createdAt, String nickname, String profile_imgUrl) {
        this.id = id;
        this.title = title;
        this.board_name = "작성한 글";
        this.meetTime = meetTime;
        this.createdAt = createdAt;
        this.nickname = nickname;
        this.profile_imgUrl = profile_imgUrl;
    }
}
