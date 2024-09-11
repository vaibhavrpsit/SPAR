/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ChangePasswordBean.java /main/19 2014/01/28 11:05:39 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   01/24/14 - Fortify: Prevent heap inspection of passwords by
 *                         avoiding using Strings
 *    abhineek  04/03/13 - fix for user is not prompted with any error message
 *                         when a special character is entered in the password
 *                         field
 *    mchellap  07/12/11 - Fortify privacy violation fix
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    glwang    02/25/09 - remove special characters from the password fields.
 *    mdecama   12/16/08 - Updated the maxLength for the UserID from 30 to 10.
 *
 * ===========================================================================
 * $Log:
 *     6    I18N_P2    1.4.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *          alphanumerice fields for I18N purpose
 *     5    360Commerce 1.4         8/9/2007 4:10:04 PM    Michael P. Barnett
 *          Changed password fields to allow special characters.
 *     4    360Commerce 1.3         8/8/2007 10:59:14 AM   Tony Zgarba     CR
 *          28128.  Updated the ChangePasswordBean to change the minimum field
 *           length check from 4 to 1 on the POS change password screen.
 *     3    360Commerce 1.2         11/2/2006 12:18:46 PM  Rohit Sachdeva
 *          21237: Activating Password Policy Evaluation and Change Password
 *     2    360Commerce 1.1         10/20/2006 11:24:48 AM Rohit Sachdeva
 *          21237: Change Password Screen Updates
 *     1    360Commerce 1.0         10/4/2006 10:50:53 AM  Rohit Sachdeva
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;


import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Contains the visual presentation for Change Password Information
 *
 * @version $Revision: /main/19 $
 */
public class ChangePasswordBean extends ValidatingBean
{
    private static final long serialVersionUID = -5709551803520677085L;

    /**
     * Revision number
     */
    public static String revisionNumber = "$Revision: /main/19 $";

    protected static final String  SPACE = "            ";
    /**
     * Password field and label index
     */
    protected static final int LOGIN_ID = 0;
    /**
     * Password field and label index
     */
    protected static final int PASSWORD = LOGIN_ID + 1;
    /**
     * Password field and label index
     */
    protected static final int BLANK = PASSWORD + 1;
    /**
     * Password field and label index
     */
    protected static final int NEW_PASSWORD = BLANK + 1;
    /**
     * Role field and label index
     */
    protected static final int VERIFY_PASSWORD = NEW_PASSWORD + 1;

    /**
     * Highest field and label index
     */
    protected static final int MAX_FIELDS = VERIFY_PASSWORD + 1;

    /**
     * Array of labels
     */
    protected static final String labelText[] =
        {
            "User ID",
            "Current Password",
            "",
            "Enter New Password",
            "Reenter New Password" };

    /**
     * Array of labels
     */
    protected static final String labelTags[] =
        {
            "UserIDLabel",
            "CurrentPasswordLabel",
            "BlankLabel",
            "NewPasswordLabel",
            "VerifyPasswordLabel" };

    /**
     * fieldLabels is an array of JLabels
     */
    protected JLabel[] fieldLabels = new JLabel[MAX_FIELDS];
    /**
     * beanModel is the ChangePassword bean model
     */
    protected ChangePasswordBeanModel beanModel = new ChangePasswordBeanModel();
    /**
     * login id field is used for displaying and entering the employee login id
     */
    protected AlphaNumericTextField loginIdField = null;
    /**
     * passwordField is used for entering the employee password
     */
    protected AlphaNumericPasswordField passwordField = null;
    /**
     * to create a blank
     */
    protected JLabel blank = null;
    /**
     * newPasswordField is used for entering the employee password
     */
    protected AlphaNumericPasswordField newPasswordField = null;
    /**
     * passwordField is used for entering the employee password
     */
    protected AlphaNumericPasswordField verifyPasswordField = null;

    /**
     * Default Constructor
     */
    public ChangePasswordBean()
    {
        super();
        initialize();
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("ChangePasswordBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initComponents();
        initLayout();
    }

    /**
     * Initialize the components.
     */
    protected void initComponents()
    {
        for (int i = 0; i < fieldLabels.length; i++)
        {
            fieldLabels[i] =
                uiFactory.createLabel(labelTags[i], labelTags[i], null, UI_LABEL);
        }

        loginIdField =
            uiFactory.createAlphaNumericField("loginIdField", "1", "10", "10", false);
        passwordField =
            uiFactory.createAlphaNumericPasswordField("passwordField", "1", "22");
        //password match does not happen for this
        passwordField.setPasswordMatchAllowed(false);
        blank =
            uiFactory.createLabel("blank", SPACE, null, UI_LABEL);
        newPasswordField =
        	uiFactory.createAlphaNumericPasswordField("newPasswordField", "1", "22");
        verifyPasswordField =
            uiFactory.createAlphaNumericPasswordField("verifyPasswordField", "1", "22");
        passwordField.setSpecialCharAllowed(true);
        newPasswordField.setSpecialCharAllowed(true);
        verifyPasswordField.setSpecialCharAllowed(true);
    }

    /**
     * Initializes the layout.
     */
    protected void initLayout()
    {
        JComponent[] fields =
            {
                loginIdField,
                passwordField,
                blank,
                newPasswordField,
                verifyPasswordField
            };

        UIUtilities.layoutDataPanel(this, fieldLabels, fields);
    }

    /**
     * The framework calls this method just before display
     */
    @Override
    public void activate()
    {
    	super.activate();

    	loginIdField.addFocusListener(this);
    	setCurrentFocus(loginIdField);
    	passwordField.setEchoChar('*');
    	newPasswordField.setEchoChar('*');
    	verifyPasswordField.setEchoChar('*');
    }

    /**
     * The framework calls this method just before removing the screen
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        loginIdField.removeFocusListener(this);
    }

    /**
     * Update the bean if the model's been changed
     */
    @Override
    public void updateBean()
    {
    	if (beanModel.getLoginID().equals(""))
    	{
    		loginIdField.setEditable(true);
    		loginIdField.setRequired(true);
    	}
    	else
    	{
    		loginIdField.setEditable(false);
    		loginIdField.setRequired(false);
    	}
    	loginIdField.setText(beanModel.getLoginID());

    	//always clear the model for password related fields
    	beanModel.setPassword(new byte[0]);
    	beanModel.setNewPassword(new byte[0]);
    	beanModel.setVerifyPassword(new byte[0]);

        passwordField.setText("");
        blank.setText(SPACE);
        newPasswordField.setText("");
        verifyPasswordField.setText("");
    }

    /**
     * Updates the model for the current settings of this bean.
     */
    @Override
    public void updateModel()
    {
        beanModel.setLoginID(loginIdField.getText());
        beanModel.setPassword(Util.charArrayToByteArray(passwordField.getPassword(), EmployeeIfc.PASSWORD_CHARSET));
        beanModel.setNewPassword(Util.charArrayToByteArray(newPasswordField.getPassword(), EmployeeIfc.PASSWORD_CHARSET));
        beanModel.setVerifyPassword(Util.charArrayToByteArray(verifyPasswordField.getPassword(), EmployeeIfc.PASSWORD_CHARSET));
    }

    /**
     * Sets the model for the current settings of this bean.
     *
     * @param model
     *            the model for the current values of this bean
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException(
                "Attempt to set ChangePasswordBeanModel" + " to null");
        }
        if (model instanceof ChangePasswordBeanModel)
        {
            beanModel = (ChangePasswordBeanModel)model;
            updateBean();
        }
    }

    /**
     * Update property fields.
     */
    @Override
    public void updatePropertyFields()
    {
        for (int i = 0; i < labelTags.length; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i], fieldLabels[i]));
        }
        fieldLabels[BLANK].setText(SPACE);
        // update label text
        passwordField.setLabel(fieldLabels[PASSWORD]);
        newPasswordField.setLabel(fieldLabels[NEW_PASSWORD]);
        verifyPasswordField.setLabel(fieldLabels[VERIFY_PASSWORD]);
    }

    /**
     * Gets the POSBaseBeanModel associated with this bean.
     *
     * @return the POSBaseBeanModel associated with this bean.
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Override JPanel set Visible to request focus.
     * @param visible indicates if the component should be visible or not.
     */
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        // Set the focus
        if (loginIdField.isEditable())
        {
            setCurrentFocus(loginIdField);
        }
        else
        {
        	setCurrentFocus(passwordField);
        }
    }

    /**
     * Returns the revision number of the class.
     *
     * @param none
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }
}
