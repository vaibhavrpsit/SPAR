/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/salesassociate/EnterSalesAssocUpdatePolicySite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:04 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:55 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/28 16:01:19  rsachdeva
 *   @scr 4865 Transaction Sales Associate
 *
 *   Revision 1.4  2004/02/24 16:21:29  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
 *
 *   Revision 1.3  2004/02/12 16:51:14  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:48  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:02:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:32   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:39:22   msg
 * Initial revision.
 * 
 *    Rev 1.1   21 Jan 2002 17:51:20   baa
 * converting to new security model
 * Resolution for POS SCR-309: Convert to new Security Override design.
 *
 *    Rev 1.0   Sep 21 2001 11:31:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.salesassociate;

// java imports
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the MULTI_SALES_ASSC screen.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EnterSalesAssocUpdatePolicySite extends PosSiteActionAdapter
{

    /**
       revision number of this class
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Checks the cargo to see if the Sales Associate Update
       policy needs to be displayed.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {

        ModifyTransactionSalesAssociateCargo cargo;
        cargo = (ModifyTransactionSalesAssociateCargo)bus.getCargo();

        /*
         * See if we need to prompt for the update policy
         */
        if (cargo.getItemsModifiedFlag()
             || cargo.isAlreadySetTransactionSalesAssociate())
        {
            /*
             * Ask the UI Manager to display the policy selection screen
             */
            POSUIManagerIfc ui;
            ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            DialogBeanModel model = new DialogBeanModel();
            model.setResourceID("MultSalesAssoc");
            model.setType(DialogScreensIfc.CONFIRMATION);

            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE,model);
        }
        else
        {
            /*
             * Bypass the policy selection screen
             * Fake a "Yes" answer, since it does what we want anyway.
             */
            bus.mail(new Letter(CommonLetterIfc.YES), BusIfc.CURRENT);
        }

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  EnterSalesAssocUpdatePolicySite (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
