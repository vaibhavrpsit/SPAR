/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  4/6/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/

package max.retail.stores.pos.services.customer.common;

// java imports
import java.util.zip.DataFormatException;

import oracle.retail.stores.domain.utility.AddressConstantsIfc;
import oracle.retail.stores.domain.utility.AddressIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.services.customer.common.CheckPostalCodeSite;
import oracle.retail.stores.pos.services.customer.common.CustomerCargo;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.DialogBeanModel;

//--------------------------------------------------------------------------
/**
    Check the postal code for validity.
    <p>
    @version $Revision: /rgbustores_12.0.9in_branch/1 $
**/
//--------------------------------------------------------------------------
public class MAXCheckPostalCodeSite extends CheckPostalCodeSite
{
    /**
        revision number
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";

    //----------------------------------------------------------------------
    /**
        Validates the postal code.
        <p>
        @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        CustomerCargo cargo = (CustomerCargo)bus.getCargo();

        // If valid, save formatted postal code to cargo.
        // If invalid based on the country, then validatePostalCode will throw a
        // DataFormatException. The exception is caught here, initiating display
        // of an error dialog screen.
        try
        {
        	 //MAX Rev 1.0 Change : Start 
            AddressIfc addressHome = (AddressIfc)cargo.getCustomer().getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_HOME);
            AddressIfc addressMail = (AddressIfc)cargo.getCustomer().getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_MAIL);
            AddressIfc addressWork = (AddressIfc)cargo.getCustomer().getAddressByType(AddressConstantsIfc.ADDRESS_TYPE_WORK);
            if(addressHome!=null){
            String postalString = addressHome.validatePostalCode(addressHome.getPostalCode(), addressHome.getCountry());

            // save formatted postal code that was returned from the validation method
            addressHome.setPostalCode(postalString);
            }
            if(addressMail!=null){
                String postalString = addressMail.validatePostalCode(addressMail.getPostalCode(), addressMail.getCountry());

                // save formatted postal code that was returned from the validation method
                addressMail.setPostalCode(postalString);
                }
            if(addressWork!=null){
                String postalString = addressWork.validatePostalCode(addressWork.getPostalCode(), addressWork.getCountry());

                // save formatted postal code that was returned from the validation method
                addressWork.setPostalCode(postalString);
                }
            //MAX Rev 1.0 Change : end 
            bus.mail(new Letter(CommonLetterIfc.CONTINUE), BusIfc.CURRENT);
        }
        catch (DataFormatException e)
        {
            // Using "generic dialog bean".
            DialogBeanModel dialogModel = new DialogBeanModel();

            // Set model to same name as dialog in config\posUI.properties
            dialogModel.setResourceID("InvalidPostalCode");
            dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
            dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);

            // set and display the model
            POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);

            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
         catch (NullPointerException e)
        {
        	DialogBeanModel dialogModel = new DialogBeanModel();
        	dialogModel.setResourceID("InvalidState");
        	dialogModel.setType(DialogScreensIfc.ACKNOWLEDGEMENT);
        	dialogModel.setButtonLetter(DialogScreensIfc.BUTTON_OK, CommonLetterIfc.RETRY);
        	POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        	ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
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
