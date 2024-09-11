/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderCashADO.java /main/19 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    ddbaker   01/21/09 - Update to use Currency Type during creation of
 *                         alternate (foreign) currency objects.
 *    ranojha   11/13/08 - Fixed Foreign Currency Till Reconciliation
 *    vchengeg  11/07/08 - To fix BAT test failure
 *    cgreene   11/06/08 - add isCollected to tenders for printing just
 *                         collected tenders
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 8:52:55 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    4    360Commerce 1.3         12/13/2005 4:42:32 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:22 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:54 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:48 PM  Robert Pearse   
 *
 *   Revision 1.3.4.1  2004/10/15 18:50:27  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.4  2004/10/07 18:55:55  bwf
 *   @scr 7314, 7315 Cash is not change, but a refund in return and redeem.
 *
 *   Revision 1.3  2004/04/22 21:03:53  epd
 *   @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 *   Revision 1.2  2004/02/12 16:47:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.5   Feb 05 2004 13:46:24   rhafernik
 * log4j changes
 * 
 *    Rev 1.4   Jan 08 2004 14:05:10   bwf
 * Refactor Locale for unit test.
 * 
 *    Rev 1.3   Jan 06 2004 11:22:58   epd
 * refactorings to remove unfriendly references to TenderHelper and DomainGateway
 * 
 *    Rev 1.2   Dec 17 2003 16:54:54   epd
 * Updated to remove unnecesarry instanceof checks
 * 
 *    Rev 1.1   Dec 10 2003 16:06:42   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash
 * 
 *    Rev 1.0   Nov 04 2003 11:13:10   epd
 * Initial revision.
 * 
 *    Rev 1.1   Oct 21 2003 10:00:56   epd
 * Refactoring.  Moved RDO tender to abstract class
 * 
 *    Rev 1.0   Oct 17 2003 12:33:44   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCashIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.common.utility.LocaleMap;

/**
 *  A Cash Tender.
 */
public class TenderCashADO extends AbstractTenderADO
{
    private static final long serialVersionUID = 5897474122012230364L;

    protected boolean refundCash = false;
    
    /**
     * No-arg constructor used by factory
     * Note: the constructor is protected by design.
     */
    protected TenderCashADO() {}
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.AbstractTenderADO#initializeTenderRDO()
     */
    protected void initializeTenderRDO()
    {
        tenderRDO = DomainGateway.getFactory().getTenderCashInstance();
    }
    
    /**
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getTenderType()
     */
    public TenderTypeEnum getTenderType()
    {
        return TenderTypeEnum.CASH;
    }
    
    /**
     * There are no special validations to perform on Cash.
     * Limits are checked the Cash tender group.
     * @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#validate()
     */
    public void validate() throws TenderException
    { /* nothing to do for cash. */ }


    /**
     * This method gets the Locale so that it can be override in the unit tests.
     * 
     * @return Locale
     */
    protected Locale getLocale()
    {
        return LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderAttributes()
     */
    public HashMap<String,Object> getTenderAttributes()
    {
        HashMap<String,Object> map = new HashMap<String,Object>(0);
        map.put(TenderConstants.TENDER_TYPE, getTenderType());
        map.put(TenderConstants.AMOUNT, getAmount().getStringValue());
        if(((TenderAlternateCurrencyIfc)tenderRDO).getAlternateCurrencyTendered() != null)
        {
            map.put(TenderConstants.ALTERNATE_AMOUNT,
                    ((TenderAlternateCurrencyIfc)tenderRDO).getAlternateCurrencyTendered()
                    .getStringValue());
            map.put(TenderConstants.FOREIGN_CURRENCY,
                    ((TenderAlternateCurrencyIfc)tenderRDO).getAlternateCurrencyTendered().getType());
        }
        map.put(TenderConstants.COLLECTED, Boolean.valueOf(tenderRDO.isCollected()));
        return map;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
     */
    public void setTenderAttributes(HashMap tenderAttributes) throws TenderException
    {
        // get the amount
        CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
        tenderRDO.setAmountTender(amount);
        String alternateAmountValue = (String)tenderAttributes.get(TenderConstants.ALTERNATE_AMOUNT);
        if (alternateAmountValue != null)
        {
            CurrencyIfc alternateAmount = parseAlternateAmount(alternateAmountValue, tenderAttributes);
            ((TenderAlternateCurrencyIfc)tenderRDO).setAlternateCurrencyTendered(alternateAmount);
            CurrencyTypeIfc alternateAmountType = (CurrencyTypeIfc)tenderAttributes.get(TenderConstants.FOREIGN_CURRENCY);
            if (alternateAmountType != null)
            {
                alternateAmount.setType(alternateAmountType);
            }
        }
        
        Boolean collected = (Boolean)tenderAttributes.get(TenderConstants.COLLECTED);
        if (collected != null)
        {
            tenderRDO.setCollected(collected.booleanValue());
        }
    }
    
    /**
     * Indicates Cash is a type of PAT Cash
     * @return true
     */
    public boolean isPATCash()
    {
        return (getAmount().signum() == CurrencyIfc.POSITIVE);
    }

    /**
     * This method returns whether the cash is a refund or not.
     * 
     * @return Returns the refundCash.
     */
    public boolean isRefundCash()
    {
        return refundCash;
    }

    /**
     * This method sets whether the cash is a refund or not.
     * 
     * @param refundCash The refundCash to set.
     */
    public void setRefundCash(boolean refundCash)
    {
        this.refundCash = refundCash;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map<String,Object> getJournalMemento()
    {
        // We can reuse the tender attributes for journalling purposes
        Map<String,Object> memento = getTenderAttributes();
        // add tender descriptor
        memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
        return memento;
    }
    
    /**
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        //assert(rdo instanceof TenderCashIfc);
        
        tenderRDO = (TenderCashIfc)rdo;
    }

    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        // update with current amount
        return tenderRDO;
    }
    
    /**
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }
}
