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

import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.element.CMLCml;
import org.xmlcml.cml.element.CMLMetadata;
import org.xmlcml.cml.element.CMLScalar;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Abstract class for all CML-based serializers.
 */
public abstract class ACmlSerializer {

    protected CMLCml rootCML;

    // CML element definitions
    protected static final String METADATALIST = "metadataList";
    protected static final String METADATA = "metadata";
    protected static final String SPECTRUMLIST = "spectrumList";
    protected static final String SPECTRUM = "spectrum";
    protected static final String SPECTRUMCONDITIONLIST = "conditionList";
    protected static final String SCALAR = "scalar";
    protected static final String SPECTRUMDATA = "spectrumData";

    // attribute names
    protected static final String FILENAME = "ms:fileName";
    protected static final String FILETITLE = "jcamp:title";
    protected static final String FILEDATE = "cmlDict:date";
    protected static final String FILEDATA = "dataFile";
    protected static final String FILEOWNER = "jcamp:owner";
    protected static final String IONMODE = "ms:ionMode";
    protected static final String MZRANGE = "ms:mzRange";
    protected static final String SCANRANGE = "ms:scanRange";
    protected static final String IONENERGY = "jcamp:ms_ionizationenergy";
    protected static final String LEVEL = "ms:level";
    protected static final String CONTENT = "content";
    protected static final String ID = "id";
    protected static final String TIC = "tic";
    protected static final String ACQUISITIONMODE = "ms:acquisitionMode";
    protected static final String BASEPEAK = "basePeak";
    protected static final String POINTER = "pointer";

    // data types
    protected static final String STRING = "xsd:string";
    protected static final String FLOAT = "xsd:float";
    protected static final String INTEGER = "xsd:integer";
    protected static final String BINARY32 = "xsd:base32Binary";

    /**
     * Returns a formatted string representation of the CML content.
     *
     * @param input  a unformatted string
     * @param indent an indent size
     * @return the formatted string
     */
    public String toString(String input, int indent) {

        try {
            Source xmlInput = new StreamSource(new StringReader(input));
            StringWriter stringWriter = new StringWriter();
            StreamResult xmlOutput = new StreamResult(stringWriter);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", String.valueOf(indent));
            transformer.transform(xmlInput, xmlOutput);
            return xmlOutput.getWriter().toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Gets the CML header.
     *
     * @return the cml header
     */
    protected CMLCml getHeader() {

        CMLCml cmlHeader = new CMLCml();
        CMLAttribute convent = new CMLAttribute("convention", "cmlKnimeDataPointer");
        cmlHeader.addAttribute(convent);
        cmlHeader.addNamespaceDeclaration("", "http://www.xml-cml.org/schema");
        cmlHeader.addNamespaceDeclaration("cml", "http://www.xml-cml.org/dict/cml/cmlDict.xml");
        cmlHeader.addNamespaceDeclaration("jcamp", "http://www.xml-cml.org/dict/jcampDict/jcampDict.xml");
        cmlHeader.addNamespaceDeclaration("siUnits", "http://www.xml-cml.org/units/siUnitsDict.xml");
        cmlHeader.addNamespaceDeclaration("units", "http://www.xml-cml.org/units/unitsDict.xml");

        return cmlHeader;
    }

    /**
     * Wraps the spectral information in CML.
     *
     * @return the cml container
     */
    public abstract CMLCml getCml();

    protected CMLScalar getCMLScalar(String dictRef, String dataType, String value) {

        CMLScalar cmlScalar = new CMLScalar();
        cmlScalar.setDictRef(dictRef);
        cmlScalar.setDataType(dataType);
        cmlScalar.setValue(value);

        return cmlScalar;
    }

    protected CMLScalar getCMLScalar(String dictRef, String dataType, double value) {

        CMLScalar cmlScalar = new CMLScalar();
        cmlScalar.setDictRef(dictRef);
        cmlScalar.setDataType(dataType);
        cmlScalar.setValue(value);

        return cmlScalar;
    }

    protected CMLScalar getCMLScalar(String dictRef, String dataType, int value) {

        CMLScalar cmlScalar = new CMLScalar();
        cmlScalar.setDictRef(dictRef);
        cmlScalar.setDataType(dataType);
        cmlScalar.setValue(value);

        return cmlScalar;
    }

    protected CMLMetadata getCMLMetaData(String name, String content) {

        CMLMetadata metadata = new CMLMetadata();
        metadata.setName(name);
        metadata.setContent(content);

        return metadata;
    }
}
