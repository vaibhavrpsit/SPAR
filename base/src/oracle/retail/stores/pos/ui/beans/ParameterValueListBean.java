/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ParameterValueListBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:19 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:00 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:12:59 PM  Robert Pearse   
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
 *    Rev 1.2   Oct 01 2003 14:06:32   lzhao
 * Cannot use setReasonCodeSelected((String)valueChoiceList.getSelectedValue()) for ReasonCodeGroupModel. The text on the list is the content of the parameter name. The parameter name in the model is the parameter name itself, not the name value. Two of them are not comparable.
 * Resolution for 2511: Selecting to move up or move down a value always moves the 1st value in Create List Editors
 * 
 *    Rev 1.1   Sep 16 2003 17:52:56   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:11:34   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.4   Jun 24 2003 16:29:04   RSachdeva
 * VALUE_LIST_PROMPT_LABEL
 * Resolution for POS SCR-2469: Tags - Parameters - Order and Reconciliation have "Tags"
 * 
 *    Rev 1.3   Sep 06 2002 17:25:34   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Aug 14 2002 18:18:20   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jun 21 2002 18:26:48   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 14:55:28   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:56:50   msg
 * Initial revision.
 *
 *    Rev 1.6   13 Feb 2002 17:34:36   baa
 * fix ui boxes
 * Resolution for POS SCR-1240: VisaRefPhoneNumber parameter value field box too small to see data
 *
 *    Rev 1.5   11 Feb 2002 17:00:40   baa
 * fix  value field box
 * Resolution for POS SCR-1219: LayawayLegalStmt parameter value field box too small to see data
 *
 *    Rev 1.4   10 Feb 2002 14:30:24   KAC
 * Now handles the situation where there are no values.
 * Resolution for POS SCR-1226: Update list parameter value editor per new requirements
 *
 *    Rev 1.3   07 Feb 2002 09:25:28   KAC
 * Now lays out the scroll pane instead of the list.
 * Resolution for POS SCR-1130: Adding more than 14 values to a Create List Parameter Editor screen causes bottom of list to leave screen
 *
 *    Rev 1.2   04 Feb 2002 13:42:46   KAC
 * Changed prompt to "Parameter Value List"
 * Resolution for POS SCR-1008: Text Errors on all Create List Parameter Editor screens
 * Resolution for POS SCR-1009: No highlight after deletion on Value List until arrow key is selected on Create List Parameter Editor screens
 *
 *    Rev 1.1   04 Feb 2002 11:39:50   KAC
 * updateBean now makes sure something is selected.
 * Resolution for POS SCR-1010: Deleting every value on Create List Parameter Editor screens hangs application
 *
 *    Rev 1.0   22 Jan 2002 13:39:14   KAC
 * Initial revision.
 * Resolution for POS SCR-672: Create List Parameter Editor
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java import
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
    This bean enables the editing of a group of reason codes.
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//------------------------------------------------------------------------------

public class ParameterValueListBean extends CycleRootPanel
{
    protected static final String GROUP_PROMPT = "Parameter Group:";
    protected static final String GROUP_PROMPT_LABEL = "ParameterGroupLabel";
    protected static final String NAME_PROMPT = "Parameter Name:";
    protected static final String NAME_PROMPT_LABEL = "ParameterNameLabel";
    protected static final String VALUE_LIST_PROMPT = "Parameter Value List:";
    protected static final String VALUE_LIST_PROMPT_LABEL = "ParameterValueListLabel";
       
    protected ReasonCodeGroupBeanModel beanModel = new ReasonCodeGroupBeanModel();

    protected JLabel parameterGroupLabel = null;
    protected JLabel parameterNameLabel = null;
    protected JList valueChoiceList = null;

    protected JLabel groupPromptLabel = null;
    protected JLabel parameterNamePromptLabel = null;
    protected JLabel valuePromptLabel = null;

    protected JScrollPane valueScrollPane = null;

     //---------------------------------------------------------------------
    /**
       Constructs bean.
    **/
    //---------------------------------------------------------------------
    public ParameterValueListBean()
    {
        super();
        initialize();
    }

    //---------------------------------------------------------------------
    /**
       Initializes the class.
    **/
    //---------------------------------------------------------------------
    private void initialize()
    {
        setName("ParameterValueListBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    //---------------------------------------------------------------------
    /**
     *  Initializes the bean compoenents.
     */
    protected void initComponents()
    {
        // create the labels
        groupPromptLabel = uiFactory.createLabel(GROUP_PROMPT, null, UI_LABEL);
        parameterNamePromptLabel = uiFactory.createLabel(NAME_PROMPT, null, UI_LABEL);
        valuePromptLabel = uiFactory.createLabel(VALUE_LIST_PROMPT, null, UI_LABEL);

        // create the display and selection components
        parameterGroupLabel = uiFactory.createLabel("valueGroup", null, UI_LABEL);
        parameterNameLabel  = uiFactory.createLabel("defaultName", null, UI_LABEL);


        valueScrollPane = uiFactory.createSelectionList("valueChoiceList", "large");
        valueScrollPane.setFocusable(true);
        valueChoiceList = (JList) valueScrollPane.getViewport().getView();
        
     }

    //---------------------------------------------------------------------
    /**
     *  Initializes the bean layout.
     */
    protected void initLayout()
    {
        JLabel[] labels =
        {
            groupPromptLabel, parameterNamePromptLabel, valuePromptLabel
        };
        JComponent[] comps =
        {
            parameterGroupLabel, parameterNameLabel, valueScrollPane
        };
        UIUtilities.layoutDataPanel(this, labels, comps);
    }

    //--------------------------------------------------------------------------
    /**
       The framework calls this method just before display
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        valueChoiceList.addFocusListener(this);
    }
    
    //---------------------------------------------------------------------
    /**
       The framework calls this method just before leaving bean
    **/
    //---------------------------------------------------------------------
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
        Vector reasonCodes = beanModel.getReasonCodes();

        // Adjust the selection
        if ((reasonCodes != null) && (reasonCodes.size() > 0))
        {
            int intSelIndex = valueChoiceList.getSelectedIndex();
            beanModel.setReasonCodeSelectionIndex(intSelIndex);
            beanModel.setReasonCodeSelected(intSelIndex);
        }
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
            throw new NullPointerException("An attempt was made to set the ParameterValueListBean model to null.");
        }
        if (model instanceof ReasonCodeGroupBeanModel)
        {
            beanModel = (ReasonCodeGroupBeanModel)model;
            updateBean();
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
        String name = beanModel.getGroupName();
        parameterGroupLabel.setText(retrieveText(group,group));
        parameterNameLabel.setText(retrieveText(name,name));

        Vector values = beanModel.getReasonCodes();
        Vector rcNames = new Vector();

        if ((values != null) && (values.size() > 0))
        {
            // Collect the reason code name into a Vector to be displayed
            for (int i = 0; i < values.size(); i++)
            {
                ReasonCode code = (ReasonCode)values.elementAt(i);
                if (code.getEnabled())
                {
                    rcNames.addElement(UIUtilities.retrieveCommonText(code.getReasonCodeName(),code.getReasonCodeName()));
                }
            }
            valueChoiceList.setListData(rcNames);
            valueChoiceList.setSelectedValue(UIUtilities.retrieveCommonText(beanModel.getReasonCodeSelected().getReasonCodeName(),
                                                                            beanModel.getReasonCodeSelected().getReasonCodeName()), true);

            // Make sure something is selected
            if (valueChoiceList.getSelectedIndex() < 0)
            {
                valueChoiceList.setSelectedIndex(0);
            }
        }
        else
        {
            valueChoiceList.setListData(rcNames);
        }

         // This bean stays on the screen through multiple iterations
        // of showScreen(); if the screen is already visible, setVisible()
        // does not get called.  Requesting focus here ensures the focus
        // remains on the list.
        setCurrentFocus(valueChoiceList);
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
         groupPromptLabel.setText(retrieveText(GROUP_PROMPT_LABEL,GROUP_PROMPT));
         parameterNamePromptLabel.setText(retrieveText(NAME_PROMPT_LABEL,NAME_PROMPT));
         valuePromptLabel.setText(retrieveText(VALUE_LIST_PROMPT_LABEL,VALUE_LIST_PROMPT));
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
        UIUtilities.doBeanTest(new ParameterValueListBean());
    }
}                                       // end class ParameterValueListBean
