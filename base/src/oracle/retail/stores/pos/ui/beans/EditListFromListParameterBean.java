/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EditListFromListParameterBean.java /main/17 2012/12/03 15:19:09 vbongu Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vbongu    11/26/12 - Dashboard changes
 *    cgreene   10/25/10 - redo to present parameter values in side-by-side
 *                         editable list
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/13/2005 4:42:44 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:27:52 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:12 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:44 PM  Robert Pearse
 *
 *   Revision 1.7  2004/07/28 19:38:57  bvanschyndel
 *   @scr 5163 Disabled highlighting of New Value List but keeps scroll bar active.
 *
 *   Revision 1.6  2004/07/20 18:40:15  awilliam
 *   @scr 4031 selecting item in new value list for security access does not give focus
 *
 *   Revision 1.5  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.4  2004/05/20 13:36:55  dfierling
 *   @scr 5163 - disabled valueChoiceList listbox
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Jan 30 2004 15:48:22   rrn
 * Modified processAdd( ) and processDelete( ) to deal get text correctly from ReasonCodeValue objects.
 * Resolution for 3769: Parameterize Manager Override behavior
 *
 *    Rev 1.1   Sep 15 2003 16:03:06   dcobb
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
 *    Rev 1.1   Jun 21 2002 18:26:38   baa
 * externalize parameter names,
 * start formatting currency base on locale
 * Resolution for POS SCR-1624: Localization Support
 *
 *    Rev 1.0   Apr 29 2002 14:54:02   msg
 * Initial revision.
 *
 *    Rev 1.0   Mar 18 2002 11:55:02   msg
 * Initial revision.
 *
 *    Rev 1.5   Mar 04 2002 14:15:36   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   13 Feb 2002 17:34:30   baa
 * fix ui boxes
 * Resolution for POS SCR-1240: VisaRefPhoneNumber parameter value field box too small to see data
 *
 *    Rev 1.3   08 Feb 2002 17:33:16   KAC
 * No longer insists on having at least one value.
 * Resolution for POS SCR-1176: Update "list from list" parameter editing
 *
 *    Rev 1.2   01 Feb 2002 14:31:18   KAC
 * Added performValidateAction() to handle events from the
 * "Add" and "Delete" buttons.
 * Resolution for POS SCR-672: Create List Parameter Editor
 *
 *    Rev 1.1   31 Jan 2002 13:51:10   KAC
 * Removed println().
 * Resolution for POS SCR-672: Create List Parameter Editor
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.io.Serializable;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;

import javax.swing.DefaultListCellRenderer;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.parameter.ParameterConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.manager.parameter.ReasonCodeValue;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.config.ConfigurationException;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSJFCUISubsystem;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.behavior.ButtonListener;

/**
 * This bean enables the editing of a parameter that has a discrete set of
 * possible values, e.g., Yes/No.
 *
 * @version $Revision: /main/17 $
 */
public class EditListFromListParameterBean extends ValidatingBean
    implements ButtonListener, ListSelectionListener
{
    private static final long serialVersionUID = -6014302385056740633L;

    /** revision number */
    public static final String revisionNumber = "$Revision: /main/17 $";

    public static String[] labelText =
    {
        "Parameter Group:", "Parameter Name:",
        "Select From List:", "New Value List:"
    };

    public static String[] labelTags =
    {
        "ParameterGroupLabel", "ParameterNameLabel",
        "SelectFromListLabel", "NewValueListLabel"
    };

    /** The labels that can be changed via the {@link #updatePropertyFields()} method. */
    protected JLabel[] labels = null;

    protected JLabel parameterGroupField = null;
    protected JLabel parameterNameField = null;

    /** The list containing the potential parameter values. */
    protected JList listPotentialChoices = null;
    /** The list containing the chosen parameter values. */
    protected JList listActualChoices = null;

    /** The scroll panes containing our JLists */
    protected JScrollPane panePotentialChoices = null;
    protected JScrollPane paneActualChoices = null;

    /** The renderer used to renderer the lists. Defaults to ParameterListValueRenderer. */
    protected ListCellRenderer renderer = new ParameterListValueRenderer();

    /** Maintain a reference to the data presented in the lists for easy manipulation. */
    protected Vector<Serializable> vectorPotentialChoices;
    protected Vector<Serializable> vectorActualChoices;
    
    protected String paramName = null;

    /**
     * Constructs bean.
     */
    public EditListFromListParameterBean()
    {
        super();
        initialize();
    }

    /**
     * Modify the lists according to which button got pressed.
     *
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent event)
    {
        super.actionPerformed(event);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        listPotentialChoices.addFocusListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#deactivate()
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        listPotentialChoices.removeFocusListener(this);
    }

    /**
     * Return the renderer used to render the lists.
     *
     * @return
     */
    public ListCellRenderer getRenderer()
    {
        return renderer;
    }

    /**
     * Returns the revision number.
     *
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#setModel(oracle.retail.stores.foundation.manager.gui.UIModelIfc)
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        super.setModel(model);
        if (model == null)
        {
            throw new NullPointerException("An attempt was made to set the EditListFromListParameterBean model to null.");
        }
        else if (model instanceof ListFromListParameterBeanModel)
        {
            beanModel = (ListFromListParameterBeanModel)model;
            updateBean();
        }
    }

    /**
     * Sets the list renderer. The resolved class must implement
     * {@link ListCellRenderer}.
     *
     * @param propValue the class name of the renderer
     */
    public void setRenderer(String propValue)
    {
        if (propValue != null)
        {
            if(renderer == null ||
               !renderer.getClass().getName().equals(propValue))
            {
                renderer = (ListCellRenderer)UIUtilities.getNamedClass(propValue);
            }
        }
        if (renderer == null)
        {
            renderer = new ParameterListValueRenderer();
        }
        if (listActualChoices != null && listActualChoices.getCellRenderer() != renderer)
        {
            listActualChoices.setCellRenderer(renderer);
        }
        if (listPotentialChoices != null && listPotentialChoices.getCellRenderer() != renderer)
        {
            listPotentialChoices.setCellRenderer(renderer);
        }
    }

    /**
     * Overrides {@link JPanel#setVisible(boolean)} method to request focus.
     */
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag && !errorFound())
        {
            setCurrentFocus(listPotentialChoices);
        }
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#toString()
     */
    @Override
    public String toString()
    {
        String strResult = new String("Class: EditListFromListParameterBean (Revision " + getRevisionNumber() + ") @"
                + hashCode());
        return (strResult);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#updateBean()
     */
    @Override
    public void updateBean()
    {
        if (beanModel instanceof ListFromListParameterBeanModel)
        {
            ListFromListParameterBeanModel lflBeanModel = (ListFromListParameterBeanModel)beanModel;

            String group = lflBeanModel.getParameterGroup();
            String name = lflBeanModel.getParameterName();
            paramName = name;
            parameterGroupField.setText(retrieveText(group,group));
            parameterNameField.setText(retrieveText(name,name));

            /* The order can change for example in Parameter distribution when
             * Central Office adds and removes values in parameters of type
             * LIST. This makes sure that the UI displays it in correct order
             */
            Serializable[] initialPotentialChoices = lflBeanModel.getPotentialValues();
            Serializable[] initialActualChoices = lflBeanModel.getNewValues();
            vectorActualChoices = new Vector<Serializable>(initialActualChoices.length);

            // Validate that the list of actual choices are among the list of potentials 
            for (int i = 0; i < initialActualChoices.length; i++)
            {
                Serializable element = initialActualChoices[i];
                if (isPresent(element, initialPotentialChoices))
                {
                    vectorActualChoices.add(element);
                }
            }

            // Create a list that potentials that does not include the actual choices
            vectorPotentialChoices = new Vector<Serializable>();

            // Build the list of potential choices
            for (int i = 0; i < initialPotentialChoices.length; i++)
            {
                // only add potential choice if its not already chosen.
                if (!vectorActualChoices.contains(initialPotentialChoices[i]))
                {
                    vectorPotentialChoices.add(initialPotentialChoices[i]);
                }
            }

            // populate selections
            listActualChoices.setListData(vectorActualChoices);

            // populate potential choices
            listPotentialChoices.setListData(vectorPotentialChoices);
            listPotentialChoices.setSelectedIndex(0);
        }
    }

    /* (non-Javadoc)
     * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
     */
    public void valueChanged(ListSelectionEvent e)
    {
        if (e.getValueIsAdjusting() && e.getSource() == listActualChoices)
        {
            listPotentialChoices.clearSelection();
        }
        else if (e.getValueIsAdjusting() && e.getSource() == listPotentialChoices)
        {
            listActualChoices.clearSelection();
        }
    }

    /**
     * Initializes the class.
     */
    protected void initialize()
    {
        setName("EditListFromListParameterBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * Initializes the components.
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
        parameterGroupField =
            uiFactory.createLabel("parameterGroup", "parameterGroup", null, UI_LABEL);
        parameterNameField =
            uiFactory.createLabel("parameterName", "parameterName", null, UI_LABEL);

        paneActualChoices = uiFactory.createSelectionList("valueChoiceList", null);
        listActualChoices = (JList)paneActualChoices.getViewport().getView();
        listActualChoices.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listActualChoices.setCellRenderer(renderer);
        listActualChoices.addListSelectionListener(this);

        panePotentialChoices = uiFactory.createSelectionList("potentialChoiceList", null);
        listPotentialChoices = (JList)panePotentialChoices.getViewport().getView();
        listPotentialChoices.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        listPotentialChoices.setCellRenderer(renderer);
        listPotentialChoices.addListSelectionListener(this);
    }

    /**
     * Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        JLabel[] headerLabels =
        {
            labels[0], labels[1]
        };
        JComponent[] headerFields =
        {
            parameterGroupField, parameterNameField,
        };
        JLabel[] listLabels =
        {
            labels[2], labels[3]
        };
        JComponent[] lists =
        {
            panePotentialChoices, paneActualChoices
        };
        UIUtilities.layoutDualListPanel(this, headerLabels, headerFields, listLabels, lists);
    }
     
    /**
     * Determines whether the provided object is present in the array.
     *
     * @param object the object whose existence we're checking for
     * @param list the array of objects to be checked
     * @return true if present; false otherwise
     */
    protected boolean isPresent(Serializable object, Serializable[] list)
    {
        boolean present = false;

        if ((list != null) && (object != null))
        {
            for (int i = 0; i < list.length && !present; i++)
            {
                present = object.equals(list[i]);
            }
        }
        return present;
    }

    /**
     * This method determines which button got pressed on the navigation button
     * bar and does the corresponding action (add or delete). If neither of
     * those were pushed, it uses the inherited "Validation" process to mail a
     * letter to the engine.
     *
     * @param event the ActionEvent contructed by the caller.
     */
    @Override
    protected void performValidateAction(ActionEvent event)
    {
        // Get the letter name and number of the button the user pressed
        // to get here.
        String letterName = event.getActionCommand();

        if ("Add".equals(letterName))
        {
            processAdd();
        }
        else if ("Delete".equals(letterName))
        {
            processDelete();
        }
        else
        {
            super.performValidateAction(event);
        }
    }

    /**
     * Recreate the list of parameter values based on the values the user has
     * asked to add.
     */
    protected void processAdd()
    {
        if (beanModel instanceof ListFromListParameterBeanModel)
        {
            int[] additions = listPotentialChoices.getSelectedIndices();
            int numToAdd = additions.length;
            
            if (numToAdd >= 0)
            {
                Serializable selection = null;

                // transfer selected value from one list model to the other
                for (int i = numToAdd - 1; i >= 0; i--)
                {
                    //limit dashboard reports to four
                    if(ParameterConstantsIfc.BASE_DashboardReports.equals(paramName) && (vectorActualChoices.size() >= 4))
                    {
                        showErrorDialog();
                        break; 
                    }
                    if (listPotentialChoices.isSelectedIndex(additions[i]))
                    {
                        selection = vectorPotentialChoices.get(additions[i]);
                        vectorActualChoices.add(selection);
                        vectorPotentialChoices.remove(additions[i]);
                    }
                }

                // sort new additions into potentials
                Collections.sort(vectorActualChoices, new ReasonCodeValueComparator());

                // update ui lists
                listActualChoices.setListData(vectorActualChoices);
                listActualChoices.setSelectedValue(selection, true);
                listPotentialChoices.setListData(vectorPotentialChoices);

                // update bean model
                Serializable[] newChoicesArray = vectorActualChoices.toArray(new Serializable[vectorActualChoices.size()]);
                ((ListFromListParameterBeanModel)beanModel).setNewValues(newChoicesArray);
            }
        }
    }

    /**
     * Recreate the list of parameter values based on the values the user has
     * asked to delete.
     */
    protected void processDelete()
    {
        if (beanModel instanceof ListFromListParameterBeanModel)
        {
            int[] deletions = listActualChoices.getSelectedIndices();
            int numToDel = deletions.length;

            if (numToDel >= 0)
            {
                // transfer selected value from one list model to the other
                for (int i = numToDel - 1; i >= 0; i--)
                {
                    if (listActualChoices.isSelectedIndex(deletions[i]))
                    {
                        Serializable selection = vectorActualChoices.get(deletions[i]);
                        vectorPotentialChoices.add(selection);
                        vectorActualChoices.remove(deletions[i]);
                    }
                }

                // sort new additions into potentials
                Collections.sort(vectorPotentialChoices, new ReasonCodeValueComparator());

                // update ui lists
                listActualChoices.setListData(vectorActualChoices);
                listPotentialChoices.setListData(vectorPotentialChoices);

                // update bean model
                Serializable[] newChoicesArray = vectorActualChoices.toArray(new Serializable[vectorActualChoices.size()]);
                ((ListFromListParameterBeanModel)beanModel).setNewValues(newChoicesArray);
            }
        }
    }

    /**
     * Updates fields based on properties.
     */
    protected void updatePropertyFields()
    {
        for (int i = 0; i < labelText.length; i++)
        {
            labels[i].setText(retrieveText(labelTags[i], labels[i]));
        }
    }
    
 
    /**
        Builds the model for the error dialog and calls show screen on
        the UISubsystem.
    **/ 
    protected void showErrorDialog()
    {
        // Set up the bean model and show the screen.  
        DialogBeanModel dialogModel = new DialogBeanModel();
        POSJFCUISubsystem ui = (POSJFCUISubsystem)UISubsystem.getInstance();
        dialogModel.setResourceID("InvalidNumOfReports");
        dialogModel.setType(DialogScreensIfc.ERROR);        
        dialogModel.setFormModel(getPOSBaseBeanModel());
        dialogModel.setFormScreenSpecName(ui.getCurrentScreenSpecName());        
        try
        {
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        catch (oracle.retail.stores.foundation.manager.gui.UIException uie)
        {
            Logger log = Logger.getLogger(oracle.retail.stores.pos.ui.beans.EditListFromListParameterBean.class);
            log.warn(uie);
        }
        catch (ConfigurationException ce)
        {
            Logger log = Logger.getLogger(oracle.retail.stores.pos.ui.beans.EditListFromListParameterBean.class);
            log.warn(ce);
        }
    }

    /**
     * Displays the bean in it's own frame.
     *
     * @param args command line arguments
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        EditListFromListParameterBean bean = new EditListFromListParameterBean();
        UIUtilities.doBeanTest(bean);
    }

    // -------------------------------------------------------------------------
    /**
     * Inner class to render cell {@link ReasonCodeValue} as strings.
     */
    class ParameterListValueRenderer extends DefaultListCellRenderer
    {
        private static final long serialVersionUID = 65182619121029457L;

        /* (non-Javadoc)
         * @see javax.swing.DefaultListCellRenderer#getListCellRendererComponent(javax.swing.JList, java.lang.Object, int, boolean, boolean)
         */
        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected,
                boolean cellHasFocus)
        {
            if (value instanceof ReasonCodeValue)
            {
                value = retrieveText(((ReasonCodeValue)value).getValue(),
                                     ((ReasonCodeValue)value).getValue());
            }
            else
            {
                value = retrieveText((String)value, (String)value);
            }

            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }

    // -------------------------------------------------------------------------
    /**
     * Inner class to compare {@link ReasonCodeValue} as strings.
     */
    class ReasonCodeValueComparator implements Comparator<Serializable>
    {
        /* (non-Javadoc)
         * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
         */
        public int compare(Serializable o1, Serializable o2)
        {
            String s1;
            String s2;

            if (o1 instanceof ReasonCodeValue)
            {
                s1 = retrieveText(((ReasonCodeValue)o1).getValue(),
                                     ((ReasonCodeValue)o1).getValue());
            }
            else
            {
                s1 = retrieveText((String)o1, (String)o1);
            }

            if (o2 instanceof ReasonCodeValue)
            {
                s2 = retrieveText(((ReasonCodeValue)o2).getValue(),
                                     ((ReasonCodeValue)o2).getValue());
            }
            else
            {
                s2 = retrieveText((String)o2, (String)o2);
            }

            return s1.compareTo(s2);
        }
        
    }
}