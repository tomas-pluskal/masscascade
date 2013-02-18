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

package uk.ac.ebi.masscascade.io.cml;

import org.apache.log4j.Logger;
import uk.ac.ebi.masscascade.core.file.raw.FileRawContainer;
import uk.ac.ebi.masscascade.core.raw.RawInfo;
import uk.ac.ebi.masscascade.core.raw.RawLevel;
import uk.ac.ebi.masscascade.interfaces.Range;
import uk.ac.ebi.masscascade.io.CmlReader;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.utilities.range.ExtendableRange;
import uk.ac.ebi.masscascade.utilities.ScanUtils;

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
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Class for CML-based raw file deserialization.
 */
public class RawDeserializer extends ACmlDeserializer {

    private static final Logger LOGGER = Logger.getLogger(CmlReader.class);

    // data container
    private FileRawContainer msFile;

    // Meta information
    private String fileName = "";
    private String fileTitle = "";
    private String creationDate = "";
    private String dataFile = "";
    private String fileOwner = "";
    private int totalScans = 0;
    private int ticId = 0;
    private Long ticNumber = 0L;
    private Range mzRange;
    private Range scanRange;
    private double ionEnergy;
    private Constants.MSN level;
    private Long basePeakNumber;
    private List<RawLevel> rawLevels = new ArrayList<RawLevel>();
    private LinkedHashMap<Constants.MSN, Long> ticNumbers = new LinkedHashMap<Constants.MSN, Long>();
    private List<LinkedHashMap<Integer, Long>> scanNumbers = new ArrayList<LinkedHashMap<Integer, Long>>();

    // scan information
    private Constants.MSN conditionLevel = Constants.MSN.MS1;
    private Map<Constants.MSN, String> ionMode = new HashMap<Constants.MSN, String>();

    // Tandem information
    private int parentCharge = 0;
    private int parentScan = -1;
    private int parentScanIndex = -1;
    private double parentMz = 0;

    // tmp variables
    String tmpValue = "";
    StringBuilder specData;

    /**
     * Constructs a raw file read task.
     *
     * @param id      a task identifier
     * @param cmlFile a cml file to read
     */
    public RawDeserializer(String id, File cmlFile, String workingDirectory) throws IOException {

        super(id, cmlFile, workingDirectory);
    }

    /**
     * Constructs a raw file read task.
     *
     * @param cmlString a cml string to read
     * @throws IOException unexpected behaviour
     */
    public RawDeserializer(String cmlString, String workingDirectory) throws IOException {

        super(cmlString, workingDirectory);
    }

    /**
     * Constructs a raw file read task.
     *
     * @param stream input stream for cml document
     * @throws IOException unexpected behaviour
     */
    public RawDeserializer(InputStream stream, String workingDirectory) throws IOException {

        super(stream, workingDirectory);
    }

    /**
     * Parses a CML file.
     *
     * @return the parsed information
     * @throws javax.xml.stream.XMLStreamException
     *          unexpected behaviour
     */
    public FileRawContainer getFile() throws XMLStreamException {

        // event callbacks
        boolean isMetaDataList = false;
        boolean isMetaData = false;
        boolean isConditionList = false;
        boolean isScalarCondition = false;
        boolean isSpectrumList = false;
        boolean isSpectrum = false;
        boolean isFileOwner = false;
        boolean isFileName = false;
        boolean isFileTitle = false;
        boolean isDataFile = false;
        boolean isFileDate = false;
        boolean isIonMode = false;
        boolean isMzRange = false;
        boolean isIonEnergy = false;
        boolean isData = false;
        boolean isGlobalScanRange = false;
        boolean isGlobalLevel = false;

        while (parser.peek() != null) {

            XMLEvent event = parser.nextEvent();

            switch (event.getEventType()) {

                case XMLStreamConstants.START_DOCUMENT:
                    break;
                case XMLStreamConstants.END_DOCUMENT:
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
                    } else if (element.getName().getLocalPart().equals(SPECTRUMCONDITIONLIST)) {
                        isConditionList = true;
                    } else if (element.getName().getLocalPart().equals(SCALAR)) {
                        isScalarCondition = true;
                    }
                    for (Iterator attributes = element.getAttributes(); attributes.hasNext(); ) {
                        Attribute attribute = (Attribute) attributes.next();
                        if (isMetaDataList && isMetaData && !isSpectrumList) {
                            if (attribute.getValue().equals(FILENAME)) {
                                isFileName = true;
                            } else if (attribute.getValue().equals(FILETITLE)) {
                                isFileTitle = true;
                            } else if (attribute.getValue().equals(FILEDATE)) {
                                isFileDate = true;
                            } else if (attribute.getValue().equals(FILEOWNER)) {
                                isFileOwner = true;
                            } else if (attribute.getValue().equals(DATAFILE)) {
                                isDataFile = true;
                            } else if (attribute.getName().getLocalPart().equals(CONTENT)) {
                                content = attribute.getValue();
                            }
                            if (isFileName && content != null) {
                                fileName = content;
                                isFileName = false;
                                content = null;
                            } else if (isFileTitle && content != null) {
                                fileTitle = content;
                                isFileTitle = false;
                                content = null;
                            } else if (isFileDate && content != null) {
                                creationDate = content;
                                isFileDate = false;
                                content = null;
                            } else if (isFileOwner && content != null) {
                                fileOwner = content;
                                isFileOwner = false;
                                content = null;
                            } else if (isDataFile && content != null) {
                                dataFile = workingDirectory + File.separator + content;
                                isDataFile = false;
                                content = null;
                            }
                        } else if (isSpectrumList && !isMetaDataList && !isConditionList && !isSpectrum) {
                            if (attribute.getName().getLocalPart().equals(BASEPEAK)) {
                                basePeakNumber = Long.parseLong(attribute.getValue());
                            }
                        } else if (isMetaDataList && isMetaData && isSpectrumList) {
                            // do nothing
                        } else if (isConditionList && !isScalarCondition && isSpectrumList) {
                            if (attribute.getName().getLocalPart().equals(ID)) {
                                conditionLevel = Constants.MSN.valueOf(attribute.getValue());
                            }
                        } else if (isConditionList && isScalarCondition && isSpectrumList) {
                            if (attribute.getValue().equals(IONMODE)) {
                                isIonMode = true;
                            } else if (attribute.getValue().equals(MZRANGE)) {
                                isMzRange = true;
                            } else if (attribute.getValue().equals(SCANRANGE)) {
                                isGlobalScanRange = true;
                            } else if (attribute.getValue().equals(IONENERGY)) {
                                isIonEnergy = true;
                            } else if (attribute.getValue().equals(LEVEL)) {
                                isGlobalLevel = true;
                            }
                        } else if (isSpectrumList && isSpectrum) {
                            if (attribute.getName().getLocalPart().equals(TIC)) {
                                ticNumber = Long.parseLong(attribute.getValue());
                            } else if (attribute.getName().getLocalPart().equals(ID)) {
                                ticId = Integer.parseInt(attribute.getValue());
                            }
                            specData = new StringBuilder();
                            isData = true;
                        }
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    Characters characters = event.asCharacters();
                    if (isIonMode) {
                        ionMode.put(conditionLevel, characters.getData());
                        isIonMode = false;
                    } else if (isMzRange) {
                        mzRange = getRange(characters.getData());
                        isMzRange = false;
                    } else if (isGlobalScanRange) {
                        scanRange = getRange(characters.getData());
                        isGlobalScanRange = false;
                    } else if (isIonEnergy) {
                        ionEnergy = Double.parseDouble(characters.getData());
                        isIonEnergy = false;
                    } else if (isGlobalLevel) {
                        level = Constants.MSN.get(characters.getData());
                        isGlobalLevel = false;
                    } else if (isData) {
                        specData.append(characters.getData());
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (event.asEndElement().getName().getLocalPart().equals(METADATALIST)) {
                        isMetaDataList = false;
                    } else if (event.asEndElement().getName().getLocalPart().equals(METADATA)) {
                        isMetaData = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(SPECTRUMCONDITIONLIST)) {
                        isConditionList = false;
                        RawLevel rawLevel = new RawLevel.Builder(scanRange, mzRange,
                                Constants.ION_MODE.valueOf(ionMode.get(conditionLevel))).msLevel(
                                level).fragmentationEnergy(ionEnergy).build();
                        rawLevels.add(rawLevel);
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(SCALAR)) {
                        isScalarCondition = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(SPECTRUMLIST)) {
                        isSpectrumList = false;
                        RawInfo rawInfo = new RawInfo(fileName, fileOwner, creationDate);
                        msFile = new FileRawContainer(fileName, rawInfo, rawLevels, basePeakNumber, ticNumbers, scanNumbers,
                                dataFile);

                        // hack to avoid EOFException thrown by the loop condition
                        parser.close();
                        return msFile;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(SPECTRUM)) {
                        isSpectrum = false;
                        LinkedHashMap<Integer, Long> scanNumberSet =
                                ScanUtils.get64BitLongArrayFromBinaryData(specData.toString(), true);
                        scanNumbers.add(scanNumberSet);
                        ticNumbers.put(Constants.MSN.get(ticId), ticNumber);
                    }
                    break;
                case XMLStreamConstants.ATTRIBUTE:
                    break;
                default:
                    break;
            }
        }
        parser.close();

        return msFile;
    }

    /**
     * Parses a string into a range (lowerLimit-upperLimit-domain).
     *
     * @param stringRange a string to be parsed
     * @return the range
     */
    private Range getRange(String stringRange) {

        String[] rangeParts = stringRange.split("-");
        double lower = Double.parseDouble(rangeParts[0]);
        double upper = Double.parseDouble(rangeParts[1]);

        return new ExtendableRange(lower, upper);
    }
}
