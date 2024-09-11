/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/patriot/ValidateIDReturnShuttle.java /rgbustores_13.4x_generic_branch/3 2011/09/02 13:05:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/01/11 - Encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  11/03/08 - updated files related to customer id type reason
 *                         code.
 *    abondala  11/03/08 - updated files related to the Patriotic customer ID
 *                         types reason code
 *
 * ===========================================================================


     $Log:
      1    360Commerce 1.0         12/13/2005 4:47:05 PM  Barry A. Pape
     $

 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */
package oracle.retail.stores.pos.services.tender.patriot;

import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.validateid.ValidateIDCargoIfc;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;

/**
 * This shuttle populates the IRSCustomer with ID information following ID
 * validation.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/3 $
 */
public class ValidateIDReturnShuttle implements ShuttleIfc
{
    private static final long serialVersionUID = -7991197356322929372L;

    /** Cargo of visited service **/
    protected ValidateIDCargoIfc childCargo;

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#load(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void load(BusIfc bus)
    {
        childCargo = (ValidateIDCargoIfc)bus.getCargo();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.foundation.tour.ifc.ShuttleIfc#unload(oracle.retail.stores.foundation.tour.ifc.BusIfc)
     */
    @Override
    public void unload(BusIfc bus)
    {
        TenderCargo callingCargo = (TenderCargo)bus.getCargo();
        IRSCustomerIfc irsCustomer = callingCargo.getCurrentTransactionADO().getIRSCustomer();
        if (irsCustomer != null)
        {
            irsCustomer.setLocalizedPersonalIDCode(childCargo.getLocalizedPersonalIDCode());
            irsCustomer.setVerifyingID(childCargo.getIdNumberEncipheredData());
            irsCustomer.setVerifyingIdIssuingCountry(childCargo.getIDCountry());
            irsCustomer.setVerifyingIdIssuingState(childCargo.getIDState());
        }
    }
}
