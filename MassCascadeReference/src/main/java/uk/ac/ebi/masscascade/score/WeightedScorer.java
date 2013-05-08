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
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.reference.ReferenceSpectrum;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import java.util.HashSet;
import java.util.Set;

/**
 * Class implementing a matching factor according to Stein (1994), consisting of two terms: the dot product between the
 * two spectral vectors and a term that describes the similarity between the shapes of two spectra.
 */
public class WeightedScorer {

    private final Logger LOGGER = Logger.getLogger(WeightedScorer.class);

    private double amu;

    private double wIntensity;
    private double wMass;

    public WeightedScorer(double amu) {

        this.amu = amu;

        this.wIntensity = 0.6;
        this.wMass = 1.5;
    }

    /**
     * Calculates the score between the unknown and reference spectrum.
     *
     * @param unknownSpectrum   the unknown spectrum
     * @param referenceSpectrum the reference spectrum
     * @return the score (0-1000)
     */
    public double getScore(Spectrum unknownSpectrum, ReferenceSpectrum referenceSpectrum) {

        XYPoint unknownBasePeak = unknownSpectrum.getBasePeak().get(0);
        XYPoint referenceBasePeak = referenceSpectrum.getBasePeak();

        XYList unknownCommons = new XYList();
        XYList referenceCommons = new XYList();

        Set<Double> addedMz = new HashSet<>();
        for (Profile profile : unknownSpectrum) {

            XYPoint referenceXY = referenceSpectrum.getMatchingPeak(profile.getMzIntDp(),
                    amu * Constants.PPM / profile.getMzIntDp().x);
            if (referenceXY == null || addedMz.contains(referenceXY.x)) continue;

            addedMz.add(referenceXY.x);
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
        double mf = ((nUnknown * dotProduct) + (nCommons * ratio)) / (nUnknown + nCommons);

//        LOGGER.log(Level.INFO, "Score: " + mf + "; Fd: " + dotProduct + "; Fr: " + ratio);

        return FastMath.round(mf * 1000);
    }
}
