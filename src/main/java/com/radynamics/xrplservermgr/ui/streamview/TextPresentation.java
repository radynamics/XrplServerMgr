package com.radynamics.xrplservermgr.ui.streamview;

import com.radynamics.xrplservermgr.utils.Utils;
import com.radynamics.xrplservermgr.xrpl.KnownValidatorRepo;
import com.radynamics.xrplservermgr.xrpl.subscription.LedgerStreamData;
import com.radynamics.xrplservermgr.xrpl.subscription.LedgerStreamListener;
import com.radynamics.xrplservermgr.xrpl.subscription.ValidationStreamData;
import com.radynamics.xrplservermgr.xrpl.subscription.ValidationStreamListener;
import org.apache.commons.lang3.StringUtils;

import javax.swing.*;
import javax.swing.text.DefaultCaret;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class TextPresentation implements Presentation, ValidationStreamListener, LedgerStreamListener {
    private final JTextArea txt = new JTextArea();
    private final KnownValidatorRepo knownValidatorRepo;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    public TextPresentation(KnownValidatorRepo knownValidatorRepo) {
        this.knownValidatorRepo = knownValidatorRepo;
        txt.setEditable(false);
        txt.setFont(new Font("monospaced", Font.PLAIN, 12));

        var caret = (DefaultCaret) txt.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
    }

    @Override
    public void onReceive(ValidationStreamData data) {
        var ledgerIndexText = StringUtils.leftPad(data.ledgerIndex(), 10, ' ');
        var signingTimeText = StringUtils.leftPad(Utils.fromRippleTime(data.signingTime()).format(formatter), 10, ' ');

        var validatorValueText = data.masterKey() == null ? data.validationPublicKey() : data.masterKey();
        var validatorText = StringUtils.leftPad(validatorValueText, 55, ' ');
        var knownValidator = knownValidatorRepo.get(data.masterKey()).orElse(null);
        if (knownValidator != null) {
            validatorText += " (%s)".formatted(knownValidator.domain());
        }
        appendLog("%s %s %s".formatted(ledgerIndexText, signingTimeText, validatorText));
    }

    @Override
    public void onReceive(LedgerStreamData data) {
        var typeText = StringUtils.leftPad(data.type(), 10, ' ');
        var ledgerIndexText = StringUtils.leftPad(String.valueOf(data.ledgerIndex()), 10, ' ');
        var txCountText = StringUtils.leftPad(String.valueOf(data.txnCount()), 6, ' ');
        var ledgerHashText = StringUtils.leftPad(data.ledgerHash(), 60, ' ');
        appendLog("%s %s %s %s".formatted(typeText, ledgerIndexText, txCountText, ledgerHashText));
    }

    private void appendLog(String text) {
        var sdf = new SimpleDateFormat("HH:mm:ss.SSS");
        var timeText = sdf.format(new Date());
        txt.append("%s: %s\n".formatted(timeText, text));
    }

    @Override
    public JComponent view() {
        return txt;
    }
}
