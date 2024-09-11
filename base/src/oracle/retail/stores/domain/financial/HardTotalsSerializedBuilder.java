/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/financial/HardTotalsSerializedBuilder.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:49:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:17 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/17 16:18:53  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.2  2004/02/12 17:13:34  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:30  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Jun 03 2003 10:52:54   jgs
 * Modified to give best compression and more reliably decompress the HardTotals Object.
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.financial;

//java imports
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterInputStream;

//----------------------------------------------------------------------------
/**
     This class defines the methods that are used to serialize/compres, 
     and deserialize/decompress the hard totals data. 
     <P>
     The advantage to this class is that it does not require the participation
     of the domain objects.
     <P>
     @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//----------------------------------------------------------------------------
public class HardTotalsSerializedBuilder implements HardTotalsBuilderIfc
{                                       
    /**
        revision number supplied by source-code-control system
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
     * Experimentation showed that the compression rate for this kind of
     * data was between 4:1 and 5:1;
    **/
    public static final int MAX_COMPRESSION_RATE = 10;
    /**
        Hardtotals object; this class is designed for compressing a serialied
        hard totals object.
    **/
    protected HardTotalsSerialized hardTotals = null;

    //---------------------------------------------------------------------
    /**
        This method sets the next field as an integer 
        <P>
        @param int the next field
    **/
    //--------------------------------------------------------------------- 
    public HardTotalsSerializedBuilder()
    {
    }
        
    //---------------------------------------------------------------------
    /**
        This method sets the next field as an integer.
        <P>
        @param int the next field
        @deprecated in 6.0
    **/
    //--------------------------------------------------------------------- 
    public void appendInt(int value)
    {
    }

    //---------------------------------------------------------------------
    /**
        This method gets the next field as an integer. 
        <P>
        @return int the next field
        @deprecated in 6.0
    **/
    //--------------------------------------------------------------------- 
    public int getIntField() throws HardTotalsFormatException
    {
        return 0;
    }
        
    //---------------------------------------------------------------------
    /**
        This method sets the next field as a String.  This method quotes
        the string so that it will be reproduced exactly.
        <P>
        @param Sting the next field
        @deprecated in 6.0
    **/
    //--------------------------------------------------------------------- 
    public void appendString(String value)
    {
    }
        
    //---------------------------------------------------------------------
    /**
        This method gets the next field as a String.  It expects the String
        to be quoted.
        <P>
        @return String the next field
        @deprecated in 6.0
    **/
    //--------------------------------------------------------------------- 
    public String getStringField() throws HardTotalsFormatException
    {
        return null;
    }
        
    //---------------------------------------------------------------------
    /**
        This method gets the next field as an instanciated class
        <P>
        @param String the next field
        @deprecated in 6.0
    **/
    //--------------------------------------------------------------------- 
    public void appendStringObject(String value)
    {
    }
        
    //---------------------------------------------------------------------
    /**
        This method gets the next field as an String.  It does not expect the
        quote the String to be quoted.
        <P>
        @return String the next field
        @deprecated in 6.0
    **/
    //--------------------------------------------------------------------- 
    public String getStringObject()
    {
        return null;
    }
        
    //---------------------------------------------------------------------
    /**
        This method gets the next field as an instanciated class
        <P>
        @return Object the next field
    **/
    //--------------------------------------------------------------------- 
    public Object getFieldAsClass() throws HardTotalsFormatException
    {
        return hardTotals;
    }
        
    //---------------------------------------------------------------------
    /**
        This method gets the next field string field in the comma delimited
        field.
        <P>
        @return String the next field
        @deprecated in 6.0
    **/
    //--------------------------------------------------------------------- 
    protected String getNextField()
    {
        return null;
    }
        
    //---------------------------------------------------------------------
    /**
        This method returns the object to be written to hard totals.  This
        object can only be gotten once.
        <P>
        This method uses in memory streams to seritalize and compress the 
        hard totals object.
        
        <P>
        @return Object the hard totals object
    **/
    //--------------------------------------------------------------------- 
    public Serializable getHardTotalsOutput() throws HardTotalsFormatException
    {
        byte[] obuf = null;

        try
        {
            // Serialize the hardtotals object to a byte array
            ByteArrayOutputStream byteOut = new ByteArrayOutputStream();
            ObjectOutputStream objOut = new ObjectOutputStream(byteOut);
            objOut.writeObject(hardTotals);
            objOut.flush();
            byteOut.flush();
            byte[] buffer = byteOut.toByteArray();
            objOut.close();
            byteOut.close();

            // Compress the serialized hardtotals object byte array
            ByteArrayOutputStream bos = new ByteArrayOutputStream(2048);
            Deflater def = new Deflater(Deflater.BEST_COMPRESSION);
            DeflaterOutputStream  dos = new DeflaterOutputStream(bos, def);

            dos.write(buffer);
            dos.finish();
            obuf = bos.toByteArray();
            dos.close();
            bos.close();
            
        }
        catch(java.io.IOException ioe)
        {
            throw new HardTotalsFormatException(
                "HardTotalsSerializedBuilder: Exception writing to the DeflaterOutputStream.");
        }
        finally
        {
            hardTotals = null;
        }

        return obuf;
    }
        
    //---------------------------------------------------------------------
    /**
        This method uses in memory streams to decompress and deserialize the
        hard totals object.
        <P>
        @return Object the hard totals object
    **/
    //--------------------------------------------------------------------- 
    public void setHardTotalsInput(Serializable ht) throws HardTotalsFormatException
    {
        // Cast the hard totals to a byte array.
        byte[] inputBuffer   = (byte[])ht;

        try
        {            
            ByteArrayInputStream bais = new ByteArrayInputStream(inputBuffer);
            InflaterInputStream iis = new InflaterInputStream(bais);
            ObjectInputStream in = new ObjectInputStream(iis);
            hardTotals = (HardTotalsSerialized)in.readObject();
            in.close();
        }
        catch(java.io.EOFException eofe)
        {
            throw new HardTotalsFormatException(
                "HardTotalsSerializedBuilder: End of file Exception: The decompress buffer is not big enough.\n" +
                "Increase the value of the MAX_COMPRESSION_RATE constant");
        }
        catch(java.io.IOException ioe)
        {
            throw new HardTotalsFormatException(
                "HardTotalsSerializedBuilder: Exception reading the ObjectInputStream: " + ioe.toString());
        }
        catch(java.lang.ClassNotFoundException cnfe)
        {
            throw new HardTotalsFormatException(
                "HardTotalsSerializedBuilder: domain.financial.HardTotalsSerialized class not found.");
        }

    }

    //---------------------------------------------------------------------
    /**
     * Returns the hardTotals.
     * @return HardTotalsSerialized
     */
    //---------------------------------------------------------------------
    public HardTotalsSerialized getHardTotals()
    {
        return hardTotals;
    }

    //---------------------------------------------------------------------
    /**
     * Sets the hardTotals.
     * @param hardTotals The hardTotals to set
     */
    //---------------------------------------------------------------------
    public void setHardTotals(HardTotalsSerialized hardTotals)
    {
        this.hardTotals = hardTotals;
    }
}
