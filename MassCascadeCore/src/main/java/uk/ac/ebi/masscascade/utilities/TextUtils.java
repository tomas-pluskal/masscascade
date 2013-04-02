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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import java.io.Closeable;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Class providing utility methods for text processing.
 */
public class TextUtils {

    private static final Logger LOGGER = Logger.getLogger(TextUtils.class);

    /**
     * Reads a byte input stream line by line.
     *
     * @param in an input stream
     * @return the read line
     * @throws IOException unexpected behaviour
     */
    public String readLineFromStream(InputStream in) throws IOException {

        byte buf[] = new byte[1024];
        int pos = 0;
        while (true) {
            int ch = in.read();
            if ((ch == '\n') || (ch < 0)) break;
            buf[pos++] = (byte) ch;
            if (pos == buf.length) buf = Arrays.copyOf(buf, pos * 2);
        }
        if (pos == 0) return null;

        return new String(Arrays.copyOf(buf, pos), "UTF-8");
    }

    /**
     * Reads a byte input stream line by line.
     *
     * @param in an input stream
     * @return the read line
     * @throws IOException unexpected behaviour
     */
    public Float readNumberFromStream(InputStream in) throws IOException {

        byte buf[] = new byte[1024];
        int pos = 0;
        while (true) {
            int ch = in.read();
            if (Character.isWhitespace(ch) || ch == '\n' || ch < 0) break;
            buf[pos++] = (byte) ch;
            if (pos == buf.length) buf = Arrays.copyOf(buf, pos * 2);
        }
        if (pos == 0) return null;

        return Float.parseFloat(new String(buf, 0, pos, "UTF-8"));
    }

    /**
     * Closes closeable reader and writer.
     *
     * @param c a reader / writer
     */
    public static void close(Closeable c) {

        if (c == null) return;
        try {
            c.close();
        } catch (IOException e) {
            LOGGER.log(Level.WARN, "Could not close closeable\n" + e.getMessage());
        }
    }

    /**
     * Closes closeable xml stream reader.
     *
     * @param c a xml stream reader
     */
    public static void close(XMLEventReader c) {

        if (c == null) return;
        try {
            c.close();
        } catch (XMLStreamException e) {
            LOGGER.log(Level.WARN, "Could not close xml stream\n" + e.getMessage());
        }
    }
}
