/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/pricing/employeediscount/DiscountOptionsSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 16:17:09 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    vikini    02/06/09 - Discount is disabled for a return transaction
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:46 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:59 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:36 PM  Robert Pearse
 *
 *   Revision 1.6  2004/02/20 17:34:58  cdb
 *   @scr 3588 Removed "developmental" log entries from file header.
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.pricing.employeediscount;

import oracle.retail.stores.domain.transaction.TransactionConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.pricing.AbstractDiscountOptionsSite;
import oracle.retail.stores.pos.services.pricing.PricingCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//--------------------------------------------------------------------------
/**
 This site displays employee discount options.
 @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//--------------------------------------------------------------------------
public class DiscountOptionsSite extends AbstractDiscountOptionsSite
{
  /** revision number supplied by version control **/
  public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

  //----------------------------------------------------------------------
  /**
   Displays the employee discount options
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
      ui.showScreen(POSUIManagerIfc.RETURN_TRANS_EMPLOYEE_DISCOUNT_OPTIONS, model);
    }
    else
    {
      ui.showScreen(POSUIManagerIfc.EMPLOYEE_DISCOUNT_OPTIONS, model);
    }
  }
}
