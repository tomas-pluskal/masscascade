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

package uk.ac.ebi.masscascade.brush;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.brush.judge.ElementJudge;
import uk.ac.ebi.masscascade.brush.judge.FragmentationJudge;
import uk.ac.ebi.masscascade.brush.judge.IdentityRelationJudge;
import uk.ac.ebi.masscascade.brush.judge.IsotopeJudge;
import uk.ac.ebi.masscascade.brush.judge.Judge;
import uk.ac.ebi.masscascade.compound.CompoundSpectrum;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;

/**
 * Judge aggregator to rationalise and filter a list of compound spectrum.
 */
public class SpectrumCourt implements Callable<List<CompoundSpectrum>> {

    private static final Logger LOGGER = Logger.getLogger(SpectrumCourt.class);

    private List<CompoundSpectrum> compoundSpectra;
    private final List<Judge> judges;

    /**
     * Constructs a new court for a list of compound spectra.
     *
     * @param compoundSpectra the list of compound spectra
     */
    public SpectrumCourt(List<CompoundSpectrum> compoundSpectra) {

        this.compoundSpectra = compoundSpectra;
        judges = new ArrayList<>();
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     */
    public void setParameters(ParameterMap params) {

        if (params.get(Parameter.ELEMENT_FILTER, Boolean.class)) judges.add(new ElementJudge());
        if (params.get(Parameter.ISOTOPE_FILTER, Boolean.class)) judges.add(new IsotopeJudge());
        if (params.get(Parameter.FRAGMENTATION_FILTER, Boolean.class)) judges.add(new FragmentationJudge());
        if (params.get(Parameter.RELATION_FILTER, Boolean.class)) judges.add(new IdentityRelationJudge());
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .Container} with the processed data.
     *
     * @return the container with the processed data
     */
    @Override
    public List<CompoundSpectrum> call() {

        if (compoundSpectra == null || compoundSpectra.isEmpty()) {
            LOGGER.log(Level.DEBUG, "Empty compound spectra array -- return.");
            return compoundSpectra;
        }

        Iterator<Judge> iter = judges.iterator();
        do {
            Judge judge = iter.next();
            compoundSpectra = judge.judge(compoundSpectra);
        } while (iter.hasNext());

        return compoundSpectra;
    }

    /**
     * Returns an array of integers indicating the number of removed or filtered compound spectra by a particular judge.
     * The number at an index corresponds to the judge at the same index in the list of judges.
     *
     * @return the array of integers indicating the number of removed or filtered compound spectra
     */
    public int[] getRemoved() {

        int[] removed = new int[judges.size()];
        int i = 0;
        for (Judge judge : judges) {
            removed[i++] = judge.removed();
        }

        return removed;
    }
}
