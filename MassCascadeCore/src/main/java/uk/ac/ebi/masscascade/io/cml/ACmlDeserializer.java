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

import uk.ac.ebi.masscascade.interfaces.container.Container;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;

/**
 * Abstract class for all CML-based deserializers.
 */
public abstract class ACmlDeserializer {

    protected String fileName;
    protected XMLEventReader parser;
    protected String workingDirectory;

    protected String content;

    // CML element definitions
    protected static final String METADATALIST = "metadataList";
    protected static final String METADATA = "metadata";
    protected static final String SPECTRUMLIST = "spectrumList";
    protected static final String SPECTRUM = "spectrum";
    protected static final String SPECTRUMCONDITIONLIST = "conditionList";
    protected static final String SCALAR = "scalar";
    protected static final String SPECTRUMDATA = "spectrumData";
    protected static final String PEAKLIST = "peakList";
    protected static final String PEAK = "peak";

    // attribute names
    protected static final String FILENAME = "ms:fileName";
    protected static final String FILETITLE = "jcamp:title";
    protected static final String FILEDATE = "cmlDict:date";
    protected static final String FILEOWNER = "jcamp:owner";
    protected static final String DATAFILE = "dataFile";
    protected static final String IONMODE = "ms:ionMode";
    protected static final String MZRANGE = "ms:mzRange";
    protected static final String SCANRANGE = "ms:scanRange";
    protected static final String IONENERGY = "jcamp:ms_ionizationenergy";
    protected static final String LEVEL = "ms:level";
    protected static final String CONTENT = "content";
    protected static final String ID = "id";
    protected static final String TIC = "tic";
    protected static final String BASEPEAK = "basePeak";
    protected static final String POINTER = "pointer";
    protected static final String RT = "rt";

    /**
     * Constructs a file read task.
     *
     * @param id      a task identifier
     * @param cmlFile a cml file to read
     */
    protected ACmlDeserializer(String id, File cmlFile, String workingDirectory) throws IOException {

        if (cmlFile == null || !cmlFile.isFile()) {
            throw new IOException();
        }
        fileName = id;
        this.workingDirectory = workingDirectory;

        content = null;

        try {
            FileReader reader = new FileReader(cmlFile);
            XMLInputFactory factory = XMLInputFactory.newInstance();
            parser = factory.createXMLEventReader(reader);
        } catch (Exception exception) {
            throw new IOException();
        }
    }

    /**
     * Constructs a file read task.
     *
     * @param cmlString a cml string to read
     * @throws IOException unexpected behaviour
     */
    protected ACmlDeserializer(String cmlString, String workingDirectory) throws IOException {

        this.workingDirectory = workingDirectory;

        try {
            InputStream stream = new ByteArrayInputStream(cmlString.getBytes());
            XMLInputFactory factory = XMLInputFactory.newInstance();
            parser = factory.createXMLEventReader(stream);
        } catch (XMLStreamException exception) {
            throw new IOException();
        }
    }

    /**
     * Constructs a file read task.
     *
     * @param stream input stream for cml document
     * @throws IOException unexpected behaviour
     */
    protected ACmlDeserializer(InputStream stream, String workingDirectory) throws IOException {

        this.workingDirectory = workingDirectory;

        try {
            XMLInputFactory factory = XMLInputFactory.newInstance();
            parser = factory.createXMLEventReader(stream);
        } catch (XMLStreamException exception) {
            throw new IOException();
        }
    }

    abstract Container getFile() throws XMLStreamException;
}
