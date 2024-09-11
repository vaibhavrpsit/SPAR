/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ValidatingBean.java /main/22 2014/01/28 11:05:44 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  07/26/13 - handle scenario where POSBaseBeanModel is null
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/13/10 - convert jgl libraries to java.util
 *    dwfung    02/03/10 - add ValidateList field
 *    abondala  01/03/10 - update header date
 *    ohorne    03/30/09 - now trimming parsed names in parseFieldNames()
 *    npoola    03/18/09 - Fix to display only first 3 error messages on the
 *                         dialog screen
 *
 * ===========================================================================
 * $Log:
 *    8    I18N_P2    1.6.1.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR
 *         29826 - Setting the size of the combo boxes. This change was
 *         necessary because the width of the combo boxes used to grow
 *         according to the length of the longest content. By setting the
 *         size, we allow the width of the combo box to be set independently
 *         from the width of the dropdown menu.
 *    7    360Commerce 1.6         10/4/2006 11:03:19 AM  Rohit Sachdeva
 *         21237: Allow flexibility that a password field may not be needed
 *         for match
 *    6    360Commerce 1.5         5/12/2006 5:25:36 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    5    360Commerce 1.4         4/2/2006 11:57:46 PM   Dinesh Gautam   Added
 *          code for new fields �Employee login Id� & �Verify Password�
 *         Added validation for matching password & verify password fields
 *    4    360Commerce 1.3         1/25/2006 4:11:55 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    3    360Commerce 1.2         3/31/2005 4:30:43 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:43 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:30 PM  Robert Pearse   
 *:
 *    4    .v700     1.2.1.0     11/2/2005 13:35:29     Jason L. DeLeau 6592:
 *         Prevent hang that occurs when trying to delete a business customer.
 *    3    360Commerce1.2         3/31/2005 15:30:43     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:26:43     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:15:30     Robert Pearse
 *
 *   Revision 1.16  2004/08/23 15:56:24  kll
 *   @scr 6358: increase number of lines present in the DialogBean
 *
 *   Revision 1.15  2004/08/03 19:13:59  mweis
 *   @scr 6643 Invalid Data Notice dialog now obeys the 8 line limit set by the DialogBean.
 *
 *   Revision 1.14  2004/07/29 20:43:25  jdeleau
 *   @scr 6594 backout changes until further requirements are known
 *
 *   Revision 1.12  2004/07/29 13:51:16  kll
 *   @scr 6358: MAX_ERROR_MESSAGES to 8
 *
 *   Revision 1.11  2004/07/28 21:13:25  mweis
 *   @scr 6197 Need precise error message for fields that have an exact length needed.
 *
 *   Revision 1.10  2004/07/27 17:58:07  kll
 *   @scr 6358: increase number of error messages allowed to be displayed via the ValidatingBean class
 *
 *   Revision 1.9  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.8  2004/07/17 18:22:29  jdeleau
 *   @scr 5624 On validation error, when going back to the
 *   original screen, make the field with the error have focus.
 *
 *   Revision 1.7  2004/06/10 18:44:59  mweis
 *   @scr 5462 Return with receipt error message does not contain all invalid fields (when date is part of the transaction)
 *
 *   Revision 1.6  2004/03/22 19:27:00  cdb
 *   @scr 3588 Updating javadoc comments
 *
 *   Revision 1.5  2004/03/22 03:49:28  cdb
 *   @scr 3588 Code Review Updates
 *
 *   Revision 1.4  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/02/11 23:22:58  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Dec 23 2003 17:36:56   cdb
 * Added handling of editable ValidatingComboBox and new Logger.
 * Resolution for 3588: Discounts/MUPS - Gap Rollback
 *
 *    Rev 1.2   15 Dec 2003 23:59:32   baa
 * enhancement for return feature
 *
 *    Rev 1.1   Sep 16 2003 17:53:36   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:12:58   CSchellenger
 * Initial revision.
 *
 *    Rev 1.8   Apr 16 2003 14:53:54   baa
 * fix missing bundle property names
 * Resolution for POS SCR-2170: Missing property names in bundles
 *
 *    Rev 1.7   Mar 07 2003 17:11:10   baa
 * code review changes for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.6   Sep 18 2002 17:15:34   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.5   Sep 10 2002 17:49:18   baa
 * add password field
 * Resolution for POS SCR-1810: Adding pasword validating fields
 *
 *    Rev 1.4   Aug 21 2002 14:32:14   DCobb
 * SCR 1789 - Fixed class cast exception when validating ConstrainedTextAreaField.
 * Resolution for POS SCR-1789: Validating ConstrainedTextAreaField
 *
 *    Rev 1.3   Aug 07 2002 19:34:28   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   28 May 2002 12:21:58   vxs
 * Removed unncessary concatenations from logging statements.
 * Resolution for POS SCR-1632: Updates for Gap - Logging
 *
 *    Rev 1.1   21 May 2002 17:42:58   baa
 * ils
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   13 May 2002 14:12:06   baa
 * Initial revision.
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.1   15 Apr 2002 09:36:42   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:52:44   msg
 * Initial revision.
 *
 *    Rev 1.7   16 Feb 2002 10:17:18   baa
 * fix required field logic
 * Resolution for POS SCR-1306: Invalid Data Notice missing after selecting Enter with no data for text area fields
 *
 *    Rev 1.6   15 Feb 2002 16:33:40   baa
 * ui fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.5   23 Jan 2002 14:51:02   sfl
 * Enable the Clear button for the Special Instruction
 * text area in the Shipping Method screen.
 * Resolution for POS SCR-783: Send - 'Shipping Method' screen, 'Clear' is disabled in 'Special Instructions' field
 *
 *    Rev 1.4   Jan 22 2002 09:12:14   mpm
 * UI fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Component;
import java.awt.Container;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.StringTokenizer;
import java.util.Vector;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JTextArea;
import javax.swing.event.DocumentListener;
import javax.swing.text.JTextComponent;

import org.apache.log4j.Logger;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ui.jfc.ButtonPressedLetter;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.config.ConfigurationException;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSJFCUISubsystem;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.ClearActionListener;
import oracle.retail.stores.pos.ui.behavior.ValidateActionListener;

//-------------------------------------------------------------------------
/**
    This class serves as a base class for screen beans that can be validated.
    @version $Revision: /main/22 $
**/
//-------------------------------------------------------------------------
public abstract class ValidatingBean extends    CycleRootPanel
                                     implements ValidatingBeanIfc,
                                                ValidateActionListener,
                                                ClearActionListener
{

    private static final long serialVersionUID = -5025434360629188732L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/22 $";
    /** the ArrayList of all required fields */
    protected ArrayList<ValidatingFieldIfc> requiredFields = new ArrayList<ValidatingFieldIfc>();
    /** the ArrayList of all optional fields */
    protected ArrayList<ValidatingFieldIfc> optionalFields = new ArrayList<ValidatingFieldIfc>();
    /** The delimiter that seperates field names */
    protected final String DELIMITER            = ",";
    /** The action name associated with the clear key */
    protected final String CLEAR                = "Clear";
    /** Maximum number of fields that fail validation to display */
    protected final int MAX_ERROR_MESSAGES      = 8;  // number of DialogSpec.InvalidData's dynamic lines
    
    /** Display Error messages at a time **/
    protected final int DISPLAY_ERROR_MESSAGES  = 3;  // number of DialogSpec.InvalidData's dynamic lines
    
    /** Error Message to display when fields fail validation */
    protected String[] errorMessage             = new String[MAX_ERROR_MESSAGES];
    /** Used by class that extend this class to tell if a validaion
        error has occured. */
    protected boolean  errorFound               = false;
    /** The component last received focus in this bean. */
    protected JComponent currentComponent       = null;

    //---------------------------------------------------------------------
    /**
        Class constructor.
    **/
    //---------------------------------------------------------------------
    protected ValidatingBean()
    {
        super();
    }

    //---------------------------------------------------------------------
    /**
       Constructor that defines the layout manager.
       @param layout the layout manager
    */
    //---------------------------------------------------------------------
    public ValidatingBean(LayoutManager layout)
    {
        super(layout);
    }

    //--------------------------------------------------------------------------
    /**
     *    Gets the model associated with the current screen information.
     *
     *    @return the model for the information currently in the bean
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
      return beanModel;
    }

    //---------------------------------------------------------------------
    /**
        Sets the fields that should be validated.  The name in the field
        must match the name of one of the validating fields in the panel.
        @param fields a comma delimited string of required field names.
    **/
    //---------------------------------------------------------------------
    public void setRequiredValidatingFields(String fields)
    {
        requiredFields = new ArrayList<ValidatingFieldIfc>();
        ArrayList<Component> childComponents = getDescendants(this);
        String[] fieldNames = parseFieldNames(fields);

        for (int i = 0; i < fieldNames.length; i++)
        {
            markField(childComponents, fieldNames[i], true);
        }
    }

    //---------------------------------------------------------------------
    /**
        Sets the fields that should be validated.  The name in the field
        must match the name of one of the validating fields in the panel.
        @param fields a comma delimited string of required field names.
    **/
    //---------------------------------------------------------------------
    public void setOptionalValidatingFields(String fields)
    {
        optionalFields = new ArrayList<ValidatingFieldIfc>();
        ArrayList<Component> childComponents = getDescendants(this);
        String[] fieldNames = parseFieldNames(fields);

        for (int i = 0; i < fieldNames.length; i++)
        {
            markField(childComponents, fieldNames[i], false);
        }
    }

    //---------------------------------------------------------------------
    /**
        Sets a field to required or optional depending on the second argument
        which is a boolean.
        @param field as ValidatingPasswordField
        @param required as boolean.
    **/
    //---------------------------------------------------------------------
    public void setFieldRequired(ValidatingPasswordField field, boolean required)
    {
       int optionalFieldsIndex = optionalFields.indexOf(field);
       int requiredFieldsIndex = requiredFields.indexOf(field);

       field.setRequired(required);
       field.setEmptyAllowed(!required);
       field.repaint();

       if (required)
       {
            if (requiredFieldsIndex == -1)
            {
                requiredFields.add(field);
            }
            if (optionalFieldsIndex != -1)
            {
                optionalFields.remove(optionalFieldsIndex);
            }
       }
       else
       {
            if (optionalFieldsIndex == -1)
            {
                optionalFields.add(field);
            }
            if (requiredFieldsIndex != -1)
            {
                requiredFields.remove(requiredFieldsIndex);
            }
       }
    }

   //---------------------------------------------------------------------
    /**
        Sets a field to required or optional depending on the second argument
        which is a boolean.
        @param field as ValidatingTextField
        @param required as boolean.
    **/
    //---------------------------------------------------------------------
    public void setFieldRequired(ValidatingTextField field, boolean required)
    {
       int optionalFieldsIndex = optionalFields.indexOf(field);
       int requiredFieldsIndex = requiredFields.indexOf(field);


       field.setRequired(required);
       field.setEmptyAllowed(!required);
       field.repaint();

       if (required)
       {
            if (requiredFieldsIndex == -1)
            {
                requiredFields.add(field);
            }
            if (optionalFieldsIndex != -1)
            {
                optionalFields.remove(optionalFieldsIndex);
            }
       }
       else
       {
            if (optionalFieldsIndex == -1)
            {
                optionalFields.add(field);
            }
            if (requiredFieldsIndex != -1)
            {
                requiredFields.remove(requiredFieldsIndex);
            }
       }
    }

    //---------------------------------------------------------------------
    /**
        Sets a field to required or optional depending on the second argument
        which is a boolean.
        @param field as ValidatingFormattedTextField
        @param required as boolean.
    **/
    //---------------------------------------------------------------------
    public void setFieldRequired(ValidatingFormattedTextField field, boolean required)
    {
       int optionalFieldsIndex = optionalFields.indexOf(field);
       int requiredFieldsIndex = requiredFields.indexOf(field);


       field.setRequired(required);
       field.setEmptyAllowed(!required);
       field.repaint();

       if (required)
       {
            if (requiredFieldsIndex == -1)
            {
                requiredFields.add(field);
            }
            if (optionalFieldsIndex != -1)
            {
                optionalFields.remove(optionalFieldsIndex);
            }
       }
       else
       {
            if (optionalFieldsIndex == -1)
            {
                optionalFields.add(field);
            }
            if (requiredFieldsIndex != -1)
            {
                requiredFields.remove(requiredFieldsIndex);
            }
       }
    }
    //---------------------------------------------------------------------
    /**
       This method parses the list of field names from a comma delimited
       string to an array of names.
       @param names a comma delimited list of field names.
       @return an array of field names.
    */
    //---------------------------------------------------------------------
    protected String[] parseFieldNames(String names)
    {
        StringTokenizer st = new StringTokenizer(names, DELIMITER);
        ArrayList<String> nameList = new ArrayList<String>();
        while (st.hasMoreTokens())
        {
            String name = st.nextToken();
            if (name != null)
            {
                nameList.add(name.trim());    
            }            
        }

        String[] nameArray = new String[nameList.size()];
        nameList.toArray(nameArray);
        return(nameArray);
    }

    //---------------------------------------------------------------------
    /**
        Returns the tree of all the children of the requested component.
        <p>
        @param c The component whose children are suppose to be queried.
        @return An ArrayList of all the children.
    **/
    //---------------------------------------------------------------------
    static public ArrayList<Component> getDescendants(Container c)
    {
        ArrayList<Component> ary = new ArrayList<Component>();

        Component[] children = c.getComponents();
        int n = children.length;
        for (int i = 0; i < n; i++)
        {
            if (children[i] != null)
            {
                ary.add(children[i]);
                ArrayList<Component> newChildren = null;
                if (children[i] instanceof Container)
                {
                    newChildren = getDescendants((Container)children[i]);
                    if (newChildren.size() > 0)
                    {
                        ary.addAll(newChildren);
                    }
                }
            }
        }
        return ary;
    }

    //---------------------------------------------------------------------
    /**
        Marks a field as required, or optional. <p>
        @param children The array of children components to this bean
        @param fieldName The name of the child field
        @param required Whether or not the field is required.
                            "true" if required, "false" otherwise.
    **/
    //----------------------------------------------------------------------
    protected void markField(ArrayList<Component> children, String fieldName, boolean required)
    {
        // search for field
        Component field = null;
        for (int i = 0; i < children.size(); i++)
        {
            Component tmpField = (Component)children.get(i);
            if (tmpField.getName()!=null && tmpField.getName().equals(fieldName))
            {
                field = tmpField;
                i = children.size();
            }
        }

        // make sure we found a field
        if (field == null)
        {
            Logger log = Logger.getLogger(oracle.retail.stores.pos.ui.beans.ValidatingBean.class);
            log.warn("FieldName: " + fieldName + " not found in bean:" + getClass());
        }

        if (field instanceof ValidatingFieldIfc)
        {
            ValidatingFieldIfc validatingField = (ValidatingFieldIfc)field;
            validatingField.setEmptyAllowed(!required);

            if (required)
            {
                validatingField.setRequired(true);
                requiredFields.add(validatingField);
            }
            else
            {
                validatingField.setRequired(false);
                optionalFields.add(validatingField);
            }
            ((Component)validatingField).invalidate();
        }
    }

    //---------------------------------------------------------------------
    /**
        This method is called through a connection from the
        GlobalNavigationBean.  Indicates that the "Clear" or "Validation"
        process should be performed.
        @param event the ActionEvent contructed by the caller.
    **/
    //---------------------------------------------------------------------
    public void actionPerformed(ActionEvent event)
    {
        if (event.getActionCommand().equalsIgnoreCase(CLEAR))
        {
            performClearAction();
        }
        else
        {
            performValidateAction(event);
        }
    }

    //---------------------------------------------------------------------
    /**
        This method performs the "Clear" process.
    **/
    //---------------------------------------------------------------------
    protected void performClearAction()
    {
        if (currentComponent != null)
        {
            setCurrentFocus(currentComponent);
            if (currentComponent instanceof JTextComponent)
            {
                ((JTextComponent)currentComponent).setText("");
            }
            else if (currentComponent instanceof JTextArea)
            {
                ((JTextArea)currentComponent).setText("");
            }
            else if (currentComponent instanceof JComboBox)
            {
                ((JComboBox)currentComponent).setSelectedItem("");
            }
        }
    }

    //---------------------------------------------------------------------
    /**
        This method performs the "Validation" process.
        @param event the ActionEvent contructed by the caller.
    **/
    //---------------------------------------------------------------------
    protected void performValidateAction(ActionEvent event)
    {
        // Get the letter name and number of the button the user pressed
        // to get here.
        String letterName   = event.getActionCommand();
        int    buttonNumber = 0;
        if (event.getSource() instanceof UIAction)
        {
            UIAction source = (UIAction)event.getSource();
            buttonNumber    = source.getButtonNumber();
        }

        // Validate the data in the screen.
        if (validateFields() && validatePasswordFields())
        {
            // No errors, mail the letter to the business logic.
            UISubsystem.getInstance().mail(new ButtonPressedLetter(letterName, buttonNumber), true);
        }
    }

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
        errorFound = false;
        // Clear out the error message array.
        for (int i = 0; i < MAX_ERROR_MESSAGES; i++)
        {
            errorMessage[i] = "";
        }
        getPOSBaseBeanModel().setFieldInErrorName(null);

        // first make sure all the required fields have valid data
        int errorCount = 0;
        Iterator<ValidatingFieldIfc> requiredEnum = requiredFields.iterator();
        while (requiredEnum.hasNext() && errorCount < DISPLAY_ERROR_MESSAGES)
        {
            ValidatingFieldIfc field = (ValidatingFieldIfc)requiredEnum.next();
            String name = ((Component)field).getName();

            // if input is not valid set error message
            if (!field.isInputValid())
            {
                if(getPOSBaseBeanModel().getFieldInErrorName() == null)
                {
                    getPOSBaseBeanModel().setFieldInErrorName(name);
                }

                errorMessage[errorCount] = invalidDataMsg(field);
                errorCount++;
            }
        }

        // now make sure all non-null optional fields have valid data
        Iterator<ValidatingFieldIfc> optionalEnum = optionalFields.iterator();
        while (optionalEnum.hasNext() && errorCount < DISPLAY_ERROR_MESSAGES)
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
            else if (field instanceof ValidatingFormattedTextField)
            {
            	ValidatingFormattedTextField tField = (ValidatingFormattedTextField) field;

                // if a field is non-null and the data is invalid, set error message
                if(!tField.getText().equals(tField.emptyTextFormat()) && !tField.isInputValid())
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
        Retrieves the field name if available otherwise use default string.
        @param field the field being validated.
        @return Externalized field name
    **/
    //---------------------------------------------------------------------
    protected String getFieldName(ValidatingFieldIfc field)
    {
        String fieldName = field.getErrorMessage();
        if (fieldName.equals(""))
        {
           fieldName = retrieveText("NoLabel","A field on this screen ");
        }
        return fieldName;
    }
    //---------------------------------------------------------------------
    /**
        Builds text message for invalid data according to the field type
        @param field the field being validated.
        @return Invalid Data Message
    **/
    //---------------------------------------------------------------------
    protected String invalidDataMsg(ValidatingFieldIfc field)
    {
        // Change string depending on field type
        String msg = null;
        Object[] data = null;
        if ( field instanceof EYSDateField)
        {
           msg = UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.InvalidDate",
                                          "{0} is not a valid calendar date.");
           data = new Object[1];
           data[0] = getFieldName(field);
        }
        else if (field instanceof EYSTimeField)
        {
           msg = UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.InvalidTime",
                                          "{0} is not a valid time.");
           data = new Object[1];
           data[0] = getFieldName(field);
        }
        else if (field instanceof ValidatingTextField)
        {
            // Determine if we need the "exact" or "at least" message
            int minLength = ((ValidatingTextField)field).getMinLength();
            int maxLength = Integer.MAX_VALUE;
            if (field instanceof ConstrainedTextField)
            {
                maxLength = ((ConstrainedTextField)field).getMaxLength();
            }

            if (minLength == maxLength)
            {
                msg = UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.NotExactLength",
                                          "{0} must be exactly {1} {1,choice,1#character|2#characters} long.");
            }
            else
            {
                msg =  UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.InvalidLength",
                                          "{0} must be at least {1} {1,choice,1#character|2#characters} long.");
            }

            data = new Object[2];
            data[0]= getFieldName(field);
            data[1]= new Integer(minLength);  // at times 'minLength' == 'maxLength'

        }
        else if (field instanceof ValidatingTextAreaField)
        {
           msg =  UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.InvalidLength",
                                          "{0} must be at least {1} {1,choice,1#character|2#characters} long.");
           data = new Object[2];
           data[0]= getFieldName(field);
           data[1]= new Integer(((ValidatingTextAreaField)field).getMinLength());

        }
        else if (field instanceof ValidatingPasswordField)
        {
           msg =  UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.InvalidLength",
                                          "{0} must be at least {1} {1, choice,1#character|2#characters} long.");
           data = new Object[2];
           data[0]= getFieldName(field);
           data[1]= new Integer(((ValidatingPasswordField)field).getMinLength());
        }
        else if (field instanceof ValidatingComboBox)
        {
           msg =  UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.InvalidSelection",
                                          "{0} does not contain a valid selection.");
           data = new Object[1];
           data[0]= getFieldName(field);
        }
        else if (field instanceof ValidatingFormattedTextField)
        {
            msg = UIUtilities.retrieveText("DialogSpec",
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.IncorrectData",
                    "{0} does not contain valid data.");
           data = new Object[1];
           data[0] = getFieldName(field);
        }
        else if (field instanceof ValidatingList)
        {
            msg = UIUtilities.retrieveText("DialogSpec",
                    BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                    "InvalidData.InvalidSelection",
                    "{0} does not contain a valid selection.");
           data = new Object[1];
           data[0] = getFieldName(field);
        }
        else
        {
           msg = UIUtilities.retrieveText("DialogSpec",
                                          BundleConstantsIfc.DIALOG_BUNDLE_NAME,
                                          "InvalidData.IncorrectData",
                                          "{0} does not contain valid data.");
           data = new Object[1];
           data[0] = getFieldName(field);
        }
        msg = LocaleUtilities.formatComplexMessage(msg,data,getLocale());
        return msg;
    }

    //---------------------------------------------------------------------
    /**
        Determines if password fields & verify password fields have 
        matching valid data
        @return True if no errors
    **/
    //---------------------------------------------------------------------
    protected boolean validatePasswordFields()
    {
        errorFound = false;
        // Clear out the error message array.
        for (int i = 0; i < MAX_ERROR_MESSAGES; i++)
        {
            errorMessage[i] = "";
        }
        getPOSBaseBeanModel().setFieldInErrorName(null);

        // first make sure all the required fields have valid data
        int errorCount = 0;
        Iterator<ValidatingFieldIfc> requiredEnum = requiredFields.iterator();

        char[] password = null;
        
        while (requiredEnum.hasNext() && errorCount < DISPLAY_ERROR_MESSAGES)
        {
            ValidatingFieldIfc field = (ValidatingFieldIfc)requiredEnum.next();
            String name = ((Component)field).getName();
            
            if (field instanceof ValidatingPasswordField)
            {
            	ValidatingPasswordField tField = (ValidatingPasswordField) field;
            	//to allow flexibility that a password field may not be needed for match.
            	//default is true
            	if (tField.isPasswordMatchAllowed())
            	{
            		char[] password2 = tField.getPassword(); 
            		// if a field is non-null and the data is invalid, set error message
            		if(password != null && !Arrays.equals(password, password2))
            		{
            			if(getPOSBaseBeanModel().getFieldInErrorName() == null)
            			{
            				getPOSBaseBeanModel().setFieldInErrorName(name);
            			}
            			errorMessage[errorCount] = invalidDataMsg(field);
            			errorCount++;
            		}
            		else
            		{
            			password = tField.getPassword();
            		}
            	}
            }
        }

        boolean valid = true;
        if (errorCount > 0)
        {
            // There were errors, show the error screen.
            showErrorDialog();
            valid = false;
        }

        return valid;
    }
    
    //---------------------------------------------------------------------
    /**
        Builds the model for the error dialog and call show screen on
        the UISubsystem.
    **/
    //---------------------------------------------------------------------
    protected void showErrorDialog()
    {
        // Set up the bean model and show the screen.
        errorFound = true;
        updateModel();
        DialogBeanModel dialogModel = new DialogBeanModel();
        POSJFCUISubsystem        ui = (POSJFCUISubsystem)UISubsystem.getInstance();
        dialogModel.setResourceID("PasswordVerificationError");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(errorMessage);
        dialogModel.setFormModel(getPOSBaseBeanModel());
        dialogModel.setFormScreenSpecName(ui.getCurrentScreenSpecName());
        dialogModel.setUiGeneratedError(true);
        try
        {
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        catch (oracle.retail.stores.foundation.manager.gui.UIException uie)
        {
            Logger log = Logger.getLogger(oracle.retail.stores.pos.ui.beans.ValidatingBean.class);
            log.warn(uie);
        }
        catch (ConfigurationException ce)
        {
            Logger log = Logger.getLogger(oracle.retail.stores.pos.ui.beans.ValidatingBean.class);
            log.warn(ce);
        }
    }

    //---------------------------------------------------------------------
    /**
        Builds the model for the error dialog and call show screen on
        the UISubsystem.
    **/
    //---------------------------------------------------------------------
    protected void showErrorScreen()
    {
        // Set up the bean model and show the screen.
        errorFound = true;
        updateModel();
        DialogBeanModel dialogModel = new DialogBeanModel();
        POSJFCUISubsystem        ui = (POSJFCUISubsystem)UISubsystem.getInstance();
        dialogModel.setResourceID("InvalidData");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setArgs(errorMessage);
        dialogModel.setFormModel(getPOSBaseBeanModel());
        dialogModel.setFormScreenSpecName(ui.getCurrentScreenSpecName());
        dialogModel.setUiGeneratedError(true);
        try
        {
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        catch (oracle.retail.stores.foundation.manager.gui.UIException uie)
        {
            Logger log = Logger.getLogger(oracle.retail.stores.pos.ui.beans.ValidatingBean.class);
            log.warn(uie);
        }
        catch (ConfigurationException ce)
        {
            Logger log = Logger.getLogger(oracle.retail.stores.pos.ui.beans.ValidatingBean.class);
            log.warn(ce);
        }
    }

    //---------------------------------------------------------------------
    /**
        Returns the Optional Fields
        <p>
        @return the optional fields
    **/
    //---------------------------------------------------------------------
    protected ArrayList<ValidatingFieldIfc> getOptionalFields()
    {
        return(optionalFields);
    }

    //---------------------------------------------------------------------
    /**
        Returns the Required Fields
        <p>
        @return the required fields
    **/
    //---------------------------------------------------------------------
    protected ArrayList<ValidatingFieldIfc> getRequiredFields()
    {
        return(requiredFields);
    }

    //---------------------------------------------------------------------
    /**
        Returns the error messages to display when text fields fail
        validation. <p>
        @return The error messages to display
    **/
    //---------------------------------------------------------------------
    public String[] getErrorMessages()
    {
        return errorMessage;
    }

    //---------------------------------------------------------------------
    /**
        Returns the error found boolean. <p>
        @return The errorFound boolean
    **/
    //---------------------------------------------------------------------
    public boolean getErrorFound()
    {
        return errorFound;
    }

    //--------------------------------------------------------------------------
    /**
        Override JPanel set Visible to request focus.
        @param aFlag indicates if the component should be visible or not.
    **/
    //--------------------------------------------------------------------------
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        POSBaseBeanModel posBaseBeanModel = getPOSBaseBeanModel();
        if (visible && posBaseBeanModel != null)
        {
            if(posBaseBeanModel.getFieldInErrorName() == null)
            {
                setFocusToFirst();
            }
            else
            {
                setFocusByName(posBaseBeanModel.getFieldInErrorName());
            }
        }
    }

    /**
     * Return whether or not an error was found during validation
     *
     *  @return true or false
     */
    public boolean errorFound()
    {
        POSBaseBeanModel posBaseBeanModel = getPOSBaseBeanModel(); 
        boolean errorFound = true;
        if (posBaseBeanModel != null)
        {
            errorFound = posBaseBeanModel.getFieldInErrorName() != null;
        }
        return errorFound;

    }
    //--------------------------------------------------------------------------
    /**
        This method sets the focus to the first editable field in the
        panel.
    **/
    //--------------------------------------------------------------------------
    protected void setFocusToFirst()
    {
        Component[] components = getComponents();
        for(int i = 0; i < components.length; i++)
        {
            if (components[i].isEnabled() && components[i].isFocusable())
            {
                if (components[i] instanceof JTextComponent)
                {
                	JTextComponent textField = (JTextComponent) components[i];
                	if(textField.isEditable())
                	{
                		textField.requestFocusInWindow();
                		break;
                	}
                }
                else if (components[i] instanceof JComboBox)
                {
                    JComboBox cb = (JComboBox)components[i];
                    if(cb.isEditable())
                    {
                    	cb.requestFocusInWindow();
                    	break;
                    }
                }
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
        This method iterates through the components in the panel for the one
        with name matching the parameter. If found, set the focus to that
        component.
        @param fieldName the name of the field on which to set focus.
    **/
    //--------------------------------------------------------------------------
    protected void setFocusByName(String fieldName)
    {
        boolean     found      = false;
        Component[] components = getComponents();
        for(int i = 0; i < components.length; i++)
        {
            if (components[i] instanceof ValidatingFieldIfc &&
                components[i].getName().equals(fieldName) &&
                components[i].isEnabled() && components[i].isFocusable())
            {
                found = true;
                setCurrentFocus((JComponent)components[i]);
                if (components[i] instanceof ValidatingTextField)
                {
                    ValidatingTextField vtf = (ValidatingTextField)components[i];
                    setCurrentFocus(vtf);
                }
                else if (components[i] instanceof ValidatingFormattedTextField)
                {
                	ValidatingFormattedTextField vtf = (ValidatingFormattedTextField)components[i];
                    setCurrentFocus(vtf);
                }
                else if (components[i] instanceof ValidatingComboBox
                         && ((ValidatingComboBox) components[i]).isEditable())
                {
                    ValidatingComboBox vcb = (ValidatingComboBox)components[i];
                    setCurrentFocus(vcb);
                }

                else if (components[i] instanceof ValidatingTextAreaField)
                {   ValidatingTextAreaField vtaf = (ValidatingTextAreaField)components[i];
                    setCurrentFocus(vtaf);
                }
                i = components.length;
            }
        }

        if (!found)
        {
            setFocusToFirst();
        }
    }

    //---------------------------------------------------------------------
    /**
        Adds a listener that will receive events from this object. <p>
        @param listener the listener to add
    **/
    //---------------------------------------------------------------------
    public void addDocumentListener(DocumentListener listener)
    {
        ArrayList<Component> childComponents = getDescendants(this);
        for(int j = 0; j < childComponents.size(); j++)
        {
            if (childComponents.get(j) instanceof ValidatingTextField)
            {
                ValidatingTextField vtf = (ValidatingTextField)childComponents.get(j);
                vtf.setPOSDocumentListener(listener, this);
            }
            else if (childComponents.get(j) instanceof ValidatingFormattedTextField)
            {
            	ValidatingFormattedTextField vtf = (ValidatingFormattedTextField)childComponents.get(j);
                vtf.setPOSDocumentListener(listener, this);
            }
            else if (childComponents.get(j) instanceof ValidatingComboBox
                     && ((ValidatingComboBox) childComponents.get(j)).isEditable())
            {
                ValidatingComboBox vcb = (ValidatingComboBox)childComponents.get(j);
                vcb.setPOSDocumentListener(listener, this);
            }
            else if (childComponents.get(j) instanceof ValidatingTextAreaField)
            {
                ValidatingTextAreaField vta = (ValidatingTextAreaField)childComponents.get(j);
                vta.setPOSDocumentListener(listener, this);
            }
            else if (childComponents.get(j) instanceof ValidatingPasswordField)
            {
                ValidatingPasswordField vta = (ValidatingPasswordField)childComponents.get(j);
                vta.setPOSDocumentListener(listener, this);
            }
        }

    }

    //---------------------------------------------------------------------
    /**
        Removes a listener that will no longer receive events
        from this object.
        @param listener the listener to remove
    **/
    //---------------------------------------------------------------------
    public void removeDocumentListener(DocumentListener listener)
    {
        // there is nothing to do; the validating text fields control
        // whether the listener is activ or not.
    }

    //---------------------------------------------------------------------
    /**
       Sets the component that currently has the focus.
       @param current a reference to the current object.
    */
    //---------------------------------------------------------------------
    public void setCurrentComponent(JComponent current)
    {
        currentComponent = current;
    }

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: " + getClass().getName() + " (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
        Returns the revision number. <P>
        @return String representation of revision number
    **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
    
    //---------------------------------------------------------------------
    /**
        Convenience method to populate a comboBox
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
        Convenience method to populate a comboBox
        @param data     a vector containing the data to be display in the combo box
        @param field    the actual combo box field receiving the data
        @param selected index the default selected value
     */
    //--------------------------------------------------------------------- 
    protected void setComboBoxModel(Vector<String> data, ValidatingComboBox field, int selectedIndex)
    {
        if (data != null)
        {
            String [] dataArray = new String[data.size()];
            data.toArray(dataArray);
            setComboBoxModel(dataArray, field, selectedIndex);
        }
    }    
    
}
