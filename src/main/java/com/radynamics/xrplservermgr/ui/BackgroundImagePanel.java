package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.utils.ImageUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class BackgroundImagePanel extends JPanel {
    private BufferedImage image;
    private Dimension backgroundImageOffset = new Dimension(0, 0);

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image == null) {
            return;
        }

        var drawingImage = image;
        if (image.getHeight() > getHeight()) {
            var offset = image.getHeight() - getHeight();
            drawingImage = ImageUtils.toBufferedImage(ImageUtils.scale(image, image.getWidth() - offset, image.getHeight() - offset));
        }
        if (image.getWidth() > getWidth()) {
            var offset = image.getWidth() - getWidth();
            drawingImage = ImageUtils.toBufferedImage(ImageUtils.scale(image, image.getWidth() - offset, image.getHeight() - offset));
        }

        var x = getWidth() - drawingImage.getWidth() - backgroundImageOffset.width;
        var y = getHeight() - drawingImage.getHeight() - backgroundImageOffset.height;
        g.drawImage(drawingImage, x, y, null);
    }

    public void backgroundImage(Image image) {
        this.image = image == null ? null : ImageUtils.toBufferedImage(image);
    }

    public void backgroundImageOffset(Dimension backgroundImageOffset) {
        this.backgroundImageOffset = backgroundImageOffset;
    }
}
