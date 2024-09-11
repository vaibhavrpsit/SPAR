/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CheckMICRBean.java /rgbustores_13.4x_generic_branch/1 2011/08/26 12:58:29 rrkohli Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rrkohli   08/26/11 - fix for 'POS - Check - Unsuccessful Check Scan error
 *                         displays when MICR manually entered'
 *    cgreene   02/15/11 - move constants into interfaces and refactor
 *    jswan     11/03/10 - Fixed issues with displaying text and drop down
 *                         fields on screen with a single lable.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         8/21/2007 4:45:30 PM   Jack G. Swan    Changed
 *      MICR Number field length from 80 to 26 to prevent transaction SQL
 *      Error.
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
import javax.swing.event.DocumentListener;

import oracle.retail.stores.foundation.manager.device.MICRModel;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.ifc.device.DeviceModelIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.ResponseDocumentListener;

/**
 * This bean is used to capture the ID for check authorization.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel
 */
public class CheckMICRBean extends ValidatingBean
{
    private static final long serialVersionUID = -6867155807278296557L;

    /**
     * Revision number supplied by source-code control system
     */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Check Entry Bean model
     */
    protected CheckEntryBeanModel beanModel = new CheckEntryBeanModel();

    /**
     * Fields and labels that contain check MICR data
     */
    protected JLabel micrNumberLabel;

    protected NumericTextField micrNumberField;
    
    protected String minLength = "1";
    
    protected DocumentListener micrNumberFieldListener = null;

    /**
     * Default Constructor.
     */
    public CheckMICRBean()
    {
        initialize();
        setTabOrder();
    }

    /**
     * Activates the bean
     */
    @Override
    public void activate()
    {
        super.activate();

        if (!beanModel.getDisplayMicrLineFlag())
        {
            micrNumberLabel.setVisible(false);
            micrNumberField.setVisible(false);
        }
        else
        {
            micrNumberLabel.setVisible(true);
            micrNumberField.setVisible(true);
        }
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        micrNumberField = uiFactory.createNumericField("MicrNumberField", "10", "26");
        micrNumberField.setColumns(21);
    }

    /**
     * Initializes the labels.
     */
    protected void initializeLabels()
    {      
        micrNumberLabel = uiFactory.createLabel("MICR Number", "MICR Number:", null, UI_LABEL);
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
        beanModel.setMICRNumber(micrNumberField.getText());
    }

    /**
     * Sets the model property value.
     * 
     * @param model UIModelIfc the new value for the property.
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if(model == null)
        {
            throw new NullPointerException("Attempt to set CheckEntryBeanModel model to null");
        }

        if (model instanceof  CheckEntryBeanModel)
        {
            beanModel = ( CheckEntryBeanModel)model;
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
        micrNumberField.setText(beanModel.getMICRNumber());
        micrNumberField.setRequired(true);
    }

    /**
     * Initialize the class.
     */
    protected void initialize() 
    {     
        setName("CheckMICRBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        
        initializeFields();
        initializeLabels();
    
        JLabel[] labels = new JLabel[]
        {
            micrNumberLabel
        };

        JComponent[] components = new JComponent[]
        {
            micrNumberField
        };
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
        micrNumberLabel.setText(retrieveText("MicrNumberLabel", micrNumberLabel));
        micrNumberField.setLabel(micrNumberLabel);
    }

    /**
     * Sets the MICR data
     * 
     * @param model DeviceModelIfc
     */
    public void setMICRData(DeviceModelIfc model)
    {
        if (logger.isInfoEnabled()) logger.info( "Received MICR data: " + model);

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
     * Adds a listener that will receive events from this object.
     * 
     * @param l the listener to add
     */
    public void addDocumentListener(DocumentListener listener)
    {
        super.addDocumentListener(listener);
        micrNumberFieldListener = listener;
        micrNumberField.getDocument().addDocumentListener(listener);
    }

    /**
     * Removes a listener that will no longer receive events from this object.
     *
     * @param l the listener to remove
     */
    public void removeDocumentListener(DocumentListener listener)
    {
      super.removeDocumentListener(listener);
      micrNumberField.getDocument().removeDocumentListener(listener);
    }
    
    public void setMinLength(String value)
    {
        minLength = value;
    }

    /**
     * Gets the minimum length of the active response field on the screen. This
     * field is set from the XML using a BEANPROPERTY.
     *
     * @return minLength as String
     */
    public String getMinLength()
    {
        return minLength;
    }
    
    /**
     * Override set Visible to request focus.
     *
     * @param aFlag indicates if the component should be visible or not.
     */
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        int minLengthInt = Integer.parseInt(minLength);;

        if (micrNumberField instanceof NumericTextField)
        {
            if (minLengthInt >= 0)
            {
                ((NumericTextField) micrNumberField).setMinLength(minLengthInt);

                if (micrNumberFieldListener != null)
                {
                    if (micrNumberFieldListener instanceof ResponseDocumentListener)
                    {
                        ((ResponseDocumentListener) micrNumberFieldListener).setMinLength(minLengthInt);
                    }
                }
            }
        }
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

        CheckMICRBean aBean = new CheckMICRBean();
        aBean.configure();
        aBean.setModel(aModel);
        aBean.activate();

        UIUtilities.doBeanTest(aBean);
    }
}
