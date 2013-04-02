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

import com.sun.j3d.utils.geometry.GeometryInfo;
import com.sun.j3d.utils.geometry.NormalGenerator;
import com.sun.j3d.utils.geometry.Stripifier;
import org.freehep.j3d.plot.AbstractPlotBuilder;

import javax.media.j3d.Appearance;
import javax.media.j3d.Geometry;
import javax.media.j3d.Material;
import javax.media.j3d.Node;
import javax.media.j3d.PolygonAttributes;
import javax.media.j3d.Shape3D;
import javax.vecmath.Color3b;
import javax.vecmath.Color3f;
import javax.vecmath.Point3d;
import javax.vecmath.Vector3f;

public class SurfaceBuilder extends AbstractPlotBuilder {

    private Shape3D shape;

    public Node buildContent(NormalizedBinned2DData data) {

        shape = createShape();
        shape.setCapability(shape.ALLOW_GEOMETRY_WRITE);
        shape.setGeometry(buildGeometry(data));
        return shape;
    }

    public void updatePlot(NormalizedBinned2DData data) {

        shape.setGeometry(buildGeometry(data));
    }

    private Geometry buildGeometry(NormalizedBinned2DData data) {

        int nXbins = data.xBins();
        int nYbins = data.yBins();
        float xBinWidth = 1.f / nXbins;
        float yBinWidth = 1.f / nYbins;
        float x, y;
        int i, k, l;

        Point3d bcoord[] = new Point3d[(nXbins - 1) * (nYbins - 1) * 4];
        Color3b bcolor[] = new Color3b[(nXbins - 1) * (nYbins - 1) * 4];

        for (i = 0; i < (nXbins - 1) * (nYbins - 1) * 4; i++) {
            bcoord[i] = new Point3d();
            bcolor[i] = new Color3b();
        }

        // Fill bcoord array with points that compose the surface
        int bcur = 0;
        for (k = 0, x = -.5f; k < nXbins - 1; k++, x += xBinWidth)
            for (l = 0, y = -.5f; l < nYbins - 1; l++, y += yBinWidth) {

                // Point x,y
                bcoord[bcur].x = x + xBinWidth / 2.f;
                bcoord[bcur].y = y + yBinWidth / 2.f;
                bcoord[bcur].z = data.zAt(k, l);
                bcolor[bcur] = data.colorAt(k, l);
                bcur++;

                // Next point in y direction
                bcoord[bcur].x = x + xBinWidth / 2.f;
                bcoord[bcur].y = y + 1.5f * xBinWidth;
                bcoord[bcur].z = data.zAt(k, l + 1);
                bcolor[bcur] = data.colorAt(k, l + 1);
                bcur++;

                // Next point diagonally
                bcoord[bcur].x = x + 1.5f * xBinWidth;
                bcoord[bcur].y = y + 1.5f * yBinWidth;
                bcoord[bcur].z = data.zAt(k + 1, l + 1);
                bcolor[bcur] = data.colorAt(k + 1, l + 1);
                bcur++;

                // Next point in x direction
                bcoord[bcur].x = x + 1.5f * xBinWidth;
                bcoord[bcur].y = y + yBinWidth / 2.f;
                bcoord[bcur].z = data.zAt(k + 1, l);
                bcolor[bcur] = data.colorAt(k + 1, l);
                bcur++;
            }

        GeometryInfo geom = new GeometryInfo(GeometryInfo.QUAD_ARRAY);

        //geom.setNormals(normals);
        geom.setCoordinates(bcoord);
        geom.setColors(bcolor);
        NormalGenerator ng = new NormalGenerator();
        ng.generateNormals(geom);

        // Make normals conform to our "handedness" of z direction
        // i.e. change their sign
        Vector3f normals[] = geom.getNormals();
        for (i = 0; i < normals.length; ++i) {
            normals[i].x = -normals[i].x;
            normals[i].y = -normals[i].y;
            normals[i].z = -normals[i].z;
        }
        geom.setNormals(normals);

        Stripifier st = new Stripifier();
        st.stripify(geom);
        geom.recomputeIndices();

        return geom.getGeometryArray();
    }

    Shape3D createShape() {

        Shape3D surface = new Shape3D();
        surface.setAppearance(createMaterialAppearance());

        return surface;
    }

    private Appearance createMaterialAppearance() {

        Appearance materialAppear = new Appearance();
        PolygonAttributes polyAttrib = new PolygonAttributes();
        polyAttrib.setCullFace(PolygonAttributes.CULL_NONE);
        materialAppear.setPolygonAttributes(polyAttrib);

        Material material = new Material();
        // set diffuse color to red (this color will only be used
        //     if lighting disabled - per-vertex color overrides)
        material.setDiffuseColor(new Color3f(1.0f, 0.0f, 0.0f));
        materialAppear.setMaterial(material);

        return materialAppear;
    }
}

