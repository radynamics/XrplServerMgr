package com.radynamics.xrplservermgr.ui.backgroundimage;

public interface BackgroundImageProvider {
    void start();

    void stop();

    void addImageChangedListener(BackgroundImageListener l);
}
