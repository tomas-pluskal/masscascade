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

package uk.ac.ebi.masscascade.utilities;

import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a data set for drawing purposes (JChart2D).
 */
public class DataSet {

    /**
     * Constructs a builder supporting the data set construction process.
     */
    public static class Builder {

        // required parameters
        private XYList xyList;
        private String title = "";

        // optional parameters
        private String xLabel = "";
        private String yLabel = "";
        private Color color = new Color(0, 0, 0, 128);
        private Map<XYPoint, String> annotations = new HashMap<XYPoint, String>();

        /**
         * Constructs a minimal data set.
         *
         * @param xyList the data point container
         * @param title  a data set title
         */
        public Builder(XYList xyList, String title) {

            this.xyList = xyList;
            this.title = title;

            if (xyList.size() == 1) {
                xyList.add(0, new XYPoint(0, 0));
            }
        }

        /**
         * Adds a x label to the data set.
         *
         * @param label the x-axis label
         * @return the compiled builder
         */
        public Builder xLabel(String label) {

            this.xLabel = label;
            return this;
        }

        /**
         * Adds a y label to the data set.
         *
         * @param label the y-axis label.
         * @return the compiled builder
         */
        public Builder yLabel(String label) {

            this.yLabel = label;
            return this;
        }

        /**
         * Sets a specific color for the data set.
         *
         * @param color the defined color
         * @return the compiled builder
         */
        public Builder color(Color color) {

            this.color = color;
            return this;
        }

        /**
         * Sets the data point annotations.
         *
         * @param annotations the data point annotations
         * @return the compiled builder
         */
        public Builder annotations(Map<XYPoint, String> annotations) {

            this.annotations = annotations;
            return this;
        }

        /**
         * Builds the data set.
         *
         * @return the complete data set
         */
        public DataSet build() {

            return new DataSet(this);
        }
    }

    private final XYList xyList;
    private final String xLabel;
    private final String yLabel;
    private final String title;
    private final Color color;
    private final Map<XYPoint, String> annotations;

    /**
     * Constructs a data set using the builder method.
     *
     * @param builder the configured builder
     */
    public DataSet(Builder builder) {

        this.xyList = builder.xyList;
        this.xLabel = builder.xLabel;
        this.yLabel = builder.yLabel;
        this.title = builder.title;
        this.color = builder.color;
        this.annotations = builder.annotations;
    }

    /**
     * Gets the data set.
     *
     * @return the x values
     */
    public final XYList getDataSet() {
        return xyList;
    }

    /**
     * Gets the data point in the data set at position i.
     *
     * @param i the list position
     * @return the data point at position i
     */
    public final XYPoint get(int i) {
        return xyList.get(i);
    }

    /**
     * Gets the x-axis label.
     *
     * @return the x-axis label
     */
    public final String getxLabel() {
        return xLabel;
    }

    /**
     * Gets the y-axis label.
     *
     * @return the y-axis label
     */
    public final String getyLabel() {
        return yLabel;
    }

    /**
     * Gets the data set title.
     *
     * @return the data set title
     */
    public final String getTitle() {
        return title;
    }

    /**
     * Gets the data set color.
     *
     * @return the data set color
     */
    public final Color getColor() {
        return color;
    }

    /**
     * Gets the data point annotations.
     *
     * @return the data point annotations map
     */
    public final Map<XYPoint, String> getAnnotations() {
        return annotations;
    }

    /**
     * Checks whether the data point is annotated.
     *
     * @param dataPoint the data point
     * @return whether the data point is annotated
     */
    public final boolean hasAnnotation(XYPoint dataPoint) {
        return annotations.containsKey(dataPoint);
    }

    /**
     * Returns the annotation of the given data point.
     *
     * @param dataPoint the target data point
     * @return the annotation of the target data point
     */
    public final String getAnnotation(XYPoint dataPoint) {
        return annotations.get(dataPoint);
    }
}
