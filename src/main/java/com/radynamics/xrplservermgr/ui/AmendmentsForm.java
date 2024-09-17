package com.radynamics.xrplservermgr.ui;

import com.radynamics.xrplservermgr.xrpl.Amendment;
import com.radynamics.xrplservermgr.xrpl.parser.Features;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class AmendmentsForm extends JDialog {
    private final AmendmentsView view = new AmendmentsView();

    private final FormAcceptCloseHandler formAcceptCloseHandler = new FormAcceptCloseHandler(this);

    public AmendmentsForm(Frame owner, Features features, List<Amendment> knownAmendments) {
        super(owner, "Amendments");
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        setModal(true);

        formAcceptCloseHandler.configure();

        var l = new SpringLayout();
        setLayout(l);

        view.init(features, knownAmendments);
        add(view);
        view.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));

        l.putConstraint(SpringLayout.WEST, view, 0, SpringLayout.WEST, getContentPane());
        l.putConstraint(SpringLayout.NORTH, view, 0, SpringLayout.NORTH, getContentPane());
        l.putConstraint(SpringLayout.EAST, view, 0, SpringLayout.EAST, getContentPane());
        l.putConstraint(SpringLayout.SOUTH, view, -60, SpringLayout.SOUTH, getContentPane());

        {
            var pnl = new JPanel();
            add(pnl);
            l.putConstraint(SpringLayout.EAST, pnl, -5, SpringLayout.EAST, getContentPane());
            l.putConstraint(SpringLayout.SOUTH, pnl, 0, SpringLayout.SOUTH, getContentPane());
            {
                var cmd = new JButton("OK");
                cmd.setPreferredSize(new Dimension(150, 35));
                cmd.addActionListener(e -> formAcceptCloseHandler.accept());
                pnl.add(cmd);
            }
            {
                var cmd = new JButton("Cancel");
                cmd.setPreferredSize(new Dimension(150, 35));
                cmd.addActionListener(e -> formAcceptCloseHandler.close());
                pnl.add(cmd);
            }
        }
    }

    public List<VoteState> changes() {
        return view.changes();
    }

    public boolean accepted() {
        return formAcceptCloseHandler.accepted();
    }
}
