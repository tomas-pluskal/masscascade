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

package uk.ac.ebi.masscascade.chartsthreed.axis;

import javax.media.j3d.Node;
import javax.media.j3d.Transform3D;
import javax.media.j3d.TransformGroup;
import javax.vecmath.Vector3f;

public class XAxisBuilder extends AxisBuilder {

    public XAxisBuilder(String label, String[] tickLabels, double[] tickLocations) {

        setLabel(label);
        setTickLabels(tickLabels);
        setTickLocations(tickLocations);
    }

    public Node getNode() {

        Transform3D t3d = new Transform3D();
        t3d.set(1 / scale, new Vector3f(-0.5f, -0.5f, 0));
        TransformGroup tg = new TransformGroup(t3d);
        tg.addChild(super.getNode());
        return tg;
    }
}
