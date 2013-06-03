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

package uk.ac.ebi.masscascade.charts;

import info.monitorenter.gui.chart.IAxis.AxisTitle;
import info.monitorenter.gui.chart.IRangePolicy;
import info.monitorenter.gui.chart.ITrace2D;
import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.ZoomableChart;
import info.monitorenter.gui.chart.pointpainters.PointPainterDisc;
import info.monitorenter.gui.chart.rangepolicies.RangePolicyFixedViewport;
import info.monitorenter.gui.chart.traces.Trace2DSimple;
import info.monitorenter.gui.chart.traces.painters.TracePainterDisc;
import info.monitorenter.gui.chart.traces.painters.TracePainterPolyline;
import info.monitorenter.gui.chart.traces.painters.TracePainterVerticalBar;
import info.monitorenter.util.Range;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.charts.painter.AnnotatedTracePoint2D;
import uk.ac.ebi.masscascade.charts.painter.TracePainterAnnotation;
import uk.ac.ebi.masscascade.charts.painter.TracePainterLabel;
import uk.ac.ebi.masscascade.charts.painter.TracePainterSpline;
import uk.ac.ebi.masscascade.utilities.DataSet;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.awt.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Class representing a customisable zoomable JChart2D chart.
 */
public class SimpleSpectrum extends ZoomableChart {

    private static final Logger LOGGER = Logger.getLogger(SimpleSpectrum.class);

    private static final double PADDING_Y = 0.05;
    private static final double PADDING_X = 0.10;

    private static final int BAR_SIZE = 2;
    private static final int DISC_SIZE = 5;
    private static final int DISC_SIZE_POINT = 1;
    private static final int FONT_SIZE = 10;
    private static final int SCALE = 1000;
    private static final int PRECISION = 2;

    private IRangePolicy rangePolicyY;
    private IRangePolicy rangePolicyX;

    private Map<String, ITrace2D> traces;

    public static enum PAINTERS {LABEL, DISC, ANNO, POLY, SPLINE, BAR, DISC_ONLY, POINT_ONLY}

    private Map<PAINTERS, Boolean> painterMap = new HashMap<PAINTERS, Boolean>() {{
        this.put(PAINTERS.POLY, true);
    }};

    /**
     * Constructs an empty spectrum chart.
     */
    public SimpleSpectrum() {

        super();
        this.setUseAntialiasing(true);
        this.setGridColor(Color.LIGHT_GRAY);
        this.getAxes().get(1).setPaintGrid(true);
        this.add(new PopupMenu("Settings"));
        traces = new HashMap<String, ITrace2D>();
    }

    /**
     * Sets both axis label.
     *
     * @param xAxis the x-axis label
     * @param yAxis the y-axis label
     */
    public void setAxisTitle(String xAxis, String yAxis) {

        this.getAxes().get(0).setAxisTitle(new AxisTitle(xAxis));
        this.getAxes().get(1).setAxisTitle(new AxisTitle(yAxis));
    }

    /**
     * Removes the label on the Y axis.
     */
    public void removeLabelsY() {
        this.getAxes().get(1).setPaintScale(false);
    }

    /**
     * Adds a data set to the chart.
     *
     * @param dataSet the data set
     */
    public void addData(DataSet dataSet) {

        ITrace2D trace = new Trace2DSimple(dataSet.getTitle());
        trace.setPhysicalUnits(dataSet.getxLabel(), dataSet.getyLabel());

        if (dataSet.getColor().equals(Color.BLACK)) {
            if (traces.containsKey(dataSet.getTitle())) trace.setColor(traces.get(dataSet.getTitle()).getColor());
        } else trace.setColor(dataSet.getColor());

        traces.put(dataSet.getTitle(), trace);
        this.addTrace(trace);

        updatePainters(new String[]{dataSet.getTitle()});

        AnnotatedTracePoint2D annotatedDataPoint;
        for (XYPoint dataPoint : dataSet.getDataSet()) {

            annotatedDataPoint = new AnnotatedTracePoint2D(dataPoint);
            if (dataSet.hasAnnotation(dataPoint)) {
                annotatedDataPoint.setAnnotation(dataSet.getAnnotation(dataPoint));
            }
            trace.addPoint(annotatedDataPoint);
        }

        PointPainterDisc pointDisc = new PointPainterDisc(DISC_SIZE);
        pointDisc.setColorFill(dataSet.getColor());
        trace.addPointHighlighter(pointDisc);
    }

    /**
     * Adds a list of data sets to the chart.
     *
     * @param dataSet a list of data sets
     */
    public void addData(List<DataSet> dataSet) {
        for (DataSet ds : dataSet) addData(ds);
    }

    /**
     * Scales the x and y-axis and adds a margin.
     *
     * @param buffer if the margins should be padded
     */
    public void updateTraceRanges(boolean buffer) {

        ExtendableRange rangeX = new ExtendableRange(Double.MAX_VALUE, Double.MIN_VALUE);
        ExtendableRange rangeY = new ExtendableRange(Double.MAX_VALUE, Double.MIN_VALUE);
        for (ITrace2D trace : traces.values()) {

            rangeX.extendRange(trace.getMinX());
            rangeX.extendRange(trace.getMaxX());
            rangeY.extendRange(trace.getMinY());
            rangeY.extendRange(trace.getMaxY());
        }

        double paddingX = PADDING_X;
        double paddingY = PADDING_Y;
        if (!buffer) {
            paddingX = 0;
            paddingY = 0;
        }

        double paddedMinX = rangeX.getLowerBounds() - rangeX.getLowerBounds() * paddingX;
        double paddedMaxX = rangeX.getUpperBounds() + rangeX.getUpperBounds() * paddingX;
        rangePolicyX = new RangePolicyFixedViewport(new Range(paddedMinX, paddedMaxX));
        this.getAxisX().setRangePolicy(rangePolicyX);

        double paddedMinY = rangeY.getLowerBounds() - rangeY.getLowerBounds() * paddingY;
        double paddedMaxY = rangeY.getUpperBounds() + rangeY.getUpperBounds() * paddingY;
        rangePolicyY = new RangePolicyFixedViewport(new Range(paddedMinY, paddedMaxY));
        this.getAxisY().setRangePolicy(rangePolicyY);
    }

    /**
     * Sets the default painter map which defines the style of newly added traces.
     *
     * @param painterMap the painter map
     */
    public void setDefaultTracePainter(Map<PAINTERS, Boolean> painterMap) {
        this.painterMap = painterMap;
    }

    /**
     * Adds annotations to the selected data sets.
     *
     * @param dataSetTitle    the data set titles
     * @param annotatedPoints the annotations for the data points (x-bound)
     */
    public void setAnnotations(String dataSetTitle, Map<Double, String> annotatedPoints) {

        if (!traces.containsKey(dataSetTitle)) return;

        Iterator<ITracePoint2D> it = traces.get(dataSetTitle).iterator();
        while (it.hasNext()) {
            ITracePoint2D tracePoint = it.next();
            if (annotatedPoints.containsKey(tracePoint.getX())) {
                ((AnnotatedTracePoint2D) tracePoint).setAnnotation(annotatedPoints.get(tracePoint));
            }
        }
    }

    /**
     * Removes all data traces.
     */
    public void clearData() {

        this.removeAllTraces();
        traces.clear();
    }

    /**
     * Removes a particular data set.
     *
     * @param dataSetTitle the data set title for removal
     */
    public void clearData(String dataSetTitle) {

        if (traces.containsKey(dataSetTitle)) {
            removeTrace(traces.get(dataSetTitle));
            traces.remove(dataSetTitle);
        }
    }

    /**
     * Shows data points as discs.
     *
     * @param dataSetTitle the data set identifier
     */
    public void toogleDisc(String dataSetTitle) {
        toogleDisc(new String[]{dataSetTitle});
    }

    /**
     * Shows data points as discs.
     *
     * @param dataSetTitles the data set identifier
     */
    public void toogleDisc(String[] dataSetTitles) {

        switchPainter(PAINTERS.DISC);
        updatePainters(dataSetTitles);
    }

    /**
     * Shows data point labels above the threshold.
     *
     * @param dataSetTitle the data set identifier
     */
    public void toogleLabels(String dataSetTitle) {
        toogleLabels(new String[]{dataSetTitle});
    }

    /**
     * Shows data point labels above the threshold.
     *
     * @param dataSetTitles the data set identifier
     */
    public void toogleLabels(String[] dataSetTitles) {

        switchPainter(PAINTERS.LABEL);
        updatePainters(dataSetTitles);
    }

    /**
     * Shows data points as vertical bars.
     *
     * @param dataSetTitle the data set identifier
     */
    public void toogleVerticalBar(String dataSetTitle) {
        toogleVerticalBar(new String[]{dataSetTitle});
    }

    /**
     * Shows data points as vertical bars.
     *
     * @param dataSetTitles the data set identifiers
     */
    public void toogleVerticalBar(String[] dataSetTitles) {

        switchPainter(PAINTERS.BAR);
        updatePainters(dataSetTitles);
    }

    /**
     * Shows data points as spline-connected trace.
     *
     * @param dataSetTitle the data set identifier
     */
    public void toogleSplines(String dataSetTitle) {
        toogleSplines(new String[]{dataSetTitle});
    }

    /**
     * Shows data points as spline-connected trace.
     *
     * @param dataSetTitles the data set identifiers
     */
    public void toogleSplines(String[] dataSetTitles) {

        switchPainter(PAINTERS.SPLINE);
        updatePainters(dataSetTitles);
    }

    /**
     * Shows data points as polyline.
     *
     * @param dataSetTitle the data set identifier
     */
    public void tooglePolyline(String dataSetTitle) {
        tooglePolyline(new String[]{dataSetTitle});
    }

    /**
     * Shows data points as polyline.
     *
     * @param dataSetTitles the data set identifiers
     */
    public void tooglePolyline(String[] dataSetTitles) {

        switchPainter(PAINTERS.POLY);
        updatePainters(dataSetTitles);
    }

    /**
     * Shows the data point annotations.
     *
     * @param dataSetTitle the data set identifiers
     */
    public void toogleAnnotations(String dataSetTitle) {
        toogleAnnotations(new String[]{dataSetTitle});
    }

    /**
     * Shows the data point annotations.
     *
     * @param dataSetTitles the data set identifiers
     */
    public void toogleAnnotations(String[] dataSetTitles) {

        switchPainter(PAINTERS.ANNO);
        updatePainters(dataSetTitles);
    }

    /**
     * Helper method to add/remove painter types to the set.
     *
     * @param painter the painter type to add/remove
     */
    private void switchPainter(PAINTERS painter) {

        if (painterMap.containsKey(painter)) painterMap.remove(painter);
        else painterMap.put(painter, false);
    }

    /**
     * Sets the trace painter for the drawn point traces.
     *
     * @param dataSetTitles the trace identifiers
     */
    private void updatePainters(String[] dataSetTitles) {

        for (String dataSetTitle : dataSetTitles) {

            if (!traces.containsKey(dataSetTitle)) continue;

            boolean force = false;
            if (painterMap.containsKey(PAINTERS.POLY) && painterMap.containsKey(PAINTERS.BAR)) {
                traces.get(dataSetTitle).setTracePainter(new TracePainterPolyline());
                traces.get(dataSetTitle).addTracePainter(new TracePainterVerticalBar(BAR_SIZE, this));
                force = true;
            } else if (painterMap.containsKey(PAINTERS.POLY)) {
                traces.get(dataSetTitle).setTracePainter(new TracePainterPolyline());
                force = true;
            } else if (painterMap.containsKey(PAINTERS.BAR)) {
                traces.get(dataSetTitle).setTracePainter(new TracePainterVerticalBar(BAR_SIZE, this));
                force = true;
            } else if (painterMap.containsKey(PAINTERS.DISC_ONLY)) {
                traces.get(dataSetTitle).setTracePainter(new TracePainterDisc(DISC_SIZE));
            } else if (painterMap.containsKey(PAINTERS.POINT_ONLY)) {
                traces.get(dataSetTitle).setTracePainter(new TracePainterDisc(DISC_SIZE_POINT));
            } else if (painterMap.containsKey(PAINTERS.SPLINE)) {
                traces.get(dataSetTitle).setTracePainter(new TracePainterSpline(PRECISION));
                force = true;
            }

            if (painterMap.containsKey(PAINTERS.ANNO) && force) {
                traces.get(dataSetTitle).addTracePainter(new TracePainterAnnotation(FONT_SIZE));
                painterMap.put(PAINTERS.ANNO, true);
            }
            if (painterMap.containsKey(PAINTERS.LABEL) && force) {
                traces.get(dataSetTitle).addTracePainter(new TracePainterLabel(FONT_SIZE));
                painterMap.put(PAINTERS.LABEL, true);
            }
            if (painterMap.containsKey(PAINTERS.DISC) && force) {
                traces.get(dataSetTitle).addTracePainter(new TracePainterDisc(DISC_SIZE));
                painterMap.put(PAINTERS.DISC, true);
            }
        }
    }

    /**
     * Scales all selected data sets to max. 1000 units.
     *
     * @param titles the selected data sets
     */
    public void scaleTraces(String[] titles) {

        for (String title : titles) {

            if (traces.containsKey(title)) {
                ITrace2D trace = traces.get(title);
                XYList xyList = new XYList();
                double maxY = trace.getMaxY();

                Iterator<ITracePoint2D> it = trace.iterator();
                while (it.hasNext()) {
                    ITracePoint2D dp = it.next();
                    double y = (dp.getY() * SCALE) / maxY;
                    xyList.add(new XYPoint(dp.getX(), y));
                }

                trace.removeAllPoints();
                for (XYPoint dp : xyList) {
                    trace.addPoint(dp.x, dp.y);
                }
                this.removeTrace(traces.get(title));
                this.addTrace(trace);
                updatePainters(titles);
            }
        }
    }
}
