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

package uk.ac.ebi.masscascade.charts.painter;

import info.monitorenter.gui.chart.ITracePoint2D;
import info.monitorenter.gui.chart.traces.painters.ATracePainter;

import java.awt.*;

/**
 * Class implementing a trace painter for the JChart2D library.
 */
public class TracePainterAnnotation extends ATracePainter {

    private static final long serialVersionUID = -7207482283184018391L;

    /**
     * The implementation for rendering the point as a disc.
     */
    private final PointPainterAnnotation pointPainter;

    /**
     * Creates an instance with a default font size of 10
     */
    public TracePainterAnnotation() {

        this.pointPainter = new PointPainterAnnotation(10);
    }

    /**
     * Creates an instance with the given font size.
     *
     * @param fontSize the font size
     */
    public TracePainterAnnotation(final int fontSize) {

        this.pointPainter = new PointPainterAnnotation(fontSize);
    }

    /**
     * @see info.monitorenter.gui.chart.ITracePainter#endPaintIteration(java.awt.Graphics)
     */
    @Override
    public void endPaintIteration(final Graphics g2d) {

        if (g2d != null) {
            int previousX = this.getPreviousX();
            int previousY = this.getPreviousY();
            if (previousX != Integer.MIN_VALUE || previousY != Integer.MIN_VALUE) {
                this.pointPainter.paintPoint(previousX, previousY, 0, 0, g2d, this.getPreviousPoint());
            }
        }
        this.pointPainter.endPaintIteration(g2d);
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (this.getClass() != obj.getClass()) return false;
        final TracePainterAnnotation other = (TracePainterAnnotation) obj;
        if (this.pointPainter == null) {
            if (other.pointPainter != null) {
                return false;
            }
        } else if (!this.pointPainter.equals(other.pointPainter)) return false;
        return true;
    }

    /**
     * Returns the font size of the annotations.
     *
     * @return font size
     */
    public int getFontSize() {

        return this.pointPainter.getFontSize();
    }

    /**
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.pointPainter == null) ? 0 : this.pointPainter.hashCode());
        return result;
    }

    /**
     * @see info.monitorenter.gui.chart.traces.painters.ATracePainter#paintPoint(int,
     *      int, int, int, java.awt.Graphics,
     *      info.monitorenter.gui.chart.ITracePoint2D)
     */
    @Override
    public void paintPoint(final int absoluteX, final int absoluteY, final int nextX, final int nextY, final Graphics g,
            final ITracePoint2D original) {

        super.paintPoint(absoluteX, absoluteY, nextX, nextY, g, original);
        this.pointPainter.paintPoint(absoluteX, absoluteY, nextX, nextY, g, original);
    }

    /**
     * Sets the font size of the annotations.
     *
     * @param fontSize the font size
     */
    public void setFontSize(final int fontSize) {

        this.pointPainter.setFontSize(fontSize);
    }

    /**
     * @see info.monitorenter.gui.chart.traces.painters.ATracePainter#startPaintIteration(java.awt.Graphics)
     */
    @Override
    public void startPaintIteration(final Graphics g2d) {

        super.startPaintIteration(g2d);
        this.pointPainter.startPaintIteration(g2d);
    }
}
