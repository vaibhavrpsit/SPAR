/* ===========================================================================
* Copyright (c) 2005, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/tdo/CaptureIRSCustomerTDO.java /main/15 2012/07/03 14:09:14 icole Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    icole     07/03/12 - FORWARD PORT: PAT customer information not being
 *                         printed on the receipt.
 *    asinton   03/21/12 - update CustomerIfc to use collections generics (i.e.
 *                         List<AddressIfc>) and remove old deprecated methods
 *                         and references to them
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/01/11 - Encryption CR
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    arathore  02/17/09 - Updated to set address type.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         8/7/2007 4:01:26 PM    Anda D. Cadar   Fix
 *         for birthdate of PAT Customer
 *    2    360Commerce 1.1         12/17/2006 4:08:50 PM  Brett J. Larsen CR
 *         21298 - country code appearing where country name should appear
 *    1    360Commerce 1.0         12/13/2005 4:47:05 PM  Barry A. Pape   
 *
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.tdo;

import java.util.HashMap;
import java.util.Vector;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.customer.CustomerIfc;
import oracle.retail.stores.domain.customer.IRSCustomerIfc;
import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.tdo.TDOAdapter;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.pos.ui.beans.CaptureIRSCustomerBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;

import org.apache.log4j.Logger;

/**
 * This class implements TDO functionality for the capture IRS customer use
 * case.
 * 
 * @version $Revision: /main/15 $
 */
public class CaptureIRSCustomerTDO extends TDOAdapter implements TDOUIIfc
{
    /** Revision number supplied by source-code control system **/
    public static final String revisionNumber = "$Revision: /main/15 $";

    /** The logger for this TDO **/
    protected static final Logger logger = Logger.getLogger(CaptureIRSCustomerTDO.class);

    /** The BUS attribute key **/
    public static final String BUS = "Bus";

    /**
     * buildBeanModel constructs a basic CaptureIRSCustomerBeanModel for use in
     * the capture IRS customer use-case.
     * 
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#buildBeanModel(java.util.HashMap)
     */
    public POSBaseBeanModel buildBeanModel(HashMap attributeMap)
    {
        BusIfc bus = (BusIfc)attributeMap.get(BUS);
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);

        CaptureIRSCustomerBeanModel model = new CaptureIRSCustomerBeanModel();
        String storeState = CustomerUtilities.getStoreState(pm);
        String storeCountry = CustomerUtilities.getStoreCountry(pm);

        int countryIndx = utility.getCountryIndex(storeCountry, pm);
        model.setCountryIndex(countryIndx);
        model.setStateIndex(utility.getStateIndex(countryIndx, storeState.substring(3, storeState.length()), pm));
        model.setCountries(utility.getCountriesAndStates(pm));
        return model;
    }

    /**
     * Not used in this implementation.
     * 
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine1(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     * @param txnADO
     * @return String
     */
    public String formatPoleDisplayLine1(RetailTransactionADOIfc txnADO)
    {
        // Not used in this implementation.
        return null;
    }

    /**
     * Not used in this implementation.
     * 
     * @see oracle.retail.stores.pos.tdo.TDOUIIfc#formatPoleDisplayLine2(oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc)
     * @param txnADO
     * @return String
     */
    public String formatPoleDisplayLine2(RetailTransactionADOIfc txnADO)
    {
        // Not used in this implementation.
        return null;
    }

    /**
     * Copies the customer information from a linked customer over to the model.
     * 
     * @param model
     * @param bus
     * @param customer
     */
    public void customerToModel(CaptureIRSCustomerBeanModel model, BusIfc bus, CustomerIfc customer)
    {
        UtilityManagerIfc utility = (UtilityManagerIfc)bus.getManager(UtilityManagerIfc.TYPE);
        ParameterManagerIfc pm = (ParameterManagerIfc)bus.getManager(ParameterManagerIfc.TYPE);
        if (customer != null)
        {
            model.setFirstName(getSafeString(customer.getFirstName()));
            model.setLastName(getSafeString(customer.getLastName()));
            model.setMiddleInitial(getSafeString(customer.getMiddleName()));
            if (model.getMiddleInitial().length() > 1)
            {
                model.setMiddleInitial(model.getMiddleInitial().substring(0, 1));
            }

            model.setDateOfBirth(customer.getBirthdate());
            model.setTaxPayerID(customer.getEncipheredTaxID());
            model.setOccupation("");

            if (customer.getAddressList().size() > 0)
            {
                AddressIfc address = customer.getAddressList().get(0);

                Vector<String> lines = address.getLines();
                if (lines.size() > 0)
                {
                    model.setAddressLine1(getSafeString(lines.get(0)));
                    if (lines.size() > 1)
                    {
                        model.setAddressLine2(getSafeString(lines.get(1)));
                    }
                }

                int countryIndex = 0;
                int stateIndex = 0;
                countryIndex = utility.getCountryIndex(address.getCountry(), pm);
                stateIndex = utility.getStateIndex(countryIndex, address.getState(), pm);
                model.setCountryIndex(countryIndex);
                model.setStateIndex(stateIndex);

                model.setCity(getSafeString(address.getCity()));
                model.setPostalCode(getSafeString(address.getPostalCode()));
                model.setExtPostalCode(getSafeString(address.getPostalCodeExtension()));
            }

            if (customer instanceof IRSCustomerIfc)
            {
                IRSCustomerIfc irsCustomer = (IRSCustomerIfc)customer;
                model.setOccupation(irsCustomer.getOccupation());
            }
        }
    }

    /**
     * Returns a string that is not null.
     * 
     * @param value The String to make safe
     * @return The safe string
     */
    public static String getSafeString(String value)
    {
        if (Util.isEmpty(value))
        {
            value = "";
        }
        return value.trim();
    }

    /**
     * Copies the model information over to the IRS customer.
     * 
     * @param model
     * @param bus
     * @param customer
     */
    public void modelToCustomer(CaptureIRSCustomerBeanModel model, BusIfc bus, IRSCustomerIfc customer)
    {
        if (customer != null)
        {
            customer.setFirstName(model.getFirstName());
            customer.setLastName(model.getLastName());
            customer.setMiddleName(model.getMiddleInitial());
            customer.setCustomerName(model.getFirstName() +" " +model.getLastName());
            // I18N - the UI for birthdate now accepts a 2 digits year, meaning
            // that by default years like 19 get translated by Java as 2019, and
            // we actually want a value of 1919.
            // So a check has been added : if year is greater than current
            // year, we subtract 100 years from the java created year
            // thus a value of 2019 becomes 1919
            EYSDate today = new EYSDate();
            EYSDate dob = model.getDateOfBirth();
            int yearInFuture = model.getDateOfBirth().getYear();
            if (yearInFuture >= today.getYear())
            {
                int yearInPast = yearInFuture - 100;
                dob.setYear(yearInPast);
            }
            customer.setBirthdate(dob);
            customer.setEncipheredTaxID(model.getTaxPayerID());
            customer.setOccupation(model.getOccupation());
            customer.setCustomerIDPrefix(Gateway.getProperty("application", "StoreID", null));

            AddressIfc address = null;
            if (customer.getAddressList().size() == 0)
            {
                address = DomainGateway.getFactory().getAddressInstance();
                customer.addAddress(address);
            }
            else
            {
                address = customer.getAddressList().get(0);
            }
            address.setCity(model.getCity());
            address.setState(model.getState());
            address.setCountry(model.getCountry());
            address.setPostalCode(model.getPostalCode());
            address.setPostalCodeExtension(model.getExtPostalCode());
            address.addAddressLine(model.getAddressLine1());
            address.addAddressLine(model.getAddressLine2());
            address.setAddressType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
        }
    }
}
