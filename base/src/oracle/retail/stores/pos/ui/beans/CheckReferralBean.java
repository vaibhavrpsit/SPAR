/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CheckReferralBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/06/10 - use default locale when displaying currency
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   5    I18N_P2    1.3.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *        alphanumerice fields for I18N purpose
 *   4    360Commerce 1.3         8/2/2007 8:59:28 PM    Ashok.Mondal    CR
 *        28042 :Display correct formatted check amount on check referral
 *        screen.
 *   3    360Commerce 1.2         3/31/2005 4:27:26 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:20:11 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:09:58 PM  Robert Pearse
 *
 *  Revision 1.5  2004/07/17 19:21:23  jdeleau
 *  @scr 5624 Make sure errors are focused on the beans, if an error is found
 *  during validation.
 *
 *  Revision 1.4  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/16 17:15:16  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.3   Nov 25 2003 14:22:32   bwf
 * Revert back to old bean.
 * Resolution for 3429: Check/ECheck Tender
 *
 *    Rev 1.1   Sep 10 2003 15:24:34   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:09:42   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Sep 03 2002 16:05:00   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 14 2002 18:16:54   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   02 May 2002 08:52:02   baa
 * ils
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.2   02 May 2002 08:34:06   baa
 * use currency instead of big decimal
 * Resolution for POS SCR-1624: Spanish translation
 *
 *    Rev 1.1   15 Apr 2002 09:33:28   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:53:40   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 01 2002 10:02:54   mpm
 * Internationalization of tender-related screens
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
// Java imports
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//------------------------------------------------------------------------------
/**
 *    Contains the visual presentation for Check Referral Information.
 */
//------------------------------------------------------------------------------
public class CheckReferralBean extends ValidatingBean
{
    // Indices for the fields
    protected static final int AUTH_RESPONSE   = 0;
    protected static final int REF_PHONE_NUM   = AUTH_RESPONSE + 1;
    protected static final int CHECK_AMOUNT    = REF_PHONE_NUM + 1;
    protected static final int IF_APPROVED     = CHECK_AMOUNT + 1;
    protected static final int APPROVAL_CODE   = IF_APPROVED + 1;
    protected static final int IF_NOT_APPROVED = APPROVAL_CODE + 1;
    protected static final int MAX_FIELDS      = IF_NOT_APPROVED + 1; //add one because of 0 index!

    // Array of text for the labels for the fields
    protected static String labelText[] =
    {
        "Authorization Response:",
        "(1) Call the following referral number for authorization.",
        "Check Amount:",
        "(2) If approved, enter the authorization code below.",
        "Approval Code:",
        "(3) If NOT approved, press declined."
    };

    // Array of tags for the labels for the fields
    protected static String labelTags[] =
    {
        "AuthorizationResponseLabel",
        "CallPromptLabel",
        "CheckAmountLabel",
        "ApprovalCodePromptLabel",
        "ApprovalCodeLabel",
        "NotApprovedPromptLabel"
    };

    // Array of labels
    protected JLabel[] fieldLabels = new JLabel[MAX_FIELDS];

    // Authorization response field
    protected JTextField authResponseField       = null;

    // Referral phone number field
    protected JTextField refPhoneNumberField     = null;

    // Check amount field
    protected CurrencyTextField checkAmountField = null;

    // Approval code field
    protected AlphaNumericTextField approvalCode = null;

    // The bean model
    protected CheckReferralBeanModel beanModel = null;

    // Flag indicating the model has changed
    protected boolean dirtyModel = true;

    //--------------------------------------------------------------------------
    /**
     *    Default Constructor.
     */
    public CheckReferralBean()
    {
        super();
        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *    Called before this bean is shown or hidden.
     */
    public void setVisible(boolean flag)
    {
        super.setVisible(flag);

        if(flag && !errorFound())
        {
            setCurrentFocus(approvalCode);
        }
    }

    //--------------------------------------------------------------------------
    /**
      *    Activates this bean.
      */
     public void activate()
     {
         super.activate();
         approvalCode.addFocusListener(this);
     }

    //--------------------------------------------------------------------------
    /**
     *    Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        approvalCode.setText("");
        approvalCode.removeFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     * Initializes this bean.
     */
    protected void initialize()
    {
        setName("CheckReferralBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();

    }

    //--------------------------------------------------------------------------
    /**
     *    Initialize the display components.
     */
    protected void initComponents()
    {
        for(int i=0; i<MAX_FIELDS; i++)
        {
            fieldLabels[i] = uiFactory.createLabel(labelText[i], null, UI_LABEL);
            fieldLabels[i].setHorizontalAlignment(JLabel.LEFT);
        }

        authResponseField = uiFactory.createTextField("authResponseField",40);
        authResponseField.setEnabled(false);
        authResponseField.setHorizontalAlignment(JTextField.CENTER);

        refPhoneNumberField = uiFactory.createTextField("accountNumberField",16);
        refPhoneNumberField.setEnabled(false);
        refPhoneNumberField.setHorizontalAlignment(JTextField.CENTER);

        checkAmountField = uiFactory.createCurrencyField("checkAmountField", "false", "false", "false");
        checkAmountField.setEnabled(false);

        approvalCode = uiFactory.createAlphaNumericField("approvalCode", "1", "15", false);
    }

    //--------------------------------------------------------------------------
    /**
     *    Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor    = GridBagConstraints.CENTER;
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.gridx     = 0;
        gbc.weighty   = 0.1;
        gbc.gridwidth = 2;

        add(fieldLabels[AUTH_RESPONSE], gbc);
        add(authResponseField, gbc);
        add(fieldLabels[REF_PHONE_NUM], gbc);
        add(refPhoneNumberField, gbc);

        gbc.gridwidth = 1;
        add(fieldLabels[CHECK_AMOUNT], gbc);

        gbc.gridwidth = 2;
        add(fieldLabels[IF_APPROVED], gbc);

        gbc.gridwidth = 1;
        add(fieldLabels[APPROVAL_CODE], gbc);

        gbc.gridwidth = 2;
        add(fieldLabels[IF_NOT_APPROVED], gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        add(checkAmountField, gbc);

        gbc.gridy = 6;
        add(approvalCode, gbc);
    }

    //--------------------------------------------------------------------------
    /**
     *    Gets the model for the current settings of this bean.
     */
    public void updateModel()
    {
        beanModel.setApprovalCode(approvalCode.getText());
    }

    //--------------------------------------------------------------------------
    /**
     *    Sets the model for the current settings of this bean.
     *    @param model the model for the current values of this bean
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException(
                "Attempt to set CheckEntryBeanModel to null");
        }
        else
        {
            if (model instanceof CheckReferralBeanModel)
            {
                beanModel = (CheckReferralBeanModel) model;
                dirtyModel = true;
                updateBean();
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     * Updates the bean if the model has changed.
     */
    public void updateBean()
    {
            authResponseField.setText(beanModel.getAuthResponse());
            refPhoneNumberField.setText(beanModel.getReferralNumber());
            //checkAmountField.setValue(beanModel.getCheckAmount());
            String checkAmount = getCurrencyService().formatCurrency(beanModel.getCheckAmount(), getDefaultLocale());
            //checkAmountField is non editable on call referral screen
            //pass this flag value to display the correct formatted check amount on both editable and non-editable fields
            checkAmountField.setText(checkAmount, false);
            approvalCode.setText(beanModel.getApprovalCode());
    }

    //--------------------------------------------------------------------------
    /**
     *    Return the POSBaseBeanModel.
     *    @return posBaseBeanModel as POSBaseBeanModel
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
       return beanModel;
    }

    //---------------------------------------------------------------------------
    /**
     *  Update property fields.
     */
    //---------------------------------------------------------------------------
    protected void updatePropertyFields()
    {
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i],
                                                fieldLabels[i]));
        }

        checkAmountField.setLabel(fieldLabels[CHECK_AMOUNT]);
        approvalCode.setLabel(fieldLabels[APPROVAL_CODE]);
    }

    //--------------------------------------------------------------------------
    /**
     *    Returns default display string.
     *    @return String representation of object
     */
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) +
                          "(Revision " + getRevisionNumber() +
                          ") @" + hashCode());
    }

    //--------------------------------------------------------------------------
    /**
     *    Retrieves the Team Connection revision number. <P>
     *    @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //--------------------------------------------------------------------------
    /**
     *    Main entry point for testing.
     *    @param args String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        CheckReferralBean bean = new CheckReferralBean();

        CheckReferralBeanModel beanModel = new CheckReferralBeanModel();

        beanModel.setAuthResponse("Please Call the Following number for Visa Approval");
        beanModel.setReferralNumber("1-800-762-8756");
        beanModel.setCheckAmount(DomainGateway.getBaseCurrencyInstance("50.00"));

        bean.setModel(beanModel);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
