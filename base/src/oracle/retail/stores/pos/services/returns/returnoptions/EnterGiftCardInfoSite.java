/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/EnterGiftCardInfoSite.java /rgbustores_13.4x_generic_branch/2 2011/06/07 16:44:02 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   06/07/11 - update to first pass of removing pospal project
 *    kelesika  12/15/10 - Enable msr
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse
 *
 *   Revision 1.5  2004/08/04 19:40:15  blj
 *   @scr 6770 - added coded submitted by Pepboys services team.
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
 *    Rev 1.2   Jan 23 2004 16:10:12   baa
 * continue returns developement
 *
 *    Rev 1.1   Dec 19 2003 13:22:54   baa
 * more return enhancements
 * Resolution for 3561: Feature Enhacement: Return Search by Tender
 *
 *    Rev 1.0   Dec 17 2003 11:36:52   baa
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
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

/**
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class EnterGiftCardInfoSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = 7319950714124241792L;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DataInputBeanModel model = new DataInputBeanModel();
        String currentLetter = bus.getCurrentLetter().getName();

        //  If re-entering this service use previous data
        if (currentLetter.equals(CommonLetterIfc.RETRY))
        {
            model = (DataInputBeanModel) ui.getModel(POSUIManagerIfc.RETURN_BY_GIFTCARD);
        }
        else
        {

            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            // read the application properties and get list of date ranges.
            ArrayList<String> rawData =
                ReturnUtilities.getPropertyValues(
                    ReturnUtilities.APPLICATION_PROPERTIES,
                    ReturnUtilities.DATE_RANGE_LIST,
                    ReturnUtilities.DEFAULT_DATE_RANGE);

            model = ReturnUtilities.setDateRangeList(utility, rawData);
            model.setScannedFields("itemNumberField");
            model.setMsrField("cardNumberField");
        }
        ui.showScreen(POSUIManagerIfc.RETURN_BY_GIFTCARD, model);
    }

    /**
     * @param bus the bus undoing its actions
     */
    @Override
    public void reset(BusIfc bus)
    {
        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();
        cargo.setSearchCriteria(null);
        cargo.setPLUItemID(null);
        cargo.setPLUItem(null);
        arrive(bus);
    }
}
