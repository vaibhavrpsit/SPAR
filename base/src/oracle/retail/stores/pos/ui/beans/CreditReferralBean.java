/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CreditReferralBean.java /rgbustores_13.4x_generic_branch/7 2011/10/05 13:39:19 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     10/04/11 - Fixed issues with gift card referral.
 *    asinton   09/29/11 - Added Card Type to the Call Referral Screen, made it
 *                         depended upon credit tender only.
 *    asinton   09/07/11 - make amount field non-editable.
 *    blarsen   08/12/11 - Setting ChargeAmount in updateModel() since it is
 *                         editable.
 *    asinton   06/27/11 - Added Call Referral UI and flow to the tender
 *                         authorization tour
 *    cgreene   06/15/11 - implement gift card for servebase and training mode
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/06/10 - use default locale when displaying currency
 *    abondala  01/03/10 - update header date
 *    crain     10/22/09 - Forward Port: - OFFLINE APPROVAL SCREEN DOES NOT
 *                         HAVE AMOUNT - FIELD IS BLANK
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         8/3/2007 10:42:04 AM   Ashok.Mondal    CR
 *        28044 :Display correct formatted charge amount on credit referral
 *        screen.
 *   3    360Commerce 1.2         3/31/2005 4:27:32 PM   Robert Pearse
 *   2    360Commerce 1.1         3/10/2005 10:20:27 AM  Robert Pearse
 *   1    360Commerce 1.0         2/11/2005 12:10:14 PM  Robert Pearse
 *
 *  Revision 1.5  2004/07/17 19:21:23  jdeleau
 *  @scr 5624 Make sure errors are focused on the beans, if an error is found
 *  during validation.
 *
 *  Revision 1.4  2004/03/16 17:15:22  build
 *  Forcing head revision
 *
 *  Revision 1.3  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:27  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 10 2003 15:26:08   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:09:50   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 14 2002 18:16:58   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:55:52   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:33:32   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:54:38   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 01 2002 10:02:56   mpm
 * Internationalization of tender-related screens
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.manager.payment.AuthorizationConstantsIfc.TenderType;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Contains the visual presentation for Credit Referral Information
 *
 */
@SuppressWarnings("serial")
public class CreditReferralBean extends ValidatingBean
{
    /** Constants for fields indices */
    protected static final int AUTH_RESPONSE   = 0;
    protected static final int CALL_FOR_AUTH   = 1;
    protected static final int MERCHANT_NUMBER = 2;
    protected static final int CHARGE_AMOUNT   = 3;
    protected static final int APPROVAL_CODE   = 4;
    protected static final int CARD_TYPE       = 5;
    protected static final int MAX_FIELDS      = 6; //add one because of 0 index!

    /** Array of label text */
    protected static String[] labelText =
    {
        "Authorization Response:",
        "Call for authorization:",
        "Merchant Number:",
        "Charge Amount:",
        "Approval Code:",
        "Card Type:"
    };

    /** Array of tags for the labels for the fields */
    protected static String[] labelTags =
    {
        "AuthorizationResponseLabel",
        "CallPromptLabel",
        "MerchantNumberLabel",
        "ChargeAmountLabel",
        "ApprovalCodeLabel",
        "CardTypeLabel"
    };

    /** Array of field labels */
    protected JLabel[] fieldLabels = new JLabel[MAX_FIELDS];

    /** Authorization response field */
    protected JTextField authResponseField = null;

    /** JTextArea for Call Referral Numbers */
    protected JTextArea callReferralList = null;

    /** Field identifying the merchant number */
    protected JTextField merchantNumberField = null;

    /** Charge amount field */
    protected CurrencyTextField chargeAmountField = null;

    /** Approval code field */
    protected AlphaNumericTextField approvalCode = null;

    /** Card sub type combo box */
    protected ValidatingComboBox cardType = null;

    /** The bean model */
    protected CreditReferralBeanModel beanModel = null;

    /** Flag indicating the model has changed */
    protected boolean dirtyModel = false;

    /**
     * Default Constructor.
     */
    public CreditReferralBean()
    {
        initialize();
    }

    /**
     * Activates the bean.
     */
    @Override
    public void activate()
    {
        super.activate();
        approvalCode.addFocusListener(this);
    }

    /**
     * Called before this bean is shown or hidden.
     */
    @Override
    public void setVisible(boolean value)
    {
        super.setVisible(value);
        if (value && !errorFound())
        {
            setCurrentFocus(approvalCode);
        }
    }

    /**
     * Deactivates this bean.
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        approvalCode.setText("");
        approvalCode.removeFocusListener(this);
    }

    /**
     * Initializes this bean.
     */
    protected void initialize()
    {
        setName("CreditReferralBean");

        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * Initialize the display components.
     */
    protected void initComponents()
    {
        for(int i=0; i<MAX_FIELDS; i++)
        {
            fieldLabels[i] = uiFactory.createLabel(labelText[i], labelText[i], null, UI_LABEL);
        }
        fieldLabels[AUTH_RESPONSE].setHorizontalAlignment(JLabel.CENTER);

        authResponseField = new JTextField();
        authResponseField.setColumns(40);
        authResponseField.setName("authResponseField");
        authResponseField.setEnabled(false);
        authResponseField.setHorizontalAlignment(JTextField.CENTER);

        callReferralList =
            uiFactory.createConstrainedTextAreaField(
                    "callReferralList",
                    "0",
                    "950",
                    "50",
                    "false",
                    "false");
        callReferralList.setEnabled(false);
        callReferralList.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));

        merchantNumberField = new JTextField();
        merchantNumberField.setColumns(30);
        merchantNumberField.setName("merchantField");
        merchantNumberField.setEnabled(false);

        chargeAmountField = uiFactory.createCurrencyField("chargeAmountField", "false", "false", "false");
        chargeAmountField.setEnabled(false);
        approvalCode = uiFactory.createAlphaNumericField("approvalCode", "1", "15", "21", false);

        cardType = uiFactory.createValidatingComboBox("cardType", "false");
    }

    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor    = GridBagConstraints.CENTER;
        gbc.fill      = GridBagConstraints.HORIZONTAL;
        gbc.gridx     = 0;
        gbc.gridy     = 0;
        gbc.weighty   = 0.1;
        gbc.gridwidth = 3;

        add(fieldLabels[AUTH_RESPONSE], gbc);
        gbc.gridy = 1;
        add(authResponseField, gbc);
        gbc.gridy = 2;
        add(fieldLabels[CALL_FOR_AUTH], gbc);

        gbc.fill = GridBagConstraints.BOTH;
        gbc.gridy = 3;
        gbc.insets.bottom = 10;
        add(callReferralList, gbc);

        gbc.insets.right = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0;
        gbc.gridy = 4;
        gbc.gridwidth = 1;
        gbc.insets.bottom = 0;
        add(fieldLabels[MERCHANT_NUMBER], gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(merchantNumberField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 1;
        add(fieldLabels[CHARGE_AMOUNT], gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(chargeAmountField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 6;
        gbc.gridwidth = 1;
        add(fieldLabels[APPROVAL_CODE], gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(approvalCode, gbc);

        gbc.gridx = 0;
        gbc.gridy = 7;
        gbc.gridwidth = 1;
        add(fieldLabels[CARD_TYPE], gbc);

        gbc.gridx = 1;
        gbc.gridwidth = 2;
        add(cardType, gbc);
    }

    /**
     * Gets the model for the current settings of this bean.
     */
    @Override
    public void updateModel()
    {
        beanModel.setApprovalCode(approvalCode.getText());
        beanModel.setChargeAmount(chargeAmountField.getCurrencyValue());
        if(TenderType.CREDIT.equals(beanModel.getTenderType()))
        {
            beanModel.setCardType(cardType.getSelectedItem().toString());
        }
    }

    /**
     * Sets the model for the current settings of this bean.
     *
     * @param model the model for the current values of this bean
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set CheckEntryBeanModel to null");
        }

        if (model instanceof CreditReferralBeanModel)
        {
            beanModel = (CreditReferralBeanModel) model;
            dirtyModel = true;
            updateBean();
        }
    }

    /**
     * Updates the bean if the model has changed.
     */
    @Override
    public void updateBean()
    {
        if (dirtyModel)
        {
            authResponseField.setText(beanModel.getAuthResponse());
            String[] list = beanModel.getCallReferralList();
            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < list.length; i++)
            {
                builder.append(list[i]).append("\n");
            }
            callReferralList.setText(builder.toString());

            merchantNumberField.setText(beanModel.getMerchantNumber());
            //Get formatted charge amount
            if (beanModel.getChargeAmount() != null)
            {
                String chargeAmount = getCurrencyService().formatCurrency(beanModel.getChargeAmount().abs(), getDefaultLocale());
                //chargeAmountField is non editable on call referral screen
                //pass this flag value to display the correct formatted charge amount on both editable and non-editable fields
                chargeAmountField.setText(chargeAmount, false);
            }
            approvalCode.setText(beanModel.getApprovalCode());

            String[] creditCardTypes = beanModel.getCreditCardTypes();
            ValidatingComboBoxModel comboBoxModel = new ValidatingComboBoxModel(creditCardTypes);

            // add clear selection
            comboBoxModel.add(0, "");

            // set the selectable data
            cardType.setModel(comboBoxModel);

            // if the tender type is credit
            if(TenderType.CREDIT.equals(beanModel.getTenderType()))
            {
                // set card type controls visible
                cardType.setVisible(true);
                fieldLabels[CARD_TYPE].setVisible(true);
                // and the selection
                int index = getSelectedCardTypeIndex(beanModel.getCardType());
                cardType.setSelectedIndex(index);
                // if card type is known make it non-editable
                if(index > 0)
                {
                    cardType.setEnabled(false);
                }
                else
                {
                    cardType.setEnabled(true);
                }
            }
            // for other tenders hide the card type controls
            else
            {
                cardType.setSelectedIndex(1);
                cardType.setVisible(false);
                fieldLabels[CARD_TYPE].setVisible(false);
            }
        }
        dirtyModel = false;
    }

    /**
     * Returns the index from the given card type value.
     * @param cardTypeValue
     * @return
     */
    protected int getSelectedCardTypeIndex(String cardTypeValue)
    {
        int returnValue = 0;
        String[] creditCardTypes = beanModel.getCreditCardTypes();
        for(int i = 0; i < creditCardTypes.length; i++)
        {
            if(creditCardTypes[i].equals(cardTypeValue))
            {
                // set the return value
                returnValue = i + 1;
                // break out of the loop
                i = creditCardTypes.length;
            }
        }
        return returnValue;
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
     * Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i],
                                                fieldLabels[i]));
        }

        chargeAmountField.setLabel(fieldLabels[CHARGE_AMOUNT]);
        approvalCode.setLabel(fieldLabels[APPROVAL_CODE]);
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

    // --------------------------------------------------------------------------
    /**
     * Main entry point for testing.
     *
     * @param args String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        CreditReferralBean bean = new CreditReferralBean();

        CreditReferralBeanModel beanModel = new CreditReferralBeanModel();

        beanModel.setAuthResponse("Please Call the Following number for Visa Approval");
        String[] list = {"one", "two", "three", "four", };
        beanModel.setCallReferralList(list);
        beanModel.setMerchantNumber("123456789");
        beanModel.setChargeAmount(DomainGateway.getBaseCurrencyInstance("50.00"));

        bean.setModel(beanModel);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
