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

package uk.ac.ebi.masscascade.io;

import com.google.common.collect.Lists;
import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.core.PropertyManager;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Profile;
import uk.ac.ebi.masscascade.interfaces.Property;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.ProfileContainer;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.pride.jmztab.MzTabFile;
import uk.ac.ebi.pride.jmztab.MzTabParsingException;
import uk.ac.ebi.pride.jmztab.model.SmallMolecule;

import java.io.File;
import java.io.FileWriter;
import java.util.Set;

public class MzTabWriter extends CallableTask {

    private String outPath;
    private Container container;

    public MzTabWriter(ParameterMap params) {

        super(MzTabWriter.class);
        setParameters(params);
    }

    /**
     * Sets the task class variables using the parameter map.
     *
     * @param params the parameter map containing the <code> Parameter </code> to <code> Object </code> relations.
     */
    @Override
    public void setParameters(ParameterMap params) {

        outPath = params.get(Parameter.OUTPUT_DIRECTORY, String.class);

        try {
            container = params.get(Parameter.PROFILE_CONTAINER, ProfileContainer.class);
        } catch (MassCascadeException exception) {
            container = params.get(Parameter.SPECTRUM_CONTAINER, SpectrumContainer.class);
        }
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .Container} with the processed data.
     *
     * @return the container with the processed data
     */
    @Override
    public Container call() {

        try {
            MzTabFile mzTab = new MzTabFile();

            for (Profile profile : container.profileIterator()) {

                if (!profile.hasProperty(PropertyManager.TYPE.Identity)) continue;

                SmallMolecule molecule = new SmallMolecule();
                molecule.setMassToCharge(profile.getMz());
                molecule.setRetentionTime(Lists.newArrayList(profile.getRetentionTime()));
                molecule.setReliability(2);

                Set<Property> identities = profile.getProperty(PropertyManager.TYPE.Identity);
                for (Property id : identities) {
                    Identity identity = (Identity) id;
                    molecule.setDescription(identity.getName());
                    molecule.setIdentifier(Lists.newArrayList(identity.getId()));
                    String notation = identity.getNotation();
                    if (notation.startsWith("InChI")) {
                        molecule.setInchiKey(Lists.newArrayList(notation));
                        molecule.setSmiles(Lists.newArrayList(""));
                    } else {
                        molecule.setInchiKey(Lists.newArrayList(""));
                        molecule.setSmiles(Lists.newArrayList(notation));
                    }
                    molecule.setAbundance(1, profile.getIntensity(), 0.0, 0.0);
                }

                mzTab.addSmallMolecule(molecule);
            }

            File mzTabFile = new File(outPath + File.separator + container.getId() + ".mzTab");
            FileWriter writer = new FileWriter(mzTabFile);
            writer.write(mzTab.toMzTab());
            TextUtils.close(writer);
        } catch (Exception exception) {
            LOGGER.log(Level.ERROR, "Error while parsing / writing mzTab file: " + exception.getMessage());
        }

        return null;
    }
}
