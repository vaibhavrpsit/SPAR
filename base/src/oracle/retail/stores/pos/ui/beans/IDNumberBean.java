/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/IDNumberBean.java /main/14 2011/02/16 09:13:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jswan     11/03/10 - Fixed issues with displaying text and drop down
 *                         fields on screen with a single lable.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 6    360Commerce 1.5         12/28/2007 1:29:33 PM  Leona R. Slepetis
 *      removed sensitive data logging PABP FR15
 * 5    360Commerce 1.4         10/8/2007 11:36:46 AM  Anda D. Cadar   UI
 *      changes to not allow double bytes chars in some cases
 * 4    360Commerce 1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 *      Base-lining of 7.1_LA
 * 3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:03 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:22 PM  Robert Pearse   
 *
 *Revision 1.4  2004/05/10 01:26:19  tfritz
 *@scr 3041 Changed ID Number field from Driver License text field to Alphanumeric field
 *
 *Revision 1.3  2004/03/16 17:15:17  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:26  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 07 2003 16:18:58   bwf
 * Initial revision.
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
 * This bean is used to capture the ID number.
 * 
 * @version $$
 * @see oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel
 */
public class IDNumberBean extends ValidatingBean
{
    private static final long serialVersionUID = 1045882351902993977L;
    /**
     * Revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/14 $";
    /**
     * Fields and labels that contain check enter ID data
     */
    protected JLabel idNumberLabel;

    protected AlphaNumericTextField idNumberField = null;

    /**
     * Default Constructor.
     */
    public IDNumberBean()
    {
        initialize();
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        idNumberField = uiFactory.createAlphaNumericField("idNumberField", "1", "20", "20", false);
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
        UIUtilities.layoutDataPanel(this, labels, components, false);
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
     **/
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
