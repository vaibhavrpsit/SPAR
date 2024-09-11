/*===========================================================================
* Copyright (c) 2012, Oracle and/or its affiliates. All rights reserved. 
* ===========================================================================
* $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/send/displaysendmethod/ShipDestinationTaxRuleLaunchShuttle.java /main/1 2012/10/22 15:36:19 yiqzhao Exp $
* ===========================================================================
* NOTES
* <other useful comments, qualifications, etc.>
*
* MODIFIED    (MM/DD/YY)
* yiqzhao     10/19/12 - Refactor, using DestinationTaxRule station to get tax
*                        rules based on shipping/send destination postal code.
* yiqzhao     10/17/12 - Creation
* ===========================================================================
*/

package oracle.retail.stores.pos.services.send.displaysendmethod;

// foundation imports
import java.util.Arrays;
import java.util.List;

import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.tax.SendTaxUtil;
import oracle.retail.stores.domain.utility.AddressBookEntryIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.tour.ifc.ShuttleIfc;
import oracle.retail.stores.pos.services.destinationtaxrule.DestinationTaxRuleCargo;
import oracle.retail.stores.pos.services.send.address.SendCargo;

//--------------------------------------------------------------------------
/**
    This shuttle updates the customer cargo with information from the item cargo.
    $Revision: /main/1 $
**/
//--------------------------------------------------------------------------
public class ShipDestinationTaxRuleLaunchShuttle implements ShuttleIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    static final long serialVersionUID = 1L;

    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /main/1 $";

    /**
       item cargo
    **/
    protected SendCargo sendCargo = null;

    //----------------------------------------------------------------------
    /**
        Copies information needed from parent service to child service.
        @param  bus    parent Service Bus to copy cargo from.
    **/
    //----------------------------------------------------------------------
    public void load(BusIfc bus)
    {
        // retrieve cargo from the parent service
    	sendCargo = (SendCargo)bus.getCargo();
    }

    //----------------------------------------------------------------------
    /**
        Stores information needed by child service. Copies the access employee
        and sales associate, sets the exit when offline flag for customer.
        @param  bus     Parent Service Bus to copy cargo to.
    **/
    //----------------------------------------------------------------------
    public void unload(BusIfc bus)
    {
        // retrieve cargo from the child - customer main/customer
        DestinationTaxRuleCargo cargo = (DestinationTaxRuleCargo)bus.getCargo();

        SendTaxUtil util = new SendTaxUtil();
        // set access
        List<SaleReturnLineItemIfc> lineItems = Arrays.asList(sendCargo.getLineItems());
        cargo.setTaxGroupIDs(util.getUniqueTaxGroups(lineItems));
      
        List<AddressIfc> addressList = sendCargo.getShipToInfo().getAddressList();
        if ( addressList!=null && !addressList.isEmpty() )
        {
        	cargo.setShippingPostalCode(addressList.get(0).getPostalCode());
        }
        else if (sendCargo.getCustomer()!=null && !sendCargo.getCustomer().getAddressList().isEmpty() )
        {
        	cargo.setShippingPostalCode(sendCargo.getCustomer().getAddressList().get(0).getPostalCode());
        }
        
        // reset the database error code to UNKNOWN
        cargo.setDataExceptionErrorCode(DataException.UNKNOWN);
    }

    //----------------------------------------------------------------------
    /**
        Returns a string representation of this object.
        @return String representation of object
    **/
    //----------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class:  ShipDestinationTaxRuleLaunchShuttle (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());
        return(strResult);
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
