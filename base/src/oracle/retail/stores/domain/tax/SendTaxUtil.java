/* ===========================================================================
* Copyright (c) 2004, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/tax/SendTaxUtil.java /main/15 2012/11/27 12:36:02 rabhawsa Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rabhawsa  11/09/12 - tax should be zero if pincode having no tax rules.
 *                         send functionality.
 *    jswan     07/02/12 - Tax cleanup in preparation for JPA conversion.
 *    jswan     06/29/12 - Rename NewTaxRuleIfc to TaxRulesIfc
 *    sgu       06/22/10 - use type saft vector
 *    sgu       06/22/10 - added the logic to process multiple send package
 *                         instead of just on per order
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/8/2008 6:04:47 PM    Sharma Yanamandra
 *         made NON_TAXABILITY take higher precedence over SEND
 *    3    360Commerce 1.2         3/31/2005 4:29:55 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:11 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:09 PM  Robert Pearse
 *
 *   Revision 1.1  2004/09/13 21:58:30  jdeleau
 *   @scr 6791 Transaction Level Send in regard to taxes
 *
 * ===========================================================================
 */
package oracle.retail.stores.domain.tax;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import oracle.retail.stores.domain.lineitem.KitHeaderLineItem;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
/**
 * This class contains methods that are useful business logic for both
 * transaction and item level send.
 *
 * $Revision: /main/15 $
 */
public class SendTaxUtil
{

    /**
     * For a list of lineItems, we only care about the taxGroupIds
     * that are distinct, because taxGroup and geoCode are what
     * define tax groups.  Gather all the unique taxGroupIds.
     *
     *  @param lineItems Collection of SaleReturnLineItemIfc objects which
     *  the tax groups are collected from.
     *  @return Collection of unique tax groups
     */
    public Collection<Integer> getUniqueTaxGroups(Collection<SaleReturnLineItemIfc> lineItems)
    {
        Set<Integer> taxGroupIDs = new HashSet<Integer>();
        Iterator<SaleReturnLineItemIfc> iter = lineItems.iterator();
        while(iter.hasNext())
        {
            SaleReturnLineItemIfc lineItem = iter.next();
            // For kits add tax groups for all kit components
            if(lineItem instanceof KitHeaderLineItem)
            {
                KitHeaderLineItem kit = (KitHeaderLineItem) lineItem;
                taxGroupIDs.addAll(kit.getTaxGroupIds());

            }
            else
            {
                taxGroupIDs.add(new Integer(lineItem.getTaxGroupID()));
            }
        }
        return taxGroupIDs;
    }

    /**
     * Set the tax rules for a single line Item
     *
     *  @param taxRulesVO Tax rules to set
     *  @param lineItem line item to set them for
     */
    public void setTaxRulesForLineItem(TaxRulesVO taxRulesVO, SaleReturnLineItemIfc lineItem)
    {
        Vector<SaleReturnLineItemIfc> v = new Vector<SaleReturnLineItemIfc>();
        v.add(lineItem);
        setTaxRulesForLineItems(taxRulesVO, v);
    }

    /**
     * Once the tax rules are retrieved, the ItemTaxIfc for each line
     * item needs to know what the rules are.  This method will set the tax
     * rules for each line item, according to what the DB told us they should be.
     *
     *  @param taxRulesVO Value Object containing the tax rules for each tax group.
     *  @param lineItems LineItems to set the tax rules on.
     *  be null if you are accessing this outside the sendCargo service.
     */
    public void setTaxRulesForLineItems(TaxRulesVO taxRulesVO, List<SaleReturnLineItemIfc> lineItems)
    {
        // Set the items off the cargos transaction (that is where the vector comes from)
        for(int i=0; i<lineItems.size(); i++)
        {
            SaleReturnLineItemIfc lineItem = lineItems.get(i);
            // For a kit, the kitHeader should know how to populate the tax for all individual components
            if(lineItem instanceof KitHeaderLineItem)
            {
                KitHeaderLineItem kit = (KitHeaderLineItem) lineItem;
                kit.setSendTaxRules(taxRulesVO);
            }
            // For kits add tax groups for all kit comonents
            TaxRuleIfc[] taxRules = taxRulesVO.getTaxRules(lineItem.getTaxGroupID());
            if (lineItem.getTaxMode() != TaxConstantsIfc.TAX_MODE_NON_TAXABLE)
            {
            	lineItem.getItemPrice().getItemTax().setOriginalTaxMode(lineItem.getTaxMode());
            }
            // If tax rules are found set them, otherwise the items default tax will be used
            if(lineItem.getTaxMode() != TaxConstantsIfc.TAX_MODE_NON_TAXABLE)
            {
                lineItem.getItemPrice().getItemTax().setTaxMode(TaxConstantsIfc.TAX_MODE_STANDARD);
                lineItem.getItemPrice().getItemTax().setSendTaxRules(taxRules);
            }

        }
    }
}
