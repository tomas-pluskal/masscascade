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

package uk.ac.ebi.masscascade.utilities;

import org.xmlcml.cml.base.CMLAttribute;
import org.xmlcml.cml.element.CMLCml;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * Class providing utility functions for cml handling.
 */
public class CmlUtils {

    /**
     * Returns the formatted string representation of the CML content.
     *
     * @param input  an unformatted string
     * @param indent an indent size
     * @return the formatted string
     */
    public static String toFormattedString(String input, int indent) {

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
     * Returns the standard CML header.
     *
     * @return the cml header
     */
    public static CMLCml getHeader() {

        CMLCml cmlHeader = new CMLCml();

        cmlHeader.setConvention("msnmetabolomics");

        cmlHeader.addNamespaceDeclaration("", "http://www.xml-cml.org/schema");
        cmlHeader.addNamespaceDeclaration("cml", "http://www.xml-cml.org/dictionary/cml/");
        cmlHeader.addNamespaceDeclaration("mzml", "http://sashimi.sourceforge.net/schema_revision/mzXML_3.0");
        cmlHeader.addNamespaceDeclaration("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        cmlHeader.addAttribute(getSchemaLocationAttribute());

        return cmlHeader;
    }

    /**
     * Returns the CML schema location attributes.
     *
     * @return the attributes
     */
    public static CMLAttribute getSchemaLocationAttribute() {

        CMLAttribute attribute = new CMLAttribute("schemaLocation",
                "http://sashimi.sourceforge.net/schema_revision/mzXML_3.0 http://sashimi.sourceforge" +
                        ".net/schema_revision/mzXML_3.0/mzXML_idx_3.0.xsd http://www.xml-cml.org/dictionary/cml/ " +
                        "http://www.xml-cml.org/schema");
        attribute.setNamespace("xsi", "http://www.w3.org/2001/XMLSchema-instance");

        return attribute;
    }
}
