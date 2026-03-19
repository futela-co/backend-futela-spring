package com.futela.api.domain.port.out.common;

import java.io.InputStream;

public interface FileStoragePort {
    String upload(InputStream inputStream, String folder, String filename, String contentType);
    void delete(String publicId);
}
