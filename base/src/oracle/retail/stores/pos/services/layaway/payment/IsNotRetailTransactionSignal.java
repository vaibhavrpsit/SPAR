/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/payment/IsNotRetailTransactionSignal.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:22:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:33 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:16  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:50:53  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:20:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:26   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:21:46   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:44   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.payment;

import java.lang.reflect.Field;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.TrafficLightIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;

//--------------------------------------------------------------------------
/**
    This signal checks to make sure that the transaction is not a
    RetialTransactionIfc
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class IsNotRetailTransactionSignal implements TrafficLightIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 7969994584974528566L;

    /** 
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.layaway.payment.IsNotRetailTransactionSignal.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Checks to make sure that the tranasction is a payment.
       <P>
       @return false if transaction type is layaway payment,
       true otherwise.
    **/
    //----------------------------------------------------------------------
    public boolean roadClear(BusIfc bus)
    {
        boolean result = false;
   
        LayawayCargo cargo = (LayawayCargo)bus.getCargo();
        TenderableTransactionIfc transaction = cargo.getTransactionIfc();
        
        // if transaction is a payment...
        if (transaction.getTransactionType() == TransactionIfc.TYPE_LAYAWAY_PAYMENT)
        {
            result = true;
        }
        
        return(result);
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // verbose flag
        boolean bVerbose = false;
        // result string
        String strResult = new String("Class:  ActivationIsNotRequiredSignal (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // if verbose mode, do inspection gig
        if (bVerbose)
        {                               // begin verbose mode
            // theClass will ascend through the inheritance hierarchy
            Class theClass = getClass();
            // type of the field currently being examined
            Class fieldType = null;
            // name of the field currently being examined
            String fieldName = "";
            // value of the field currently being examined
            Object fieldValue = null;

            // Ascend through the class hierarchy, capturing field information
            while (theClass != null)
            {                           // begin loop through fields
                // fields contains all noninherited field information
                Field[] fields = theClass.getDeclaredFields();

                // Go through each field, capturing information
                for (int i = 0; i < fields.length; i++)
                {
                    fieldType = fields[i].getType();
                    fieldName = fields[i].getName();

                    // get the field's value, if possible
                    try
                    {
                        fieldValue = fields[i].get(this);
                    }
                    // if the value can't be gotten, say so
                    catch (IllegalAccessException ex)
                    {
                        fieldValue = "*no access*";
                    }

                    // If it is a "simple" field, use the value
                    if (Util.isSimpleClass(fieldType))
                    {
                        strResult += "\n\t" + fieldName + ":\t" + fieldValue;
                    }       // if simple
                    // If it is a null value, say so
                    else if (fieldValue == null)
                    {
                        strResult += "\n\t" + fieldName + ":\t(null)";
                    }
                    // Otherwise, use <type<hashCode>
                    else
                    {
                        strResult += "\n\t" + fieldName + ":\t" +
                            fieldType.getName() + "@" +
                            fieldValue.hashCode();
                    }
                }   // for each field
                theClass = theClass.getSuperclass();
            }                           // end loop through fields
        }                               // end verbose mode
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
       Main to run a test..
       <P>
       @param  args    Command line parameters
    **/
    //----------------------------------------------------------------------
    public static void main(String args[])
    {                                   // begin main()
        // instantiate class
        IsNotRetailTransactionSignal obj = new IsNotRetailTransactionSignal();

        // output toString()
        System.out.println(obj.toString());
    }                                   // end main()
}
