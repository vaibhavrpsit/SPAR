/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/LinkCustomerReturnShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:48 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:28:51 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:23:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:19 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:12  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:01  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:48:22  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:22:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:12  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 04 2003 11:17:46   epd
 * Initial revision.
 * 
 *    Rev 1.0   Oct 24 2003 14:54:52   bwf
 * Initial revision.
 * Resolution for 3418: Purchase Order Tender Refactor
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//--------------------------------------------------------------------------
/**
    This shuttle returns from the customer service with a customer.
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class LinkCustomerReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -2735207988343454360L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.tender.LinkCustomerReturnShuttle.class);

    public static final String SHUTTLENAME = "FindCustomerReturnShuttle";

    // the customer  cargo
    protected CustomerCargo customerCargo = null;
    //----------------------------------------------------------------------
    /**
       Gets a copy of CustomerCargo for use in unload(). <p>
       @param bus the bus being loaded
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        customerCargo = (CustomerCargo) bus.getCargo();
    }
    //----------------------------------------------------------------------
    /**
       Links the Customer to the Transaction and copies Customer info. <p>
       @param bus the bus being unloaded
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        CustomerIfc customer = customerCargo.getCustomer();
        if (customer != null)
        {
            TenderCargo tenderCargo = (TenderCargo)bus.getCargo();

            // Link the customer to the transaction

         //   tenderCargo.setCustomer(customerCargo.getCustomer());
            RetailTransactionADOIfc trans = 
                    (RetailTransactionADOIfc) tenderCargo.getCurrentTransactionADO();
            trans.linkCustomer(customer);
                 
            // set the customer's name in the status area
            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
            StatusBeanModel statusModel = new StatusBeanModel();
            statusModel.setCustomerName(customer.getCustomerName());
            POSBaseBeanModel baseModel = new POSBaseBeanModel();
            baseModel.setStatusBeanModel(statusModel);
            ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);

        }

    }
}
