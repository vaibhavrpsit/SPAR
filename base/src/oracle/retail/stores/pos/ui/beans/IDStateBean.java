/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/IDStateBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:54 mszekely Exp $
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
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *
 * ===========================================================================
 * $Log:
 * 7    I18N_P2    1.5.1.0     1/4/2008 5:00:24 PM    Maisa De Camargo CR 29826
 *       - Setting the size of the combo boxes. This change was necessary
 *      because the width of the combo boxes used to grow according to the
 *      length of the longest content. By setting the size, we allow the width
 *       of the combo box to be set independently from the width of the
 *      dropdown menu.
 * 6    360Commerce 1.5         4/12/2007 2:50:18 PM   Ashok.Mondal    CR 24044
 *       :Merge V7.2.2 to trunk
 * 5    360Commerce 1.4         8/9/2006 4:10:15 PM    Brendan W. Farrell Merge
 *       fix to keep state box the same length when changing countries.
 * 4    360Commerce 1.3         12/13/2005 4:42:45 PM  Barry A. Pape
 *      Base-lining of 7.1_LA
 * 3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:03 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:22 PM  Robert Pearse   
 *
 *Revision 1.4  2004/07/17 19:21:23  jdeleau
 *@scr 5624 Make sure errors are focused on the beans, if an error is found
 *during validation.
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
 *    Rev 1.2   Jan 13 2004 13:39:00   baa
 * fix focus bug
 * Resolution for 3541: ID Number field on ID Origin screen has no focus
 *
 *    Rev 1.1   Nov 13 2003 17:49:24   bwf
 * Move country above state.
 *
 *    Rev 1.0   Nov 07 2003 16:19:00   bwf
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean is used to capture the ID's state.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.pos.ui.beans.CheckEntryBeanModel
 */
public class IDStateBean extends ValidatingBean
{
    private static final long serialVersionUID = -1424536253093590350L;

    /** Revision number supplied by source-code control system */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
     * Check Entry Bean model
     */
    protected CheckEntryBeanModel beanModel = new CheckEntryBeanModel();

    protected static final int ID_COUNTRY = 0;
    protected static final int ID_STATE = ID_COUNTRY + 1;
    protected static final int MAX_FIELDS = ID_STATE + 1;

    protected String labelTags[] = { "IDCountryLabel", "IDStateLabel" };
    /**
        Fields and labels that contain ID state data
    */
    protected JLabel[] fieldLabels;
    //protected StateComboBox idStateField;
    protected ValidatingComboBox idStateField;

    /** Country list */
    protected ValidatingComboBox idCountryField = null;

    protected boolean hideStateField = false;

    /**
     * Default Constructor.
     */
    public IDStateBean()
    {
        initialize();
    }

    /**
     * Initializes the fields.
     */
    protected void initializeFields()
    {
        idStateField = uiFactory.createValidatingComboBox("IdStateField", "false", "20");
        idCountryField = uiFactory.createValidatingComboBox("idCountryField", "false", "15");
    }

    /**
     * Initializes the labels.
     */
    protected void initializeLabels()
    {
        fieldLabels = new JLabel[MAX_FIELDS];

        for (int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i] = uiFactory.createLabel(labelTags[i], labelTags[i], null, UI_LABEL);
        }
    }

    /**
     * Returns the base bean model.
     * 
     * @return POSBaseBeanModel
     */
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
        if (idStateField.getSelectedIndex() >= 0)
        {
            beanModel.setStateIndex(idStateField.getSelectedIndex());
        }
        if (idCountryField.getSelectedIndex() >= 0)
        {
            beanModel.setCountryIndex(idCountryField.getSelectedIndex());
        }
        else
        {
            beanModel.setCountryIndex(0);
        }
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
    @Override
    protected void updateBean()
    {
        //Retrieve countries and update combo box
        String[] countryList = beanModel.getCountryNames();
        ValidatingComboBoxModel countryModel = new ValidatingComboBoxModel(countryList);

        idCountryField.setModel(countryModel);
        idCountryField.setSelectedIndex(beanModel.getCountryIndex());

        // update the state combo box with the new list of states
        String[] stateList = beanModel.getStateNames();
        ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

        idStateField.setModel(stateModel);
        int index=-1;
        setComboBoxModel(stateList,idStateField,index);

        idCountryField.setRequired(true);
        idStateField.setRequired(!hideStateField);
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("IDStateBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();

        JComponent[] components = new JComponent[] { idCountryField, idStateField };
        UIUtilities.layoutDataPanel(this, fieldLabels, components);
    }

    /**
     *  The framework calls this method just before display
     */
    public void activate()
    {
        super.activate();
        idCountryField.addFocusListener(this);
        idCountryField.addActionListener(this);
    }

    /**
     * The framework calls this method just before display
     */
    public void deactivate()
    {
        super.deactivate();
        idCountryField.removeFocusListener(this);
        idCountryField.removeActionListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#actionPerformed(java.awt.event.ActionEvent)
     */
    @Override
    public void actionPerformed(ActionEvent event)
    {
        if (event.getSource() == idCountryField)
        {
            updateStates();
        }
        else
        {
            super.actionPerformed(event);
        }
    }

    /**
     * Update property fields.
     */
    protected void updatePropertyFields()
    {
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i]));
        }
    }

    /**
     * Update states as country selection changes
     */
    public void updateStates()
    {
        int countryIndx = idCountryField.getSelectedIndex();
        if (countryIndx == -1)
        {
            countryIndx = 0;
        }
        beanModel.setCountryIndex(countryIndx);
        String[] stateList = beanModel.getStateNames();

        // update the state combo box with the new list of states
        ValidatingComboBoxModel stateModel = new ValidatingComboBoxModel(stateList);

        idStateField.setModel(stateModel);
        idStateField.setSelectedIndex(0); // select 1st element of the list
    }

    /**
     * Requests focus on parameter value name field if visible is true.
     * 
     * @param visible true if setting visible, false otherwise
     */
    public void setVisible(boolean visible)
    {
        super.setVisible(visible);
        if (visible && !errorFound())
        {
            setCurrentFocus(idCountryField);
        }
    }

    /**
     * Allows bean to be configured to display only the country - to handle id's
     * that don't have an associated state.
     * 
     * @param hide true if hiding the state, false otherwise
     */
    public void setHideStateField(String hide)
    {
        hideStateField = UIUtilities.getBooleanValue(hide);
        idStateField.setVisible(!hideStateField);
        fieldLabels[ID_STATE].setVisible(!hideStateField);
        JLabel[] tempFieldLabels = null;
        JComponent[] components = null;
        if (hideStateField)
        {
            tempFieldLabels = new JLabel[] { fieldLabels[ID_COUNTRY] };
            components = new JComponent[] { idCountryField };
        }
        else
        {
            tempFieldLabels = fieldLabels;
            components = new JComponent[] { idCountryField,idStateField };
        }
        UIUtilities.layoutDataPanel(this, tempFieldLabels, components);
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
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        CheckEntryBeanModel aModel = new CheckEntryBeanModel();
        aModel.setStateIndex(10);

        IDStateBean aBean = new IDStateBean();
        aBean.configure();
        aBean.setModel(aModel);
        aBean.activate();

        UIUtilities.doBeanTest(aBean);
    }
}
