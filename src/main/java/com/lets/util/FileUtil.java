package com.lets.util;

import com.lets.exception.CustomException;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Base64;

import static com.lets.exception.ErrorCode.MULTIPARTFILE_TO_FILE_ERROR;


@Component
public class FileUtil {
    public File decodeFile(String profile){

        File file = null;

        if(profile != null && !profile.isEmpty()) {
            try {
                file = new File("profile");
                file.createNewFile();

                //BASE64를 일반 파일로 변환
                Base64.Decoder decoder = Base64.getDecoder();
                byte[] decodeBytes = decoder.decode(profile.getBytes());

                FileOutputStream fos = new FileOutputStream(file);
                fos.write(decodeBytes);
                fos.close();
                file = file;

            } catch (Exception e) {
                throw new CustomException(MULTIPARTFILE_TO_FILE_ERROR);
            }
        }


        return file;
    }
}
