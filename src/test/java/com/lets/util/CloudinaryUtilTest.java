package com.lets.util;

import com.cloudinary.Cloudinary;
import org.junit.BeforeClass;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class CloudinaryUtilTest {
    @Autowired
    Cloudinary cloudinary;

    @Autowired
    CloudinaryUtil cloudinaryUtil;

    static File file;

    @BeforeAll
    public static void setup(){
        file = new File("./src/test/java/com/lets/tea.jpg");

    }

    @Test
    public void save() throws Exception
    {
        //given
        //when
        String publicId = cloudinaryUtil.saveFile(file);

        //then
        assertThat(publicId).isNotBlank();
        assertThat(publicId).isNotEqualTo("default");

    }

    @Test
    public void findFileURL() throws Exception
    {
        //given
        String publicId = cloudinaryUtil.saveFile(file);

        //when
        String fileURL = cloudinaryUtil.findFileURL(publicId);

        //then
        assertThat(fileURL).isNotBlank();

    }

    @Test
    public void deleteFile() throws Exception
    {

        //given
        String publicId = cloudinaryUtil.saveFile(file);

        //when
        cloudinaryUtil.deleteFile(publicId);

        //then
        Map map = cloudinary.uploader().destroy(publicId, new HashMap<>());
        assertThat(map.get("result")).isNotEqualTo("ok");

    }
}
