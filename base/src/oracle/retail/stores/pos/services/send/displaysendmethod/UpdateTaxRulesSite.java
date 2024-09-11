/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/displaysendmethod/UpdateTaxRulesSite.java /main/2 2012/11/27 12:36:04 rabhawsa Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* rabhawsa    11/09/12 - tax should be zero if pincode having no tax rules.
*                        send functionality.
* yiqzhao     10/19/12 - Refactor, using DestinationTaxRule station to get tax
*                        rules based on shipping/send destination postal code.
* yiqzhao     10/17/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.send.displaysendmethod;

import java.util.Arrays;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tax.SendTaxUtil;
import oracle.retail.stores.domain.tax.TaxRuleIfc;
import oracle.retail.stores.domain.tax.TaxRulesVO;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.send.address.SendCargo;

/**
 * Undo the send item operation.
 *
 * $Revision: /main/2 $
 */
public class UpdateTaxRulesSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 1L;
    
    //----------------------------------------------------------------------
    /**
     * Arrival at the site.  Try to get the tax rules site.  If its not possible
     * because there are multiple geo codes, then call the displayMultipleGeoCodesSIte.
     *
     * @param bus
     * @see oracle.retail.stores.foundation.tour.ifc.SiteActionIfc#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        SendCargo cargo = (SendCargo) bus.getCargo();
        Letter letter = new Letter(CommonLetterIfc.NEXT);
        if (cargo.isTransactionLevelSendInProgress())
        {
             letter = new Letter(CommonLetterIfc.DONE);
        }
        
        SendTaxUtil util = new SendTaxUtil();
        TaxRulesVO taxRules = cargo.getDestinationTaxRule();
        // If this was successful, set the tax rules..
        if(taxRules!=null)
        {
            util.setTaxRulesForLineItems(taxRules, Arrays.asList(cargo.getLineItems()));
            cargo.getTransaction().setSendTaxRules(taxRules);
            setRulesForCargo(cargo, taxRules);
        }
        bus.mail(letter, BusIfc.CURRENT);
    }
    
    /**
     * Set the tax rules in the cargo object
     * 
     * @param cargo
     * @param taxRulesVO
     */
    private void setRulesForCargo(SendCargo cargo, TaxRulesVO taxRulesVO)
    {
        SaleReturnLineItemIfc[] items = cargo.getLineItems();
        for(int i=0; i<items.length; i++)
        {
            TaxRuleIfc[] otherTaxRules = taxRulesVO.getTaxRules(items[i].getTaxGroupID());
            items[i].getItemPrice().getItemTax().setSendTaxRules(otherTaxRules);
        }
        cargo.setLineItems(items);
    }
}

