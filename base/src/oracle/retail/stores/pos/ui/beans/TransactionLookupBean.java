/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TransactionLookupBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/22/09 - translate date/time labels
 *    acadar    02/09/09 - use default locale for display of date and time
 *    asinton   11/19/08 - Changed calls from getScanData to getScanLabelData.
 *    sgu       11/18/08 - fix error log based on review comments
 *    sgu       11/18/08 - fix the scan of transaction scan
 *
 * ===========================================================================
 * $Log:
 *   6    360Commerce 1.5         4/2/2008 11:38:47 AM   Sameer Thajudin This
 *        is revision 6 which is same as revision 4.
 *
 *        The changes made in revision 5 as per the change request 31020 was
 *        reviewed by Alan Sinton. After going through the change request, it
 *        was determined that the code in revision 4 confirmed to the
 *        requirements.
 *
 *        Implementing the change request 31020 would result in an
 *        implementation whose functionality deviated from what was mentioned
 *        in the requirements.
 *
 *        So the change request 31020 is rejected.
 *
 *
 *
 *   5    360Commerce 1.4         4/1/2008 7:29:18 PM    Sameer Thajudin By
 *        default the TransactionLookupBeanModel has its 'fieldStartDate' and
 *        'fieldEndDate variable set to the current date.
 *
 *        Setting the fieldStartDate variable to null is the fix.
 *   5    I18N_P2    1.3.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *        alphanumerice fields for I18N purpose
 *   4    360Commerce 1.3         5/16/2007 3:36:18 PM   Mathews Kochummen use
 *        locale's short date format
 *   3    360Commerce 1.2         3/31/2005 4:30:35 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:26:23 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:15:15 PM  Robert Pearse
 *
 *  Revision 1.9.2.1  2004/10/29 20:57:39  jdeleau
 *  @scr 7558 Use System date instead of business date if no date is
 *  specified for start/end dates in the ejournal.
 *
 *  Revision 1.9  2004/07/24 16:41:23  kll
 *  @scr 5825: check length of date fields
 *
 *  Revision 1.8  2004/07/17 19:21:23  jdeleau
 *  @scr 5624 Make sure errors are focused on the beans, if an error is found
 *  during validation.
 *
 *  Revision 1.7  2004/07/15 22:10:54  jdeleau
 *  @scr 6077 Remove required field characteristics from start date
 *  and end date.
 *
 *  Revision 1.6  2004/07/09 20:59:24  jdeleau
 *  @scr 6077 If the dates on the EJ screen are both blank dont throw an
 *  error message, instead use the  business date
 *
 *  Revision 1.5  2004/03/16 17:15:18  build
 *  Forcing head revision
 *
 *  Revision 1.4  2004/03/05 09:50:22  rzurga
 *  @scr 3898 Add additional space for the longer transaction ID barcode that includes the date
 *
 *  Revision 1.3  2004/02/18 21:12:16  tfritz
 *  @scr 3632 - EJournal search gives an error if only a start date or an end date is entered
 *  @scr 3635
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Dec 17 2003 09:30:26   rrn
 * Added Register Number field.
 * layoutComponents( ) - Rearranged order of display to pair Start Date with Start Time and End Date with End Time.
 * updateModel( ) - made sure date fields *can* be blank.
 * updateBean( ) - if register number field is not allowed as a search variable, make
 * it uneditable and with a gray background.
 * Resolution for 3611: EJournal to database
 *
 *    Rev 1.1   Sep 16 2003 17:53:34   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:12:56   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Jul 02 2003 11:28:12   bwf
 * Format date with HH:mm to avoid problems with ejournal.
 * Resolution for 2232: E. Journal Searches unable to search by Time if time is after 12 noon.
 *
 *    Rev 1.3   Apr 16 2003 12:23:12   baa
 * defect fixes
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.2   Aug 16 2002 17:12:34   baa
 * remove close drawer message for training mode
 * Resolution for POS SCR-1750: Training Mode Close Cash Drawer Message
 *
 *    Rev 1.1   Aug 07 2002 19:34:28   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:56:08   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:36:40   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:52:40   msg
 * Initial revision.
 *
 *    Rev 1.4   Mar 09 2002 10:46:12   mpm
 * More text externalization.
 *
 *    Rev 1.3   Feb 27 2002 16:30:22   dfh
 * removed performclearaction, cleanup
 * Resolution for POS SCR-1349: Clear enabled but does not work on Find Transaction
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.Color;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.text.DateFormat;
import java.util.Iterator;
import java.util.Locale;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionID;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.device.DeviceException;
import oracle.retail.stores.foundation.manager.device.ScannerModel;
import oracle.retail.stores.foundation.manager.device.ScannerSession;
import oracle.retail.stores.foundation.manager.ifc.DeviceTechnicianIfc;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.foundation.tour.gate.TechnicianNotFoundException;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
    This class displays the find transaction screen.
    It is used with the TransactionLookupBeanModel class. <P>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
    @see oracle.retail.stores.pos.ui.beans.TransactionLookupBeanModel
 **/
//---------------------------------------------------------------------
public class TransactionLookupBean extends ValidatingBean
{
    /** revision number for this class */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** label text for top panel
     * @deprecated as of release 6.0 obsolete */
    public static final String[] topText =
    {
        "Start Transaction:", "End Transaction:"
    };

    /** label text for bottom panel
     * @deprecated as of release 6.0 obsolete*/
    public static final String[] bottomText =
    {
        "Start Date:", "Start Time:", "Cashier ID:",
        "Sales Associate ID:", "End Date:", "End Time:"
    };

    /** label text tags for top panel
     * @deprecated as of release 6.0 replace obsolete */
    public static final String[] topTags =
    {
        "StartTransactionFieldLabel",
        "EndTransactionFieldLabel"
    };

    /** label text tags for bottom panel
     * @deprecated as of release 6.0 replace obsolete */
    public static final String[] bottomTags =
    {
        "StartDateFieldLabel",
        "StartTimeFieldLabel",
        "CashierIDFieldLabel",
        "SalesAssociateIDFieldLabel",
        "EndDateFieldLabel",
        "EndTimeFieldLabel"
    };


    /** start transaction id field **/
    protected NumericTextField startTransactionIDField;

    /** end transaction id field **/
    protected NumericTextField endTransactionIDField;

    /** start date field **/
    protected EYSDateField startDateField;

    /** end date field **/
    protected EYSDateField endDateField;

    /** start time field **/
    protected EYSTimeField startTimeField;

    /** end time field **/
    protected EYSTimeField endTimeField;

    /** register number field **/
    protected NumericTextField registerNumberField;

    /** cashier id field **/
    protected AlphaNumericTextField cashierIDField;

    /** sales associated id field **/
    protected AlphaNumericTextField salesAssociateIDField;

    /** array of top panel labels
     * @deprecated as of release 6.0 */
    protected JLabel[] topLabels;

    /** array of bottom panel labels
     * @deprecated as of release 6.0 */
    protected JLabel[] bottomLabels;

    /** start transaction label **/
    protected JLabel startTransactionLabel;

    /** end transaction label **/
    protected JLabel endTransactionLabel;

    /** start date label **/
    protected JLabel startDateLabel;

    /** start time label **/
    protected JLabel startTimeLabel;

    /** register number label **/
    protected JLabel registerNumberLabel;

    /** cashier label **/
    protected JLabel cashierIDLabel;

    /** sale associate label **/
    protected JLabel salesAssociateIDLabel;

    /** end date label **/
    protected JLabel endDateLabel;

     /** end time label **/
    protected JLabel endTimeLabel;

    /** top panel for the splitPane
     * @deprecated as of release 5.0.0 no longer used **/
    protected JPanel panel1;

    /** bottom panel for the splitPane
     * @deprecated as of release 5.0.0 no longer used  **/
    protected JPanel panel2;

    /** split pane
     * @deprecated as of release 5.0 no longer used **/
    protected JSplitPane splitPane;

    /** current focus field **/
    protected String focusField = "";

    //---------------------------------------------------------------------
    /**
       Default class Constructor and initializes its components.
    **/
    //---------------------------------------------------------------------
    public TransactionLookupBean()
    {
        super();
        setName("TransactionLookupBean");
    }
    //---------------------------------------------------------------------
    /**
       Configures the class and its screen members.
    **/
    //---------------------------------------------------------------------
    public void configure()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new GridBagLayout());

        initTopPanel();
        initBottomPanel();
        layoutComponents();
    }

    //---------------------------------------------------------------------
    /**
       Initialize and define all labels for the top panel
    **/
    //---------------------------------------------------------------------
    protected void initTopPanel()
    {

        //Init labels
        startTransactionLabel = uiFactory.createLabel("startTransactionLabel", null, UI_LABEL);
        endTransactionLabel = uiFactory.createLabel("endTransactionLabel", null, UI_LABEL);

        // Init fields
        startTransactionIDField =  uiFactory.createNumericField("StartTransactionIDField", "1", new Integer(TransactionID.getTransactionIDLength()).toString());
        endTransactionIDField   =  uiFactory.createNumericField("EndTransactionIDField", "1", new Integer(TransactionID.getTransactionIDLength()).toString());

    }

    //-------------------------------------------------------------------------
    /**
       Initialize and define the start transaction, end transaction, cashier id,
       and sales associate id fields for this bean.
    **/
    //---------------------------------------------------------------------
    protected void initBottomPanel()
    {
        // create bottom labels
        startDateLabel = uiFactory.createLabel("startDateLabel", null,UI_LABEL);
        startTimeLabel = uiFactory.createLabel("startTimeLabel", null,UI_LABEL);

        registerNumberLabel = uiFactory.createLabel("registerNumberLabel", null, UI_LABEL);

        cashierIDLabel = uiFactory.createLabel("cashierLabel", null,UI_LABEL);
        salesAssociateIDLabel = uiFactory.createLabel("salesAssociateIDLabel", null,UI_LABEL);
        endDateLabel   = uiFactory.createLabel("endDateLabel", null,UI_LABEL);
        endTimeLabel   = uiFactory.createLabel("endTimeLabel", null,UI_LABEL);

        // create bottom fields
        startDateField = uiFactory.createEYSDateField("StartDateField");
        startTimeField = uiFactory.createEYSTimeField("StartTimeField");

        registerNumberField = uiFactory.createNumericField("RegisterNumberField", "1", "3");

        cashierIDField =  uiFactory.createAlphaNumericField("CashierIDField", "1", "10", false);
        salesAssociateIDField = uiFactory.createAlphaNumericField("SalesAssociateIDField", "1", "10", false);
        endDateField = uiFactory.createEYSDateField("EndDateField");
        endTimeField = uiFactory.createEYSTimeField("EndTimeField");

    }

   //---------------------------------------------------------------------
    /**
       Initialize and define all labels for the top panel
    **/
    //---------------------------------------------------------------------
    protected void layoutComponents()
    {
        JLabel divider = uiFactory.createDivider();
        JLabel[] labels = new JLabel[]
        {
           startTransactionLabel,
           endTransactionLabel,
           divider,
           startDateLabel,
           startTimeLabel,
           endDateLabel,
           endTimeLabel,
           registerNumberLabel,
           cashierIDLabel,
           salesAssociateIDLabel
        };

        JComponent[] components = new JComponent[]
        {
           startTransactionIDField,
           endTransactionIDField, null,
           startDateField,
           startTimeField,
           endDateField,
           endTimeField,
           registerNumberField,
           cashierIDField,
           salesAssociateIDField
        };

        setLayout(new GridBagLayout());

        UIUtilities.layoutDataPanel(this,labels,components);
    }
    //---------------------------------------------------------------------
    /**
       Set the focus field name
       @param  String  focus field name
     **/
    //---------------------------------------------------------------------
    protected void setFocusField(String value)
    {
        focusField = "";
        if (value != null)
        {
          focusField = value;
        }
    }

    //---------------------------------------------------------------------
    /**
       Updates the model with the newest values from the bean
    **/
    //---------------------------------------------------------------------
    public void updateModel()
    {

        if(beanModel instanceof TransactionLookupBeanModel)
        {
            TransactionLookupBeanModel model = (TransactionLookupBeanModel)beanModel;

            model.setStartTransaction(startTransactionIDField.getText());
            model.setEndTransaction(endTransactionIDField.getText());


            if(!Util.isEmpty(startDateField.getText()))
            {
                model.setStartDate(startDateField.getEYSDate());
            }
            else
            {
                if(model.getStartDate() != null)
                {
                    model.setStartDate(model.getStartDate());
                }
                else
                {
                    EYSDate eysDate = DomainGateway.getFactory().getEYSDateInstance();
                    eysDate.setMonth(0);
                    model.setStartDate(eysDate);
                }
            }

            if(!Util.isEmpty(endDateField.getText()))
            {
                model.setEndDate(endDateField.getEYSDate());
            }
            else
            {
                if(model.getEndDate() != null)
                {
                    model.setEndDate(model.getEndDate());
                }
                else
                {
                    EYSDate eysDate = DomainGateway.getFactory().getEYSDateInstance();
                    eysDate.setMonth(0);
                    model.setEndDate(eysDate);
                }
            }

            if(!Util.isEmpty(startTimeField.getText()) && startTimeField.getText().length() == 5)
            {
                model.setStartTime(startTimeField.getEYSDate());
            }
            else
            {
                EYSDate eysDate = startTimeField.getEYSDate();
                eysDate.setMonth(0);
                model.setStartTime(eysDate);
            }

            if(!Util.isEmpty(endTimeField.getText()) && endTimeField.getText().length() == 5)
            {
                model.setEndTime(endTimeField.getEYSDate());
            }
            else
            {
                EYSDate eysDate = endTimeField.getEYSDate();
                eysDate.setMonth(0);
                model.setEndTime(eysDate);
            }

            model.setRegisterNumber(registerNumberField.getText());

            model.setCashierID(cashierIDField.getText());
            model.setSalesAssociateID(salesAssociateIDField.getText());
            model.setFocusField(focusField);

        }

    }

    //---------------------------------------------------------------------
    /**
       Update the bean with fresh data.
    **/
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if(beanModel instanceof TransactionLookupBeanModel)
        {
            TransactionLookupBeanModel model = (TransactionLookupBeanModel)beanModel;

            startTransactionIDField.setText(model.getStartTransaction());
            endTransactionIDField.setText(model.getEndTransaction());
            Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
            if (model.getStartDate().getMonth() > 0)
            {
                EYSDate date = model.getStartDate();

                if (date.isValid())
                {
                    DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
                    String sDate = dateTimeService.formatDate(date.dateValue(), defaultLocale, DateFormat.SHORT);
                    startDateField.setText(sDate);
                }
                else
                {
                    startDateField.setText("");
                }

                date = model.getEndDate();
                if (date.isValid())
                {
                    DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
                    String eDate = dateTimeService.formatDate(date.dateValue(), defaultLocale, DateFormat.SHORT);
                    endDateField.setText(eDate);
                }
                else
                {
                    endDateField.setText("");
                }
            }
            else
            {
                startDateField.setText("");
                endDateField.setText("");
            }

            if (model.getStartTime().getHour() > 0 &&
                model.getStartTime().getHour() < 24)
            {
                EYSDate time = model.getStartTime();
                startTimeField.setText(time.toFormattedString("HH:mm"));
                time = model.getEndTime();
                endTimeField.setText(time.toFormattedString("HH:mm"));
            }
            else
            {
                startTimeField.setText("");
                endTimeField.setText("");
            }

            registerNumberField.setText(model.getRegisterNumber());

            if( model.getAllowRegisterNumberField() == false )
            {
                registerNumberField.setEnabled(false);
                registerNumberField.setBackground(Color.LIGHT_GRAY);
            }

            cashierIDField.setText(model.getCashierID());
            salesAssociateIDField.setText(model.getSalesAssociateID());
            //setFocusField(model.getFocusField());
        }

    }
    //---------------------------------------------------------------------
    /**
       Receive scanner data. Called by the UI Framework.
       @param data DeviceModelIfc
    **/
    //---------------------------------------------------------------------
    public void setScannerData(DeviceModelIfc data)
    {
        ScannerModel scannerModel = (ScannerModel) data;

        // Strip any leading alpha characters from the JPOS scanner input. UPC codes are always numeric
        int index = 0;
        String temp = new String(scannerModel.getScanLabelData());

        while (Character.isLetter(temp.charAt(index)))
        {
            index++;
        }
        final String numeric = temp.substring(index);

        String startTransactionID = startTransactionIDField.getText();
        String endTransactionID = endTransactionIDField.getText();

        //current behavior - set start and end transaction fields only
        if(startTransactionID.trim().length() == 0)
        {
        	startTransactionIDField.setText(numeric);
        }
        else if(endTransactionID.trim().length() == 0)
        {
        	endTransactionIDField.setText(numeric);
        }

        //re-enable scans
        try
        {
            DeviceTechnicianIfc dt = (DeviceTechnicianIfc)
              Gateway.getDispatcher().getLocalTechnician(DeviceTechnicianIfc.TYPE);

            if ( dt != null)
            {
                try
                {
                    String sessionName = ScannerSession.TYPE;
                    ScannerSession scannerSession = (ScannerSession) dt.getDeviceSession(sessionName);
                    scannerSession.setEnabled(true);
                }
                catch (DeviceException e)
                {
                    logger.error( "setScannerData: deviceException=", e);
                }
            }
        }
        catch (TechnicianNotFoundException e)
        {
            logger.error( "setScannerData: can't get deviceTechnician=", e);
        }
        catch (Exception e)
        {
            logger.error( "setScannerData: can't get deviceTechnician=", e);
        }

        updateModel();
    }
    //---------------------------------------------------------------------
    /**
       Determines and sets the focus field.
    **/
    //---------------------------------------------------------------------
    protected void setFocusField()
    {
        if (focusField.equals(startDateField.getName()))
        {
            setCurrentFocus(startDateField);
        }
        else if (focusField.equals(startTimeField.getName()))
        {
            setCurrentFocus(startTimeField);
        }
        else if (focusField.equals(registerNumberField.getName()))
        {
            setCurrentFocus(registerNumberField);
        }
        else if (focusField.equals(cashierIDField.getName()))
        {
            setCurrentFocus(cashierIDField);
        }
        else if (focusField.equals(salesAssociateIDField.getName()))
        {
            setCurrentFocus(salesAssociateIDField);
        }
        else
        {
            setCurrentFocus(startTransactionIDField);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * activate any settings made by this bean to external entities
     */
    //--------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        startDateField.addFocusListener(this);
        startTimeField.addFocusListener(this);
        registerNumberField.addFocusListener(this);
        cashierIDField.addFocusListener(this);
        salesAssociateIDField.addFocusListener(this);
        startTransactionIDField.addFocusListener(this);

    }

    //--------------------------------------------------------------------------
    /**
     * deactivate any settings made by this bean to external entities
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        startDateField.removeFocusListener(this);
        startTimeField.removeFocusListener(this);
        registerNumberField.removeFocusListener(this);
        cashierIDField.removeFocusListener(this);
        salesAssociateIDField.removeFocusListener(this);
        startTransactionIDField.removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()

        startTransactionLabel.setText(retrieveText("StartTransactionFieldLabel"));
        endTransactionLabel.setText(retrieveText("EndTransactionFieldLabel"));

        startTimeLabel.setText(retrieveText("StartTimeFieldLabel"));
        endTimeLabel.setText(retrieveText("EndTimeFieldLabel"));

        //Retrieve bundle text for labels
        String label    =  retrieveText("StartDateFieldLabel");
        String translatedLabel = getTranslatedDatePattern();
        startDateLabel.setText(LocaleUtilities.formatComplexMessage(label, translatedLabel));

        label    =  retrieveText("EndDateFieldLabel");
        endDateLabel.setText(LocaleUtilities.formatComplexMessage(label, translatedLabel));

        registerNumberLabel.setText(retrieveText("RegisterNumberFieldLabel"));

        cashierIDLabel.setText(retrieveText("CashierIDFieldLabel"));
        salesAssociateIDLabel.setText(retrieveText("SalesAssociateIDFieldLabel"));

        startTransactionIDField.setLabel(startTransactionLabel);
        endTransactionIDField.setLabel(endTransactionLabel);


        startDateField.setLabel(startDateLabel);
        startTimeField.setLabel(startTimeLabel);

        registerNumberField.setLabel(registerNumberLabel);

        cashierIDField.setLabel(cashierIDLabel);
        salesAssociateIDField.setLabel(salesAssociateIDLabel);
        endDateField.setLabel(endDateLabel);
        endTimeField.setLabel(endTimeLabel);

    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
        Determines if all the required fields have non-null, valid
        data; and determines if all the non-null optional fields have
        valid data; if so, it fires a "validated" event, otherwise it
        fires an "invalidated" event.
        @return True if no errors
    **/
    //---------------------------------------------------------------------
    protected boolean validateFields()
    {

        boolean datesLinked = false;
        if(beanModel instanceof TransactionLookupBeanModel)
        {
            TransactionLookupBeanModel model = (TransactionLookupBeanModel)beanModel;
            datesLinked = model.getDatesLinked();
            if(datesLinked)
            {
                if(!requiredFields.contains(startDateField))
                {
                    requiredFields.add(startDateField);
                }
                if(!requiredFields.contains(endDateField))
                {
                    requiredFields.add(endDateField);
                }
            }
        }
        errorFound = false;
        // Clear out the error message array.
        for (int i = 0; i < MAX_ERROR_MESSAGES; i++)
        {
            errorMessage[i] = "";
        }
        getPOSBaseBeanModel().setFieldInErrorName(null);

        // first make sure all the required fields have valid data
        int errorCount = 0;
        int linkedDateCount = 0;
        Iterator requiredEnum = requiredFields.iterator();
        String badPairName = null;
        ValidatingFieldIfc badPairField = null;
        while (requiredEnum.hasNext() && errorCount < MAX_ERROR_MESSAGES)
        {
            ValidatingFieldIfc field = (ValidatingFieldIfc)requiredEnum.next();
            String name = ((Component)field).getName();

            // if input is not valid set error message
            if (!field.isInputValid())
            {
                // If the model is set to use linked dates, then only throw
                // an error if only one of the 2 date fields is invalid.  This if
                // conditon will save the bad date fields, and keep a counter to
                // make sure only one is bad.  If only one is, it is handled after
                // this while loop complets.
                boolean saveError = true;
                if(datesLinked && (field.equals(startDateField) || field.equals(endDateField)))
                {
                    if(((EYSDateField)field).getText().trim().equals(""))
                    {
                        linkedDateCount++;
                        badPairName = name;
                        badPairField = field;
                        saveError = false;
                    }
                }
                if(saveError)
                {
                    if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                    {
                        getPOSBaseBeanModel().setFieldInErrorName(name);
                    }

                    errorMessage[errorCount] = invalidDataMsg(field);
                    errorCount++;
                }
            }
        }
        // They may have been added here temporarily, if so remove them now
        if(datesLinked)
        {
            requiredFields.remove(startDateField);
            requiredFields.remove(endDateField);
        }
        // Only one of the date fields was blank, this really is an error
        // Its ok if both are blank, are none are blank but if only one is
        // blank then we are to throw an error. (SCR 6077)
        if(linkedDateCount == 1)
        {
            if(getPOSBaseBeanModel().getFieldInErrorName() == null)
            {
                getPOSBaseBeanModel().setFieldInErrorName(badPairName);
                errorMessage[errorCount] = invalidDataMsg(badPairField);
                errorCount++;
            }
        }
        else if(linkedDateCount == 2)
        {
            if(beanModel instanceof TransactionLookupBeanModel)
            {
                TransactionLookupBeanModel model = (TransactionLookupBeanModel)beanModel;
                // Changed to current date - scr 7558.
                model.setStartDate(DomainGateway.getFactory().getEYSDateInstance());
                model.setEndDate(DomainGateway.getFactory().getEYSDateInstance());
            }
        }

        // now make sure all non-null optional fields have valid data
        Iterator optionalEnum = optionalFields.iterator();
        while (optionalEnum.hasNext() && errorCount < MAX_ERROR_MESSAGES)
        {
            ValidatingFieldIfc field = (ValidatingFieldIfc)optionalEnum.next();
            String name = ((Component)field).getName();
            if (field instanceof ValidatingTextField)
            {
                ValidatingTextField tField = (ValidatingTextField) field;

                // if a field is non-null and the data is invalid, set error message
                if(!tField.getText().equals("") && !tField.isInputValid())
                {
                    if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                    {
                        getPOSBaseBeanModel().setFieldInErrorName(name);
                    }
                    errorMessage[errorCount] = invalidDataMsg(field);
                    errorCount++;
                }
            }
            else if (field instanceof ValidatingComboBox
                && ((ValidatingComboBox) field).isEditable())
            {
                ValidatingComboBox comboBox = (ValidatingComboBox) field;

                // if a field is non-null and the data is invalid, set error message
                if(!"".equals(comboBox.getEditor().getItem()) && !comboBox.isInputValid())
                {
                    if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                    {
                        getPOSBaseBeanModel().setFieldInErrorName(name);
                    }
                    errorMessage[errorCount] = invalidDataMsg(field);
                    errorCount++;
                }
            }
            else if (field instanceof ValidatingPasswordField)
            {
                ValidatingPasswordField tField = (ValidatingPasswordField) field;

                // if a field is non-null and the data is invalid, set error message
                if((tField.getPassword().length > 0) && !tField.isInputValid())
                {
                    if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                    {
                        getPOSBaseBeanModel().setFieldInErrorName(name);
                    }
                    errorMessage[errorCount] = invalidDataMsg(field);
                    errorCount++;
                }
            }
            else
            {
                // if a field is non-null and the data is invalid set error message
                if(!field.isInputValid())
                {
                    if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                    {
                        getPOSBaseBeanModel().setFieldInErrorName(name);
                    }

                    errorMessage[errorCount] =  invalidDataMsg(field);
                    errorCount++;
                }
            }
        }

        boolean valid = true;
        if (errorCount > 0)
        {
            // There were errors, show the error screen.
            showErrorScreen();
            valid = false;
        }

        return valid;
    }
    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: TransactionLookupBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //--------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //--------------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        javax.swing.JFrame frame = new javax.swing.JFrame();
        frame.setSize(400,300);
        TransactionLookupBean aTransactionLookupBean;
        aTransactionLookupBean = new TransactionLookupBean();
        frame.getContentPane().add("Center", aTransactionLookupBean);
        frame.setVisible(true);
    }

}
