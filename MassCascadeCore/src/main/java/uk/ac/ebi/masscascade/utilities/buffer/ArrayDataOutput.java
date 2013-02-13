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