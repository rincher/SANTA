package sparta.enby.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AttendedBoardDto {
    private Long id;
    private String board_name;
    private String title;
    private String board_imgUrl;
    private String location;
    private LocalDateTime meetTime;
    private LocalDateTime createdAt;
    private int people_current;
    private int people_max;

    public AttendedBoardDto(Long id, String title, String board_imgUrl, String location, LocalDateTime meetTime, LocalDateTime createdAt, int people_current, int people_max) {
        this.id = id;
        this.board_name = "참석한 모임";
        this.title = title;
        this.board_imgUrl = board_imgUrl;
        this.location = location;
        this.meetTime = meetTime;
        this.createdAt = createdAt;
        this.people_current = people_current;
        this.people_max = people_max;
    }
}
