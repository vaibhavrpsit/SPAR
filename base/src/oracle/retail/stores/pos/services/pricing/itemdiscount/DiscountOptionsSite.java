/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/itemdiscount/DiscountOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    vikini    02/09/09 - Txn Discount is disabled for a return transactions
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:45 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:36 PM  Robert Pearse
 *
 *   Revision 1.9  2004/07/22 22:10:49  dcobb
 *   @scr 3312 Cannot specify transaction level discount before entering an item
 *   Enabled Trans. Amt. button when there are no line items
 *
 *   Revision 1.8  2004/03/22 18:35:05  cdb
 *   @scr 3588 Corrected some javadoc
 *
 *   Revision 1.7  2004/03/22 03:49:27  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.6  2004/03/16 18:30:46  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.5  2004/02/19 20:42:10  cdb
 *   @scr 3588 Removed unused import.
 *
 *   Revision 1.4  2004/02/19 19:01:10  cdb
 *   @scr 3588     Cleaned up some discount options buttons
 *
 *   Revision 1.3  2004/02/12 16:51:36  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:05  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Feb 03 2004 15:44:50   cdb
 * Removed "cut and paste" carryovers.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.0   Feb 03 2004 14:26:12   cdb
 * Initial revision.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.itemdiscount;

import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.pricing.AbstractDiscountOptionsSite;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//--------------------------------------------------------------------------
/**
 This site displays item and transaction discount options.
 @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class DiscountOptionsSite extends AbstractDiscountOptionsSite
{
  /** revision number supplied by version control **/
  public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

  //----------------------------------------------------------------------
  /**
   Displays item and transaction discount options.
   @param  bus     Service Bus
   **/
  //----------------------------------------------------------------------
  public void arrive(BusIfc bus)
  {
    // Get the ui manager
    POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

    PricingCargo pricingCargo = (PricingCargo) bus.getCargo();

    ListBeanModel model = getModifyItemBeanModel(pricingCargo.getItems());

    configureLocalButtons(model, pricingCargo);

    if (pricingCargo.getTransaction().getTransactionType() == TransactionConstantsIfc.TYPE_RETURN)
    {
      ui.showScreen(POSUIManagerIfc.RETURN_TRANS_DISCOUNT_OPTIONS, model);
    }
    else
    {
      ui.showScreen(POSUIManagerIfc.DISCOUNT_OPTIONS, model);
    }
  }

}
