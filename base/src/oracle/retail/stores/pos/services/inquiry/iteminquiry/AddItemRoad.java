/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/AddItemRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/03/14 - Deprecating this file as it is not used.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:31 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:24 PM  Robert Pearse   
 *
 *   Revision 1.1  2004/02/27 17:07:44  lzhao
 *   @scr 3841 Inquiry Options Enhancement
 *   Add the item into transaction when Add button clicked.
 *
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// foundation imports
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

//--------------------------------------------------------------------------
/**
    This road is traveled when the user selects Add button to add item 
    sale transaction.
    @deprecated As of 14.1, AddItemRoad is not used.
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class AddItemRoad extends LaneActionAdapter
{
    //----------------------------------------------------------------------
    /**
        Stores the item info and dept list  in the cargo.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();

        //Item to be added to current transaction
        cargo.setModifiedFlag(true);
    }
}
