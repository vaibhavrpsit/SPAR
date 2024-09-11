/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderTravelersCheckADO.java /main/18 2013/09/05 10:36:15 abondala Exp $
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
 *    kulu      02/22/09 - Fix the bug that foreign currency in tender info is
 *                         always CAD
 *    ddbaker   01/21/09 - Removed tab characters causing alignment problems.
 *    ddbaker   01/21/09 - Update to use Currency Type during creation of
 *                         alternate (foreign) currency objects.
 *    vchengeg  11/07/08 - To fix BAT test failure
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 8:52:52 AM   Anda D. Cadar   I18N
 *         merge
 *         
 *    4    360Commerce 1.3         12/13/2005 4:42:33 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:26 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:57 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/07/15 16:13:22  kmcbride
 *   @scr 5954 (Services Impact): Adding logging to these ADOs, also fixed some exception handling issues.
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
 *    Rev 1.4   Feb 05 2004 13:46:44   rhafernik
 * log4j changes
 * 
 *    Rev 1.3   Jan 08 2004 14:05:12   bwf
 * Refactor Locale for unit test.
 * 
 *    Rev 1.2   Jan 06 2004 11:23:12   epd
 * refactorings to remove unfriendly references to TenderHelper and DomainGateway
 * 
 *    Rev 1.1   Jan 02 2004 09:35:20   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash/Canadian Travelers Check/Canadian Check
 * 
 *    Rev 1.0   Nov 04 2003 11:13:20   epd
 * Initial revision.
 * 
 *    Rev 1.1   Oct 21 2003 10:01:04   epd
 * Refactoring.  Moved RDO tender to abstract class
 * 
 *    Rev 1.0   Oct 17 2003 12:33:52   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

//java imports
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyTypeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.journal.JournalConstants;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.common.utility.LocaleMap;



/**
 *  
 */
public class TenderTravelersCheckADO extends AbstractTenderADO
{
    /**
     * The logger to which log messages will be sent.
     */
    protected static transient Logger logger = Logger.getLogger(TenderTravelersCheckADO.class);
    
    /**
     * no-arg constructor
     * It is intended that the tender factory instantiate this.
     */
    protected TenderTravelersCheckADO() {}
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.AbstractTenderADO#initializeTenderRDO()
     */
    protected void initializeTenderRDO()
    {
        tenderRDO = DomainGateway.getFactory().getTenderTravelersCheckInstance();
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderType()
     */
    public TenderTypeEnum getTenderType()
    {
        return TenderTypeEnum.TRAVELERS_CHECK;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#validate()
     */
    public void validate() throws TenderException
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Validating travelers check information...");   
        }
        
        // 1) Make sure the quantity is a valid quantity
        if (((TenderTravelersCheckIfc)tenderRDO).getNumberChecks() < 1)
        {
            throw new TenderException("Invalid check quantity",
                    TenderErrorCodeEnum.INVALID_QUANTITY);
        }
    }
    
    //----------------------------------------------------------------------
    /**
     This method gets the Locale so that it can be override in the
     unit tests.
     @return Locale
     **/
    //----------------------------------------------------------------------
    protected Locale getLocale()
    {
        return LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#getTenderAttributes()
     */
    public HashMap getTenderAttributes()
    {
        HashMap map = new HashMap(5);
        map.put(TenderConstants.TENDER_TYPE, TenderTypeEnum.TRAVELERS_CHECK);
        map.put(TenderConstants.AMOUNT, 
                getAmount().getStringValue());
        map.put(TenderConstants.COUNT, new Short(((TenderTravelersCheckIfc)tenderRDO).getNumberChecks()));
        //alternate currency
        if(((TenderAlternateCurrencyIfc)tenderRDO).getAlternateCurrencyTendered() != null)
        {
            map.put(TenderConstants.ALTERNATE_AMOUNT,
                    ((TenderAlternateCurrencyIfc)tenderRDO).getAlternateCurrencyTendered()
                    .getStringValue());
            map.put(TenderConstants.FOREIGN_CURRENCY,
                    ((TenderAlternateCurrencyIfc)tenderRDO).getAlternateCurrencyTendered().getType());
        }
        return map;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
     */
    public void setTenderAttributes(HashMap tenderAttributes)
    throws TenderException
    {
        // get the amount
        CurrencyIfc amount = parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT));
        ((TenderTravelersCheckIfc)tenderRDO).setAmountTender(amount);
        //alternate currency
        String alternateAmountValue = (String)tenderAttributes.get(TenderConstants.ALTERNATE_AMOUNT);
        if (alternateAmountValue != null)
        {
            CurrencyIfc alternateAmount = 
                parseAlternateAmount(alternateAmountValue, tenderAttributes);
            ((TenderAlternateCurrencyIfc)tenderRDO).setAlternateCurrencyTendered(alternateAmount);
            CurrencyTypeIfc alternateAmountType = (CurrencyTypeIfc)tenderAttributes.get(TenderConstants.FOREIGN_CURRENCY);
            if (alternateAmountType != null)
            {
                alternateAmount.setType(alternateAmountType);
            }
        }
        // get the number of checks
        ((TenderTravelersCheckIfc)tenderRDO).setNumberChecks(((Short)tenderAttributes.get(TenderConstants.COUNT)).shortValue());
    }
    
    /**
     * Indicates Traveler's Check is a type of PAT Cash
     * @return true for travelers check is less than or equal to $10k
     */
    public boolean isPATCash()
    {
        boolean isPATCash = false;
        if (getAmount().signum() == CurrencyIfc.POSITIVE)
        {
            if (((TenderTravelersCheckIfc)tenderRDO).getNumberChecks() > 1)
            {
                isPATCash = true;
            }
            else if (((TenderTravelersCheckIfc)tenderRDO).getNumberChecks() == 1)
            {
                int result = getAmount().compareTo(DomainGateway.getBaseCurrencyInstance(TenderADOIfc.PAT_CASH_THRESHOLD));
                isPATCash = (result == CurrencyIfc.LESS_THAN || result == CurrencyIfc.EQUALS);
            }
        }
        return isPATCash;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map getJournalMemento()
    {
        Map memento = getTenderAttributes();
        // add tender descriptor
        memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
        return memento;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
     */
    public void fromLegacy(EYSDomainIfc rdo)
    {
        tenderRDO = (TenderTravelersCheckIfc)rdo;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy()
     */
    public EYSDomainIfc toLegacy()
    {
        return tenderRDO;
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.ado.ADOIfc#toLegacy(java.lang.Class)
     */
    public EYSDomainIfc toLegacy(Class type)
    {
        return toLegacy();
    }
    
}
