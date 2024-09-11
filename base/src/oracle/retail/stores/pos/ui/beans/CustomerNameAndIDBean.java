/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerNameAndIDBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:43 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    asinton   09/13/10 - Added case for CODE_INTEGER_UNDEFINED for the ID
 *                         Type selector in method updateBean.
 *    asinton   09/13/10 - XbranchMerge sakumari_bug-9769699 from
 *                         rgbustores_13.1x_branch
 *    sakumari  06/28/10 - The id type field is updated with the default
 *                         undefined code string (-1), if the selected id type
 *                         is of undefined code string
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   06/22/09 - ensure that any listeners are removed in deactivate
 *                         method
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *    glwang    02/24/09 - allow first/last name fields with one character.
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         10/9/2007 1:13:54 AM   Anda D. Cadar
 *         Switch to Validating Combo Box and read the values from the
 *         resource bundles
 *    4    360Commerce 1.3         3/30/2007 5:55:28 AM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         4    .v8x      1.2.2.0     3/11/2007 5:33:51 PM   Brett J. Larsen
 *         CR 4530
 *         - default reason code value not being used
 *    3    360Commerce 1.2         4/1/2005 2:57:37 AM    Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 9:50:41 PM   Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 11:40:23 PM  Robert Pearse
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.0   Dec 12 2003 13:52:24   blj
 * Initial revision.
 *
 *    Rev 1.1   Nov 03 2003 11:47:22   epd
 * Updated for internationalization
 *
 *    Rev 1.0   Oct 31 2003 16:51:58   epd
 * Initial revision.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * Work panel bean for customer name and id entry
 */
public class CustomerNameAndIDBean extends ValidatingBean
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = -5242988465538009905L;

    /**
     * fields and labels for customer ID data
     */
    protected JLabel idTypeLabel = null;
    protected JLabel firstNameLabel = null;
    protected JLabel lastNameLabel = null;

    protected ValidatingComboBox idTypeField = null;

    protected ConstrainedTextField firstNameField = null;
    protected ConstrainedTextField lastNameField = null;

    // The bean model
    protected CustomerNameAndIDBeanModel beanModel = new CustomerNameAndIDBeanModel();

    /**
     * Constructor
     */
    public CustomerNameAndIDBean()
    {
        initialize();
    }

    /**
     * Initialize the class.
     */
    protected void initialize()
    {
        setName("CustomerNameAndIDBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);
        initComponents();
        initLayout();
    }

    /**
     * Initialize the components.
     */
    protected void initComponents()
    {
        // init labels
        firstNameLabel = uiFactory.createLabel("FirstNameLabel", "FirstNameLabel", null, UI_LABEL);
        lastNameLabel = uiFactory.createLabel("LastNameLabel", "LastNameLabel", null, UI_LABEL);
        idTypeLabel = uiFactory.createLabel("IdTypeLabel", "IdTypeLabel", null, UI_LABEL);

        // init fields
        firstNameField = uiFactory.createConstrainedField("firstNameField", "1", "30", "16");
        lastNameField = uiFactory.createConstrainedField("lastNameField", "1", "30", "20");
        idTypeField = uiFactory.createValidatingComboBox("idTypeField", "false", "15");
    }

    /**
     * Lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this,
                                    new JLabel[]{firstNameLabel, lastNameLabel, idTypeLabel},
                                    new JComponent[]{firstNameField, lastNameField, idTypeField});
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#activate()
     */
    @Override
    public void activate()
    {
        super.activate();
        firstNameField.addFocusListener(this);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.beans.BaseBeanAdapter#deactivate()
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        firstNameField.removeFocusListener(this);
    }

    /**
     * Updates the model property
     */
    @Override
    public void updateModel()
    {

        beanModel.setFirstName(firstNameField.getText());
        beanModel.setLastName(lastNameField.getText());
        beanModel.setSelectedIDType(idTypeField.getSelectedIndex());
    }

    /**
     * Sets the model property
     * @param model UIModelIfc
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set CustomerIDBean model to null");
        }

        if (model instanceof CustomerIDBeanModel)
        {
            beanModel = (CustomerNameAndIDBeanModel) model;
            updateBean();
        }
    }

    /**
     * Updates the information displayed on the screen's if the model's been
     * changed.
     */
    protected void setupComponent(JComponent field,
                                       boolean isEditable,
                                       boolean isVisible)
    {
        if (field instanceof ValidatingFieldIfc)
        {
            ((ValidatingFieldIfc)field).getLabel().setVisible(isVisible);
        }

        if (field instanceof JTextField)
        {
            ((JTextField)field).setEditable(isEditable);
        }

        field.setRequestFocusEnabled(isVisible);
        field.setVisible(isVisible);
    }


    /**
     * Do actual updating of bean from the model
     */
    @Override
    protected void updateBean()
    {
        firstNameField.setText(beanModel.getFirstName());
        setupComponent(firstNameField,true,true);
        firstNameField.setRequired(true);
        setFieldRequired(firstNameField,true);

        lastNameField.setText(beanModel.getLastName());
        setupComponent(lastNameField,true,true);
        lastNameField.setRequired(true);
        setFieldRequired(lastNameField,true);

        if (beanModel.getIDTypes() != null)
        {
            Vector idTypeList = beanModel.getIDTypes();
            //I18N phase 2 changes - make sure that the resource bundles are used properly
            ValidatingComboBoxModel listModel =
                new ValidatingComboBoxModel(idTypeList);

            idTypeField.setModel(listModel);
        }
        if (beanModel.getSelectedIDType() > CodeConstantsIfc.CODE_INTEGER_UNDEFINED)
        {
            idTypeField.setSelectedIndex(beanModel.getSelectedIDType());
        }
        else if(beanModel.getSelectedIDType() == CodeConstantsIfc.CODE_INTEGER_UNDEFINED)
        {
            idTypeField.setSelectedIndex(CodeConstantsIfc.CODE_INTEGER_UNDEFINED);
        }

        setRequiredFields();
    }

    /**
     * Determine what fields should be required.
     */
    protected void setRequiredFields()
    {
        firstNameField.setRequired(true);
        lastNameField.setRequired(true);
        idTypeField.setRequired(true);
    }

    /**
     * Gets the POSBaseBeanModel associated with this bean.
     *
     * @return the POSBaseBeanModel associated with this bean.
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Update property fields.
     */
    @Override
    protected void updatePropertyFields()
    {
        firstNameLabel.setText(retrieveText("FirstNameLabel",firstNameLabel));
        lastNameLabel.setText(retrieveText("LastNameLabel",lastNameLabel));
        idTypeLabel.setText(retrieveText("IdTypeLabel",
                                         idTypeLabel));

        // associate labels with fields
        firstNameField.setLabel(firstNameLabel);
        lastNameField.setLabel(lastNameLabel);
        idTypeField.setLabel(idTypeLabel);
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        CustomerNameAndIDBean bean = new CustomerNameAndIDBean();

        UIUtilities.doBeanTest(bean);
    }
}
