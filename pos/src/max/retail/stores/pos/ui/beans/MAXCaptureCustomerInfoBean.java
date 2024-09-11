/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2016 - 2017 MAX Hypermarket, Inc.    All Rights Reserved.
 *	
 *	Rev 1.0     Nov 08, 2016		Ashish Yadav		Home Delivery Send FES (Telephone Number not coming on UI after customer Search)

 *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package max.retail.stores.pos.ui.beans;

import java.util.Vector;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.CaptureCustomerInfoBean;
import oracle.retail.stores.pos.ui.beans.CaptureCustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;

public class MAXCaptureCustomerInfoBean  extends CaptureCustomerInfoBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
    private static Logger logger = Logger.getLogger(max.retail.stores.pos.ui.beans.MAXCaptureCustomerInfoBean.class);


	protected void updateBean()
    {
		logger.info("MAXCaptureCustomerInfoBean"+ beanModel);

        if (beanModel instanceof CaptureCustomerInfoBeanModel)
        {
        	// get model
            CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel) beanModel;

            // set edit mode
            boolean editMode = model.getEditableFields();
    		logger.info("Edit Mode"+ editMode);

            if(!editMode)
            {
            	
            	firstNameField.setEditable(editMode);
            	lastNameField.setEditable(editMode);
            	addressLine1Field.setEditable(editMode);
            	addressLine2Field.setEditable(editMode);
            	cityField.setEditable(editMode);
            	postalCodeField.setEditable(editMode);
 // Changes starts for code merging(commenting below line)
            	//extPostalCodeField.setEditable(editMode);
// Changes ends for code merging
// Changes starts for code merging(commenting below line)
            	//IDTypeField.setEditable(editMode); 
            	idTypeField.setEditable(editMode); 
// Changes ends for code merging      
            	telephoneField.setEditable(editMode);
            	phoneTypeField.setEnabled(editMode);
            	stateField.setEnabled(editMode);
            	countryField.setEnabled(editMode);
// Changes starts for code merging(commenting below line)
            	//IDTypeField.setVisible(editMode);
            	idTypeField.setVisible(editMode);
            	//IDTypeLabel.setVisible(editMode);
            	idTypeLabel.setVisible(editMode);
// Changes ends for code merging            	
            	
            }
            else
            {
            	firstNameField.setEditable(editMode);
            	lastNameField.setEditable(editMode);
            	addressLine1Field.setEditable(editMode);
            	addressLine2Field.setEditable(editMode);
            	cityField.setEditable(editMode);
            	postalCodeField.setEditable(editMode);
// Changes starts for code merging(commenting below line)
            	//extPostalCodeField.setEditable(editMode);
// Changes ends for code merging
// Changes starts for code merging(commenting below line)
            	//IDTypeField.setEditable(editMode); 
            	idTypeField.setEditable(editMode); 
// Changes ends for code merging
            	telephoneField.setEditable(editMode);
            	phoneTypeField.setEnabled(editMode);
            	stateField.setEnabled(editMode);
            	countryField.setEnabled(editMode);
// Changes starts for code merging(commenting below line)
            	//IDTypeField.setVisible(!editMode);
            	//IDTypeLabel.setVisible(!editMode);
            	idTypeField.setVisible(!editMode);
            	idTypeLabel.setVisible(!editMode);
// Changes ends for code merging
            }
            // hide first && last name fields if business customer
            firstNameField.setText(model.getFirstName());
            lastNameField.setText(model.getLastName());
            addressLine1Field.setText(model.getAddressLine1());
            addressLine2Field.setText(model.getAddressLine2());
            cityField.setText(model.getCity());
 // Changes starts for code merging(commenting below line)           
            //IDTypeField.setSelectedItem("Passport");
            idTypeField.setSelectedItem("Passport");
//Changes ends for code merging
            
            if (model.getCountryIndex() < 0)
            {
                model.setCountryIndex(0);
            }
            if (model.getStateIndex() < 0)
            {
                model.setStateIndex(0);
            }
            //Retrieve countries and update combo box
            setComboBoxModel(model.getCountryNames(), countryField, model.getCountryIndex());
            // update the state combo box with the new list of states
            setComboBoxModel(model.getStateNames(), stateField, model.getStateIndex());
            postalCodeField.setText(model.getPostalCode());
 // Changes starts for code merging(commenting below line)
            //extPostalCodeField.setText(model.getExtPostalCode());
  //Changes ends for code merging       
            // update the phone
            int index = model.getPhoneType();
            if (index < 0)
            {
                index = 0;
            }
            // update the phone type list
            setComboBoxModel(model.getPhoneTypes(), phoneTypeField, index);
            // Chnages start for Rev 1.0 (Send : Showing phone number after clicking send button on capture customer screen)
           /* telephoneField.setValue(model.getPhoneNumber(index));
            phoneTypeField.setSelectedIndex(index);*/
         // Chnages end for Rev 1.0 (Send : Showing phone number after clicking send button on capture customer screen)
            // update postalfields
            int countryIndx = model.getCountryIndex();
            if (countryIndx == -1)
            {
                countryIndx = 0;
            }
            setPostalFields();
         // Chnages start for Rev 1.0 (Send : Showing phone number after clicking send button on capture customer screen)
            telephoneField.setValue(model.getPhoneNumber(index));
            phoneTypeField.setSelectedIndex(index);
         // Chnages end for Rev 1.0 (Send : Showing phone number after clicking send button on capture customer screen)
            Vector idTypes = model.getIDTypes();
            //model.currentIDType = -1;
            ValidatingComboBoxModel listModel =
            new ValidatingComboBoxModel(UIUtilities.getReasonCodeTextEntries(idTypes));
            idTypeField.setModel(listModel);
            
 // Changes starts for code merging(commenting below line)
               //IDTypeField.setSelectedIndex(idTypes.indexOf("PanCard"));
            idTypeField.setSelectedIndex(idTypes.indexOf("PanCard"));
  //Changes ends for code merging         
          

          //setComboBoxModel(model.getIDTypes(), IDTypeField, indx);
        }
    }
	
	 public void updateModel()
	    {
	        setJournalStringUpdates();
	        if(beanModel instanceof CaptureCustomerInfoBeanModel)
	        {
	            CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel)beanModel;
	            model.setFirstName(firstNameField.getText());
	            model.setLastName(lastNameField.getText());
	            model.setPhoneType(phoneTypeField.getSelectedIndex());
	            int indx = phoneTypeField.getSelectedIndex();
	            model.setPhoneNumber(telephoneField.getFieldValue(), model.getPhoneType());
	            model.setAddressLine1(addressLine1Field.getText());
	            model.setAddressLine2(addressLine2Field.getText());
	            model.setPostalCode(postalCodeField.getText());
 // Changes starts for code merging(commenting below line)
	           // if(extPostalCodeField.isVisible())
	               // model.setExtPostalCode(extPostalCodeField.getText());
//Changes ends for code merging              
	            model.setCity(cityField.getText());
	            model.setCountryIndex(countryField.getSelectedIndex());
	            model.setStateIndex(stateField.getSelectedIndex());
	            Vector idTypes = model.getIDTypes();
	            model.setSelectedIDType(idTypes.indexOf("PanCard"));
	            model.setPostalCodeRequired(postalCodeField.isRequired());
	        }
	    }
	/**MAX Rev 1.2 Change : Start**/
	   protected void setJournalStringUpdates()
	    {
	        if (beanModel instanceof CaptureCustomerInfoBeanModel)
	        {
	            CaptureCustomerInfoBeanModel model = (CaptureCustomerInfoBeanModel) beanModel;
	            // convert the telephoneField to String for comparison
	            PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
	            phone.parseString(telephoneField.getText());
	            String telephoneFieldPhoneNumber = new String(phone.getPhoneNumber());
	            String modelPhoneNumber = model.getPhoneNumber();
	            if (model.getFirstName()!=null && model.getFirstName().compareTo(firstNameField.getText()) != 0) //Rev 1.3 change
	            {
	                model.setJournalString(
	                        model.getJournalString()
	                            + "\nOld First Name: "
	                            + model.getFirstName()
	                            + "\nNew First Name: "
	                            + firstNameField.getText());
	            }
	            if (model.getLastName()!=null&& model.getLastName().compareTo(lastNameField.getText()) != 0)  //Rev 1.3 changes
	            {
	                model.setJournalString(
	                        model.getJournalString()
	                            + "\nOld Last Name: "
	                            + model.getLastName()
	                            + "\nNew Last Name: "
	                            + lastNameField.getText());
	            }
	            if (model.getAddressLine1().compareTo(addressLine1Field.getText()) != 0)
	            {
	                model.setJournalString(
	                        model.getJournalString()
	                            + "\nOld Address Line 1: "
	                            + model.getAddressLine1()
	                            + "\nNew Address Line 1: "
	                            + addressLine1Field.getText());
	            }
	            if (model.getAddressLine2().compareTo(addressLine2Field.getText()) != 0)
	            {
	                model.setJournalString(
	                        model.getJournalString()
	                            + "\nOld Address Line 2: "
	                            + model.getAddressLine2()
	                            + "\nNew Address Line 2: "
	                            + addressLine2Field.getText());
	            }
	            if (model.getCity().compareTo(cityField.getText()) != 0)
	            {
	                model.setJournalString(
	                        model.getJournalString()
	                            + "\nOld City: "
	                            + model.getCity()
	                            + "\nNew City: "
	                            + cityField.getText());
	            }
 // Chnages start for code merging(commenting below line)
	            //if ((model.getPostalCode().compareTo(postalCodeField.getText()) != 0)|| (model.getExtPostalCode().compareTo(extPostalCodeField.getText()) != 0))
	            if ((model.getPostalCode().compareTo(postalCodeField.getText()) != 0))
// Changes ends for code merging
	            {
	                model.setJournalString(
	                    model.getJournalString()
	                        + "\nOld Postal Code: "
	                        + model.getPostalCode()
	                        + "-"
// Chnages start for code merging(commenting below line)
	                        //+ model.getExtPostalCode()
// Changes ends for code merging
	                        + "\nNew Postal Code: "
	                        + postalCodeField.getText());
 // Chnages start for code merging(commenting below line)
	                       // + "-"
	                       // + extPostalCodeField.getText());
 // Changes ends for code merging
	            }

	            if (modelPhoneNumber.compareTo(telephoneFieldPhoneNumber) != 0)
	            {
	                model.setJournalString(
	                        model.getJournalString()
	                            + "\nOld Phone Number: "
	                            + modelPhoneNumber
	                            + "\nNew Phone Number: "
	                            + telephoneFieldPhoneNumber);
	            }
	            if ( model.getPhoneType()!= -1 && model.getPhoneType() != phoneTypeField.getSelectedIndex())
	            {
	                model.setJournalString(
	                    model.getJournalString()
	                        + "\nOld Phone Type: "
	                        + model.getPhoneTypes()[model.getPhoneType()]
	                        + "\nNew Phone Type: "
	                        + (String) phoneTypeField.getSelectedItem());
	            }
	            if (model.getStateIndex() != stateField.getSelectedIndex())
	            {
	                model.setJournalString(
	                    model.getJournalString()
	                        + "\nOld State: "
	                        + model.getState()
	                        + "\nNew State: "
	                        + (String) stateField.getSelectedItem());
	            }
	            if (model.getCountryIndex() != countryField.getSelectedIndex())
	            {
	                model.setJournalString(
	                    model.getJournalString()
	                        + "\nOld Country: "
	                        + model.getCountry()
	                        + "\nNew Country: "
	                        + (String) countryField.getSelectedItem());
	            }
// Chnages start for code merging(commenting below line)
	           // if (model.getCurrentIDType() != IDTypeField.getSelectedIndex())
	            if (model.getCurrentIDType() != idTypeField.getSelectedIndex())
	// Chnages ends for code merging
	            {
	                String modelIDType = model.getIDType(model.getCurrentIDType());
 // Chnages start for code merging(commenting below line)
	                //String fieldIDType = model.getIDType(IDTypeField.getSelectedIndex());
	                String fieldIDType = model.getIDType(idTypeField.getSelectedIndex());
 // Chnages ends for code merging
	                model.setJournalString(
	                    model.getJournalString()
	                        + "\nOld ID Type: "
	                        + modelIDType
	                        + "\nNew ID Type: "
	                        + fieldIDType);
	            }

	        }
	    }
	/**MAX Rev 1.2 Change : End**/	
}