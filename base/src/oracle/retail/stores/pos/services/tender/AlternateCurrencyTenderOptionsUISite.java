/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/AlternateCurrencyTenderOptionsUISite.java /main/15 2012/10/16 17:37:28 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/10/12 - Popup menu implementation
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       01/14/09 - use decimal format to set string value of a currency
 *                         object
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         4/25/2007 8:52:46 AM   Anda D. Cadar   I18N
 *         merge
 *
 *    4    360Commerce 1.3         1/22/2006 11:45:03 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:27:13 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:19:37 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:28 PM  Robert Pearse
 *
 *   Revision 1.11  2004/06/18 00:58:56  crain
 *   @scr 5690 Crash when select Cancel (F12) in Alt. Currency screen
 *
 *   Revision 1.10  2004/05/14 14:15:46  aschenk
 *   @scr 5051 - Selecting esc from the currency screen returns the user to the Foreign currency options screen instead of the Tender options screen
 *
 *   Revision 1.9  2004/04/21 15:08:58  blj
 *   @scr 3872 - cleanup from code review
 *
 *   Revision 1.8  2004/04/13 17:19:31  crain
 *   @scr 4206 Updating Javadoc
 *
 *   Revision 1.7  2004/04/09 19:26:01  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.6  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/04/01 01:46:32  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.4  2004/03/31 19:55:07  crain
 *   @scr 4105 Foreign Currency
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.7   Jan 06 2004 11:00:44   epd
 * removed references to TenderHelper and DomainGateway (where appropriate)
 *
 *    Rev 1.6   Jan 06 2004 09:30:58   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash/Canadian Travelers Check/Canadian Check
 *
 *    Rev 1.5   Jan 06 2004 08:22:06   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash/Canadian Travelers Check/Canadian Check
 *
 *    Rev 1.4   Jan 05 2004 18:01:30   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash/Canadian Travelers Check/Canadian Check
 *
 *    Rev 1.3   Dec 22 2003 11:43:04   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 *    Rev 1.2   Dec 19 2003 16:37:44   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 *    Rev 1.1   Dec 09 2003 15:53:18   rsachdeva
 * Alternate Currency
 * Resolution for POS SCR-3551: Tender using Canadian Cash
 *
 *    Rev 1.0   Dec 08 2003 16:10:48   rsachdeva
 * Initial revision.
 * Resolution for POS SCR-3551: Tender using Canadian Cash
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import java.math.BigDecimal;
import java.util.HashMap;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

import org.apache.log4j.Logger;

/**
 * User has selected Alternate tender (US or Canadian).
 * 
 * @version $Revision: /main/15 $
 */
@SuppressWarnings("serial")
public class AlternateCurrencyTenderOptionsUISite extends PosSiteActionAdapter
{

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * alternate currency error resource id
     */
    public static final String ALTERNATE_CURRENCY_ERROR = "AlternateCurrencyError";

    /**
     * tdo tender alternate currency key
     */
    public static final String TDO_TENDER_ALTERNATECURRENCYTENDEROPTIONS = "tdo.tender.AlternateCurrencyTenderOptions";

    /**
     * The logger to which log messages will be sent.
     */
    protected static final Logger logger = Logger.getLogger(AlternateCurrencyTenderOptionsUISite.class);

    /**
     * Displays Tender Options for Alternate Currency
     * 
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        TenderCargo cargo = (TenderCargo) bus.getCargo();
        HashMap<String,Object> attributeMap = cargo.getTenderAttributes();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        CurrencyIfc alternateCurrencyInstance = (CurrencyIfc)attributeMap.get(TenderTDOConstants.ALTERNATE_CURRENCY);
        BigDecimal rate = alternateCurrencyInstance.getBaseConversionRate();
        if (rate.signum() != CurrencyIfc.POSITIVE)
        {
            // Display alternate currency exchange rate error dialog screen
            UIUtilities.setDialogModel(ui, DialogScreensIfc.ACKNOWLEDGEMENT, ALTERNATE_CURRENCY_ERROR);
        }
        else
        {
            TDOUIIfc tdo = null;
            try
            {
                tdo = (TDOUIIfc)TDOFactory.create(TDO_TENDER_ALTERNATECURRENCYTENDEROPTIONS);
            }
            catch (TDOException e)
            {
                logger.error(e.getMessage());
            }
            // Create hash map for TDO
            attributeMap.put(TenderTDOConstants.BUS, bus);
            attributeMap.put(TenderTDOConstants.TRANSACTION, cargo.getCurrentTransactionADO());
            ui.showScreen(POSUIManagerIfc.ALT_CURRENCY, tdo.buildBeanModel(attributeMap));
        }
    }

    /**
     * Depart method retrieves input.
     * 
     * @param bus Service Bus
     */
    @Override
    public void depart(BusIfc bus)
    {
        LetterIfc letter = bus.getCurrentLetter();
        String letterName = bus.getCurrentLetter().getName();
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        NavigationButtonBeanModel navModel = ((POSBaseBeanModel)ui.getModel()).getLocalButtonBeanModel();
        if (navModel != null)
        {
            if(navModel.checkLetter(letterName))
            {
                TenderCargo cargo = (TenderCargo)bus.getCargo();
                CurrencyIfc alternateCurrencyInstance =
                  (CurrencyIfc)cargo.getTenderAttributes().get(TenderTDOConstants.ALTERNATE_CURRENCY);
                // Get the alternate amount
                String alternateCashAmount = LocaleUtilities.formatCurrency(ui.getInput().trim(), LocaleMap.getLocale(LocaleMap.DEFAULT));
                cargo.getTenderAttributes().put(TenderConstants.ALTERNATE_AMOUNT,
                                                alternateCashAmount);
               if (!alternateCurrencyInstance.getStringValue().equalsIgnoreCase(alternateCashAmount))
                {
                    alternateCurrencyInstance.setStringValue(alternateCashAmount);
                    CurrencyIfc baseCurrency =
                      DomainGateway.convertToBase(alternateCurrencyInstance);
                    cargo.getTenderAttributes().put(TenderConstants.AMOUNT,
                                                    baseCurrency.getStringValue());
                }
            }

        }

        if ((letter instanceof ButtonPressedLetter)&& letterName.equals(CommonLetterIfc.UNDO))
        {
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            //clear foriegn amount so that is recalculated if the operator selects foreign currency again.
            cargo.getTenderAttributes().put(TenderConstants.ALTERNATE_AMOUNT, null);
        }
    }
}
