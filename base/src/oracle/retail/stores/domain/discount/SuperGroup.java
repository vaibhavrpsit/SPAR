/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/discount/SuperGroup.java /rgbustores_13.4x_generic_branch/1 2011/05/04 11:48:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   03/12/09 - fix deal used past its applicationLimit error by
 *                         fixing logic in updateDiscountLimits to recursively
 *                         call itself
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 10:00:59 AM  Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:30:16 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/09/23 00:30:53  kmcbride
 *   @scr 7211: Inserting serialVersionUIDs in these Serializable classes
 *
 *   Revision 1.3  2004/02/12 17:13:28  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:27  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:29  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:35:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Dec 16 2002 14:21:20   pjf
 * Deprecated replaceLineItem() in BestDealGroup, clone pricing rules in PLUItem.
 * Resolution for 101: Merge KB discount fixes.
 * 
 *    Rev 1.1   05 Jun 2002 17:11:54   jbp
 * changes for pricing updates
 * Resolution for POS SCR-1626: Pricing Feature
 * 
 *    Rev 1.0   Jun 03 2002 16:50:04   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 22:58:22   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 12:18:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   Feb 05 2002 16:34:22   mpm
 * Modified to use IBM BigDecimal class.
 * Resolution for Domain SCR-27: Employ IBM BigDecimal class
 * 
 *    Rev 1.0   Sep 20 2001 16:13:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 12:36:38   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.domain.discount;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.foundation.utility.Util;

/**
 * SuperGroup aggregates n number of BestDealGroups and allows their
 * manipulation as a single unit. It implements the Composite design pattern to
 * provide the same interface as a single BestDealGroup.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class SuperGroup extends BestDealGroup implements SuperGroupIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = 4109536870044570015L;

    /** revision number supplied by source-code-control system */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * container for the BestDealGroups that this object represents
     */
    public List<BestDealGroupIfc> subgroups = new ArrayList<BestDealGroupIfc>(2);

    /**
     * Default constructor.
     */
    public SuperGroup()
    {
    }

    /**
     * Constructor initializes this SuperGroup with an ArrayList containing
     * subgroups.
     */
    public SuperGroup(List<BestDealGroupIfc> groups)
    {
        subgroups = groups;
    }

    /**
     * Returns a concatenation of the RuleIDs of the BestDealGroups used to
     * compose this group.
     * 
     * @return String
     */
    public String getRuleID()
    {
        String id = "";
        for (BestDealGroupIfc group : subgroups)
        {
            id += group.getRuleID() + ":";
        }
        return id.substring(0, id.length() - 1);
    }

    /**
     * Applies advanced pricing discounts to each individual subgroup.
     */
    public void applyAdvancedPricingDiscounts()
    {
        for (BestDealGroupIfc group : subgroups)
        {
            group.applyAdvancedPricingDiscounts();
        }
    }

    /**
     * Replace a line item in the bestDealGroup winner collection.
     * <P>
     * 
     * @param SaleReturnLineItemIfc line item object
     * @param index index into line item vector
     * @deprecated as of release 5.5 - BestDealGroups should be regenerated when
     *             a line item is replaced. Maintaining a consistent state
     *             between BestDealGroups when replacing an item would be
     *             complex and prone to error.
     */
    public void replaceLineItem(SaleReturnLineItemIfc newItem, int index)
    {
        for (BestDealGroupIfc group : subgroups)
        {
            group.replaceLineItem(newItem, index);
        }
    }

    /**
     * Removes advanced pricing discounts from each individual subgroup.
     */
    public void removeAdvancedPricingDiscounts()
    {
        for (BestDealGroupIfc group : subgroups)
        {
            group.removeAdvancedPricingDiscounts();
        }
    }

    /**
     * Retrieves the sum of total discounts for all subgroups.
     * 
     * @return CurrencyIfc
     */
    public CurrencyIfc getTotalDiscount()
    {
        CurrencyIfc total = DomainGateway.getBaseCurrencyInstance();
        for (BestDealGroupIfc group : subgroups)
        {
            total = total.add(group.getTotalDiscount());
        }
        return total;
    }

    /**
     * Returns an ArrayList containing all discount sources used by subgroups.
     * 
     * @return ArrayList
     */
    @SuppressWarnings("unchecked")
    public ArrayList getSources()
    {
        ArrayList list = new ArrayList();
        for (BestDealGroupIfc group : subgroups)
        {
            list.addAll(group.getSources());
        }
        return list;
    }

    /**
     * Returns an ArrayList containing all discount targets used by subgroups.
     * 
     * @return ArrayList
     */
    @SuppressWarnings("unchecked")
    public ArrayList getTargets()
    {
        ArrayList list = new ArrayList();
        for (BestDealGroupIfc group : subgroups)
        {
            list.addAll(group.getTargets());
        }
        return list;
    }

    /**
     * Returns an ArrayList containing all subgroups.
     * 
     * @return ArrayList
     */
    public List<BestDealGroupIfc> getSubgroups()
    {
        return subgroups;
    }

    /**
     * Sets the ArrayList containing subgroups.
     * 
     * @param ArrayList
     */
    public void setSubgroups(List<BestDealGroupIfc> groups)
    {
        subgroups = groups;
    }

    /**
     * Returns an Iterator over the subgroups.
     * 
     * @return Iterator
     */
    public Iterator<BestDealGroupIfc> subgroups()
    {
        return subgroups.iterator();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.BestDealGroup#clone()
     */
    @Override
    public Object clone()
    {
        SuperGroup newGroup = new SuperGroup();
        setCloneAttributes(newGroup);
        if (!subgroups.isEmpty())
        {
            List<BestDealGroupIfc> subs = new ArrayList<BestDealGroupIfc>(subgroups.size());
            for (BestDealGroupIfc group : subgroups)
            {
                subs.add((BestDealGroupIfc)group.clone());
            }
            newGroup.setSubgroups(subs);
        }
        return newGroup;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.domain.discount.BestDealGroup#toString()
     */
    @Override
    public String toString()
    {
        // result string
        StringBuilder strResult = new StringBuilder("Class:  SuperGroup ");

        strResult.append("(Revision ").append(getRevisionNumber())
                .append(") @").append(hashCode()).append(Util.EOL)
                .append("\tbestDeal:        [")
                .append(isBestDeal()).append("]").append(Util.EOL)
                .append("\ttotalDiscount:   [").append(getTotalDiscount().toString()).append("]").append(Util.EOL);
        
        if (getRuleID() == null)
        {
           strResult.append("\tdiscountRule:    [null]").append(Util.EOL);
        }
        else
        {
           strResult.append("\tdiscountRule:    [").append(getRuleID()).append("]").append(Util.EOL);
        }

        // pass back result
        return (strResult.toString());

    }
}
