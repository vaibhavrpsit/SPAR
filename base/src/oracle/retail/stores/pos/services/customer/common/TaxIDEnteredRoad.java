/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/customer/common/TaxIDEnteredRoad.java /main/5 2012/03/26 13:08:26 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    rrkohli   07/01/11 - Encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    mahising  11/13/08 - Added for Customer module for both ORPOS and ORCO
 *    mahising  11/12/08 - added for customer
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.customer.common;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.LaneActionAdapter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;

/**
 * Traversed if tax ID is entered
 */
@SuppressWarnings("serial")
public class TaxIDEnteredRoad extends LaneActionAdapter
{
    /**
     * Stores Tax ID entered by the user.
     * 
     * @param bus
     *            the bus traversing this lane
     */
    public void traverse(BusIfc bus)
    {
        // get the user input
        POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);
        String taxID = ui.getInput();
        // build customer
        CustomerIfc customer = DomainGateway.getFactory().getCustomerInstance();
        EncipheredDataIfc encipheredTaxID = null;
        try
        {
            encipheredTaxID = FoundationObjectFactory.getFactory().createEncipheredDataInstance(taxID.getBytes());
        }
        catch(EncryptionServiceException ese)
        {
            logger.warn("Could not encrypt tax ID", ese);
        }
        customer.setEncipheredTaxID(encipheredTaxID);

        // store the tax ID in the cargo
        CustomerCargo cargo = (CustomerCargo) bus.getCargo();
        cargo.setCustomer(customer);
        cargo.setTaxID(taxID);
    }

}
