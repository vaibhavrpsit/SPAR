/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/DataBaseErrorAisle.java /main/15 2013/06/12 12:56:50 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  06/12/13 - Fix to display explicit exception message in case of
 *                         multiple orders found under one order id
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
 *   Revision 1.6  2004/07/21 20:33:41  mweis
 *   @scr 5809 Need correct error dialog when too many transactions are returned from a returns' search.
 *
 *   Revision 1.5  2004/06/21 22:46:15  mweis
 *   @scr 5643 Returning when database is offline displays wrong error dialog
 *
 *   Revision 1.4  2004/04/14 18:44:57  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Removed redundant methods.
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
 *    Rev 1.2   Mar 03 2003 11:02:56   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Oct 09 2002 14:50:10   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 15:36:02   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:08:50   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:22:26   msg
 * Initial revision.
 * 
 *    Rev 1.1   16 Nov 2001 10:31:38   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:13:18   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:24   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import java.text.MessageFormat;

import oracle.retail.stores.pos.services.common.DBErrorCargoIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.order.common.OrderCargo;
import oracle.retail.stores.pos.services.returns.returnfindtrans.ReturnFindTransCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

/**
 * The DataBaseErrorAisle is traversed when the database returns a message
 * indicating that the save was not successful. An error message will be
 * displayed here
 * 
 * @version $Revision: /main/15 $
 */
public class DataBaseErrorAisle extends PosLaneActionAdapter
{
    private static final long serialVersionUID = -8826236872790870820L;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/15 $";

    /**
     * resource id constant for everything except Returns
     */
    public static final String RESOURCE_ID = "DatabaseError";

    /**
     * resource id constant for Returns
     */
    public static final String RETURNS_RESOURCE_ID = "DatabaseErrorForReturns";

    /**
     * resource id constant for Too Many Matches returned from a search
     */
    public static final String TOO_MANY_MATCHES_ID = "TransactionTooManyMatches";

    /**
     * The DataBaseErrorAisle is traversed when the database returns a message
     * indicating that the save was not successful. An error message will be
     * displayed here
     * 
     * @param bus the bus traversing this lane
     */
    @Override
    public void traverse(BusIfc bus)
    {
        POSUIManagerIfc ui= (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        DBErrorCargoIfc cargo = (DBErrorCargoIfc) bus.getCargo();

        // Using "generic dialog bean".
        DialogBeanModel model = new DialogBeanModel();

        // Set the correct argument, getting it from the cargo
        String args[] = new String[1];
        UtilityManagerIfc utility = 
          (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        args[0] = utility.getErrorCodeString
          (cargo.getDataExceptionErrorCode());
        
        // Set button and arguments
        if (isReturn(cargo))
        {
            if (cargo.getDataExceptionErrorCode() == DataException.RESULT_SET_SIZE)
            {
                model.setResourceID(TOO_MANY_MATCHES_ID);
            }
            else
            {    
                // Returns gets a kinder dialog, per SCR 5643.
                model.setResourceID(RETURNS_RESOURCE_ID);
            }
        }
        else
        {
            // Everything except "Returns".
            model.setResourceID(RESOURCE_ID);
            if(cargo.getDataExceptionErrorCode()== DataException.ORDER_RESULT_SET_SIZE)
            {
            	Object testArgs[]=new Object[1];
            	if(bus.getCargo() instanceof OrderCargo)
            	{
            	testArgs[0]=((OrderCargo)bus.getCargo()).getOrderID();
            	MessageFormat form = new MessageFormat(args[0]);
            	args[0]=form.format(testArgs);
            	}

            }
            model.setArgs(args);         
        }
        model.setType(DialogScreensIfc.ERROR);

        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, model);
    }
    
    /**
     * Returns whether this cargo is a "Returns" type one.
     * @param cargo the cargo
     * @return Whether this cargo is a "Returns" type one.
     */
    protected boolean isReturn(CargoIfc cargo)
    {
        // Per SCR 5643.
        return (cargo instanceof ReturnFindTransCargo);
    }
}
