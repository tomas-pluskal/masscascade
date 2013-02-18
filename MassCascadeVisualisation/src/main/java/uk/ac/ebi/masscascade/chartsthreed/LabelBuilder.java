/*
 * Copyright (c) 2013, Stephan Beisken. All rights reserved.
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
 */

package uk.ac.ebi.masscascade.chartsthreed;

import org.freehep.j3d.plot.Binned2DData;
import uk.ac.ebi.masscascade.core.container.file.profile.FileProfileContainer;
import uk.ac.ebi.masscascade.interfaces.Profile;

import javax.media.j3d.*;
import javax.vecmath.Color3f;
import javax.vecmath.Point3f;
import java.awt.*;

public class LabelBuilder {

    private static final Font3D FONT = new Font3D(new Font("SansSerif", Font.PLAIN, 1), new FontExtrusion());
    private static final float PLOT_WIDTH = 200;
    private static final float PLOT_HALF_WIDTH = 100;
    private static final double PLOT_Z = 1.5;
    private static final double SCALE = 0.005;

    private float mx;
    private float my;
    private float mz;
    private float bx;
    private float by;

    private Group labelsGroup;

    private FileProfileContainer peaks;

    public void updateLabels(FileProfileContainer peaks) {

        this.peaks = peaks;
    }

    public Node buildContent(Binned2DData binned2DData) {

        if (labelsGroup == null) {
            labelsGroup = new Group();
            labelsGroup.setCapability(Group.ALLOW_CHILDREN_EXTEND);
            labelsGroup.setCapability(Group.ALLOW_CHILDREN_READ);
            labelsGroup.setCapability(Group.ALLOW_CHILDREN_WRITE);
        }
        labelsGroup.removeAllChildren();

        Appearance ap = buildAppearance();

        calculateCoefficients(binned2DData);

        if (peaks == null) return labelsGroup;

        Profile peak;
        for (int peakNumber : peaks.getProfileNumbers().keySet()) {

            peak = peaks.getProfile(peakNumber);

            Point3f labelPos = getPoint(peak);
            double mass = ((int) (peak.getMzIntDp().x * 100)) / 100.0;

            Shape3D shape = createShape("" + mass, labelPos, ap);

            // high-memory
            TransformGroup tg = transformShape(shape);
            BranchGroup bg = new BranchGroup();
            bg.addChild(tg);
            labelsGroup.addChild(bg);
        }

        return labelsGroup;
    }

    private void calculateCoefficients(Binned2DData binned2DData) {

        mx = PLOT_WIDTH / (binned2DData.xMax() - binned2DData.xMin());
        bx = PLOT_HALF_WIDTH - (binned2DData.xMax() * mx);

        my = PLOT_WIDTH / (binned2DData.yMax() - binned2DData.yMin());
        by = PLOT_HALF_WIDTH - (binned2DData.yMax() * my);

        mz = (float) (PLOT_Z / (binned2DData.zMax() - binned2DData.zMin()));
    }

    private Point3f getPoint(Profile peak) {

        float x = (float) (mx * peak.getRetentionTime() + bx);
        float y = (float) (my * peak.getMzIntDp().x + by) * -1; // reverse
        float z = (float) (mz * peak.getMzIntDp().y);

        return new Point3f(x, y, z);
    }

    private Shape3D createShape(String labelText, Point3f vector, Appearance ap) {

        Text3D text = new Text3D(FONT, labelText);
        text.setPosition(vector);
        Shape3D shape = new Shape3D(text, ap);
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_READ);
        shape.setCapability(Shape3D.ALLOW_APPEARANCE_WRITE);

        return shape;
    }

    private TransformGroup transformShape(Shape3D shape) {

        Transform3D tr = new Transform3D();
        tr.setScale(SCALE);
        TransformGroup tg = new TransformGroup(tr);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        tg.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        tg.addChild(shape);

        return tg;
    }

    private Appearance buildAppearance() {

        Appearance ap = new Appearance();

        Material material = new Material();
        material.setAmbientColor(new Color3f(Color.red));
        material.setDiffuseColor(new Color3f(Color.red));
        ap.setMaterial(material);

        return ap;
    }
}
