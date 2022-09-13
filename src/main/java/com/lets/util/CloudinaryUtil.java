package com.lets.util;

import static com.lets.exception.ErrorCode.*;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.cloudinary.Cloudinary;
import com.lets.exception.CustomException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Component
public class CloudinaryUtil {
  @Value("${cloud_name}")
  private String cloud_name;

  private final String url = "https://res.cloudinary.com/";
  private final Cloudinary cloudinary;

  /**
   * 기본 프로필 이미지 저장
   * 이렇게 해도 프로젝트 재시작 할 때마다 같은 이미지가 여러 번 반복 저장될 일이 없음.
   * cloudinary에서는 기본적으로 동일한 publicId일 경우 이미지 덮어쓰기가 됨.
   * 참고: https://cloudinary.com/documentation/image_upload_api_reference#upload
   */
  @PostConstruct
  private void init() {
    File file = new File("src/main/resources/default.png");

    HashMap<String, String> map = new HashMap<>();
    map.put("public_id", "default");

    try {
      Map resultMap = cloudinary
          .uploader()
          .upload(file, map);
    } catch (Exception e) {
      throw new CustomException(CLOUDINARY_ERROR);
    }

  }

  /**
   * 파일 저장
   * 파일 저장 후에 사용자 정보 변경
   */
  public String saveFile(File file) {

    String publicId = "default";
    try {
      Map resultMap = cloudinary
          .uploader()
          .upload(file, new HashMap<>());
      publicId = (String)resultMap.get("public_id");
    } catch (Exception e) {
      throw new CustomException(CLOUDINARY_ERROR);
    }
    return publicId;
  }

  /**
   * 파일 접근 url 반환
   */
  public String findFileURL(String publicId) {
    String fileUrl = null;
    if (publicId != null) {
      fileUrl = url + cloud_name + "/image/upload/" + publicId;
    }
    return fileUrl;
  }

  /**
   * 파일 삭제
   */
  public void deleteFile(String publicId) {
    /**
     To bypass the CDN caching,
     you can include the invalidate parameter in your POST request in order to also invalidate the cached copies of the asset on the CDN.
     It usually takes between a few seconds and a few minutes for the invalidation to fully propagate through the CDN.
     */
    Map<String, String> config = new HashMap<>();
    config.put("invalidate", "true");
    try {
      Map map = cloudinary
          .uploader()
          .destroy(publicId, config);
      String result = (String)map.get("result");
      if (!result.equals("ok")) {
        throw new CustomException(CLOUDINARY_ERROR);
      }

    } catch (Exception e) {
      throw new CustomException(CLOUDINARY_ERROR);
    }

  }
}
