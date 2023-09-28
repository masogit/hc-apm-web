package com.ge.apm.service.utils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TempFileUtil {

    private byte[] contents;

    private String contentType;

    private String fileName;

    public StreamedContent getStreamedContent() {
        return new DefaultStreamedContent(new ByteArrayInputStream(contents), contentType, fileName);
    }

    public InputStream getInputStream() {
        return new ByteArrayInputStream(contents);
    }
}
