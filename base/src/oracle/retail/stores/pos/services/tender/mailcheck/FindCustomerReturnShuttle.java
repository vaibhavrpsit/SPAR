/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/mailcheck/FindCustomerReturnShuttle.java /main/12 2012/09/12 11:57:11 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:28:11 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:42 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:11:06 PM  Robert Pearse   
 * $
 * Revision 1.3  2004/09/23 00:07:16  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.2  2004/08/06 18:25:53  dcobb
 * @scr 6655 Letters being checked in shuttle classes.
 * Added check for Letter or letterName != null.
 *
 * Revision 1.1  2004/04/05 15:44:13  epd
 * @scr 4263 Moved Mail Bank Check to new sub tour
 *
 * Revision 1.6  2004/03/16 18:30:41  cdb
 * @scr 0 Removed tabs from all java source code.
 *
 * Revision 1.5  2004/02/27 20:59:05  bjosserand
 * @scr 0 Mail Bank Check
 *
 * Revision 1.4  2004/02/26 17:13:59  bjosserand
 * @scr 0 Mail Bank Check
 * Revision 1.3 2004/02/12 16:48:22 mcs Forcing head revision
 * 
 * Revision 1.2 2004/02/11 21:22:51 rhafernik @scr 0 Log4J conversion and code cleanup
 * 
 * Revision 1.1.1.1 2004/02/11 01:04:12 cschellenger updating to pvcs 360store-current
 * 
 * 
 * 
 * Rev 1.2 Feb 06 2004 16:53:56 bjosserand Mail Bank Check.
 * 
 * Rev 1.1 Feb 05 2004 14:27:16 bjosserand Mail Bank Check.
 * 
 * Rev 1.0 Feb 01 2004 13:44:32 bjosserand Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.mailcheck;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.LetterIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;
//--------------------------------------------------------------------------
/**
 * Returns from CustomerFind to Tender.
 * 
 * @version $Revision: /main/12 $
 */
//--------------------------------------------------------------------------
public class FindCustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -948865603512331521L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger =
        Logger.getLogger(oracle.retail.stores.pos.services.tender.mailcheck.FindCustomerReturnShuttle.class);
    ;

    public static final String SHUTTLENAME = "FindCustomerReturnShuttle";

    // the customer cargo
    protected CustomerCargo customerCargo = null;
    //----------------------------------------------------------------------
    /**
     * Gets a copy of CustomerCargo for use in unload().
     * <p>
     * 
     * @param bus
     *            the bus being loaded
     */
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        customerCargo = (CustomerCargo) bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
     * Links the Customer to the Transaction and copies Customer info.
     * <p>
     * 
     * @param bus
     *            the bus being unloaded
     */
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        LetterIfc ltr = (LetterIfc) bus.getCurrentLetter();
        if (ltr != null)
        {
            String letterName = ltr.getName();
            if (letterName != null)
            {   
                CustomerIfc customer = customerCargo.getCustomer();
                TenderCargo tenderCargo = (TenderCargo) bus.getCargo();
        
                if (letterName.equals(CommonLetterIfc.CONTINUE))
                {
                    if (customer != null)
                    {
                        tenderCargo.setCustomer(customer);
                        tenderCargo.setFindOrAddOrUpdateLinked(true);
        
                        // set the customer's name in the status area
                        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
                        StatusBeanModel statusModel = new StatusBeanModel();
                        statusModel.setCustomerName(customer.getCustomerName());
                        POSBaseBeanModel baseModel = new POSBaseBeanModel();
                        baseModel.setStatusBeanModel(statusModel);
                        ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
                    }
                    if (tenderCargo != null && tenderCargo.getTransaction() != null)
                    {
                        CustomerUtilities.journalCustomerExit(bus, 
                                tenderCargo.getTransaction().getCashier().getEmployeeID(),
                                tenderCargo.getTransaction().getTransactionID());
                    }
                }
            }
        }
    }
}
