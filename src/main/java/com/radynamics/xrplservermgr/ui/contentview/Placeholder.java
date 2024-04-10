package com.radynamics.xrplservermgr.ui.contentview;

import javax.swing.*;

public class Placeholder extends ContentView {
    private final String tabText;

    public Placeholder(JFrame parent, String tabText) {
        super(parent);
        this.tabText = tabText;
        add(new JLabel("This is a placeholder. Show content for %s".formatted(tabText())));
    }

    @Override
    public String tabText() {
        return tabText;
    }

    @Override
    public void close() {

    }
}
