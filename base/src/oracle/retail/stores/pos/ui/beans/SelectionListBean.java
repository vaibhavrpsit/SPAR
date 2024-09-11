/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SelectionListBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:51 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/22/10 - cleanup
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:29:54 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:08 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:08 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/08/12 18:40:03  mweis
 *   @scr 3771 Parameter lists weren't scrolling when selections were made by using keyboard letter.
 *
 *   Revision 1.4  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
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
 *    Rev 1.2   Nov 04 2003 10:50:06   kll
 * SCR 2606: Label Change
 * Resolution for 2606: At Suspend Reason screen, label name is "Select Item" but should be "Reason Code"
 * 
 *    Rev 1.1   Sep 16 2003 17:53:08   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * 
 *    Rev 1.0   Aug 29 2003 16:12:04   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.3   Sep 03 2002 16:07:52   baa
 * externalize domain  constants and parameter values
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.2   Aug 07 2002 19:34:26   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jun 21 2002 18:26:56   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 14:49:48   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:57:32   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 04 2002 14:15:40   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   24 Jan 2002 16:02:12   KAC
 * Can now work with a DataInputBeanModel.
 * Resolution for POS SCR-372: Modify Parameter UI for register level editing
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.plaf.UIFactoryIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean takes input from a list.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class SelectionListBean extends ValidatingBean implements ListSelectionListener
{
    private static final long serialVersionUID = 2715741673374921315L;

    /** revision number **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** reason code label */
    public static final String REASON_CODE_LABEL = "ReasonCodeLabel";

    /** label for the list */
    protected JLabel choiceLabel = null;

    /** text for the label */
    protected String labelText = "Reason Code:";

    /** text for the label */
    protected String labelTags = "ReasonCodeColonLabel";

    /** list to display */
    protected JList choiceList = null;

    /** scroll pane that contains the list */
    protected JScrollPane scrollPane = null;

    /** the bean model */
    protected POSBaseBeanModel beanModel = null;

    /** flag that indicates if bean needs updating 
        @deprecated as of release 5.5 obsolete code
    */
    protected boolean dirtyModel = true;

    /** flag that indicates if the list should get focus */
    protected boolean focused = true;

    /** vector with list of tags for choices **/
    protected Vector<?> tag_list = null;

    /**
     *  Default constructor.
     */
    public SelectionListBean()
    {
        super();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        choiceList.addFocusListener(this);
        choiceList.addListSelectionListener(this);
    }
    
    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#deactivate()
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        choiceList.removeFocusListener(this);
        choiceList.removeListSelectionListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#configure()
     */
    @Override
    public void configure()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);
        beanModel = new SelectionListBeanModel();

        choiceLabel =
            uiFactory.createLabel(labelText, labelText, null, UI_LABEL);

        scrollPane = uiFactory.createSelectionList("choiceList", UIFactoryIfc.DIMENSION_LARGE);
        choiceList = (JList)scrollPane.getViewport().getView();

        initComponents();
        initLayout();
    }

    /**
     * Initializes the bean components.
     */
    protected void initComponents()
    {

    }

    /**
     * Creates the layout and lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this, new JLabel[] { choiceLabel },
                new JComponent[] { scrollPane });
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#getPOSBaseBeanModel()
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#setModel(oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException(
                "Attempt to set " +
                Util.getSimpleClassName(this.getClass()) +
                " model to null.");
        }
        beanModel = (POSBaseBeanModel)model;
        updateBean();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#updateBean()
     */
    @Override
    protected void updateBean()
    {
        if(beanModel instanceof SelectionListBeanModel)
        {
            SelectionListBeanModel myModel =
                (SelectionListBeanModel)beanModel;
            /*
             * Don't replace the label text if it's not specified.
             * This allows the label to be specified in the uicfg.xml.
             */
            String text = myModel.getLabelText();

            if (text != null && text.length() > 0)
            {
                labelText = text;
            }
            choiceLabel.setText(labelText);
            tag_list = myModel.getChoices();
            String[] choices = new String[tag_list.size()];
            String tag = null;
            for (int i = 0; i < tag_list.size() -1 ; i++)
            {
               tag = (String)tag_list.get(i);
               choices[i]= retrieveText(tag,tag);
            }

            choiceList.setListData(choices);
            int selected = tag_list.indexOf(myModel.getSelectionValue());
            choiceList.setSelectedIndex(selected);
         }
        else if(beanModel instanceof DataInputBeanModel)
        {

            DataInputBeanModel dibModel = (DataInputBeanModel)beanModel;
            Object objChoices = dibModel.getValue("choiceList");

            if (objChoices instanceof POSListModel)
            {
                POSListModel myModel = (POSListModel)objChoices;
                tag_list = myModel.toVector();
                String[] choices = new String[tag_list.size()];
                String tag = null;
                for (int i = 0; i < tag_list.size() ; i++)
                {
                   tag = (String)tag_list.get(i);
                   choices[i]= retrieveText(tag,tag);
                }
                choiceList.setListData(choices);
                int selected = tag_list.indexOf(myModel.getSelectedValue());
                choiceList.setSelectedIndex(selected);
            }
         }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#updateModel()
     */
    @Override
    public void updateModel()
    {
        String selected = null;
        if(beanModel instanceof SelectionListBeanModel)
        {
            SelectionListBeanModel myModel =
                (SelectionListBeanModel)beanModel;

            selected = (String)tag_list.get(choiceList.getSelectedIndex());
            myModel.setSelectionValue(selected);
        }
        else if(beanModel instanceof DataInputBeanModel)
        {
            DataInputBeanModel dibModel = (DataInputBeanModel)beanModel;
            Object objChoices = dibModel.getValue("choiceList");

            if (objChoices instanceof POSListModel)
            {
                POSListModel myModel = (POSListModel)objChoices;
                selected = (String)tag_list.get(choiceList.getSelectedIndex());
                myModel.setSelectedValue(selected);
            }
        }
    }

    /**
     * Returns if this list should receive the screen focus. This should be true
     * for screens where no other component will receive focus.
     * 
     * @return Whether the list should receive focus.
     */
    public boolean isFocused()
    {
        return focused;
    }

    /**
     * Sets the focused attribute of this list. If true, the list will request
     * focus when displayed.
     * 
     * @param aValue true if focused, false otherwise
     */
    public void setFocused(boolean aValue)
    {
        focused = aValue;
    }

    /**
     * Sets the label for the selection list field.
     * 
     * @param text the label
     */
    public void setLabelText(String text)
    {
        labelText = text;
    }

    /**
     * Sets the label tag for the selection list field.
     * 
     * @param text the label
     */
    public void setLabelTags(String text)
    {
        labelTags = text;
        updatePropertyFields();
    }

    /**
     * Updates fields based on properties.
     */
    protected void updatePropertyFields()
    {
        choiceLabel.setText(retrieveText(labelTags, labelText));
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#setVisible(boolean)
     */
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        // if visible is true, set the selection and request focus
        if (aFlag && !errorFound() && choiceList.getModel().getSize() > 0)
        {
            if (choiceList.getSelectedIndex() == -1)
            {
                choiceList.setSelectedIndex(0);
            }
            choiceList.ensureIndexIsVisible(choiceList.getSelectedIndex());

            if (isFocused() && choiceList.isEnabled())
            {
                setCurrentFocus(choiceList);
            }
            else
            {
                choiceList.setFocusable(false);
            }
        }
    }

    /**
     * Callback method when the user selects a different item in the list.
     * 
     * @param evt The event.
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent evt)
    {
        // When things have calmed down...
        if (evt.getValueIsAdjusting() == false && choiceList.getSelectedIndex() > -1)
        {
            // ... ensure the user can see their selection.
            choiceList.ensureIndexIsVisible(choiceList.getSelectedIndex());
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    @Override
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) +
                "(Revision " + getRevisionNumber() + ") @" + hashCode());
    }

    /**
     * Returns the revision number of the class.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * Main entry point for testing.
     * 
     * @param args command line parameters
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        SelectionListBean bean = new SelectionListBean();
        bean.configure();

        UIUtilities.doBeanTest(bean);
    }
}
