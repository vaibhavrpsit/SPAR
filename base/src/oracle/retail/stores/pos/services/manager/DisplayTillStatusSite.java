/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/manager/DisplayTillStatusSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:11 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   08/14/14 - add setButtonLetter for error dialog
 *                         when no till available to avoid system
 *                         hangs.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:27:50 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:06 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:41 PM  Robert Pearse   
 *
 *Revision 1.3.4.1  2004/11/10 23:16:01  lzhao
 *@scr 7635: read till status from db when display till status screen.
 *
 *Revision 1.3  2004/02/12 16:50:58  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:51:37  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:17  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:01:12   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Jul 16 2003 17:37:54   bwf
 * Get cashiers from correct till and register.
 * Resolution for 1782: Cashier Logoff is not removing the cashier from the list of active cashiers
 * 
 *    Rev 1.3   Feb 18 2003 16:31:32   DCobb
 * LookupRegisterStatusSite needs to update the register tills.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.2   Feb 17 2003 15:43:32   DCobb
 * Added Register Open flow to Resume Till service.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.1   Feb 12 2003 18:50:54   DCobb
 * Update the register's tills before display when the register has a floating till.
 * Resolution for POS SCR-1867: POS 6.0 Floating Till
 * 
 *    Rev 1.0   Apr 29 2002 15:18:36   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:36:12   msg
 * Initial revision.
 * 
 *    Rev 1.2   Mar 13 2002 12:38:02   mpm
 * Modified to use till from in-memory register object to capture all cashiers.
 * Resolution for POS SCR-1547: Till Status screen does not display all Cashiers when in Register accountability
 *
 *    Rev 1.1   Mar 08 2002 09:39:00   mpm
 * Modified to read statuses from database, handle multiple cashiers.
 *
 *    Rev 1.0   Mar 06 2002 09:56:20   mpm
 * Initial revision.
 * Resolution for POS SCR-1513: Add Till Status screen
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.manager;
// Foundation imports
import oracle.retail.stores.domain.arts.DataTransactionFactory;
import oracle.retail.stores.domain.arts.DataTransactionKeys;
import oracle.retail.stores.domain.arts.FinancialTotalsDataTransaction;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.TillIfc;
import oracle.retail.stores.domain.store.StoreIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.ListBeanModel;

//------------------------------------------------------------------------------
/**
   This site displays the status of the tills assigned to this register. <P>
   @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------
public class DisplayTillStatusSite extends PosSiteActionAdapter
{
    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
       site name
    **/
    public static final String SITENAME = "DisplayTillStatusSite";

    //--------------------------------------------------------------------------
    /**
       Display the status of configured devices and database
       @param bus the bus arriving at this site
    **/
    //--------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        ManagerCargo cargo = (ManagerCargo) bus.getCargo();
        POSUIManagerIfc  ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        RegisterIfc register = cargo.getRegister();
        TillIfc[] tills = register.getTills();
        
        FinancialTotalsDataTransaction transaction = (FinancialTotalsDataTransaction) DataTransactionFactory.create(DataTransactionKeys.FINANCIAL_TOTALS_DATA_TRANSACTION);
        try
        {
            if ( tills != null )
            {
                for (int i=0; i<tills.length; i++)
                {
                    String tillID = tills[i].getTillID();
                    StoreIfc store = register.getWorkstation().getStore();
                    TillIfc till = transaction.readTillStatus(store, tillID);
                    tills[i].setStatus(till.getStatus());
                }
            }
        } 
        catch (DataException e)
        {
            logger.error("Unable to read till status: " + e.toString() );            
        }

        // if no tills, display dialog
        if (tills == null ||
            tills.length == 0)
        {
            DialogBeanModel dialogModel = new DialogBeanModel();
            dialogModel.setResourceID("NoTillsAssigned");
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.NEXT);
            // display dialog
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        else
        {
            // set up the list
            ListBeanModel beanModel = new ListBeanModel();
            POSListModel posListModel = new POSListModel(tills);
            beanModel.setListModel(posListModel);

            ui.showScreen(POSUIManagerIfc.TILL_STATUS, beanModel);
        }
    }

}
