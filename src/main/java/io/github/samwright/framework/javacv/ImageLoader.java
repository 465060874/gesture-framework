package io.github.samwright.framework.javacv;

import com.googlecode.javacpp.Loader;
import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_objdetect;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.datatypes.StartType;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import lombok.Getter;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;

/**
 * User: Sam Wright Date: 05/09/2013 Time: 17:23
 */
public class ImageLoader extends AbstractElement {

    static {
        Loader.load(opencv_objdetect.class);
    }


    @Getter private String directory;
    @Getter private TaggedImage activeImage;
    private List<TaggedImage> images;


    public ImageLoader() {
        super(new TypeData(StartType.class, TaggedImage.class));
        images = new ArrayList<>();
        directory = "/Users/eatmuchpie/Documents/imageDir/";
        reloadImages();
    }

    public ImageLoader(ImageLoader oldElement) {
        super(oldElement);
        images = new ArrayList<>(oldElement.getImages());
        activeImage = oldElement.getActiveImage();
        directory = oldElement.getDirectory();
        reloadImages();
    }

    public List<TaggedImage> getImages() {
        return Collections.unmodifiableList(images);
    }

    public void setActiveImage(TaggedImage image) {
        if (image != null && !images.contains(image))
            throw new RuntimeException("Can only select a loaded image");
        this.activeImage = image;
    }

    @Override
    public Mediator process(Mediator input) {
        if (activeImage == null)
            takeSnapshot("Snapshot");

        if (activeImage == null)
            throw new RuntimeException("Null active image!");

        return Mediator.createEmpty().createNext(this, activeImage);
    }

    @Override
    public List<Mediator> processTrainingData(Mediator input) {
        List<Mediator> outputs = new ArrayList<>();
        for (TaggedImage image : images)
            outputs.add(input.createNext(this, image));

        return outputs;
    }

    @Override
    public ImageLoader createMutableClone() {
        return new ImageLoader(this);
    }

    @Override
    public boolean isValid() {
        return true;
    }

    public ImageLoader withDirectory(String directory) {
        if (isMutable()) {
            this.directory = directory;
            reloadImages();
            return this;
        } else {
            return createMutableClone().withDirectory(directory);
        }
    }

    public void reloadImages() {
        if (!isDirectoryValid()) {
            images.clear();
            return;
        }

        File pwd = new File(directory);
        File[] jpgs = pwd.listFiles(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir.getAbsolutePath() + "/" + name).isFile()
                        && name.endsWith(".jpg");
            }
        });

        Map<String, TaggedImage> alreadyLoaded = new HashMap<>();
        for (TaggedImage image : images)
            alreadyLoaded.put(image.getTag(), image);

        for (File jpg : jpgs) {
            if (!alreadyLoaded.containsKey(jpg.getName())) {
                TaggedImage image = TaggedImage.loadFromFilename(jpg.getAbsolutePath());
                images.add(image);
            } else {
                alreadyLoaded.remove(jpg.getName());
            }
        }

        images.removeAll(alreadyLoaded.values());
        if (activeImage != null && !images.contains(activeImage))
            activeImage = null;

        Collections.sort(images);
    }

    public void takeSnapshot(String tag) {
        if (tag == null || tag.isEmpty())
            tag = "Snapshot";

        tag = tag + ".jpg";
        TaggedImage image;
        FrameGrabber grabber = null;

        try {
            grabber = FrameGrabber.createDefault(-1);
            grabber.start();
            image = new TaggedImage(grabber.grab(), tag);
        } catch (FrameGrabber.Exception e) {
            throw new RuntimeException(e);
        } finally {
            try {
                if (grabber != null) {
                    grabber.stop();
                    grabber.release();
                }
            } catch (FrameGrabber.Exception e) {
                e.printStackTrace();
            }
        }

        if (isDirectoryValid()) {
            cvSaveImage(directory + tag, image.getImage());
            reloadImages();
            activeImage = null;
            for (TaggedImage loadedImage : images) {
                if (loadedImage.getTag().equals(tag)) {
                    activeImage = loadedImage;
                    break;
                }
            }
        } else {
            activeImage = image;
        }
    }

    private boolean isDirectoryValid() {
        return directory != null && new File(directory).isDirectory();
    }
}
