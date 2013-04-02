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

package uk.ac.ebi.masscascade.utilities.buffer;

import java.io.IOException;

interface ArrayDataOutput extends java.io.DataOutput {

    public void writeArray(Object o) throws IOException;

    /* Write a complete array */
    public void write(byte[] buf) throws IOException;

    public void write(boolean[] buf) throws IOException;

    public void write(short[] buf) throws IOException;

    public void write(char[] buf) throws IOException;

    public void write(int[] buf) throws IOException;

    public void write(long[] buf) throws IOException;

    public void write(float[] buf) throws IOException;

    public void write(double[] buf) throws IOException;

    /* Write an array of Strings */
    public void write(String[] buf) throws IOException;

    /* Write a segment of a primitive array. */
    public void write(byte[] buf, int offset, int size) throws IOException;

    public void write(boolean[] buf, int offset, int size) throws IOException;

    public void write(char[] buf, int offset, int size) throws IOException;

    public void write(short[] buf, int offset, int size) throws IOException;

    public void write(int[] buf, int offset, int size) throws IOException;

    public void write(long[] buf, int offset, int size) throws IOException;

    public void write(float[] buf, int offset, int size) throws IOException;

    public void write(double[] buf, int offset, int size) throws IOException;

    /* Write some of an array of Strings */
    public void write(String[] buf, int offset, int size) throws IOException;

    /* Flush the output buffer */
    public void flush() throws IOException;

    public void close() throws IOException;
}