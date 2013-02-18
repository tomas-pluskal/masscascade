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
import uk.ac.ebi.masscascade.chartsthreed.axis.AxisBuilder;
import uk.ac.ebi.masscascade.chartsthreed.axis.XAxisBuilder;
import uk.ac.ebi.masscascade.chartsthreed.axis.YAxisBuilder;
import uk.ac.ebi.masscascade.chartsthreed.axis.ZAxisBuilder;
import uk.ac.ebi.masscascade.chartsthreed.data.Unbinned3DDataImpl;
import uk.ac.ebi.masscascade.core.file.profile.FileProfileContainer;

import javax.media.j3d.Group;
import javax.media.j3d.LineArray;
import javax.media.j3d.Node;
import javax.media.j3d.Shape3D;
import javax.vecmath.Point3d;
import javax.vecmath.Point3f;

public class Plot3DSurface extends Plot3D {

    private Binned2DData data;
    private FileProfileContainer peaks;
    private SurfaceBuilder surfaceBuilder;
    private LabelBuilder labelBuilder;
    private Node plot;
    private Node label;
    private AxisBuilder xAxis;
    private AxisBuilder yAxis;
    private AxisBuilder zAxis;
    private String xAxisLabel = "Retention Time [s]";
    private String yAxisLabel = "Mass-over-Charge";
    private String zAxisLabel = "Abundance";
    private double xmin;
    private double xmax;
    private double ymin;
    private double ymax;
    private double zmin;
    private double zmax;

    public Plot3DSurface() {

        super();

        // add dummy data
        Unbinned3DDataImpl unbinned3DData = new Unbinned3DDataImpl();
        unbinned3DData.addDataPoint(new Point3f(0f, 0f, 0f));
        unbinned3DData.addDataPoint(new Point3f(5f, 5f, 1f));
        unbinned3DData.addDataPoint(new Point3f(10f, 10f, 0f));
        this.data = unbinned3DData.getBinned2DData(2, 2);
    }

    public void setData(Binned2DData data) {

        this.data = data;
        if (init) {
            if (data.xMin() != xmin || data.xMax() != xmax) {
                xmin = data.xMin();
                xmax = data.xMax();
                xAxis.createLabelsNTicks(xmin, xmax);
                xAxis.apply();
            }
            if (data.yMin() != ymin || data.yMax() != ymax) {
                ymin = data.yMin();
                ymax = data.yMax();
                yAxis.createLabelsNTicks(ymin, ymax);
                yAxis.apply();
            }
            if (data.zMin() != zmin || data.zMax() != zmax) {
                zmin = data.zMin();
                zmax = data.zMax();
                zAxis.createLabelsNTicks(zmin, zmax);
                zAxis.apply();
            }

            // z-normalized
            surfaceBuilder.updatePlot(new NormalizedBinned2DData(data));
        }
    }

    public void setPeaks(FileProfileContainer peaks) {

        if (init) {
            labelBuilder.updateLabels(peaks);
            labelBuilder.buildContent(data);
        }

        this.peaks = peaks;
    }

    protected Node createPlot() {

        surfaceBuilder = new SurfaceBuilder();
        plot = surfaceBuilder.buildContent(new NormalizedBinned2DData(data));

        labelBuilder = new LabelBuilder();
        labelBuilder.updateLabels(peaks);
        label = labelBuilder.buildContent(data);

        double[] tick = {0, .1, .2, .3, .4, .5, .6, .7, .8, .9, 1.0};
        String[] labels = {"0.0", "0.2", "0.4", "0.6", "0.8", "1.0"};

        xAxis = new XAxisBuilder(xAxisLabel, labels, tick);
        yAxis = new YAxisBuilder(yAxisLabel, labels, tick);
        zAxis = new ZAxisBuilder(zAxisLabel, labels, tick);

        xAxis.createLabelsNTicks(data.xMin(), data.xMax());
        yAxis.createLabelsNTicks(data.yMin(), data.yMax());
        zAxis.createLabelsNTicks(data.zMin(), data.zMax());

        xAxis.apply();
        yAxis.apply();
        zAxis.apply();

        Shape3D axis = new Shape3D();
        LineArray lines = new LineArray(4, LineArray.COORDINATES);
        lines.setCoordinate(0, new Point3d(0.5f, -0.5f, 0));
        lines.setCoordinate(1, new Point3d(0.5f, 0.5f, 0));
        lines.setCoordinate(2, new Point3d(-0.5f, 0.5f, 0));
        lines.setCoordinate(3, new Point3d(0.5f, 0.5f, 0));

        axis.setGeometry(lines);

        Group g = new Group();
        g.addChild(plot);
        g.addChild(label);
        g.addChild(xAxis.getNode());
        g.addChild(yAxis.getNode());
        g.addChild(zAxis.getNode());
        g.addChild(axis);

        return g;
    }
}
