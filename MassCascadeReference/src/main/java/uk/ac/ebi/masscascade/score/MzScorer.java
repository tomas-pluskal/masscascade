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

package uk.ac.ebi.masscascade.score;

import org.apache.commons.math3.util.FastMath;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.reference.ReferenceSpectrum;
import uk.ac.ebi.masscascade.utilities.range.ToleranceRange;

public class MzScorer {

    private final Logger LOGGER = Logger.getLogger(MzScorer.class);

    private double ppm;

    public MzScorer(double ppm) {
        this.ppm = ppm;
    }

    public double score(Profile unknownProfile, ReferenceSpectrum referenceSpectrum) {

        if (referenceSpectrum.getMzIntList().size() != 1) return 0d;

        double refMz = referenceSpectrum.getBasePeak().x;
        Range refRange = new ToleranceRange(refMz, ppm);

        double score = 0;
        if (refRange.contains(unknownProfile.getMz())) {
            score = FastMath.abs(unknownProfile.getMz() - refMz) * Constants.PPM / unknownProfile.getMz();
            score = FastMath.round(-0.001 * score + 1000);
        }

        return score;
    }
}
