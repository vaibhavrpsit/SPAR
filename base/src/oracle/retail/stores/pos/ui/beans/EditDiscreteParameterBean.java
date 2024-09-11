/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditDiscreteParameterBean.java /main/13 2012/05/31 18:40:56 acadar Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    acadar    05/23/12 - CustomerManager refactoring
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:21:11 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/29 16:09:36  bvanschyndel
 *   @scr 4059 Set selected item as the item last selected in order to display
 *   the correct item when returning from Help screen.
 *
 *   Revision 1.4  2004/06/30 15:12:12  aschenk
 *   @scr 5892 - changed the Edit Parameter screen to default to the saved value and not the first in the list.
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
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
 *    Rev 1.1   Sep 15 2003 16:03:04   dcobb
 * Migrate to JDK 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:10:18   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Sep 03 2002 16:05:04   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Aug 14 2002 18:17:28   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jun 21 2002 18:26:36   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 14:54:00   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:55:02   msg
 * Initial revision.
 *
 *    Rev 1.8   Mar 04 2002 14:15:34   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.7   16 Feb 2002 10:17:10   baa
 * fix required field logic
 * Resolution for POS SCR-1306: Invalid Data Notice missing after selecting Enter with no data for text area fields
 *
 *    Rev 1.6   13 Feb 2002 17:34:30   baa
 * fix ui boxes
 * Resolution for POS SCR-1240: VisaRefPhoneNumber parameter value field box too small to see data
 *
 *    Rev 1.5   04 Feb 2002 14:03:24   KAC
 * If nothing in list is selected, selects the first element.
 * Resolution for POS SCR-998: No highlight on Discrete Parameter Editor screens until arrow key is selected
 *
 *    Rev 1.4   24 Jan 2002 09:29:50   KAC
 * Modified updateBean, so that If there are no choices explicitly
 * set, it checks the editValueField for choices.
 * Resolution for POS SCR-372: Modify Parameter UI for register level editing
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
    This bean enables the editing of a parameter that has a discrete set of
    possible values, e.g., Yes/No.
    @version $Revision: /main/13 $
**/
//------------------------------------------------------------------------------

public class EditDiscreteParameterBean extends CycleRootPanel
{
    /** revision number **/
    public static final String revisionNumber = "$Revision: /main/13 $";

    public static String[] labelText =
    {
        "Parameter Group:", "Parameter Name:", "Parameter Value:"
    };

    public static String[] labelTags =
    {
        "ParameterGroupLabel", "ParameterNameLabel", "ParameterValueLabel"
    };

    protected JLabel[] labels = null;

    protected DiscreteParameterBeanModel beanModel = new DiscreteParameterBeanModel();

    protected JLabel parameterGroupField = null;
    protected JLabel parameterNameField = null;
    protected ValidatingComboBox valueChoiceList = null;
    protected JScrollPane valueChoicePane = null;

    protected Vector choices = null;
    //---------------------------------------------------------------------
    /**
        Constructs bean.
    **/
    //---------------------------------------------------------------------
    public EditDiscreteParameterBean()
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
        setName("EditDiscreteParameterBean");
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
            labels[i].setHorizontalAlignment(JLabel.LEFT);
        }

        // create non-enterable display fields
        parameterGroupField = uiFactory.createLabel("parameterGroup", null, UI_LABEL);
        parameterNameField  = uiFactory.createLabel("parameterName", null, UI_LABEL);

        valueChoiceList = uiFactory.createValidatingComboBox("valueChoiceList");

    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the layout and lays out the components. <P>
     */
    //--------------------------------------------------------------------------
    protected void initLayout()
    {
        JComponent[] fields =
        {
            parameterGroupField, parameterNameField,
            valueChoiceList
        };
        UIUtilities.layoutDataPanel(this, labels, fields);
    }

    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before display. <P>
     */
    //--------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        valueChoiceList.addFocusListener(this);
    }
     
    //--------------------------------------------------------------------------
    /**
     *  The framework calls this method just before leaving bean
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        valueChoiceList.removeFocusListener(this);
    } 
     
    //---------------------------------------------------------------------
    /**
        Overrides JPanel setVisible() method to request focus. <P>
    **/
    //---------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag)
        {
            setCurrentFocus(valueChoiceList);
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates the bean model, populated with data from the bean. <P>
    **/
    //---------------------------------------------------------------------
    public void updateModel()
    {
        // store tag and not the translated value
        beanModel.setNewValue((String)choices.elementAt(valueChoiceList.getSelectedIndex()));
    }

    //---------------------------------------------------------------------
    /**
        Sets the model data into the bean fields. <P>
        @param model the bean model
    **/
    //---------------------------------------------------------------------
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("An attempt was made to set the EditDiscreteParameterBean model to null.");
        }
        else
        {
            if (model instanceof DiscreteParameterBeanModel)
            {
                beanModel = (DiscreteParameterBeanModel)model;
                updateBean();
            }
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates the bean, populated with data from the model. <P>
    **/
    //---------------------------------------------------------------------
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void updateBean()
    {
        String group = beanModel.getParameterGroup();
        String name = beanModel.getParameterName();
        parameterGroupField.setText(retrieveText(group,group));
        parameterNameField.setText(retrieveText(name,name));
        choices = beanModel.getValueChoices();
        Vector translatedChoices = new Vector();

        // If there are no choices explicitly set, check the editValueField
        if (choices == null)
        {
            Object editValueField = beanModel.getValue("editValueField");

            if (editValueField instanceof POSListModel)
            {
                choices = ((POSListModel)editValueField).toVector();
            }
        }

        if (choices != null)
        {
           // retrieve translated text for parameter values
            translatedChoices = new Vector(choices.size());
            for (int i=0; i < choices.size(); i++) 
            {
               String value = retrieveText((String)choices.elementAt(i),(String)choices.elementAt(i));
               translatedChoices.add(i,value);
            }
            valueChoiceList.setModel(new ValidatingComboBoxModel(translatedChoices));
        }
        String value = beanModel.getNewValue();
        if (value == null)
        {
            value = beanModel.getValue();
        }
        valueChoiceList.setSelectedItem(retrieveText(value, value));

        // Make sure something is selected
        if (valueChoiceList.getSelectedIndex() < 0)
        {
            valueChoiceList.setSelectedIndex(0);
        }
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
            labels[i].setText(retrieveText(labelTags[i],
                                           labels[i]));
        }
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
        Returns default display string. <P>
        @return String representation of object
    **/
    //---------------------------------------------------------------------
    public String toString()
    {
        String strResult = new String("Class: EditDiscreteParameterBean (Revision " +
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
        EditDiscreteParameterBean bean = new EditDiscreteParameterBean();
        UIUtilities.doBeanTest(bean);
    }
}                                       // end class EditDiscreteParameterBean
