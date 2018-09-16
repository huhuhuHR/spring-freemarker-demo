
package com.huo.service;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

public class CommonsMultipartSuningResolver extends CommonsMultipartResolver {

    private Long elseMaxUploadSize;
    private Long maxUploadDocSize;
    private Long maxUploadSize;

    public Long getElseMaxUploadSize() {
        return elseMaxUploadSize;
    }

    public void setElseMaxUploadSize(Long elseMaxUploadSize) {
        this.elseMaxUploadSize = elseMaxUploadSize;
    }

    public Long getMaxUploadDocSize() {
        return maxUploadDocSize;
    }

    public void setMaxUploadDocSize(Long maxUploadDocSize) {
        this.maxUploadDocSize = maxUploadDocSize;
    }

    public Long getMaxUploadSize() {
        return maxUploadSize;
    }

    public void setMaxUploadSize(Long maxUploadSize) {
        this.maxUploadSize = maxUploadSize;
    }

    @Override
    protected MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {

        String encoding = determineEncoding(request);
        FileUpload fileUpload = prepareFileUpload(encoding);
        long size = 0;
        String fileType = "";
        boolean ists = false;// 是否模板展示上传
        try {
            List<FileItem> fileItems = ((ServletFileUpload) fileUpload).parseRequest(request);
            for (FileItem item : fileItems) {
                String fileName = item.getName();
                if (fileName != null) {
                    fileType = fileName.substring(fileName.lastIndexOf('.') + 1, fileName.length());
                    size = item.getSize();
                } else if ("tempshowtype".equals(item.getFieldName())) {
                    ists = true;
                }
            }
            // 文件上传类型
            String[] Type = { "docx", "doc", "pdf", "rar", "ppt", "xlsx", "xls", "pptx" };
            // 判断上传文件的大小
            if (Arrays.asList(Type).contains(fileType) && size > maxUploadDocSize && !ists) {
                throw new MultipartException("上传文件大小不能大于" + maxUploadDocSize + "个字节");
            } else if ("zip".equals(fileType) && size > maxUploadSize && !ists) {
                throw new MultipartException("上传文件大小不能大于" + maxUploadSize + "个字节");
            } else if (!Arrays.asList(Type).contains(fileType) && !"zip".equals(fileType) && size > elseMaxUploadSize
                    && !ists) {
                throw new MultipartException("上传文件大小不能大于" + elseMaxUploadSize + "个字节");
            }
            return parseFileItems(fileItems, encoding);
        } catch (FileUploadBase.SizeLimitExceededException ex) {
            throw new MultipartException("上传文件大小不能大于" + fileUpload.getSizeMax() + "个字节");
        } catch (FileUploadException ex) {
            throw new MultipartException("Could not parse multipart servlet request", ex);
        }
    }
}
