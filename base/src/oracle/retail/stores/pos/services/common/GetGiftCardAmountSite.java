/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/GetGiftCardAmountSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:53 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:14 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:49 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:11:11 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:08  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:38:50  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 18 2003 12:09:10   lzhao
 * deprecate the file.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.0   Aug 29 2003 15:54:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:35:08   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:22:44   msg
 * Initial revision.
 * 
 *    Rev 1.0   15 Feb 2002 16:02:24   cir
 * Initial revision.
 * Resolution for POS SCR-1203: Gift Card sale - add open amount gift card to transaction from item inquiry, skips steps to enter amount
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This site shows ENTER_GIFT_CARD_AMOUNT screen.
    @deprecated deprecated in version 7.0
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class GetGiftCardAmountSite extends PosSiteActionAdapter
{
    /**
        revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";


    //----------------------------------------------------------------------
    /**
        Show ENTER_GIFT_CARD_AMOUNT screen.
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel model = new POSBaseBeanModel();
        ui.showScreen(POSUIManagerIfc.ENTER_GIFT_CARD_AMOUNT, model);
    }
}
