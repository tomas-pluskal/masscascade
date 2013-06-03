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

package uk.ac.ebi.masscascade.identification;

import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.core.container.file.spectrum.FileSpectrumContainer;
import uk.ac.ebi.masscascade.core.spectrum.PseudoSpectrum;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.interfaces.Spectrum;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Adduct;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Class to filter for profiles that have an isotope annotation.
 * * <ul>
 * <li>Parameter <code> TIME WINDOW </code>- The scan window for the alignment in scans.</li>
 * <li>Parameter <code> REFERENCE FILE </code>- The input raw reference container.</li>
 * <li>Parameter <code> SPECTRUM FILE </code>- The input spectrum container.</li>
 * </ul>
 */
public class IsotopeKeeper extends CallableTask {

    private SpectrumContainer spectrumContainer;

    /**
     * Constructs an isotope keeper task.
     *
     * @param params the parameter map holding all required task parameters
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *          if the task fails
     */
    public IsotopeKeeper(ParameterMap params) throws MassCascadeException {

        super(IsotopeKeeper.class);
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

        spectrumContainer = params.get(Parameter.SPECTRUM_CONTAINER, SpectrumContainer.class);
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .RawContainer} with the processed data.
     *
     * @return the spectrum container with the processed data
     */
    @Override
    public SpectrumContainer call() {

        String id = spectrumContainer.getId() + IDENTIFIER;
        SpectrumContainer outSpectrumContainer = spectrumContainer.getBuilder().newInstance(SpectrumContainer.class, id,
                spectrumContainer.getWorkingDirectory());

        XYList xyList;
        Range rtRange;
        Profile exProfile;
        Set<Profile> profileSet;

        for (Spectrum spectrum : spectrumContainer) {

            Set<Integer> idSet = new HashSet<>();
            List<Integer> adductParentIds = new ArrayList<>();

            for (Profile profile : ((PseudoSpectrum) spectrum).getProfileList()) {

                if (profile.hasProperty(PropertyManager.TYPE.Isotope)) {
                    idSet.add(profile.getId());
                    for (Property propI : profile.getProperty(PropertyManager.TYPE.Isotope)) {
                        if (propI.getValue(Integer.class) == 0 && profile.hasProperty(PropertyManager.TYPE.Adduct))
                            adductParentIds.add(profile.getId());
                    }
                }
            }

            for (Profile profile : ((PseudoSpectrum) spectrum).getProfileList()) {

                if (profile.hasProperty(PropertyManager.TYPE.Adduct)) {
                    for (Property propA : profile.getProperty(PropertyManager.TYPE.Adduct))
                        if (adductParentIds.contains(((Adduct) propA).getParentId())) idSet.add(profile.getId());
                }
            }

            rtRange = new ExtendableRange();
            xyList = new XYList();
            profileSet = new HashSet<>();

            for (int profileId : idSet) {

                exProfile = spectrum.getProfile(profileId);

                if (exProfile == null) continue;

                profileSet.add(exProfile);
                xyList.add(exProfile.getMzIntDp());
                rtRange.extendRange(exProfile.getRetentionTime());
            }

            if (xyList.isEmpty()) continue;

            outSpectrumContainer.addSpectrum(
                    new PseudoSpectrum(spectrum.getIndex(), xyList, rtRange, spectrum.getRetentionTime(), profileSet));
        }

        outSpectrumContainer.finaliseFile();
        return outSpectrumContainer;
    }
}
