package uk.ac.ebi.masscascade.bless.table.renderer;

import uk.ac.ebi.masscascade.commons.Status;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.net.URL;

public class IconUtil {

    private static final ImageIcon strongIcon =
            createImageIcon("/uk/ac/ebi/masscascade/icons/like.png", "Strong evidence");
    private static final ImageIcon intermediateIcon =
            createImageIcon("/uk/ac/ebi/masscascade/icons/stop.png", "Intermediate evidence");
    private static final ImageIcon weakIcon =
            createImageIcon("/uk/ac/ebi/masscascade/icons/review.png", "Weak evidence");

    private static final ImageIcon deleteIcon =
            createImageIcon("/uk/ac/ebi/masscascade/icons/close.png", "Delete record");

    public static Icon getStatusIcon(Status status) {

        Icon icon = null;

        switch (status) {
            case STRONG:
                icon = strongIcon;
                break;
            case INTERMEDIATE:
                icon = intermediateIcon;
                break;
            case WEAK:
                icon = weakIcon;
                break;
        }

        return icon;
    }

    public static Icon getDeleteIcon() {
        return deleteIcon;
    }

    public static ImageIcon createImageIcon(String path, String description) {

        URL imgURL = IconUtil.class.getResource(path);
        if (imgURL != null) return new ImageIcon(imgURL, description);
        else return null;
    }
}
