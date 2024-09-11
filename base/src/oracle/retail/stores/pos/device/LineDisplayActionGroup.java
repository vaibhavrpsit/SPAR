/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/device/LineDisplayActionGroup.java /main/16 2011/12/05 12:16:17 cgreene Exp $
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
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    cgreene   11/03/09 - Use short item description on receipts, pole
 *                         display, and CPOI
 *
 * ===========================================================================
 * $Log:
 3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse
 2    360Commerce 1.1         3/10/2005 10:23:05 AM  Robert Pearse
 1    360Commerce 1.0         2/11/2005 12:12:18 PM  Robert Pearse
 *
 * Revision 1.4  2004/09/23 00:07:13  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.3  2004/02/12 16:48:34  mcs
 * Forcing head revision
 *
 * Revision 1.2  2004/02/11 21:30:29  rhafernik
 * @scr 0 Log4J conversion and code cleanup
 *
 * Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 * updating to pvcs 360store-current
 *
 *    Rev 1.0   Aug 29 2003 15:51:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Jul 11 2003 15:32:56   baa
 * calculate space available for descriptions based on the space left after amounts are entered.
 *
 *    Rev 1.1   Mar 24 2003 16:43:40   baa
 * support for customer locale on pole display
 * Resolution for POS SCR-1843: Multilanguage support
 *
 *    Rev 1.0   Jan 08 2003 12:23:52   vxs
 * Initial revision.
 * Resolution for POS SCR-1901: Pos Device Action/ActionGroup refactoring
 *Revision: /main/8 $
 * ===========================================================================
 */
package oracle.retail.stores.pos.device;

import jpos.JposException;
import jpos.LineDisplay;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.DeviceMode;
import oracle.retail.stores.foundation.manager.device.LineDisplaySession;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModeIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceSessionIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.foundation.utility.Util;

/**
 * The <code>LineDisplayActionGroup</code> defines the LineDisplay specific
 * device operations available to POS applications.
 */
public class LineDisplayActionGroup extends POSDeviceActionGroup implements LineDisplayActionGroupIfc
{
    // This id is used to tell the compiler not to generate a new serialVersionUID.
    static final long serialVersionUID = -6241512546640464504L;

    public static final String RCS_REVISION = "$Revision: /main/16 $";

    /**
     * Get Line Display from the DeviceSession
     */
    public LineDisplay getLineDisplay() throws DeviceException
    {
        // obtain LineDisplaySession from DeviceTechnician
        DeviceTechnicianIfc dt;
        DeviceSessionIfc lineDisplaySession = null;
        LineDisplay lineDisplay;
        try
        {
            dt = getDeviceTechnician();
            lineDisplaySession = dt.getDeviceSession(LineDisplaySession.TYPE);
            DeviceModeIfc dm = new DeviceMode();
            dm.setDeviceSessionName(LineDisplaySession.TYPE);
            dm.setDeviceModeName(LineDisplaySession.MODE_DISPLAY);
            lineDisplay = (LineDisplay)lineDisplaySession.getDeviceInMode(dm);
        }
        catch (DeviceException e)
        {
            throw e;
        }
        finally
        {
            if (lineDisplaySession != null)
            {
                lineDisplaySession.releaseDevice();
            }
        }
        return lineDisplay;
    }

    /**
     * Clears the line display device
     */
    public void clearText() throws DeviceException
    {
        try
        {
            getLineDisplay().clearText();
        }
        catch (JposException e)
        {
            logger.warn("Unable to use Line Display: " + e.getMessage() + "");
            throw new DeviceException(DeviceException.JPOS_ERROR, "Line Display error", e);
        }
    }

    /**
     * Display text on line display device
     *
     * @param row start row for text
     * @param col start col for text
     * @param text String to show in line display device.
     */
    public void displayTextAt(int row, int col, String text) throws DeviceException
    {
        try
        {
            getLineDisplay().displayTextAt(row, col, text, jpos.LineDisplayConst.DISP_DT_NORMAL);
        }
        catch (JposException e)
        {
            throw new DeviceException(DeviceException.JPOS_ERROR, "Line Display error", e);
        }
    }

    /**
     * Display text on line display device
     *
     * @param item lineitem whose description/price has to be displayed
     */
    public void lineDisplayItem(SaleReturnLineItemIfc item) throws DeviceException
    {
        lineDisplayItem(item.getPLUItem(), item.getExtendedDiscountedSellingPrice());
    }

    /**
     * Display text on line display device
     *
     * @param item lineitem whose description/price has to be displayed
     */
    public void lineDisplayItem(PLUItemIfc item) throws DeviceException
    {
        lineDisplayItem(item, item.getPrice());
    }

    /**
     *
     * @param item
     * @param price
     * @throws DeviceException
     */
    protected void lineDisplayItem(PLUItemIfc item, CurrencyIfc price) throws DeviceException
    {
        clearText();

        String description = item.getShortDescription(LocaleMap.getLocale(LocaleConstantsIfc.POLE_DISPLAY));
        String priceStr = price.toFormattedString();

        int priceLen = priceStr.length();
        int descLen = POSDeviceActionGroupIfc.LINE_DISPLAY_SIZE - priceLen;

        String displayLine1 = Util.formatTextData(description, descLen, false)
                + Util.formatTextData(priceStr, priceLen, true);
        displayTextAt(0, 0, displayLine1);
    }
}
