/* ===========================================================================
* Copyright (c) 2004, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditIntegerParameterBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:38 mszekely Exp $
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
 *    mdecama   02/23/09 - Wrapping the parameterName JLabel.
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.1  2004/04/16 18:56:33  tfritz
 *   @scr 4251 - Integer parameters now can except negative and positive integers.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
    This bean enables the editing of a parameter that could be a negaive
    or positive integer.

    $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class EditIntegerParameterBean extends ValidatingBean
{
    /**
     * Generated Serial Version UID
     */
    private static final long serialVersionUID = 1179802825547169420L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final int VALUE_LABEL = 2;

    public static String[] labelText =
        {
            "Parameter Group:", "Parameter Name:", "Parameter Value:",
            "Minimum Allowed Value:", "Maximum Allowed Value:"
    };

    public static String[] labelTags =
        {
            "ParameterGroupLabel", "ParameterNameLabel", "ParameterValueLabel",
            "MinimumAllowedValueLabel", "MaximumAllowedValueLabel"
    };

    protected JLabel[] labels = null;

    protected WholeParameterBeanModel beanModel = new WholeParameterBeanModel();

    protected JLabel parameterGroupField = null;

    protected JLabel parameterNameField = null;

    protected IntegerTextField valueTextField = null;

    protected JLabel parameterMinField = null;

    protected JLabel parameterMaxField = null;


    //---------------------------------------------------------------------
    /**
     Constructs bean.
     **/
    //---------------------------------------------------------------------
    public EditIntegerParameterBean()
    {
        super();
        initialize();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the class.
     */
    protected void initialize()
    {
        setName("EditIntegerParameterBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the components.
     */
    protected void initComponents()
    {
        labels = new JLabel[labelText.length];

        // create the display labels
        for(int i=0; i<labelText.length; i++)
        {
            labels[i] = uiFactory.createLabel(labelText[i], labelText[i], null, UI_LABEL);
        }

        // create non-enterable display fields
        parameterGroupField = uiFactory.createLabel("parameterGroup", "parameterGroup", null, UI_LABEL);
        parameterNameField  = uiFactory.createLabel("parameterName", "parameterName", null, UI_LABEL);
        parameterMinField   = uiFactory.createLabel("parameterMin", "parameterMin", null, UI_LABEL);
        parameterMaxField   = uiFactory.createLabel("parameterMax", "parameterMax", null, UI_LABEL);

        // create the enterable fields
        valueTextField =
            uiFactory.createIntegerField("valueTextField", "1", "8");
        valueTextField.setEmptyAllowed(false);
        valueTextField.setColumns(8);
    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        JComponent[] fields =
            {
                parameterGroupField, parameterNameField, valueTextField,
                parameterMinField, parameterMaxField
        };
        UIUtilities.alternateLayoutDataPanel (this, labels, fields, true);
    }


    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before display.
     */
    //--------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        valueTextField.addFocusListener(this);
    }
    
    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before leaving the bean.
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        valueTextField.removeFocusListener(this);
    } 
    
    //--------------------------------------------------------------------------
    /**
     Overrides JPanel setVisible() method to request focus. <P>
     **/
    //--------------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            setCurrentFocus(valueTextField);
        }
    }

    //-----------------------------------------------------------------------
    /**
     Returns the POSBaseBeanModel associated with this bean.
     @return the POSBaseBeanModel associated with this bean.
     **/
    //-----------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return(beanModel);
    }

    //---------------------------------------------------------------------
    /**
     Returns the current value.
     <P>
     @return the current value
     **/
    //---------------------------------------------------------------------
    protected IntegerTextField getValueTextField()
    {
        return valueTextField;
    }

    //---------------------------------------------------------------------
    /**
     Updates the bean model, populated with data from the bean. <P>
     **/
    //---------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setNewValue(valueTextField.getLongValue());
    }

    //---------------------------------------------------------------------
    /**
     Sets the model data into the bean fields. <P>
     @param model the bean model
     */
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("An attempt was made to set the EditIntegerParameterBean model to null.");
        }
        else
        {
            if (model instanceof WholeParameterBeanModel)
            {
                beanModel = (WholeParameterBeanModel)model;
                updateBean();
            }
        }
    }

    //---------------------------------------------------------------------
    /**
     Updates the bean, populated with data from the model. <P>
     **/
    //---------------------------------------------------------------------
    public void updateBean()
    {
        String group = beanModel.getParameterGroup();
        String name = beanModel.getParameterName();

        valueTextField.setText(beanModel.getValue());
        parameterGroupField.setText(retrieveText(group,group));
        
        parameterNameField.setText(retrieveText(name,name));
        UIUtilities.wrapJLabelText(parameterNameField);
        
        parameterMinField.setText(String.valueOf(beanModel.getMinValue()));
        parameterMaxField.setText(String.valueOf(beanModel.getMaxValue()));
    }

    //---------------------------------------------------------------------
    /**
     Updates fields based on properties.
     **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        for (int i = 0; i < labelText.length; i++)
        {
            labels[i].setText(retrieveText(labelTags[i],labels[i]));
        }
        valueTextField.setLabel(labels[VALUE_LABEL]);

    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
     Returns default display string. <P>
     @return String representation of object
     **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: EditIntegerParameterBean (Revision " +
                getRevisionNumber() + ") @" +
                hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
     Returns the revision number. <P>
     @return String representation of revision number
     **/
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
     Displays the bean in it's own frame. <p>
     @param args command line arguments
     **/
    //---------------------------------------------------------------------
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        EditWholeParameterBean bean = new EditWholeParameterBean();
        UIUtilities.doBeanTest(bean);
    }
}
