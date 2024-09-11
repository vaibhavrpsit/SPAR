/** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ************************
 * Copyright (c) 2013 Max Hypermarket.    All Rights Reserved. 
 *  Rev 1.0  3/4/2013               Izhar                                       MAX-POS-Customer-FES_v1.2.doc requirement.
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * ***************************/
package max.retail.stores.pos.ui.beans;

//java imports
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.domain.utility.PhoneConstantsIfc;
import oracle.retail.stores.domain.utility.PhoneIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.AlphaNumericTextField;
import oracle.retail.stores.pos.ui.beans.ConstrainedTextField;
import oracle.retail.stores.pos.ui.beans.CountryModel;
import oracle.retail.stores.pos.ui.beans.CustomerInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.MailBankCheckInfoBeanModel;
import oracle.retail.stores.pos.ui.beans.NaPhoneNumField;
import oracle.retail.stores.pos.ui.beans.NumericTextField;
import oracle.retail.stores.pos.ui.beans.ShippingMethodBeanModel;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBox;
import oracle.retail.stores.pos.ui.beans.ValidatingComboBoxModel;
import oracle.retail.stores.pos.ui.beans.ValidatingFieldIfc;
import oracle.retail.stores.pos.ui.beans.ValidatingFormattedTextField;
import oracle.retail.stores.pos.ui.beans.ValidatingTextField;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

//---------------------------------------------------------------------
/**
   This bean is used for displaying the Customer information screen
   based on the data from the CustomerInfoBeanModel.<P>
   @see com.extendyourstore.pos;ui.beans.CustomerInfoBeanModel
 */
//---------------------------------------------------------------------
public class MAXCustomerInfoBeanForLookup extends ValidatingBean
//public class MAXCustomerInfoBean extends CustomerInfoBean
{

    /**
        Fields and labels that contain customer data
    */
    protected JLabel customerIDLabel = null;
//  MAX Rev 1.0 Change : Start 
    protected JLabel ORLabel = null;
   //MAX Rev 1.0 Change : Start 
    protected JLabel asterixLabel = null;
    protected JLabel asterixLabelLN = null;
    protected JLabel asterixLabelMN = null;
//  MAX Rev 1.0 Change : end
    protected JLabel employeeIDLabel = null;
    protected JLabel firstNameLabel = null;
    protected JLabel lastNameLabel = null;
    protected JLabel addressLine1Label = null;
    protected JLabel addressLine2Label = null;
    protected JLabel addressLine3Label = null;
    protected JLabel cityLabel = null;
    protected JLabel stateLabel = null;
    protected JLabel postalCodeLabel = null;
    protected JLabel countryLabel = null;
    protected JLabel telephoneLabel = null;
    protected JLabel telephoneLabelNa = null;
    protected JLabel postalDelim = null;
    protected JLabel emailLabel = null;
    protected JLabel customerNameLabel = null;
    protected JLabel phoneTypeLabel = null;
    protected JLabel discountLabel = null;
    protected JLabel taxCertificateLabel;
    protected JLabel reasonCodeLabel;
    protected JLabel extPostalCodeLabel = null;

    
//  MAX Rev 1.0 Change : Start
    protected AlphaNumericTextField ORField = null;
//  MAX Rev 1.0 Change : end
    protected AlphaNumericTextField taxCertificateField;
    protected ValidatingComboBox reasonCodeField;
    protected ValidatingTextField customerNameField = null;
    protected ValidatingComboBox phoneTypeField = null;
    protected ConstrainedTextField emailField = null;
    protected AlphaNumericTextField customerIDField = null;
    protected AlphaNumericTextField employeeIDField = null;
    protected ConstrainedTextField firstNameField = null;
    protected ConstrainedTextField lastNameField = null;
    protected ConstrainedTextField addressLine1Field = null;
    protected ConstrainedTextField addressLine2Field = null;
    protected ConstrainedTextField addressLine3Field = null;
    protected ConstrainedTextField cityField = null;
    protected ConstrainedTextField postalCodeField = null;
    protected NumericTextField extPostalCodeField = null;
    protected NumericTextField telephoneField = null;
    protected NaPhoneNumField telephoneFieldNa = null;
	/*protected NaPhoneNumField telephoneField = null;*/
    protected ValidatingComboBox stateField = null;
    protected ValidatingComboBox countryField = null;
    protected ValidatingComboBox discountField = null;
    private int fieldTelephoneTypeSelected = PhoneConstantsIfc.PHONE_TYPE_HOME;

    protected boolean customerLookup = false;
    protected boolean businessCustomer = false;

    protected int initialCountryIndex;
    /**
        editable indicator
    **/
    protected boolean editableFields = true;

    /**
        Revision number supplied by source-code control system
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_12.0.9in_branch/1 $";

    //---------------------------------------------------------------------
    /**
       Default Constructor.
    */
    //---------------------------------------------------------------------
    public MAXCustomerInfoBeanForLookup()
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
        Initialize the class.
     */
    //---------------------------------------------------------------------
    protected void initialize()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();
        initLayout();
    }

    //--------------------------------------------------------------------------
    /**
     *    Initialize the layout.
     */
    protected void initLayout()
    {
        JPanel postalPanel = uiFactory.createPostalPanel(postalCodeField, extPostalCodeField, postalDelim);

        JLabel[] labels =
            {
                firstNameLabel,
                lastNameLabel,
                ORLabel,//  MAX Rev 1.0 Change : added
                //asterixLabel,//izhar
                customerNameLabel,
                addressLine1Label,
                addressLine2Label,
                addressLine3Label,
                cityLabel,
                countryLabel,
                stateLabel,
                postalCodeLabel,
                phoneTypeLabel,
                telephoneLabel,
                telephoneLabelNa,
                emailLabel,
                discountLabel,
                taxCertificateLabel,
                reasonCodeLabel,
                };
        JComponent[] components =
            {
                firstNameField,
                lastNameField,
                ORField,//  MAX Rev 1.0 Change : added 
                customerNameField,
                addressLine1Field,
                addressLine2Field,
                addressLine3Field,
                cityField,
                countryField,
                stateField,
                postalPanel,
                phoneTypeField,
                telephoneField,
                telephoneFieldNa,
                emailField,
                discountField,
                taxCertificateField,
                reasonCodeField };

        setLayout(new GridBagLayout());

        int xValue = 0;
        if (!getCustomerLookup()) // if customer info lookup do not layout the customer id
        {
            UIUtilities.layoutComponent(this, customerIDLabel, customerIDField, 0, 0, false);
            UIUtilities.layoutComponent(this, employeeIDLabel, employeeIDField, 0, 1, false);
            customerIDField.setLabel(customerIDLabel);
            employeeIDField.setLabel(employeeIDLabel);
            xValue = 2;
        }

        for (int i = 0; i < labels.length; i++)
        {
        	if(i==0)
            layoutComponent(this, labels[i], components[i],asterixLabel, 0, xValue, false);
        	 if(i==1)
         		layoutComponent(this, labels[i], components[i],asterixLabelLN, 0, xValue, false);
         	if(i==12)
         		layoutComponent(this, labels[i], components[i],asterixLabelMN, 0, xValue, false);
        	else
        	 UIUtilities.layoutComponent(this, labels[i], components[i], 0, xValue, false);
        	xValue++;
        }
        // init labels for fields
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);
        customerNameField.setLabel(customerNameLabel);

    }
    //---------------------------------------------------------------------
    /**
       Initializes the fields.
    */
    //---------------------------------------------------------------------
    protected void initializeFields()
    {
        addressLine1Field = uiFactory.createConstrainedField("addressLine1Field", "1", "30");
        addressLine2Field = uiFactory.createConstrainedField("addressLine2Field", "1", "30");
        addressLine3Field = uiFactory.createConstrainedField("addressLine3Field", "1", "30");
        cityField = uiFactory.createConstrainedField("cityField", "1", "20");
        firstNameField = uiFactory.createConstrainedField("firstNameField", "2", "16");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "2", "20");
        postalCodeField = uiFactory.createConstrainedField("postalCodeField", "0", "10");

        customerIDField = uiFactory.createAlphaNumericField("customerIDField", "1", "14");
    //  MAX Rev 1.0 Change : added
        ORField = uiFactory.createAlphaNumericField("ORField", "1", "2");
        employeeIDField = uiFactory.createAlphaNumericField("employeeIDField", "1", "14");
        extPostalCodeField = uiFactory.createNumericField("extpostalCodeField", "4", "4");
        extPostalCodeField.setColumns(10);
        stateField = uiFactory.createValidatingComboBox("stateField");
        countryField = uiFactory.createValidatingComboBox("countryField");

        emailField = uiFactory.createConstrainedField("emailField", "6", "30");

        telephoneField = uiFactory.createNumericField("telephoneField", "1", "10", "10");
		/*telephoneField = new NaPhoneNumField();*/
        telephoneField.setName("telephoneField");
        telephoneField.setColumns(14);
        
        telephoneFieldNa = new NaPhoneNumField();
        telephoneFieldNa.setName("telephoneField");
        telephoneFieldNa.setColumns(14);
        telephoneFieldNa.setLabel(telephoneLabelNa);
        telephoneFieldNa.setVisible(false);
        phoneTypeField = uiFactory.createValidatingComboBox("phoneTypeField");

        discountField = uiFactory.createValidatingComboBox("discountField");
        discountField.setEditable(false);
        customerNameField = uiFactory.createConstrainedField("customerNameField", "2", "30");
        taxCertificateField = uiFactory.createAlphaNumericField("taxCertificateField", "1", "15");
        reasonCodeField = uiFactory.createValidatingComboBox("reasonCodeField");

    }
    //---------------------------------------------------------------------
    /**
       Initializes the labels.
    */
    //---------------------------------------------------------------------
    protected void initializeLabels()
    {
        addressLine1Label = uiFactory.createLabel("addressLine1Label", "addressLine1Label", null, UI_LABEL);
        addressLine2Label = uiFactory.createLabel("addressLine2Label", "addressLine2Label", null, UI_LABEL);
        addressLine3Label = uiFactory.createLabel("addressLine3Label", "addressLine3Label", null, UI_LABEL);
        cityLabel = uiFactory.createLabel("cityLabel", "cityLabel", null, UI_LABEL);

        customerIDLabel = uiFactory.createLabel("customerIDLabel", "customerIDLabel", null, UI_LABEL);
    //  MAX Rev 1.0 Change :start
        ORLabel = uiFactory.createLabel("ORLabel", "ORLabel", null, UI_LABEL);
        //izhar
        asterixLabel = uiFactory.createLabel("asterixLabel", "*", null, UI_LABEL);
        asterixLabel.setForeground(Color.RED);
        asterixLabel.setFont(new Font("Helvetica", Font.BOLD, 20));
        asterixLabelLN = uiFactory.createLabel("asterixLabelLN", "*", null, UI_LABEL);
        asterixLabelLN.setForeground(Color.RED);
        asterixLabelLN.setFont(new Font("Helvetica", Font.BOLD, 20));
        asterixLabelMN = uiFactory.createLabel("asterixLabelMN", "*", null, UI_LABEL);
        asterixLabelMN.setForeground(Color.RED);
        asterixLabelMN.setFont(new Font("Helvetica", Font.BOLD, 20));
        employeeIDLabel = uiFactory.createLabel("employeeIDLabel", "employeeIDLabel", null, UI_LABEL);
    //MAX Rev 1.0 Change : end
       firstNameLabel = uiFactory.createLabel("firstNameLabel", "firstNameLabel", null, UI_LABEL);
        lastNameLabel = uiFactory.createLabel("lastNameLabel", "lastNameLabel", null, UI_LABEL);
        postalCodeLabel = uiFactory.createLabel("postalCodeLabel", "postalCodeLabel", null, UI_LABEL);
        stateLabel = uiFactory.createLabel("stateLabel", "stateLabel", null, UI_LABEL);
        countryLabel = uiFactory.createLabel("countryLabel", "countryLabel", null, UI_LABEL);
        telephoneLabel = uiFactory.createLabel("telephoneLabel", "telephoneLabel", null, UI_LABEL);
        telephoneLabelNa = uiFactory.createLabel("telephoneLabel", null, UI_LABEL);
        postalDelim = uiFactory.createLabel("postalDelim", "", null, UI_LABEL);
        emailLabel = uiFactory.createLabel("emailLabel", "emailLabel", null, UI_LABEL);
        customerNameLabel = uiFactory.createLabel("customerNameLabel", "customerNameLabel", null, UI_LABEL);
        phoneTypeLabel = uiFactory.createLabel("phoneTypeLabel", "phoneTypeLabel", null, UI_LABEL);
        discountLabel = uiFactory.createLabel("discountLabel", "discountLabel", null, UI_LABEL);
        taxCertificateLabel = uiFactory.createLabel("taxCertificateLabel", "taxCertificateLabel", null, UI_LABEL);

        reasonCodeLabel = uiFactory.createLabel("reasonCodeLabel", "reasonCodeLabel", null, UI_LABEL);

        extPostalCodeLabel = uiFactory.createLabel("extPostalCodeLabel", "extPostalCodeLabel", null, UI_LABEL);
    }

    //---------------------------------------------------------------------
    /**
        Updates the model from the screen.
    */
    //---------------------------------------------------------------------
    public void updateModel()
    {
        setChangeStateValue();
        setJournalStringUpdates();
        if (beanModel instanceof MAXCustomerInfoBeanModel)
        {
            CustomerInfoBeanModel model = (CustomerInfoBeanModel) beanModel;

            if (isBusinessCustomer() || model.isBusinessCustomer())
            {
                model.setOrgName(customerNameField.getText());
                model.setLastName(customerNameField.getText());

                if (!getCustomerLookup())
                {
                    model.setTaxCertificate(taxCertificateField.getText());

                    model.setSelected(false);
                    String reason = model.getSelectedReason();
                    if (reason != null)
                    {
                        model.setSelectedReasonCode(reasonCodeField.getSelectedIndex());
                        model.setSelected(true);
                    }
                }
            //  MAX Rev 1.0 Change : Start
                model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_MOBILE);
                //model.setTelephoneType(PhoneConstantsIfc.PHONE_TYPE_WORK);
				/*model.setTelephoneNumber(telephoneField.getPhoneNumber());*/
            //  MAX Rev 1.0 Change : end
                if(telephoneField.isVisible()){
                	// Changes start for code merging(commenting below line)
                	//model.setTelephoneNumber(telephoneField.getPhoneNumber());
                	model.setTelephoneNumber(telephoneField.getText());
                	// Changes ends for code merging
                }
                else
                {
                	model.setTelephoneNumber(telephoneFieldNa.getPhoneNumber());
                }
            }
            else
            {
                if (model instanceof MAXMailBankCheckInfoBeanModel)
                {
                    model.setSelected(false);
                    String reason = model.getSelectedReason();
                    if (reason != null)
                    {
                        model.setSelectedReasonCode(reasonCodeField.getSelectedIndex());
                        model.setSelected(true);
                    }
                }
                //For shipping to customer we can have business name
                //along with fist and last name
                boolean shippingToCustomer = model instanceof ShippingMethodBeanModel;
                if (shippingToCustomer &&
                    !Util.isEmpty(customerNameField.getText()))
                {
                    model.setOrgName(customerNameField.getText());
                }

                model.setFirstName(firstNameField.getText());
                model.setLastName(lastNameField.getText());
                model.setTelephoneType(phoneTypeField.getSelectedIndex());

                int indx = phoneTypeField.getSelectedIndex();
                if (indx == -1)
                {
                    indx = 0;
                }
                model.setTelephoneType(indx);

                if ( shippingToCustomer )
                {
                    // remove previously defined phone number.
                    // there is only phone number allowed for shipping customer.
                    // One of the reason is that the first available phone number will be loaded in updateBean()
                    if ( model.getPhoneList() != null )
                    {
                        for ( int i = 0; i < model.getPhoneList().length; i++ )
                        {
                            model.setTelephoneNumber("", i);
                        }
                    }
                }
				/*model.setTelephoneNumber(telephoneField.getText(), indx);*/
                if(telephoneField.isVisible())
                	model.setTelephoneNumber(telephoneField.getText(), indx);
                else
                	model.setTelephoneNumber(telephoneFieldNa.getText(), indx);
            }

            model.setAddressLine1(addressLine1Field.getText());
            
            model.setPostalCode(postalCodeField.getText());
            if (extPostalCodeField.isVisible())
            {
                ((MAXCustomerInfoBeanModel) model).setExtPostalCode(extPostalCodeField.getText());
            }

            if (!getCustomerLookup())
            {
                if (!model.isContactInfoOnly())
                {
                      /**
                       * @since 09AUG06 - CR17720 No point in setting a bogus id
                       * to customer since it is not mutable by the client. CMG
                       */
//                    model.setCustomerID(customerIDField.getText());
                    model.setSelectedCustomerGroupIndex(discountField.getSelectedIndex());
                }
                if (!(model instanceof MAXMailBankCheckInfoBeanModel))
                {
                    model.setEmployeeID(employeeIDField.getText());
                    model.setEmail(emailField.getText());
                }
                model.setAddressLine2(addressLine2Field.getText());
                model.setAddressLine3(addressLine3Field.getText());
                model.setCity(cityField.getText());

                model.setCountryIndex(countryField.getSelectedIndex());
                model.setStateIndex(stateField.getSelectedIndex());
            }
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates the information displayed on the screen's if the model's
        been changed.
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if (beanModel instanceof MAXCustomerInfoBeanModel)
        {
            // get model
            CustomerInfoBeanModel model = (CustomerInfoBeanModel) beanModel;
            //MAX Rev 1.0 Change : Start 
            boolean TICCust = false; 
            if( ((MAXCustomerInfoBeanModel) model).getTypeTICOrPOS().equalsIgnoreCase("T"))
            	TICCust=true;
            	
            // set edit mode
            boolean editMode = model.getEditableFields();
            //MAX Rev 1.0 Change : end
            if(TICCust)
            	editMode=false;

            if (model.isBusinessCustomer())
            {
                businessCustomer = true;
            }
            else
            {
                businessCustomer = false;
            }

            //business Customer fields to show
            // only for add/update screen
            boolean isBusinessCustomerUpdate = (businessCustomer && !customerLookup && !model.isContactInfoOnly());

            customerIDField.setText(model.getCustomerID());
            /**
             * @since 09AUG06 - CR17720 Do not make the field editable since this is a database primary key. - CMG
             */
//            setupComponent(customerIDField, isBusinessCustomerUpdate && editMode, !customerLookup && !model.isContactInfoOnly());
            setupComponent(customerIDField, false, !customerLookup && !model.isContactInfoOnly());
            customerIDField.setRequired(false);

            // hide first && last name fields if business customer
            firstNameField.setText(model.getFirstName());
            setupComponent(firstNameField, editMode, !businessCustomer);
            firstNameField.setRequired(!businessCustomer);
        //  MAX Rev 1.0 Change : Start
            setFieldRequired(firstNameField, !customerLookup && !businessCustomer);
        //  MAX Rev 1.0 Change : end
            lastNameField.setText(model.getLastName());
            setupComponent(lastNameField, editMode,  !businessCustomer);
            lastNameField.setRequired(!businessCustomer);
        //  MAX Rev 1.0 Change : Start
            setFieldRequired(lastNameField, !customerLookup && !businessCustomer);
            setupComponent(ORField, editMode, !businessCustomer && customerLookup);
            ORField.setVisible(false);
            employeeIDField.setText(model.getEmployeeID());
        //  MAX Rev 1.0 Change : end
            setupComponent(
                employeeIDField,
                editMode,
                !customerLookup && !(model instanceof MAXMailBankCheckInfoBeanModel));

            if (!customerLookup && !model.isContactInfoOnly())
            {
                // set up preferred customer field
                String[] discountStrings = model.getCustomerGroupStrings();
                // if strings exist, set them in field
                if (discountStrings != null)
                {
                    if (discountStrings.length > 0)
                    {
                        discountStrings[0] = retrieveText("NoneLabel", "None");
                    }
                    discountField.setModel(new DefaultComboBoxModel(discountStrings));
                    // if index valid, set it
                    int index = model.getSelectedCustomerGroupIndex();
                    if (index > -1)
                    {
                        discountField.setSelectedIndex(index);
                    }
                }
            }
            setupComponent(discountField, editMode, !customerLookup && !model.isContactInfoOnly());
            discountField.setFocusable(editMode);
            discountField.setEnabled(editMode);
            

            addressLine3Field.setText(model.getAddressLine3());
            setupComponent(addressLine3Field, editMode, (!customerLookup && model.is3LineAddress()));

            addressLine1Field.setText(model.getAddressLine1());
        //  MAX Rev 1.0 Change : Start
            setFieldRequired(addressLine1Field, !customerLookup && !businessCustomer);
            setupComponent(addressLine1Field, editMode, !customerLookup || businessCustomer);
            //addressLine1Field.setEditable(editMode);

            addressLine2Field.setText(model.getAddressLine2());
            setupComponent(addressLine2Field, editMode, !customerLookup);

            cityField.setText(model.getCity());
            setupComponent(cityField, editMode, !customerLookup);
            setFieldRequired(cityField, !customerLookup && !businessCustomer);
        //  MAX Rev 1.0 Change : end
            //Retrieve countries and update combo box
            setComboBoxModel(model.getCountryNames(), countryField, model.getCountryIndex());
            setupComponent(countryField, editMode, !customerLookup);
            countryField.setFocusable(editMode);
            countryField.setEnabled(editMode);

            boolean shippingToCustomer = model instanceof ShippingMethodBeanModel;
            if ( shippingToCustomer )
            {
                countryField.setRequired(true);
            }

            // update the state combo box with the new list of states
        //  MAX Rev 1.0 Change : Start
            stateField.setRequired(true);
            setComboBoxModel(model.getStateNames(), stateField, model.getStateIndex());
            setupComponent(stateField, editMode, !customerLookup);
            stateField.setFocusable(editMode);
            stateField.setEnabled(editMode);
        //  MAX Rev 1.0 Change : end
            if ( shippingToCustomer )
            {
                stateField.setRequired(true);
            }

            postalCodeField.setText(model.getPostalCode());
        //  MAX Rev 1.0 Change : Start
            setupComponent(postalCodeField, editMode, !customerLookup || businessCustomer);
            //setFieldRequired(postalCodeField, !customerLookup && !businessCustomer);
            
            //postalCodeField.setEditable(editMode);
        //  MAX Rev 1.0 Change : end
            extPostalCodeField.setText(((MAXCustomerInfoBeanModel) model).getExtPostalCode());
            extPostalCodeField.setEditable(editMode);

            // update the phone
            int index = model.getTelephoneIntType();
            if (index < 0)
            {
                index = 0;
            }
            // update the phone type list
            setComboBoxModel(model.getPhoneTypes(), phoneTypeField, index);
        //  MAX Rev 1.0 Change : Start
            setupComponent(phoneTypeField, editMode,customerLookup && !customerLookup && !businessCustomer && !(model.isMailBankCheck()));
            //setupComponent(phoneTypeField, editMode, !businessCustomer && !(model.isMailBankCheck()));
        //  MAX Rev 1.0 Change : end
            for (int i = 0; i < phoneTypeField.getItemCount(); i++)
            {
                String phoneNumber = model.getTelephoneNumber(i);
                if (!Util.isEmpty(phoneNumber))
                {
                    // make first available phone the default
                    index = i;
                    i = phoneTypeField.getItemCount();
                }
            }
            phoneTypeField.setFocusable(editMode);
            phoneTypeField.setEnabled(editMode);

            if("IN".equals(model.getCountry()))
            {
            telephoneField.setText(model.getTelephoneNumber(index));
            telephoneField.setEditable(editMode);
            setupComponent(telephoneField, editMode, true); // added - brian j.
        //  MAX Rev 1.0 Change : Start
            setFieldRequired(telephoneField, !customerLookup);
        //  MAX Rev 1.0 Change : end
            telephoneFieldNa.setVisible(false);
            telephoneLabelNa.setVisible(false);
            }
            else
            {
            	telephoneFieldNa.setPhoneNumber(model.getTelephoneNumber(index));
            	telephoneFieldNa.setEditable(editMode);
                setupComponent(telephoneFieldNa, editMode, true);
                telephoneField.setVisible(false);
                telephoneLabel.setVisible(false);
            }

            if (!businessCustomer)
            {
                phoneTypeField.setSelectedIndex(index);
            }

            emailField.setText(model.getEmail());
            setupComponent(emailField, editMode, !customerLookup && !(model instanceof MAXMailBankCheckInfoBeanModel));

            // update postalfields
            int countryIndx = model.getCountryIndex();
            if (countryIndx == -1)
            {
                countryIndx = 0;
            }
            initialCountryIndex = countryIndx;
            String[] stateList = model.getStateNames();

            // update the state combo box with the new list of states
            ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

            stateField.setModel(stateModel);
            int stateIndx = model.getStateIndex();
            if (stateIndx == -1)
            {
                stateIndx = 0;
            }
            stateField.setSelectedIndex(stateIndx);

            setPostalFields();

            // these are business customer specific fields
            customerNameField.setText(model.getOrgName());
            //This field is displayed for businessCustomer as well as shippingToCustomer
            //This field is optional for shippingToCustomer
            setupComponent(customerNameField, editMode, businessCustomer || shippingToCustomer);

            customerNameField.setRequired(businessCustomer);
            setFieldRequired(customerNameField, businessCustomer);


            taxCertificateField.setText(model.getTaxCertificate());
            setupComponent(taxCertificateField, editMode, isBusinessCustomerUpdate);

            if (model.getReasonCodes() != null)
            {
                ValidatingComboBoxModel listModel = new ValidatingComboBoxModel(
                        UIUtilities.getReasonCodeTextEntries(model.getReasonCodes()));
                reasonCodeField.setModel(listModel);
     
                if (model.isSelected())
                {
                    reasonCodeField.setSelectedItem(UIUtilities.retrieveCommonText(model.getSelectedReason()));
                } else
                {
                	reasonCodeField.setSelectedItem(UIUtilities.retrieveCommonText(model.getDefaultValue()));
                }
            }
            setupComponent(reasonCodeField, editMode, isBusinessCustomerUpdate || model.isMailBankCheck());
            reasonCodeField.setFocusable(editMode);
            reasonCodeField.setEnabled(editMode);


            if (model instanceof MAXMailBankCheckInfoBeanModel)
            {
                reasonCodeLabel.setText(retrieveText("IdTypeLabel", reasonCodeLabel));
            }
        }
    }

    //---------------------------------------------------------------------
    /**
        Convinience method to populate a comboBox
        @param data     the data to be display in the combo box
        @param field    the actual combo box field receiving the data
        @param selected index the default selected value
     */
    //---------------------------------------------------------------------
    protected void setComboBoxModel(String[] data, ValidatingComboBox field, int selectedIndex)
    {
        if (data != null)
        {
            ValidatingComboBoxModel model = new ValidatingComboBoxModel(data);

            field.setModel(model);

            field.setSelectedIndex(selectedIndex);
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates the information displayed on the screen's if the model's
        been changed.
     */
    //---------------------------------------------------------------------
    protected void setupComponent(JComponent field, boolean isEditable, boolean isVisible)
    {
        if (field instanceof ValidatingFieldIfc)
        {
            ((ValidatingFieldIfc) field).getLabel().setVisible(isVisible);
        }

        if (field instanceof JTextField)
        {
            ((JTextField) field).setEditable(isEditable);
        }
        field.setFocusable(isEditable);
        //field.setRequestFocusEnabled(isVisible);
        field.setVisible(isVisible);
    }

    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before display
     */
    public void activate()
    {
        super.activate();
        firstNameField.addFocusListener(this);
        customerNameField.addFocusListener(this);
        customerIDField.addFocusListener(this);
        employeeIDField.addFocusListener(this);
        countryField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                updateStates();
                setPostalFields();
                if(!"India".equalsIgnoreCase(countryField.getSelectedItem().toString()))
                {
              	 // modifyTelephoneNo();
              	  telephoneField.setVisible(false);
              	  telephoneLabel.setVisible(false);
              	  telephoneFieldNa.setVisible(true);
              	  telephoneLabelNa.setVisible(true);
                }
                else
                {
              	  telephoneField.setVisible(true);
              	  telephoneLabel.setVisible(true);
              	  telephoneFieldNa.setVisible(false);
              	  telephoneLabelNa.setVisible(false);
                }
            }
        });
        phoneTypeField.addActionListener(new ActionListener()
        {
            public void actionPerformed(ActionEvent e)
            {
                updatePhoneList(e);
            }
        });
    }

    //--------------------------------------------------------------------------
    /**
     *    Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        firstNameField.removeFocusListener(this);
        customerNameField.removeFocusListener(this);
        customerIDField.removeFocusListener(this);
        employeeIDField.removeFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *  Requests focus on parameter value name field if visible is true.
     *  @param aFlag true if setting visible, false otherwise
    **/
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            if (customerIDField.isVisible() && customerIDField.isEditable())
            {
                setCurrentFocus(customerIDField);
            }
            else
            {
                if (employeeIDField.isVisible())
                {
                    setCurrentFocus(employeeIDField);
                }
                else
                {
                    if (isBusinessCustomer())
                    {
                        setCurrentFocus(customerNameField);
                    }
                    else
                    {
                        setCurrentFocus(firstNameField);
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Indicates whether this screens is used for a lookup. True indicates it will
     *  be used for a lookup, false otherwise.
     *  @param propValue customer lookup indicator
     */
    public void setCustomerLookup(String propValue)
    {
        customerLookup = (new Boolean(propValue)).booleanValue();
    }

    //--------------------------------------------------------------------------
    /**
     *  Retrieves the customer lookup indicator. True indicates it will
     *  be used for a lookup, false otherwise.
     *  @param propValue customer lookup indicator
     */
    public boolean getCustomerLookup()
    {
        return (customerLookup);
    }

    //--------------------------------------------------------------------------
    /**
     *  Indicates whether this is a business customer scenario or not.
     *  @param propValue the business customer indicator
     */
    public void setBusinessCustomer(String propValue)
    {
        businessCustomer = (new Boolean(propValue)).booleanValue();
    }

    //--------------------------------------------------------------------------
    /**
     *  Retrieves the business customer indicator. True indicates it is
     *  a business customer, false it is not.
     *  @param propValue business customer indicator
     */
    public boolean isBusinessCustomer()
    {
        return (businessCustomer);
    }

    //----------------------------------------------------------------------------
    /**
     * Updates shipping charge base on the shipping method selected
     * @param ListSelectionEvent the listSelection event
     */
    //----------------------------------------------------------------------------
   public void updatePhoneList(ActionEvent e)
    {
       int indx = phoneTypeField.getSelectedIndex();
       if (indx == -1)
       {
           indx = 0;
       }
       String phonenumber=telephoneField.getText().trim();
       PhoneIfc phone = null;
       if(((CustomerInfoBeanModel)beanModel).getPhoneList()!= null)
       {
           phone = ((CustomerInfoBeanModel)beanModel).getPhoneList()[fieldTelephoneTypeSelected];
       }
       if (phone != null)
       {
           if (oracle.retail.stores.common.utility.Util.isEmpty(phonenumber))
           {
               phone.setStatusCode(PhoneConstantsIfc.STATUS_INACTIVE);
           }
           else
           {
               phone.setStatusCode(PhoneConstantsIfc.STATUS_ACTIVE);
               phone.setPhoneNumber(phonenumber);
           }
       }
       else
       {
           if(!oracle.retail.stores.common.utility.Util.isEmpty(phonenumber))
           {

               ((CustomerInfoBeanModel)beanModel).setTelephoneNumber(phonenumber, fieldTelephoneTypeSelected);
           }
       }
       fieldTelephoneTypeSelected = indx;
       ((CustomerInfoBeanModel)beanModel).setTelephoneType(indx);
       telephoneField.setText(((CustomerInfoBeanModel)beanModel).getTelephoneNumber(indx));
   }

    //---------------------------------------------------------------------------
    /**
     *  Update property fields.
     */
    //---------------------------------------------------------------------------
    protected void updatePropertyFields()
    {
        customerIDLabel.setText(retrieveText("CustomerIDLabel", customerIDLabel));
    //  MAX Rev 1.0 Change : added
        ORLabel.setText(retrieveText("ORLabel", ORLabel));
        employeeIDLabel.setText(retrieveText("EmployeeIDLabel", employeeIDLabel));
        customerNameLabel.setText(retrieveText("OrgNameLabel", customerNameLabel));
        firstNameLabel.setText(retrieveText("FirstNameLabel", firstNameLabel));
        lastNameLabel.setText(retrieveText("LastNameLabel", lastNameLabel));

        discountLabel.setText(retrieveText("DiscountLabel", discountLabel));
        taxCertificateLabel.setText(retrieveText("TaxCertificateLabel", taxCertificateLabel));

        reasonCodeLabel.setText(retrieveText("ReasonCodeLabel", reasonCodeLabel));

        addressLine1Label.setText(retrieveText("AddressLine1Label", addressLine1Label));
        addressLine2Label.setText(retrieveText("AddressLine2Label", addressLine2Label));
        addressLine3Label.setText(retrieveText("AddressLine3Label", addressLine3Label));
        cityLabel.setText(retrieveText("CityLabel", cityLabel));
        stateLabel.setText(retrieveText("StateProvinceLabel", stateLabel));
        countryLabel.setText(retrieveText("CountryLabel", countryLabel));
        postalCodeLabel.setText(retrieveText("PostalCodeLabel", postalCodeLabel));
        postalDelim.setText(retrieveText("ExtPostalCode", "-"));
        emailLabel.setText(retrieveText("EmailLabel", emailLabel));
        extPostalCodeLabel.setText(retrieveText("ExtendedPostalCode", "Extended Postal Code"));

        telephoneLabel.setText(retrieveText("TelephoneNumberLabel", telephoneLabel));
        telephoneLabelNa.setText(retrieveText("TelephoneNumberLabel", telephoneLabel));
        phoneTypeLabel.setText(retrieveText("PhoneTypeLabel", phoneTypeLabel));

        // customer info
        customerIDField.setLabel(customerIDLabel);
    //  MAX Rev 1.0 Change : added
        ORField.setLabel(ORLabel);
        employeeIDField.setLabel(employeeIDLabel);
        customerNameField.setLabel(customerNameLabel);
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);
        //ORLabel.show(arg0)
        discountField.setLabel(discountLabel);
        taxCertificateField.setLabel(taxCertificateLabel);
        reasonCodeField.setLabel(reasonCodeLabel);

        // address properties
        addressLine1Field.setLabel(addressLine1Label);
        addressLine2Field.setLabel(addressLine2Label);
        addressLine3Field.setLabel(addressLine3Label);
        cityField.setLabel(cityLabel);
        stateField.setLabel(stateLabel);
        countryField.setLabel(countryLabel);
        extPostalCodeField.setLabel(extPostalCodeLabel);
        postalCodeField.setLabel(postalCodeLabel);
        emailField.setLabel(emailLabel);

        // phone properties
        telephoneField.setLabel(telephoneLabel);
        phoneTypeField.setLabel(phoneTypeLabel);
    }

    //------------------------------------------------------------------------------
    /**
    *   Update states as country selection changes
    *   @param e a document event
    */
    public void updateStates()
    {
        int countryIndx = countryField.getSelectedIndex();
        if (countryIndx < 0)
        {
            countryIndx = 0;
        }
        String[] stateList = ((CountryModel) beanModel).getStateNames(countryIndx);

        stateList = LocaleUtilities.sort(stateList, getLocale());

        // update the state combo box with the new list of states
        ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

        stateField.setModel(stateModel);
        //select 1st element of the list for the current country
        stateField.setSelectedIndex(0);
    }

    //---------------------------------------------------------------------
    /**
       Determine what  postal fields should be enable/required.
    **/
    //---------------------------------------------------------------------
    protected void setPostalFields()
    {
        CountryModel countryModel = (CountryModel) beanModel;
    //  MAX Rev 1.0 Change : Start
        
        //setFieldRequired(postalCodeField, false);
        setFieldRequired(postalCodeField, !customerLookup && !businessCustomer);
        if(businessCustomer)
        {
        	setFieldRequired(postalCodeField, countryModel.isPostalCodeRequired(countryField.getSelectedIndex()));
        }
        //setFieldRequired(postalCodeField, countryModel.isPostalCodeRequired(countryField.getSelectedIndex()));
        //modification ends
    //  MAX Rev 1.0 Change : end
        if (countryModel.isExtPostalCodeRequired(countryField.getSelectedIndex()))
        {
            extPostalCodeField.setVisible(true);
        }
        else
        {
            extPostalCodeField.setVisible(false);
            extPostalCodeField.setText("");
        }
        String extPostalFormat = countryModel.getCountry(countryField.getSelectedIndex()).getExtPostalCodeFormat();
        String postalFormat = countryModel.getCountry(countryField.getSelectedIndex()).getPostalCodeFormat();
        if (extPostalFormat != null)
        {
            extPostalCodeField.setMinLength(extPostalFormat.length());
            extPostalCodeField.setMaxLength(extPostalFormat.length());
        }
        else
        {
            extPostalCodeField.setMinLength(0);
        }
        if (postalFormat != null)
        {
            postalCodeField.setMinLength(getMinLength(postalFormat));
            postalCodeField.setMaxLength(postalFormat.length());

        }
        else
        {
            postalCodeField.setMinLength(0);
        }
        telephoneField.setText(countryModel.getCountry(countryField.getSelectedIndex()).getPhoneFormat());

        postalDelim.setText(countryModel.getPostalCodeDelim(countryField.getSelectedIndex()));
        postalDelim.repaint();
    }

    protected int getMinLength(String value)
    {
        int minLen = 0;
        for (int i = 0; i < value.length(); i++)
        {
            //parse string to ignoring empty spaces
            // NOTE: the assumption that spaces are optional
            // characters
            if (!Character.isSpaceChar(value.charAt(i)))
            {
                minLen++;
            }
        }
        return minLen;
    }
    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: CustomerInfoBean (Revision " + getRevisionNumber() + ") @" + hashCode());
        if (beanModel != null)
        {

            strResult += "\n\nbeanModel = ";
            strResult += beanModel.toString();

        }
        else
        {
            strResult += "\nbeanModel = null\n";
        }

        if (customerIDField != null)
        {

            strResult += "\ncustomerIDField text = ";
            strResult += customerIDField.getText();
            strResult += ", min length = ";
            strResult += customerIDField.getMinLength();
            strResult += ", max length = ";
            strResult += customerIDField.getMaxLength();

        }
        else
        {
            strResult += "\ncustomerIDField = null\n";
        }

        if (employeeIDField != null)
        {

            strResult += "\nemployeeIDField text = ";
            strResult += employeeIDField.getText();
            strResult += ", min length = ";
            strResult += employeeIDField.getMinLength();
            strResult += ", max length = ";
            strResult += employeeIDField.getMaxLength();

        }
        else
        {
            strResult += "\nemployeeIDField = null\n";
        }

        if (firstNameField != null)
        {

            strResult += "\nfirstNameField text = ";
            strResult += firstNameField.getText();
            strResult += ", min length = ";
            strResult += firstNameField.getMinLength();
            strResult += ", max length = ";
            strResult += firstNameField.getMaxLength();

        }
        else
        {
            strResult += "\nfirstNameField = null\n";
        }

        if (lastNameField != null)
        {

            strResult += "\nlastNameField text = ";
            strResult += lastNameField.getText();
            strResult += ", min length = ";
            strResult += lastNameField.getMinLength();
            strResult += ", max length = ";
            strResult += lastNameField.getMaxLength();

        }
        else
        {
            strResult += "\nlastNameField = null\n";
        }

        if (addressLine1Field != null)
        {

            strResult += "\naddressLine1Field text = ";
            strResult += addressLine1Field.getText();
            strResult += ", min length = ";
            strResult += addressLine1Field.getMinLength();
            strResult += ", max length = ";
            strResult += addressLine1Field.getMaxLength();

        }
        else
        {
            strResult += "\naddressLine1Field = null\n";
        }

        if (addressLine2Field != null)
        {

            strResult += "\naddressLine2Field text = ";
            strResult += addressLine2Field.getText();
            strResult += ", min length = ";
            strResult += addressLine2Field.getMinLength();
            strResult += ", max length = ";
            strResult += addressLine2Field.getMaxLength();

        }
        else
        {
            strResult += "\naddressLine2Field = null\n";
        }

        if (cityField != null)
        {

            strResult += "\ncityField text = ";
            strResult += cityField.getText();
            strResult += ", min length = ";
            strResult += cityField.getMinLength();
            strResult += ", max length = ";
            strResult += cityField.getMaxLength();

        }
        else
        {
            strResult += "\ncityField = null\n";
        }

        if (postalCodeField != null)
        {

            strResult += "\npostalCodeField text = ";
            strResult += postalCodeField.getText();
            strResult += ", min length = ";
            strResult += postalCodeField.getMinLength();
            strResult += ", max length = ";
            strResult += postalCodeField.getMaxLength();

        }
        else
        {
            strResult += "\npostalCodeField = null\n";
        }

        if (extPostalCodeField != null)
        {

            strResult += "\nExtpostalCodeField text = ";
            strResult += extPostalCodeField.getText();
            strResult += ", min length = ";
            strResult += extPostalCodeField.getMinLength();
            strResult += ", max length = ";
            strResult += extPostalCodeField.getMaxLength();

        }
        else
        {
            strResult += "\nExtpostalCodeField = null\n";
        }
        strResult += "\neditableFields =" + editableFields + "\n";

        // pass back result
        return (strResult);
    }

    //------------------------------------------------------------------------
    /**
        Tests each data field to determine if user has entered or updated data.
        If data has changed then set the set change status to true, otherwise
        set it to false.
    */
    //------------------------------------------------------------------------
    protected void setChangeStateValue()
    {
        // convert the telephoneField to String for comparison
        PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
        phone.parseString(telephoneField.getText());
        String phoneNumber = new String(phone.getPhoneNumber());

        if (beanModel instanceof MailBankCheckInfoBeanModel)
        {
            MailBankCheckInfoBeanModel model = (MailBankCheckInfoBeanModel) beanModel;
            if ((!model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getFirstName(), firstNameField.getText()) != 0))
                || (!model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getLastName(), lastNameField.getText()) != 0))
                || (model.isBusinessCustomer() && (LocaleUtilities.compareValues(model.getOrgName(), customerNameField.getText()) != 0))
                || (LocaleUtilities.compareValues(model.getPostalCode(), postalCodeField.getText()) != 0)
                || (LocaleUtilities.compareValues(model.getAddressLine1(), addressLine1Field.getText()) != 0)
                || (LocaleUtilities.compareValues(model.getAddressLine2(), addressLine2Field.getText()) != 0)
                || (LocaleUtilities.compareValues(model.getAddressLine3(), addressLine3Field.getText()) != 0)
                || (LocaleUtilities.compareValues(model.getCity(), cityField.getText()) != 0)
                //|| (LocaleUtilities.compareValues(model.getExtPostalCode(), extPostalCodeField.getText()) != 0)
                || (LocaleUtilities
                    .compareValues(model.getTelephoneNumber(phoneTypeField.getSelectedIndex()), phoneNumber)
                    != 0)
                || (model.getSelectedIndex() != reasonCodeField.getSelectedIndex())
                || (model.getStateIndex() != stateField.getSelectedIndex())
                || (initialCountryIndex != countryField.getSelectedIndex()))
            {
                model.setChangeState(true); // user changed data
            }
            else
            {
                model.setChangeState(false); // same data as in the model
            }
        }
    }
    //------------------------------------------------------------------------
    /**
        Tests each data field to determine if user has entered or updated data.
        If data has changed then construct the journalString with the old and
        new values for this field.
    */
    //------------------------------------------------------------------------
    protected void setJournalStringUpdates()
    {
        if (beanModel instanceof MailBankCheckInfoBeanModel)
        {
            MailBankCheckInfoBeanModel model = (MailBankCheckInfoBeanModel) beanModel;
            // convert the telephoneField to String for comparison
            PhoneIfc phone = DomainGateway.getFactory().getPhoneInstance();
            phone.parseString(telephoneField.getText());
            String phoneNumber = new String(phone.getPhoneNumber());

            if (model.isBusinessCustomer())
            {
                if (model.getCustomerName().compareTo(customerNameField.getText()) != 0)
                {
                    model.setJournalString(
                        "\nOld Business Name: "
                            + model.getCustomerName()
                            + "\nNew Business Name: "
                            + customerNameField.getText());
                }
            }
            else
            {
                // test every field and set journal string for those that have changed
                if (model.getFirstName().compareTo(firstNameField.getText()) != 0)
                {
                    model.setJournalString(
                        "\nOld First Name: " + model.getFirstName() + "\nNew First Name: " + firstNameField.getText());
                }
                if (model.getLastName().compareTo(lastNameField.getText()) != 0)
                {
                    model.setJournalString(
                        model.getJournalString()
                            + "\nOld Last Name: "
                            + model.getLastName()
                            + "\nNew Last Name: "
                            + lastNameField.getText());
                }
            }
            if (model.getAddressLine1().compareTo(addressLine1Field.getText()) != 0)
            {
                model.setJournalString(
                    model.getJournalString()
                        + "\nOld Address Line1: "
                        + model.getAddressLine1()
                        + "\nNew Address Line1: "
                        + addressLine1Field.getText());
            }
            if (model.getAddressLine2().compareTo(addressLine2Field.getText()) != 0)
            {
                model.setJournalString(
                    model.getJournalString()
                        + "\nOld Address Line2: "
                        + model.getAddressLine2()
                        + "\nNew Address Line2: "
                        + addressLine2Field.getText());
            }

            if (model.getAddressLine3().compareTo(addressLine3Field.getText()) != 0)
            {
                model.setJournalString(
                    model.getJournalString()
                        + "\nOld Address Line3: "
                        + model.getAddressLine3()
                        + "\nNew Address Line3: "
                        + addressLine3Field.getText());
            }
            if (model.getCity().compareTo(cityField.getText()) != 0)
            {
                model.setJournalString(
                    model.getJournalString() + "\nOld City: " + model.getCity() + "\nNew City: " + cityField.getText());
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
            if ((model.getPostalCode().compareTo(postalCodeField.getText()) != 0))
                //|| (model.getExtPostalCode().compareTo(extPostalCodeField.getText()) != 0))
            {
                model.setJournalString(
                    model.getJournalString()
                        + "\nOld Postal Code: "
                        + model.getPostalCode()
                       /* + "-"
                        + model.getExtPostalCode()
                        + "\nNew Postal Code: "
                        + postalCodeField.getText()
                        + "-"
                        + extPostalCodeField.getText()*/);
            }
            if (model.getTelephoneNumber(model.getTelephoneIntType()).compareTo(phoneNumber) != 0)
            {
                model.setJournalString(
                    model.getJournalString()
                        + "\nOld Phone Number: "
                        + model.getTelephoneNumber(model.getTelephoneIntType())
                        + "\nNew Phone Number: "
                        + phoneNumber);
            }

            if (model.getEmail().compareTo(emailField.getText()) != 0)
            {
                model.setJournalString(
                    model.getJournalString()
                        + "\nOld Email: "
                        + model.getEmail()
                        + "\nNew Email: "
                        + emailField.getText());
            }
        }
    }
    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }
    //MAX Rev 1.0 Change : Start 
   /* protected void configureFoodTotals(BigDecimal foodTotal,BigDecimal nonFoodTotal){
        
    	if(p != null)
    		remove(p);
    	p = new JPanel();
    
    	foodTotalLabel = new JLabel("<html><b>"+FOOD_TOTAL+foodTotal+"<br><br>"+NON_FOOD_TOTAL+nonFoodTotal+"</b></html>");
    	foodTotalLabel.setFont(new Font("Helvetica", Font.PLAIN, 20));
    	uiFactory.configureUIComponent(p, "JPanel");
    	p.setLayout(new GridBagLayout());
    	p.add(foodTotalLabel);   
    	p.setBorder(BorderFactory.createLineBorder(Color.GRAY));
    	
    	add(p,BorderLayout.CENTER);
    }*/
    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        MAXCustomerInfoBean bean = new MAXCustomerInfoBean();
        bean.telephoneField.setValue("4043865851");
        System.out.println("1: " + bean.telephoneField.getFieldValue());
        bean.telephoneField.setValue("4(512)555-1212");
        System.out.println("2: " + bean.telephoneField.getFieldValue());
        bean.telephoneField.setValue("(512)555-1212");
        System.out.println("3: " + bean.telephoneField.getFieldValue());

        UIUtilities.doBeanTest(bean);
    }
    
    public static void layoutComponent(
            JPanel bean,
            JLabel label,
            JComponent component,
            JLabel asterixLabel2, int xValue,
            int yValue,
            boolean bottom)
        {
            UIFactory factory = UIFactory.getInstance();
            GridBagConstraints gbc = factory.getConstraints("DataEntryBean");

            gbc.fill = GridBagConstraints.HORIZONTAL;
            gbc.weightx = 0;
            gbc.weighty = 0;

            String labelInsets = new String("defaultLabelLeft");
            String fieldInsets = new String("defaultFieldRight");

            // layout top label and field
            gbc.gridx = xValue;
            gbc.gridy = yValue;

            if (yValue == 0) // use top insets
            {
                labelInsets = "defaultLabelTop";
                fieldInsets = "defaultFieldTop";
            }
            else if (bottom) // use bottom insets
            {
                labelInsets = "defaultLabelBottom";
                fieldInsets = "defaultFieldBottom";
            }

            gbc.insets = factory.getInsets(labelInsets);
            // check if the label is a divider
            if (label instanceof UIFactory.EYSDivider)
            {
                gbc.gridwidth = GridBagConstraints.REMAINDER;
            }
            bean.add(label, gbc);

            if (component != null)
            {
                gbc.fill = GridBagConstraints.NONE;
                if (bottom)
                {
                    gbc.gridwidth = GridBagConstraints.REMAINDER;
                    gbc.weighty = 1.0;
                    //gbc.weightx=1.0;
                }
                gbc.insets = factory.getInsets(fieldInsets);
                gbc.gridx = xValue + 1;
                bean.add(component, gbc);
            }
            if (asterixLabel2 != null){
            gbc.fill = GridBagConstraints.NONE;
            if (bottom)
            {
                gbc.gridwidth = GridBagConstraints.REMAINDER;
                gbc.weighty = 1.0;
                //gbc.weightx=1.0;
            }
            gbc.insets = factory.getInsets(fieldInsets);
            gbc.gridx = xValue + 2;
            bean.add(asterixLabel2, gbc);
            }
        }
    
    
}
