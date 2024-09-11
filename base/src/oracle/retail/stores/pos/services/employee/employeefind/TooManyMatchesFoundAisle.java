/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/TooManyMatchesFoundAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:08 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:33 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:10 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:17  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/06/04 13:28:29  tmorris
 *   @scr 5246 -Too Many Matches error screen was not appearing when needed.
 *
 *   Revision 1.3  2004/02/12 16:50:18  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:39:47  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:16  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:59:26   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:23:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:32:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   05 Nov 2001 17:37:12   baa
 * Code Review changes. Customer, Customer history Inquiry Options
 * Resolution for POS SCR-244: Code Review  changes
 *
 *    Rev 1.0   Sep 21 2001 11:23:32   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:48   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.employee.employeefind;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LaneActionIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//------------------------------------------------------------------------------
/**

       The TooManyMatchesFoundAisle is traversed when
       the number of matches from the database exceeds the
       Maximum Matches parameter.

    @version $Revision: /rgbustores_13.4x_generic_branch/1 $

**/
//------------------------------------------------------------------------------

public class TooManyMatchesFoundAisle extends PosLaneActionAdapter implements LaneActionIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -9166714333894777397L;


    public static final String LANENAME = "TooManyMatchesFoundAisle";

    //--------------------------------------------------------------------------
    /**

       The TooManyMatchesFoundAisle is traversed when
       the number of matches from the database exceeds the
       Maximum Matches parameter.

       @param bus the bus traversing this lane
    **/
    //--------------------------------------------------------------------------

    public void traverse(BusIfc bus)
    {


        POSUIManagerIfc ui;
        ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // Using "generic dialog bean". display the error dialog

        DialogBeanModel model = new DialogBeanModel();

        model.setResourceID("TooManyMatches");
        model.setType(DialogScreensIfc.ERROR);
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK,CommonLetterIfc.UNDO);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);


    }
}  // end class TooManyMatchesFoundAisle
