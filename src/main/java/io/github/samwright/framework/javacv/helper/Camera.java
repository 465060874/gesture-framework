package io.github.samwright.framework.javacv.helper;

import com.googlecode.javacv.FrameGrabber;
import com.googlecode.javacv.cpp.opencv_core;
import io.github.samwright.framework.controller.MainWindowController;
import lombok.Getter;

import java.util.Calendar;

/**
 * User: Sam Wright Date: 15/09/2013 Time: 08:44
 */
public class Camera {

    @Getter private static final Camera instance = new Camera();
    private final static long timeout = 5000;
    private final static int deviceNumber = -1;

    private final Object[] lock = new Object[0];
    private long lastAccessTimestamp = 0l;
    private FrameGrabber grabber;
    private boolean grabberIsActive = false;


    private Camera() {
        try {
            grabber = FrameGrabber.createDefault(deviceNumber);
        } catch (FrameGrabber.Exception e) {
            e.printStackTrace();
        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(true) {
                    try {
                        Thread.sleep(timeout);
                    } catch (InterruptedException e) {
                        MainWindowController.getTopController().handleException(e);
                    }
                    disableCameraAfterTimeout();
                }
            }
        }).start();
    }

    public opencv_core.IplImage grabImage() {

        synchronized (lock) {
            if (!grabberIsActive) {
                try {
                    grabber.start();
                } catch (FrameGrabber.Exception e) {
                    throw new RuntimeException(e);
                }
                grabberIsActive = true;
                lastAccessTimestamp = Calendar.getInstance().getTimeInMillis();
            }

            opencv_core.IplImage toReturn;
            int attempts = 0;
            do {
                ++attempts;
                try {
                    toReturn = grabber.grab();
                } catch (FrameGrabber.Exception e) {
                    throw new RuntimeException(e);
                }
            } while(toReturn == null && attempts < 10);

            lock.notifyAll();

            if (toReturn == null)
                throw new RuntimeException("Could not capture image (grabber kept returning null)");

            return toReturn;
        }
    }

    private void disableCameraAfterTimeout() {
        synchronized (lock) {
            if (Calendar.getInstance().getTimeInMillis() > lastAccessTimestamp + 2000
                    && grabberIsActive) {
                try {
                    grabber.stop();
                    grabberIsActive = false;
                    lock.wait();
                } catch (FrameGrabber.Exception | InterruptedException e) {
                    MainWindowController.getTopController().handleException(e);
                }
            }
        }
    }
}
