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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import uk.ac.ebi.masscascade.core.PropertyType;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.Feature;
import uk.ac.ebi.masscascade.interfaces.container.Container;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.properties.Identity;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.pride.jmztab.model.*;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.*;

/**
 * Class implementing a mzTab writer. Features, either from a {@link uk.ac.ebi.masscascade.interfaces.container.FeatureContainer}
 * or {@link uk.ac.ebi.masscascade.interfaces.container.FeatureSetContainer},
 * with identified features, are written out in mzTab format.
 * <ul>
 * <li>Parameter <code> OUTPUT_DIRECTORY </code>- The m/z tolerance in ppm.</li>
 * <li>Parameter <code> FEATURE_CONTAINER </code>- The input feature container.</li>
 * ################### or ###################
 * <li>Parameter <code> FEATURE_SET_CONTAINER </code>- The input feature set container.</li>
 * </ul>
 */
public class MzTabWriter extends CallableTask {

    private String outPath;
    private Container container;

    /**
     * Constructs an mzTab writer task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException if the task fails
     */
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
            container = params.get(Parameter.FEATURE_CONTAINER, FeatureContainer.class);
        } catch (MassCascadeException exception) {
            container = params.get(Parameter.FEATURE_SET_CONTAINER, FeatureSetContainer.class);
        }
    }

    /**
     * Executes the task. The <code> Callable </code> returns a {@link uk.ac.ebi.masscascade.interfaces.container
     * .Container} with the processed data.
     *
     * @return null; the task writes to disk
     */
    @Override
    public Container call() {

        String id = TextUtils.cleanId(container.getId())[0];
        MZTabFile mzTabFile = new MZTabFile(convertMetadata(id));
        mzTabFile.setSmallMoleculeColumnFactory(MZTabColumnFactory.getInstance(Section.Small_Molecule));
        for (SmallMolecule sm : smallMolecule(container, mzTabFile.getMetadata())) {
            mzTabFile.addSmallMolecule(sm);
        }

        try {
            File file = new File(outPath + File.separator + id);
            BufferedOutputStream bout = new BufferedOutputStream(new FileOutputStream(file));
            mzTabFile.printMZTab(bout);
            bout.close();
        } catch (IOException exception) {
            exception.printStackTrace();
        }
        return null;
    }

    protected Metadata convertMetadata(String id) {

        Metadata metadata = new Metadata();
        metadata.setMZTabID(id);
        metadata.setDescription("Reference Standard: LC-MS/MS Fragmentation");

        metadata.addSampleProcessingParam(1, new CVParam("MS", "MS:1000544", "Conversion to mzML", ""));

        SortedMap<Integer, Software> softwareMap = new TreeMap<>();
        Software software1 = new Software(1);
        software1.setParam(new CVParam("MS", "MS:1002205", "ProteoWizard msconvert", ""));
        software1.addSetting("Peak Picking MS1");
        Software software2 = new Software(2);
        software2.setParam(new CVParam("MS", "MS:1001457", "data processing software", "MassCascade-KNIME"));
        softwareMap.put(1, software1);
        softwareMap.put(2, software2);
        metadata.setSoftwareMap(softwareMap);

        SortedMap<Integer, Instrument> instrumentMap = new TreeMap<>();
        Instrument instrument = new Instrument(1);
        instrument.setName(new CVParam("MS", "MS:1000483", "Thermo Fisher Scientific instrument model", "LTQ Orbitrap Velos"));
        instrument.setSource(new CVParam("MS", "MS:1000008", "Ionization Type", "ESI"));
        instrument.setAnalyzer(new CVParam("MS", "MS:1000443", "Mass Analyzer Type", "Orbitrap"));
        instrument.setDetector(new CVParam("MS", "MS:1000453", "Detector", "Dynode Detector"));
        instrumentMap.put(1, instrument);
        metadata.setInstrumentMap(instrumentMap);

        Contact contact = new Contact(1);
        contact.setName("Stephan Beisken");
        contact.setEmail("beiken@ebi.ac.uk");
        contact.setAffiliation("European Bioinformatics Institute (EMBL-EBI)");
        metadata.addContact(contact);
        metadata.addUri(uri("http://www.ebi.ac.uk/metabolights/MTBLS38"));

        metadata.addMsRunFormat(1, new CVParam("MS", "MS:1000584", "Proteomics Standards Inititative mzML file format", "mzML file"));
        metadata.addMsRunLocation(1, url("ftp://ftp.ebi.ac.uk/pub/databases/metabolights/studies/public/MTBLS38/" + id + ".mzML"));
        metadata.addMsRunIdFormat(1, new CVParam("MS", "MS:1000767", "Native spectrum identifier format", ""));
        metadata.addMsRunFragmentationMethod(1, new CVParam("MS", "MS:1000133", "Collision-induced dissociation", ""));

        return metadata;
    }

    protected List<SmallMolecule> smallMolecule(Container container, Metadata metadata) {

        List<SmallMolecule> sms = new ArrayList<>();
        Set<String> duplicates = new HashSet<>();
        for (Feature feature : container.featureIterator()) {

            if (!feature.hasProperty(PropertyType.Identity)) continue;

            Set<Identity> identities = feature.getProperty(PropertyType.Identity, Identity.class);
            for (Identity identity : identities) {

                if (!feature.hasMsnSpectra(Constants.MSN.MS2) ||
                        !identity.getName().contains(container.getId().substring(0, 4))) continue;

                for (Feature msnProfile : feature.getMsnSpectra(Constants.MSN.MS2).get(0)) {
                    if (!msnProfile.hasProperty(PropertyType.Identity)) continue;

                    for (Identity msnident : msnProfile.getProperty(PropertyType.Identity, Identity.class)) {
                        if (identity.getId().contains(msnident.getSource())) {
                            String dupString = "" + msnident.getNotation();
                            if (duplicates.contains(dupString)) continue;
                            duplicates.add(dupString);

                            SmallMolecule smallMolecule = new SmallMolecule(metadata);
                            smallMolecule.setIdentifier("Fragment of " + msnident.getSource());
                            smallMolecule.setSmiles(msnident.getNotation());
                            // smallMolecule.setDescription(msnident.getName());

                            smallMolecule.setExpMassToCharge(msnProfile.getMz());
                            smallMolecule.addRetentionTime(msnProfile.getRetentionTime());
                            smallMolecule.setCharge(+1);

                            smallMolecule.setDatabase("ChEBI");
                            smallMolecule.setDatabaseVersion("109");
                            smallMolecule.setReliability(Reliability.High);
                            smallMolecule.setURI(uri("http://www.ebi.ac.uk/metabolights/MTBLS38"));

                            sms.add(smallMolecule);
                        }
                    }
                }
            }
        }

        Collections.sort(sms, new Comparator<SmallMolecule>() {
            @Override
            public int compare(SmallMolecule o1, SmallMolecule o2) {
                if (o1.getExpMassToCharge() < o2.getExpMassToCharge()) {
                    return -1;
                } else if (o1.getExpMassToCharge() > o2.getExpMassToCharge()) {
                    return 1;
                }
                return 0;
            }
        });

        return sms;
    }

    private URI uri(String identifier) {
        URI uri = null;
        try {
            uri = new URI(identifier);
        } catch (URISyntaxException exception) {
            exception.printStackTrace();
        }
        return uri;
    }

    private URL url(String identifier) {
        URL url = null;
        try {
            url = new URL(identifier);
        } catch (MalformedURLException exception) {
            exception.printStackTrace();
        }
        return url;
    }
}