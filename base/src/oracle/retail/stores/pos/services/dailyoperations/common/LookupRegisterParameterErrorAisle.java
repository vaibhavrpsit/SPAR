/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/dailyoperations/common/LookupRegisterParameterErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:16 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   02/04/09 - updated UIManager call to showScreen
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:21 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:28 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:49:36  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:02  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:56:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Apr 11 2003 10:08:36   jgs
 * Removed @deprecation placed on this class by mistake.
 * Resolution for 2103: Remove uses of deprecated items in POS.
 * 
 *    Rev 1.0   Apr 29 2002 15:31:32   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:13:32   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:26:22   msg
 * Initial revision.
 * 
 *    Rev 1.0   29 Nov 2001 08:28:30   epd
 * Initial revision.
 * Resolution for POS SCR-216: Making POS changes to accommodate OnlineOffice
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.dailyoperations.common;

import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * Aisle to traverse if there is a parameter error preventing a store-status
 * lookup.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $ instead.
 */
public class LookupRegisterParameterErrorAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -7205199740470071800L;

    /**
     * revision number supplied by Team Connection
     **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Display an error message, wait for user acknowlegement.
     * 
     * @param bus Service Bus
     */
    @Override
    public void traverse(BusIfc bus)
    {
        // get ui handle
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

        // set bean model
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("RegisterParameterError");
        model.setType(DialogScreensIfc.ERROR);
        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }
}