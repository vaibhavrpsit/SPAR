/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returnoptions/EnterCheckInfoSite.java /main/11 2012/10/29 16:37:48 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse   
 * $
 * Revision 1.4  2004/02/27 22:43:50  baa
 * @scr 3561 returns add trans not found flow
 * Revision 1.3 2004/02/12 16:51:52 mcs
 * Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:52:25 rhafernik @scr 0 Log4J conversion and code
 * cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:20 cschellenger updating to pvcs
 * 360store-current
 * 
 * 
 * 
 * Rev 1.2 Jan 23 2004 16:10:08 baa continue returns developement
 * 
 * Rev 1.1 Dec 19 2003 13:22:50 baa more return enhancements Resolution for
 * 3561: Feature Enhacement: Return Search by Tender
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returnoptions;

import java.util.ArrayList;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;

import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.returns.returncommon.ReturnUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DataInputBeanModel;

//------------------------------------------------------------------------------
/**
 * @version $Revision: /main/11 $
 */
//------------------------------------------------------------------------------
public class EnterCheckInfoSite extends PosSiteActionAdapter
{

    //--------------------------------------------------------------------------
    /**
     * 
     * Displays the screen that request check info and date range to be used on
     * search by tender return.
     * 
     * @param bus the bus arriving at this site
     */
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DataInputBeanModel model = new DataInputBeanModel();
        
        
        //  If re-entering this service use previous data
        if (bus.getCurrentLetter() != null && CommonLetterIfc.RETRY.equals(bus.getCurrentLetter().getName()))
        {
            model = (DataInputBeanModel) ui.getModel(POSUIManagerIfc.RETURN_BY_CHECK);
        } 
        else
        {
            // Display this screen without default data
            // read the application properties and get list of date ranges.
            ArrayList rawData = ReturnUtilities.getPropertyValues(ReturnUtilities.APPLICATION_PROPERTIES,
                                                                  ReturnUtilities.DATE_RANGE_LIST, 
                                                                  ReturnUtilities.DEFAULT_DATE_RANGE);
            UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
            model = ReturnUtilities.setDateRangeList(utility, rawData);
            model.setScannedFields("itemNumberField");
        }

        ui.showScreen(POSUIManagerIfc.RETURN_BY_CHECK, model);

    }

    //--------------------------------------------------------------------------
    /**
     * @param bus the bus undoing its actions
     */
    //--------------------------------------------------------------------------
    public void reset(BusIfc bus)
    {
        ReturnOptionsCargo cargo = (ReturnOptionsCargo) bus.getCargo();
        cargo.setSearchCriteria(null);
        cargo.setPLUItemID(null);
        cargo.setPLUItem(null);
        arrive(bus);
    }
}
