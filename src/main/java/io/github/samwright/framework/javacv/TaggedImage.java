package io.github.samwright.framework.javacv;

import com.googlecode.javacv.cpp.opencv_core;
import lombok.Getter;
import lombok.Setter;

import java.io.File;

import static com.googlecode.javacv.cpp.opencv_highgui.cvLoadImage;

/**
 * User: Sam Wright Date: 06/09/2013 Time: 20:50
 */
public class TaggedImage implements Comparable<TaggedImage> {

    @Setter @Getter private String tag;
    @Getter private final opencv_core.IplImage image;

    public TaggedImage(opencv_core.IplImage image, String tag) {
        this.image = image;
        setTag(tag);
    }

    @Override
    public String toString() {
        return tag;
    }

    public static TaggedImage loadFromFilename(String filename) {
        File imageFile = new File(filename);
        if (!imageFile.isFile())
            throw new RuntimeException("Not a file");
        String tag = imageFile.getName();

        opencv_core.IplImage image = cvLoadImage(filename);

        return new TaggedImage(image, tag);
    }

    @Override
    public int compareTo(TaggedImage o) {
        return tag.compareTo(o.getTag());
    }
}
