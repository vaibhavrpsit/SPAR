/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/check/CheckNumberEnteredAisle.java /rgbustores_13.4x_generic_branch/4 2011/07/27 14:44:54 rrkohli Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   06/27/11 - Encryption CR
 *    cgreene   07/12/11 - update generics
 *    ohorne    05/06/11 - created class
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.check;

import java.util.HashMap;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel;

/**
 * Collects User Entered Check Number 
 */
@SuppressWarnings("serial")
public class CheckNumberEnteredAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    public static final String LANENAME = "CheckNumberEnteredAisle";

    /**
     * Sets user entered check number into the cargo's tender attributes  
     * @param bus the bus traversing this lane
     **/
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        CheckEntryBeanModel model = (CheckEntryBeanModel)ui.getModel(POSUIManagerIfc.ENTER_CHECK_NUMBER);
        TenderCargo cargo = (TenderCargo)bus.getCargo();
        
        HashMap<String, Object> tenderAttributes = cargo.getTenderAttributes();
        tenderAttributes.put(TenderConstants.CHECK_NUMBER, model.getCheckNumber());

        bus.mail(new Letter(CommonLetterIfc.SUCCESS), BusIfc.CURRENT);       
    }

}
