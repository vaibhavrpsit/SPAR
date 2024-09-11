/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/itemcheck/AddItemAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
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
 *  3    360Commerce 1.2         3/31/2005 4:27:09 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:19:31 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:09:24 PM  Robert Pearse   
 *
 * Revision 1.1  2004/08/05 22:17:54  dcobb
 * @scr 6655 Remove letter checks from shuttles.
 * Modified itemcheck service to initialize the modifyFlag to false and set to true when the item is ready to add to the sale.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry.itemcheck;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;

//--------------------------------------------------------------------------
/**
  This aisle is traversed when the item has passed all checks.
  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class AddItemAisle extends PosLaneActionAdapter
{
  /**
      revision number
  **/
  public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

  //----------------------------------------------------------------------
  /**
      Sets the flag in the cargo to add the item.
      @param  bus     Service Bus
  **/
  //----------------------------------------------------------------------
  public void traverse(BusIfc bus)
  {
      //  Set the cargo flag to add the item
      ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();          
      cargo.setModifiedFlag(true);      
  }
}
