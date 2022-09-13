package com.lets.util;

import static org.assertj.core.api.Assertions.*;

import java.io.File;
import java.io.FileInputStream;
import java.util.Base64;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class FileUtilTest {
  private final FileUtil fileUtil = new FileUtil();

  @Test
  @DisplayName("decodeFile메서드는 파일을 디코딩한다")
  public void decodeFile() {
    //given

    //when
    File file = null;
    try {

      FileInputStream fis = new FileInputStream(new File("src/test/java/com/lets/tea.jpg"));

      file = fileUtil.decodeFile(Base64
                                     .getEncoder()
                                     .encodeToString(fis.readAllBytes()));
    } catch (Exception e) {
      throw new RuntimeException();
    }
    //then
    assertThat(file).isNotNull();
  }
}
