/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ReasonCodeListBean.java /main/12 2011/12/05 12:16:24 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:34 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:24:32 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:13:34 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/08/12 18:40:03  mweis
 *   @scr 3771 Parameter lists weren't scrolling when selections were made by using keyboard letter.
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
 *    Rev 1.1   Sep 16 2003 17:53:00   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:11:50   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.5   Jun 27 2003 13:49:32   bwf
 * isAlreadyInternationalized added.
 * Resolution for 2269: Tags instead of names displaying in the Reason Code List Screen
 * 
 *    Rev 1.4   May 20 2003 10:40:50   adc
 * changes for internationalization
 * Resolution for 2286: Edit Reason Code Screen not displaying correct Name and ID information
 * 
 *    Rev 1.3   Sep 06 2002 17:25:34   baa
 * allow for currency to be display using groupings
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Sep 05 2002 16:50:26   baa
 * I18n changes
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Aug 14 2002 18:18:28   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:57:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:57:10   msg
 * Initial revision.
 * 
 *    Rev 1.3   Mar 09 2002 10:46:14   mpm
 * More text externalization.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java import
import java.util.Locale;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
    This bean enables the editing of a group of reason codes.
    @version $Revision: /main/12 $
**/
//---------------------------------------------------------------------
public class ReasonCodeListBean extends CycleRootPanel implements ListSelectionListener
{
    protected static final String RC_GROUP_PROMPT_LABEL = "ReasonCodeGroupPromptLabel";
    protected static final String RC_PROMPT_LABEL       = "ReasonCodePromptLabel";
    protected static final String PM_GROUP_PROMPT_LABEL = "ParameterGroupPromptLabel";
    protected static final String PM_PROMPT_LABEL       = "ParameterPromptLabel";

    protected static final String RC_GROUP_PROMPT = "Reason Code Group:";
    protected static final String RC_PROMPT       = "Reason Code:";
    protected static final String PM_GROUP_PROMPT = "Parameter Group:";
    protected static final String PM_PROMPT       = "Parameter:";

    protected static String DEFAULT_TEXT = "Default:";
    protected static String DEFAULT_TEXT_LABEL = "DefaultLabel";
    protected static String MODIFY_TEXT  = "Modifiable:";

    protected static String[] CHOICES = {"Yes", "No"};

    protected ReasonCodeGroupBeanModel beanModel = new ReasonCodeGroupBeanModel();

    protected JLabel reasonCodeGroupLabel = null;
    protected JLabel defaultNameLabel = null;
    protected JList modifiableChoiceList = null;
    protected JList reasonCodeChoiceList = null;

    protected JLabel groupPromptLabel = null;
    protected JLabel defaultPromptLabel = null;
    protected JLabel modifiablePromptLabel = null;
    protected JLabel reasonCodePromptLabel = null;

    protected JScrollPane reasonCodeScrollPane = null;
    protected JScrollPane modifiableChoicePane = null;
    //---------------------------------------------------------------------
    /**
        Constructs bean.
    **/
    //---------------------------------------------------------------------
    public ReasonCodeListBean()
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
        setName("ReasonCodeListBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    //---------------------------------------------------------------------
    /**
     *    Initializes the bean compoenents.
     */
    protected void initComponents()
    {
        // create the labels
        groupPromptLabel      = uiFactory.createLabel(RC_GROUP_PROMPT, null, UI_LABEL);
        defaultPromptLabel    = uiFactory.createLabel(retrieveText(DEFAULT_TEXT_LABEL,
                                                                   DEFAULT_TEXT), null, UI_LABEL);
        modifiablePromptLabel = uiFactory.createLabel(MODIFY_TEXT, null, UI_LABEL);
        reasonCodePromptLabel = uiFactory.createLabel(RC_PROMPT, null, UI_LABEL);

        // create the display and selection components
        reasonCodeGroupLabel = uiFactory.createLabel("reasonCodeGroup", null, UI_LABEL);
        defaultNameLabel     = uiFactory.createLabel("defaultName", null, UI_LABEL);

        modifiableChoicePane = uiFactory.createSelectionList("reasonCodeList", "large");
        modifiableChoiceList = (JList)modifiableChoicePane.getViewport().getView();
        modifiableChoiceList.setListData(CHOICES);

        reasonCodeScrollPane = uiFactory.createSelectionList("reasonCodeList", "large");
        reasonCodeChoiceList = (JList) reasonCodeScrollPane.getViewport().getView();
        reasonCodeChoiceList.setListData(CHOICES);
       
    }

    //---------------------------------------------------------------------
    /**
     *    Initializes the bean layout.
     */
    protected void initLayout()
    {
        JLabel[] labels =
        {
            groupPromptLabel, defaultPromptLabel,
            modifiablePromptLabel, reasonCodePromptLabel
        };
        JComponent[] comps =
        {
            reasonCodeGroupLabel, defaultNameLabel,
            modifiableChoicePane, reasonCodeScrollPane
        };
        UIUtilities.layoutDataPanel(this, labels, comps);
    }

    //---------------------------------------------------------------------
    /**
        Activates bean and fields. <P>
    **/
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();

        if (beanModel.getModifyingParameter())
        {
            modifiablePromptLabel.setVisible(true);
            modifiableChoicePane.setVisible(true);
        }
        else
        {
            modifiablePromptLabel.setVisible(false);
            modifiableChoicePane.setVisible(false);
        }
        
        reasonCodeChoiceList.addFocusListener(this);
        reasonCodeChoiceList.addListSelectionListener(this);
    }
    
    //---------------------------------------------------------------------
    /**
        Deactivates bean and fields. <P>
    **/
    //---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate(); 
        reasonCodeChoiceList.removeFocusListener(this);
        reasonCodeChoiceList.removeListSelectionListener(this);
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
            setCurrentFocus(reasonCodeChoiceList);
        }
    }

    //---------------------------------------------------------------------
    /**
        Updates the bean model, populated with data from the bean. <P>
    **/
    //---------------------------------------------------------------------
    public void updateModel()
    {
        
        beanModel.setReasonCodeSelected(reasonCodeChoiceList.getSelectedIndex());
        beanModel.setModifiableValue((String)modifiableChoiceList.getSelectedValue());
        int intSelIndex = reasonCodeChoiceList.getSelectedIndex();
        beanModel.setReasonCodeSelectionIndex(intSelIndex);
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
            throw new NullPointerException("An attempt was made to set the ReasonCodeListBean model to null.");
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
        reasonCodeGroupLabel.setText(UIUtilities.retrieveCommonText(beanModel.getGroupName(),beanModel.getGroupName()));
        if(!isAlreadyInternationalized(beanModel.getGroupName()))
        {
            defaultNameLabel.setText(UIUtilities.retrieveCommonText(beanModel.getDefaultReasonCode(),beanModel.getDefaultReasonCode()));
        }
        else
        {
            defaultNameLabel.setText(beanModel.getDefaultReasonCode());
        }

        Vector reasonCodes = beanModel.getReasonCodes();
        Vector rcNames = new Vector();
        // Collect the reason code name into a Vector to be displayed
        if(!isAlreadyInternationalized(beanModel.getGroupName()))
        {
            for (int i = 0; i < reasonCodes.size(); i++)
            {
                ReasonCode code = (ReasonCode)reasonCodes.elementAt(i);
                if (code.getEnabled())
                {
                    rcNames.addElement(UIUtilities.retrieveCommonText(code.getReasonCodeName(),code.getReasonCodeName()));
                }
            }
        }
        else
        {
            Locale defaultLocale = LocaleMap.getLocale(LocaleConstantsIfc.DEFAULT_LOCALE);
            Locale employeeLocale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
            if(defaultLocale != employeeLocale)
            {
            //    UIUtilities.getReasonCodeTextEntries();    
            }
            for (int i = 0; i < reasonCodes.size(); i++)
            {
                ReasonCode code = (ReasonCode)reasonCodes.elementAt(i);
                if (code.getEnabled())
                {
                    rcNames.addElement(code.getReasonCodeName());
                }
            }
        }
        reasonCodeChoiceList.setListData(rcNames);
        String selected =beanModel.getReasonCodeSelected().getReasonCodeName(); 
        reasonCodeChoiceList.setSelectedValue(UIUtilities.retrieveCommonText(selected,selected), true);

        // If the user is at the corporate level, let him change the
        // modifiability value
        if (ReasonCodesCommon.CORPORATION.equalsIgnoreCase(beanModel.getGroupLevel()))
        {
            Vector modifiableChoices = beanModel.getModifiableChoices();
            // get translated descriptions
            for (int i=0; i < modifiableChoices.size(); i++)
            {
            
              modifiableChoices.setElementAt(UIUtilities.retrieveCommonText((String)modifiableChoices.elementAt(i),
                                                                            (String)modifiableChoices.elementAt(i)),i);
            }
            modifiableChoiceList.setListData(modifiableChoices);
            modifiableChoiceList.setSelectedValue(UIUtilities.retrieveCommonText(beanModel.getModifiableValue(),
                                                                                 beanModel.getModifiableValue()), true);
            modifiablePromptLabel.setVisible(true);
            modifiableChoiceList.setVisible(true);
        }
        // If the user is not at the corporate level, don't distract him
        // with the modifiableChoiceList
        else
        {
            modifiablePromptLabel.setVisible(false);
            modifiableChoiceList.setVisible(false);
        }

        if (beanModel.getModifyingParameter())
        {
            groupPromptLabel.setText(retrieveText(PM_GROUP_PROMPT_LABEL, PM_GROUP_PROMPT));
            reasonCodePromptLabel.setText(retrieveText(PM_PROMPT_LABEL,   PM_PROMPT));
                                                      
        }
        else
        {
            groupPromptLabel.setText(retrieveText(RC_GROUP_PROMPT_LABEL,RC_GROUP_PROMPT));
            reasonCodePromptLabel.setText(retrieveText(RC_PROMPT_LABEL, RC_PROMPT));
            defaultPromptLabel.setText(retrieveText(DEFAULT_TEXT_LABEL,DEFAULT_TEXT));                                           
        }
        
        // This bean stays on the screen through multiple iterations
        // of showScreen(); if the screen is already visible, setVisible()
        // does not get called.  Requesting focus here ensures the focus
        // remains on the list.
        setCurrentFocus(reasonCodeChoiceList);
        reasonCodeChoiceList.setSelectedIndex(beanModel.getReasonCodeSelectionIndex());
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        if (beanModel != null &&
            beanModel.getModifyingParameter())
        {
            groupPromptLabel.setText(retrieveText(PM_GROUP_PROMPT_LABEL,PM_GROUP_PROMPT));
            reasonCodePromptLabel.setText(retrieveText(PM_PROMPT_LABEL,PM_PROMPT));
                                                       
        }
        else
        {
            groupPromptLabel.setText(retrieveText(RC_GROUP_PROMPT_LABEL,RC_GROUP_PROMPT));
            reasonCodePromptLabel.setText(retrieveText(RC_PROMPT_LABEL, RC_PROMPT));
            defaultPromptLabel.setText(retrieveText(DEFAULT_TEXT_LABEL,DEFAULT_TEXT));                                               
        }
         
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /** 
        Determines whether reason codes already internationalized by group type.
        @param String group name
        @return boolean internationalized     
     **/
    //---------------------------------------------------------------------
    public boolean isAlreadyInternationalized(String groupName)
    {
        boolean internationalized = false;
        if(groupName.equals("MarkdownPercentReasonCodes") ||
           groupName.equals("MarkdownAmountReasonCodes") ||
           groupName.equals("PreferredCustomerDiscount") ||
           groupName.equals("ItemDiscountByAmount") ||
           groupName.equals("ItemDiscountByPercentage") ||
           groupName.equals("TransactionDiscountByAmount") ||
           groupName.equals("TransactionDiscountByPercentage") ||
           groupName.equals("UnitOfMeasure") ||
           groupName.equals("ShippingMethod") ||
           groupName.equals("Department"))
           {
               internationalized = true;
           }
        
        return(internationalized);
    }
    
    //----------------------------------------------------------------------
    /**
     Callback method when the user selects a different item in the list.
     @param evt The event.
     @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     **/
    //----------------------------------------------------------------------
    public void valueChanged(ListSelectionEvent evt)
    {
        // When things have calmed down...
        if (evt.getValueIsAdjusting() == false && reasonCodeChoiceList.getSelectedIndex() > -1)
        {
            // ... ensure the user can see their selection.
            reasonCodeChoiceList.ensureIndexIsVisible(reasonCodeChoiceList.getSelectedIndex());
        }
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
        UIUtilities.doBeanTest(new ReasonCodeListBean());
    }
}                                       // end class ReasonCodeListBean
