/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/delete/InvalidLayawayDeletionFeeAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:14 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
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
 *    3    360Commerce 1.2         3/31/2005 4:28:24 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:09 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:26 PM  Robert Pearse
 *
 *   Revision 1.4  2004/04/08 20:33:03  cdb
 *   @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *   Revision 1.3  2004/02/12 16:50:48  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:22  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:00:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 30 2002 08:34:52   jriggins
 * Now formatting the layaway deletion fee per the Locale associated with the user interface.
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:20:58   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:34:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:21:06   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:08:30   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.delete;

//foundation imports
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.financial.LayawayIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**
    Displays the invalid layaway deletion fee error dialog screen.
    <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class InvalidLayawayDeletionFeeAisle extends LaneActionAdapter
{
    /**
     *
     */
    private static final long serialVersionUID = 7100190698061141299L;

    /**
        class name constant
    **/
    public static final String LANENAME = "InvalidLayawayDeletionFeeAisle";

    /**
        revision number for this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
       Displays a dialog that the layaway deletion fee was too much (i.e. would
       cause the customer to owe money).
       <P>
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // get reference to ui and bean model
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // get layaway
        LayawayIfc layaway = ((LayawayCargo) bus.getCargo()).getLayaway();

        // show dialog
        String[] args = new String[1];
        String layawayFeeAmt =
            LocaleUtilities.formatCurrency(layaway.getDeletionFee().toString(),
                                         LocaleMap.getLocale(LocaleMap.DEFAULT));

        args[0] = "" + layawayFeeAmt;
        DialogBeanModel dbm = new DialogBeanModel();
        dbm.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        dbm.setResourceID("InvalidLayawayDeletionFee");
        dbm.setArgs(args);
        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, dbm);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dbm);
    }

}
