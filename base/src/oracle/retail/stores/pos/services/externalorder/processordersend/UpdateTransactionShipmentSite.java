/* ===========================================================================
* Copyright (c) 2010, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/externalorder/processordersend/UpdateTransactionShipmentSite.java /main/9 2012/12/11 14:33:11 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  12/11/12 - Fixing HP Fortify redundant null check issues
 *    yiqzhao   10/19/12 - Refactor by using DestinationTaxRule station to get
 *                         new tax rules from shipping destination postal code.
 *    yiqzhao   04/16/12 - refactor store send from transaction totals
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    sgu       06/22/10 - added the logic to process multiple send package
 *                         instead of just on per order
 *    cgreene   05/26/10 - convert to oracle packaging
 *    sgu       05/18/10 - set external send flag on shipping packages
 *    acadar    05/17/10 - temporarily rename the package
 *    acadar    05/17/10 - fix compilation error
 *    acadar    05/17/10 - incorporated feedback from code review
 *    acadar    05/14/10 - initial version for external order processing
 *    acadar    05/14/10 - initial version
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.externalorder.processordersend;

// java imports


import java.util.List;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.lineitem.SendPackageLineItemIfc;
import oracle.retail.stores.domain.tax.SendTaxUtil;
import oracle.retail.stores.domain.transaction.SaleReturnTransactionIfc;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;


/**
 * Retrieves send method selected, adds to transaction totals, journals current
 * send information
 * @author acadar
 */
public class UpdateTransactionShipmentSite extends PosSiteActionAdapter
{


    /**
     *  Serial version UID
     */
    private static final long serialVersionUID = 1L;


    /**
     * Retrieves selected shipping method and calculate totals.
     *
     * @param bus the bus arriving at this site
     */
    public void arrive(BusIfc bus)
    {
       ProcessOrderSendCargo cargo = (ProcessOrderSendCargo) bus.getCargo();
       LetterIfc letter = new Letter(CommonLetterIfc.DONE);


       ShippingMethodIfc selectedMethodOfShipping = cargo.getShippingMethod();
       SaleReturnTransactionIfc transaction = cargo.getTransaction();

       TransactionTotalsIfc totals = transaction.getTransactionTotals();

       //Add send packages info
       SendPackageLineItemIfc sendPackage = transaction.addSendPackageInfo(selectedMethodOfShipping, cargo.getShipToCustomer());
       sendPackage.setExternalSendFlag(true);

       //Assign Send label count on Sale Return Line Items
       List<SaleReturnLineItemIfc> items = cargo.getSaleReturnSendLineItems();

      if(null != items)
      {
       for (SaleReturnLineItemIfc item : items)
       {
    	   item.setItemSendFlag(true);
    	   item.setSendLabelCount(transaction.getItemSendPackagesCount());
       }

           SendTaxUtil sendTaxUtil = new SendTaxUtil();
           sendTaxUtil.setTaxRulesForLineItems(cargo.getDestinationTaxRule(), items);
      }
       transaction.updateTransactionTotals();   // Must do this to force tax recalculation
       totals.setBalanceDue(totals.getGrandTotal());
       bus.mail(letter, BusIfc.CURRENT);

    }


}
