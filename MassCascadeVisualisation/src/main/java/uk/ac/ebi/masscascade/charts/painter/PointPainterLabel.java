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
import info.monitorenter.gui.chart.pointpainters.APointPainter;
import uk.ac.ebi.masscascade.utilities.math.MathUtils;

import java.awt.*;

/**
 * Class implementing an annotated xy data point painter for the JChart2D library.
 */
public class PointPainterLabel extends APointPainter<PointPainterLabel> {

    private final static int OFFSET_Y = 3;
    private final static int FONT_SIZE = 10;

    private static final long serialVersionUID = -2243935385165018795L;

    /**
     * The font size of the annotation
     */
    private int fontSize;
    private Font font;

    /**
     * Creates an instance with a default font size of 10
     */
    public PointPainterLabel() {
        this.setFontSize(FONT_SIZE);
    }

    /**
     * Creates an instance with the given font size.
     *
     * @param size the font size to use.
     */
    public PointPainterLabel(final int size) {
        this.setFontSize(size);
    }

    /**
     * @see info.monitorenter.gui.chart.pointpainters.APointPainter#equals(Object)
     */
    @Override
    public boolean equals(final Object obj) {

        if (this == obj) return true;
        if (!super.equals(obj)) return false;
        if (this.getClass() != obj.getClass()) return false;
        final PointPainterLabel other = (PointPainterLabel) obj;
        if (this.fontSize != other.fontSize) return false;

        return true;
    }

    /**
     * Returns the font size of the annotations.
     *
     * @return the font size of the annotations
     */
    public int getFontSize() {
        return this.fontSize;
    }

    /**
     * @see info.monitorenter.gui.chart.pointpainters.APointPainter#hashCode()
     */
    @Override
    public int hashCode() {

        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + this.fontSize;

        return result;
    }

    private ITracePoint2D lowestOriginal;

    /**
     * @see info.monitorenter.gui.chart.IPointPainter#paintPoint(int, int, int,
     *      int, java.awt.Graphics, info.monitorenter.gui.chart.ITracePoint2D)
     */
    public void paintPoint(final int absoluteX, final int absoluteY, final int nextX, final int nextY, final Graphics g,
            final ITracePoint2D original) {

        if (nextY < absoluteY) {
            lowestOriginal = original;
        } else if (lowestOriginal != null) {
            if (lowestOriginal.getScaledY() >= 0.3) {
                g.setFont(font);
                String value = MathUtils.roundToThreeDecimals(lowestOriginal.getX()) + "";
                int stringWidth = g.getFontMetrics(font).stringWidth(value);
                g.drawString(value, absoluteX - (stringWidth / 2), absoluteY - OFFSET_Y);
            }
            lowestOriginal = null;
        }
    }

    /**
     * Sets the font size of the annotations.
     *
     * @param fontSize the font size of the annotations
     */
    public void setFontSize(final int fontSize) {

        this.fontSize = fontSize;
        font = new Font("Arial", Font.PLAIN, fontSize);
    }
}