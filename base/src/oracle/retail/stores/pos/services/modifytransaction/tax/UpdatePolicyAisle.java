/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/UpdatePolicyAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:35 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:25 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:03:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:06   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:31:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:09:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;

//java imports
import java.lang.reflect.Field;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;

//--------------------------------------------------------------------------
/**
    ##COMMENT##
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class UpdatePolicyAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**

       This aisle will be traversed if the Yes letter
       is sent from the UI. The updateAllItems flag will be set
       to indicate that all items in the transaction will
       be set to the new tax rate about to be entered.
       Then it will send a Next Letter.
       <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>none
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>none
       </UL>
       @param bus Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // retrieve cargo
        ModifyTransactionTaxCargo cargo =
            (ModifyTransactionTaxCargo) bus.getCargo();

        // set update flag in cargo
        cargo.setUpdateAllItemsFlag(true);
        // determine road to traverse based on flag
        int flag = cargo.getTaxUpdateFlag();
        if (flag == ModifyTransactionTaxCargo.TAX_UPDATE_RATE)
        {
            bus.mail(new Letter("EnterTaxRate"), BusIfc.CURRENT);
        }
        else
        {
            bus.mail(new Letter("EnterTaxAmount"), BusIfc.CURRENT);
        }

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
        String strResult = new String("Class:  UpdatePolicyAisle (Revision " +
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
        UpdatePolicyAisle clsUpdatePolicyAisle = new UpdatePolicyAisle();

        // output toString()
        System.out.println(clsUpdatePolicyAisle.toString());
    }                                   // end main()
}
