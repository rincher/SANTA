package sparta.enby.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
public class ReviewRequestDto {

    private MultipartFile reviewImg;
    private String title;
    private String contents;
}
