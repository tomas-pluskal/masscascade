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

package uk.ac.ebi.masscascade.compound;

import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;

import java.util.ArrayList;
import java.util.List;

public class CompoundSpectrumGenerator {

    private double missingness;
    private List<SpectrumContainer> spectraContainer;

    public CompoundSpectrumGenerator() {
        missingness = 0;
    }

    public void setMissingness(double missingness) {
        this.missingness = missingness;
    }

    public double getMissingness() {
        return missingness;
    }

    public List<CompoundSpectrum> getSpectra(List<SpectrumContainer> spectraContainer) {

        List<CompoundSpectrum> compoundSpectra = new ArrayList<>();

        for (SpectrumContainer spectrumContainer : spectraContainer) {
            for (Spectrum spectrum : spectrumContainer) {
                resolveCompoundSpectra(spectrum);
            }
        }

        return compoundSpectra;
    }

    private void resolveCompoundSpectra(Spectrum spectrum) {


    }
}
