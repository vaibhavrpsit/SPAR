/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EmployeeClockEntryBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:45 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   02/02/10 - prevent setting of list index when there are no
 *                         reason codes
 *    abondala  01/03/10 - update header date
 *    acadar    02/10/09 - use default locale for date/time display
 *    acadar    02/09/09 - use default locale for display of date and time
 *    mdecama   10/20/08 - Refactored Dropdowns to use the new
 *                         CodeListManagerIfc

     $Log:
      4    360Commerce 1.3         3/29/2007 6:29:08 PM   Michael Boyd    CR
           26172 - v8x merge to trunk

           4    .v8x      1.2.1.0     3/12/2007 2:23:11 PM   Maisa De Camargo
           Updated Reason Code to use Default Settings.
      3    360Commerce 1.2         3/31/2005 3:27:56 PM   Robert Pearse
      2    360Commerce 1.1         3/10/2005 10:21:17 AM  Robert Pearse
      1    360Commerce 1.0         2/11/2005 12:10:48 PM  Robert Pearse
     $
     Revision 1.4  2004/07/17 19:21:23  jdeleau
     @scr 5624 Make sure errors are focused on the beans, if an error is found
     during validation.

     Revision 1.3  2004/03/16 17:15:17  build
     Forcing head revision

     Revision 1.2  2004/02/11 20:56:27  rhafernik
     @scr 0 Log4J conversion and code cleanup

     Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
     updating to pvcs 360store-current


 *
 *    Rev 1.1   Sep 16 2003 17:52:30   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:10:24   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   May 08 2003 16:03:32   bwf
 * Do not retrieve locale text because text is already retrieved in LookupLastEntrySite.
 * Resolution for 2326: <> appear on Clock Entry screen
 *
 *    Rev 1.4   Feb 24 2003 11:57:26   HDyer
 * Fixed problem of deprecation warning.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.2   Sep 24 2002 14:10:22   baa
 * i18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   07 May 2002 15:24:24   dfh
 * updates for adding clock entry type -
 * Resolution for POS SCR-1622: Employee Clock In/Out needs type code
 *
 *    Rev 1.0   Apr 29 2002 14:51:16   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:34:26   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:54:20   msg
 * Initial revision.
 *
 *    Rev 1.6   Feb 23 2002 15:04:12   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.5   Feb 17 2002 16:32:42   mpm
 * Removed test flag for internationalization.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 *
 *    Rev 1.4   Feb 12 2002 18:57:24   mpm
 * Added support for text externalization.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   Jan 19 2002 10:30:06   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.2   31 Oct 2001 08:05:44   mpm
 * Cleaned up excessive GridBagConstraints objects.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 *
 *    Rev 1.1   28 Oct 2001 17:53:02   mpm
 * Tweaked beans.
 *
 *    Rev 1.0   28 Oct 2001 13:02:02   mpm
 * Initial revision.
 * Resolution for POS SCR-235: Employee clock-in, clock-out
 */
package oracle.retail.stores.pos.ui.beans;

import java.text.DateFormat;
import java.util.Locale;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean displays the last and current time entries, a list of reason codes,
 * and a list of entry types for employee clock-in and clock-out functions.
 * 
 * @see oracle.retail.stores.pos.ui.beans.EmployeeClockEntryBeanModel
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class EmployeeClockEntryBean extends ValidatingBean
{
    /** Generated SerialVersionUID */
    private static final long serialVersionUID = 8828959870050397786L;
    /** Revision Number */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Label for Reason Code.
     */
    protected JLabel reasonCodeLabel = null;

    /**
     * container for reason list
     **/
    protected ValidatingComboBox clockReasonList = null;

    /**
     * Reference to the bean model being used.
     */
    protected EmployeeClockEntryBeanModel beanModel = null;

    /**
     * Label for the last time entry
     */
    protected JLabel lastEntryLabel = null;

    /**
     * Label for the current time entry
     */
    protected JLabel currentEntryLabel = null;

    /**
     * text for the last date entry
     */
    protected JLabel lastEntryDateText = null;

    /**
     * text for the current date entry
     */
    protected JLabel currentEntryDateText = null;

    /**
     * Label for entry type code.
     */
    protected JLabel typeCodeLabel = null;

    /**
     * container for entry type code list
     **/
    protected ValidatingComboBox typeCodeList = null;

    /**
     * reason codes to populate typeCodeList
     */
    protected Vector reasonCodes = null;

    /**
     *  Constructor
     */
    public EmployeeClockEntryBean()
    {
        super();
    }

    /**
     * Configures the class.
     */
    @Override
    public void configure()
    {
        setName("ClockEntryBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * This updates the local bean model.
     *
     * @see oracle.retail.stores.pos.ui.beans.EmployeeClockEntryBeanModel
     */
    @Override
    public void updateModel()
    {
        beanModel.setSelected(false);
        int selectedIndx = clockReasonList.getSelectedIndex();
        beanModel.setSelectedReasonCode(selectedIndx);
        beanModel.setSelectedTypeCodeIndex(typeCodeList.getSelectedIndex());
    }

    /**
     * This method sets the model of this bean. This bean uses the
     * EmployeeClockEntryBeanModel.
     * 
     * @param model The new EmployeeClockEntryBeanModel to use.
     * @see #beanModel
     * @see oracle.retail.stores.pos.ui.beans.EmployeeClockEntryBeanModel
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set EmployeeClockEntryBean model to null");
        }
        
        if (model instanceof EmployeeClockEntryBeanModel)
        {
            beanModel = (EmployeeClockEntryBeanModel)model;
            updateBean();
        }
    }

    /**
     * This method updates the Bean if when changed by setModel.
     *  
     * @see #setModel(UIModelIfc)
     */
    @Override
    protected void updateBean()
    {
         // display last entry time, if it exists
        Locale locale = getDefaultLocale();
        if (beanModel.getLastEntry() == null)
        {
            lastEntryDateText.setText("");
        }
        else
        {
            String lastEntry = beanModel.getLastEntry().toFormattedString(locale);
            if (beanModel.getTypeCode() != -1)
            {
               String pattern = retrieveText("TimeEntryPattern","{0} {1} {2}");
               String[] args = {lastEntry,
                                beanModel.getEntryTypeLabelString(),
                                beanModel.getTypeCodeString()};

               lastEntryDateText.setText(LocaleUtilities.formatComplexMessage(pattern,args));
            }
            else
            {
                lastEntryDateText.setText(lastEntry);
            }
        }

        // display current entry time, if it exists (it should always exist)
        if (beanModel.getCurrentEntry() == null)
        {
            currentEntryDateText.setText("");
        }
        else
        {
            String date = beanModel.getCurrentEntry().toFormattedString(DateFormat.SHORT,locale);
            String time = beanModel.getCurrentEntry().toFormattedTimeString(DateFormat.SHORT,locale);
            currentEntryDateText.setText(date+ " " + time);
        }

        // if strings exist, set them in field
        reasonCodes = beanModel.getReasonCodes();
        clockReasonList.setModel(new ValidatingComboBoxModel(reasonCodes));

        // if index valid, set it
        int index = ((ReasonBeanModel) beanModel).getSelectedIndex();
        if (reasonCodes != null && reasonCodes.size() > 0)
        {
            if (index > -1)
            {
                clockReasonList.setSelectedIndex(index);
                clockReasonList.setSelectedItem(beanModel.getSelectedReason());
            }
            else
            {
            	clockReasonList.setSelectedIndex(-1);
            	clockReasonList.setSelectedItem(beanModel.getDefaultValue());
            }
        }

        // set type codes In/Out
        ValidatingComboBox typeField = typeCodeList;

        // if strings exist, set them in typeField
        typeField.setModel(new ValidatingComboBoxModel(beanModel.getTypeCodes()));
        // if index valid, set it
        int typeIndex = beanModel.getSelectedTypeCodeIndex();
        if (typeIndex > -1)
        {
            typeField.setSelectedIndex(typeIndex);
        }
    }

    /**
     * Returns the bean model.
     * 
     * @return model object
     * @see oracle.retail.stores.pos.ui.beans.POSBaseBeanModel
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Initializes the components.
     */
    protected void initComponents()
    {
        lastEntryLabel =
            uiFactory.createLabel("lastEntryLabel", "Last Time Entry:", null, UI_LABEL);

        currentEntryLabel =
            uiFactory.createLabel("currentEntryLabel", "Current Time Entry:", null, UI_LABEL);

        reasonCodeLabel =
            uiFactory.createLabel("reasonCodeLabel", "Reason:", null, UI_LABEL);

        typeCodeLabel =
            uiFactory.createLabel("typeCodeLabel", "Type:", null, UI_LABEL);

        lastEntryDateText =
            uiFactory.createDisplayField("LastEntryDateText");

         currentEntryDateText =
            uiFactory.createDisplayField("CurrentEntryDateText");

        clockReasonList =
            uiFactory.createValidatingComboBox("clockReasonList", "false", "10");
        clockReasonList.setEditable(false);

        typeCodeList =
            uiFactory.createValidatingComboBox("typeCodeList", "false", "15");
        typeCodeList.setEditable(false);
    }

    /**
     *  Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        lastEntryLabel.setText(retrieveText("LastEntryLabel", lastEntryLabel));
        currentEntryLabel.setText(retrieveText("CurrentEntryLabel",currentEntryLabel));
        reasonCodeLabel.setText(retrieveText("ReasonCodeLabel", reasonCodeLabel));
        clockReasonList.setLabel(reasonCodeLabel);
        typeCodeLabel.setText(retrieveText("TypeCodeLabel", typeCodeLabel));
        typeCodeList.setLabel(typeCodeLabel);
    }

    /**
     * Initializes the layout.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(
            this,
            new JLabel[]{lastEntryLabel, currentEntryLabel, typeCodeLabel, reasonCodeLabel},
            new JComponent[]{lastEntryDateText, currentEntryDateText, typeCodeList, clockReasonList}
        );
    }

    /**
     * Overrides setVisible in order to set focus on typeCodeList.
     *  
     * @param value boolean
     */
    @Override
    public void setVisible(boolean value)
    {
        super.setVisible(value);

        // Set the focus
        if (value && !errorFound())
        {
            setCurrentFocus(typeCodeList);
        }
    }

    /**
     * Activates this screen and listeners.
     */
    @Override
    public void activate()
    {
        super.activate();
        typeCodeList.addFocusListener(this);
    }

    /**
     * Deactivates this screen and listeners.
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        typeCodeList.removeFocusListener(this);
    }

    /**
     * Returns default display string.
     *
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: EmployeeClockEntryBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    /**
     * Retrieves the Team Connection revision number.
     *
     * @return String representation of revision number
     */
    @Override
    public String getRevisionNumber()
    {
        return(revisionNumber);
    }
}
