/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/sale/scansheet/ScanSheetItemEnteredAisle.java /main/2 2012/09/04 14:41:34 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 09/04/12 - Code cleanup, method name cleanup and refactor to allow
 *                      for single-clicks and ESC back to previous category
 *    jkoppo 03/02/11 - New aisle in scan sheet tour
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.scansheet;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.ImageGridBeanModel;

@SuppressWarnings("serial")
public class ScanSheetItemEnteredAisle extends PosLaneActionAdapter
{

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.application.LaneActionAdapter#traverse(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        ImageGridBeanModel imgbm = (ImageGridBeanModel) ui.getModel(POSUIManagerIfc.SCAN_SHEET);
        if (imgbm != null)
        {
            if (imgbm.getSelectedItemID() != null)
            {
                if (!imgbm.isCategorySelected())
                {
                    bus.mail(CommonLetterIfc.NEXT);

                }
                else
                {
                    bus.mail("CategorySelected");
                }
            }

        }

    }

}
