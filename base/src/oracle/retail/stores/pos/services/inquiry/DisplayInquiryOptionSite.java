/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/inquiry/DisplayInquiryOptionSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:48 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:03 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:39 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/06/29 19:59:03  lzhao
 *   @scr 5477: add gift card inquiry in training mode.
 *
 *   Revision 1.3  2004/02/12 16:50:26  mcs
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
 *    Rev 1.1   Jan 30 2004 14:14:10   lzhao
 * update based on req. changes.
 * Resolution for 3371: Feature Enhancement:  Gift Card Enhancement
 * 
 *    Rev 1.0   Aug 29 2003 15:59:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:21:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:33:04   msg
 * Initial revision.
 * 
 *    Rev 1.2   07 Mar 2002 16:43:28   vxs
 * Added line cargo.setGiftCard(null);
 * Resolution for POS SCR-1468: Gift Card Inquiry error for unknown # press Enter.  Can't re-enter correct GC#
 *
 *    Rev 1.1   25 Oct 2001 17:41:08   baa
 * cross store inventory feature
 * Resolution for POS SCR-230: Cross Store Inventory
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.inquiry;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.SiteActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.SiteActionIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
//import oracle.retail.stores.pos.ui.beans.ItemOptionsBeanModel;

//------------------------------------------------------------------------------
/**

    @version $KW; $Ver; $EKW;
**/
//------------------------------------------------------------------------------

public class DisplayInquiryOptionSite extends SiteActionAdapter implements SiteActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -8399931643007594526L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:49; $EKW;";


    //--------------------------------------------------------------------------
    /**
          Display Inquiry Options screen.
          @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------

    public void arrive(BusIfc bus)
    {
         //reset giftcard otherwise previously entered giftcard can get picked up
         //by the GiftCardStation
         InquiryOptionsCargo cargo = (InquiryOptionsCargo)bus.getCargo();
         cargo.setGiftCard(null);
         //Display inquiry options screen
         POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
         
         POSBaseBeanModel beanModel = new POSBaseBeanModel();
         ui.showScreen(POSUIManagerIfc.INQUIRY_OPTIONS, beanModel);

         
    }
}
