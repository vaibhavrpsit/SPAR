/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/LocationBean.java /main/20 2013/01/15 16:55:52 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    cgreene   02/02/10 - removed deprecated field
 *    abondala  01/03/10 - update header date
 *    asinton   03/12/09 - remove erroneous else block
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *    acadar    02/09/09 - use default locale for display of date and time
 *
 * ===========================================================================
 * $Log:
 *    3    I18N_P2    1.1.1.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR
 *         29826 - Setting the size of the combo boxes. This change was
 *         necessary because the width of the combo boxes used to grow
 *         according to the length of the longest content. By setting the
 *         size, we allow the width of the combo box to be set independently
 *         from the width of the dropdown menu.
 *    2    360Commerce 1.1         5/11/2007 4:13:12 PM   Mathews Kochummen use
 *          locale's date format
 *    1    360Commerce 1.0         11/27/2006 5:37:44 PM  Charles D. Baker
 *
 *   Revision 1.8  2004/09/30 16:36:55  mweis
 *   @scr 7012 If applicable, pre-select an Order's order location.
 *
 *   Revision 1.7  2004/09/27 18:27:40  mweis
 *   @scr 7012 Special Order restoration of "oder list" (and fixes for SCR 7243).
 *
 *   Revision 1.6  2004/08/27 17:51:03  bvanschyndel
 *   Added check for inventory integration switch
 *
 *   Revision 1.5  2004/07/31 17:28:42  cdb
 *   @scr 6348 Updated so updateModel doesn't update bean components. Cures a Java UI thread deadlock.
 *
 *   Revision 1.4  2004/06/29 22:03:30  aachinfiev
 *   Merge the changes for inventory & POS integration
 *
 *   Revision 1.3.2.2  2004/06/21 14:17:08  jeffp
 *   Removed unused imports
 *
 *   Revision 1.3.2.1  2004/06/14 17:48:08  aachinfiev
 *   Inventory location/state related modifications
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
 *    Rev 1.1   Sep 16 2003 17:52:42   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:11:08   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 07 2002 19:34:20   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:48:42   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:56:04   msg
 * Initial revision.
 *
 *    Rev 1.4   Mar 02 2002 17:58:54   mpm
 * Internationalized order UI.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   Jan 24 2002 16:15:02   dfh
 * updates to display the order location, cleanup
 * Resolution for POS SCR-260: Special Order feature for release 5.0
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.text.DateFormat;
import java.util.Vector;

import javax.swing.JLabel;

import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceIfc;
import oracle.retail.stores.commerceservices.common.datetime.DateTimeServiceLocator;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.common.utility.Util;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.order.OrderIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Contains the visual presentation for Order Location and Edit Location
 * screens.
 * 
 * @version $Revision: /main/20 $;
 */
public class LocationBean extends CycleRootPanel
{
    private static final long serialVersionUID = -664561654219869260L;
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/20 $";
    /** Constant for order ID row */
    protected static final int ORDER_ID_ROW = 0;
    /** Constant for customer name row*/
    protected static final int CUSTOMER_NAME_ROW = ORDER_ID_ROW + 1;
    /** Constant for date row*/
    protected static final int ORDER_DATE_ROW = CUSTOMER_NAME_ROW + 1;
    /** Constant for location row */
    protected static final int LOCATION_ROW   = ORDER_DATE_ROW + 1;
    /** Constant for status row */
    protected static final int STATUS_ROW     = LOCATION_ROW + 1;
    /** Constant for max fields */
    protected static final int MAX_FIELDS     = STATUS_ROW + 1;
    /** Constants for label text */
    protected static String labelText[] =
    {
        "Order Number",
        "Customer Name",
        "Order Date",
        "Location",
        "Status",
    };
    /** Constants for label text */
    protected static String labelTags[] =
    {
        "OrderNumberLabel",
        "CustomerNameLabel",
        "OrderDateLabel",
        "LocationLabel",
        "StatusLabel",
    };
    /** Array of label objects */
    protected JLabel[] fieldLabels        = new JLabel[MAX_FIELDS];
    /** Order ID label */
    protected JLabel orderIDLabel         = null;
    /** Customer Name Label */
    protected JLabel customerNameLabel    = null;
    /** Order Date Label */
    protected JLabel orderDateLabel       = null;
    /** Location Label */
    protected JLabel locationLabel        = null;
    /** Status Label */
    protected JLabel statusLabel          = null;
    /** Order Id field*/
    protected JLabel orderIDField         = null;
    /** Customer Name field */
    protected JLabel customerNameField    = null;
    /** Date field */
    protected JLabel orderDateField       = null;
    /** Status field */
    protected JLabel statusField          = null;
    /** Loacation field */
    protected JLabel             locationField        = null;
    /** Combo box for location list field */
    protected ValidatingComboBox locationListField    = null;
    /** Bean model */
    protected LocationBeanModel beanModel    = null;

    /**
     * Default Constructor
     */
    public LocationBean()
    {
        initialize();
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("LocationBean");
        setLayout(new GridBagLayout());
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initFields();
        initLabels();
    }

    /**
     * Initialize the setting for the data fields and place the on the panel
     */
    protected void initFields()
    {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;
        gbc.ipady = 10;

        orderIDField =  uiFactory.createDisplayField("orderIDField");
        gbc.gridy = ORDER_ID_ROW;
        add(orderIDField, gbc);

        customerNameField = uiFactory.createDisplayField("customerNameField");
        gbc.gridy = CUSTOMER_NAME_ROW;
        add(customerNameField, gbc);

        orderDateField = uiFactory.createDisplayField("orderDateField");
        gbc.gridy = ORDER_DATE_ROW;
        add(orderDateField, gbc);

        statusField = uiFactory.createDisplayField("statusField");
        gbc.gridy = STATUS_ROW;
        add(statusField, gbc);
    }

    /**
     * Initialize the setting for the field labels and place them on the panel
     */
    protected void initLabels()
    {
        GridBagConstraints gbc = new GridBagConstraints();
        Insets insets = new java.awt.Insets(0, 5, 0, 5);
        gbc.gridx = 0;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.ipady = 10;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.insets = insets;

        for(int cnt = 0; cnt < MAX_FIELDS; cnt++)
        {
            fieldLabels[cnt] = uiFactory.createLabel(labelText[cnt], labelText[cnt], null, UI_LABEL);
            gbc.gridy = cnt;
            add(fieldLabels[cnt], gbc);
        }
    }

    /**
     * This method is only called when a user has an input screen. The Business
     * Logic, like a site, road or aisle calls getModel() after user presses
     * Accept. For example, see InfoAcceptAisle. Code this bean so that Strings
     * from the input screen get passed to the Model in anticipation of future
     * functionality.
     */
    @Override
    public void updateModel()
    {
        if (beanModel !=null && beanModel.getEditMode() && locationListField != null)
        {

            int locationIndex = locationListField.getSelectedIndex();

            if (locationIndex >= 0)
            {
                String orderLocationCode = beanModel.getOrderLocationsList().getKeyEntries().get(locationIndex);
                beanModel.setSelectedLocation(orderLocationCode);
            }
        }
    }

    /**
     * Sets the model to be used with the LocationBean.
     *
     * @param model the model for this bean
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set LocationBeanModel" +
                                           " to null");
        }
        if (model instanceof LocationBeanModel)
        {
            beanModel = (LocationBeanModel) model;

            updateLocationField(); // combo box or JLabel

            orderIDField.setText(beanModel.getOrderID());
            customerNameField.setText(beanModel.getCustomerName());
            DateTimeServiceIfc dateTimeService = DateTimeServiceLocator.getDateTimeService();
            String orderDate = dateTimeService.formatDate(beanModel.getOrderDate().dateValue(), LocaleMap.getLocale(LocaleMap.DEFAULT), DateFormat.SHORT);
            orderDateField.setText(orderDate);
            statusField.setText(beanModel.getStatus());

            // (See "updateLocationField()" method to de-mystify this.)
            // If location is display only, then set the JLabel text here.
            if (locationField != null)
            {
                if(beanModel.getSelectedLocation() != null &&  !beanModel.getSelectedLocation().equals(CodeConstantsIfc.CODE_NOT_APPLICABLE))
                {
                    CodeEntryIfc codeEntry = beanModel.getOrderLocationsList().findListEntryByCode(beanModel.getSelectedLocation());

                    locationField.setText(codeEntry.getText(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
                }
                else
                {
                    locationField.setText(beanModel.getLocalizedNotAvailbleLocation());
                }

            }

            Vector<String> layawayLocations = beanModel.getOrderLocationsList().getTextEntries(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));

            // If applicable, populate the combo box.
            if (locationListField != null && layawayLocations != null)
            {
                int location = 0;
                if((beanModel.getSelectedLocation() != null) && (!beanModel.getSelectedLocation().equals(CodeConstantsIfc.CODE_NOT_APPLICABLE)))
                {
                     location = Integer.parseInt(beanModel.getSelectedLocation());
                }

                String[] list = layawayLocations.toArray(new String[layawayLocations.size()]);
                ValidatingComboBoxModel listModel = new ValidatingComboBoxModel();
                for(int i=0; i < list.length; i++)
                {
                    listModel.addElement(list[i]);
                }
                locationListField.setModel(listModel);

                if (list.length > 0 && location != -1)
                {
                    locationListField.setSelectedIndex(location);
                }
            }
        }
    }

    /**
     * Called from setModel(). Adds or removes the Location Component based on
     * the type of component needed in the current use of the bean. Combo Box or
     * JLabel.
     */
    protected void updateLocationField()
    {
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.CENTER;
        gbc.weightx = 0.0;
        gbc.weighty = 0.0;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.gridheight = 1;

        // remove any prior location component from the screen to ensure that
        // the correct component is added dynamically based on the current use
        // of the bean
        if (locationListField !=null)
        {
            remove(locationListField);
            locationListField = null;
        }
        if (locationField !=null)
        {
            remove(locationField);
            locationField = null;
        }


        // Display ComboBox or JLabel for Location
        if (beanModel !=null)
        {
            if (beanModel.getEditMode())
            {
                locationListField = new ValidatingComboBox();
                locationListField = uiFactory.createValidatingComboBox("locationListField", "false", "20");
                locationListField.setName("locationListField");
                gbc.gridy = LOCATION_ROW;

                add(locationListField, gbc);
             }
            else
            {
                locationField = new JLabel();
                locationField = uiFactory.createDisplayField("locationField");
                gbc.gridy = LOCATION_ROW;

                add(locationField, gbc);
            }
        }

    }

    /**
     * Overrides setVisible() in order to set the focus on the locationListField
     * 
     * @param value boolean
     */
    @Override
    public void setVisible(boolean value)
    {
        super.setVisible(value);

        // Set the focus
        if(value && beanModel.getEditMode())
        {
            setCurrentFocus(locationListField);
        }
    }

    /**
     * Activates this bean.
     */
    @Override
    public void activate()
    {
        super.activate();
        if (locationListField != null)
        {
            locationListField.addFocusListener(this);
        }
    }

    /**
     * Deactivates this bean.
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        if (locationListField !=null)
        {
         locationListField.removeFocusListener(this);
        }
    }

    /**
     * Updates fields based on properties.
     */
    protected void updatePropertyFields()
    {
        for (int i = 0; i < labelText.length; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i], fieldLabels[i]));
        }
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class: LocationBean (Revision " + getRevisionNumber() + ") @" +                            hashCode());
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

    //----------------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        LocationBean bean = new LocationBean();

        OrderIfc o = DomainGateway.getFactory().getOrderInstance();
        LocationBeanModel beanModel = new LocationBeanModel(o);

        bean.setModel(beanModel);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
