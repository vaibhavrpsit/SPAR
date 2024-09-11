/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OtherTenderDetailBean.java /main/20 2013/08/01 15:06:35 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  08/01/13 - Fixed issue where listModel was not added to
 *                         scrollPane correctly
 *    tksharma  04/03/13 - updateBean() added listModel.clear()
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    acadar    04/08/10 - merge to tip
 *    acadar    04/06/10 - use default locale for currency display
 *    acadar    04/06/10 - use default locale for currency, date and time
 *                         display
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    acadar    04/01/10 - use default locale for currency display
 *    abondala  01/03/10 - update header date
 *    sgu       02/02/09 - fix tab
 *    sgu       02/02/09 - allow double byte space for french locale
 *
 * ===========================================================================
 * $Log:
 *    14   I18N_P2    1.12.1.0    1/8/2008 2:56:48 PM    Sandy Gu        Set
 *         max length of constraied text field.
 *    13   360Commerce 1.12        8/21/2007 10:09:45 AM  Anda D. Cadar   CR
 *         28399: Fix for display of negative amounts
 *    12   360Commerce 1.11        8/7/2007 6:14:33 PM    Alan N. Sinton  CR
 *         28211: Modified the logic so that the comparison is done with like
 *         currencies.
 *    11   360Commerce 1.10        8/1/2007 5:51:27 PM    Ranjan X Ojha
 *         OtherTenderDetailBeanModel total set.
 *    10   360Commerce 1.9         5/30/2007 6:44:42 PM   Anda D. Cadar
 *         cleanup
 *    9    360Commerce 1.8         5/18/2007 9:18:15 AM   Anda D. Cadar   EJ
 *         and currency UI changes
 *    8    360Commerce 1.7         5/8/2007 11:32:28 AM   Anda D. Cadar
 *         currency changes for I18N
 *    7    360Commerce 1.6         4/25/2007 8:51:31 AM   Anda D. Cadar   I18N
 *         merge
 *    6    360Commerce 1.5         1/25/2006 4:11:34 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    5    360Commerce 1.4         1/22/2006 11:45:27 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    4    360Commerce 1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:29:15 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:55 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:55 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     9/19/2005 13:48:11     Jason L. DeLeau Make
 *         sure CurrencyTextFields can have a blank default value.
 *    3    360Commerce1.2         3/31/2005 15:29:15     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:23:55     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:55     Robert Pearse
 *
 *         Base-lining of 7.1_LA
 *    3    360Commerce1.2         3/31/2005 3:29:15 PM   Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:23:55 AM  Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:55 PM  Robert Pearse
 *: OtherTenderDetailBean.java,v $
 *
 *:
 *    5    .v710     1.2.2.1     10/24/2005 14:20:53    Charles Suehs   Merged
 *         from .v700 to fix CR 3965.
 *    4    .v710     1.2.2.0     10/20/2005 18:25:57    Charles Suehs   Merge
 *         from OtherTenderDetailBean.java, Revision 1.2.1.0
 *    3    360Commerce1.2         3/31/2005 15:29:15     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:23:55     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:12:55     Robert Pearse
 *
 *   Revision 1.5  2004/06/24 01:16:48  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Add Foreign currency detail count.
 *
 *   Revision 1.4  2004/06/17 22:36:28  dcobb
 *   @scr 4205 Feature Enhancement: Till Options
 *   Add foreign currency to tender detail count interface.
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
 *    Rev 1.0   Aug 29 2003 16:11:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   23 Jul 2003 01:01:36   baa
 * allow focus to switch between prompt response and work panel
 *
 *    Rev 1.1   Aug 14 2002 18:18:18   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:55:20   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:35:40   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:56:44   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 08 2002 11:00:12   mpm
 * Externalized text for poscount UI screens.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.math.BigDecimal;
import java.util.Enumeration;

import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceException;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyServiceLocator;
import oracle.retail.stores.common.utility.BigDecimalConstants;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.AddDeleteListener;
import oracle.retail.stores.pos.ui.behavior.ClearActionListener;
import oracle.retail.stores.pos.ui.behavior.ResponseTextListener;

/**
 * Allows the usr to enter non currency count detail.
 * 
 * @version $Revision: /main/20 $
 */
public class OtherTenderDetailBean extends BaseBeanAdapter implements AddDeleteListener
{
    private static final long serialVersionUID = -1671086991058062093L;

    /**
     * revision number supplied by Team Connection
     */
    public static final String revisionNumber = "$Revision: /main/20 $ $EKW;";

    protected static final String BUTTON_ADD_LABEL = "Add";
    protected static final int BUTTON_ADD_KEY = KeyEvent.VK_A;
    protected static final String BUTTON_DELETE_LABEL = "Delete";
    protected static final int BUTTON_DELETE_KEY = KeyEvent.VK_D;

    protected OtherTenderDetailBeanModel beanModel = new OtherTenderDetailBeanModel();
    // @deprecated as of release 5.5 redundant flag
    protected boolean isDirty                      = true;

    protected JLabel            totalLabel     = null;
    protected JList   amountsList    = null;
    protected ConstrainedTextField totalField     = null;
    protected ActionListener   defBtnListener  = null;
    protected KeyListener      defKeyListener  = null;
    protected ListDataListener defListListener = null;
    protected DefaultListModel listModel       = null;
    protected ResponseTextListener responseTextListener = null;
    protected ClearActionListener clearActionListener = null;
    protected static final String TOTAL_LABEL = "TotalLabel";
    protected static final String STRING_ZERO = "0";

    /**
    currency service reference
   **/
   protected static CurrencyServiceIfc currencyService = null;

    /**
        Constructs the bean. <P>
        <B>Pre-Condition(s)</B>
        <UL>
        <LI> None.
        </UL>
        <B>Post-Condition(s)</B>
        <UL>
        <LI> None.
        </UL>
    **/
     public OtherTenderDetailBean()
    {
        super();
    }

    /**
     *  Returns an instance of the CurrencyService
     * @return CurrencyServiceIfc
     */
    protected static CurrencyServiceIfc getCurrencyService()
    {
         if (currencyService == null)
        {
            currencyService = CurrencyServiceLocator.getCurrencyService();
        }
        return currencyService;
    }

    /**
     * Called when the panel is created.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>None.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>None.
     * </UL>
     **/
    public void configure()
    {
        setName("OtherTenderDetailBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setLayout(new GridBagLayout());

        listModel = new POSListModel();
        listModel.addListDataListener(getDefaultListListener());

        JScrollPane scrollPane = uiFactory.createSelectionList("scrollPane", "large");
        amountsList = (JList)scrollPane.getViewport().getView();
        amountsList.setModel(listModel);
        amountsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        amountsList.setName("amountsList");

        totalLabel = uiFactory.createLabel("Total $ :", null, UI_LABEL);
        totalField = uiFactory.createConstrainedField("TotalField", "1", "10", true);
        totalField.setEditable(false);
        totalField.setEnabled(false);

        GridBagConstraints constraints = uiFactory.getConstraints("DataEntryBean");
        constraints.insets = uiFactory.getInsets("defaultLabelTop");
        constraints.fill = GridBagConstraints.NONE;
        constraints.gridwidth = 2;
        add(scrollPane, constraints);

        constraints.gridwidth = 1;
        constraints.insets = uiFactory.getInsets("defaultLabelLeft");
        constraints.gridy = 1;
        add(totalLabel, constraints);

        constraints.insets = uiFactory.getInsets("defaultFieldRight");
        constraints.gridx = 1;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        add(totalField, constraints);
    }

    /**
     * Called when the panel is added to the screen.
     **/
    public void activate()
    {
        super.activate();
        calcTotals();
    }

    /**
     * Called when the panel is added to the screen.
     **/
    public void deactivate()
    {
        super.deactivate();
        listModel.clear();
        totalField.setText("");
    }

    /**
     * Update the screen with values from the model.
     **/
    protected void updateBean()
    {
        listModel.clear();
        CurrencyIfc[] amounts = beanModel.getTenderAmounts();
        if (amounts != null)
        {
            for (int i = 0; i < amounts.length; i++)
            {
                listModel.addElement(amounts[i].toFormattedString());
            }
        }
    }

    /**
     * Get data from the screen fields and puts it in the model.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>None.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>None.
     * </UL>
     * 
     * @return The model containing the data from the screen.
     **/
    public void updateModel()
    {
        CurrencyIfc[] amounts = new CurrencyIfc[listModel.getSize()];
        Enumeration list        = listModel.elements();
        int index                = 0;
        String amountString = "";
        BigDecimal dec = BigDecimalConstants.ZERO_AMOUNT;

        while(list.hasMoreElements())
        {
            amountString = (String)list.nextElement();
            dec = (BigDecimal)LocaleUtilities.parseCurrency(amountString, getDefaultLocale());
            amounts[index] = getCurrencyService().createCurrency(dec, beanModel.getTotal().getType());
            index++;
        }

        beanModel.setTenderAmounts(amounts);

        if(listModel.getSize() == 0)
        {
            // CurrencyTextField.getCurrencyValue() always returns instance of base currency or null
            BigDecimal tot = BigDecimalConstants.ZERO_AMOUNT;
            if (!(Util.isEmpty(totalField.getText())))
        	{
                tot = (BigDecimal)LocaleUtilities.parseCurrency(totalField.getText(), getDefaultLocale());
        	}
            // beanModel.total was previously set with the expected amount with the
            // relavent currency.  When resetting the beanModel.total we can make use
            // of the previously set currency type.  This will allow
            // ValidateEnteredTenderDetailAisle to compare against a currency amount
            // with the same currency type.
            CurrencyIfc enteredAmount =
                getCurrencyService().createCurrency(tot, beanModel.getTotal().getType());
            beanModel.setTotal(enteredAmount);
        }
        else
        {
            CurrencyIfc cd = (CurrencyIfc)beanModel.getTotal().clone();
            cd.setDecimalValue((BigDecimal)LocaleUtilities.parseCurrency(totalField.getText(), getDefaultLocale()));
            beanModel.setTotal(cd);
        }

    }

    /**
     * Move data from the model to the bean.
     * <P>
     * <B>Pre-Condition(s)</B>
     * <UL>
     * <LI>None.
     * </UL>
     * <B>Post-Condition(s)</B>
     * <UL>
     * <LI>None.
     * </UL>
     * 
     * @param the model containing data from the service.
     **/
    public void setModel(UIModelIfc model)
    {
        if (model==null)
        {
            throw new NullPointerException("Attempt to set OtherTenderDetailBean"
                                           + " to null");
        }
        if (model instanceof OtherTenderDetailBeanModel)
        {
            beanModel = (OtherTenderDetailBeanModel)model;
            updateBean();
        }
    }

    /**
     * Retrieves Retry/Continue key listener. Listener instantiated if not
     * already
     * 
     * @return KeyListener for Retry/Continue buttons
     */
    protected KeyListener getDefaultKeyListener()
    {
        if (defKeyListener == null)
        {
            defKeyListener = new KeyListener()
            {  
                /**
                 * Handles key-pressed event. <P>
                 * @param evt key event
                */
                public void keyPressed(KeyEvent evt)
                {
                    //these keys can oly be handled here
                    //not in the Key Typed function
                    switch (evt.getKeyCode())
                    {
                         //forward these to the list
                        case KeyEvent.VK_UP:
                        case KeyEvent.VK_PAGE_UP:
                        case KeyEvent.VK_DOWN:
                        case KeyEvent.VK_PAGE_DOWN:
                        {
                            amountsList.dispatchEvent(evt);
                            break;
                        }
                    }
                }

                /**
                 * Handles key-released event.
                 * <P>
                 * 
                 * @param evt key event
                 */
                            public void keyReleased(KeyEvent evt)
                {
                }

                            /**
                 * Handles key-typed event.
                 * <P>
                 * 
                 * @param evt key event
                 */
                            public void keyTyped(KeyEvent evt)
                {
    }
            };
        }
        return defKeyListener;
    }

    /**
     * The framework calls this method through a connection when the user
     * presses the Yes/No key on the local navigation bar.
     * 
     * @return java.awt.event.ActionListener a default button listener
     **/
    public void actionPerformed(ActionEvent evt)
    {
        if ((evt.getActionCommand()).equals(BUTTON_ADD_LABEL))
            doAddAction();
        else if ((evt.getActionCommand()).equals(BUTTON_DELETE_LABEL))
            doDeleteAction();
    }

    /**
      * This method adds the Currency amount from the bean to the list
      */
    protected void doAddAction()
    {
        try
        {
            CurrencyIfc value = (CurrencyIfc) beanModel.getTotal().clone();
            // use store locale for currency display
            String amount = "0";
            if (responseTextListener != null)
                amount = responseTextListener.getResponseText();

            if (amount != null && !(amount.equals("")))
            {
                BigDecimal amt = (BigDecimal) LocaleUtilities.parseCurrency(amount, getDefaultLocale());
                value.setDecimalValue(amt);

                listModel.addElement(value.toFormattedString());
                clearActionListener.actionPerformed(new ActionEvent(this, 0, ""));
            }
        }
        catch (java.lang.NumberFormatException e)
        {
            logger.warn("Number format exception occured");
        }

    }

    /**
     * This method removes the selected currency amount from the list
     */
    protected void doDeleteAction()
    {
        if (listModel.getSize() > 0)
            listModel.remove(amountsList.getSelectedIndex());
    }

    /**
      * Returns a singleton ListListener
      */
    protected ListDataListener getDefaultListListener()
    {
        if (defListListener == null)
        {
            defListListener = new ListDataListener()
            {
                public void intervalAdded(ListDataEvent e)
                {
                    calcTotals();
                }

                public void intervalRemoved(ListDataEvent e)
                {
                    calcTotals();
                }

                public void contentsChanged(ListDataEvent e)
                {
                    calcTotals();
                }
            };
        }
        return defListListener;
    }

    /**
      *  This method calculates the total of each value in the list
      */
    protected void calcTotals()
    {
        int size = listModel.getSize();
        if (listModel.getSize() == 0)
        {
            totalField.setText("");
        }
        else
        {

            CurrencyIfc cd = (CurrencyIfc) beanModel.getTotal().clone();
            cd.setStringValue(STRING_ZERO);
            String interimAmt = "0";
            BigDecimal dec = BigDecimalConstants.ZERO_AMOUNT;
            // use default locale for currency display
            for (int cnt = 0; cnt < size; cnt++)
            {
                interimAmt = (String) listModel.elementAt(cnt);
                try
                {
                    dec = getCurrencyService().parseCurrency(interimAmt, getDefaultLocale());
                }
                catch (CurrencyServiceException e)
                {
                    logger.error("Parse error for " + interimAmt, e);
                }
                CurrencyIfc newCurrency = getCurrencyService().createCurrency(dec, cd.getType());
                cd = cd.add(newCurrency);
            }

            String totalText = cd.toFormattedString();
            totalField.setText(totalText);
            if (amountsList.getSelectedIndex() == -1)
            {
                amountsList.setSelectedIndex(0);
            }
        }
    }

    /**
     * Adds (actually sets) the response text listener.
     * 
     * @Param listener the response text Listener
     **/
    public void addResponseTextListener(ResponseTextListener listener)
    {
        responseTextListener = listener;
    }

    /**
     * Removes the response text listener.
     * 
     * @Param listener the response text Listener
     **/
    public void removeResponseTextListener(ResponseTextListener listener)
    {
        responseTextListener = null;
    }

    /**
     * Adds (actually sets) the clear action listener.
     * 
     * @Param listener the clear action Listener
     **/
    public void addClearActionListener(ClearActionListener listener)
    {
        clearActionListener = listener;
    }

    /**
     * Removes the clear action listener.
     * 
     * @Param listener the clear action Listener
     **/
    public void removeClearActionListener(ClearActionListener listener)
    {
        clearActionListener = null;
    }

    /**
     * Updates property-based fields.
     **/
    protected void updatePropertyFields()
    { // begin updatePropertyFields()
        totalLabel.setText(retrieveText(TOTAL_LABEL, totalLabel));
        totalField.setLabel(totalLabel);
    } // end updatePropertyFields()

    /**
     * Retrieves the Team Connection revision number.
     * <P>
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Returns default display string.
     * <P>
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class: OtherTenderDetailBean (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }

    /**

    */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        final OtherTenderDetailBean bean = new OtherTenderDetailBean();
        bean.configure();
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
