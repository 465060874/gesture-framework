package io.github.samwright.framework.javacv.helper;

import com.googlecode.javacv.cpp.opencv_core;
import lombok.Getter;

import java.io.File;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

/**
 * User: Sam Wright Date: 12/09/2013 Time: 18:59
 */
public class LoadedImage extends TaggedImage {
    @Getter private final String filename;

    public LoadedImage(opencv_core.IplImage image, String tag, String filename) {
        super(image, tag);
        this.filename = filename;
    }

    public static LoadedImage loadFromFilename(String filename) {
        File imageFile = new File(filename);
        if (!imageFile.isFile())
            throw new RuntimeException("Not a file");

        String name = imageFile.getName();
        int tagEndIndex = name.indexOf(' ');
        if (tagEndIndex == -1)
            tagEndIndex = name.indexOf('.');
        if (tagEndIndex == -1)
            throw new RuntimeException("Filename must have a space or a dot.");

        String tag = name.substring(0, tagEndIndex);

        opencv_core.IplImage image = cvLoadImage(filename);

        return new LoadedImage(image, tag, filename);
    }
}
