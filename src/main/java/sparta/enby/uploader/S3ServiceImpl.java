package sparta.enby.uploader;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.DeleteObjectRequest;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@RequiredArgsConstructor
@Component
public class S3ServiceImpl implements S3Service{

    private final AmazonS3Client amazonS3Client;

    private static final String BUCKET = "hanghae99-gitlog";

    @Override
    public void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String fileName){
        amazonS3Client.putObject(new PutObjectRequest(BUCKET, fileName, inputStream, objectMetadata).withCannedAcl(CannedAccessControlList.PublicRead));

    }

    @Override
    public String getFileUrl(String fileName){
        return String.valueOf(amazonS3Client.getUrl(BUCKET, fileName));
    }

    @Override
    public void removeFile(String fileName){
        amazonS3Client.deleteObject(new DeleteObjectRequest(BUCKET, fileName));
    }

}
