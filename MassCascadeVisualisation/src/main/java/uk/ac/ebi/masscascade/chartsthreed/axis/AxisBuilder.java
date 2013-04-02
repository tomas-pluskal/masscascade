/*
 * Copyright (C) 2013 EMBL - European Bioinformatics Institute
 *
 * This file is part of MassCascade.
 *
 * MassCascade is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MassCascade is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with MassCascade. If not, see <http://www.gnu.org/licenses/>.
 *
 * Contributors:
 *   Stephan Beisken - initial API and implementation
 */

package uk.ac.ebi.masscascade.chartsthreed.axis;

import org.freehep.j3d.plot.AxisLabelCalculator;

import javax.media.j3d.Billboard;
import javax.media.j3d.BoundingSphere;
import javax.media.j3d.BranchGroup;
import javax.media.j3d.Font3D;
import javax.media.j3d.LineArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.media.j3d.Text3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;
import java.awt.*;

/**
 * Class to build and layout a 3D axis.
 */
public class AxisBuilder {

    private String labelText;
    private Font3D labelFont = defaultLabelFont;
    private double[] tickLocations;
    private String[] tickLabels;
    private Font3D tickFont = defaultTickFont;

    private TransformGroup mainGroup;
    private Text3D label;
    private Shape3D axis;
    private BranchGroup ticks;

    protected static float scale = 256f; // See comment on apply
    protected static float major = 0.02f * scale;
    protected static float minor = 0.01f * scale;
    protected static float tickOffSet = 0.06f * scale;
    protected static float labelOffSet = 0.12f * scale;

    private static final Font3D defaultLabelFont = new Font3D(new Font("DIALOG", Font.BOLD, 8), null);
    private static final Font3D defaultTickFont = new Font3D(new Font("DIALOG", Font.PLAIN, 8), null);

    /**
     * Constructs an axis
     */
    AxisBuilder() {
        // build all the major components of the Axis, keeping the important
        // parts in member variables so we can modify them later.

        label = new Text3D(); // The Axis label
        label.setAlignment(label.ALIGN_CENTER);
        Point3f pos = new Point3f(scale / 2, -labelOffSet, 0);
        label.setPosition(pos);
        label.setCapability(label.ALLOW_FONT3D_WRITE);
        label.setCapability(label.ALLOW_STRING_WRITE);

        axis = new Shape3D(); // The axis and tick marks.
        axis.setCapability(axis.ALLOW_GEOMETRY_WRITE);

        // We can create the tick labels yet, since we don't know how many there
        // will be, but we can make a group to hold them.

        ticks = new BranchGroup();
        ticks.setCapability(ticks.ALLOW_CHILDREN_READ);
        ticks.setCapability(ticks.ALLOW_CHILDREN_WRITE);
        ticks.setCapability(ticks.ALLOW_DETACH);

        // Group the components together

        mainGroup = new TransformGroup();
        mainGroup.setCapability(ticks.ALLOW_CHILDREN_WRITE);
        mainGroup.setCapability(ticks.ALLOW_CHILDREN_EXTEND);
        mainGroup.addChild(new Shape3D(label));
        mainGroup.addChild(axis);
        mainGroup.addChild(ticks);

        // Set up a BulletinBoard behaviour to keep the axis oriented
        // towards the user.

        mainGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        mainGroup.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        Billboard bboard = new Billboard(mainGroup);
        mainGroup.addChild(bboard);
        bboard.setSchedulingBounds(new BoundingSphere(new Point3d(0, 0, 0), 10));
        bboard.setAlignmentAxis(1.0f, 0.0f, 0.0f);
    }

    public String getLabel() {

        return labelText;
    }

    public void setLabel(String label) {

        labelText = label;
    }

    public Font3D getLabelFont() {

        return labelFont;
    }

    public void setLabelFont(Font3D font) {

        labelFont = font;
    }

    public Font3D getTickFont() {

        return tickFont;
    }

    public void setTickFont(Font3D font) {

        tickFont = font;
    }

    /**
     * Tick labels and locations (positions) can be set
     * by the caller or calculated and set by the
     * createLabelsNTicks method as a convenience.
     */
    public double[] getTickLocations() {

        return tickLocations;
    }

    public void setTickLocations(double[] ticks) {

        tickLocations = ticks;
    }

    public String[] getTickLabels() {

        return tickLabels;
    }

    public void setTickLabels(String[] labels) {

        tickLabels = labels;
    }

    /**
     * Call the createLabelsNTicks method if you would like the
     * axisbuilder to create axis labels and tick positions for you.
     */
    public void createLabelsNTicks(double min, double max) {

        AxisLabelCalculator axisCalc = new AxisLabelCalculator();
        axisCalc.createNewLabels(min, max);
        tickLabels = axisCalc.getLabels();
        tickLocations = axisCalc.getPositions();
    }

    /**
     * Call this method after setting the required axis properties, to actually
     * setup/modify the axis appearance.
     */
    public void apply() {
        /*
           * Build an axis on the X axis from 0 to 256. Note the reason for the
           * 256 is to compensate for the giant size of the Text3D characters.
           * The axis is built in the XY plane.
           * We will use suitable translation and rotation to position it later.
           *
           * Note we currently use Text3D to draw the labels. This is because Text3D
           * seems somewhat easier to use. A better solution would
           * probably involve using Text2D, but the com.sun.j3d.utils.geometry.Text2D
           * class seems pretty brain dead. A better solution may be to get the source
           * for the Text2D class and rewrite it for our own purposes.
           */

        int tMajor = (tickLocations.length - 1) / (tickLabels.length - 1);

        LineArray lines = new LineArray(2 * tickLocations.length + 2, LineArray.COORDINATES);
        int coordIdx = 0;            // coordinate index into Axis linearray

        // Actual axis
        lines.setCoordinate(coordIdx++, new Point3d(0, 0, 0));
        lines.setCoordinate(coordIdx++, new Point3d(scale, 0, 0));

        ticks.detach();
        ticks = new BranchGroup();
        ticks.setCapability(ticks.ALLOW_CHILDREN_READ);
        ticks.setCapability(ticks.ALLOW_CHILDREN_WRITE);
        ticks.setCapability(ticks.ALLOW_DETACH);

        // Rendering Ticks on Axis

        for (int i = 0; i < tickLocations.length; i++) {
            float x = (float) tickLocations[i] * scale;
            if (i % tMajor == 0) {
                lines.setCoordinate(coordIdx++, new Point3d(x, 0, 0));
                lines.setCoordinate(coordIdx++, new Point3d(x, -major, 0));

                // Add the tick label

                int nt = i / tMajor;
                Point3f pos = new Point3f(x, -tickOffSet, 0);
                Text3D tickLabel = new Text3D(tickFont, tickLabels[nt], pos);
                tickLabel.setAlignment(tickLabel.ALIGN_CENTER);
                ticks.addChild(new Shape3D(tickLabel));
            } else // Minor tick mark
            {
                lines.setCoordinate(coordIdx++, new Point3d(x, 0, 0));
                lines.setCoordinate(coordIdx++, new Point3d(x, -minor, 0));
            }
        }

        mainGroup.addChild(ticks);
        label.setFont3D(labelFont);
        label.setString(labelText);
        axis.setGeometry(lines);
    }

    /**
     * Returns the node representing this Axis
     * Subclasses can override this method to transform this axis
     * to make it into an X,Y,Z axis.
     */
    public Node getNode() {

        return mainGroup;
    }
}
