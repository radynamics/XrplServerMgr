package com.radynamics.xrplservermgr.ui.backgroundimage;

import com.radynamics.xrplservermgr.utils.ImageUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class LittleLedgers implements BackgroundImageProvider {
    private final static Logger log = LogManager.getLogger(LittleLedgers.class);
    private Timer timer;
    private final ArrayList<BackgroundImageListener> listener = new ArrayList<>();

    @Override
    public void start() {
        stop();
        timer = new java.util.Timer("backgroundImageTimer");
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                raiseChanged(createBackgroundImageLittleLedgers());
            }
        }, 20000L, 2000);

        raiseChanged(createBackgroundImageLittleLedgers());
    }

    @Override
    public void stop() {
        if (timer != null) {
            timer.cancel();
        }
        timer = null;
    }

    private Image createBackgroundImageLittleLedgers() {
        var images = new ArrayList<BufferedImage>();
        try {
            images.add(loadLittleLedger("littleledger0.png"));
            images.add(loadLittleLedger("littleledger1.png"));
            images.add(loadLittleLedger("littleledger2.png"));
            images.add(loadLittleLedger("littleledger3.png"));
            images.add(loadLittleLedger("littleledger4.png"));
            images.add(loadLittleLedger("littleledger5.png"));
            images.add(loadLittleLedger("littleledger6.png"));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
            return null;
        }
        return ImageUtils.toGrayscale(ImageUtils.mergeHorizontally(images));
    }

    private BufferedImage loadLittleLedger(String name) throws IOException {
        var img = ImageUtils.toBufferedImage(ImageUtils.fromResource("img/littleledgers/" + name, 105, 128), 0.2f);
        var flip = Math.random() < 0.5;
        if (flip) {
            img = ImageUtils.mirrorHorizontal(img);
        }
        return img;
    }

    @Override
    public void addImageChangedListener(BackgroundImageListener l) {
        listener.add(l);
    }

    private void raiseChanged(Image image) {
        for (var l : listener) {
            l.onImageChanged(image);
        }
    }
}
