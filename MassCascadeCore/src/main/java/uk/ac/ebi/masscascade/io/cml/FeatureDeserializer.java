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

package uk.ac.ebi.masscascade.io.cml;

import com.google.common.collect.TreeMultimap;
import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.container.file.feature.FileFeatureContainer;
import uk.ac.ebi.masscascade.interfaces.container.FeatureContainer;
import uk.ac.ebi.masscascade.parameters.Constants;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * Class for CML-based feature deserialization.
 */
public class FeatureDeserializer extends ACmlDeserializer {

    private static final Logger LOGGER = Logger.getLogger(FeatureDeserializer.class);

    // tmp variables
    String tmpValue = "";

    // spectra variables
    private double retentionTime;
    private Long pointer;
    private int id;
    private String dataFile = "";
    private Constants.ION_MODE ionMode;
    private TreeMultimap<Double, Integer> peakTimes;
    private LinkedHashMap<Integer, Long> peakNumbers;

    /**
     * Constructs a feature file read task.
     *
     * @param id      a task identifier
     * @param cmlFile a cml file to read
     */
    public FeatureDeserializer(String id, File cmlFile, String workingDirectory) throws IOException {

        super(id, cmlFile, workingDirectory);
    }

    /**
     * Constructs a feature file read task.
     *
     * @param cmlString a cml string to read
     * @throws IOException unexpected behaviour
     */
    public FeatureDeserializer(String cmlString, String workingDirectory) throws IOException {

        super(cmlString, workingDirectory);
    }

    /**
     * Constructs a feature file read task.
     *
     * @param stream input stream for cml document
     * @throws IOException unexpected behaviour
     */
    public FeatureDeserializer(InputStream stream, String workingDirectory) throws IOException {

        super(stream, workingDirectory);
    }

    /**
     * Parses a CML file.
     *
     * @return the parsed information
     */
    public FeatureContainer getFile() throws XMLStreamException {

        // event callbacks
        boolean isMetaDataList = false;
        boolean isMetaData = false;
        boolean isFileName = false;
        boolean isIonMode = false;
        boolean isDataFile = false;
        boolean isSpectrumList = false;
        boolean isSpectrum = false;
        boolean isPeakList = false;
        boolean isPeak = false;

        peakNumbers = new LinkedHashMap<Integer, Long>();
        peakTimes = TreeMultimap.create();

        while (parser.peek() != null) {

            XMLEvent event = parser.nextEvent();
            switch (event.getEventType()) {

                case XMLStreamConstants.START_DOCUMENT:
                    break;
                case XMLStreamConstants.END_DOCUMENT:
                    parser.close();
                    break;
                case XMLStreamConstants.START_ELEMENT:
                    StartElement element = event.asStartElement();
                    if (element.getName().getLocalPart().equals(METADATALIST)) {
                        isMetaDataList = true;
                    } else if (element.getName().getLocalPart().equals(METADATA)) {
                        isMetaData = true;
                    } else if (element.getName().getLocalPart().equals(SPECTRUMLIST)) {
                        isSpectrumList = true;
                    } else if (element.getName().getLocalPart().equals(SPECTRUM)) {
                        isSpectrum = true;
                    } else if (element.getName().getLocalPart().equals(PEAKLIST)) {
                        isPeakList = true;
                    } else if (element.getName().getLocalPart().equals(PEAK)) {
                        isPeak = true;
                    }
                    for (Iterator<Attribute> attributes = element.getAttributes(); attributes.hasNext(); ) {
                        Attribute attribute = attributes.next();
                        if (isMetaDataList && isMetaData) {
                            if (attribute.getValue().equals(FILENAME)) {
                                isFileName = true;
                            } else if (attribute.getValue().equals(DATAFILE)) {
                                isDataFile = true;
                            } else if (attribute.getValue().equals(IONMODE)) {
                                isIonMode = true;
                            } else if (attribute.getName().getLocalPart().equals(CONTENT)) {
                                content = attribute.getValue();
                            }
                            if (isFileName && content != null) {
                                fileName = content;
                                content = null;
                                isFileName = false;
                            } else if (isDataFile && content != null) {
                                dataFile = workingDirectory + File.separator + content;
                                content = null;
                                isDataFile = false;
                            } else if (isIonMode && content != null) {
                                ionMode = Constants.ION_MODE.valueOf(content);
                                isIonMode = false;
                                content = null;
                            }
                        } else if (isSpectrumList && isSpectrum && !isPeakList) {
                            if (attribute.getName().getLocalPart().equals(ID)) {
                                // do nothing
                            } else if (attribute.getName().getLocalPart().equals(RT)) {
                                retentionTime = Double.parseDouble(attribute.getValue());
                            }
                        } else if (isPeakList && isPeak) {
                            if (attribute.getName().getLocalPart().equals(ID)) {
                                id = Integer.parseInt(attribute.getValue());
                            } else if (attribute.getName().getLocalPart().equals(POINTER)) {
                                pointer = Long.parseLong(attribute.getValue());
                            }
                        }
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    Characters characters = event.asCharacters();
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (event.asEndElement().getName().getLocalPart().equals(METADATALIST)) {
                        isMetaDataList = false;
                    } else if (event.asEndElement().getName().getLocalPart().equals(METADATA)) {
                        isMetaData = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(SPECTRUMLIST)) {
                        // hack to avoid EOFException thrown by the loop condition
                        parser.close();
                        return new FileFeatureContainer(fileName, ionMode, dataFile, peakTimes, peakNumbers);
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(SPECTRUM)) {
                        isSpectrum = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(PEAKLIST)) {
                        isPeakList = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(PEAK)) {
                        peakNumbers.put(id, pointer);
                        peakTimes.put(retentionTime, id);
                        isPeak = false;
                    }
                    break;
                case XMLStreamConstants.ATTRIBUTE:
                    break;
                default:
                    break;
            }
        }

        return new FileFeatureContainer(fileName, ionMode, dataFile, peakTimes, peakNumbers);
    }
}
