package sparta.enby.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class BoardResponseDto {
    private Long id;
    private String title;
    private String board_imgUrl;
    private int people_current;
    private int people_max;
    private String contents;
    private String location;
    private LocalDateTime meetTime;
    private Boolean deadlineStatus;

    public BoardResponseDto(Long id, String title, String board_imgUrl, int people_current, int people_max, String contents, String location, LocalDateTime meetTime, Boolean deadlineStatus) {
        this.id = id;
        this.title = title;
        this.board_imgUrl = board_imgUrl;
        this.people_current = people_current;
        this.people_max = people_max;
        this.contents = contents;
        this.location = location;
        this.meetTime = meetTime;
        this.deadlineStatus = deadlineStatus;
    }

    public BoardResponseDto(Long id, String board_imgUrl, String contents, String title, String location, LocalDateTime meetTime, Boolean deadlineStatus) {
        this.id = id;
        this.board_imgUrl = board_imgUrl;
        this.contents = contents;
        this.title = title;
        this.location = location;
        this.meetTime = meetTime;
        this.deadlineStatus = deadlineStatus;
    }
}
