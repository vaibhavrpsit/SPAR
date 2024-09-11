/* ===========================================================================
* Copyright (c) 2008, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CheckEntryBean.java /main/17 2014/03/10 08:55:18 mjwallac Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mjwallac  03/07/14 - Fortify privacy violation - remove test code
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    acadar    04/22/09 - translate date/time labels
 *    abondala  11/03/08 - updated files related to the Patriotic customer ID
 *                         types reason code
 *
 * ===========================================================================

    $Log:
     8    I18N_P2    1.5.1.1     1/4/2008 5:00:24 PM    Maisa De Camargo CR
          29826 - Setting the size of the combo boxes. This change was
          necessary because the width of the combo boxes used to grow
          according to the length of the longest content. By setting the size,
           we allow the width of the combo box to be set independently from
          the width of the dropdown menu.
     7    I18N_P2    1.5.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
          alphanumerice fields for I18N purpose
     6    360Commerce 1.5         10/10/2007 1:02:52 PM  Anda D. Cadar
          changes to not allow double byte chars
     5    360Commerce 1.4         10/8/2007 11:36:46 AM  Anda D. Cadar   UI
          changes to not allow double bytes chars in some cases
     4    360Commerce 1.3         1/25/2006 4:10:52 PM   Brett J. Larsen merge
          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
     3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse
     2    360Commerce 1.1         3/10/2005 10:20:07 AM  Robert Pearse
     1    360Commerce 1.0         2/11/2005 12:09:54 PM  Robert Pearse
    $:
     4    .v700     1.2.1.0     11/17/2005 16:39:27    Jason L. DeLeau 4345:
          Replace any uses of Gateway.log() with the log4j.
     3    360Commerce1.2         3/31/2005 15:27:24     Robert Pearse
     2    360Commerce1.1         3/10/2005 10:20:07     Robert Pearse
     1    360Commerce1.0         2/11/2005 12:09:54     Robert Pearse
    $
    Revision 1.4  2004/03/16 17:15:22  build
    Forcing head revision

    Revision 1.3  2004/03/16 17:15:16  build
    Forcing head revision

    Revision 1.2  2004/02/11 20:56:27  rhafernik
    @scr 0 Log4J conversion and code cleanup

    Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
    updating to pvcs 360store-current


 *
 *    Rev 1.1   Sep 10 2003 15:20:52   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:09:40   CSchellenger
 * Initial revision.
 *
 *    Rev 1.17   Aug 08 2003 16:49:12   baa
 * change listeners for the state/country fields
 * Resolution for 3318: Bad Listener allows state/country mismatch on Check Entry
 *
 *    Rev 1.16   Jul 24 2003 15:07:38   DCobb
 * Update the DOB in the model only if the field is not empty.
 * Resolution for POS SCR-3186: Tendering with Check- Opening Printer cover fills DOB field with today's date.
 *
 *    Rev 1.15   Jul 23 2003 17:53:10   sfl
 * Refined the condition checking.
 * Resolution for POS SCR-3186: Tendering with Check- Opening Printer cover fills DOB field with today's date.
 *
 *    Rev 1.14   Jul 23 2003 17:47:58   sfl
 * Clean the DOB data field after invalid DOB value is detected
 * so that the data re-entry will have a fresh clean field.
 * Resolution for POS SCR-3186: Tendering with Check- Opening Printer cover fills DOB field with today's date.
 *
 *    Rev 1.13   20 Jul 2003 00:41:56   baa
 * increase the length of the driver number field
 *
 *    Rev 1.12   Apr 24 2003 14:46:04   bwf
 * idNum is now a DriversLicenseTextField instead of an AlphaNumericTextField.
 * Resolution for 2208: Space and Asterisk chars are not allowed in a driver's license ID number
 *
 *    Rev 1.11   Apr 16 2003 12:23:10   baa
 * defect fixes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.10   Apr 10 2003 11:43:28   bwf
 * Add UIUtilities instead of UtilityManagerIfc.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.9   Apr 09 2003 17:49:38   bwf
 * Internationalization Clean Up - remove UtilityManager from Beans
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.8   Mar 21 2003 10:58:46   baa
 * Refactor mailbankcheck customer screen, second wave
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.7   Feb 20 2003 15:02:22   HDyer
 * Fixed typos.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.6   Feb 14 2003 17:30:48   HDyer
 * Use UtilityManager to get I18n localized strings for the check id type list.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.5   Feb 04 2003 16:34:10   HDyer
 * Display localized strings instead of string tags in the ID type pulldown.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.4   Jan 29 2003 13:48:06   baa
 * merge changes for micr bad read error
 * Resolution for POS SCR-1846: Unsuccessful Check Scan message appears when slip printer cover is opened at bank information screen during check tender process
 *
 *    Rev 1.3   Sep 18 2002 17:15:28   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 03 2002 16:04:58   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:34:08   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:54:34   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:33:26   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:53:38   msg
 * Initial revision.
 *
 *    Rev 1.9   07 Mar 2002 18:06:34   cir
 * Changed updateModel
 * Resolution for POS SCR-625: Returning from Invalid Data Notice puts a default in DOB field on Check Entry
 *
 *    Rev 1.8   06 Mar 2002 18:19:22   baa
 * fix reseting of required fields
 * Resolution for POS SCR-1488: ID State field always defaults to TX on Check Entry
 *
 *    Rev 1.6   Mar 01 2002 10:02:54   mpm
 * Internationalization of tender-related screens
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.MICRModel;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Contains the visual presentation for Check Entry Information
 * 
 * @version $Revision: /main/17 $
 */
public class CheckEntryBean extends ValidatingBean implements DocumentListener
{
    private static final long serialVersionUID = 3478319116865904578L;
    // Logger
    protected static final Logger logger = Logger.getLogger(CheckEntryBean.class);

    // Revision number
    public static final String revisionNumber = "$Revision: /main/17 $";

    // Constant field indices
    protected static final int ABA_NUMBER    = 0;
    protected static final int ACCT_NUMBER   = ABA_NUMBER + 1;
    protected static final int CHECK_NUMBER  = ACCT_NUMBER + 1;
    protected static final int DIVIDER       = CHECK_NUMBER + 1; //add one for placeholder
    protected static final int ID_NUMBER     = DIVIDER + 1;
    protected static final int ID_TYPE       = ID_NUMBER + 1;
    protected static final int ID_STATE      = ID_TYPE + 1;
    protected static final int ID_COUNTRY    = ID_STATE + 1;
    protected static final int DATE_OF_BIRTH = ID_COUNTRY + 1;
    protected static final int MAX_FIELDS    = DATE_OF_BIRTH + 1; //add one because of 0 index!

    // Top panel label text aray
    // @deprecated as of release 6.0 replaced by labelTags
    protected static String labelText[] =
    {
        "ABA Number:",
        "Acct. Number:",
        "Check Number:", "",
        "ID Number:",
        "ID Type:",
        "ID State:",
        "Country:",
        "DOB ({0}):"
    };

    // Top panel label text aray
    protected static String labelTags[] =
    {
        "ABANumberLabel",
        "AccountNumberLabel",
        "CheckNumber", "",
        "IDNumberLabel",
        "IDTypeLabel",
        "IDStateLabel",
        "IDCountryLabel",
        "DateOfBirthLabel"
    };

    //  label array
    protected JLabel[] fieldLabels = null;

    /** ABA number field */
    protected NumericTextField abaNumberField = null;
    /** Account number field */
    protected AlphaNumericTextField accountNumberField = null;
    /** Check number field */
    protected NumericTextField checkNumberField = null;
    /** ID number field */
    protected DriversLicenseTextField idNumberField = null;
    /** ID type list */
    protected ValidatingComboBox idTypeField = null;
    /** State list */
    protected ValidatingComboBox idStateField = null;
    /** Country list */
    protected ValidatingComboBox idCountryField = null;
    /** Date of birth field */
    protected EYSDateField dobField = null;

    /**
     * Default Constructor.
     */
    public CheckEntryBean()
    {
        initialize();
    }

    /**
     * Configures the class.
     */
    public void initialize()
    {
        setName("CheckEntryBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * Initialize this bean's components.
     */
    protected void initComponents()
    {
        initLabels();
        initFields();
    }

    /**
     * Initialize the setting for the data fields.
     */
    protected void initFields()
    {
        abaNumberField      = uiFactory.createNumericField("abaNumberField", "1", "9");
        accountNumberField  = uiFactory.createAlphaNumericField("accountNumberField", "1", "17", false);

        checkNumberField = uiFactory.createNumericField("checkNumberField", "1", "8");

        idNumberField   = uiFactory.createDriversLicenseField("idNumberField", "1", "25", false);
        idTypeField     = uiFactory.createValidatingComboBox("idTypeField", "false", "15");
        idStateField    = uiFactory.createValidatingComboBox("idStateField", "false", "20");
        idCountryField  = uiFactory.createValidatingComboBox("idCountryField", "false", "15");
        dobField        = uiFactory.createEYSDateField("dobField");
    }

    /**
     * Initializes the setting for the field labels.
     */
    protected void initLabels()
    {

        fieldLabels = new JLabel[MAX_FIELDS];

        for(int i = 0; i < MAX_FIELDS; i++)
        {
            if(i == DIVIDER)
            {
                fieldLabels[i] = uiFactory.createDivider();
            }
            else
            {
                 fieldLabels[i] = uiFactory.createLabel(labelTags[i], labelTags[i], null, UI_LABEL);
            }
        }
    }

    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        // create component array with null placeholder for divider
        JComponent[] comps =
        {
            abaNumberField, accountNumberField, checkNumberField,
            null, idNumberField, idTypeField, idStateField, idCountryField, dobField
        };
        UIUtilities.layoutDataPanel(this, fieldLabels, comps);
   }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();

        abaNumberField.getDocument().addDocumentListener(this);
        abaNumberField.addFocusListener(this);
        accountNumberField.getDocument().addDocumentListener(this);
        checkNumberField.getDocument().addDocumentListener(this);

        idNumberField.getDocument().addDocumentListener(this);
        idNumberField.addFocusListener(this);
        dobField.getDocument().addDocumentListener(this);

        idCountryField.addActionListener(this);

        updateBean();
    }

    /**
     * deactivate any settings made by this bean to external entities
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        abaNumberField.getDocument().removeDocumentListener(this);
        abaNumberField.removeFocusListener(this);
        accountNumberField.getDocument().removeDocumentListener(this);
        checkNumberField.getDocument().removeDocumentListener(this);

        idNumberField.getDocument().removeDocumentListener(this);
        idNumberField.removeFocusListener(this);
        dobField.getDocument().removeDocumentListener(this);

        idCountryField.removeActionListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == idCountryField)
        {
            updateStates();
        }
        else
        {
            super.actionPerformed(event);
        }
    }

    /**
     * Overrides the inherited setVisible().
     * 
     * @param visible
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        // Set the focus
        if (visible && !errorFound)
        {
            if (beanModel != null)
            {
                if (((CheckEntryBeanModel)beanModel).isFocusOnIDNumber())
                {
                    setCurrentFocus(idNumberField);
                }
                else
                {
                    setCurrentFocus(abaNumberField);
                }
            }
        }
    }

    /**
     * Updates the model with the current settings of this bean.
     */
    @Override
    public void updateModel()
    {
       if (beanModel instanceof CheckEntryBeanModel)
       {
           CheckEntryBeanModel model = (CheckEntryBeanModel) beanModel;

            model.setABANumber(abaNumberField.getText());
            model.setAccountNumber(accountNumberField.getText());
            model.setCheckNumber(checkNumberField.getText());
            model.setIDNumber(idNumberField.getText());
            if (idStateField.getSelectedIndex() >= 0)
            {
              model.setStateIndex(idStateField.getSelectedIndex());
            }
            if (idCountryField.getSelectedIndex() >= 0)
            {
               model.setCountryIndex(idCountryField.getSelectedIndex());
            }
            else
            {
               model.setCountryIndex(0);
            }

            model.setDOBValid(!dobField.getText().equals(""));
            model.setSelectedIDType(idTypeField.getSelectedIndex());

            String text = dobField.getText();
            if (!Util.isEmpty(text))
            {
                EYSDate date = dobField.getEYSDate();

                if (date != null)
                {
                    model.setDOB(date);
                }
            }
        }
     }

    /**
     * Update the bean if the model has changed
     */
    @Override
    protected void updateBean()
    {
       if (beanModel instanceof CheckEntryBeanModel)
       {
            CheckEntryBeanModel model = (CheckEntryBeanModel) beanModel;
            abaNumberField.setText(model.getABANumber());
            accountNumberField.setText(model.getAccountNumber());
            checkNumberField.setText(model.getCheckNumber());
            idNumberField.setText(model.getIDNumber());

            //Retrieve countries and update combo box
            String[] countryList =model.getCountryNames();
            ValidatingComboBoxModel countryModel = new ValidatingComboBoxModel(countryList);

            idCountryField.setModel(countryModel);
            idCountryField.setSelectedIndex(model.getCountryIndex());

            // update the state combo box with the new list of states
            String[] stateList = model.getStateNames();
            ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

            idStateField.setModel(stateModel);
            idStateField.setSelectedIndex(model.getStateIndex());
            if (model.isDOBValid() && model.getDOB() != null)
            {
                if (model.getDOB().isValid() &&
                    !model.getDOB().after(DomainGateway.getFactory().getEYSDateInstance()))
                {
                    dobField.setDate(model.getDOB());
                }
                else
                {
                    dobField.setText("");
                }
            }
            else
            {
                dobField.setText("");
            }

            // Update the combo box with the ID Type localized strings
            Vector idTypes = model.getIDTypes();

            if (idTypes != null)
            {
                ValidatingComboBoxModel listModel =
                        new ValidatingComboBoxModel(idTypes);
                idTypeField.setModel(listModel);
            }

            int idIndex = 0;
            if (model.getSelectedIDType() > -1)
            {
               idIndex = model.getSelectedIDType();
            }
            idTypeField.setSelectedIndex(idIndex);
            setRequiredFields();
        }
    }

    /**
     * Sets the MICR data
     * 
     * @param model DeviceModelIfc
     */
    public void setMICRData(DeviceModelIfc model)
    {
        if (logger.isInfoEnabled()) logger.info( "Received MICR data: " + model);

        CheckEntryBeanModel chkModel = (CheckEntryBeanModel)beanModel;
        if (model instanceof MICRModel && !chkModel.isPrinterIgnored())
        {
            MICRModel micrModel = (MICRModel) model;

            chkModel.setMICRData(micrModel);
            chkModel.setCheckMICRed(true);

            // Mail the letter for an implied 'Enter' if still receiving printer input
            UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);
        }
    }

    /**
     * Update states as country selection changes
     */
    public void updateStates()
    {
        int countryIndx = idCountryField.getSelectedIndex();
        if ( countryIndx == -1)
        {
             countryIndx = 0;
        }
        ((CheckEntryBeanModel)beanModel).setCountryIndex(countryIndx);
        String[] stateList = ((CheckEntryBeanModel)beanModel).getStateNames();

        // update the state combo box with the new list of states
        ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

        idStateField.setModel(stateModel);
        idStateField.setSelectedIndex(0);  // select 1st element of the list
    }

    /**
     * Calls {@link #setRequiredFields()} on document changes.
     * 
     * @see javax.swing.event.DocumentListener#changedUpdate(javax.swing.event.DocumentEvent)
     */
    public void changedUpdate(DocumentEvent e)
    {
        setRequiredFields();
    }

    /**
     * Calls {@link #setRequiredFields()} on document changes.
     * 
     * @see javax.swing.event.DocumentListener#insertUpdate(javax.swing.event.DocumentEvent)
     */
    public void insertUpdate(DocumentEvent e)
    {
        setRequiredFields();
    }

    /**
     * Calls {@link #setRequiredFields()} on document changes.
     * 
     * @see javax.swing.event.DocumentListener#removeUpdate(javax.swing.event.DocumentEvent)
     */
    public void removeUpdate(DocumentEvent e)
    {
        setRequiredFields();
    }

    /**
     * Determine what fields should be required. This is not always the same for
     * Check Entry. If the user only enters something in the top panel, then all
     * fields are required in the top panel, and vice versa.
     */
    protected void setRequiredFields()
    {
        try
        {
            String abaNumberValue=abaNumberField.getText().trim();
            String accountNumberValue = accountNumberField.getText().trim();
            String checkNumberValue = checkNumberField.getText().trim();
            String idNumberValue = idNumberField.getText().trim();
            String dobValue = dobField.getText().trim();

            // check if upper panel fields have data and  make it required
            if (( (!abaNumberValue.equals("") || !accountNumberValue.equals("") || !checkNumberValue.equals("")) &&
                  (idNumberValue.equals("") || dobField.equals("")))  ||
                (!abaNumberValue.equals("") && !accountNumberValue.equals("") && !checkNumberValue.equals("") ))
            {
                  setFieldRequired(abaNumberField,true);
                  setFieldRequired(accountNumberField,true);
                  setFieldRequired(checkNumberField,true);
                  setFieldRequired(idNumberField,false);
                  setFieldRequired(dobField,false);

            }
            // check if lower panel fields have data and  make it required
            if (((!idNumberValue.equals("") || !dobValue.equals(""))  &&
                 (abaNumberValue.equals("") || accountNumberValue.equals("") || checkNumberValue.equals(""))) ||
                (!idNumberValue.equals("") && !dobField.equals("")))
            {
                  setFieldRequired(abaNumberField,false);
                  setFieldRequired(accountNumberField,false);
                  setFieldRequired(checkNumberField,false);
                  setFieldRequired(idNumberField,true);
                  setFieldRequired(dobField,true);

            }
            // reset required fields if no data has been entered.
            if (idNumberValue.equals("") && dobValue.equals("") && abaNumberValue.equals("") &&
                accountNumberValue.equals("") && checkNumberValue.equals(""))
            {
                  if (((CheckEntryBeanModel)beanModel).isFocusOnIDNumber())
                  {
                    setFieldRequired(idNumberField,true);
                    setFieldRequired(dobField,true);
                    setFieldRequired(abaNumberField,false);
                    setFieldRequired(accountNumberField,false);
                    setFieldRequired(checkNumberField,false);
                  }
                  else
                  {
                    setFieldRequired(idNumberField,false);
                    setFieldRequired(dobField,false);
                    setFieldRequired(abaNumberField,true);
                    setFieldRequired(accountNumberField,true);
                    setFieldRequired(checkNumberField,true);
                  }
            }
        }
        catch (Exception exc)
        {
          logger.warn("No ABA number.");
        }
    }

    /**
     *  Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            if (i != DIVIDER)
            {
                fieldLabels[i].setText(retrieveText(labelTags[i]));
            }
        }
        String translatedLabel = getTranslatedDatePattern();
        String dateLabel = fieldLabels[DATE_OF_BIRTH].getText();
        fieldLabels[DATE_OF_BIRTH].setText(LocaleUtilities.formatComplexMessage(dateLabel,translatedLabel));

        // set field labels
        abaNumberField.setLabel(fieldLabels[ABA_NUMBER]);
        accountNumberField.setLabel(fieldLabels[ACCT_NUMBER]);
        checkNumberField.setLabel(fieldLabels[CHECK_NUMBER]);
        idNumberField.setLabel(fieldLabels[ID_NUMBER]);
        dobField.setLabel(fieldLabels[DATE_OF_BIRTH]);

    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    @Override
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) +
                "(Revision " + getRevisionNumber()
                + ") @" + hashCode());
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

}
