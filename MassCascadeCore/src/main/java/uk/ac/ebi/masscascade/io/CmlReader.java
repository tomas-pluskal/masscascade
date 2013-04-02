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

import org.apache.log4j.Level;
import uk.ac.ebi.masscascade.core.container.file.raw.FileRawContainer;
import uk.ac.ebi.masscascade.core.raw.ScanImpl;
import uk.ac.ebi.masscascade.exception.MassCascadeException;
import uk.ac.ebi.masscascade.interfaces.CallableTask;
import uk.ac.ebi.masscascade.interfaces.container.RawContainer;
import uk.ac.ebi.masscascade.parameters.Constants;
import uk.ac.ebi.masscascade.parameters.Parameter;
import uk.ac.ebi.masscascade.parameters.ParameterMap;
import uk.ac.ebi.masscascade.utilities.ScanUtils;
import uk.ac.ebi.masscascade.utilities.TextUtils;
import uk.ac.ebi.masscascade.utilities.xyz.XYList;
import uk.ac.ebi.masscascade.utilities.xyz.XYPoint;

import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.File;
import java.io.FileReader;
import java.util.Iterator;

/**
 * Class for reading CML files following the 'msnmetabolomics' convention.
 * <ul>
 * <li>Parameter <code> DATA FILE </code>- The data file to be read.</li>
 * <li>Parameter <code> RAW FILE </code>- The target raw container.</li>
 * <li>Parameter <code> WORKING DIRECTORY </code>- The working directory.</li>
 * </ul>
 */
@Deprecated
public class CmlReader extends CallableTask {

    // CML element definitions
    private static final String METADATALIST = "metadataList";
    private static final String METADATA = "metadata";
    private static final String SPECTRUMLIST = "spectrumList";
    private static final String SPECTRUM = "spectrum";
    private static final String SCALAR = "scalar";
    private static final String PARAMETERLIST = "parameterList";
    private static final String PARAMETER = "parameter";
    private static final String SPECTRUMDATA = "spectrumData";
    private static final String XAXIS = "xaxis";
    private static final String YAXIS = "yaxis";
    private static final String ARRAY = "array";
    private static final String SAMPLE = "sample";
    private static final String PEAKLIST = "peakList";
    private static final String PEAK = "profile";
    private static final String MOLECULELIST = "moleculeList";
    private static final String MOLECULE = "molecule";

    // attribute names
    private static final String FILENAME = "mzml:fileName";
    private static final String FILETYPE = "mzml:fileType";
    private static final String FILEDATE = "cml:date";
    private static final String FILEOWNER = "owner";

    private static final String RETENTIONTIME = "mzml:retentionTime";
    private static final String IONMODE = "mzml:msIonisation";
    private static final String HIGHMZ = "mzml:lowMz";
    private static final String LOWMZ = "mzml:highMz";
    private static final String IONENERGY = "mzml:ionisationEnergy";
    private static final String LEVEL = "mzml:msLevel";
    private static final String BASEPEAK = "mzml:basePeakMz";
    private static final String BASEPEAKINT = "mzml:basePeakIntensity";
    private static final String TIC = "mzml:totalIonCurrent";

    private static final String PARENTSCAN = "mzml:precursorScanNum";
    private static final String PARENTCHARGE = "mzml:precursorCharge";
    private static final String PARENTMZ = "mzml:precursorMz";

    private static final String CONTENT = "content";

    private static final String ID = "id";

    // data container
    private RawContainer rawContainer;
    private String workingDirectory;
    private XMLEventReader parser;

    // Meta information
    private String fileName = "";
    private String fileType = "";
    private String creationDate = "";
    private String fileOwner = "";

    // scan information
    private int scanNumber = 0;
    private Constants.MSN msn = Constants.MSN.MS1;
    private double ionEnergy;
    private double retentionTime = 0;
    private double basePeak = 0;
    private double basePeakIntensity = 0;
    private double totalIonCurrent = 0;
    private double lowMz = 0;
    private double highMz = 0;
    private double[] xs = null;
    private double[] ys = null;
    private int conditionLevel = 0;
    private String ionMode = "";

    // Tandem information
    private int parentCharge = 0;
    private int parentScan = -1;
    private double parentMz = 0;

    // tmp variables
    String tmpValue = "";
    StringBuilder specData = new StringBuilder();

    /**
     * Constructs a CML reader task.
     *
     * @param params the parameter map
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public CmlReader(ParameterMap params) throws MassCascadeException {

        super(CmlReader.class);
        setParameters(params);
    }

    /**
     * Sets the parameters for the CML reader.
     *
     * @param params the new parameter values
     * @throws uk.ac.ebi.masscascade.exception.MassCascadeException
     *
     */
    public void setParameters(ParameterMap params) throws MassCascadeException {

        File cmlFile = params.get(Parameter.DATA_FILE, File.class);
        workingDirectory = params.get(Parameter.WORKING_DIRECTORY, String.class);

        fileName = cmlFile.getName();

        if (cmlFile == null || !cmlFile.isFile()) throw new MassCascadeException("File not found.");

        FileReader reader = null;
        try {
            reader = new FileReader(cmlFile);
            XMLInputFactory factory = XMLInputFactory.newInstance();
            parser = factory.createXMLEventReader(reader);
        } catch (Exception exception) {
            throw new MassCascadeException("Parameter exception.", exception);
        } finally {
            TextUtils.close(reader);
            TextUtils.close(parser);
        }
    }

    /**
     * Parses a CML file.
     *
     * @return the parsed information
     */
    @Override
    public RawContainer call() {

        // meta information
        boolean isMetaDataList = false;
        boolean isMetaData = false;
        boolean isParameterList = false;
        boolean isParameter = false;
        boolean isScalarParameter = false;
        boolean isSample = false;

        // spectrum
        boolean isSpectrumList = false;
        boolean isSpectrum = false;
        boolean isSpectrumData = false;
        boolean isXaxis = false;
        boolean isYaxis = false;
        boolean isArray = false;

        // profile switches
        boolean isPeakList = false;
        boolean isPeak = false;

        // molecule switches
        boolean isMoleculeList = false;
        boolean isMolecule = false;

        while (parser.hasNext()) {

            XMLEvent event = getNextEvent();

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
                    } else if (element.getName().getLocalPart().equals(PARAMETERLIST)) {
                        isParameterList = true;
                    } else if (element.getName().getLocalPart().equals(PARAMETER)) {
                        isParameter = true;
                    } else if (element.getName().getLocalPart().equals(SCALAR)) {
                        isScalarParameter = true;
                    } else if (element.getName().getLocalPart().equals(SAMPLE)) {
                        isSample = true;
                    } else if (element.getName().getLocalPart().equals(SPECTRUMLIST)) {
                        isSpectrumList = true;

                        constructFile();
                    } else if (element.getName().getLocalPart().equals(SPECTRUM)) {
                        isSpectrum = true;
                    } else if (element.getName().getLocalPart().equals(SPECTRUMDATA)) {
                        isSpectrumData = true;
                    } else if (element.getName().getLocalPart().equals(XAXIS)) {
                        isXaxis = true;
                    } else if (element.getName().getLocalPart().equals(YAXIS)) {
                        isYaxis = true;
                    } else if (element.getName().getLocalPart().equals(ARRAY)) {
                        isArray = true;
                    } else if (element.getName().getLocalPart().equals(PEAKLIST)) {
                        isPeakList = true;
                    } else if (element.getName().getLocalPart().equals(PEAK)) {
                        isPeak = true;
                    } else if (element.getName().getLocalPart().equals(MOLECULELIST)) {
                        isMoleculeList = true;
                    } else if (element.getName().getLocalPart().equals(MOLECULE)) {
                        isMolecule = true;
                    }

                    tmpValue = "";
                    for (Iterator<Attribute> attributes = element.getAttributes(); attributes.hasNext(); ) {

                        Attribute attribute = attributes.next();
                        if (attribute.getName().getLocalPart().equals(CONTENT)) {
                            tmpValue = attribute.getValue();
                        }
                    }

                    for (Iterator<Attribute> attributes = element.getAttributes(); attributes.hasNext(); ) {

                        Attribute attribute = attributes.next();

                        if (isMetaDataList && isMetaData && !isSpectrumList) {
                            if (attribute.getValue().equals(FILENAME)) {
                                fileName = tmpValue;
                            } else if (attribute.getValue().equals(FILETYPE)) {
                                fileType = tmpValue;
                            } else if (attribute.getValue().equals(FILEDATE)) {
                                creationDate = tmpValue;
                            } else if (attribute.getValue().equals(FILEOWNER)) {
                                fileOwner = tmpValue;
                            }
                        } else if (isSpectrumList && isSpectrum && !isMetaDataList && !isSpectrumData && !isPeakList) {
                            if (attribute.getName().getLocalPart().equals(ID)) {
                                String spectrumId = attribute.getValue();
                                spectrumId = spectrumId.replace("spectrum", "");
                                scanNumber = Integer.parseInt(spectrumId);
                            }
                        } else if (isSpectrumList && isSpectrum && isMetaDataList && isMetaData) {
                            if (attribute.getValue().equals(RETENTIONTIME)) {
                                retentionTime = Double.parseDouble(tmpValue);
                            } else if (attribute.getValue().equals(IONMODE)) {
                                ionMode = tmpValue;
                            } else if (attribute.getValue().equals(LOWMZ)) {
                                lowMz = Double.parseDouble(tmpValue);
                            } else if (attribute.getValue().equals(HIGHMZ)) {
                                highMz = Double.parseDouble(tmpValue);
                            } else if (attribute.getValue().equals(IONENERGY)) {
                                ionEnergy = Double.parseDouble(tmpValue);
                            } else if (attribute.getValue().equals(LEVEL)) {
                                msn = Constants.MSN.get(tmpValue);
                            } else if (attribute.getValue().equals(BASEPEAK)) {
                                basePeak = Double.parseDouble(tmpValue);
                            } else if (attribute.getValue().equals(BASEPEAKINT)) {
                                basePeakIntensity = Double.parseDouble(tmpValue);
                            } else if (attribute.getValue().equals(TIC)) {
                                totalIonCurrent = Double.parseDouble(tmpValue);
                            } else if (attribute.getValue().equals(PARENTCHARGE)) {
                                parentCharge = Integer.parseInt(tmpValue);
                            } else if (attribute.getValue().equals(PARENTMZ)) {
                                parentMz = Double.parseDouble(tmpValue);
                            } else if (attribute.getValue().equals(PARENTSCAN)) {
                                parentScan = Integer.parseInt(tmpValue);
                            }
                        } else if (isSpectrumList && isSpectrum && isSpectrumData && isXaxis && isArray) {
                            // do nothing
                        } else if (isSpectrumList && isSpectrum && isSpectrumData && isYaxis && isArray) {
                            // do nothing
                        } else if (isSpectrumList && isSpectrum && isPeakList && isPeak) {

                        }
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    Characters characters = event.asCharacters();
                    if (isXaxis && isArray) {
                        specData.append(characters.getData());
                    } else if (isYaxis && isArray) {
                        specData.append(characters.getData());
                    }
                    break;
                case XMLStreamConstants.END_ELEMENT:
                    if (event.asEndElement().getName().getLocalPart().equals(METADATALIST)) {
                        isMetaDataList = false;
                    } else if (event.asEndElement().getName().getLocalPart().equals(METADATA)) {
                        isMetaData = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(PARAMETERLIST)) {
                        isParameterList = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(PARAMETER)) {
                        isParameter = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(SAMPLE)) {
                        isSample = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(SPECTRUMLIST)) {
                        isSpectrumList = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(SPECTRUM)) {
                        isSpectrum = false;

                        addScanToFile();
                        freeMemoryAndReset();
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(SPECTRUMDATA)) {
                        isSpectrumData = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(XAXIS)) {
                        xs = ScanUtils.get64BitFloatArrayFromBinaryData(specData.toString(), true);
                        specData = new StringBuilder();
                        isXaxis = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(YAXIS)) {
                        ys = ScanUtils.get64BitFloatArrayFromBinaryData(specData.toString(), true);
                        specData = new StringBuilder();
                        isYaxis = false;
                    } else if (event.asEndElement().getName().getLocalPart().endsWith(ARRAY)) {
                        isArray = false;
                    }
                    break;
                case XMLStreamConstants.ATTRIBUTE:
                    break;
                default:
                    break;
            }
        }

        closeParser();

        rawContainer.finaliseFile(creationDate);
        return rawContainer;
    }

    private void addScanToFile() {

        XYList xyList = new XYList();
        for (int i = 0; i < xs.length; i++) {
            xyList.add(new XYPoint(xs[i], ys[i]));
        }

        Constants.ION_MODE selIonMode = Constants.ION_MODE.valueOf(ionMode);
        ScanImpl scan = new ScanImpl(scanNumber, msn, selIonMode, xyList, retentionTime, parentScan, parentCharge,
                parentMz);
        rawContainer.addScan(scan);
    }

    private void constructFile() {

        rawContainer = new FileRawContainer(fileName, workingDirectory);
    }

    private void freeMemoryAndReset() {

        // scan information
        xs = null;
        ys = null;

        // Tandem information
        parentCharge = 0;
        parentScan = -1;
        parentMz = 0;
    }

    /**
     * Gets the next xml event in the stream reader.
     *
     * @return the read xml event
     */
    private XMLEvent getNextEvent() {

        XMLEvent event;

        try {
            event = parser.nextEvent();
        } catch (XMLStreamException exception) {
            XMLEventFactory eventFactory = XMLEventFactory.newFactory();
            event = eventFactory.createAttribute("ParseFailed", "next event failed");
        }

        return event;
    }

    private void closeParser() {

        try {
            parser.close();
        } catch (XMLStreamException exception) {
            LOGGER.log(Level.INFO, "Failed to close XMLStream.");
        }
    }
}