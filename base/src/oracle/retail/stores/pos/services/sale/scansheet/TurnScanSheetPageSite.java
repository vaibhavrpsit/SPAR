/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mchell 03/06/13 - Scansheet tour refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.scansheet;

import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.beans.ImageGridBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

/**
 * This site handles scan sheet local navigation buttons.
 */
public class TurnScanSheetPageSite extends PosSiteActionAdapter
{

    private static final long serialVersionUID = -8179121156063397481L;

    /*
     * (non-Javadoc)
     * @see
     * oracle.retail.stores.foundation.tour.application.SiteActionAdapter#arrive
     * (oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void arrive(BusIfc bus)
    {
        ScanSheetCargo cargo = (ScanSheetCargo) bus.getCargo();
        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();
        ImageGridBeanModel igbm = cargo.getImageGridBeanModel();

        if (igbm.getCurrentCategoryModel() == null)
        {
            this.turnPage(igbm, nModel, bus.getCurrentLetter().getName(), bus);
        }
        else
        {
            this.turnPage(igbm.getCurrentCategoryModel(), nModel, bus.getCurrentLetter().getName(), bus);
            // Enable display category flag
            igbm.setDisplayCategory(true);
        }

        bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);

    }

    private void turnPage(ImageGridBeanModel igbm, NavigationButtonBeanModel nModel, String letterName, BusIfc bus)
    {
        if (letterName.equals("LastPage"))
        {
            igbm.setCurrentPageNumber(igbm.getNumberOfPages());
            nModel.setButtonEnabled("NextPage", false);
            nModel.setButtonEnabled("LastPage", false);
            nModel.setButtonEnabled("FirstPage", true);
            nModel.setButtonEnabled("PreviousPage", true);
        }
        else if (letterName.equals("FirstPage"))
        {
            igbm.setCurrentPageNumber(1);
            nModel.setButtonEnabled("NextPage", true);
            nModel.setButtonEnabled("LastPage", true);
            nModel.setButtonEnabled("FirstPage", false);
            nModel.setButtonEnabled("PreviousPage", false);
        }
        else if (letterName.equals("NextPage"))
        {
            int nextPage = igbm.getCurrentPageNumber() + 1;
            if (nextPage == igbm.getNumberOfPages())
            {
                nModel.setButtonEnabled("LastPage", false);
                nModel.setButtonEnabled("NextPage", false);
            }
            else
            {
                nModel.setButtonEnabled("LastPage", true);
                nModel.setButtonEnabled("NextPage", true);
            }
            nModel.setButtonEnabled("FirstPage", true);
            nModel.setButtonEnabled("PreviousPage", true);
            igbm.setCurrentPageNumber(nextPage);
        }
        else if (letterName.equals("PreviousPage"))
        {
            int previousPage = igbm.getCurrentPageNumber() - 1;
            if (previousPage == 1)
            {
                nModel.setButtonEnabled("LastPage", true);
                nModel.setButtonEnabled("NextPage", true);
                nModel.setButtonEnabled("FirstPage", false);
                nModel.setButtonEnabled("PreviousPage", false);
            }
            else
            {
                nModel.setButtonEnabled("LastPage", true);
                nModel.setButtonEnabled("NextPage", true);
                nModel.setButtonEnabled("FirstPage", true);
                nModel.setButtonEnabled("PreviousPage", true);
            }
            igbm.setCurrentPageNumber(previousPage);
        }
        igbm.setLocalButtonBeanModel(nModel);
    }
}
