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

package uk.ac.ebi.masscascade.chartsthreed;

import com.sun.j3d.utils.behaviors.mouse.MouseRotate;
import com.sun.j3d.utils.behaviors.mouse.MouseTranslate;
import com.sun.j3d.utils.behaviors.mouse.MouseZoom;
import com.sun.j3d.utils.universe.SimpleUniverse;
import org.freehep.j3d.plot.PlotKeyNavigatorBehavior;

import javax.media.j3d.*;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

abstract class Plot3D extends Canvas3D {

    protected boolean init = false;
    protected SimpleUniverse universe;

    Plot3D() {

        super(SimpleUniverse.getPreferredConfiguration());
    }

    protected void init() {

        Node plot = createPlot();
        BranchGroup scene = defineMouseBehaviour(plot);
        setupLights(scene);
        scene.compile();

        universe = new SimpleUniverse(this);
        universe.getViewingPlatform().setNominalViewingTransform();
        universe.addBranchGraph(scene);

        init = true;
    }

    // addNotify is called when the Canvas3D is added to a container
    public void addNotify() {

        if (!init) init();
        // must call for Java3D to operate properly when overriding
        super.addNotify();
    }

    /**
     * Override to provide plot content
     */
    protected abstract Node createPlot();

    /**
     * Override to provide different mouse behaviour
     */
    protected BranchGroup defineMouseBehaviour(Node scene) {

        BranchGroup bg = new BranchGroup();
        Bounds bounds = getDefaultBounds();

        TransformGroup objTransform = new TransformGroup();
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_WRITE);
        objTransform.setCapability(TransformGroup.ALLOW_TRANSFORM_READ);
        objTransform.addChild(scene);
        bg.addChild(objTransform);

        MouseRotate mouseRotate = new MouseRotate();
        mouseRotate.setTransformGroup(objTransform);
        mouseRotate.setSchedulingBounds(bounds);
        bg.addChild(mouseRotate);

        MouseTranslate mouseTranslate = new MouseTranslate();
        mouseTranslate.setTransformGroup(objTransform);
        mouseTranslate.setSchedulingBounds(bounds);
        bg.addChild(mouseTranslate);

        MouseZoom mouseZoom = new MouseZoom();
        mouseZoom.setTransformGroup(objTransform);
        mouseZoom.setSchedulingBounds(bounds);
        bg.addChild(mouseZoom);

        // Set initial transformation
        Transform3D trans = createDefaultOrientation();
        objTransform.setTransform(trans);

        Behavior keyBehavior = new PlotKeyNavigatorBehavior(objTransform, .1f, 10f);
        objTransform.addChild(keyBehavior);
        keyBehavior.setSchedulingBounds(bounds);

        return bg;
    }

    protected void setupLights(BranchGroup root) {

        DirectionalLight lightD = new DirectionalLight();
        lightD.setDirection(new Vector3f(0.0f, -0.7f, -0.7f));
        lightD.setInfluencingBounds(getDefaultBounds());
        root.addChild(lightD);

        //  This second light is added for the Surface Plot, so you can see the "under" surface
        DirectionalLight lightD1 = new DirectionalLight();
        lightD1.setDirection(new Vector3f(0.0f, 0.7f, 0.7f));
        lightD1.setInfluencingBounds(getDefaultBounds());
        root.addChild(lightD1);

        AmbientLight lightA = new AmbientLight();
        lightA.setInfluencingBounds(getDefaultBounds());
        root.addChild(lightA);
    }

    /**
     * Override to set a different initial transformation
     */
    protected Transform3D createDefaultOrientation() {

        Transform3D trans = new Transform3D();
        trans.setIdentity();
        trans.rotX(-Math.PI / 3.);
        trans.setTranslation(new Vector3f(0.f, -.3f, 0.f));
        return trans;
    }

    /**
     * Returns a bounds object that can be used for most behaviours, lighting models, etc.
     */
    protected Bounds getDefaultBounds() {

        if (bounds == null) {
            Point3d center = new Point3d(0, 0, 0);
            bounds = new BoundingSphere(center, 10);
        }
        return bounds;
    }

    private Bounds bounds;
}
