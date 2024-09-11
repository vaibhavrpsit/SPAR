/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/itemcheck/UnitOfMeasureEnteredAisle.java /main/14 2012/10/15 13:48:09 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     10/15/12 - Forward port fix for Unexpected exception error when
 *                         only a period entered for a quantiy such as
 *                         kilograms.
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
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
 *   Revision 1.5  2004/07/30 22:02:55  aschenk
 *   @scr 4960 - Selling a kit with a UOM item now asks for the qty.
 *
 *   Revision 1.4  2004/03/03 23:15:12  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:36  mcs
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
 *    Rev 1.0   13 Nov 2003 10:35:20   jriggins
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry.itemcheck;

// java imports
import java.math.BigDecimal;

import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.stock.ItemKitIfc;
import oracle.retail.stores.domain.stock.KitComponentIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.inquiry.iteminquiry.ItemInquiryCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This aisle is traversed when the Unit of Measure has been entered.
    @version $Revision: /main/14 $
**/
//--------------------------------------------------------------------------
public class UnitOfMeasureEnteredAisle extends LaneActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = -6279507143866795694L;
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    //----------------------------------------------------------------------
    /**
        Sets Unit of Measure quantity entered in the UI.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        boolean mail = true;
        String letter = CommonLetterIfc.CONTINUE;
        //  retrieve cargo
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();

        //  get a hold of the ui manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // get units from ui input field
        String amountStr = null;
        if(ui.getInput().length() == 1 && !Character.isDigit(ui.getInput().charAt(0)))
        {
            // if only the separator was entered treat as zero
            amountStr = LocaleUtilities.parseNumber("0", 
                    LocaleMap.getLocale(LocaleMap.DEFAULT)).toString();
        }
        else if(!ui.getInput().isEmpty())
        {
            amountStr =LocaleUtilities.parseNumber(ui.getInput(),
                    LocaleMap.getLocale(LocaleMap.DEFAULT)).toString();
        }
        if (  amountStr!=null &&
           new BigDecimal(amountStr).compareTo(BigDecimal.ZERO) != 0)
        {
            BigDecimal units = new BigDecimal(amountStr);
            if (cargo.getPLUItem().isKitHeader())
            {
                letter = "NextItem";
                KitComponentIfc kc[] = ((ItemKitIfc)cargo.getPLUItem()).getComponentItems();
                int index = ((ItemKitIfc)cargo.getPLUItem()).getindex();
                kc[index].setQuantity(units);

            }
            else
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
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
    }
}
