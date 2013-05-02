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

package uk.ac.ebi.masscascade.filter;

import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.HashSet;
import java.util.Set;

/**
 * Class implementing a spectrum filter.
 * <ul>
 * <li>Parameter <code> MZ_RANGE </code>- The mz range used for filtering in amu.</li>
 * <li>Parameter <code> TIME RANGE </code>- The time range used for filtering in seconds.</li>
 * <li>Parameter <code> MIN. INTENSITY</code>- The minimum intensity for a profile.</li>
 * <li>Parameter <code> SPECTRUM FILE </code>- The input spectrum container.</li>
 * </ul>
 */
public class SpectrumFilter extends CallableTask {

    private Range timeRange;
    private Range mzRange;
    private double minIntensity;
    private boolean keepIsotopes;
    private SpectrumContainer spectrumContainer;

    /**
     * Constructs a spectrum filter task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public SpectrumFilter(ParameterMap params) throws MassCascadeException {

        super(SpectrumFilter.class);
        setParameters(params);
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the parameter map does not contain all variables required by this class
     */
    @Override
    public void setParameters(ParameterMap params) throws MassCascadeException {

        mzRange = params.get(Parameter.MZ_RANGE, ExtendableRange.class);
        timeRange = params.get(Parameter.TIME_RANGE, ExtendableRange.class);
        minIntensity = params.get(Parameter.MIN_PROFILE_INTENSITY, Double.class);
        keepIsotopes = params.get(Parameter.KEEP_ISOTOPES, Boolean.class);
        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, SpectrumContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .SpectrumContainer} with the processed data.
     *
     * @return the spectrum container with the processed data
     */
    @Override
    public SpectrumContainer call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer outSpectrumContainer = spectrumContainer.getBuilder().newInstance(SpectrumContainer.class, id,
                spectrumContainer.getWorkingDirectory());

        for (Spectrum spectrum : spectrumContainer) {
            if (timeRange.contains(spectrum.getRetentionTime())) {
                Set<Profile> outProfiles = new HashSet<>();
                Range rtRange = null;
                XYList mzIntList = new XYList();
                for (Profile profile : spectrum) {
                    if (mzRange.contains(profile.getMz())) {
                        if (profile.getIntensity() >= minIntensity || (keepIsotopes && profile.hasProperty(
                                PropertyManager.TYPE.Isotope))) {
                            if (rtRange == null) rtRange = new ExtendableRange(profile.getRetentionTime());
                            else rtRange.extendRange(profile.getRetentionTime());
                            outProfiles.add(profile);
                            mzIntList.add(profile.getMzIntDp());
                        }
                    }
                }

                if (outProfiles.size() == 0) continue;

                Spectrum outSpectrum =
                        new PseudoSpectrum(spectrum.getIndex(), mzIntList, rtRange, spectrum.getRetentionTime(),
                                outProfiles);
                outSpectrumContainer.addSpectrum(outSpectrum);
            }
        }

        outSpectrumContainer.finaliseFile();
        return outSpectrumContainer;
    }
}
