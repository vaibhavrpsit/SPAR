/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/SuccessLookupPriceCodeRoad.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    npoola    08/06/10 - change the price based on the gift receipt PRICE
 *                         CODE entered
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         4/25/2007 8:52:14 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    5    360Commerce 1.4         1/25/2006 4:11:49 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         1/22/2006 11:45:18 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:15 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/4/2005 10:44:36     Jason L. DeLeau 4201:
 *         Services Impact - Fix PriceCodeConverter extensibility issues.
 *    3    360Commerce1.2         3/31/2005 15:30:15     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:25:39     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:34     Robert Pearse
 *
 *   Revision 1.4  2004/03/26 05:39:05  baa
 *   @scr 3561 Returns - modify flow to support entering price code for not found gift receipt
 *
 *   Revision 1.3  2004/02/12 16:51:49  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:06:10   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:05:42   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:46:06   msg
 * Initial revision.
 * 
 *    Rev 1.3   13 Mar 2002 17:07:42   pdd
 * Modified to use the domain object factory and ifcs.
 * Resolution for POS SCR-1332: Ensure domain objects are created through factory
 * 
 *    Rev 1.2   Feb 05 2002 16:43:20   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 * 
 *    Rev 1.1   Dec 10 2001 17:23:40   blj
 * updated per codereview findings.
 * Resolution for POS SCR-237: Gift Receipt Feature
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

// 3rd party
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.PriceCodeConverter;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.event.PriceChangeIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.GiftReceiptLookupBeanModel;

//------------------------------------------------------------------------------
/**
    
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class SuccessLookupPriceCodeRoad extends PosLaneActionAdapter
{
    //--------------------------------------------------------------------------
    /**
        This road will convert the price code to the CurrencyIfc price
        in the local currency.                 
        @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        GiftReceiptLookupBeanModel model = (GiftReceiptLookupBeanModel) ui.getModel(POSUIManagerIfc.GIFT_RECEIPT_ITEM);
 
        // Get the price code and convert to CurrencyIfc.
        String priceCode = model.getPriceCode();
        BigDecimal price = PriceCodeConverter.getInstance().convertPriceCodeToPrice(priceCode);
        CurrencyIfc priceCurrency = DomainGateway.getBaseCurrencyInstance(price);
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();
        List<PriceChangeIfc> priceChangeList = new ArrayList<PriceChangeIfc>();
        PriceChangeIfc[] priceChangeIfcs = cargo.getPLUItem().getPermanentPriceChanges();
        for (PriceChangeIfc priceChangeIfc : priceChangeIfcs)
        {
            priceChangeIfc.setOverridePriceAmount(priceCurrency);
            priceChangeList.add(priceChangeIfc);
        }
        cargo.getPLUItem().setPermanentPriceChanges((PriceChangeIfc[])priceChangeList.toArray(new PriceChangeIfc[priceChangeList.size()]));
    }

}
