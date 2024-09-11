/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CustomerCargoReturnShuttle.java /main/11 2013/11/08 16:30:12 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  11/08/13 - customer group change not persisted when tour
 *                         reloads page
 *    sgu       11/03/11 - fix nullpointer in previous customer
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:35 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:34 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:19 PM  Robert Pearse
 *
 *   Revision 1.5  2004/09/23 00:07:11  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:02  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:25  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:40:12  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:14  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 15:55:10   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   May 27 2003 08:48:02   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 *
 *    Rev 1.3   Mar 03 2003 15:47:38   RSachdeva
 * Clean Up Code Conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 14 2002 11:09:06   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Jul 18 2002 15:24:10   RSachdeva
 * Code conversion
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 15:33:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:18   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:24:02   msg
 * Initial revision.
 *
 *    Rev 1.4   07 Jan 2002 13:20:44   baa
 * fix journal problems and adding offline
 * Resolution for POS SCR-506: Customer Find prints 'Add Custumer: ' in EJ
 *
 *    Rev 1.3   17 Dec 2001 10:42:34   baa
 * updates to print customer name on status bar
 * Resolution for POS SCR-199: Cust Offline screen returns to Sell Item instead of Cust Opt's
 *
 *    Rev 1.2   10 Dec 2001 15:55:44   baa
 * Fix minor defects in customer
 * Resolution for POS SCR-99: Data on Customer Delete screen should be non-editable
 *
 *    Rev 1.1   16 Nov 2001 10:31:58   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-209: Customer History
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.0   Sep 21 2001 11:15:14   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

// foundation imports
import org.apache.log4j.Logger;

import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.LineItemsModel;
import oracle.retail.stores.pos.ui.beans.StatusBeanModel;

//--------------------------------------------------------------------------
/**
    Transfer data from a Customer service back to another Customer service.
    @version $Revision: /main/11 $
**/
//--------------------------------------------------------------------------
public class CustomerCargoReturnShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -6331622922883672748L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.customer.common.CustomerCargoReturnShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /main/11 $";
    /**
      Customer cargo
     **/
    protected CustomerCargo customerCargo = null;


    //----------------------------------------------------------------------
    /**
       Transfers data from one customer service to another.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {   // Store customer cargo
        customerCargo = (CustomerCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Transfers data from one customer service to another.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        CustomerCargo cargo  = (CustomerCargo)bus.getCargo();
        CustomerIfc originalCustomer = customerCargo.getPreviousCustomer();
        cargo.setDiscountTypeChanged(customerCargo.getDiscountTypeChanged());
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        StatusBeanModel statusModel = new StatusBeanModel();
        if (customerCargo.getCustomer() != null)
        {   // set the Customer reference in the cargo
            cargo.setCustomer(customerCargo.getCustomer());
            cargo.setOfflineIndicator(customerCargo.getOfflineIndicator());
            if (customerCargo.isLink())
            {
                statusModel.setCustomerName(cargo.getCustomer().getCustomerName());
            }

        }
        else if (originalCustomer !=null)
        {
            // Change status back to original customer
            // set the customer's name in the status area
            statusModel.setCustomerName(originalCustomer.getCustomerName());
            LineItemsModel baseModel = new LineItemsModel();
            baseModel.setStatusBeanModel(statusModel);
            ui.setModel(POSUIManagerIfc.SHOW_STATUS_ONLY, baseModel);
        }

        cargo.setCustomerLink(customerCargo.isCustomerLink());
        cargo.setLink(customerCargo.isLink());
        cargo.setNewCustomer(customerCargo.isNewCustomer());
        // Need to know if db is offline
        cargo.setDataExceptionErrorCode(customerCargo.getDataExceptionErrorCode());
    }

    //----------------------------------------------------------------------
    /**
       Returns a string representation of this object.
       @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: " +  getClass().getName() + " (Revision " +
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
