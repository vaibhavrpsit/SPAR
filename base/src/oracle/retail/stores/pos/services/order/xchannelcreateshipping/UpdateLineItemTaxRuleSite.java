/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/order/xchannelcreateshipping/UpdateLineItemTaxRuleSite.java /main/1 2012/10/22 15:36:15 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     10/22/12 - Remove unnecessary code.
* yiqzhao     10/19/12 - Refactor, using DestinationTaxRule station to get tax
*                        rules based on shipping/send destination postal code.
* yiqzhao     10/15/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.order.xchannelcreateshipping;


import java.util.ArrayList;
import java.util.List;


import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tax.SendTaxUtil;
import oracle.retail.stores.domain.transaction.TransactionTotalsIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;

//--------------------------------------------------------------------------
/**
    This site calls the UI manager to display the enter pickup customer data
    screen.
    @version $Revision: /main/1 $
**/
//--------------------------------------------------------------------------
@SuppressWarnings("serial")
public class UpdateLineItemTaxRuleSite extends PosSiteActionAdapter
{
    //----------------------------------------------------------------------
    /**
        This method calls the UI manager to display the enter pickup customer data
        screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        XChannelShippingCargo cargo = (XChannelShippingCargo) bus.getCargo();

        // Set the tax rules on the line items..
        if (cargo.getDestinationTaxRule()!=null)
        {
        	List<SaleReturnLineItemIfc> lineItems = new ArrayList<SaleReturnLineItemIfc>();

        	for (int i=0; i<cargo.getTransaction().getLineItems().length; i++)
        	{
        		SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)cargo.getTransaction().getLineItems()[i];
        		if ( lineItem.isSelectedForItemModification() )
        		{
        			lineItems.add(lineItem);
        		}
        	}
        	if (lineItems.size()>0)
        	{
        		SendTaxUtil sendTaxUtil = new SendTaxUtil();
        		sendTaxUtil.setTaxRulesForLineItems(cargo.getDestinationTaxRule(), lineItems);
	        	//recalculate tax
	        	TransactionTotalsIfc totals = cargo.getTransaction().getTransactionTotals();
	        	cargo.getTransaction().updateTransactionTotals();
        	}
        }
       
        bus.mail(new Letter(CommonLetterIfc.NEXT), BusIfc.CURRENT);
    }  
}