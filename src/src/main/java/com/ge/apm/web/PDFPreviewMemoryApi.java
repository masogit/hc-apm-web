package com.ge.apm.web;

import com.ge.apm.domain.dto.FileInfoResult;
import com.ge.apm.view.preview.DocumentInMemoryUtil;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayInputStream;

@RestController
@RequestMapping("/preview")
public class PDFPreviewMemoryApi {

    @RequestMapping(path = "/{key}", method = RequestMethod.GET)
    public ResponseEntity<?> getPDFPreviewMemory(@PathVariable("key")String key) {
        FileInfoResult fileInfoResult = DocumentInMemoryUtil.remove(key);

        return ResponseEntity.ok().header("Cache-Control", "max-age=315360000")
                .contentLength(fileInfoResult.getObjectSize())
                .contentType(MediaType.parseMediaType(fileInfoResult.getObjectType().concat(";charset=UTF-8")))
                .body(new InputStreamResource(new ByteArrayInputStream(fileInfoResult.getBytes())));
    }

}
