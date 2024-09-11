/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/iteminquiry/CheckUnitOfMeasureSite.java /main/13 2011/12/05 12:16:19 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:14 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:00 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/22 00:06:33  jdeleau
 *   @scr 3665 Standardize on I18N standards across all properties files.
 *   Use {0}, {1}, etc instead of remaining <ARG> or #ARG# variables.
 *
 *   Revision 1.4  2004/03/03 23:15:10  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:50:30  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:10  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:00:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:22:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:33:40   msg
 * Initial revision.
 * 
 *    Rev 1.1   02 Mar 2002 19:51:12   baa
 * remove default value on response area
 * Resolution for POS SCR-230: Cross Store Inventory
 * Resolution for POS SCR-1325: In Item Inquiry a UoM item when selected shows 1.00 on the UoM screen
 *
 *    Rev 1.0   Sep 21 2001 11:29:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:14   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry.iteminquiry;

// foundation imports
import java.util.Locale;

import max.retail.stores.pos.services.sale.MAXSaleCargo;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.stock.GiftCardPLUItemIfc;
import oracle.retail.stores.domain.stock.PLUItemIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.PromptAndResponseModel;
//--------------------------------------------------------------------------
/**
    This site checks to see if additional Unit of Measure information
    is needed.
    @version $Revision: /main/13 $
**/
//--------------------------------------------------------------------------
public class CheckUnitOfMeasureSite extends PosSiteActionAdapter
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /main/13 $";
    /**
        Constant for unit of measure UNITS.
    **/
    public static final String UNITS = "UN";

    //----------------------------------------------------------------------
    /**
        Checks the Unit of Measure information to see if additional
        information is needed.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        String letter = null;

        // retrieve item object
        ItemInquiryCargo cargo = (ItemInquiryCargo)bus.getCargo();
        MAXSaleCargo cargo1 = (MAXSaleCargo)bus.getCargo(); 
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        PLUItemIfc pluItem = cargo.getPLUItem();

        if (pluItem.getUnitOfMeasure() == null ||
            pluItem.getUnitOfMeasure().getUnitID().equals(UNITS))
        {   // Default UOM; if it is a gift card the next site will get
            // the gift card information
            if (pluItem instanceof GiftCardPLUItemIfc)
            {
                letter = CommonLetterIfc.GIFTCARD;
            }
            else
            {
                letter = CommonLetterIfc.CONTINUE;
            }
            bus.mail(new Letter(letter), BusIfc.CURRENT);
        }
        else
        {
            // initialize the bean model
            POSBaseBeanModel baseModel = new POSBaseBeanModel();
            PromptAndResponseModel beanModel = new PromptAndResponseModel();

            //beanModel.setResponseText(cargo.getItemQuantity().toString());
            beanModel.setArguments(pluItem.getUnitOfMeasure().getName(locale));
            baseModel.setPromptAndResponseModel(beanModel);

            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
            ui.showScreen(POSUIManagerIfc.UNIT_OF_MEASURE, baseModel);

        }
    }
}
