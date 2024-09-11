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

import java.awt.Color;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ImageGridBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * Utility class for Scan sheet operations.
 */
public class ScanSheetUtility
{
    public static void showErrorDialog(BusIfc bus, String resourceID, String letter)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DialogBeanModel dialogBean = new DialogBeanModel();
        Color BannerColor = Color.RED;
        String strBannerColor = UIFactory.getInstance().getUIProperty("Color.attention",
                LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
        if (!strBannerColor.equals(""))
        {
            BannerColor = Color.decode(strBannerColor);
        }
        dialogBean.setResourceID(resourceID);
        dialogBean.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dialogBean.setButtonLetter(DialogScreensIfc.BUTTON_OK, letter);
        dialogBean.setBannerColor(BannerColor);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogBean);
    }

    public static void configureImageGridBeanModel(ImageGridBeanModel igbm, NavigationButtonBeanModel nModel)
    {
        int noOfItems = igbm.getScanSheet().getScItemListSize();
        // Calculate the no. of pages
        igbm.setNumberOfPages(0);
        if ((noOfItems % igbm.maxNumberOfItems) == 0)
        {
            igbm.setNumberOfPages(noOfItems / igbm.maxNumberOfItems);
        }
        else
        {
            igbm.setNumberOfPages((noOfItems / igbm.maxNumberOfItems) + 1);
        }
        int n = igbm.getCurrentPageNumber();
        int pageNo = 1;
        if (n == 0)
        {
            igbm.setCurrentPageNumber(1);
        }
        else
        {
            pageNo = n;
        }
        if (igbm.getCurrentPageNumber() > igbm.getNumberOfPages())
        {
            pageNo = 1;
        }
        if (pageNo == 1)
        {
            if (igbm.getNumberOfPages() > 1)
            {
                nModel.setButtonEnabled("FirstPage", false);
                nModel.setButtonEnabled("NextPage", true);
                nModel.setButtonEnabled("LastPage", true);
                nModel.setButtonEnabled("PreviousPage", false);
            }
            else
            {
                nModel.setButtonEnabled("FirstPage", false);
                nModel.setButtonEnabled("NextPage", false);
                nModel.setButtonEnabled("LastPage", false);
                nModel.setButtonEnabled("PreviousPage", false);
            }
        }
        // If in last page
        else if (igbm.getNumberOfPages() != 1 && pageNo == igbm.getNumberOfPages())
        {
            nModel.setButtonEnabled("FirstPage", true);
            nModel.setButtonEnabled("LastPage", false);
            nModel.setButtonEnabled("NextPage", false);
            nModel.setButtonEnabled("PreviousPage", true);
        }
        // If in between first and last page
        else
        {
            nModel.setButtonEnabled("FirstPage", true);
            nModel.setButtonEnabled("LastPage", true);
            nModel.setButtonEnabled("NextPage", true);
            nModel.setButtonEnabled("PreviousPage", true);
        }
    }

}
