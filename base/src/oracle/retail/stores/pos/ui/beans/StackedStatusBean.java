/* ===========================================================================
* Copyright (c) 2011, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header:    
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    abonda 06/02/14 - mpos notifications distribution
 *    abonda 05/28/14 - notificaitons available indicator message to registers
 *    vbongu 09/21/12 - to remove border of the main panel
 *    cgreen 10/27/11 - refactored StackedStatusBean to extend StatusBean
 *    icole  08/29/11 - Use a singleton for OnlineStatusContainer as multiple
 *                      instances resulted in erroneous status.
 *    vtemke 07/07/11 - Fixed transaction status update problem
 *    blarse 06/30/11 - SIGNATURE_CAPTURE and PIN_PAD status were replaced with
 *                      FINANCIAL_NETWORK status as part of the advance payment
 *                      foundation feature.
 *    cgreen 06/17/11 - make background transparent so training mode color can
 *                      show through
 *    cgreen 05/26/11 - add class header and formatting
 *    rrkohl 05/19/11 - pos ui quickwin
 *    rrkohl 05/06/11 - formatting
 *    rrkohl 05/05/11 - POS UI quickwin
 *    rrkohl 05/05/11 - POS UI quickwin
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

import javax.swing.JLabel;
import javax.swing.JPanel;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.config.bundles.BundleConstantsIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.popup.PopupMessageManagerIfc;

/**
 * This bean is the presentation of the status area; it contains information
 * such as the Cashier, Operator, and Customer Names; the online/offline state,
 * time and register number.
 * 
 * @since 13.4
 */
public class StackedStatusBean extends StatusBean
{

    private static final long serialVersionUID = 5516449647001722617L;

    /** The sub panels that logically groups and "stack" the widgets. */
    protected JPanel transactionInfoSubPanel = null;
    protected JPanel statusSubPanel = null;
    protected JPanel screenNameSubPanel = null;

    /**
     * Constructor
     */
    public StackedStatusBean()
    {
        super("StackedStatusBean");
    }

    /**
     * Create the widgets displayed in this bean.
     */
    @Override
    protected void initComponents()
    {
        super.initComponents();

        // this panel should see-through so it's corners are not visible in training mode
        setOpaque(false);
        setBorder(null);

        statusSubPanel = new JPanel();
        uiFactory.configureUIComponent(statusSubPanel, UI_PREFIX);

        screenNameSubPanel = new JPanel();
        uiFactory.configureUIComponent(screenNameSubPanel, UI_PREFIX);

        transactionInfoSubPanel = new JPanel();
        uiFactory.configureUIComponent(transactionInfoSubPanel, UI_PREFIX);
    }


    /**
     * Layout the bean's UI in a "stacked" formation using {@link GridBagLayout}.
     */
    @Override
    protected void initLayout()
    {
        setLayout(new GridBagLayout());
        GridBagConstraints gridBagConstraints;

        statusSubPanel.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.insets = new Insets(5, 5, 1, 5);
        statusField.setHorizontalAlignment(JLabel.LEFT);
        statusSubPanel.add(statusField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();

        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = new Insets(1, 5, 1, 5);
        registerField.setHorizontalAlignment(JLabel.LEFT);
        statusSubPanel.add(registerField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 0.8;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = new Insets(1, 5, 5, 5);
        dateField.setHorizontalAlignment(JLabel.LEFT);
        statusSubPanel.add(dateField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 0.0;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.gridwidth = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.NORTHWEST;
        add(statusSubPanel, gridBagConstraints);

        transactionInfoSubPanel.setLayout(new GridBagLayout());

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = new Insets(5, 5, 1, 0);
        customerLabel.setHorizontalAlignment(JLabel.RIGHT);
        transactionInfoSubPanel.add(customerLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = new Insets(5, 0, 1, 5);
        customerField.setHorizontalAlignment(JLabel.LEFT);
        transactionInfoSubPanel.add(customerField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = new Insets(1, 5, 1, 0);
        cashierLabel.setHorizontalAlignment(JLabel.RIGHT);
        transactionInfoSubPanel.add(cashierLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridy = 1;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = new Insets(1, 0, 1, 5);
        cashierField.setHorizontalAlignment(JLabel.LEFT);
        transactionInfoSubPanel.add(cashierField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = new Insets(1, 5, 5, 0);
        salesAssociateLabel.setHorizontalAlignment(JLabel.RIGHT);
        transactionInfoSubPanel.add(salesAssociateLabel, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 0.4;
        gridBagConstraints.weighty = 0.33;
        gridBagConstraints.insets = new Insets(1, 0, 5, 5);
        salesAssociateField.setHorizontalAlignment(JLabel.LEFT);
        transactionInfoSubPanel.add(salesAssociateField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 1;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.anchor = GridBagConstraints.NORTH;
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.gridheight = 2;
        gridBagConstraints.gridwidth = 1;
        add(transactionInfoSubPanel, gridBagConstraints);

        screenNameSubPanel.setLayout(new GridBagLayout());
        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.fill = GridBagConstraints.BOTH;
        gridBagConstraints.anchor = GridBagConstraints.CENTER;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.insets = new Insets(5, 5, 5, 5);
        screenNameField.setHorizontalAlignment(JLabel.LEFT);
        screenNameSubPanel.add(screenNameField, gridBagConstraints);

        gridBagConstraints = new GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 2;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        gridBagConstraints.fill = GridBagConstraints.HORIZONTAL;
        gridBagConstraints.anchor = GridBagConstraints.SOUTH;
        gridBagConstraints.gridheight = 3;
        gridBagConstraints.gridwidth = 2;
        add(screenNameSubPanel, gridBagConstraints);
    }

    /**
     * Overrides the super class method because this bean doesn't use the
     * {@link StatusBean#timeField}. Instead, the time is appended into the
     * date field.
     */
    @Override
    public void actionPerformed(ActionEvent e)
    {
        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        Date now = new Date();
        Locale defaultLocale = LocaleMap.getLocale(LocaleMap.DEFAULT);
        String sDate = dateTimeService.formatDate(now, defaultLocale, DateFormat.SHORT);
        String sTime = dateTimeService.formatTime(now, defaultLocale, DateFormat.SHORT);
        dateField.setText(sDate + " " + sTime);
        
        if(UIUtilities.getNotificationsAvailableStatus("POS") != null && 
                UIUtilities.getNotificationsAvailableStatus("POS").booleanValue())
        {
            if(dateField.isShowing())
            {
                PopupMessageManagerIfc popup = (PopupMessageManagerIfc)BeanLocator.getApplicationBean(PopupMessageManagerIfc.BEAN_KEY);
                popup.setOwner(dateField);
                String message = UIUtilities.retrieveText("StatusPanelSpec",
                        BundleConstantsIfc.POS_BUNDLE_NAME,
                        "NotificationsAvailableMessage", "Warning");
                popup.addMessage(message);
                UIUtilities.updateNotificationsAvailableStatus("POS", Boolean.FALSE);
            }
        }        
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        StackedStatusBean bean = new StackedStatusBean();
        UIUtilities.doBeanTest(bean);
    }
}