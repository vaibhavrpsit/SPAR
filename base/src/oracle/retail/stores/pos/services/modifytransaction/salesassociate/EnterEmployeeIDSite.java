/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/salesassociate/EnterEmployeeIDSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:01 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:54 PM  Robert Pearse   
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
 *    Rev 1.1   Dec 04 2002 15:33:02   DCobb
 * Removed default sales associate.
 * Resolution for POS SCR-1844: Sales Associate ID prompt is defaulted with an ID and if you press Enter the default is not accepted
 * 
 *    Rev 1.0   Apr 29 2002 15:15:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   26 Mar 2002 13:35:06   vxs
 * Customized PromptAndResponse panel added in arrive() which displays sales associate in response field.
 * Resolution for POS SCR-104: Sales Assoc info does not default in at the transaction level
 *
 *    Rev 1.0   Mar 18 2002 11:39:22   msg
 * Initial revision.
 *
 *    Rev 1.0   Sep 21 2001 11:31:20   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.salesassociate;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

//--------------------------------------------------------------------------
/**
    This site displays the TRANSACTION_SALES_ASSOCIATE screen.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EnterEmployeeIDSite extends PosSiteActionAdapter
{

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //----------------------------------------------------------------------
    /**
       Displays the TRANSACTION_SALES_ASSOCIATE screen.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        POSUIManagerIfc ui =(POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        POSBaseBeanModel baseModel = new POSBaseBeanModel();
        ui.showScreen(POSUIManagerIfc.TRANSACTION_SALES_ASSOCIATE, baseModel);
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
        String strResult = new String("Class:  EnterEmployeeIDSite (Revision " +
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
