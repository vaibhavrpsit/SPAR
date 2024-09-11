/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/find/CustomerLookupLaunchShuttle.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:27 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:37 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:40 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:23 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/09/23 00:07:15  kmcbride
 *   @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *   Revision 1.4  2004/04/09 16:56:02  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.3  2004/02/12 16:49:27  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:41:51  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:15  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 15:55:42   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   May 27 2003 12:28:48   baa
 * rework offline flow for customer
 * Resolution for 2455: Layaway Customer screen, blank customer name is accepted
 * 
 *    Rev 1.1   04 Sep 2002 08:55:28   djefferson
 * added support for Business Customer
 * Resolution for POS SCR-1605: Business Customer
 * 
 *    Rev 1.0   Apr 29 2002 15:33:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Mar 18 2002 23:12:22   msg
 * - updated copyright
 * 
 *    Rev 1.0   Mar 18 2002 11:25:06   msg
 * Initial revision.
 * 
 *    Rev 1.4   10 Dec 2001 15:56:02   baa
 * Fix minor defects in customer
 * Resolution for POS SCR-99: Data on Customer Delete screen should be non-editable
 *
 *    Rev 1.3   16 Nov 2001 10:33:00   baa
 * Cleanup code & implement new security model on customer
 * Resolution for POS SCR-263: Apply new security model to Customer Service
 *
 *    Rev 1.2   30 Oct 2001 16:10:36   baa
 * customer history. Enable training mode
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.1   23 Oct 2001 16:53:20   baa
 * updates for customer history and for getting rid of CustomerMasterCargo.
 * Resolution for POS SCR-209: Customer History
 *
 *    Rev 1.0   Sep 21 2001 11:15:30   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:07:02   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.find;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

//--------------------------------------------------------------------------
/**
    Transfer data from the CustomerFind service to the
    CustomerLookup service.
    <p>
    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CustomerLookupLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 6751778378417491948L;

    /**
        The logger to which log messages will be sent.
    **/
    protected static Logger logger = Logger.getLogger(oracle.retail.stores.pos.services.customer.find.CustomerLookupLaunchShuttle.class);

    /**
       revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    /**
           customer find cargo
        **/
    protected CustomerCargo customerCargo = null;

    //----------------------------------------------------------------------
    /**
       Stores information in the next service.
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
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

        // grab the cargo
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();
        cargo.setTransactionID(customerCargo.getTransactionID());
        if (customerCargo.getOriginalCustomer() != null)
        {
           cargo.setOriginalCustomer(customerCargo.getOriginalCustomer());
        }
        cargo.setCustomerGroups(customerCargo.getCustomerGroups());
        cargo.setEmployee(customerCargo.getEmployee());
        cargo.setLinkDoneSwitch(customerCargo.getLinkDoneSwitch());
        cargo.setOfflineExit(customerCargo.getOfflineExit());
        cargo.setRegister(customerCargo.getRegister());
        cargo.setLink(customerCargo.isLink());
        cargo.setCustomerLink(customerCargo.isCustomerLink());
        cargo.setOperator(customerCargo.getOperator());
        cargo.setNewCustomer(false);
        cargo.setOfflineIndicator(customerCargo.getOfflineIndicator());
        cargo.setAccessFunctionID(customerCargo.getAccessFunctionID());

        // set the screen to be used when displaying a list of customers
        cargo.setScreen(POSUIManagerIfc.CUSTOMER_SELECT_MODIFY);
        cargo.setFindOnlyMode(customerCargo.isFindOnlyMode());

        cargo.setAddCustomerMode(customerCargo.isAddCustomerEnabled());
        cargo.setAddBusinessMode(customerCargo.isAddBusinessEnabled());

    }

 }
