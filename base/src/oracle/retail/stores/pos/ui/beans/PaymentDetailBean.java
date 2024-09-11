/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PaymentDetailBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       12/16/09 - update balance due in the model only if layaway fee
 *                         is updated.
 *    asinton   07/17/09 - Checkin of merge from 13.1.1 branch.
 *    asinton   07/17/09 - XbranchMerge asinton_bug-8692518 from
 *                         rgbustores_13.1x_branch
 *    asinton   07/16/09 - Added null checks to layawayFee and payment in the
 *                         updateModel method to prevent a null pointer
 *                         exception in updateBean method.
 *    acadar    04/13/09 - cleanup
 *    acadar    04/13/09 - make layaway location required; refactor the way we
 *                         handle layaway reason codes
 *    cgreene   03/30/09 - implement printing of layaway location on receipt by
 *                         adding new location code to layaway object and
 *                         deprecating the old string
 *    abondala  03/01/09 - check if the location is empty
 *    abondala  02/27/09 - updated
 *    abondala  02/27/09 - updated
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *    vikini    02/23/09 - Enabling the Location field
 *    vikini    02/12/09 - Removing ASTERISK after Location Dropdown
 *    acadar    02/09/09 - use default locale for display of date and time
 *
 * ===========================================================================
 * $Log:
 *    17   I18N_P2    1.13.1.2    1/8/2008 2:56:48 PM    Sandy Gu        Set
 *         max length of constraied text field.
 *    16   I18N_P2    1.13.1.1    1/7/2008 3:52:27 PM    Maisa De Camargo CR
 *         29826 - Setting the size of the combo boxes. This change was
 *         necessary because the width of the combo boxes used to grow
 *         according to the length of the longest content. By setting the
 *         size, we allow the width of the combo box to be set independently
 *         from the width of the dropdown menu.
 *    15   I18N_P2    1.13.1.0    1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *         alphanumerice fields for I18N purpose
 *    14   360Commerce 1.13        10/9/2007 10:33:34 AM  Anda D. Cadar
 *         Externalized static text
 *    13   360Commerce 1.12        6/26/2007 2:26:55 PM   Anda D. Cadar   use
 *         setCurrencyValue() method of the CurrencyTextField when displaying
 *         currency amount
 *    12   360Commerce 1.11        5/11/2007 3:46:32 PM   Mathews Kochummen use
 *          locale's date format
 *    11   360Commerce 1.10        4/25/2007 8:51:30 AM   Anda D. Cadar   I18N
 *         merge
 *    10   360Commerce 1.9         10/25/2006 10:24:57 AM Charles D. Baker CR
 *         21233 - This re-enables the layaway location capture.
 *    9    360Commerce 1.8         10/24/2006 4:33:48 PM  Charles D. Baker CF
 *         21233 - Removed check in until it's detemined if it's causing some
 *         queueing exceptions.
 *    8    360Commerce 1.7         10/24/2006 4:15:55 PM  Charles D. Baker CR
 *         21233 - Adding back some layaway location functionality that is NOT
 *          related to inventory.
 *    7    360Commerce 1.6         5/12/2006 5:25:35 PM   Charles D. Baker
 *         Merging with v1_0_0_53 of Returns Managament
 *    6    360Commerce 1.5         5/4/2006 5:11:52 PM    Brendan W. Farrell
 *         Remove inventory.
 *    5    360Commerce 1.4         1/25/2006 4:11:35 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    4    360Commerce 1.3         12/13/2005 4:42:46 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:19 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:24:01 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:13:01 PM  Robert Pearse
 *: PaymentDetailBean.java,v $
 *
 *   Revision 1.8.2.1  2004/10/15 18:50:31  kmcbride
 *   Merging in trunk changes that occurred during branching activity
 *
 *   Revision 1.10  2004/10/12 20:03:59  bwf
 *   @scr 7318 Fixed layway delete.  Removed unecessary log to screens.
 *
 *   Revision 1.9  2004/10/12 16:38:51  mweis
 *   @scr 7012 Make common getters/setters for Inventory methods in preparation for Sale, Layaway, and Order sharing code.
 *
 *   Revision 1.8  2004/09/17 23:12:43  mweis
 *   @scr 7012 Make Layaway's screens comply to specs for Inventory integration.
 *
 *   Revision 1.7  2004/09/16 20:15:50  mweis
 *   @scr 7012 Correctly update the inventory counts when a layaway is picked up (completed).
 *
 *   Revision 1.6  2004/08/27 17:51:03  bvanschyndel
 *   Added check for inventory integration switch
 *
 *   Revision 1.5  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.4  2004/06/29 22:03:30  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.3.2.5  2004/06/28 14:49:14  aachinfiev
 *   Changed from location label to department location
 *
 *   Revision 1.3.2.4  2004/06/21 14:17:08  jeffp
 *   Removed unused imports
 *
 *   Revision 1.3.2.3  2004/06/07 16:27:07  aachinfiev
 *   Added ability to prompt for inventory location as part of inventory & pos
 *   integration requirements.
 *
 *   Revision 1.3.2.1  2004/06/03 13:12:38  aachinfiev
 *   Added inventoryLocation drop-down box
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Aug 29 2003 16:11:36   CSchellenger
 * Initial revision.
 *
 *    Rev 1.7   17 Aug 2003 22:27:26   baa
 * currency formatting issues
 *
 *    Rev 1.6   Jul 26 2003 12:46:06   sfl
 * When delete layaway, don't charge layaway fee again.
 * Resolution for POS SCR-3252: Layaway Delete is Layaway Fee and Delete Fee is not Correct in totals
 *
 *    Rev 1.5   Aug 28 2002 14:30:46   dfh
 * fix ui bugs
 * Resolution for POS SCR-1760: Layaway feature updates
 *
 *    Rev 1.4   Aug 27 2002 17:00:18   dfh
 * updates to better tender/refund a layaway
 * Resolution for POS SCR-1760: Layaway feature updates
 *
 *    Rev 1.3   27 Aug 2002 10:00:48   dfh
 * fix payment/refund screen - still needs updates...
 * Resolution for POS SCR-1760: Layaway feature updates
 *
 *    Rev 1.2   Aug 14 2002 18:18:22   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:34:24   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:53:32   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:56:52   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 06 2002 20:23:08   mpm
 * Externalized text for layaway screens.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Color;
import java.text.DateFormat;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.Document;



import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.DomainGateway;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean is used for displaying the Payment Detail screen or Refund Detail
 * screen based on the data from the PaymentDetailBeanModel.
 *
 * @see oracle.retail.stores.pos;ui.beans.PaymentDetailBeanModel
 */
public class PaymentDetailBean extends ValidatingBean implements DocumentListener
{
    private static final long serialVersionUID = -567773385625813626L;

    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    // field number constants
    public static final int LAYAWAY_NUM = 0;
    public static final int CUSTOMER = 1;
    public static final int EXP_DATE = 2;
    public static final int BALANCE_DUE = 3;
    public static final int AMOUNT_PAID = 4;
    public static final int LAYAWAY_FEE = 5;
    public static final int DELETE_FEE = 6;
    public static final int PAYMENT = 7;
    public static final int REFUND_DISPLAY = 8;
    public static final int LOCATION = 9;
    public static final int NUM_COMPONENTS = 10;

    /** array of label text */
    protected String[] labelText =
    {
        "Layaway Number:", "Customer:", "Expiration Date:",
        "Balance Due:", "Amount Paid:", "Layaway Fee:",
        "Deletion Fee:", "Payment:", "Refund:", "Location:"
    };
    /** array of label tags */
    protected String[] labelTags =
    {
        "LayawayNumberLabel", "CustomerLabel", "LayawayExpirationDateLabel",
        "BalanceDueLabel", "AmountPaidLabel", "LayawayFeeLabel",
        "DeletionFeeLabel", "PaymentLabel", "RefundLabel", "LocationLabel"
    };

    /** array of screen components */
    protected JComponent[] components = new JComponent[NUM_COMPONENTS];

    /** array of component labels */
    protected JLabel[] labels = new JLabel[labelText.length];

    /** the bean model */
    protected PaymentDetailBeanModel beanModel = new PaymentDetailBeanModel();

    /** zero string */
    protected static final String zero = "0.00";

    /**
     *    Default Constructor.
     */
    public PaymentDetailBean()
    {
        super();
    }

    /**
     * Activates the bean. Add listeners to layaway fee or delete fee.
     */
    @Override
    public void activate()
    {
        super.activate();

        if (beanModel.isNewLayawayFlag())
        {
            ((JTextField)components[LAYAWAY_FEE]).getDocument().addDocumentListener(this);
        }

        if (beanModel.isDeleteLayawayFlag())
        {
            ((JTextField)components[DELETE_FEE]).getDocument().addDocumentListener(this);
        }
    }

    /**
     * Deactivates the bean. Remove listeners to layaway fee or delete fee.
     */
    @Override
    public void deactivate()
    {
        super.deactivate();

        if (beanModel.isNewLayawayFlag())
        {
            ((JTextField)components[LAYAWAY_FEE]).getDocument().removeDocumentListener(this);
        }

        if (beanModel.isDeleteLayawayFlag())
        {
            ((JTextField)components[DELETE_FEE]).getDocument().removeDocumentListener(this);
        }
    }

    /**
     * Configures the class. Initialize the fields and labels.
     */
    @Override
    public void configure()
    {
        setName("PaymentDetails");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();

        for(int i=0; i<labelText.length; i++)
        {
            labels[i] = uiFactory.createLabel(labelText[i], labelText[i], null, UI_LABEL);
        }

        UIUtilities.layoutDataPanel(this, labels, components);
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        components[AMOUNT_PAID]     =   uiFactory.createCurrencyField("Amount Paid","true","false","false");
        ((JTextField)components[AMOUNT_PAID]).setEnabled(false);

        components[LAYAWAY_FEE]     =   uiFactory.createCurrencyField("Layaway Fee","true","false","false");
        components[DELETE_FEE]      =   uiFactory.createCurrencyField("Deletion Fee","true","false","false");

        components[LAYAWAY_NUM]     =   uiFactory.createAlphaNumericField("Layaway Number", "1", "20", false);
        ((AlphaNumericTextField)components[LAYAWAY_NUM]).setEnabled(false);

        components[CUSTOMER]        =   uiFactory.createConstrainedField("Customer", "1", "60", "30");
        ((JTextField)components[CUSTOMER]).setEnabled(false);

        components[EXP_DATE]        =   uiFactory.createEYSDateField("Experation Date");
        ((JTextField)components[EXP_DATE]).setEnabled(false);

        components[BALANCE_DUE]     =   uiFactory.createCurrencyField("Balance Due","true","false","false");


        ((JTextField)components[BALANCE_DUE]).setEnabled(false);

        components[PAYMENT]         =   uiFactory.createCurrencyField( "Payment","false","false","false");

        // used to display the refund, not editable
        components[REFUND_DISPLAY]  =   uiFactory.createCurrencyField("Refund","true","false","false");
        components[LOCATION]        =   uiFactory.createValidatingComboBox("Location", "false", "15");
        ((ValidatingComboBox)(components[LOCATION])).setRequired(true);
    }

    /**
     * Create a label.
     *
     * @return JLabel
     */
    protected JLabel makeLabel(String text)
    {
        JLabel label = uiFactory.createLabel(text, text, null, UI_LABEL);
        label.setForeground(Color.black);
        label.setHorizontalAlignment(SwingConstants.LEFT);
        label.setHorizontalTextPosition(SwingConstants.CENTER);
        return label;
    }

    /**
     * Configures one of the bean components. Sets the column size and editable
     * flag to false. Turns off negative values for currency fields. Disables
     * all fields.
     *
     * @param idx the index of the component in the component array
     * @param c the component
     * @deprecated release 5.5 field components have to be initialize by the
     *             UIfactory
     */
    protected void configureComponent(int idx, JComponent c, String name)
    {
        // if it's a text field, set the columns
        if(c instanceof JTextField)
        {
            ((JTextField)c).setColumns(17);
        }
        // if it's a currency field, set the negative allowed flag to false
        if(c instanceof CurrencyTextField)
        {
            ((CurrencyTextField)c).setNegativeAllowed(false);
            ((CurrencyTextField)c).setEmptyAllowed(false);
        }
        c.setEnabled(false);

        // add it to the component array
        components[idx] = c;
    }

    /**
     * Returns the base bean model.
     *
     * @return POSBaseBeanModel
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }


    /**
     * Updates the model from the screen. Sets the layaway creation fee, payment,
     * balance due, location, and deletion fee, if applicable.
     */
    @Override
    public void updateModel()
    {
       int selectedIndx = ((ValidatingComboBox)components[LOCATION]).getSelectedIndex();

       beanModel.setSelectedReasonCode(selectedIndx);
       /*
        * The layawayFee and payment fields on the Payment Detail screen are editable
        * and can be blanked out by the operator.  When this field is blank the beanModel
        * is updated with null in the layawayFee and payment fields which causes a
        * NullPointerException in updateBean method where the payment is calculated
        * from the layawayFee plus the minimumDownPayment.
        */
       CurrencyIfc layawayFee = ((CurrencyTextField)components[LAYAWAY_FEE]).getCurrencyValue();
       if(layawayFee != null)
       {
           beanModel.setLayawayFee(layawayFee);
           // balance due is tied to layaway fee. Only update balance due if layaway fee is updated.
           beanModel.setBalanceDue(((CurrencyTextField)components[BALANCE_DUE]).getCurrencyValue());
       }
       CurrencyIfc payment = ((CurrencyTextField)components[PAYMENT]).getCurrencyValue();
       if(payment != null)
       {
           beanModel.setPayment(payment);
       }
       beanModel.setDeletionFee(((CurrencyTextField)components[DELETE_FEE]).getCurrencyValue());
       beanModel.setRefund(((CurrencyTextField)components[REFUND_DISPLAY]).getCurrencyValue());
       beanModel.setAmountPaid(((CurrencyTextField)components[AMOUNT_PAID]).getCurrencyValue());
    }

    /**
     * Sets the model property value and updates the bean with the new values.
     *
     * @param model UIModelIfc the new value for the property.
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set PaymentDetailBean " +
                "model to null");
        }

        if (model instanceof PaymentDetailBeanModel)
        {
            beanModel = (PaymentDetailBeanModel)model;
            updateBean();
        }
    }

    /**
     * Updates the information displayed on the screen with the model's data.
     */
    @Override
    protected void updateBean()
    {
        ((JTextField)components[LAYAWAY_NUM]).setText(
            beanModel.getLayawayNumber());

        ((JTextField)components[CUSTOMER]).setText(
            beanModel.getCustomerName());

        DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
        String expDate = dateTimeService.formatDate(beanModel.getExpirationDate().dateValue(), LocaleMap.getLocale(LocaleMap.DEFAULT), DateFormat.SHORT);
        ((JTextField)components[EXP_DATE]).setText(expDate);

        ((CurrencyTextField)components[BALANCE_DUE]).setCurrencyValue(beanModel.getBalanceDue());
        ((CurrencyTextField)components[AMOUNT_PAID]).setCurrencyValue(beanModel.getAmountPaid());
        ((CurrencyTextField)components[LAYAWAY_FEE]).setCurrencyValue(beanModel.getLayawayFee());

        // make sure there is an amount to display before formatting the data
        if (beanModel.getDeletionFee() != null)
        {
            ((CurrencyTextField)components[DELETE_FEE]).setCurrencyValue(beanModel.getDeletionFee());
        }


        String selectedReason = beanModel.getSelectedReason();
        String[] externalizedLocation = new String[1];
        externalizedLocation[0] = selectedReason;


        Vector locationValues = beanModel.getReasonCodes();

        // If a payment is being made on a new layaway...
        if (beanModel.isNewLayawayFlag())
        {

            ((ValidatingComboBox)components[LOCATION]).setModel(
                new ValidatingComboBoxModel (locationValues));

            // Set payment to minimum payment plus layaway fee
            CurrencyIfc pay =
                beanModel.getMinimumDownPayment().add(beanModel.getLayawayFee());

            enableAndRequire(false, new int[]{DELETE_FEE});
            enableAndRequire(true, new int[]{LAYAWAY_FEE, PAYMENT});

            ((CurrencyTextField)components[PAYMENT]).setCurrencyValue(pay);
            ((JTextField)components[PAYMENT]).setVisible(true);
            labels[PAYMENT].setVisible(true);

            ((ValidatingComboBox)components[LOCATION]).setVisible(true);
            ((ValidatingComboBox)components[LOCATION]).setEnabled(true);
            labels[LOCATION].setVisible(true);

            ((JTextField)components[REFUND_DISPLAY]).setVisible(false);
            labels[REFUND_DISPLAY].setVisible(false);

            ((JTextField)components[DELETE_FEE]).setText(zero);

            ((ValidatingComboBox)components[LOCATION]).setEnabled(true);
            ((ValidatingComboBox)components[LOCATION]).setSelectedItem("");
        }
        else if (beanModel.isDeleteLayawayFlag()) // The layaway is being deleted
        {
            // the layaway fee is non refundable
            CurrencyIfc refund =
                beanModel.getAmountPaid().subtract(beanModel.getLayawayFee());

            // the delete fee must be taken away from the total also
            if (beanModel.getDeletionFee() != null)
            {
                refund = (refund.subtract(beanModel.getDeletionFee())).abs();
            }
            else
            {
                refund = refund.abs();
            }

            ((JTextField)components[PAYMENT]).setVisible(false);
            labels[PAYMENT].setVisible(false);

            ((CurrencyTextField)components[REFUND_DISPLAY]).setCurrencyValue(refund);
            ((JTextField)components[REFUND_DISPLAY]).setVisible(true);
            labels[REFUND_DISPLAY].setVisible(true);

            //I18N phase 2 - externalize static text
            ((ValidatingComboBox)components[LOCATION]).setModel(
                    new ValidatingComboBoxModel(externalizedLocation));



            enableAndRequire(true, new int[]{DELETE_FEE});
            enableAndRequire(false, new int[]{REFUND_DISPLAY, LAYAWAY_FEE, LOCATION, PAYMENT});
        }
        else if (beanModel.isPickupLayawayFlag()) // the layaway is being picked up - full payment required
        {
            enableAndRequire(false, new int[]{PAYMENT, DELETE_FEE, LAYAWAY_FEE, LOCATION});

            ((CurrencyTextField)components[PAYMENT]).setCurrencyValue(
                beanModel.getBalanceDue());

            ((JTextField)components[PAYMENT]).setVisible(true);
            labels[PAYMENT].setVisible(true);

            //I18N phase 2 - externalize static text
            ((ValidatingComboBox)components[LOCATION]).setModel(
                    new ValidatingComboBoxModel(locationValues));
            ((ValidatingComboBox)components[LOCATION]).setSelectedItem(externalizedLocation[0]);


            ((JTextField)components[REFUND_DISPLAY]).setVisible(false);
            labels[REFUND_DISPLAY].setVisible(false);

            ((JTextField)components[DELETE_FEE]).setText(zero);
        }
        else // If a payment is being made on an existing layaway...not initial payment
        {
            enableAndRequire(true, new int[]{PAYMENT});
            enableAndRequire(false, new int[]{DELETE_FEE, LAYAWAY_FEE, LOCATION});

            // Set payment to minimum payment plus layaway fee
            CurrencyIfc pay = DomainGateway.getBaseCurrencyInstance(zero);
            ((CurrencyTextField)components[PAYMENT]).setCurrencyValue(pay);

            ((JTextField)components[PAYMENT]).setVisible(true);
            labels[PAYMENT].setVisible(true);

          //I18N phase 2 - externalize static text
            ((ValidatingComboBox)components[LOCATION]).setModel(
                    new ValidatingComboBoxModel(locationValues));

            ((ValidatingComboBox)components[LOCATION]).setSelectedItem(externalizedLocation[0]);

            ((JTextField)components[REFUND_DISPLAY]).setVisible(false);
            labels[REFUND_DISPLAY].setVisible(false);

            ((JTextField)components[DELETE_FEE]).setText(zero);
        }
    }

    /**
     * Enables and sets the required status of a set of components.
     *
     * @param enable true to enable and set required, false otherwise
     * @param indices list of component indicies
     */
    protected void enableAndRequire(boolean enable, int[] indices)
    {
        for (int i = 0; i < indices.length; i++)
        {
            JComponent comp = components[indices[i]];

            if(comp instanceof ValidatingTextField)
            {
                ((ValidatingTextField)comp).setEnabled(enable);
                setFieldRequired(((ValidatingTextField)comp), enable);
            }
            else if(comp instanceof ValidatingComboBox)
            {
                comp.setEnabled(enable);
               // ((ValidatingComboBox)comp).setRequired(enable);
            }
            else if (comp instanceof JLabel)
            {
                ((JLabel)comp).setEnabled(enable);
            }
        }
    }

    /**
     * Implementation of DocumentListener interface. Does nothing.
     */
    public void changedUpdate(DocumentEvent e)
    {
    }

    /**
     * Implementation of DocumentListener interface. Recalculates the minimum
     * payment amount based on a change in the layaway fee.
     *
     * @param e a document event
     */
    public void insertUpdate(DocumentEvent e)
    {
        adjustPaymentField(e.getDocument());
    }

    /**
     * Implementation of DocumentListener interface. Recalculates the minimum
     * payment amount based on a change in the layaway fee.
     *
     * @param e a document event
     */
    public void removeUpdate(DocumentEvent e)
    {
        adjustPaymentField(e.getDocument());
    }

    /**
     * Adjusts the value in the payment field based on a change in the layaway
     * fee. Updates ther refund amount for a layaway delete.
     *
     * @param doc a swing text document
     */
    protected void adjustPaymentField(Document doc)
    {
        String newText;
        CurrencyIfc newValue = null;
        CurrencyIfc pay = null;
        CurrencyIfc balanceDue = null;

        if (beanModel.isDeleteLayawayFlag())
        {
        	if(((CurrencyTextField)components[LAYAWAY_FEE]).getCurrencyValue() == null)
        	{
        		newValue = DomainGateway.getBaseCurrencyInstance();
        	}
        	else
        	{
        		newText = ((CurrencyTextField)components[LAYAWAY_FEE]).getCurrencyValue().getStringValue();
        		newValue = DomainGateway.getBaseCurrencyInstance(newText);
        	}
            pay = beanModel.getAmountPaid().subtract(newValue);
            if(((CurrencyTextField)components[DELETE_FEE]).getCurrencyValue() == null)
            {
                newValue = DomainGateway.getBaseCurrencyInstance();
            }
            else
            {
                newText = ((CurrencyTextField)components[DELETE_FEE]).getCurrencyValue().getStringValue();
                newValue = DomainGateway.getBaseCurrencyInstance(newText);
            }
            pay = pay.subtract(newValue);

            if (pay.signum() == CurrencyIfc.NEGATIVE)
            {
                pay = DomainGateway.getBaseCurrencyInstance(zero);
            }
            ((CurrencyTextField)components[REFUND_DISPLAY]).setCurrencyValue(pay);
        }
        else
        {
        	if(((CurrencyTextField)components[LAYAWAY_FEE]).getCurrencyValue() == null)
        	{
        		newValue = DomainGateway.getBaseCurrencyInstance();
        	}
        	else
        	{
        		newText = ((CurrencyTextField)components[LAYAWAY_FEE]).getCurrencyValue().getStringValue();
        		newValue = DomainGateway.getBaseCurrencyInstance(newText);
        	}
            pay = beanModel.getMinimumDownPayment().add(newValue);
            balanceDue = beanModel.getBalanceDue().subtract(beanModel.getLayawayFee()).add(newValue);
            ((CurrencyTextField)components[PAYMENT]).setCurrencyValue(pay);
            ((CurrencyTextField)components[BALANCE_DUE]).setCurrencyValue(balanceDue);
        }
    }

    /**
     * Updates property-based fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        for (int i = 0; i < NUM_COMPONENTS; i++)
        {
            labels[i].setText(retrieveText(labelTags[i],
                                           labelText[i]));

        }

        // set field labels
        ((CurrencyTextField)components[LAYAWAY_FEE]).setLabel(labels[LAYAWAY_FEE]);
        ((CurrencyTextField)components[PAYMENT]).setLabel(labels[PAYMENT]);
        ((ValidatingComboBox)components[LOCATION]).setLabel(labels[LOCATION]);
        ((CurrencyTextField)components[REFUND_DISPLAY]).setLabel(labels[REFUND_DISPLAY]);
        ((CurrencyTextField)components[DELETE_FEE]).setLabel(labels[DELETE_FEE]);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    @Override
    public String toString()
    {
        // result string
        String strResult = new String("Class:  PaymentDetailBean (Revision " +
                                      getRevisionNumber() +
                                      ")" + hashCode());

        // pass back result
        return(strResult);
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
     * Entry point for testing.
     *
     * @param args command line parameters
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        PaymentDetailBean
            bean = new PaymentDetailBean();
            bean.configure();

        UIUtilities.doBeanTest(bean);
    }
}
