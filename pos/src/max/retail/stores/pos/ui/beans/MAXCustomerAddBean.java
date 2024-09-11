/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	
 *  Rev 1.2    	 14/09/2018 		Purushotham Reddy   	Changes for Code Merge Prod Defects
 *  Rev 1.1  	 10/09/2018         Bhanu Priya             Code Merge CR
 *  Rev 1.0      3/4/2013           Izhar             		MAX-POS-Customer-FES_v1.2.doc requirement.
 *
 ********************************************************************************/


package max.retail.stores.pos.ui.beans;

//java imports
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


//---------------------------------------------------------------------
/**
 This bean is used for displaying the Customer information screen
 based on the data from the CustomerInfoBeanModel.<P>
 @version $Revision: /rgbustores_12.0.9in_branch/1 $
 @see com.extendyourstore.pos;ui.beans.CustomerInfoBeanModel
*/
//---------------------------------------------------------------------
public class MAXCustomerAddBean extends ValidatingBean
{

  /**
      Fields and labels that contain customer data
  */
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
  protected AlphaNumericTextField taxCertificateField;
  protected ValidatingComboBox reasonCodeField;
  protected ValidatingTextField customerNameField = null;
  protected ValidatingComboBox phoneTypeField = null;
  protected ConstrainedTextField emailField = null;
  protected AlphaNumericTextField employeeIDField = null;
  protected MAXOnlyLettersWithoutSpaceTextField firstNameField = null;
  protected MAXOnlyLettersWithoutSpaceTextField lastNameField = null;
  protected ConstrainedTextField addressLine1Field = null;
  protected ConstrainedTextField addressLine2Field = null;
  protected ConstrainedTextField addressLine3Field = null;
  protected MAXOnlyLettersWithoutSpaceTextField cityField = null;
  protected NumericTextField postalCodeField = null;
  //protected NumericTextField extPostalCodeField = null;
  protected ValidatingFormattedTextField telephoneField = null;
  protected NaPhoneNumField telephoneFieldNa = null;
  /*  protected NaPhoneNumField telephoneField = null;*/
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
  public MAXCustomerAddBean()
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
      JPanel postalPanel = uiFactory.createPostalPanel(postalCodeField);

      JLabel[] labels =
          {
              firstNameLabel,
              lastNameLabel,
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
          UIUtilities.layoutComponent(this, employeeIDLabel, employeeIDField, 0, 0, false);

          employeeIDField.setLabel(employeeIDLabel);
          xValue = 1;
      }

      for (int i = 0; i < labels.length; i++)
      {
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
      /*firstNameField = uiFactory.createConstrainedField("firstNameField", "2", "16");
      lastNameField = uiFactory.createConstrainedField("lastNameField", "2", "20");*/
	  // Rev 1.2  Changes for Code Merge Prod Defects
      firstNameField = new MAXOnlyLettersWithoutSpaceTextField("", 1, 20);
      firstNameField.setColumns(20);
      
      lastNameField = new MAXOnlyLettersWithoutSpaceTextField("", 1, 20);
      lastNameField.setColumns(20);
      
      cityField = new MAXOnlyLettersWithoutSpaceTextField("", 1, 20);
      cityField.setColumns(20);
      
      addressLine1Field = uiFactory.createConstrainedField("addressLine1Field", "1", "30");
      addressLine2Field = uiFactory.createConstrainedField("addressLine2Field", "1", "30");
      addressLine3Field = uiFactory.createConstrainedField("addressLine3Field", "1", "30");
      
      postalCodeField = uiFactory.createNumericField("postalCodeField", "0", "10");
      employeeIDField = uiFactory.createAlphaNumericField("employeeIDField", "1", "14");
      
      /*extPostalCodeField = uiFactory.createNumericField("extpostalCodeField", "4", "4");
      extPostalCodeField.setColumns(10);*/
      stateField = uiFactory.createValidatingComboBox("stateField");
      countryField = uiFactory.createValidatingComboBox("countryField");

      emailField = uiFactory.createConstrainedField("emailField", "6", "30");

      telephoneField = uiFactory.createValidatingFormattedTextField("telephoneField", "", "30", "20");
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
      firstNameLabel = uiFactory.createLabel("firstNameLabel", null, UI_LABEL);
      lastNameLabel = uiFactory.createLabel("lastNameLabel", null, UI_LABEL);
      addressLine1Label = uiFactory.createLabel("addressLine1Label", null, UI_LABEL);
      addressLine2Label = uiFactory.createLabel("addressLine2Label", null, UI_LABEL);
      addressLine3Label = uiFactory.createLabel("addressLine3Label", null, UI_LABEL);
      cityLabel = uiFactory.createLabel("cityLabel", null, UI_LABEL);
      employeeIDLabel = uiFactory.createLabel("employeeIDLabel", null, UI_LABEL);
      postalCodeLabel = uiFactory.createLabel("postalCodeLabel", null, UI_LABEL);
      stateLabel = uiFactory.createLabel("stateLabel", null, UI_LABEL);
      countryLabel = uiFactory.createLabel("countryLabel", null, UI_LABEL);
      telephoneLabel = uiFactory.createLabel("telephoneLabel", null, UI_LABEL);
      telephoneLabelNa = uiFactory.createLabel("telephoneLabel", null, UI_LABEL);
      postalDelim = uiFactory.createLabel("", null, UI_LABEL);
      emailLabel = uiFactory.createLabel("emailLabel", null, UI_LABEL);
      customerNameLabel = uiFactory.createLabel("customerNameLabel", null, UI_LABEL);
      phoneTypeLabel = uiFactory.createLabel("phoneTypeLabel", null, UI_LABEL);
      discountLabel = uiFactory.createLabel("discountLabel", null, UI_LABEL);
      taxCertificateLabel = uiFactory.createLabel("taxCertificateLabel", null, UI_LABEL);
      reasonCodeLabel = uiFactory.createLabel("reasonCodeLabel", null, UI_LABEL);
      extPostalCodeLabel = uiFactory.createLabel("extPostalCodeLabel", null, UI_LABEL);
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
 //   MAX Rev 1.0 Change : Start 
      phoneTypeLabel.setVisible(false);
      phoneTypeField.setVisible(false);
 //   MAX Rev 1.0 Change : end
      if (beanModel instanceof CustomerInfoBeanModel)
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
              int indx = phoneTypeField.getSelectedIndex();
              if (indx == -1)
              {
                  indx = 0;
              }
              model.setTelephoneType(indx);

			  /*model.setTelephoneNumber(telephoneField.getPhoneNumber());*/
              if(telephoneField.isVisible())
            	  model.setTelephoneNumber(telephoneField.getFieldValue(), indx);
              else
            	  model.setTelephoneNumber(telephoneFieldNa.getPhoneNumber());
          }
          else
          {
              if (model instanceof MailBankCheckInfoBeanModel)
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
         /* if (extPostalCodeField.isVisible())
          {
              model.setExtPostalCode(extPostalCodeField.getText());
          }*/

          if (!getCustomerLookup())
          {
              if (!model.isContactInfoOnly())
              {
                  model.setSelectedCustomerGroupIndex(discountField.getSelectedIndex());
              }
              if (!(model instanceof MailBankCheckInfoBeanModel))
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
      if (beanModel instanceof CustomerInfoBeanModel)
      {
    	  
          // get model
          CustomerInfoBeanModel model = (CustomerInfoBeanModel) beanModel;

          // set edit mode
          boolean editMode = model.getEditableFields();

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

          // hide first && last name fields if business customer
          firstNameField.setText(model.getFirstName());
          setupComponent(firstNameField, editMode, !businessCustomer);
          firstNameField.setRequired(!businessCustomer);
          setFieldRequired(firstNameField, !businessCustomer);
          lastNameField.setText(model.getLastName());
          setupComponent(lastNameField, editMode, !businessCustomer);
          lastNameField.setRequired(!businessCustomer);
          setFieldRequired(lastNameField, !businessCustomer);
     //   MAX Rev 1.0 Change : Start 
          employeeIDField.setText(model.getEmployeeID());
     //   MAX Rev 1.0 Change : end
          setupComponent(
              employeeIDField,
              editMode,
              !customerLookup && !(model instanceof MailBankCheckInfoBeanModel));
     
          if (!customerLookup && !model.isContactInfoOnly())
          {
              // set up preferred customer field
              String[] discountStrings = model.getCustomerGroupStrings();
              String[] none = new String[1];
              none[0] = "None";
             // String[] none =discountStrings[0] ;
              // if strings exist, set them in field
              if (discountStrings != null)
              {
                  if (discountStrings.length > 0)
                  {
                      discountStrings[0] = retrieveText("NoneLabel", "None");
                  }
                  //discountField.setModel(new DefaultComboBoxModel(discountStrings));
                  discountField.setModel(new DefaultComboBoxModel(none));
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
          addressLine1Field.setEditable(editMode);

          addressLine2Field.setText(model.getAddressLine2());
          setupComponent(addressLine2Field, editMode, !customerLookup);

          cityField.setText(model.getCity());
          setupComponent(cityField, editMode, !customerLookup);

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
          setComboBoxModel(model.getStateNames(), stateField, model.getStateIndex());
          setupComponent(stateField, editMode, !customerLookup);
          stateField.setFocusable(editMode);
          stateField.setEnabled(editMode);
          if ( shippingToCustomer )
          {    
              stateField.setRequired(true);
          }
          
          postalCodeField.setText(model.getPostalCode());
          postalCodeField.setEditable(editMode);

          /*extPostalCodeField.setText(model.getExtPostalCode());
          extPostalCodeField.setEditable(editMode);*/

          // update the phone        
          int index = model.getTelephoneIntType();
          if (index < 0)
          {
              index = 0;
          }
          // update the phone type list
          
          setComboBoxModel(model.getPhoneTypes(), phoneTypeField, index);
          setupComponent(phoneTypeField, editMode, !businessCustomer && !(model.isMailBankCheck()));
     //   MAX Rev 1.0 Change : Start 
          phoneTypeLabel.setVisible(false);
          phoneTypeField.setVisible(false);
     //   MAX Rev 1.0 Change : end
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
          
          if("IN".equals(model.getCountry().toString()))
          {
        	  telephoneFieldNa.setVisible(false);
        	  telephoneLabelNa.setVisible(false);
          telephoneField.setValue(model.getTelephoneNumber(index));            
          telephoneField.setEditable(editMode);
          setupComponent(telephoneField, editMode, true); // added - brian j.
          }
          else
          {
        	  telephoneField.setVisible(false);
        	  telephoneLabel.setVisible(false);
        	  telephoneFieldNa.setPhoneNumber(model.getTelephoneNumber(index));            
              telephoneFieldNa.setEditable(editMode);
              setupComponent(telephoneFieldNa, editMode, true); // added - brian j.
          }
         
          if (!businessCustomer)
          {
              phoneTypeField.setSelectedIndex(index);
          }

          emailField.setText(model.getEmail());
          setupComponent(emailField, editMode, !customerLookup && !(model instanceof MailBankCheckInfoBeanModel));

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
              reasonCodeField.setModel(new DefaultComboBoxModel(model.getReasonCodes()));
              if (model.isSelected())
              {
                  reasonCodeField.setSelectedItem(model.getSelectedReason());
              }
          }
          setupComponent(reasonCodeField, editMode, isBusinessCustomerUpdate || model.isMailBankCheck());
          reasonCodeField.setFocusable(editMode);
          reasonCodeField.setEnabled(editMode);
          
                  
          if (model instanceof MailBankCheckInfoBeanModel)
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

      field.setRequestFocusEnabled(isVisible);
      field.setVisible(isVisible);
  }

  //--------------------------------------------------------------------------
  /**
   *  The framework calls this method just before display
   */
  public void activate()
  {
      super.activate();
 //   MAX Rev 1.0 Change : Start 
      phoneTypeLabel.setVisible(false);
      phoneTypeField.setVisible(false);
 //   MAX Rev 1.0 Change : end
      firstNameField.addFocusListener(this);
      customerNameField.addFocusListener(this);
      //customerIDField.addFocusListener(this);
      employeeIDField.addFocusListener(this);
      countryField.addActionListener(new ActionListener()
      {
          public void actionPerformed(ActionEvent e)
          {
              updateStates();
              setPostalFields();
              if(!"India".equalsIgnoreCase(countryField.getSelectedItem().toString()))
              {
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
      String phonenumber=telephoneField.getFieldValue().trim();
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
      telephoneField.setValue(((CustomerInfoBeanModel)beanModel).getTelephoneNumber(indx));
  }

  //---------------------------------------------------------------------------
  /**
   *  Update property fields.
   */
  //---------------------------------------------------------------------------
  protected void updatePropertyFields()
  {
      employeeIDLabel.setText(retrieveText("EmployeeIDLabel", employeeIDLabel));
      customerNameLabel.setText(retrieveText("OrgNameLabel", customerNameLabel));
      firstNameLabel.setText(retrieveText("FirstNameLabel", firstNameLabel));
      lastNameLabel.setText(retrieveText("LastNameLabel", lastNameLabel));
	  discountLabel.setText(retrieveText("DiscountLabelWithColon", discountLabel)); 
      taxCertificateLabel.setText(retrieveText("TaxCertificateLabel", taxCertificateLabel));
      CustomerInfoBeanModel model = (CustomerInfoBeanModel) beanModel;

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
      employeeIDField.setLabel(employeeIDLabel);
      customerNameField.setLabel(customerNameLabel);
      firstNameField.setLabel(firstNameLabel);
      lastNameField.setLabel(lastNameLabel);

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
    //  extPostalCodeField.setLabel(extPostalCodeLabel);
      postalCodeField.setLabel(postalCodeLabel);
      emailField.setLabel(emailLabel);

      // phone properties
      telephoneField.setLabel(telephoneLabel);
      telephoneFieldNa.setLabel(telephoneLabelNa);
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
      setFieldRequired(postalCodeField, countryModel.isPostalCodeRequired(countryField.getSelectedIndex()));
      /* setFieldRequired(extPostalCodeField, false);
      if (countryModel.isExtPostalCodeRequired(countryField.getSelectedIndex()))
      {
          extPostalCodeField.setVisible(true);
      }
      else
      {
          extPostalCodeField.setVisible(false);
          extPostalCodeField.setText("");
      }*/
      String extPostalFormat = countryModel.getCountry(countryField.getSelectedIndex()).getExtPostalCodeFormat();
      String postalFormat = countryModel.getCountry(countryField.getSelectedIndex()).getPostalCodeFormat();
     /* if (extPostalFormat != null)
      {
          extPostalCodeField.setMinLength(extPostalFormat.length());
          extPostalCodeField.setMaxLength(extPostalFormat.length());
      }
      else
      {
          extPostalCodeField.setMinLength(0);
      }*/
      if (postalFormat != null)
      {
          postalCodeField.setMinLength(getMinLength(postalFormat));
          postalCodeField.setMaxLength(postalFormat.length());

      }
      else
      {
          postalCodeField.setMinLength(0);
      }
      telephoneField.setFormat(countryModel.getCountry(countryField.getSelectedIndex()).getPhoneFormat());

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
      String strResult = new String("Class: CustomerAddBean (Revision " + getRevisionNumber() + ") @" + hashCode());
      if (beanModel != null)
      {

          strResult += "\n\nbeanModel = ";
          strResult += beanModel.toString();

      }
      else
      {
          strResult += "\nbeanModel = null\n";
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

/*      if (extPostalCodeField != null)
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
      }*/
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

  //---------------------------------------------------------------------
  /**
   * main entrypoint - starts the part when it is run as an application
   * @param args java.lang.String[]
   */
  //---------------------------------------------------------------------
  public static void main(java.lang.String[] args)
  {
      UIUtilities.setUpTest();

      MAXCustomerAddBean bean = new MAXCustomerAddBean();
      bean.telephoneField.setValue("4043865851");
      System.out.println("1: " + bean.telephoneField.getFieldValue());
      bean.telephoneField.setText("4(512)555-1212");
      System.out.println("2: " + bean.telephoneField.getFieldValue());
      bean.telephoneField.setValue("(512)555-1212");
      System.out.println("3: " + bean.telephoneField.getFieldValue());

      UIUtilities.doBeanTest(bean);
  }
}
