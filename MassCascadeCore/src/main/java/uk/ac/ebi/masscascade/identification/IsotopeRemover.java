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

package uk.ac.ebi.masscascade.identification;

import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.core.file.spectrum.FileSpectrumContainer;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.HashSet;
import java.util.Set;

/**
 * Class to remove profiles that have an isotope annotation. The method does not remove the major isotope species.
 * <ul>
 * <li>Parameter <code> TIME WINDOW </code>- The scan window for the alignment in scans.</li>
 * <li>Parameter <code> REFERENCE FILE </code>- The input raw reference container.</li>
 * <li>Parameter <code> PROFILE FILE </code>- The input profile container.</li>
 * </ul>
 */
public class IsotopeRemover extends CallableTask {

    private SpectrumContainer spectrumContainer;

    /**
     * Constructor for an isotope remover task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public IsotopeRemover(ParameterMap params) throws MassCascadeException {

        super(IsotopeRemover.class);

        setParameters(params);
    }

    /**
     * Sets the parameters for the isotope remover task.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, FileSpectrumContainer.class);
    }

    /**
     * Executes the isotope remover task.
     *
     * @return the deisotoped peak collection
     */
    public SpectrumContainer call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer
                outSpectrumContainer = new FileSpectrumContainer(id, spectrumContainer.getWorkingDirectory());

        XYList xyList;
        Range rtRange;
        Set<Profile> profileSet;

        for (PseudoSpectrum spectrum : spectrumContainer) {

            rtRange = new ExtendableRange();
            xyList = new XYList();
            profileSet = new HashSet<Profile>();

            for (Profile profile : spectrum.getProfileList()) {
                if (profile.hasProperty(PropertyManager.TYPE.Isotope)) {
                    for (Property prop : profile.getProperty(PropertyManager.TYPE.Isotope)) {
                        if (prop.getValue(Integer.class) == 0) {
                            profileSet.add(profile);
                            xyList.add(profile.getMzIntDp());
                            rtRange.extendRange(profile.getRetentionTime());
                        }
                    }
                } else {
                    profileSet.add(profile);
                    xyList.add(profile.getMzIntDp());
                    rtRange.extendRange(profile.getRetentionTime());
                }
            }
            outSpectrumContainer.addSpectrum(
                    new PseudoSpectrum(spectrum.getIndex(), xyList, rtRange, spectrum.getRetentionTime(),
                            profileSet));
        }

        outSpectrumContainer.finaliseFile();
        return outSpectrumContainer;
    }
}
