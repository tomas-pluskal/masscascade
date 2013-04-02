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

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

/**
 * Class providing utility functions for package operations.
 */
public class PackUtils {

    private static final Logger LOGGER = Logger.getLogger(PackUtils.class);

    /**
     * Gets the URI from the objects archive.
     *
     * @param aClass an object inside the archive
     * @return an uri of the archive
     * @throws URISyntaxException
     */
    public static URI getJarURI(Class aClass) throws URISyntaxException {

        final ProtectionDomain domain;
        final CodeSource source;
        final URL url;
        final URI uri;

        domain = aClass.getProtectionDomain();
        source = domain.getCodeSource();
        url = source.getLocation();
        uri = url.toURI();

        return (uri);
    }

    /**
     * Gets the URI from a file within an archive.
     *
     * @param where    an URI of an archive
     * @param fileName a name of the file
     * @return the URI of the file
     * @throws ZipException
     * @throws IOException
     */
    public static URI getFile(final URI where, final String fileName) throws ZipException, IOException {

        final File location;
        final URI fileURI;

        location = new File(where);

        // not in a JAR, just return the path on disk
        if (location.isDirectory()) {
            fileURI = URI.create(where.toString() + fileName);
            LOGGER.log(Level.INFO, "Dir: " + fileURI.toString());
        } else {
            final ZipFile zipFile;

            zipFile = new ZipFile(location);

            try {
                fileURI = extract(zipFile, fileName);
            } finally {
                zipFile.close();
            }
        }

        return (fileURI);
    }

    /**
     * Gets the actual file from within an archive.
     *
     * @param zipFile  a zip archive file
     * @param fileName a name of the archive
     * @return the file URI
     * @throws IOException
     */
    private static URI extract(final ZipFile zipFile, final String fileName) throws IOException {

        final File tempFile;
        final ZipEntry entry;
        final InputStream zipStream;
        OutputStream fileStream;

        String tmpFileName = fileName.substring(fileName.lastIndexOf("/"));

        tempFile = new File(System.getProperty("java.io.tmpdir") + File.separator + tmpFileName);
        tempFile.deleteOnExit();
        entry = zipFile.getEntry(fileName);

        if (entry == null)
            throw new FileNotFoundException("cannot find file: " + fileName + " in archive: " + zipFile.getName());

        zipStream = zipFile.getInputStream(entry);
        fileStream = null;

        try {
            final byte[] buf;
            int i;

            fileStream = new FileOutputStream(tempFile);
            buf = new byte[1024];

            while ((i = zipStream.read(buf)) != -1) {
                fileStream.write(buf, 0, i);
            }
        } finally {
            TextUtils.close(zipStream);
            TextUtils.close(fileStream);
        }

        tempFile.setExecutable(true);
        return (tempFile.toURI());
    }
}
