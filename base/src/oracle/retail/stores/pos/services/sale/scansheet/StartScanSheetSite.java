/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    mchell 03/06/13 - Removed unused new visit attribute
 *    mchell 03/06/13 - Scansheet tour refactoring
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.sale.scansheet;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.ScanSheetTransaction;
import oracle.retail.stores.domain.stock.ScanSheet;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.beans.ImageGridBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;

/**
 * Start site for scan sheet tour. This site initializes ImageGridBeanModel
 * object with scan sheet items and categories.
 */
public class StartScanSheetSite extends PosSiteActionAdapter
{
    private static final long serialVersionUID = -4741565133005749308L;

    private static final String APPLICATION_PROPERTY_GROUP_NAME = "application";

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
        ImageGridBeanModel igbm = null;
        NavigationButtonBeanModel nModel = new NavigationButtonBeanModel();

        ScanSheet ss = null;
        ScanSheetTransaction scanSheetTransaction = (ScanSheetTransaction) DataTransactionFactory
                .create(DataTransactionKeys.SCAN_SHEET_TRANSACTION);
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        locale = LocaleMap.getBestMatch(locale);
        try
        {
            ss = scanSheetTransaction.getScanSheet(locale.toString());

            int i = Integer.parseInt(Gateway.getProperty(APPLICATION_PROPERTY_GROUP_NAME, "maxGridSize", "4"));
            if (i < 1)
            {
                logger.error("Unable to configure scan sheet - Application property maxGridSize is less than 1");
                ScanSheetUtility.showErrorDialog(bus, "ScanSheetConfigWarning2", "Undo");
            }
            else
            {
                if (ss.getScItemList().size() != 0)
                {
                    igbm = new ImageGridBeanModel(ss);
                    cargo.setImageGridBeanModel(igbm);
                    ScanSheetUtility.configureImageGridBeanModel(igbm, nModel);
                    igbm.setLocalButtonBeanModel(nModel);
                    igbm.setCurrentCategoryModel(null);
                    igbm.setCategoryID(null);
                    igbm.setCategoryDescription(null);
                }
                else
                {
                    ScanSheetUtility.showErrorDialog(bus, "ScanSheetConfigWarning", "Undo");
                }
            }
            bus.mail(CommonLetterIfc.CONTINUE);
        }
        catch (DataException de)
        {
            logger.error("Unable to fetch scan sheet - System is offline " + de.getMessage() + "");
            ScanSheetUtility.showErrorDialog(bus, "ScanSheetOffline", "Undo");
        }
        catch (NumberFormatException ex)
        {
            logger.error("Unable to configure scan sheet - Application property maxGridSize is not an integer");
            ScanSheetUtility.showErrorDialog(bus, "ScanSheetConfigWarning2", "Undo");
        }

    }

}
