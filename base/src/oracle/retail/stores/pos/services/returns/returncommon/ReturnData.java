/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/ReturnData.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:58 mszekely Exp $
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
 *    4    360Commerce 1.3         1/22/2006 11:45:17 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:29:44 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:50 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:52 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.5  2004/03/22 22:39:47  epd
 *   @scr 3561 Refactored cargo to get rid of itemQuantities attribute.  Added it to ReturnItemIfc instead.  Refactored to reduce code complexity and confusion.
 *
 *   Revision 1.4  2004/02/12 20:41:40  baa
 *   @scr 0 fixjavadoc
 *
 *   Revision 1.3  2004/02/12 16:51:46  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:06:48   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:14   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 05 2002 16:43:16   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.0   Sep 21 2001 11:24:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

// java imports
import java.io.Serializable;

import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import java.math.BigDecimal;

//--------------------------------------------------------------------------
/**
    This is convenience class; it purpose is to package the data for a
    return to make easy to pass the return data between return services.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class ReturnData implements Serializable
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -7036379684977573325L;

    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
        The origninal customer transaction
    **/
    protected SaleReturnTransactionIfc originalTransaction = null;
    /**
        The sale return line item selected from the list.
    **/
    protected ReturnItemIfc[] returnItems = null;
    /**
        The sale return line item selected from the list.  These are always
        Used to populate the return item info screen.
    **/
    protected PLUItemIfc[] pluItems = null;
    /**
        The sale return line item selected from the list.
    **/
    protected SaleReturnLineItemIfc[] saleReturnLineItems = null;
    /**
        The sale return line item selected from the list.  These contain
        the modifications from the return item info screen.
    **/
    protected BigDecimal[] itemQuantities = null;
    /**
        An array of integers that points to the selected items in
        saleLineItems Array.
    **/

    //----------------------------------------------------------------------
    /**
        Class Constructor.
        <p>
        Initializes the Returns data for item returns.
    **/
    //----------------------------------------------------------------------
        public ReturnData()
        {
        }

    //----------------------------------------------------------------------
    /**
        Returns the original transaction. <P>
        @return The transaction
    **/
    //----------------------------------------------------------------------
    public SaleReturnTransactionIfc getOriginalTransaction()
    {
        return originalTransaction;
    }

    //----------------------------------------------------------------------
    /**
        Sets the orginal transaction. <P>
        @param transaction The transaction
    **/
    //----------------------------------------------------------------------
    public void setOriginalTransaction(SaleReturnTransactionIfc transaction)
    {
        originalTransaction = transaction;
    }

    //---------------------------------------------------------------------
    /**
        Retrieves PLU item array. <P>
        @return PLUItemIfc[]
    **/
    //--------------------------------------------------------------------- 
    public PLUItemIfc[] getPLUItems()
    {                           
        return pluItems;
    }                                  

    //---------------------------------------------------------------------
    /**
        Sets PLU item array. <P>
        @param value list of pluitems
    **/
    //--------------------------------------------------------------------- 
    public void setPLUItems(PLUItemIfc[] value)
    {                           
        pluItems = value;
    }                                  

    //---------------------------------------------------------------------
    /**
        Retrieves SaleReturnLineItemIfc item array. <P>
        @return SaleReturnLineItemIfc[]
    **/
    //--------------------------------------------------------------------- 
    public SaleReturnLineItemIfc[] getSaleReturnLineItems()
    {                           
        return saleReturnLineItems;
    }                                  

    //---------------------------------------------------------------------
    /**
        Sets SaleReturnLineItemIfc item array. <P>
        @param value list of return line items
    **/
    //--------------------------------------------------------------------- 
    public void setSaleReturnLineItems(SaleReturnLineItemIfc[] value)
    {                           
        saleReturnLineItems = value;
    }                                  

    //----------------------------------------------------------------------
    /**
        Sets the array of return items.
        <P>
        @param value list of return items
    **/
    //----------------------------------------------------------------------
    public void setReturnItems(ReturnItemIfc[] value)
    {
        returnItems = value;
    }

    //----------------------------------------------------------------------
    /**
        Returns the array of return items.
        <P>
        @return ReturnItemIfc[]
    **/
    //----------------------------------------------------------------------
    public ReturnItemIfc[] getReturnItems()
    {
        return returnItems;
    }

    //----------------------------------------------------------------------
    /**
        Sets the item quantity array.
        <P>
        @param value the item quantity
        @deprecated since 7.0. quantity added to ReturnItemIfc. no longer needed here
    **/
    //----------------------------------------------------------------------
        public void setItemQuantities(BigDecimal[] value)
        {
                itemQuantities = value;
        }

    //----------------------------------------------------------------------
    /**
        Gets the item quantity array.
        <P>
        @return the item quantity
        @deprecated since 7.0 quantity added to ReturnItemIfc. no longer needed here
    **/
    //----------------------------------------------------------------------
        public BigDecimal[] getItemQuantities()
        {
                return(itemQuantities);
        }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  ReturnTransactionCargo (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
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
}
