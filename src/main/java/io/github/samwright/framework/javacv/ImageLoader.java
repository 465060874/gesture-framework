package io.github.samwright.framework.javacv;

import io.github.samwright.framework.javacv.helper.Camera;
import io.github.samwright.framework.javacv.helper.LoadedImage;
import io.github.samwright.framework.model.AbstractElement;
import io.github.samwright.framework.model.Element;
import io.github.samwright.framework.model.Processor;
import io.github.samwright.framework.model.datatypes.StartType;
import io.github.samwright.framework.model.helper.Mediator;
import io.github.samwright.framework.model.helper.TypeData;
import io.github.samwright.framework.model.helper.XMLHelper;
import lombok.Getter;
import lombok.Setter;
import org.w3c.dom.Document;

import java.io.File;
import java.io.FilenameFilter;
import java.util.*;

import static com.googlecode.javacv.cpp.opencv_core.IplImage;
import static com.googlecode.javacv.cpp.opencv_highgui.cvSaveImage;

/**
 * User: Sam Wright Date: 05/09/2013 Time: 17:23
 */
public class ImageLoader extends AbstractElement {

    @Getter private String directory;
    @Getter private LoadedImage activeImage;
    private List<LoadedImage> images;
    @Getter @Setter private String snapshotTag = "Snapshot";
    @Getter @Setter private boolean saveMode = false;
    private boolean activeImageNotSaved = false;


    public ImageLoader() {
        super(new TypeData(StartType.class, LoadedImage.class));
        images = new ArrayList<>();
        directory = "";
        reloadImages();
    }

    public ImageLoader(ImageLoader oldElement) {
        super(oldElement);
        images = new ArrayList<>(oldElement.getImages());
        activeImage = oldElement.getActiveImage();
        directory = oldElement.getDirectory();
        saveMode = oldElement.saveMode;
        snapshotTag = oldElement.snapshotTag;
        reloadImages();
    }

    public List<LoadedImage> getImages() {
        return Collections.unmodifiableList(images);
    }

    public void setActiveImage(LoadedImage image) {
        if (image != null && image != activeImage && !images.contains(image))
            throw new RuntimeException("Can only select a loaded image");
        if (activeImage != image)
            activeImageNotSaved = false;
        this.activeImage = image;
    }

    @Override
    public Mediator process(Mediator input) {
        if (activeImage == null)
            takeSnapshot();

        if (activeImage == null)
            throw new RuntimeException("Null active image!");

        return input.createNext(this, activeImage);
    }

    @Override
    public List<Mediator> processTrainingData(Mediator input) {
        if (images.isEmpty())
            throw new RuntimeException("No images are loaded to use for training.  Try setting " +
                    "the directory to somewhere with images.");

        List<Mediator> outputs = new ArrayList<>();
        for (LoadedImage image : images)
            outputs.add(input.createNext(this, image));
        return outputs;
    }

    @Override
    public ImageLoader createMutableClone() {
        return new ImageLoader(this);
    }

    @Override
    public boolean isValid() {
        return isDirectoryValid();
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

        Map<String, LoadedImage> alreadyLoaded = new HashMap<>();
        for (LoadedImage image : images)
            alreadyLoaded.put(image.getFilename(), image);

        for (File jpg : jpgs) {
            if (!alreadyLoaded.containsKey(jpg.getAbsolutePath())) {
                LoadedImage image = LoadedImage.loadFromFilename(jpg.getAbsolutePath());
                images.add(image);
            } else {
                alreadyLoaded.remove(jpg.getAbsolutePath());
            }
        }

        images.removeAll(alreadyLoaded.values());
        if (activeImage != null && !images.contains(activeImage))
            activeImage = null;

        Collections.sort(images);
    }

    public void takeSnapshot() {
        boolean saveMode = this.saveMode;
        IplImage image = Camera.getInstance().grabImage();

        if (isDirectoryValid() && saveMode) {
            String fullFileName;
            int i = 1;
            do {
                fullFileName = directory + File.separator + snapshotTag + " " + i;
            } while(new File(fullFileName + ".jpg").exists());
            fullFileName = fullFileName + ".jpg";

            try {
                cvSaveImage(fullFileName, image);
            } finally {
                activeImage = new LoadedImage(image, snapshotTag, fullFileName);
                images.add(activeImage);
                reloadImages();
                for (LoadedImage loadedImage : images) {
                    if (loadedImage.getFilename().equals(fullFileName)) {
                        activeImage = loadedImage;
                        break;
                    }
                }
            }
        } else {
            activeImage = new LoadedImage(image, snapshotTag, null);
            activeImageNotSaved = true;
        }
    }

    private boolean isDirectoryValid() {
        return !directory.isEmpty() && new File(directory).isDirectory();
    }

    @Override
    public Element withXML(org.w3c.dom.Element node, Map<UUID, Processor> map) {
        if (!isMutable())
            return createMutableClone().withXML(node, map);

        super.withXML(node, map);
        directory = XMLHelper.getDataUnderNode(node, "Directory");
        return this;
    }

    @Override
    public org.w3c.dom.Element getXMLForDocument(Document doc) {
        org.w3c.dom.Element node = super.getXMLForDocument(doc);
        XMLHelper.addDataUnderNode(node, "Directory", directory);
        return node;
    }
}
