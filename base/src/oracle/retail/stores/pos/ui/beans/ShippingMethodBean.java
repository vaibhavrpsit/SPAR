/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   05/01/13 - Layout screens for window resizing.
 *    yiqzhao   05/01/13 - Save the reason code(id_lu_cd.LU_CD_ENT) rather than
 *                         the description to retail price
 *                         modifier(CO_MDFR_RTL_PRC.RC_MDFR_RT_PRC).
 *    yiqzhao   03/13/13 - Add reason code for shipping charge override for
 *                         cross channel and store send.
 *    hyin      05/18/12 - rollback changes made to CustomerUI for AddressType.
 *                         Change required field to phone number from
 *                         postalcode.
 *    yiqzhao   04/03/12 - refactor store send for cross channel (add imports)
 *    yiqzhao   04/03/12 - refactor store send for cross channel
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    dwfung    02/03/10 - Use ValidatingList for ShipVia Field
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    nkgautam  02/25/09 - fix for name field when length of tis field exceeds
 *                         50
 *    cgreene   02/18/09 - prevent null pointers when no shipping charge codes
 *                         are available
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *    mkochumm  11/19/08 - cleanup based on i18n changes
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         4/25/2007 8:51:28 AM   Anda D. Cadar   I18N
 *         merge
 *    3    360Commerce 1.2         3/31/2005 4:29:58 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:17 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:13 PM  Robert Pearse
 *
 *   Revision 1.9.2.1  2004/10/28 18:48:38  rsachdeva
 *   @scr 7385 Scrolling up/down the shipping method list
 *
 *   Revision 1.9  2004/09/15 14:50:07  rsachdeva
 *   @scr 6791 Transaction Level Send
 *
 *   Revision 1.8  2004/08/06 20:26:08  mweis
 *   @scr 6781 Shipping method's "Ship To" address should be city, comma, blank, state.
 *
 *   Revision 1.7  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.6  2004/06/10 18:56:03  rsachdeva
 *   @scr 4670 Send: Multiple Sends Shipping Charge Field Required and Editable
 *
 *   Revision 1.5  2004/06/03 13:29:21  lzhao
 *   @scr 4670: delete send item.
 *
 *   Revision 1.4  2004/05/10 20:41:02  rsachdeva
 *   @scr 4670 Send: Shipping To Address
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 16 2003 17:53:16   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:12:18   CSchellenger
 * Initial revision.
 *
 *    Rev 1.6   Jul 29 2003 12:19:10   sfl
 * In Shipping method screeen, switched the order of displaying shipping method selection list and the shipping charge so that it is more logical during application.
 * Resolution for POS SCR-3287: Offline- Send Transaction Shipping Method screen has no focus
 *
 *    Rev 1.5   Apr 11 2003 17:35:14   baa
 * removing 2nd column of data
 * Resolution for POS SCR-2147: Modidy   Two columns (label/field pair)  screens
 *
 *    Rev 1.4   Mar 21 2003 10:58:48   baa
 * Refactor mailbankcheck customer screen, second wave
 * Resolution for POS SCR-2098: Refactoring of Customer Service Screens
 *
 *    Rev 1.3   Sep 20 2002 18:03:06   baa
 * country/state fixes and other I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Sep 19 2002 09:45:32   baa
 * fix state country  pair for I18n
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 14 2002 18:18:42   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:52:32   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:36:06   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:53:46   msg
 * Initial revision.
 *
 *    Rev 1.15   14 Mar 2002 11:19:58   baa
 * make screen fit on linux box
 * Resolution for POS SCR-1561: shipping method screen shrinks on linux
 *
 *    Rev 1.13   Mar 08 2002 14:36:12   mpm
 * Externalized text for send UI screens.
 *
 *    Rev 1.12   08 Feb 2002 18:52:40   baa
 * defect fix
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.11   06 Feb 2002 20:47:38   baa
 * defect fix
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.10   05 Feb 2002 17:41:04   baa
 * ui fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.9   Jan 23 2002 17:40:28   mpm
 * UI fixes.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.8   Jan 19 2002 10:31:54   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.6   02 Jan 2002 18:43:32   baa
 * updates for send
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.5   21 Dec 2001 16:46:28   baa
 * hooks for validating shipping charge
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.4   19 Dec 2001 17:42:04   baa
 * updates for send
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.3   17 Dec 2001 19:14:10   baa
 * updates to ui
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.2   13 Dec 2001 18:00:14   baa
 * No change.
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.1   06 Dec 2001 18:49:06   baa
 * additional updates for  send feature
 * Resolution for POS SCR-287: Send Transaction
 *
 *    Rev 1.0   04 Dec 2001 15:33:22   baa
 * Initial revision.
 * Resolution for POS SCR-287: Send Transaction
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.domain.shipping.ShippingMethodIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Contains the visual presentation for Shipping Method Information $Revision:
 * /main/10 $
 */
public class ShippingMethodBean extends ValidatingBean implements ListSelectionListener
{
    private static final long serialVersionUID = 5230288540292131137L;

    /**
     * revision number
     */

    /**
     * business name string used to set name for this field
     */
    public static final String BUSINESS_NAME = "BusinessName";
    
    /** ship to label tag */
    protected static final String SHIP_TO_LABEL = "ShipToLabel";
    /** ship via label tag */
    protected static String SHIP_VIA_LABEL = "ShipViaLabel";
    /** ship charges label tag */
    protected static String SHIP_CHARGE_LABEL = "ShipChargeLabel";
    /** instructions label tag */
    protected static String INSTRUCTIONS_LABEL = "InstructionsLabel";
    /** instructions label tag */
    protected static String REASON_CODE_LABEL = "ReasonCodeLabel";

    /** The bean model */
    protected ShippingMethodBeanModel beanModel = null;
    /** First, last name field */
    protected JLabel nameField = null;
    /** business name label */
    protected JLabel businessName = null;
    /** Address line 1 field */
    protected JLabel addressLine1Field = null;
    /** Address line 2 field */
    protected JLabel addressLine2Field = null;
    /** Address line 3 field */
    protected JLabel addressLine3Field = null;
    /** city, state, zip, country field */
    protected JLabel cityStateZipField = null;
    /** labels for fields */
    protected JLabel shipToLabel = null;
    /** Shipping Method Label */
    protected JLabel shipViaLabel = null;
    /** Shipping Charge label */
    protected JLabel shipChargeLabel = null;
    /** Label for Reason Code.  */
    protected JLabel reasonCodeLabel = null;    
    /** Instructions label */
    protected JLabel instrLabel = null;
    /** empty label */
    protected JLabel emptyLabel = null;
    /** Container of method lists */
    protected ValidatingList shipViaList = null;
    /** Scroll long list of departments */
    protected JScrollPane shipViaScrollPane = null;
    /** shipping charge */
    protected CurrencyTextField shipChargeField = null;
    /** the reason code of discount */
    protected ValidatingComboBox reasonList;    
    /** special instructions field */
    protected ConstrainedTextAreaField instrField = null;
    /** Scroll long text area of departments */
    protected JScrollPane instrScrollPane = null;

    /**
     * Maximum Full Name length in one row
     */
    protected int MAX_NAME_LENGTH = 50;

    /**
     * Default class Constructor and initializes its components.
     */
    public ShippingMethodBean()
    {
        initialize();
    }

    /**
     * Initialize the class and its screen members.
     */
    protected void initialize()
    {
        setName("ShippingMethodBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeLabels();
        initializeFields();
        layoutComponents();
    }

    /**
     * Initialize the labels on this screen
     */
    protected void initializeLabels()
    {
        shipToLabel = uiFactory.createLabel("shipToLabel", "shipToLabel", null, UI_LABEL);
        emptyLabel = uiFactory.createLabel("emptyLabel", "", null, UI_LABEL);

        nameField = uiFactory.createDisplayField("nameField");
        businessName = uiFactory.createDisplayField(BUSINESS_NAME);
        addressLine1Field = uiFactory.createDisplayField("addressLine1Field");
        addressLine2Field = uiFactory.createDisplayField("addressLine2Field");
        addressLine3Field = uiFactory.createDisplayField("addressLine3Field");
        cityStateZipField = uiFactory.createDisplayField("cityStateZipField");

        shipViaLabel = uiFactory.createLabel("shipViaLabel", "shipViaLabel", null, UI_LABEL);
        shipChargeLabel = uiFactory.createLabel("shipChargeLabel", "shipChargeLabel", null, UI_LABEL);
        reasonCodeLabel = uiFactory.createLabel("reasonCode", "Reason Code:", null, UI_LABEL);
        instrLabel = uiFactory.createLabel("instrLabel", "instrLabel", null, UI_LABEL);
    }

    /**
     * Initialize the fields on this screens.
     */
    protected void initializeFields()
    {
        shipViaScrollPane = uiFactory.createSelectionList("shipViaList", 300, 60, "false");
        shipViaList = (ValidatingList)shipViaScrollPane.getViewport().getView();
        shipChargeField = uiFactory.createCurrencyField("shipChargeField", "false", "false", "false");
        reasonList = uiFactory.createValidatingComboBox("ReasonCodeField", "false", "20");
        reasonList.setName("ReasonCodeField");
        reasonList.setLabel(reasonCodeLabel);
        instrScrollPane = uiFactory.createConstrainedTextAreaFieldPane("instrViaScrollPane", "0", "400", "80", "true",
                "true", JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        instrField = (ConstrainedTextAreaField)instrScrollPane.getViewport().getView();
    }

    /**
     * Layout the components on this screen
     */
    protected void layoutComponents()
    {
        JLabel[] labels = { shipToLabel, emptyLabel, emptyLabel, emptyLabel, emptyLabel, emptyLabel, shipViaLabel,
                shipChargeLabel, reasonCodeLabel, instrLabel };

        JComponent[] components = { nameField, businessName, addressLine1Field, addressLine2Field, addressLine3Field,
                cityStateZipField, shipViaScrollPane, shipChargeField, reasonList, instrScrollPane };

        setLayout(new GridBagLayout());
        UIUtilities.layoutDataPanel(this, labels, components, false);
    }

    /**
     * Overrides the inherited setVisible().
     *
     * @param visible boolean
     */
    @Override
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);

        if (visible && !errorFound())
        {
            setCurrentFocus(shipViaList);
        }
    }

    /**
     * Gets the POSBaseBeanModel for validation of the current settings of the
     * bean.
     *
     * @return the POSBaseBeanModel for the current values.
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Calls setchangeStateValue to determine if any of the fields were changed.
     * Updates the model with the newest values from the bean.
     */
    @Override
    public void updateModel()
    {
        beanModel.setSelectedShipMethod(shipViaList.getSelectedIndex());
        if (Util.isEmpty(shipChargeField.getText()))
        {
            beanModel.setShippingCharge(null);
        }
        else
        {
            beanModel.setShippingCharge(shipChargeField.getCurrencyValue());
        }
        
        beanModel.setSelectedReasonCode(reasonList.getSelectedIndex());
        
        beanModel.setInstructions(instrField.getText());

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
            throw new NullPointerException("Attempt to set ShippingMethodBean model to null");
        }

        if (model instanceof ShippingMethodBeanModel)
        {
            beanModel = (ShippingMethodBeanModel)model;
            updateBean();
        }
    }

    /**
     * Updates the bean and changes fields based upon whether a layaway is in
     * progress
     */
    @Override
    public void updateBean()
    {
        String fullName = beanModel.getFirstName() + " " + beanModel.getLastName();
        if (fullName.length() < MAX_NAME_LENGTH)
        {
            nameField.setText(beanModel.getFirstName() + " " + beanModel.getLastName());
        }
        else
        {
            nameField.setText("<html><b><body>" + beanModel.getFirstName() +
                    "<br>" + beanModel.getLastName()
                    + "</body></b></html>");
        }
        businessName.setText(beanModel.getOrgName());
        addressLine1Field.setText(beanModel.getAddressLine1());
        addressLine2Field.setText(beanModel.getAddressLine2());
        String cityState = beanModel.getCity();

        if (beanModel.getCountryInfo() != null)
        {
            if (beanModel.getStateInfo() != null)
            {
                cityState = cityState + beanModel.getCountryInfo().getAddressDelimiter() + " " + beanModel.getState();
            }

            cityState = cityState + " " + beanModel.getPostalCode();
            cityState = cityState + " " + beanModel.getCountryInfo().getPostalCodeDelimiter() + " "
                    + beanModel.getCountryInfo().getCountryName();
        }

        cityStateZipField.setText(cityState);

        ShippingMethodIfc methods[] = beanModel.getShipMethodsList();

        if (methods != null)
        {
            String methodDesc[] = new String[methods.length];
            for (int i = 0; i < methods.length; i++)
            {
            	StringBuffer shipMethod = new StringBuffer(methods[i].getShippingType(LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE)));
                methodDesc[i] =  shipMethod.append("                    ").append(methods[i].getCalculatedShippingCharge()).toString();
            }
            shipViaList.setListData(methodDesc);
        }

        ShippingMethodIfc selection = beanModel.getSelectedShipMethod();
        if (selection != null)
        {
            shipViaList.setSelectedValue(selection.getShippingType(LocaleMap
                    .getLocale(LocaleConstantsIfc.USER_INTERFACE)), true);
        }

        CurrencyIfc charge = beanModel.getShippingCharge();
        shipChargeField.setValue(charge);
        instrField.setText(beanModel.getInstructions());
        setCurrentFocus(shipViaList);
        
        Vector<String> reasonCodelist = beanModel.getReasonCodes();
        reasonList.setModel(new ValidatingComboBoxModel(reasonCodelist));
        
        String selectedReasonCode = beanModel.getSelectedReason();
        if (!reasonList.isEditable() && selectedReasonCode.equals(""))
        {
            selectedReasonCode = beanModel.getDefaultValue();
        }
        reasonList.setSelectedItem(selectedReasonCode);
    }

    /**
     * The framework calls this method just before display
     */
    @Override
    public void activate()
    {
        super.activate();
        shipViaList.addListSelectionListener(this);
        shipViaList.addFocusListener(this);
        instrField.addFocusListener(this);
        updateModel();
    }

    /**
     * deactivate any settings made by this bean to external entities
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        shipViaList.removeListSelectionListener(this);
        shipViaList.removeFocusListener(this);
        instrField.removeFocusListener(this);
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e)
    {
        // we are interested in the final state of the selection
        if (!e.getValueIsAdjusting())
        {
            updateShippingCharge(e);
        }
    }

    /**
     * updateShippingCharge
     */
    public void updateShippingCharge(ListSelectionEvent e)
    {
        int indx = shipViaList.getSelectedIndex();
        if (indx == -1)
        {
            indx = 0;
        }

        beanModel.setSelectedShipMethod(indx);
        ShippingMethodIfc shippingMethod = beanModel.getSelectedShipMethod();
        CurrencyIfc charge = beanModel.getShippingCharge();
        shipChargeField.setValue(charge);
    }

    /**
     * Updates property-based fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        if (shipToLabel != null)
        {
            shipToLabel.setText(retrieveText(SHIP_TO_LABEL, shipToLabel.getText()));
        }
        if (shipViaLabel != null)
        {
            shipViaLabel.setText(retrieveText(SHIP_VIA_LABEL, shipViaLabel.getText()));
        }
        if (shipChargeLabel != null)
        {
            shipChargeLabel.setText(retrieveText(SHIP_CHARGE_LABEL, shipChargeLabel.getText()));
        }
        if (instrLabel != null)
        {
            instrLabel.setText(retrieveText(INSTRUCTIONS_LABEL, instrLabel.getText()));
        }
        shipViaList.setLabel(shipViaLabel);
        shipChargeField.setLabel(shipChargeLabel);
        
        reasonCodeLabel.setText(retrieveText(REASON_CODE_LABEL, reasonCodeLabel));
        reasonList.errorMessage = reasonCodeLabel.getText();
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     *
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        javax.swing.JFrame frame = new javax.swing.JFrame("CheckReferralBean");

        ShippingMethodBean bean = new ShippingMethodBean();

        ShippingMethodBeanModel beanModel = new ShippingMethodBeanModel();

        beanModel.setFirstName("John");
        beanModel.setLastName("Doe");
        beanModel.setAddressLine1("1 main street");
        beanModel.setAddressLine2("Suite 1b");
        beanModel.setCity("Austin");
        bean.setModel(beanModel);
        bean.activate();

        frame.setSize(530, 290);
        frame.getContentPane().add(bean);
        frame.setVisible(true);
    }
}
