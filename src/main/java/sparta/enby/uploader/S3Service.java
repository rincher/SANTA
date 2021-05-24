package sparta.enby.uploader;

import com.amazonaws.services.s3.model.ObjectMetadata;

import java.io.InputStream;

public interface S3Service {
    void uploadFile(InputStream inputStream, ObjectMetadata objectMetadata, String filename);

    String getFileUrl(String fileName);

    void removeFile(String fileName);
}
