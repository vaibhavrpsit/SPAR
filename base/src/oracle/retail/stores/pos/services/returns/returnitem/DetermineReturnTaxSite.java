/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnitem/DetermineReturnTaxSite.java /main/12 2012/07/02 10:12:51 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *
 * ===========================================================================
 * $Log:
 3    360Commerce 1.2         3/31/2005 4:27:44 PM   Robert Pearse   
 2    360Commerce 1.1         3/10/2005 10:20:56 AM  Robert Pearse   
 1    360Commerce 1.0         2/11/2005 12:10:34 PM  Robert Pearse   
 *
Revision 1.3  2004/06/11 15:35:17  mweis
@scr 0 Remove unused import / fix eclipse build break
 *
Revision 1.2  2004/06/11 13:59:35  mkp1
@scr 2775 More Tax - Returns
 *
Revision 1.1  2004/06/11 12:37:56  mkp1
@scr 2775 More Tax - Returns
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnitem;

import java.util.Arrays;

import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ReadNewTaxRuleTransaction;
import oracle.retail.stores.domain.lineitem.ReturnItemIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;


/**
 * @author mkp1
 */
public class DetermineReturnTaxSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -7258567629832539257L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ReturnItemCargo cargo = (ReturnItemCargo) bus.getCargo();
        
        String storeID = "";
        
        if( cargo.getStoreStatus() != null
                && cargo.getStoreStatus().getStore() != null)
        { 
            storeID = cargo.getStoreStatus().getStore().getStoreID();
        }
        SaleReturnLineItemIfc[] saleReturnLineItems = cargo.getReturnSaleLineItems();
        //ReturnItemIfc[] returnItems = cargo.getReturnItems();
        StoreIDTaxGroupInformationHolder storeIDTaxGroupInformationHolder = new StoreIDTaxGroupInformationHolder();
        
        //Collect all items that have a different store number
        //on the return.
        for(int i = 0; i < saleReturnLineItems.length; i++ )
        {
            ReturnItemIfc returnItem = saleReturnLineItems[i].getReturnItem();
            if(returnItem != null && returnItem.getStore() != null)
            {
                if(storeID.equals(returnItem.getStore().getStoreID()) == false)
                {
                    storeIDTaxGroupInformationHolder.addReturnItem(saleReturnLineItems[i]);
                }
            }
        }
        
        String[] storeIDs = storeIDTaxGroupInformationHolder.getStoreNumbers();
        ReadNewTaxRuleTransaction taxRuleTransaction = null;
        
        for(int i = 0; i < storeIDs.length; i++)
        {
            try
            {
                Integer groupIDs[] = storeIDTaxGroupInformationHolder.getGroupIDs(storeIDs[i]);
                
                if(groupIDs.length > 0 )
                {
                    taxRuleTransaction = (ReadNewTaxRuleTransaction) DataTransactionFactory.create(DataTransactionKeys.READ_NEW_TAX_RULE_TRANSACTION);
                    TaxRulesVO taxRulesVO = taxRuleTransaction.getTaxRulesByStore(storeIDs[i], Arrays.asList(groupIDs));
                    if ( taxRulesVO.hasTaxRules() )
                    {
                        for(int j = 0; j < groupIDs.length; j++)
                        {
                            TaxRuleIfc[] taxRules = taxRulesVO.getTaxRules(groupIDs[j].intValue());
                            saleReturnLineItems = storeIDTaxGroupInformationHolder.getReturnItems(storeIDs[i], groupIDs[j]);
                            for(int k = 0; k < saleReturnLineItems.length; k++)
                            {
                                saleReturnLineItems[k].getPLUItem().setTaxRules(taxRules);
                            }
                        }
                    }
                }

            }
            catch(DataException de)
            {
                if(logger.isInfoEnabled())
                {
                    logger.info("Tax rules do not exist for store " + storeIDs[i] + " using defaults stores tax rules");
                }                
            }
        }
        
        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);
                                
    }

}
