/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/AlterationReceiptParameterBean.java /main/11 2011/12/05 12:16:17 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.AlterationPLUItemIfc;
import oracle.retail.stores.domain.transaction.RetailTransactionIfc;
import oracle.retail.stores.domain.utility.AlterationIfc;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 * Alteration Receipt Parameter Bean.
 * 
 * $Revision: /main/11 $
 */
public class AlterationReceiptParameterBean extends ReceiptParameterBean
    implements AlterationReceiptParameterBeanIfc
{
    private static final long serialVersionUID = -5101561347723237902L;
    protected Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.AlterationReceiptParameterBeanIfc#getUserLocale()
     */
    public Locale getUserLocale()
    {
        return this.locale;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.AlterationReceiptParameterBeanIfc#setUserLocale(java.util.Locale)
     */
    public void setUserLocale(Locale locale)
    {
        this.locale = locale;
    }

    /**
     * Hardcoded to return {@link ReceiptTypeConstantsIfc#ALTERATION}
     * 
     * @return
     * @see oracle.retail.stores.pos.receipt.ReceiptParameterBean#getDocumentType()
     */
    public String getDocumentType()
    {
        return ReceiptTypeConstantsIfc.ALTERATION;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.ReceiptParameterBean#setDocumentType(java.lang.String)
     */
    public void setDocumentType(String docType)
    {
        // Empty
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.receipt.AlterationReceiptParameterBeanIfc#getAlterations()
     */
    @SuppressWarnings("unchecked")
    public AlterationIfc[] getAlterations()
    {
        List<AlterationIfc> results = new ArrayList<AlterationIfc>();
        Iterator iter = ((RetailTransactionIfc)transaction).getLineItemsIterator();
        while (iter.hasNext())
        {
            SaleReturnLineItemIfc lineItem = (SaleReturnLineItemIfc)iter.next();
            if (lineItem.isAlterationItem())
            {
                AlterationPLUItemIfc pluItem = (AlterationPLUItemIfc)lineItem.getPLUItem();
                results.add(pluItem.getAlteration());
            }
        }
        return results.toArray(new AlterationIfc[results.size()]);
    }
}
