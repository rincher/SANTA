package sparta.enby.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReviewResponseDto {
    private Long review_id;
    private String title;
    private String review_imgUrl;
    private String contents;
    private Long board_id;
    private LocalDateTime createdAt;
    private String nickname;
    private String profile_Img;

    public ReviewResponseDto(Long review_id, String title, String review_imgUrl, String contents, Long board_id, String nickname, String profile_Img) {
        this.review_id = review_id;
        this.title = title;
        this.review_imgUrl = review_imgUrl;
        this.contents = contents;
        this.board_id = board_id;
        this.nickname = nickname;
        this.profile_Img = profile_Img;

    }

    public ReviewResponseDto(Long review_id, String title, String review_imgUrl, String contents, Long board_id, LocalDateTime createdAt, String nickname, String profile_img) {
        this.review_id = review_id;
        this.title = title;
        this.review_imgUrl = review_imgUrl;
        this.contents = contents;
        this.board_id = board_id;
        this.createdAt = createdAt;
        this.nickname = nickname;
        this.profile_Img = profile_img;
    }

    public ReviewResponseDto(Long review_id, String title, String review_imgUrl, String contents, Long board_id) {
        this.review_id = review_id;
        this.title = title;
        this.review_imgUrl = review_imgUrl;
        this.contents = contents;
        this.board_id = board_id;
    }
}
