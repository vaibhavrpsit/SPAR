/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/DatabaseFailureAisle.java /main/15 2013/07/05 15:31:26 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  07/05/13 - Updated dialog screen for Role update when offline
 *                         error.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/27/10 - XbranchMerge cgreene_refactor-duplicate-pos-classes
 *                         from st_rgbustores_techissueseatel_generic_branch
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:40 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:45 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:26 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/03 23:15:06  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
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
 *    Rev 1.0   Aug 29 2003 15:54:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Mar 03 2003 11:10:24   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Oct 09 2002 14:55:36   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:36:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:52   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:22:28   msg
 * Initial revision.
 * 
 *    Rev 1.1   Dec 10 2001 19:11:24   cir
 * Assign float label
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * 
 *    Rev 1.0   Dec 10 2001 19:02:18   cir
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * The DatabaseFailureAisle is traversed when the database returns a message
 * indicating that the save was not successful. An error message will be
 * displayed here
 * 
 * @version $Revision: /main/15 $
 */
public class DatabaseFailureAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -2151550734729285927L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * class name constant
     */
    public static final String LANENAME = "DatabaseFailureAisle";

    /**
     * resource id constant
     */
    public static final String RESOURCE_ID = "DatabaseError";

    /**
     * The DatabaseFailureAisle is traversed when the database returns a message
     * indicating that the save was not successful. An error message will be
     * displayed here
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {

        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DBErrorCargoIfc cargo = (DBErrorCargoIfc) bus.getCargo();

        // Get the dialog bean model
        DialogBeanModel model = new DialogBeanModel();

        // Set the letter for the OK button
        model.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.FAILURE);
        model.setType(DialogScreensIfc.ERROR);

        setModelDisplayText(bus, cargo, model);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

    }

    /**
     * Sets the message to display on the Dialog screen. 
     * Resource ID and Args are set for the model passed in. 
     * @param bus
     * @param cargo
     * @param model
     */
    public void setModelDisplayText(BusIfc bus, DBErrorCargoIfc cargo, DialogBeanModel model)
    {
        model.setResourceID(RESOURCE_ID);

        String args[] = new String[1];
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        args[0] = utility.getErrorCodeString(cargo.getDataExceptionErrorCode());
        model.setArgs(args);
    }

    /**
     * Returns a string representation of this object.
     * 
     * @param none
     * @return String representation of object
     */
    public String toString()
    {
        // result string
        String strResult = new String("Class: " + LANENAME + " (Revision " + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return (strResult);
    }

    /**
     * Returns the revision number of the class.
     * 
     * @param none
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (revisionNumber);
    }

}
