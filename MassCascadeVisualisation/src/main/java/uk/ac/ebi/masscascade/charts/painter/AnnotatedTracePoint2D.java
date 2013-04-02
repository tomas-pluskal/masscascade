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

package uk.ac.ebi.masscascade.charts.painter;

import info.monitorenter.gui.chart.TracePoint2D;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

/**
 * Class implementing an annotated xy data point for the JChart2D library.
 */
public class AnnotatedTracePoint2D extends TracePoint2D {

    private static final long serialVersionUID = 5836384902703695458L;

    private String annotation;

    /**
     * Constructs one data point.
     *
     * @param x the x position
     * @param y the y position
     */
    public AnnotatedTracePoint2D(double x, double y) {

        super(x, y);
        annotation = "";
    }

    /**
     * Constructs one data point.
     *
     * @param dataPoint the xy data point
     */
    public AnnotatedTracePoint2D(XYPoint dataPoint) {

        super(dataPoint.x, dataPoint.y);
        annotation = "";
    }

    /**
     * Adds a String annotation to the data point.
     *
     * @param annotation the annotation
     */
    public void setAnnotation(String annotation) {
        this.annotation = annotation;
    }

    /**
     * Gets the String annotation.
     *
     * @return the string annotation
     */
    public String getAnnotation() {
        return annotation;
    }

    /**
     * Whether the data point is annotated.
     *
     * @return boolean whether data point is annotated
     */
    public boolean isAnnotated() {
        return (annotation != null && !annotation.isEmpty());
    }
}
