/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/layaway/find/AddCustomerLaunchShuttle.java /main/1 2013/07/30 15:31:23 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     06/17/13 - Initial
 *
 * ===========================================================================
 * $Log: $
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.layaway.find;

import org.apache.log4j.Logger;

import oracle.retail.stores.pos.services.layaway.LayawayCargo;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;


//------------------------------------------------------------------------------
/**
 * Launch CustomerAdd from Layaway Lookup.
 *
 * @since Release 14
 */
//------------------------------------------------------------------------------
public class AddCustomerLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 1766224939014980969L;

    /**
     * The logger to which log messages will be sent.
     */
    protected static Logger logger =
        Logger.getLogger(oracle.retail.stores.pos.services.layaway.find.AddCustomerLaunchShuttle.class);
    ;

    /**
     * revision number
     */
    public static final String revisionNumber = "$Revision: /main/1 $";
    /**
     * shuttle name constant
     */
    public static final String SHUTTLENAME = "AddCustomerLaunchShuttle";
    /**
    the cargo being passed from the Find Layaway service to the Find Customer
    service
     */
    protected LayawayCargo layawayCargo = null;

    //--------------------------------------------------------------------------
    /**
     * Loads LayawayCargo into the shuttle for use in unload().
     *
     * @param bus - the bus being loaded
     */
    //--------------------------------------------------------------------------

    public void load(BusIfc bus)
    {
        layawayCargo = (LayawayCargo) bus.getCargo();
    }

    //--------------------------------------------------------------------------
    /**
     * Makes a CustomerAddCargo and populates it. Copies known customer information to the new Customer object.
     *
     * @param bus
     *            the bus being unloaded
     */
    //--------------------------------------------------------------------------

    public void unload(BusIfc bus)
    {
        CustomerCargo customerCargo = (CustomerCargo) bus.getCargo();

        // set role function ID
        customerCargo.setAccessFunctionID(RoleFunctionIfc.CUSTOMER_ADD_FIND);

        customerCargo.setHistoryMode(false);
        customerCargo.setRegister(layawayCargo.getRegister());
        customerCargo.setOperator(layawayCargo.getOperator());
        customerCargo.setLinkDoneSwitch(CustomerCargo.LINK);

    }

    //----------------------------------------------------------------------
    /**
     * Returns a string representation of this object.
     *
     * @return String representation of object
     */
    //----------------------------------------------------------------------
    public String toString()
    { // begin toString()
        // result string
        String strResult =
            new String("Class:  AddCustomerLaunchShuttle (Revision " + getRevisionNumber() + ")" + hashCode());

        // pass back result
        return (strResult);
    } // end toString()

    //----------------------------------------------------------------------
    /**
     * Returns the revision number of the class.
     *
     * @return String representation of revision number
     */
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    { // begin getRevisionNumber()
        // return string
        return (revisionNumber);
    } // end getRevisionNumber()
}
