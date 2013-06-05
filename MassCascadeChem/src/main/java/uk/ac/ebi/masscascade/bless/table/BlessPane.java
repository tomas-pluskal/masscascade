package uk.ac.ebi.masscascade.bless.table;

import javax.swing.*;
import java.awt.*;

public class BlessPane extends JScrollPane {

    public BlessPane(Component view) {

        super(view);
        this.setVerticalScrollBarPolicy(VERTICAL_SCROLLBAR_ALWAYS);
    }
}
