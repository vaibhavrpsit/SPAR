/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/HardTotalsStringBuilder.java /main/15 2012/09/12 11:57:12 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    blarse 04/25/12 - Changed appendString() to be more efficient. This
 *                      method was a hotspot in jProfiler.
 *    cgreen 05/26/10 - convert to oracle packaging
 *    abonda 01/03/10 - update header date
 *    cgreen 05/06/09 - update getNext field to create strings from fresh char
 *                      arrays to allow for lengthy string data to be garbage
 *                      collected.
 *    ohorne 10/20/08 - removed system.out.println of StringBuilder due to
 *                      OutOfMemory error observed in dev environment
 *
 * ===========================================================================
 * $Log:
 *  5    360Commerce 1.4         4/15/2008 5:30:23 PM   Christian Greene
 *       Upgrade StringBuffer to StringBuilder
 *  4    360Commerce 1.3         4/3/2008 12:06:08 PM   Christian Greene 24966
 *       Base64 encode the employee ssn into the hardttotals so that it is not
 *        clear text.
 *  3    360Commerce 1.2         3/31/2005 4:28:19 PM   Robert Pearse
 *  2    360Commerce 1.1         3/10/2005 10:22:00 AM  Robert Pearse
 *  1    360Commerce 1.0         2/11/2005 12:11:18 PM  Robert Pearse
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

/**
 * This class defines the methods that are used to flatten and unflatten the
 * hard totals data.
 *
 * @version $Revision: /main/15 $
 */
public class HardTotalsStringBuilder implements HardTotalsBuilderIfc
{
    /**
     * revision number supplied by source-code-control system
     */
    public static String revisionNumber = "$Revision: /main/15 $";

    /**
     * Constants used in conversion routines.
     */
    protected static String COMMA = ",";
    protected static char QUOTE = '\'';
    protected static int START_LEN = 2500;
    protected static String EMPTY = "'',";
    protected static String NULL = "null";
    protected static int BUF_LEN = 1024;
    protected static String QUOTE_COMMA = "',";

    /**
     * Place holder for outbound data
     */
    protected StringBuilder sb = null; // if multi-threading issues are found, use StringBuffer

    /**
     * Place holder for inbound data
     */
    protected String data = null;

    /**
     * Current working offset
     */
    protected int offset = 0;

    /**
     * This method sets the next field as an integer
     *
     * @param int the next field
     */
    public HardTotalsStringBuilder()
    {
        sb = new StringBuilder(START_LEN);
    }

    /**
     * This method sets the next field as an integer.
     *
     * @param int the next field
     */
    public void appendInt(int value)
    {
        sb.append(Integer.toString(value));
        sb.append(COMMA);
    }

    /**
     * This method gets the next field as an integer.
     *
     * @return int the next field
     */
    public int getIntField() throws HardTotalsFormatException
    {
        String str = getNextField();
        int field = 0;
        try
        {
            field = Integer.valueOf(str).intValue();
        }
        catch (java.lang.NumberFormatException nfe)
        {
            throw new HardTotalsFormatException("Error converting numeric in HardTotalsStringBuilder");
        }

        return field;
    }

    /**
     * This method sets the next field as a String. This method quotes the
     * string so that it will be reproduced exactly.
     *
     * @param Sting the next field
     */
    public void appendString(String value)
    {

        // Start all strings with a single quote
        sb.append(QUOTE);

        addDoubleQuotes(sb, value);

        // End all strings with a quote and a comma
        sb.append(QUOTE_COMMA);
    }

    /**
     * Changes all quotes in the specified to 2 quotes and appends the resulting
     * string to the string builder.
     *
     * @param sb - the string builder onto which the converted string is appended
     * @param string
     */
    private static void addDoubleQuotes(StringBuilder sb, String string)
    {
        // This method walks down the specified string looking for quotes and appends parts of the string
        // to the StringBuilder inserting the extra quote when necessary.

        int startingPos = 0;
        int nextStartingPos = 0;

        int quotePos = string.indexOf(QUOTE, startingPos);
        while (quotePos != -1)
        {
            nextStartingPos = quotePos + 1;
            sb.append(string.substring(startingPos, nextStartingPos));
            sb.append(QUOTE);
            startingPos = nextStartingPos;

            quotePos = string.indexOf(QUOTE, startingPos);
        }

        if (startingPos < string.length())
        {
            sb.append(string.substring(startingPos));
        }
    }

    /**
     * This method gets the next field as a String. It expects the String to be
     * quoted.
     *
     * @return String the next field
     */
    public String getStringField() throws HardTotalsFormatException
    {
        StringBuffer temp = new StringBuffer(100);
        boolean done = false;

        if (data.charAt(offset) != QUOTE)
        {
            throw new HardTotalsFormatException("HardTotalsStringBuilder: A String field is not properly quoted.");
        }

        // If this string field parses out to just "''", then it is empty
        String empty = data.substring((offset), (offset + 3));
        if (empty.equals(EMPTY))
        {
            offset++;
        }
        else
        {
            // Move past first quote.
            offset++;

            // Check for two quotes in a row.
            while (!done)
            {
                // Check the next two characters to see if we are done.
                char c1 = data.charAt(offset);
                char c2 = data.charAt(offset + 1);
                if (c1 == QUOTE && c2 != QUOTE)
                {
                    done = true;
                }
                else
                {
                    // Add the character to the buffer.
                    temp.append(c1);

                    // If the next two characters a quotes, skip the next one.
                    if (c1 == QUOTE && c2 == QUOTE)
                    {
                        offset++;
                    }

                    // Go to the next character.
                    offset++;
                }
            }
        }

        // We currently pointed at the last quote, skip it and the comma.
        offset = offset + 2;

        return temp.toString();
    }

    /**
     * This method gets the next field as an instantiated class
     *
     * @param String the next field
     */
    public void appendStringObject(String value)
    {
        sb.append(value);
        sb.append(COMMA);
    }

    /**
     * This method gets the next field as an String. It does not expect the
     * quote the String to be quoted.
     *
     * @return String the next field
     */
    public String getStringObject()
    {
        return getNextField();
    }

    /**
     * This method gets the next field as an instantiated class
     *
     * @return Object the next field
     */
    public Object getFieldAsClass() throws HardTotalsFormatException
    {
        String cName = getNextField();
        Object obj = null;
        Class<?> cClass = null;

        if (!cName.equals(NULL))
        {
            try
            {
                cClass = Class.forName(cName);
                obj = cClass.newInstance();
            }
            catch (java.lang.ClassNotFoundException cnfe)
            {
                throw new HardTotalsFormatException("Class Not Found: class = " + cName + " in "
                        + this.getClass().getName());
            }
            catch (java.lang.IllegalAccessException iae)
            {
                throw new HardTotalsFormatException("Illegal Access Exception: class = " + cName + " in "
                        + this.getClass().getName());
            }
            catch (java.lang.InstantiationException ie)
            {
                throw new HardTotalsFormatException("Instantiation Exception: class = " + cName + " in "
                        + this.getClass().getName());
            }
        }

        return obj;
    }

    /**
     * This method gets the next field string field in the comma delimited
     * field. A new char array is created from the underlying string data, which
     * is likely very large. This allows for it to be garbage collected.
     *
     * @return String the next field
     */
    protected String getNextField()
    {
        int endIndex = data.indexOf(COMMA, offset);
        char[] value = new char[endIndex - offset];
        data.getChars(offset, endIndex, value, 0);
        String field = new String(value);
        offset = endIndex + 1;
        return field;
    }

    /**
     * This method returns the object to be written to hard totals. This object
     * can only be gotten once. * This method uses an in memory stream to
     * compress the string data in the string buffer to get approximately a 40
     * to 1 compression. I would have perfered to use the Deflater class
     * directly, but inspite of some sample that I found, I could not get it to
     * work.
     *
     * @return Object the hard totals object
     */
    public Serializable getHardTotalsOutput() throws HardTotalsFormatException
    {
        byte[] obuf = null;
        byte[] sbBuf = sb.toString().getBytes();
        ByteArrayOutputStream bos = new ByteArrayOutputStream(2048);
        DeflaterOutputStream dos = new DeflaterOutputStream(bos);
        try
        {
            dos.write(sbBuf);
            dos.finish();
            obuf = bos.toByteArray();
            dos.close();
        }
        catch (java.io.IOException ioe)
        {
            throw new HardTotalsFormatException(
                    "HardTotalsStringBuilder: Exception writing to the DeflaterOutputStream.");
        }

        return obuf;
    }

    /**
     * This method sets the object which has been read from hard totals.
     *
     * @return Object the hard totals object
     */
    public void setHardTotalsInput(Serializable ht) throws HardTotalsFormatException
    {
        char[] cbuf = new char[BUF_LEN];
        byte[] ibuf = (byte[])ht;
        sb = new StringBuilder(START_LEN);
        ByteArrayInputStream bis = new ByteArrayInputStream(ibuf);
        InflaterInputStream iis = new InflaterInputStream(bis);
        int bytesRead = 1;
        int index = 0;
        try
        {
            while (bytesRead > 0)
            {
                bytesRead = iis.read();
                if (bytesRead > -1)
                {
                    cbuf[index] = (char)bytesRead;
                    index++;
                    if (index >= BUF_LEN)
                    {
                        index = 0;
                        sb.append(cbuf);
                    }
                }
            }
            if (index > 0)
            {
                sb.append(cbuf);
            }
            iis.close();
        }
        catch (java.io.IOException ioe)
        {
            throw new HardTotalsFormatException("HardTotalsStringBuilder: Exception reading the DefalterInputStream.");
        }

        offset = 0;
        data = sb.toString();
        sb = new StringBuilder(START_LEN);
    }

    /**
     * Entry point. Parses XML file specified by arg[0].
     * @param args
     */
    public static void main(String[] args) throws Exception
    {
        StringBuilder sb = new StringBuilder();
        addDoubleQuotes(sb, "");
        sb = new StringBuilder();
        addDoubleQuotes(sb, "a");
        sb = new StringBuilder();
        addDoubleQuotes(sb, "ab");
        sb = new StringBuilder();
        addDoubleQuotes(sb, "'");
        sb = new StringBuilder();
        addDoubleQuotes(sb, "''");
        sb = new StringBuilder();
        addDoubleQuotes(sb, "after'");
        sb = new StringBuilder();
        addDoubleQuotes(sb, "'before");
        sb = new StringBuilder();
        addDoubleQuotes(sb, "in'side");
        sb = new StringBuilder();
        addDoubleQuotes(sb, "mul'ti'ples");
        sb = new StringBuilder();
        addDoubleQuotes(sb, "'a'bunch'of'em'");

    }


}
