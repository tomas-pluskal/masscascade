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

package uk.ac.ebi.masscascade.threed;

import org.jzy3d.plot3d.builder.Mapper;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.Map;

public class ThreeDMapper extends Mapper {

    private Map<XYPoint, Double> coordinates;

    public ThreeDMapper(Map<XYPoint, Double> coordinates) {

        super();
        this.coordinates = coordinates;
    }

    @Override
    public double f(double v, double v2) {
        XYPoint xy = new XYPoint(v, v2);
        return coordinates.containsKey(xy) ? coordinates.get(xy) : 0d;
    }
}
