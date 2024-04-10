package com.radynamics.xrplservermgr.utils;

import com.radynamics.xrplservermgr.ui.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;

public class ImageUtils {
    public static Image fromResource(String path, int w, int h) throws IOException {
        return scale(ImageIO.read(Utils.class.getClassLoader().getResourceAsStream(path)), w, h);
    }

    public static Image scale(Image image, int w, int h) {
        return image.getScaledInstance(w, h, Image.SCALE_DEFAULT);
    }

    public static Image toGrayscale(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int p = image.getRGB(x, y);

                int a = (p >> 24) & 0xff;
                int r = (p >> 16) & 0xff;
                int g = (p >> 8) & 0xff;
                int b = p & 0xff;

                int avg = (r + g + b) / 3;
                p = (a << 24) | (avg << 16) | (avg << 8) | avg;
                image.setRGB(x, y, p);
            }
        }

        return image;
    }

    public static BufferedImage toBufferedImage(Image image) {
        return toBufferedImage(image, 1);
    }

    public static BufferedImage toBufferedImage(Image image, float opacity) {
        if (image instanceof BufferedImage) {
            return (BufferedImage) image;
        }

        var bi = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        var g = bi.createGraphics();
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, opacity));
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return bi;
    }

    public static BufferedImage mergeHorizontally(ArrayList<BufferedImage> images) {
        var totalWidth = 0;
        var maxHeight = 0;
        for (var img : images) {
            totalWidth += img.getWidth();
            maxHeight = Math.max(maxHeight, img.getHeight());
        }

        var result = new BufferedImage(totalWidth, maxHeight, BufferedImage.TYPE_INT_ARGB);
        var g = result.createGraphics();
        var offsetLeft = 0;
        for (var img : images) {
            g.drawImage(img, offsetLeft, 0, null);
            offsetLeft += img.getWidth();
        }
        g.dispose();

        return result;
    }

    public static BufferedImage mirrorHorizontal(BufferedImage image) {
        int w = image.getWidth();
        int h = image.getHeight();
        var result = new BufferedImage(w, h, image.getType());
        var g = result.createGraphics();
        g.drawImage(image, 0, 0, w, h, w, 0, 0, h, null);
        g.dispose();
        return result;
    }
}
