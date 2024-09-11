/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/CustomerCargoLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:26 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
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
 *    Rev 1.2   May 27 2003 08:48:00   baa
 * rework customer offline flow
 * Resolution for 2387: Deleteing Busn Customer Lock APP- & Inc. Customer.
 *
 *    Rev 1.1   04 Sep 2002 08:44:50   djefferson
 * added support for Business Customer
 * Resolution for POS SCR-1605: Business Customer
 *
 *    Rev 1.0   Apr 29 2002 15:33:28   msg
 * Initial revision.
 *
 *    Rev 1.1   Mar 18 2002 23:11:16   msg
 * - updated copyright
 *
 *    Rev 1.0   Mar 18 2002 11:24:02   msg
 * Initial revision.
 *
 *    Rev 1.5   07 Jan 2002 13:20:42   baa
 * fix journal problems and adding offline
 * Resolution for POS SCR-506: Customer Find prints 'Add Custumer: ' in EJ
 *
 *    Rev 1.4   17 Dec 2001 10:42:32   baa
 * updates to print customer name on status bar
 * Resolution for POS SCR-199: Cust Offline screen returns to Sell Item instead of Cust Opt's
 *
 *    Rev 1.3   10 Dec 2001 15:55:42   baa
 * Fix minor defects in customer
 * Resolution for POS SCR-99: Data on Customer Delete screen should be non-editable
 *
 *    Rev 1.2   16 Nov 2001 10:31:56   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.1   30 Oct 2001 16:10:28   baa
 * customer history. Enable training mode
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:15:02   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:06:52   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

//--------------------------------------------------------------------------
/**
    Shuttle data from the Customer services.
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CustomerCargoLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = -5505158816974237230L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.customer.common.CustomerCargoLaunchShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Customer Cargo
     */
    protected CustomerCargo customerCargo = null;
    //----------------------------------------------------------------------
    /**
       Saves the relevent information from the current service. <p>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // get the cargo of the current service
        customerCargo = (CustomerCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
       Stores information in the next service.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {

        CustomerCargo cargo = (CustomerCargo)bus.getCargo();

        cargo.setTransactionID(customerCargo.getTransactionID());
        if (customerCargo.getOriginalCustomer() != null)
        {
          cargo.setPreviousCustomer(customerCargo.getOriginalCustomer());
        }
        cargo.setCustomerGroups(customerCargo.getCustomerGroups());
        cargo.setEmployee(customerCargo.getEmployee());
        cargo.setLinkDoneSwitch(customerCargo.getLinkDoneSwitch());
        cargo.setOfflineExit(customerCargo.getOfflineExit());
        cargo.setRegister(customerCargo.getRegister());
        cargo.setLink(customerCargo.isLink());
        cargo.setCustomerLink(customerCargo.isCustomerLink());
        cargo.setOperator(customerCargo.getOperator());
        cargo.setOfflineIndicator(customerCargo.getOfflineIndicator());
        cargo.setAccessFunctionID(RoleFunctionIfc.CUSTOMER_ADD_FIND);

        cargo.setAddCustomerMode(customerCargo.isAddCustomerEnabled());
        cargo.setAddBusinessMode(customerCargo.isAddBusinessEnabled());
        // Setting Array of Pricing group.
        cargo.setPricingGroup(customerCargo.getPricingGroup());
        // Pricing Group names in customer cargo.
        cargo.setPricingGroupNames(customerCargo.getPricingGroupNames());
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
