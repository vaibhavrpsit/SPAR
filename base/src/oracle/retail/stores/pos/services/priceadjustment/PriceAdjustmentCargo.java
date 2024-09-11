/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/priceadjustment/PriceAdjustmentCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:03 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:28 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:19 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:22 PM  Robert Pearse   
 *
 *   Revision 1.6  2004/06/07 14:58:49  jriggins
 *   @scr 5016 Added logic to persist previously entered transactions with price adjustments outside of the priceadjustment service so that a user cannot enter the same receipt multiple times in a transaction.
 *
 *   Revision 1.5  2004/04/27 21:31:14  jriggins
 *   @scr 3979 Code review cleanup
 *
 *   Revision 1.4  2004/04/19 03:28:11  jriggins
 *   @scr 3979 Added setPriceAdjustmentLineItems()
 *
 *   Revision 1.3  2004/03/30 23:49:17  jriggins
 *   @scr 3979 Price Adjustment feature dev
 *
 *   Revision 1.2  2004/03/30 00:04:59  jriggins
 *   @scr 3979 Price Adjustment feature dev
 *
 *   Revision 1.1  2004/03/05 16:34:26  jriggins
 *   @scr 3979 Price Adjustment additions
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.priceadjustment;

import oracle.retail.stores.domain.lineitem.PriceAdjustmentLineItemIfc;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;

//--------------------------------------------------------------------------
/**
    Cargo for the priceadjustment service.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class PriceAdjustmentCargo
    extends ReturnFindTransCargo
    //implements PriceAdjustmentCargoIfc
{
    protected static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /**
     * The dialog ID of the dialog to show if necessary
     */
    protected String dialogID;

    /**
     * List of price adjustment line items
     */
    protected PriceAdjustmentLineItemIfc[] priceAdjustmentLineItems;    
    
    public PriceAdjustmentCargo()
    {
        super();
    }
    
    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        <P>
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult =
            new String("Class:  PriceAdjustementSCargo (Revision " + getRevisionNumber() + ")" + hashCode());
        return (strResult);
    } // end toString()

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()

    //----------------------------------------------------------------------
    /**
        Returns the dialog ID of the dialog to show
        @return String representing the dialog ID
    **/
    //----------------------------------------------------------------------
    public String getDialogID()
    {        
        return dialogID;
    }

    //----------------------------------------------------------------------    
    /**
     * Sets the dialog ID of the dialog to show
     * @param dialogID The dialogID to set.
     */
    //----------------------------------------------------------------------    
    public void setDialogID(String dialogID)
    {
        this.dialogID = dialogID;
    }

    //----------------------------------------------------------------------
    /**
     * Sets list of price adjustment line items.
     * 
     * @param priceAdjustmentLineItems
     */
    //----------------------------------------------------------------------
    public void setPriceAdjustmentLineItems(PriceAdjustmentLineItemIfc[] priceAdjustmentLineItems)
    {
        this.priceAdjustmentLineItems = priceAdjustmentLineItems;        
    }

    //--------------------------------------------------------------------------
    /**
     * Gets list of price adjustment line items.
     * 
     * @return Returns the list of price adjustment line items or null if none exist.
     */
    //--------------------------------------------------------------------------
    public PriceAdjustmentLineItemIfc[] getPriceAdjustmentLineItems()
    {
        return priceAdjustmentLineItems;
    }
}
