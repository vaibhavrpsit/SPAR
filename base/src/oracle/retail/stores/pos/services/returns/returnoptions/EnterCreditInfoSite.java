/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/EnterCreditInfoSite.java /rgbustores_13.4x_generic_branch/4 2011/08/11 19:22:51 ohorne Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    ohorne    07/28/11 - introduced ReturnByCreditBeanModel
 *    cgreene   07/14/11 - tweak search by credit debit and gift card number
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    jswan     06/26/09 - Fixed enableMSR() method header.
 *    jswan     06/26/09 - Fix issues swiping card when looking up transactions
 *                         with credit card.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         12/27/2007 10:39:29 AM Alan N. Sinton  CR
 *         29677: Check in changes per code review.  Reviews are Michael
 *         Barnett and Tony Zgarba.
 *    4    360Commerce 1.3         11/29/2007 5:15:58 PM  Alan N. Sinton  CR
 *         29677: Protect user entry fields of PAN data.
 *    3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 *
 *   Revision 1.5  2004/05/21 20:56:27  mweis
 *   @scr 4902 Returns' INVALID_CARD_NUMBER message and key prompt incorrect
 *
 *   Revision 1.4  2004/02/27 22:43:50  baa
 *   @scr 3561 returns add trans not found flow
 *
 *   Revision 1.3  2004/02/12 16:51:52  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:25  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Jan 23 2004 16:10:10   baa
 * continue returns developement
 *
 *    Rev 1.1   Dec 19 2003 13:22:52   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Dec 17 2003 11:36:46   baa
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import java.util.ArrayList;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ReturnByCreditBeanModel;

/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/4 $
 */
public class EnterCreditInfoSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -6611573283538516287L;

    /**
     * @param bus the bus arriving at this site
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ReturnByCreditBeanModel model;

        //  If re-entering this service use previous data
        String currentLetter = bus.getCurrentLetter().getName();
        if (currentLetter.equals(CommonLetterIfc.RETRY))
        {
            model = (ReturnByCreditBeanModel) ui.getModel(POSUIManagerIfc.RETURN_BY_CREDIT);
        }
        else
        {
            model = new ReturnByCreditBeanModel();

            // read the application properties and get list of date ranges.
            ArrayList<String> rawData = ReturnUtilities.getPropertyValues(ReturnUtilities.APPLICATION_PROPERTIES,
                                                                  ReturnUtilities.DATE_RANGE_LIST,
                                                                  ReturnUtilities.DEFAULT_DATE_RANGE);

            UtilityManagerIfc  utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
            ArrayList<String> i18nData = ReturnUtilities.localalizeDateRangeList(utility, rawData);
            model.setDateRangeList(i18nData);
            model.setScannedFields("itemNumberField");
        }

        // show the screen
        ui.showScreen(POSUIManagerIfc.RETURN_BY_CREDIT, model);
    }
    
    /**
     * Enables the MSR/CPOI device for the card swipe.
     * @param SessionBusIfc
     * @deprecated as of 13.4. No replacement.
     */
    protected void enableMSR(BusIfc bus)
    {
    }

    /**
     * @param bus the bus undoing its actions
     */
    @Override
    public void reset(BusIfc bus)
    {
        ReturnOptionsCargo cargo = (ReturnOptionsCargo)bus.getCargo();
        cargo.setSearchCriteria(null);
        cargo.setPLUItemID(null);
        cargo.setPLUItem(null);
        arrive(bus);
    }
}
