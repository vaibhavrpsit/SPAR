/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/UnitOfMeasureEnteredAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:44 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     11/01/10 - Fixed issues with UNDO and CANCEL letters; this
 *                         includes properly canceling transactions when a user
 *                         presses the cancel button in the item inquiry and
 *                         item inquiry sub tours.
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         5/12/2006 5:25:29 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    4    360Commerce 1.3         1/22/2006 11:45:11 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    3    360Commerce 1.2         3/31/2005 4:30:39 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:26:32 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:15:23 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:31  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:11  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:00:20   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Sep 20 2002 17:55:14   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:22:40   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:34:04   msg
 * Initial revision.
 *
 *    Rev 1.2   26 Feb 2002 14:42:38   jbp
 * do not allow zero quantitys
 * Resolution for POS SCR-1386: Can add a UoM item from Item Inquiry with O quantity 0 price to the sell item
 *
 *    Rev 1.1   Feb 05 2002 16:42:32   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.0   Sep 21 2001 11:29:54   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:06   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// java imports
import java.math.BigDecimal;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the Unit of Measure has been entered.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class UnitOfMeasureEnteredAisle extends LaneActionAdapter
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -8246126778235109846L;
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Sets Unit of Measure quantity entered in the UI.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        boolean mail = true;
        //  retrieve cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();

        //  get a hold of the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // get units from ui input field
        String amountStr =LocaleUtilities.parseNumber(ui.getInput(),
                                                      LocaleMap.getLocale(LocaleMap.DEFAULT)).toString();
        if (  amountStr!=null &&
           new BigDecimal(amountStr).compareTo(BigDecimal.ZERO) != 0)
        {
            BigDecimal units = new BigDecimal(amountStr);
            cargo.setItemQuantity(units);
            cargo.setModifiedFlag(true);
        }
        else
        if ( amountStr != null &&  new BigDecimal(amountStr).compareTo(BigDecimal.ZERO) == 0)
        {
            mail = false;
            DialogBeanModel dModel = new DialogBeanModel();
            dModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dModel.setResourceID("QuantityCannotBeZero");
            dModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, "Loop");
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,dModel);
        }
        else
        {
            cargo.setModifiedFlag(false);
        }
        if (mail)
        {
            bus.mail(new Letter(CommonLetterIfc.ADD), BusIfc.CURRENT);
        }
    }
}
