/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/nosale/NoSaleCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mdecama   10/21/08 - I18N - Localizing No Sale ReasonCode
     $Log:
      3    360Commerce 1.2         3/31/2005 4:29:09 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:23:42 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:12:45 PM  Robert Pearse
     $
     Revision 1.5  2004/09/27 22:32:02  bwf
     @scr 7244 Merged 2 versions of abstractfinancialcargo.

     Revision 1.4  2004/07/22 15:05:52  awilliam
     @scr 4465 no sale receipt print control transaction header and barcode are missing

     Revision 1.3  2004/02/12 16:51:18  mcs
     Forcing head revision

     Revision 1.2  2004/02/11 21:51:48  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.0   Aug 29 2003 16:03:06   CSchellenger
 * Initial revision.
 *
 *    Rev 1.0   Apr 29 2002 15:13:54   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:40:12   msg
 * Initial revision.
 *
 *    Rev 1.1   22 Oct 2001 16:11:30   pdd
 * Removed old security access methods and data.
 * Added getAccessFunctionID().
 * Resolution for POS SCR-219: Add Tender Limit Override
 *
 *    Rev 1.0   Sep 21 2001 11:31:56   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:10:10   msg
 * header update
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.nosale;

// java imports
import java.lang.reflect.Field;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.pos.services.common.AbstractFinancialCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.transaction.NoSaleTransactionIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.CashDrawerCargoIfc;

//--------------------------------------------------------------------------
/**
    This is the cargo object for the NoSale service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class NoSaleCargo extends AbstractFinancialCargo implements CashDrawerCargoIfc
{
    /**
     * GeneratedSerialVersionUID
     */
    private static final long serialVersionUID = 1L;

    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
        The reason code for the No Sale
        @deprecated as of 13.1 Use {@link #selectedReasonCode}
    **/
    protected String reasonCode;

    /**
     * Selected Reason Code
     */
    protected LocalizedCodeIfc selectedReasonCode = DomainGateway.getFactory().getLocalizedCode();

    /**
     * The Code List for CODE_LIST_NO_SALE_REASON_CODES
     */
    protected CodeListIfc localizedReasonCodes = null;
    /**
        The cashdrawer online indicator.
    **/
    protected boolean cashDrawerOnline = true;
    /**
        The cashdrawer open indicator.
    **/
    protected boolean cashDrawerHasBeenClosed = false;
    /**
     * hold the nosale transaction for the receipt
     */
    protected NoSaleTransactionIfc trans = null;

    //----------------------------------------------------------------------
    /**
        Returns the reason code
        <P>
        @return The reason code
        @deprecated as of 13.1 Use {@link #getSelectedReasonCode()}
    **/
    //----------------------------------------------------------------------
    public String getReasonCode()
    {
        return selectedReasonCode.getCode();
    }

    //----------------------------------------------------------------------
    /**
        Sets the reason code
        <P>
        @param  code   The reason code
        @deprecated as of 13.1. Use {@link #setSelectedReasonCode(LocalizedCodeIfc)}
    **/
    //----------------------------------------------------------------------
    public void setReasonCode(String code)
    {
        selectedReasonCode.setCode(code);
    }

    //----------------------------------------------------------------------
    /**
        Returns the cash drawer status
        <P>
        @return the cash drawer status
    **/
    //----------------------------------------------------------------------
    public boolean getCashDrawerOnline()
    {
        return cashDrawerOnline;
    }

    //----------------------------------------------------------------------
    /**
        Sets the cash drawer status
        <P>
        @param  the cash drawer status
    **/
    //----------------------------------------------------------------------
    public void setCashDrawerOnline(boolean value)
    {
        cashDrawerOnline = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns the cash drawer open status
        <P>
        @return the cash drawer status
    **/
    //----------------------------------------------------------------------
    public boolean getCashDrawerHasBeenClosed()
    {
        return cashDrawerHasBeenClosed;
    }

    //----------------------------------------------------------------------
    /**
        Sets the cash drawer open status
        <P>
        @param  the cash drawer status
    **/
    //----------------------------------------------------------------------
    public void setCashDrawerHasBeenClosed(boolean value)
    {
        cashDrawerHasBeenClosed = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns the cash drawer retry flag
        <P>
        @return true if the retry is on cash drawer; false is for printer
    **/
    //----------------------------------------------------------------------
    public boolean getDeviceIsCashDrawer()
    {
        return true;
    }

    //----------------------------------------------------------------------
    /**
        Sets the cash drawer retry flag
        <P>
        @param boolean value of the flag
    **/
    //----------------------------------------------------------------------
    public void setDeviceIsCashDrawer(boolean value)
    {
    }

    //----------------------------------------------------------------------
    /**
        Returns the function ID whose access is to be checked.
        @return int Role Function ID
    **/
    //----------------------------------------------------------------------
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.NO_SALE;
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        // verbose flag
        boolean bVerbose = false;
        // result string
        String strResult = new String("Class:  NoSaleCargo (Revision " +
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

        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
    //----------------------------------------------------------------------
    /**
     Set's the no sale transaction
     <P>
     @param noSaleTransactionIfc
     **/
    //----------------------------------------------------------------------
    public void setNoSaleTrans(NoSaleTransactionIfc value)
    {
        trans = value;
    }
//----------------------------------------------------------------------
    /**
     returns the no sale transaction
     <P>
     @return noSaleTransactionIfc
     **/
    //----------------------------------------------------------------------
    public NoSaleTransactionIfc getNoSaleTrans()
    {
        return trans;
    }

    /**
     * @return the localizedReasonCodes
     */
    public CodeListIfc getLocalizedReasonCodes()
    {
        return localizedReasonCodes;
    }

    /**
     * @param localizedReasonCode the localizedReasonCode to set
     */
    public void setLocalizedReasonCodes (CodeListIfc localizedReasonCodes)
    {
        this.localizedReasonCodes = localizedReasonCodes;
    }

    /**
     * @return the selectedReasonCode
     */
    public LocalizedCodeIfc getSelectedReasonCode()
    {
        return selectedReasonCode;
    }

    /**
     * @param selectedReasonCode the selectedReasonCode to set
     */
    public void setSelectedReasonCode(LocalizedCodeIfc selectedReasonCode)
    {
        this.selectedReasonCode = selectedReasonCode;
    }



}
