/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/LoginBean.java /main/13 2014/02/17 08:54:00 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   02/12/14 - Fortify Fix: Prevent heap inspection of passwords by
 *                         avoiding the use of Strings for passwords
 *    vtemker   01/24/14 - Fortify: Prevent heap inspection of passwords by
 *                         avoiding using Strings
 *    cgreene   11/27/13 - addNotify method helps gets focus onto login id
 *                         field
 *    cgreene   11/18/13 - set login field as focus if not set
 *    cgreene   06/04/13 - implement manager override as dialogs
 *    mkutiana  10/04/12 - moved initLayout since ParameterManager/bus not
 *                         available during class initialization
 *    mkutiana  10/02/12 - Using Gateway to get the parameterManager
 *    cgreene   03/13/12 - Deprecate pos ADOContext code in favor of foundation
 *                         TourContext class
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   02/14/11 - move parameterconstants path
 *    mkutiana  02/11/11 - Based on parameter (ManualEntryRequiresPassword) the
 *                         password field/label is hidden
 *    icole     02/10/11 - Removed hack for context being null, unable to
 *                         reproduce error.
 *    blarsen   10/21/10 - login bean
 *    blarsen   05/11/10 - login bean
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.event.WindowEvent;
import java.io.UnsupportedEncodingException;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This class is used to display and gather login information (login
 * id/password/fingerprint)
 *
 * @version $Revision: /main/13 $
 */
public class LoginBean extends ValidatingBean
{
    private static final long serialVersionUID = 7790923844652177038L;

    protected JLabel loginIDLabel = null;
    protected JLabel passwordLabel = null;
    protected JLabel andLabel = null;
    protected JLabel fingerprintLabel = null;

    protected AlphaNumericTextField loginIDField = null;
    protected AlphaNumericPasswordField passwordField = null;

    /**
     * Default Constructor.
     */
    public LoginBean()
    {
        initialize();
    }

    /**
     * Return the POSBaseBeanModel.
     *
     * @return posBaseBeanModel as POSBaseBeanModel
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("LoginBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();
    }

    /**
     * Initialize the layout.
     * <p>
     * This requires the ParameterManager to be initialized before it can be
     * called.
     */
    protected void initLayout()
    {
        setLayout(new GridBagLayout());

        String fingerprintLoginOption = ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_NO_FINGERPRINT;
        boolean manualLoginEntryReqPwdParm = true; //Defaulting to "Y"

        try
        {
            ParameterManagerIfc pm = (ParameterManagerIfc)Gateway.getDispatcher().getManager(ParameterManagerIfc.TYPE);
            fingerprintLoginOption = pm.getStringValue(ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions);
            manualLoginEntryReqPwdParm = pm.getBooleanValue(ParameterConstantsIfc.OPERATORID_ManualEntryRequiresPassword);
        }
        catch (ParameterException e)
        {
            logger.error("Unable to get parameter: " + ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions + ".  Using default (" + fingerprintLoginOption + ")", e);
            logger.error("Unable to get parameter: " + ParameterConstantsIfc.OPERATORID_ManualEntryRequiresPassword + ".  Using default (" + manualLoginEntryReqPwdParm + ")", e);
        }

        if (ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_NO_FINGERPRINT.equals(fingerprintLoginOption))
        {
            UIUtilities.layoutComponent(this, loginIDLabel,  loginIDField,  0, 0, false);
            if (manualLoginEntryReqPwdParm)
            {
                UIUtilities.layoutComponent(this, passwordLabel, passwordField, 0, 1, false);
            }
        }
        else if (ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_ID_AND_FINGERPRINT.equals(fingerprintLoginOption))
        {
            UIUtilities.layoutComponent(this, loginIDLabel,     loginIDField, 0, 0, false, true);
            UIUtilities.layoutComponent(this, andLabel,         null,         2, 0, false, true);
            UIUtilities.layoutComponent(this, fingerprintLabel, null,         3, 0, false, true);
        }
        else if (ParameterConstantsIfc.OPERATORID_FingerprintLoginOptions_FINGERPRINT_ONLY.equals(fingerprintLoginOption))
        {
            UIUtilities.layoutComponent(this, fingerprintLabel, null,          0, 0, false);
        }

        loginIDField.setLabel(loginIDLabel);
        passwordField.setLabel(passwordLabel);
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        loginIDField = uiFactory.createAlphaNumericField("loginIDField", "1", "10", "22", false);
        loginIDField.setRequired(false);
        passwordField = uiFactory.createAlphaNumericPasswordField("passwordField", "1", "22");
        passwordField.setRequired(false);

    }

    /**
     * Initializes the labels.
     */
    protected void initializeLabels()
    {
        loginIDLabel = uiFactory.createLabel("LoginIDLabel", "LoginID", null, UI_LABEL);
        passwordLabel = uiFactory.createLabel("PasswordLabel", "Password", null, UI_LABEL);

        andLabel = uiFactory.createLabel("AndLabel", "+", null, UI_LABEL);

        Image fingerprintImage = UIUtilities.getImage("images/fingerprint.gif", this);
        ImageIcon fingerprintIcon = new ImageIcon(fingerprintImage);
        fingerprintLabel = uiFactory.createLabel("", "", fingerprintIcon, UI_LABEL);
    }

    /**
     * Updates the model from the screen.
     */
    @Override
    public void updateModel()
    {
        if (beanModel instanceof LoginBeanModel)
        {
            LoginBeanModel model = (LoginBeanModel) beanModel;

            model.setLoginID(loginIDField.getText());
            model.setPassword(Util.charArrayToByteArray(passwordField.getPassword(), EmployeeIfc.PASSWORD_CHARSET));
        }
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    @Override
    protected void updateBean()
    {
        if (beanModel instanceof LoginBeanModel)
        {
            // get model
            LoginBeanModel model = (LoginBeanModel)beanModel;

            loginIDField.setText(model.getLoginID());
            setupComponent(loginIDField, true, true);
            
            passwordField.setText("");
            setupComponent(passwordField, true, true);
        }
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    protected void setupComponent(JComponent field, boolean isEditable, boolean isVisible)
    {
        if (field instanceof ValidatingFieldIfc)
        {
            ((ValidatingFieldIfc)field).getLabel().setVisible(isVisible);
        }

        if (field instanceof JTextField)
        {
            ((JTextField)field).setEditable(isEditable);
        }
        field.setFocusable(isEditable);
        field.setVisible(isVisible);
    }

    /**
     * Overridden to help make sure the {@link #loginIDField} gets focus
     * whether in the main screen or in a dialog.
     *
     * @see javax.swing.JComponent#addNotify()
     */
    @Override
    public void addNotify()
    {
        super.addNotify();
        resetFocus();
    }

    /* (non-Javadoc)
     * @see com.extendyourstore.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        if (getComponents().length == 0)
        {
            initLayout();
        }

        loginIDField.addFocusListener(this);
        passwordField.addFocusListener(this);
        resetFocus();
    }

    /**
     * Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        loginIDField.removeFocusListener(this);
        passwordField.removeFocusListener(this);
    }

    /**
     * Requests focus on parameter value name field if visible is true.
     *
     * @param visible true if setting visible, false otherwise
     */
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if (visible && !errorFound())
        {
            resetFocus();
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#windowGainedFocus(java.awt.event.WindowEvent)
     */
    @Override
    public void windowGainedFocus(WindowEvent e)
    {
        logger.debug("Window with LoginBean gaining focus...");
        resetFocus();
        super.windowGainedFocus(e);
    }

    /**
     * Resets the focus to the {@link #loginIDField}.
     */
    protected void resetFocus()
    {
        SwingUtilities.invokeLater(new Runnable()
        {
           public void run()
           {
               if (currentComponent == null)
               {
                   setCurrentFocus(loginIDField);
               }
               loginIDField.requestFocusInWindow();
           }
        });
    }

    /**
     * Update property fields.
     */
    protected void updatePropertyFields()
    {
        loginIDLabel.setText(retrieveText("LoginID", loginIDLabel));
        passwordLabel.setText(retrieveText("Password", passwordLabel));
        andLabel.setText(retrieveText("And", andLabel));
        loginIDField.setLabel(loginIDLabel);
        passwordField.setLabel(passwordLabel);
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    @SuppressWarnings("deprecation")
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        LoginBean bean = new LoginBean();
        bean.loginIDField.setText("testLoginId");
        System.out.println("1: " + bean.loginIDField.getText());
        bean.passwordField.setText("testPassword");
        System.out.println("2: " + bean.passwordField.getPassword().toString());

        UIUtilities.doBeanTest(bean);
    }
}
