package com.radynamics.xrplservermgr.ui;

import java.awt.*;

public class WaitCursor implements AutoCloseable {
    private final Window owner;

    public WaitCursor(Window owner) {
        this.owner = owner;
        owner.setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
    }

    @Override
    public void close() {
        owner.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
    }
}
