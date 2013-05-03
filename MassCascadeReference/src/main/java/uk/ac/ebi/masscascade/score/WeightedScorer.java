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
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.reference.ReferenceSpectrum;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

public class WeightedScorer {

    private final Logger LOGGER = Logger.getLogger(WeightedScorer.class);

    private double ppm;

    private double wIntensity;
    private double wMass;

    public WeightedScorer(double ppm) {

        this.ppm = ppm;

        this.wIntensity = 0.6;
        this.wMass = 1.5;
    }

    public double getScore(Spectrum unknownSpectrum, ReferenceSpectrum referenceSpectrum) {

        XYPoint unknownBasePeak = unknownSpectrum.getBasePeak().get(0);
        XYPoint referenceBasePeak = referenceSpectrum.getBasePeak();

        XYList unknownCommons = new XYList();
        XYList referenceCommons = new XYList();

        for (Profile profile : unknownSpectrum) {
            XYPoint referenceXY = referenceSpectrum.getMatchingPeak(profile.getMzIntDp(), ppm);
            if (referenceXY == null) continue;

            unknownCommons.add(new XYPoint(profile.getMz(), profile.getIntensity() / unknownBasePeak.y));
            referenceCommons.add(new XYPoint(referenceXY.x, referenceXY.y / referenceBasePeak.y));
        }

        int nCommons = unknownCommons.size();
        if (nCommons == 0) return 0d;

        // TODO: merge dot product and ratio loops

        // dot product
        double numerator = 0;
        double denominatorUnknown = 0;
        double denominatorReference = 0;
        for (int i = 0; i < nCommons; i++) {
            XYPoint unknownXY = unknownCommons.get(i);
            XYPoint referenceXY = referenceCommons.get(i);
            double unknownW = FastMath.pow(unknownXY.x, wMass) * FastMath.pow(unknownXY.y, wIntensity);
            double referenceW = FastMath.pow(referenceXY.x, wMass) * FastMath.pow(referenceXY.y, wIntensity);

            numerator += (unknownW * referenceW);
            denominatorUnknown += (unknownW * unknownW);
            denominatorReference += (referenceW * referenceW);
        }

        double dotProduct = (numerator * numerator) / (denominatorUnknown * denominatorReference);

        // ratio
        double sumRatio = 0;
        XYPoint unknownXY = unknownCommons.get(0);
        XYPoint referenceXY = referenceCommons.get(0);
        double pUnknownW = FastMath.pow(unknownXY.x, wMass) * FastMath.pow(unknownXY.y, wIntensity);
        double pReferenceW = FastMath.pow(referenceXY.x, wMass) * FastMath.pow(referenceXY.y, wIntensity);
        for (int i = 1; i < nCommons; i++) {
            unknownXY = unknownCommons.get(i);
            referenceXY = referenceCommons.get(i);
            double unknownW = FastMath.pow(unknownXY.x, wMass) * FastMath.pow(unknownXY.y, wIntensity);
            double referenceW = FastMath.pow(referenceXY.x, wMass) * FastMath.pow(referenceXY.y, wIntensity);

            double ratio = (pUnknownW / unknownW) * (referenceW / pReferenceW);
            if (ratio > 1) ratio = 1 / ratio;
            sumRatio += ratio;

            pUnknownW = unknownW;
            pReferenceW = referenceW;
        }

        double ratio = sumRatio / nCommons;

        // matching factor
        int nUnknown = unknownSpectrum.size();
        double mf = (nUnknown * dotProduct) + (nCommons * ratio) / (nUnknown + nCommons);

        LOGGER.log(Level.INFO, "Score: " + mf + "; Fd: " + dotProduct + "; Fr: " + ratio);

        return FastMath.round(mf * 1000);
    }
}
