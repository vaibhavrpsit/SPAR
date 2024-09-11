/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/AlternateCurrencyTenderOptionsTDO.java /main/17 2013/10/28 09:04:41 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/25/13 - remove currency type deprecations and use currency
 *                         code instead of description
 *    abondala  09/04/13 - initialize collections
 *    mjwallac  04/25/12 - Fixes for Fortify redundant null check, take2
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    mahising  02/19/09 - Bug_2153 fix Fix the Screen Name of Foreign
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         7/2/2007 5:06:30 PM    Anda D. Cadar
 *         changes to display the amount when tendering with foreign currency
 *    4    360Commerce 1.3         4/25/2007 8:52:43 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:37 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:27 PM  Robert Pearse
 *
 *   Revision 1.10  2004/07/14 18:47:08  epd
 *   @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation
 *
 *   Revision 1.9  2004/06/29 17:48:15  crain
 *   @scr 5482 Foreign Currency: Local Nav buttons are wrong on <ARG> Currency screen
 *
 *   Revision 1.8  2004/06/14 21:49:13  aschenk
 *   @scr 5468 - Foreign Currency: <ARG> Currency screen has wrong screen name and message text.  Fixed to be the same as the requirements.
 *
 *   Revision 1.6  2004/04/21 15:08:58  blj
 *   @scr 3872 - cleanup from code review
 *
 *   Revision 1.5  2004/04/13 17:19:32  crain
 *   @scr 4206 Updating Javadoc
 *
 *   Revision 1.4  2004/04/01 01:46:32  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.3  2004/02/12 16:48:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:23:20  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.6   Feb 05 2004 11:35:20   Tim Fritz
 * Fixed SCR3777
 * Resolution for 3777: Canadian Monies are not being displayed correctly on the Tender screen or Pole Display
 *
 *    Rev 1.5   Jan 07 2004 10:04:46   epd
 * really refactored out TenderHelper this time.
 *
 *    Rev 1.4   Jan 06 2004 11:00:52   epd
 * removed references to TenderHelper and DomainGateway (where appropriate)
 *
 *    Rev 1.3   Jan 05 2004 18:05:12   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash/Canadian Travelers Check/Canadian Check
 *
 *    Rev 1.2   Dec 19 2003 11:34:52   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 *    Rev 1.1   Dec 14 2003 14:41:18   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 *    Rev 1.0   Dec 08 2003 16:02:30   rsachdeva
 * Initial revision.
 * Resolution for POS SCR-3551: Tender using Canadian Cash
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

// java imports
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

import org.apache.log4j.Logger;

/**
 * This tdo builds the alternate currency enter amount screen.
 * 
 * @version $Revision: /main/17 $
 */
public class AlternateCurrencyTenderOptionsTDO extends TDOAdapter implements TDOUIIfc
{
    public static final Logger logger = Logger.getLogger(AlternateCurrencyTenderOptionsTDO.class);
    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/17 $";
    /**
     * Tag for Nationality descriptor
     */
    public static final String NATIONALITY_TAG = "_Nationality";

    /**
     * gift certificate label tag
     */
    public static final String GIFTCERTIFICATE = "GiftCertificate";

    /**
     * gift certificate button
     */
    public static final String STORECREDIT = "StoreCredit";
    /**
     * CashAccepted parameter name constant
     */
    public static String CASHACCEPTED = "CashAccepted";
    /**
     * TravelersCheckAccepted parameter constant
     */
    public static String TRAVELERSCHECKSACCEPTED = "TravelersChecksAccepted";
    /**
     * ChecksAccepted parameter constant
     */
    public static String CHECKSACCEPTED = "ChecksAccepted";
    /**
     * Gift Certificate parameter constant
     */
    public static String GIFTCERTIFICATESACCEPTED = "GiftCertificatesAccepted";

    /**
     * Gift Certificate parameter constant
     */
    public static String STORECREDITACCEPTED = "StoreCreditsAccepted";

    /**
     * tag for screen name constant
     */
    public static final String SCREEN_NAME_TAG = "_ScreenName";

    /**
     * Builds the bean model
     * 
     * @param attributeMap hash map
     * @return POSBaseBeanModel pos base bean model
     */
    @Override
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        PromptAndResponseModel beanModel = new PromptAndResponseModel();
        StatusBeanModel beanStatusModel = new StatusBeanModel();
        String alternateCurrencyNationality =null;
        CurrencyIfc alternateCurrencyInstance = (CurrencyIfc)attributeMap.get(TenderTDOConstants.ALTERNATE_CURRENCY);
        String responseText = LocaleUtilities.formatCurrency(alternateCurrencyInstance.getDecimalValue(),LocaleMap.getLocale(LocaleMap.DEFAULT), false);
        beanModel.setResponseText(responseText);

        BusIfc bus = (BusIfc)attributeMap.get(TenderTDOConstants.BUS);
        UtilityManagerIfc utility =  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String screenName = alternateCurrencyInstance.getCurrencyCode()+ SCREEN_NAME_TAG;
        String alternateCurrencyNationalityTag =
          alternateCurrencyInstance.getCountryCode() + NATIONALITY_TAG;
            alternateCurrencyNationality = utility.retrieveCommonText(screenName,
                                                                         alternateCurrencyNationalityTag);
        String alternateCurrencyStatus = utility.retrieveText(POSUIManagerIfc.STATUS_SPEC,
                BundleConstantsIfc.TENDER_BUNDLE_NAME,"AlternateCurrencyScreenName","Currency");
        beanStatusModel.setScreenName(alternateCurrencyNationality + " " + alternateCurrencyStatus);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        baseModel.setPromptAndResponseModel(beanModel);
        baseModel.setStatusBeanModel(beanStatusModel);
        baseModel.setLocalButtonBeanModel
          (getNavigationBeanModel(alternateCurrencyInstance.getCurrencyCode()));
        return baseModel;
    }

    /**
     * enables/disables local navigation buttons
     * 
     * @param alternateCurrencyDescription alternate currency description
     * @return NavigationButtonBeanModel navigation bean model
     */
    protected NavigationButtonBeanModel getNavigationBeanModel(String alternateCurrencyDescription)
    {
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        UtilityIfc util = null;
        try
        {
            util = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
        //cash button
        String[] cash = util.getParameterValueList(CASHACCEPTED);
        if (cash == null)
        {
            cash = new String[1];
            cash[0] = DomainGateway.getBaseCurrencyInstance().getCurrencyCode();
        }
        boolean enableCash = util.isStringListed(alternateCurrencyDescription,
                                                 cash);
        navModel.setButtonEnabled(CommonActionsIfc.CASH, enableCash);

        //travelers check button
        String[] travelersChecksAccepted = util.getParameterValueList(TRAVELERSCHECKSACCEPTED);
        if (travelersChecksAccepted == null)
        {
            travelersChecksAccepted = new String[1];
            travelersChecksAccepted[0] = DomainGateway.getBaseCurrencyInstance().getCurrencyCode();
        }
        else
        {
            travelersChecksAccepted = extractCheckCurrencyPart(travelersChecksAccepted);
        }
        boolean enableTravelersChecks = util.isStringListed(alternateCurrencyDescription,
                                                            travelersChecksAccepted);
        navModel.setButtonEnabled(CommonActionsIfc.TRAVEL_CHECK, enableTravelersChecks);

        //check button
        String[] checksAccepted = util.getParameterValueList(CHECKSACCEPTED);
        if (checksAccepted == null)
        {
            checksAccepted = new String[1];
            checksAccepted[0] = DomainGateway.getBaseCurrencyInstance().getCurrencyCode();
        }
        else
        {
            checksAccepted = extractCheckCurrencyPart(checksAccepted);
        }
        boolean enableChecks = util.isStringListed(alternateCurrencyDescription,
                                                   checksAccepted);

        navModel.setButtonEnabled(CommonActionsIfc.CHECK, enableChecks);

        // gift certificate button
        String[] giftCertificate = util.getParameterValueList(GIFTCERTIFICATESACCEPTED);
        if (giftCertificate == null)
        {
            giftCertificate = new String[1];
            giftCertificate[0] = DomainGateway.getBaseCurrencyInstance().getCurrencyCode();
        }
        boolean enableGiftCertificate = util.isStringListed(alternateCurrencyDescription,
                                        giftCertificate);
        navModel.setButtonEnabled(CommonActionsIfc.GIFT_CERT, CommonActionsIfc.GIFT_CERTIFICATE, enableGiftCertificate);
        // List of possible tenders
        // TODO: update all buttons to use this method
        ArrayList acceptedTendersList = new ArrayList();
        //acceptedTendersList.add(CASH);
        //acceptedTendersList.add(TRAVEL_CHECK);
        //acceptedTendersList.add(CHECK);
        //acceptedTendersList.add(GIFTCERT);
        acceptedTendersList.add(STORECREDIT);

        HashMap tendersList = new HashMap(1);
        //tendersList.put(STORECREDIT, CASHACCEPTED);
        //tendersList.put(STORECREDIT, TRAVELCHECKSACCEPTED);
        //tendersList.put(STORECREDIT, CHECKSACCEPTED);
        //tendersList(STORECREDIT, GIFTCERTIFICATESACCEPTED);
        tendersList.put(STORECREDIT, STORECREDITACCEPTED);

        Iterator iter = acceptedTendersList.iterator();

        while (iter.hasNext())
        {
            enableTenderButtons(alternateCurrencyDescription,
                    navModel,
                    (String)iter.next(),
                    tendersList,
                    util);
        }

        return navModel;
    }

    /**
     * @param alternateCurrencyDescription
     * @param navModel
     * @param util
     */
    protected void enableTenderButtons(String alternateCurrencyDescription, NavigationButtonBeanModel navModel,
            String acceptedParam, HashMap tList, UtilityIfc util)
    {
        // tender button
        String[] tenderAccepted = util.getParameterValueList((String)tList.get(acceptedParam));
        if (tenderAccepted == null)
        {
            tenderAccepted = new String[1];
            tenderAccepted[0] = DomainGateway.getBaseCurrencyInstance().getCurrencyCode();
        }
        boolean enableTenderAccepted = util.isStringListed(alternateCurrencyDescription, tenderAccepted);
        navModel.setButtonEnabled(acceptedParam, enableTenderAccepted);
    }

    /**
     * Formats pole display line 1.
     * 
     * @param txnADO RetailTransactionADOIfc
     * @return String
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Formats pole display line 2.
     * 
     * @param txnADO RetailTransactionADOIfc
     * @return String
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        return null;
    }

    /**
     * Extracts Currency Part from the Parameter Values for ChecksAccepted and
     * TravelersChecksAccepted.
     * 
     * @param checkList String[]
     * @return An array of Parameter Values with currency part extracted
     */
    protected String[] extractCheckCurrencyPart(String[] checkList)
    {
        String[] currencyChecks;
        if (checkList != null)
        {
            currencyChecks = new String[checkList.length];
            for (int i = 0; (checkList != null && i < checkList.length); i++)
            {
                if (checkList[i].indexOf("CHK") != -1) // not None
                {
                    String extractedCurrency = checkList[i].substring(0, checkList[i].indexOf("CHK"));
                    currencyChecks[i] = extractedCurrency;
                }
                else
                {
                    currencyChecks[i] = checkList[i];
                }
            }
        }
        else
        {
            currencyChecks = new String[0];
        }

        return currencyChecks;
    }
}
