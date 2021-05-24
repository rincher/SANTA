package sparta.enby.dto;

import lombok.Data;

@Data
public class RegistrationResponseDto {
    private Long register_id;
    private boolean accepted;
    private String contents;
    private String nickname;
    private String profile_img;
    private String kakao_id;

    public RegistrationResponseDto(Long id, boolean accepted, String contents,String nickname, String profile_img, String kakao_id){
        this.register_id = id;
        this.accepted = accepted;
        this.contents = contents;
        this.nickname = nickname;
        this.profile_img = profile_img;
        this.kakao_id = kakao_id;
    }
}
