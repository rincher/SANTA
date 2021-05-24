package sparta.enby.dto;

import lombok.Data;

@Data
public class AccountResponseDto {
    private String nickname;
    private String profile_imgUrl;

    public AccountResponseDto(String nickname){
        this.nickname = nickname;
    }

    public AccountResponseDto(String nickname, String profile_imgUrl){
        this.nickname = nickname;
        this.profile_imgUrl = profile_imgUrl;
    }
}
