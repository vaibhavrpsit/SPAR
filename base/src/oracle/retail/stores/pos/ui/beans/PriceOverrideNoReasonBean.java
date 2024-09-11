/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PriceOverrideNoReasonBean.java /main/13 2013/09/05 10:36:16 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   11/08/10 - update layout to not stretch field widget
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:29:29 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:24:22 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:13:24 PM  Robert Pearse   
 *
 * Revision 1.2  2004/07/19 17:49:06  awilliam
 * @scr 4485 extra spaces in msg prompt
 *
 * Revision 1.1  2004/05/06 05:05:53  tfritz
 * @scr 4605 Added new CaptureReasonCodeForPriceOverride parameter
 *
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.HashMap;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean uses the DecimalWithReasonBeanModel.
 * 
 * @return DecimalWithReasonBeanModel The model of the bean.
 * @see #setModel
 * @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
 * @version $Revision: /main/13 $
 */
public class PriceOverrideNoReasonBean extends ValidatingBean
{
    private static final long serialVersionUID = -5792270804127328424L;

    /**
     * Revision Number furnished by TeamConnection.
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    public static String CHOICE_SEPARATOR = " - ";

    /**
     * local reference to model being used.
     */
    protected DecimalWithReasonBeanModel beanModel = null;

    /**
     * TextField for the Override Price.
     */
    protected CurrencyTextField overridePriceField = null;

    /**
     * Label for OverridePriceField.
     */
    protected JLabel overridePriceLabel = null;

    /**
     * Indicates when the model is dirty.
     */
    protected boolean dirtyModel = false;

    /** vector with list of tags for choices **/
    protected Vector tag_list = null;

    /** vector with list of tag ids for choices **/
    protected Vector tag_list_ids = null;

    /** Indicates if code id should prepend code description in list **/
    protected boolean prependCodeID = false;

    protected HashMap reverseMap = new HashMap(1);

    /**
     * Constructor
     */
    public PriceOverrideNoReasonBean()
    {
        super();
    }

    /**
     * Configures the class.
     */
    @Override
    public void configure()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);
        setName("PriceOverrideBean");
        overridePriceLabel = uiFactory.createLabel("Override Price:", "Override Price:", null, UI_LABEL);
        overridePriceField = uiFactory.createCurrencyField("OverridePriceField", "false", "false", "false");
        beanModel = new DecimalWithReasonBeanModel();
        initLayout();
    }

    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this, new JLabel[] { overridePriceLabel }, new JComponent[] { overridePriceField });
    }

    /**
     * Allows for editable combo box. Default is false.
     */
    public void setPrependCodeID(String prepend)
    {
        prependCodeID = UIUtilities.getBooleanValue(prepend);
    }

    /**
     * Returns the bean model.
     * 
     * @return model object
     * @see oracle.retail.stores.pos.ui.beans.POSBaseBeanModel
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * updates the Model properties.
     * 
     * @return The model property value.
     * @see #setModel
     */
    @Override
    public void updateModel()
    {
        beanModel.setValue(overridePriceField.getDecimalValue());
        beanModel.setSelected(false);

    }

    /**
     * This method sets the model of this bean. This bean uses the
     * DecimalWithReasonBeanModel.
     * 
     * @param model The new DecimalWithReasonBeanModel to use.
     * @see #getModel
     * @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set DiscAmountBean model to null");
        }
        if (model instanceof DecimalWithReasonBeanModel)
        {
            beanModel = (DecimalWithReasonBeanModel) model;
            dirtyModel = true;
            updateBean();
        }
    }

    /**
     * Update the bean if It's been changed
     */
    @Override
    protected void updateBean()
    {
        if (dirtyModel)
        {
            overridePriceField.setDecimalValue(beanModel.getValue());

        }
    }

    /**
     * Updates property-based fields.
     */
    protected void updatePropertyFields()
    {
        overridePriceLabel.setText(retrieveText("OverridePriceLabel", overridePriceLabel));
        overridePriceField.setLabel(overridePriceLabel);

    }

    /**
     * Activates this bean.
     */
    @Override
    public void activate()
    {
        super.activate();
        overridePriceField.addFocusListener(this);
    }

    /**
     * Deactivates this bean.
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        overridePriceField.removeFocusListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: PriceOverrideNoReasonBean (Revision " + getRevisionNumber() + ") @"
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

        PriceOverrideEntryBean bean = new PriceOverrideEntryBean();
        bean.configure();

        UIUtilities.doBeanTest(bean);
    }
}
