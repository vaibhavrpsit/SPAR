/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/tender/mailcheck/GetCheckMailingInfoSite.java /main/13 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    npoola    12/20/10 - action button texts are moved to CommonActionsIfc
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.tender.mailcheck;

import java.util.HashMap;

import oracle.retail.stores.pos.ado.ADO;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.transaction.RetailTransactionADOIfc;
import oracle.retail.stores.pos.ado.utility.Utility;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.pos.services.tender.tdo.MailBankCheckTDO;
import oracle.retail.stores.pos.tdo.TDOException;
import oracle.retail.stores.pos.tdo.TDOFactory;
import oracle.retail.stores.pos.tdo.TDOUIIfc;
import oracle.retail.stores.domain.transaction.TenderableTransactionIfc;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.manager.ifc.UtilityManagerIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.services.customer.common.CustomerUtilities;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.POSBaseBeanModel;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBeanModel;
import oracle.retail.stores.pos.services.common.CommonActionsIfc;
import oracle.retail.stores.domain.customer.CustomerIfc;

//-----------------------------------------------------------------------------
/**
 * Present the Mail Bank Check UI
 * 
 * @version $Revision: /main/13 $
 */
//-----------------------------------------------------------------------------
public class GetCheckMailingInfoSite extends PosSiteActionAdapter
{

    /**
     * revision number for this class
     */
    public static final String revisionNumber = "$KW=@(#); $Ver=pos_4.5.0:77; $EKW:";

    // Site name
    public static final String SITENAME = "GetCheckMailingInfoSite";

    //-------------------------------------------------------------------------
    /**
     * Get the CustomerIfc data if linked Put up the MBC entry screen
     * 
     * @param bus
     *            the bus arriving at this site
     */
    //-------------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        UtilityIfc utility;
        try
        {
            utility = Utility.createInstance();
        }
        catch (ADOException e)
        {
            String message = "Configuration problem: could not instantiate UtilityIfc instance";
            logger.error(message, e);
            throw new RuntimeException(message, e);
        }
        if ("N".equals(utility.getParameterValue("OracleCustomer", "N")))
        {
            bus.mail(new Letter(CommonLetterIfc.ONE_TIME_CUSTOMER_CAPTURE), BusIfc.CURRENT);
        }
        else
        {
            //Get the cargo and transaction objects
            TenderCargo cargo = (TenderCargo) bus.getCargo();

            POSUIManagerIfc ui = (POSUIManagerIfc) bus.getManager(UIManagerIfc.TYPE);

            RetailTransactionADOIfc transADO = cargo.getCurrentTransactionADO();
            TenderableTransactionIfc trans = (TenderableTransactionIfc) ((ADO) transADO).toLegacy();

            if (trans == null) // If current transaction unavailable from ADO object,
            { // then get it from TenderCargo,
                trans = cargo.getTenderableTransaction();
                // and reflect it in ADO transaction
                 ((ADO) transADO).fromLegacy(trans);
                // BTW, if cargo also doesn't have a tenderable transaction in this case, were S.O.L.!!!
                // From what I can tell, the embedded RDO object should not be null.
            }

            // Setup hashmap for use by TDO object.
            HashMap attributeMap = new HashMap(3);
            attributeMap.put(MailBankCheckTDO.BUS, bus);
            attributeMap.put(MailBankCheckTDO.TRANSACTION, cargo.getCurrentTransactionADO());
            attributeMap.put(MailBankCheckTDO.ORIG_RETURN_TXNS, cargo.getOriginalReturnTxnADOs());

            // build bean model helper
            TDOUIIfc tdo = null;
            try
            {
                tdo = (TDOUIIfc) TDOFactory.create("tdo.tender.MailBankCheck");
            }
            catch (TDOException tdoe)
            {
                logger.error("Error creating MailBankCheck TDO object", tdoe);
            }
            
            POSBaseBeanModel model = tdo.buildBeanModel(attributeMap);
            configureLocalButtons(model, cargo);
            ui.showScreen(POSUIManagerIfc.MAIL_BANK_CHECK_INFO, model);
        }
    }

    //-------------------------------------------------------------------------
    /**
     * Display the specified Error Dialog
     * 
     * @param String
     *            name of the Error Dialog to display
     * @param POSUIManagerIfc
     *            UI Manager to handle the IO
     */
    //-------------------------------------------------------------------------
    protected void displayErrorDialog(POSUIManagerIfc ui, String name)
    {
        DialogBeanModel dialogModel = new DialogBeanModel();
        dialogModel.setResourceID(name);
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.INVALID);        
        ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
    }

    //-------------------------------------------------------------------------
    /**
     * Sets the default state, country code and phone type when none is available or the customer was linked offline.
     * Uses the parameter manager to retrieve the values for state and country, sets phone type to Home.
     * 
     * @param ui
     *            model to set the data
     * @param bus
     *            the bus arriving at this site to get the parameter manager
     * @return updated model
     */
    //-------------------------------------------------------------------------
    protected MailBankCheckInfoBeanModel setModeldefaultValues(MailBankCheckInfoBeanModel model, BusIfc bus)
    {
        // Get defaults for state and country code from parameter mgr
        ParameterManagerIfc pm = (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        UtilityManagerIfc utility = (UtilityManagerIfc) bus.getManager(UtilityManagerIfc.TYPE);
        String defaultState = CustomerUtilities.getStoreState(pm);
        String defaultCountry = CustomerUtilities.getStoreCountry(pm);

        // get list of all available states and selected country and state
        int countryIndex = utility.getCountryIndex(defaultCountry, pm);
        model.setCountryIndex(countryIndex);
        model.setStateIndex(utility.getStateIndex(countryIndex, defaultState.substring(3, defaultState.length()), pm));
        model.setCountries(utility.getCountriesAndStates(pm));
        model.setPhoneTypes(CustomerUtilities.getPhoneTypes(utility));
        if (model.isBusinessCustomer())
        {
            model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
        }
        else
        {
            model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_HOME);
        }

        return (model);
    }
    

    public void configureLocalButtons(POSBaseBeanModel model, TenderCargo cargo)
    {
        NavigationButtonBeanModel navModel = new NavigationButtonBeanModel();
        
        CustomerIfc customerMBC = cargo.getCustomer();
        CustomerIfc customerLinked = cargo.getTransaction().getCustomer();

        if (customerMBC != null || customerLinked != null)
            navModel.setButtonEnabled(CommonActionsIfc.UPDATE,true);
        else 
            navModel.setButtonEnabled(CommonActionsIfc.UPDATE,false);
        
        model.setLocalButtonBeanModel(navModel); 
    }    

    
}
