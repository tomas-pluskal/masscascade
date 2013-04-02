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

import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.container.file.spectrum.FileSpectrumContainer;
import uk.ac.ebi.masscascade.interfaces.container.SpectrumContainer;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Class for CML-based spectra deserialization.
 */
public class SpectrumDeserializer extends ACmlDeserializer {

    private static final Logger LOGGER = Logger.getLogger(SpectrumDeserializer.class);

    // spectra variables
    private int id;
    private Long pointer;
    private String dataFile;
    private LinkedHashMap<Integer, Long> spectraNumbers;
    private List<XYPoint> basePeaks;

    /**
     * Constructs a spectrum file read task.
     *
     * @param id      a task identifier
     * @param cmlFile a cml file to read
     */
    public SpectrumDeserializer(String id, File cmlFile, String workingDirectory) throws IOException {
        super(id, cmlFile, workingDirectory);
    }

    /**
     * Constructs a spectrum file read task.
     *
     * @param cmlString a cml string to read
     * @throws IOException unexpected behaviour
     */
    public SpectrumDeserializer(String cmlString, String workingDirectory) throws IOException {
        super(cmlString, workingDirectory);
    }

    /**
     * Constructs a spectrum file read task.
     *
     * @param stream input stream for cml document
     * @throws IOException unexpected behaviour
     */
    public SpectrumDeserializer(InputStream stream, String workingDirectory) throws IOException {
        super(stream, workingDirectory);
    }

    /**
     * Parses a CML file.
     *
     * @return the parsed information
     */
    public SpectrumContainer getFile() throws XMLStreamException {

        // event callbacks
        boolean isMetaDataList = false;
        boolean isMetaData = false;
        boolean isDataFile = false;
        boolean isFileName = false;
        boolean isSpectrumList = false;
        boolean isSpectrum = false;

        spectraNumbers = new LinkedHashMap<Integer, Long>();
        basePeaks = new ArrayList<XYPoint>();

        double x = -1;
        double y = -1;

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
                    }
                    for (Iterator<Attribute> attributes = element.getAttributes(); attributes.hasNext(); ) {
                        Attribute attribute = attributes.next();
                        if (isMetaDataList && isMetaData) {
                            if (attribute.getValue().equals(FILENAME)) {
                                isFileName = true;
                            } else if (attribute.getValue().endsWith(DATAFILE)) {
                                isDataFile = true;
                            } else if (attribute.getName().getLocalPart().equals(CONTENT)) {
                                content = attribute.getValue();
                            }
                            if (isFileName && content != null) {
                                fileName = content;
                                isFileName = false;
                                content = null;
                            } else if (isDataFile && content != null) {
                                dataFile = workingDirectory + File.separator + content;
                                isDataFile = false;
                                content = null;
                            }
                        } else if (isSpectrumList && isSpectrum) {
                            if (attribute.getName().getLocalPart().equals(ID)) {
                                id = Integer.parseInt(attribute.getValue());
                            } else if (attribute.getName().getLocalPart().equals(POINTER)) {
                                pointer = Long.parseLong(attribute.getValue());
                            } else if (attribute.getName().getLocalPart().equals("x")) {
                                x = Double.parseDouble(attribute.getValue());
                            } else if (attribute.getName().getLocalPart().equals("y")) {
                                y = Double.parseDouble(attribute.getValue());
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
                        return new FileSpectrumContainer(fileName, dataFile, spectraNumbers, basePeaks);
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(SPECTRUM)) {
                        spectraNumbers.put(id, pointer);
                        basePeaks.add(new XYPoint(x, y));
                        isSpectrum = false;
                    }
                    break;
                case XMLStreamConstants.ATTRIBUTE:
                    break;
                default:
                    break;
            }
        }

        return new FileSpectrumContainer(fileName, dataFile, spectraNumbers, basePeaks);
    }
}
