/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/main/LookupTenderLimitsParameterErrorAisle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:12 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:58 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:22 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:29 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/25 23:41:39  cdb
 *   @scr 4166 Removing Deprecation Warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:05  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:24:06  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Dec 15 2003 09:30:28   bjosserand
 * Initial revision.
 * 
 *    Rev 1.0   Aug 29 2003 16:01:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:19:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:35:58   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:21:58   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:08:50   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.main;
// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Aisle to traverse if there is a parameter error preventing a tender-limits lookup. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LookupTenderLimitsParameterErrorAisle extends PosLaneActionAdapter
{
    /**
       revision number supplied by Team Connection
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Display an error message, wait for user acknowlegement. <P>
       <B>Pre-Condition(s)</B>
       <UL>
       <LI>none
       </UL>
       <B>Post-Condition(s)</B>
       <UL>
       <LI>none
       </UL>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {
        // get ui handle
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

        // set bean model
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID("TenderLimitsParameterError");
        model.setType(DialogScreensIfc.ERROR);
        ui.setModel(POSUIManagerIfc.DIALOG_TEMPLATE, model);

        // display dialog
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE);

    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class. <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()

}
