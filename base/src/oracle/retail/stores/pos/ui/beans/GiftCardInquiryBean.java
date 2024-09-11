/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/GiftCardInquiryBean.java /rgbustores_13.4x_generic_branch/5 2011/08/30 17:34:32 asinton Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   08/30/11 - Show the truncated gift card number if available
 *    cgreene   07/26/11 - moved StatusCode to GiftCardIfc
 *    cgreene   07/26/11 - removed tenderauth and giftcard.activation tours and
 *                         financialnetwork interfaces.
 *    cgreene   05/27/11 - move auth response objects into domain
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    acadar    04/06/10 - use default locale when displaying currency
 *    abondala  01/03/10 - update header date
 *    miparek   12/18/08 - fixed 1226, POS: GC Inquiry work panel is not fully
 *                         Padded
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/27/2008 12:25:49 AM  Manikandan Chellapan
 *         CR#30967 Fixed Gift Card Inquiry receipt format errors. Code
 *         reviewed by anil kandru.
 *    3    360Commerce 1.2         3/14/2008 9:47:58 AM   Jogesh Panda
 *         Change the Gift card Inquiry verbiage bundle message
 *    2    360Commerce 1.1         7/11/2007 11:07:31 AM  Anda D. Cadar
 *         removed ISO currency code when using base currency
 *    1    360Commerce 1.0         5/24/2007 5:59:04 PM   Owen D. Horne
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.math.BigDecimal;

import javax.swing.JLabel;

import oracle.retail.stores.domain.utility.GiftCardIfc.StatusCode;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean is used to display information about Gift Cards. No user input.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/5 $ $EKW;
 */
public class GiftCardInquiryBean extends BaseBeanAdapter
{
    private static final long serialVersionUID = -7264261346922575821L;

    /** version number from revision system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/5 $ $EKW;";

    // label and field placeholder constants
    public static final int CARD_NUMBER = 0;
    public static final int CARD_AMOUNT = 1;
    public static final int CARD_AVAILABLE_BAL = 2;
    public static final int CARD_REMAINING_BAL = 3;
    public static final int MAX_FIELDS = 4;

    public static final String NOT_AVAILABLE = "not available";
    public static final String NOT_AVAILABLE_TAG = "NotAvailableLabel";
    public static final String NOT_APPLICABLE_TAG = "NotApplicable";

    public static final String[] labelText = { "Gift Card Number:", "Gift Card Amount:", "GC Available Balance:",
            "Remaining Balance:" };

    public static final String[] labelTags = { "GiftCardNumberLabel", "GiftCardAmountLabel", "GCAvailableBalance",
            "RemainingBalance" };

    /** array of labels */
    protected JLabel[] labels = null;

    /** array of display fields */
    protected JLabel[] fields = null;

    /** the bean model */
    protected GiftCardBeanModel beanModel = null;

    /**
     * Default constructor.
     */
    public GiftCardInquiryBean()
    {
        super();
        initialize();
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("GiftCardInquiryBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * Initialize the display components.
     */
    protected void initComponents()
    {

        labels = new JLabel[MAX_FIELDS];
        fields = new JLabel[MAX_FIELDS];

        for (int i = 0; i < MAX_FIELDS; i++)
        {
            labels[i] = uiFactory.createLabel(labelTags[i], labelText[i], null, UI_LABEL);
            fields[i] = uiFactory.createLabel(labelTags[i] + "Field", NOT_AVAILABLE, null, UI_LABEL);
        }
    }

    /**
     * Layout the components.
     */
    public void initLayout()
    {
        UIUtilities.layoutDataPanel(this, labels, fields);
    }

    /**
     * Sets the information to be shown by this bean.
     * 
     * @param model UIModelIfc
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set GiftCardBean model to null");
        }
        if (model instanceof GiftCardBeanModel)
        {
            beanModel = (GiftCardBeanModel)model;
            updateBean();
        }
    }

    /**
     * Update the bean if the model has changed.
     */
    protected void updateBean()
    {
        labels[CARD_NUMBER].setText("");
        labels[CARD_AMOUNT].setText("");
        labels[CARD_AVAILABLE_BAL].setText("");
        labels[CARD_REMAINING_BAL].setText("");

        fields[CARD_NUMBER].setText("");
        fields[CARD_AMOUNT].setText("");
        fields[CARD_AVAILABLE_BAL].setText("");
        fields[CARD_REMAINING_BAL].setText("");

        if (beanModel.getGiftCardNumber() != null)
        {
            labels[CARD_NUMBER].setText(retrieveText(labelTags[CARD_NUMBER], labelText[CARD_NUMBER]));
            fields[CARD_NUMBER].setText(beanModel.getGiftCardNumber());
        }
        
        if ((beanModel.getGiftCardStatus() != null))
        {
            if (beanModel.getGiftCardStatus() == StatusCode.Reload ||
                beanModel.getGiftCardStatus() == StatusCode.Active)
            {
                if (beanModel.getGiftCardAmount() != null)
                {
                    // I18N change - do not display ISO currency code for base
                    // currencies
                    String amount = getCurrencyService().formatCurrency(beanModel.getGiftCardAmount(),
                            getDefaultLocale());
                    fields[CARD_AMOUNT].setText(amount);
                    labels[CARD_AMOUNT].setText(retrieveText(labelTags[CARD_AMOUNT], labelText[CARD_AMOUNT]));
                }
            }
            else
            // inquiry, return any status, if fail, the status is empty string
            {
                if (beanModel.getGiftCardAmount() != null)
                {
                    labels[CARD_AMOUNT].setText(retrieveText(labelTags[CARD_AVAILABLE_BAL],

                    labelText[CARD_REMAINING_BAL]));
                    // I18N change - do not display ISO currency code for base
                    // currencies
                    String amount = getCurrencyService().formatCurrency(beanModel.getGiftCardAmount(),
                            getDefaultLocale());
                    fields[CARD_AMOUNT].setText(amount);
                }
            }
        }
        else
        {
            // activation fail, request reenter gift card number.
            // show gift card amount at this time.
            if (beanModel.getGiftCardAmount() != null)
            {
                // I18N change - do not display ISO currency code for base
                // currencies
                String amount = getCurrencyService().formatCurrency(beanModel.getGiftCardAmount(), getDefaultLocale());
                fields[CARD_AMOUNT].setText(amount);
                labels[CARD_AMOUNT].setText(retrieveText(labelTags[CARD_AMOUNT], labelText[CARD_AMOUNT]));
            }
        }
    }

    /**
     * Gets the POSBaseBeanModel associated with this bean.
     * 
     * @return the POSBaseBeanModel associated with this bean.
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Update property fields.
     */
    protected void updatePropertyFields()
    {
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            labels[i].setText(retrieveText(labelTags[i], labels[i]));

            // check for not available
            if (Util.isObjectEqual(fields[i].getText(), NOT_AVAILABLE))
            {
                fields[i].setText(retrieveText(NOT_AVAILABLE_TAG, fields[i]));
            }
        }
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class: GiftCardInquiryBean (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
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

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        GiftCardBeanModel model = new GiftCardBeanModel();
        model.setGiftCardNumber("20020012");
        model.setGiftCardInitialBalance(new BigDecimal("100.00"));
        model.setGiftCardAmount(new BigDecimal("49.99"));
        model.setGiftCardStatus(StatusCode.Active);

        GiftCardInquiryBean bean = new GiftCardInquiryBean();
        bean.setModel(model);

        UIUtilities.doBeanTest(bean);
    }
}
