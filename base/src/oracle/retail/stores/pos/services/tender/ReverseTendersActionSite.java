/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/ReverseTendersActionSite.java /main/13 2012/04/30 15:55:32 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   04/26/12 - remove shipping charge line item when tender Undo
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:46 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:55 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:57 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Nov 14 2003 16:42:08   epd
 * removed code that marked tenders for reversal
 * 
 *    Rev 1.0   Nov 11 2003 16:19:42   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import oracle.retail.stores.pos.ado.lineitem.TenderLineItemCategoryEnum;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.domain.lineitem.AbstractTransactionLineItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SaleReturnTransaction;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

/**
 *  Sets up all reversible tenders for a reversal
 */
public class ReverseTendersActionSite extends PosSiteActionAdapter
{
    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        
        TenderADOIfc[] tenders = cargo.getCurrentTransactionADO()
                   .getTenderLineItems(TenderLineItemCategoryEnum.REVERSAL_PENDING);
        
        //when undo selected from the tender option screen, remove transaction level shipping charge line item if it exists.
        TenderableTransactionIfc txn = cargo.getTransaction();
        if ( txn instanceof SaleReturnTransaction )
        {
        	SaleReturnTransaction transaction = (SaleReturnTransaction)txn;
        	if ( transaction.isTransactionLevelSendAssigned() )
        	{
        		AbstractTransactionLineItemIfc lineItems[] = transaction.getLineItems();
        		for (int i = 0; i < lineItems.length; i++)
        		{
        			if ( lineItems[i] instanceof SaleReturnLineItemIfc )
        			{
        				SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)lineItems[i];
        				if ( lineItem.isShippingCharge() )
        				{
        					//remove the shipping charge line item, keep the send package line item for later update
        					transaction.removeLineItem(lineItem.getLineNumber());
        					break;
        				}
        			}
        		}
        	}
        }

        // if we marked any tenders for reversal, go to authorization
        // otherwise go on to delete tenders
        String letter = "Authorize";
        if (tenders.length == 0)
        {
            letter = "Continue";
        }        
        
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }
}
