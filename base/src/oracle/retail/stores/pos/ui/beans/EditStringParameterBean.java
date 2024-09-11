/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditStringParameterBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
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
 *
 * ===========================================================================
 * $Log:
 *   4    I18N_P2    1.2.1.0     1/8/2008 2:56:48 PM    Sandy Gu        Set max
 *         length of constraied text field.
 *   3    360Commerce 1.2         3/31/2005 4:27:54 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:13 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse   
 *
 *  Revision 1.6  2004/07/20 20:29:24  cdb
 *  @scr 6127 Updated behavior of StringLengthValidator.
 *
 *  Revision 1.5  2004/07/17 19:21:23  jdeleau
 *  @scr 5624 Make sure errors are focused on the beans, if an error is found
 *  during validation.
 *
 *  Revision 1.4  2004/04/09 16:56:00  cdb
 *  @scr 4302 Removed double semicolon warnings.
 *
 *  Revision 1.3  2004/03/16 17:15:17  build
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 20:56:26  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 15 2003 16:03:08   dcobb
 * Migrate to JDK 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:10:20   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Sep 03 2002 16:07:52   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Aug 14 2002 18:17:32   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jun 21 2002 18:26:38   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 14:54:10   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:34:22   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:55:06   msg
 * Initial revision.
 *
 *    Rev 1.8   Mar 04 2002 14:15:36   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.7   22 Feb 2002 11:35:06   baa
 * fix value field to hold and display 40 characters
 * Resolution for POS SCR-1395: BackOfficeURL parameter datafield will not hold 40 characters
 *
 *    Rev 1.6   16 Feb 2002 10:17:14   baa
 * fix required field logic
 * Resolution for POS SCR-1306: Invalid Data Notice missing after selecting Enter with no data for text area fields
 *
 *    Rev 1.5   11 Feb 2002 23:53:26   baa
 * fixing text area display
 * Resolution for POS SCR-1204: AutomaticEmailCanceledOrder parameter value field box too small to see data
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java import

// swing imports
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.apache.log4j.Logger;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
    This bean enables the editing of a parameter that has a string value.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//---------------------------------------------------------------------
public class EditStringParameterBean extends ValidatingBean
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    public static final int VALUE_LABEL = 2;
    public static final int MIN_LABEL = 3;
    public static final int MAX_LABEL = 4;
    //public static final int MODIFY_LABEL = 3;

    public static String[] labelText =
    {
        "Parameter Group:", "Parameter Name:", "Parameter Value:",
        "Minimum Allowed Length:", "Maximum Allowed Length:"
    };

    public static String[] labelTags =
    {
        "ParameterGroupLabel", "ParameterNameLabel", "ParameterValueLabel",
        "MinimumAllowedLengthLabel", "MaximumAllowedLengthLabel"
    };

    protected JLabel[] labels = null;

    protected StringParameterBeanModel beanModel = new StringParameterBeanModel();

    protected JLabel parameterGroupField = null;

    protected JLabel parameterNameField = null;

    protected ConstrainedTextField valueTextField = null;

    protected JLabel parameterMinField = null;

    protected JLabel parameterMaxField = null;
    //protected JList modifiableChoiceList = null;

    protected Logger logger = Logger.getLogger(oracle.retail.stores.pos.ui.beans.EditStringParameterBean.class);

    //---------------------------------------------------------------------
    /**
        Constructs bean.
    **/
    //---------------------------------------------------------------------
    public EditStringParameterBean()
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
        setName("EditStringParameterBean");
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
            labels[i] = uiFactory.createLabel(labelText[i], null, UI_LABEL);
        }

        // create non-enterable display fields
        parameterGroupField =
            uiFactory.createLabel("parameterGroup", null, UI_LABEL);
        parameterNameField  =
            uiFactory.createLabel("parameterName", null, UI_LABEL);
        parameterMinField   = uiFactory.createLabel("parameterMin", null, UI_LABEL);
        parameterMaxField   = uiFactory.createLabel("parameterMax", null, UI_LABEL);
        
        // create the enterable fields
        valueTextField = uiFactory.createConstrainedField("valueTextField", "1", "30", "40");
        valueTextField.setColumns(30);
   }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        JComponent[] fields =
        {
            parameterGroupField,
            parameterNameField,
            valueTextField,
            parameterMinField,
            parameterMaxField
        };
        UIUtilities.layoutDataPanel(this, labels, fields);
    }


    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before display
     */
    public void activate()
    {
        super.activate();
        valueTextField.addFocusListener(this);
    }
    
    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before leaving bean
     */
    public void deactivate()
    {
        super.deactivate();
        valueTextField.removeFocusListener(this);
    }
     
    //---------------------------------------------------------------------
    /**
        Overrides JPanel setVisible() method to request focus. <P>
    **/
    //---------------------------------------------------------------------
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

    //-----------------------------------------------------------------------
    /**
        Returns the value text field.
        @return the value text field.
    **/
    //-----------------------------------------------------------------------
    public ConstrainedTextField getValueTextField()
    {
        return(valueTextField);
    }

    //---------------------------------------------------------------------
    /**
        Updates the bean model, populated with data from the bean. <P>
    **/
    //---------------------------------------------------------------------
    public void updateModel()
    {
        beanModel.setNewValue(valueTextField.getText());
    }

    //---------------------------------------------------------------------
    /**
        Sets the model data into the bean fields. <P>
        @param model the bean model
    **/
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
            if (logger.isDebugEnabled()) logger.debug(
                         "entering EditStringParameterBean.setModel");
        if (model == null)
        {
            throw new NullPointerException("An attempt was made to set the EditStringParameterBean model to null.");
        }
        else if (model instanceof StringParameterBeanModel)
        {
            beanModel = (StringParameterBeanModel)model;
            updateBean();
        }
        else
        {
            logger.warn(
                         "EditStringParameterBean.setModel does not handle " + beanModel.getClass().getName() + "");
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates the bean, populated with data from the model. <P>
    **/
    //---------------------------------------------------------------------
    public void updateBean()
    {
        if (logger.isDebugEnabled()) logger.debug(
                         "entering EditStringParameterBean.updateBean");

        String group = beanModel.getParameterGroup();
        String name = beanModel.getParameterName();
        parameterGroupField.setText(retrieveText(group,group));
        parameterNameField.setText(retrieveText(name,name));

        if (beanModel.getMinValue() < 0)
        {
            parameterMinField.setText("");
            labels[MIN_LABEL].setText("");
        }
        else
        {
            parameterMinField.setText(String.valueOf(beanModel.getMinValue()));
            labels[MIN_LABEL].setText(retrieveText(labelTags[MIN_LABEL], labels[MIN_LABEL]));
        }
        if (beanModel.getMaxValue() < 0)
        {    
            parameterMaxField.setText("");
            labels[MAX_LABEL].setText("");
        }
        else
        {
            parameterMaxField.setText(String.valueOf(beanModel.getMaxValue()));
            labels[MAX_LABEL].setText(retrieveText(labelTags[MAX_LABEL], labels[MAX_LABEL]));
        }
        
        if (logger.isDebugEnabled()) logger.debug(
                    "EditStringParameterBean.updateBean beanModel = " + beanModel + "");
        if (logger.isDebugEnabled()) logger.debug(
                    "EditStringParameterBean.updateBean(" + beanModel.getValue() + ")");
        valueTextField.setText(beanModel.getValue());
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
            labels[i].setText(retrieveText(labelTags[i], labels[i]));
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
        String strResult = new String("Class: EditStringParameterBean (Revision " +
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
        EditStringParameterBean bean = new EditStringParameterBean();
        UIUtilities.doBeanTest(bean);
    }
}
