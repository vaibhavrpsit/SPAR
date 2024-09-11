/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/returns/returncommon/TooManyTransactionsErrorAisle.java /main/11 2013/12/16 14:22:37 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  12/16/13 - XbranchMerge lapattab_bug-17496189 from
 *                         rgbustores_13.3x_generic_branch
 *    lapattab  10/11/13 - Fix For bug#17496189 - Added code to display
 *                         different error dialogs for return with receipt flow
 *                         and return using search criteria.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:33 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:18 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:10 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/02/27 19:51:16  baa
 *   @scr 3561 Return enhancements
 *
 *   Revision 1.3  2004/02/12 16:51:46  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:30  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:20  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:05:52   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 15:06:56   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:45:20   msg
 * Initial revision.
 * 
 *    Rev 1.0   Sep 21 2001 11:24:36   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:12:22   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.returns.returncommon;

// foundation imports
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    This road is traveled when application detects that there are Too
    many transactions to display.
    <p>
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class TooManyTransactionsErrorAisle extends PosLaneActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
       Constant for error screen
    **/
    public static final String RETRIEVED_TOO_MANY_MATCHES = "TransactionTooManyMatches";
    /**
     * Constant for error screen
     **/
    public static final String RETRIEVED_TOO_MANY_MATCHES_BY_SEARCHCRITERIA = "TransactionTooManyMatchesBySearchCriteria";


    //----------------------------------------------------------------------
    /**
       This road is traveled when application detects that there are Too
       many transactions to display.
       <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void traverse(BusIfc bus)
    {

        // Using "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();
        model.setResourceID(RETRIEVED_TOO_MANY_MATCHES_BY_SEARCHCRITERIA);
        if (bus.getCargo() instanceof ReturnFindTransCargo)
        {
            ReturnFindTransCargo rfCargo = (ReturnFindTransCargo)bus.getCargo();
            if (rfCargo.getSearchCriteria() == null)
            {
                model.setResourceID(RETRIEVED_TOO_MANY_MATCHES);
            }

        }
        model.setType(DialogScreensIfc.ERROR);

        // set and display the model
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);

    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of the object.
       <P>
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {                                   // begin toString()
        // result string
        String strResult = new String("Class:  TooManyTransactionsErrorAisle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        // pass back result
        return(strResult);
    }                                   // end toString()

    //----------------------------------------------------------------------
    /**
       Returns the revision number of the class.
       <P>
       @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(revisionNumber);
    }                                   // end getRevisionNumber()
}
