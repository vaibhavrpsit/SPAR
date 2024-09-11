/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/delete/DeleteCustomerRoad.java /main/14 2012/09/12 11:57:18 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    blarsen   08/27/12 - Merge from project Echo (MPOS) into trunk.
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    asinton   03/07/12 - Use new CustomerManager instead of DataTransaction
 *                         method to access customer data.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:43 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:53 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:33 PM  Robert Pearse
 *
 *   Revision 1.6  2004/06/03 14:47:45  epd
 *   @scr 5368 Update to use of DataTransactionFactory
 *
 *   Revision 1.5  2004/04/20 13:19:02  tmorris
 *   @scr 4332 -Replaced direct instantiation(new) with Factory call.
 *
 *   Revision 1.4  2004/03/03 23:15:14  bwf
 *   @scr 0 Fixed CommonLetterIfc deprecations.
 *
 *   Revision 1.3  2004/02/12 16:49:26  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:41:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:55:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Mar 03 2003 16:21:22   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Oct 09 2002 15:50:28   RSachdeva
 * Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:33:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:12:08   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:24:52   msg
 * Initial revision.
 *
 *    Rev 1.2   14 Jan 2002 09:54:30   baa
 * add : to ej message
 * Resolution for POS SCR-601: Customer Delete EJ text error
 *
 *    Rev 1.1   03 Jan 2002 15:39:02   vxs
 * Added check at very beginning to not execute code if in training mode.
 * Resolution for POS SCR-521: Customer package training mode updates
 *
 *    Rev 1.0   16 Nov 2001 10:49:08   baa
 * Initial revision.
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:15:22   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:54   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.delete;

// foundation imports
import oracle.retail.stores.domain.customer.CustomerConstantsIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.manager.customer.CustomerManagerIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosLaneActionAdapter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.utility.I18NConstantsIfc;
import oracle.retail.stores.utility.I18NHelper;
import oracle.retail.stores.utility.JournalConstantsIfc;
/**
 * Site to mark customer deleted in the database.
 */
@SuppressWarnings("serial")
public class DeleteCustomerRoad extends PosLaneActionAdapter
{
    /**

     * Marks the customer as deleted in the database.
     * @param  bus     Service Bus
     */
    public void traverse(BusIfc bus)
    {
        String letterName = CommonLetterIfc.CONTINUE;
        // get the customer to delete, don't delete in training mode
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        Object[] dataArgs = new Object[2];
        if(cargo.getRegister().getWorkstation().isTrainingMode() == false)
        {
            CustomerIfc customer = cargo.getCustomer();

            // update the status of the customer to Deleted
            customer.setStatus(CustomerConstantsIfc.CUSTOMER_STATUS_DELETED);

            // get the Journal manager
            JournalManagerIfc jmi = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);

            try
            {
                CustomerManagerIfc customerManager = (CustomerManagerIfc)bus.getManager(CustomerManagerIfc.TYPE);
                customerManager.deleteCustomer(customer);
                // Journal the messaging information.  Journal Format requires two space indent.
                if (jmi != null) {
					dataArgs[0] = customer.getCustomerID().trim();
					jmi.journal(cargo.getEmployeeID(),
							cargo.getTransactionID(), I18NHelper.getString(
									I18NConstantsIfc.EJOURNAL_TYPE,
									JournalConstantsIfc.DELETE_CUSTOMER_LABEL,
									dataArgs));
				}
                else
                {
                    logger.warn( "No journal manager found!");
                }
                letterName = CommonLetterIfc.CONTINUE;
            }
            catch (DataException e)
            {
                cargo.setDataExceptionErrorCode(e.getErrorCode());

                // Set the correct argument, getting it from the cargo
                String args[] = new String[1];
                UtilityManagerIfc utility =
                  (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
                args[0] = utility.getErrorCodeString(e.getErrorCode());

                // Journal the customer delete attempt. Journal Format requires two space indent.
                if (jmi != null)
                {
					dataArgs[0] = customer.getCustomerID().trim();
					jmi
							.journal(
									cargo.getEmployeeID(),
									cargo.getTransactionID(),
									I18NHelper
											.getString(
													I18NConstantsIfc.EJOURNAL_TYPE,
													JournalConstantsIfc.UNABLE_TO_DELETE_CUSTOMER_LABEL,
													dataArgs));
				}

                // Set button and arguments
                POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
                UIUtilities.setDialogModel(ui, DialogScreensIfc.ERROR, "DatabaseError",
                                           args, CommonLetterIfc.CONTINUE);
            }
        }//end (isTrainingMode() == false)
        bus.mail(new Letter(letterName), BusIfc.CURRENT);
    }
}
