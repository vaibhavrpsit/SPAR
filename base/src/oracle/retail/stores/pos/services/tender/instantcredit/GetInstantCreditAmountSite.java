/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/instantcredit/GetInstantCreditAmountSite.java /main/16 2011/12/05 12:16:22 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/12/10 - use default locale for display of currency
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    sgu       02/16/09 - reponse text must be localized
 *    sgu       01/15/09 - convert string to decimal format before calling
 *                         CurrencyIfc.setStringValue
 *
 * ===========================================================================
 * $Log:
 * 6    360Commerce 1.5         5/30/2007 9:01:57 AM   Anda D. Cadar   code
 *      cleanup
 * 5    360Commerce 1.4         5/18/2007 9:19:18 AM   Anda D. Cadar   always
 *      use decimalValue toString
 * 4    360Commerce 1.3         4/25/2007 8:52:44 AM   Anda D. Cadar   I18N
 *      merge
 *
 * 3    360Commerce 1.2         3/31/2005 4:28:15 PM   Robert Pearse
 * 2    360Commerce 1.1         3/10/2005 10:21:49 AM  Robert Pearse
 * 1    360Commerce 1.0         2/11/2005 12:11:11 PM  Robert Pearse
 *
 *Revision 1.1  2004/04/06 20:22:50  epd
 *@scr 4263 Updates to move instant credit enroll to sub tour
 *
 *Revision 1.3  2004/02/12 16:48:22  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:22:51  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Jan 23 2004 12:33:12   nrao
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.instantcredit;

// foundation imports
import java.math.BigDecimal;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;

//--------------------------------------------------------------------------
/**
    Displays screen for user to confirm or enter in amount to be applied to
    Instant Credit
    @version $Revision: /main/16 $
**/
//--------------------------------------------------------------------------
public class GetInstantCreditAmountSite extends PosSiteActionAdapter
{
    /** revision number supplied by version control **/
    public static final String revisionNumber = "$Revision: /main/16 $";

    //----------------------------------------------------------------------
    /**
       The arrive method displays the screen.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        TenderCargo cargo = (TenderCargo) bus.getCargo();

        // get amount if it already exists
        String balance = (String) cargo.getTenderAttributes().get(TenderConstants.AMOUNT);

        PromptAndResponseModel model = new PromptAndResponseModel();
        if (balance != null)
        {
        	String responseText = LocaleUtilities.formatCurrency(new BigDecimal(balance),LocaleMap.getLocale(LocaleMap.DEFAULT), false);
            model.setResponseText(responseText);
        }
        POSBaseBeanModel beanModel = new POSBaseBeanModel();
        beanModel.setPromptAndResponseModel(model);
        ui.showScreen(POSUIManagerIfc.ENTER_AMOUNT, beanModel);
    }

    //-----------------------------------------------------------------------
    /**
       The depart method captures the user input.
       @param bus    Service Bus
    **/
    //-----------------------------------------------------------------------
    public void depart(BusIfc bus)
    {
        LetterIfc letter = (LetterIfc) bus.getCurrentLetter();

        // If the user accepts existing amount or enters new amount
        if (letter.getName().equals("Next"))
        {
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            // Get the amount from the screen
            String amount = LocaleUtilities.parseCurrency(ui.getInput().trim(), LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE)).toString();
            CurrencyIfc balance = DomainGateway.getBaseCurrencyInstance(amount);
            TenderCargo cargo = (TenderCargo)bus.getCargo();
            cargo.getTenderAttributes().put(TenderConstants.AMOUNT, balance.getDecimalValue().toString());
        }
    }
}
