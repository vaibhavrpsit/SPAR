/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/OrderStatusReportBean.java /main/3 2013/10/31 15:54:33 subrdey Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    subrdey   10/31/13 - Set start business date if it is deleted
 *    rgour     10/22/13 - putting currentComponent to null while selecting
 *                         something from ComboBox
 *    sgu       01/15/13 - add back order status report
 *    sgu       01/15/13 - add back order status report
 *    mchellap  08/12/11 - BUG#12615160 Remove Voided status from order status
 *                         report
 *    abhayg    08/09/10 - Added Method for setting the Focus on Order Status
 *                         Field
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *    acadar    04/22/09 - translate date/time labels
 *    blarsen   03/18/09 - Replaced status keys to use the array in
 *                         OrderConstantsIfc array. Changed code to
 *                         consistently use the status keys and NOT the
 *                         translated status strings. Other code relies on the
 *                         values returned by this class to match the values in
 *                         OrderConstantsIfc.ORDER_STATUS_DESCRIPTORS.
 *    mkochumm  02/12/09 - use default locale for dates
 *
 * ===========================================================================
 * $Log:
 *    5    I18N_P2    1.3.1.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR
 *         29826 - Setting the size of the combo boxes. This change was
 *         necessary because the width of the combo boxes used to grow
 *         according to the length of the longest content. By setting the
 *         size, we allow the width of the combo box to be set independently
 *         from the width of the dropdown menu.
 *    4    360Commerce 1.3         4/26/2007 3:15:21 PM   Mathews Kochummen use
 *          locale appropriate date label
 *    3    360Commerce 1.2         3/31/2005 4:29:14 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:23:53 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:12:54 PM  Robert Pearse
 *
 *   Revision 1.5  2004/08/24 22:19:52  jdeleau
 *   @scr 6931 Perform validation for OrderStatusReportBean
 *
 *   Revision 1.4  2004/08/24 15:02:17  jdeleau
 *   @scr 6910 Fix the displayable date from MM/dd/yyyy to MM/DD/YYYY
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
 *    Rev 1.1   Sep 16 2003 17:52:52   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:11:28   CSchellenger
 * Initial revision.
 *
 *    Rev 1.3   Jun 04 2003 11:03:08   RSachdeva
 * Order Status Initialization
 * Resolution for POS SCR-2672: Internationalization: When Order Report screen is displayed, a "< >" added to status
 *
 *    Rev 1.2   May 12 2003 10:53:08   bwf
 * Check if status is null before doing work with it.
 * Resolution for 2436: System hangs if "Printed" status of Order Status Report is selected for printing
 *
 *    Rev 1.1   Aug 07 2002 19:34:24   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:54:56   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:35:38   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:56:42   msg
 * Initial revision.
 *
 *    Rev 1.5   Mar 07 2002 20:44:42   mpm
 * Externalized text for report UI screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   Feb 11 2002 16:19:42   dfh
 * changed end busn date field to label, tabs between required fields
 * Resolution for POS SCR-885: Tab and arrow keys not functioning on Order Report screen
 *
 *    Rev 1.3   Feb 11 2002 14:10:42   dfh
 * makes start busn date required if status canceled/completed
 * Resolution for POS SCR-885: Tab and arrow keys not functioning on Order Report screen
 *
 *    Rev 1.2   Jan 22 2002 15:47:08   mpm
 * Set data to load combo box.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderConstantsIfc;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//----------------------------------------------------------------------------
/**
   Contains the visual presentation for Summary Report Information
   @version $Revision: /main/3 $
*/
//----------------------------------------------------------------------------
public class OrderStatusReportBean extends ValidatingBean
{
    /**  Revision number  */
    public static final String revisionNumber = "$Revision: /main/3 $";
    /**  Status row  */
    protected static final int STATUS_START = 0;
    /**  Start Business day row  */
    protected static final int BUSN_DATE_START = STATUS_START + 1;
    /**  End Business day row  */
    protected static final int BUSN_DATE_END   = BUSN_DATE_START + 1;
    /**  Maximum fields  */
    protected static final int MAX_FIELDS      = BUSN_DATE_END + 1; //add one because of 0 index!
    /**  Label text  */
    protected static final String labelText[] =
    {
        "Status:",
        "Starting Business Day ({0}):",
        "Ending Business Day ({0}):"
    };

    /**  Label text  */
    protected static final String labelTags[] =
    {
        "StatusLabel",
        "StartingBusinessDayLabel",
        "EndingBusinessDayLabel"
    };

   /** Status text   */

    /** Start text index   */
    protected static final int STATUS_NEW = 0;
    /** Printed text index   */
    protected static final int STATUS_PRINTED = STATUS_NEW + 1;
    /** Partial text index   */
    protected static final int STATUS_PARTIAL = STATUS_PRINTED + 1;
    /** Filled text index   */
    protected static final int STATUS_FILLED = STATUS_PARTIAL + 1;
    /** Canceled text index   */
    protected static final int STATUS_CANCELED = STATUS_FILLED + 1;
    /** Completed text index   */
    protected static final int STATUS_COMPLETED = STATUS_CANCELED + 1;
    /** Maximum labels   */
    protected JLabel[] fieldLabels = new JLabel[MAX_FIELDS];
   /** Status text  */
    protected String[] statusFieldLabels = new String[OrderConstantsIfc.ORDER_STATUS_REPORT_DESCRIPTORS.length];
    /** Bean model   */
    protected OrderStatusReportBeanModel beanModel = new OrderStatusReportBeanModel();
    /**
       orderStatusField is used for displaying and entering the type of order status
    **/
    protected ValidatingComboBox orderStatusField = null;
    /** Start Business Day field   */
    protected EYSDateField startBusnDateField = null;
    /** End Business Day field   */
    protected JLabel endBusnDateField   = null;
    /** Action listener   */
    protected ActionListener statusListener = null;

    // Ignore updates on an error, don't clear out the screen let the
    // user examine his errors.
    private boolean ignoreUpdate;
    //----------------------------------------------------------------------------
    /**
     * Default Constructor
     */
    //----------------------------------------------------------------------------
    public OrderStatusReportBean()
    {
        super();
    }

    //----------------------------------------------------------------------------
    /**
     * Configures the class.
     */
    //----------------------------------------------------------------------------
    public void configure()
    {
        setName("OrderStatusReportBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        for(int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i] =
                uiFactory.createLabel(labelText[i], null, UI_LABEL);
        }
        for (int i = 0; i < OrderConstantsIfc.ORDER_STATUS_REPORT_DESCRIPTORS.length; i++)
        {
            statusFieldLabels[i] = OrderConstantsIfc.ORDER_STATUS_REPORT_DESCRIPTORS[i];
        }
        orderStatusField = uiFactory.createValidatingComboBox("orderStatusField", "false", "15");
        orderStatusField.setEditable(false);
        orderStatusField.addActionListener(getStatusListener());

        startBusnDateField = uiFactory.createEYSDateField("startBusnDateField");
        startBusnDateField.setColumns(10);
        startBusnDateField.setEditable(false);

        endBusnDateField = uiFactory.createLabel("",null,UI_LABEL);
        UIUtilities.layoutDataPanel
        (
            this,
            fieldLabels,
            new JComponent[]
            {
                orderStatusField, startBusnDateField, endBusnDateField
            }
        );

    }

    //----------------------------------------------------------------------------
    /**
     * activate any settings made by this bean to external entities
     */
    //----------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        startBusnDateField.addFocusListener(this);
        endBusnDateField.addFocusListener(this);
    }

    //----------------------------------------------------------------------------
    /**
     * Deactivate any settings made by this bean to external entities
     */
    //----------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();

        startBusnDateField.removeFocusListener(this);
        endBusnDateField.removeFocusListener(this);
    }

    //------------------------------------------------------------------------
    /**
     * Gets Action listener for the status field
     * this will disable the starting business day field when status values of
     * New, Printed, Partial or Filled are selected and also set the starting
     * business day to today's date
     * this will enable the starting business day field when status values of
     * Canceled or Complete are selected
     * @return Selection listener for the status field
     */
    //------------------------------------------------------------------------
    protected ActionListener getStatusListener()
    {
        if(statusListener == null)
        {
            statusListener = new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    String status = (String)orderStatusField.getSelectedItem();
                    currentComponent = null;
                    if(status != null) // status is null when returning to screen after error msg
                    {
                        if(status.equals(statusFieldLabels[STATUS_CANCELED]) ||
                           status.equals(statusFieldLabels[STATUS_COMPLETED]))
                        {
                            startBusnDateField.setRequired(true);
                            startBusnDateField.setEditable(true);
                            startBusnDateField.setFocusable(true);
                            startBusnDateField.setEmptyAllowed(false);
                            if(beanModel.getStartBusinessDate() == null)
                            {
                                startBusnDateField.setDate(startBusnDateField.getEYSDate());
                            }
                        }
                        else
                        {
                            startBusnDateField.setRequired(false);
                            startBusnDateField.setEditable(false);
                            startBusnDateField.setFocusable(false);

                            if(beanModel.getEndBusinessDate() == null)
                            {
                                startBusnDateField.setDate(startBusnDateField.getEYSDate());
                            }
                            else
                            {
                                startBusnDateField.setDate(beanModel.getEndBusinessDate());
                            }
                        }
                    }
                }
            };
        }
        return(statusListener);
    }
    //------------------------------------------------------------------------
    /**
     * Updates the model for the current settings of this bean.
     */
    //------------------------------------------------------------------------
    public void updateModel()
    {
        String displayedValue = (String)orderStatusField.getSelectedItem();
        String keyValue = Util.getMatchingValue(displayedValue, statusFieldLabels,
                OrderConstantsIfc.ORDER_STATUS_REPORT_DESCRIPTORS);
        beanModel.setSelectedOrderStatus(keyValue);
        beanModel.setStartBusinessDate(startBusnDateField.getEYSDate());

    }
    //------------------------------------------------------------------------
    /**
     * Sets the model for the current settings of this bean.
     * @param model the model for the current values of this bean
    */
    //------------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set OrderStatusReportBeanModel" +
                                           " to null");
        }
        if (model instanceof OrderStatusReportBeanModel)
        {
            beanModel = (OrderStatusReportBeanModel) model;
            updateBean();
        }
    }
    //---------------------------------------------------------------------
    /**
     * Update the model if It's been changed
     */
    //---------------------------------------------------------------------
    protected void updateBean()
    {
        if(this.ignoreUpdate == false)
        {
            orderStatusField.setModel(new ValidatingComboBoxModel(statusFieldLabels));
            orderStatusField.setSelectedIndex(0);

            if(beanModel.getStartBusinessDate() == null)
            {
                startBusnDateField.setDate(startBusnDateField.getEYSDate());
            }
            else
            {
                startBusnDateField.setDate(beanModel.getStartBusinessDate());
            }


            // end business date is a JLabel not an EYSDateField therfore use setText
            // method to initialize the field
            String dateFieldFormat =((DateDocument) startBusnDateField.getDocument()).getFormat();
            if(beanModel.getEndBusinessDate() == null)
            {
                endBusnDateField.setText(startBusnDateField.getEYSDate().toFormattedString(dateFieldFormat,getDefaultLocale()));
            }
            else
            {
                endBusnDateField.setText(beanModel.getEndBusinessDate().toFormattedString(dateFieldFormat,getDefaultLocale()));
            }

            String status = (String)orderStatusField.getSelectedItem();
            if(status.equals(statusFieldLabels[STATUS_CANCELED]) ||
                    status.equals(statusFieldLabels[STATUS_COMPLETED]))
            {
                startBusnDateField.setRequired(true);
                startBusnDateField.setEditable(true);
                startBusnDateField.setFocusable(true);
            }
            else
            {
                startBusnDateField.setFocusable(false);
            }
        }
        this.ignoreUpdate = false;
    }

    public void showErrorScreen()
    {
        super.showErrorScreen();
        ignoreUpdate = true;
    }
    //-----------------------------------------------------------------------
    /**
       Gets the POSBaseBeanModel associated with this bean.
       @return the POSBaseBeanModel associated with this bean.
    */
    //-----------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i],
                                                labelText[i]));
        }

        //use locale appropriate date label
        String translatedLabel = getTranslatedDatePattern();
        fieldLabels[BUSN_DATE_START].setText(LocaleUtilities.formatComplexMessage(fieldLabels[BUSN_DATE_START].getText(), translatedLabel));
        fieldLabels[BUSN_DATE_END].setText(LocaleUtilities.formatComplexMessage(fieldLabels[BUSN_DATE_END].getText(), translatedLabel));

        //associate labels with fields
        orderStatusField.setLabel(fieldLabels[STATUS_START]);
        startBusnDateField.setLabel(fieldLabels[BUSN_DATE_START]);

        // retrieve text for status field
        for (int i = 0; i < OrderConstantsIfc.ORDER_STATUS_REPORT_DESCRIPTORS.length; i++)
        {
            statusFieldLabels[i] = retrieveText(OrderConstantsIfc.ORDER_STATUS_REPORT_DESCRIPTORS[i],
                    OrderConstantsIfc.ORDER_STATUS_REPORT_DESCRIPTORS[i]);
        }
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
       Returns default display string. <P>
       @return String representation of object
    */
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: OrderStatusReportBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }
    
    /**
     * Requests focus on parameter value name field if visible is true.
     * 
     * @param visible true if setting visible, false otherwise
     */
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if (visible && !errorFound())
        {
            setCurrentFocus(orderStatusField);
        }
    }

    //----------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args String[]
     */
    //----------------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        OrderStatusReportBeanModel
            beanModel = new OrderStatusReportBeanModel();
            beanModel.setStartBusinessDate(DomainGateway.getFactory().getEYSDateInstance());
            beanModel.setEndBusinessDate(DomainGateway.getFactory().getEYSDateInstance());

        OrderStatusReportBean
            bean = new OrderStatusReportBean();
            bean.configure();
            bean.setModel(beanModel);
            bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
