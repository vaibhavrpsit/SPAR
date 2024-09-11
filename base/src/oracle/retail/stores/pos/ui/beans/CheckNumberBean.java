/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CheckNumberBean.java /main/15 2011/02/16 09:13:31 cgreene Exp $
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
 * 4    360Commerce 1.3         12/28/2007 1:29:33 PM  Leona R. Slepetis
 *      removed sensitive data logging PABP FR15
 * 3    360Commerce 1.2         3/31/2005 4:27:25 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:20:10 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:09:57 PM  Robert Pearse   
 *
 *Revision 1.4  2004/03/16 17:15:22  build
 *Forcing head revision
 *
 *Revision 1.3  2004/03/16 17:15:16  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:27  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Nov 07 2003 16:18:56   bwf
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.device.MICRModel;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean is used to capture check number.
 * 
 * @see oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel
 */
public class CheckNumberBean extends ValidatingBean
{
    private static final long serialVersionUID = 2378431298939010968L;
    /**
     * Revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /main/15 $";
 
    /**
     * Check Entry Bean model
     */
    protected CheckEntryBeanModel beanModel = new CheckEntryBeanModel();

    /**
     * Fields and labels that contains the check number
     */
    protected JLabel checkNumberLabel;

    protected NumericTextField checkNumberField;

    /**
     * Default Constructor.
     */
    public CheckNumberBean()
    {
        initialize();
        setTabOrder();
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        checkNumberField = uiFactory.createNumericField("CheckNumberField", "1", "8");
    }

    /**
     * Initializes the labels.
     */
    protected void initializeLabels()
    {
        checkNumberLabel = uiFactory.createLabel("Check Number", "Check Number:", null, UI_LABEL);
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
     * Updates the model from the screen.
     */
    @Override
    public void updateModel()
    {
        beanModel.setCheckNumber(checkNumberField.getText());
    }

    /**
     * Sets the model property value.
     * 
     * @param model UIModelIfc the new value for the property.
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set CheckEntryBeanModel model to null");
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
    @Override
   protected void updateBean()
    {
        checkNumberField.setText(beanModel.getCheckNumber());
        checkNumberField.setRequired(true);
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("CheckNumberBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();

        JLabel[] labels = new JLabel[] { checkNumberLabel };

        JComponent[] components = new JComponent[] { checkNumberField };
        UIUtilities.layoutDataPanel(this, labels, components, false);
    }

    /**
     * Override the tab key ordering scheme of the default focus manager where
     * appropriate. The default is to move in a zig-zag pattern from left to
     * right across the screen. In some cases, however, it makes more sense to
     * move down column one on the screen then start at the top of column 2.
     */
    protected void setTabOrder()
    {
    }

    /**
     * Update property fields.
     */
    protected void updatePropertyFields()
    {
        checkNumberLabel.setText(retrieveText("CheckNumberLabel", checkNumberLabel));
        checkNumberField.setLabel(checkNumberLabel);
    }

    /**
     * Sets the MICR data
     * 
     * @param model DeviceModelIfc
     */
    public void setMICRData(DeviceModelIfc model)
    {

        if (model instanceof MICRModel)
        {
            MICRModel micrModel = (MICRModel) model;
            beanModel.setMICRData(micrModel);
            beanModel.setCheckMICRed(true);

            // Mail the letter for an implied 'Enter'
            UISubsystem.getInstance().mail(new Letter(CommonLetterIfc.NEXT), true);
        }
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
        aModel.setMICRNumber("01234567890123456789");

        CheckNumberBean aBean = new CheckNumberBean();
        aBean.configure();
        aBean.setModel(aModel);
        aBean.activate();

        UIUtilities.doBeanTest(aBean);
    }
}
