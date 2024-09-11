/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/DLIDNumberBean.java /main/14 2011/02/16 09:13:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *   4    360Commerce 1.3         12/28/2007 1:29:33 PM  Leona R. Slepetis
 *        removed sensitive data logging PABP FR15
 *   3    360Commerce 1.2         10/10/2007 1:02:52 PM  Anda D. Cadar
 *        changes to not allow double byte chars
 *   2    360Commerce 1.1         10/8/2007 11:36:46 AM  Anda D. Cadar   UI
 *        changes to not allow double bytes chars in some cases
 *   1    360Commerce 1.0         7/27/2006 7:16:12 PM   Brett J. Larsen 
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.device.MSRModel;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean is used to capture the Driver's License ID number.
 * 
 * @version $$
 * @see oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel
 */
public class DLIDNumberBean extends ValidatingBean
{
    private static final long serialVersionUID = -420903557616542759L;

    /**
     * Revision number supplied by source-code control system
     **/
    public static final String revisionNumber = "$Revision: /main/14 $";

    /**
     * Fields and labels that contain check enter ID data
     */
    protected JLabel idNumberLabel;

    protected DriversLicenseTextField idNumberField = null;

    /**
     * Default Constructor.
     */
    public DLIDNumberBean()
    {
        initialize();
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        idNumberField = uiFactory.createDriversLicenseField("idNumberField", "1", "20", false);
        idNumberField.setColumns(21);
    }

    /**
     * Initializes the labels.
     */
    protected void initializeLabels()
    {
        idNumberLabel = uiFactory.createLabel("IDNumberLabel", "IDNumberLabel", null, UI_LABEL);
    }

    /**
     * Updates the model from the screen.
     */
    public void updateModel()
    {
        if (beanModel instanceof CheckEntryBeanModel)
        {
            CheckEntryBeanModel model = (CheckEntryBeanModel) beanModel;
            model.setIDNumber(idNumberField.getText());
        }
    }

    /**
     * Sets the model property value.
     * 
     * @param model UIModelIfc the new value for the property.
     */
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set CheckEntryBeanModel" + "model to null");
        }
        if (model instanceof CheckEntryBeanModel)
        {
            beanModel = (CheckEntryBeanModel) model;
            updateBean();
        }
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    protected void updateBean()
    {
        if (beanModel instanceof CheckEntryBeanModel)
        {
            CheckEntryBeanModel model = (CheckEntryBeanModel) beanModel;
            idNumberField.setText(model.getIDNumber());
        }
        idNumberField.setRequired(true);
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("IDNumberBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();

        JLabel[] labels = new JLabel[] { idNumberLabel };

        JComponent[] components = new JComponent[] { idNumberField };
        UIUtilities.layoutDataPanel(this, labels, components);
    }

    /**
     * Update property fields.
     */
    protected void updatePropertyFields()
    {
        idNumberLabel.setText(retrieveText("IdNumberLabel", idNumberLabel));
        idNumberField.setLabel(idNumberLabel);
    }

    /**
     * Receive MSR data. Called by the UI Framework.
     * 
     * @param data DeviceModelIfc
     */
    public void setMSRData(DeviceModelIfc data)
    {
        MSRModel msrModel = (MSRModel) data;

        if (beanModel != null && beanModel instanceof CheckEntryBeanModel)
        {
            CheckEntryBeanModel model = (CheckEntryBeanModel) beanModel;
            model.setCardSwiped(true);
            model.setMSRModel(msrModel);
        }

        // Mail the letter for an implied 'Enter'
        UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (revisionNumber);
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        CheckEntryBeanModel aModel = new CheckEntryBeanModel();
        aModel.setIDNumber("12345");

        IDNumberBean aBean = new IDNumberBean();
        aBean.configure();
        aBean.setModel(aModel);
        aBean.activate();

        UIUtilities.doBeanTest(aBean);
    }

}
