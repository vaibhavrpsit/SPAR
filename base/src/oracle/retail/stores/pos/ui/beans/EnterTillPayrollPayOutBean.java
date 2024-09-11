/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EnterTillPayrollPayOutBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:38 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    sgu       03/11/09 - change text fields to alphanumerice field
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *
 * ===========================================================================
 * $Log:
 *    6    360Commerce 1.5         3/29/07 2:22:35 PM CDT Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         6    .v8x      1.3.1.1     3/11/2007 8:00:09 PM   Brett J. Larsen
 *         CR 4530
 *         - default code values not being pre-selected in drop-down lists
 *
 *         this change adds support for default values (while maintaining
 *         support for values selected earlier in the xaction flow)
 *    5    360Commerce 1.4         2/11/07 4:02:27 PM CST Charles D. Baker CR
 *         25092 - merged fix from v8x
 *    4    360Commerce 1.3         7/28/06 6:06:42 PM CDT Brett J. Larsen CR
 *         4530 - default reason code fix
 *         v7x->360Commerce merge
 *    3    360Commerce 1.2         3/31/05 3:28:05 PM CST Robert Pearse
 *    2    360Commerce 1.1         3/10/05 10:21:28 AM CSTRobert Pearse
 *    1    360Commerce 1.0         2/11/05 12:10:55 PM CSTRobert Pearse
 *
 *
 *    4    .v7x      1.2.1.0     6/23/2006 4:54:20 AM   Dinesh Gautam   CR
 *         4530: Fix for reason code
 *
 *   Revision 1.11.2.1  2004/10/25 20:05:29  cdb
 *   @scr 7481 Updated Payout UI Beans to disallow negative amounts.
 *
 *   Revision 1.11  2004/09/27 19:56:06  jdeleau
 *   @scr 7249 If no selected index is available in the dropdown
 *   combo box, default to the first item in the list.
 *
 *   Revision 1.10  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.9  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.8  2004/06/30 16:09:01  dfierling
 *   @scr 5576 - Adjusted comment field UI size,
 *
 *   Revision 1.7  2004/06/16 00:04:47  aschenk
 *   @scr 5521 - Changed the approval drop down to not have a default. Also had to update approval code label (it was printing incorrectly)
 *
 *   Revision 1.6  2004/06/02 16:52:11  khassen
 *   @scr 0 - Updates to the bean.
 *
 *   Revision 1.5  2004/06/02 16:50:17  khassen
 *   @scr 0 - Updates to the bean.
 *
 *   Revision 1.4  2004/05/26 14:36:41  jeffp
 *   @scr 4371  -  to set address line 3 to    visible.
 *
 *   Revision 1.3  2004/03/16 18:30:41  cdb
 *   @scr 0 Removed tabs from all java source code.
 *
 *   Revision 1.2  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.1  2004/03/12 18:48:29  khassen
 *   @scr 0 Till Pay In/Out use case
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.Dimension;
import java.awt.GridBagLayout;
import java.util.Vector;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JScrollPane;

import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.beans.ValidatingBean;

/**
 *
 * @author khassen
 *
 * This class implements the bean for the payroll pay out
 * use case.
 */
public class EnterTillPayrollPayOutBean extends ValidatingBean
{
    /**
        Revision number.
    */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected JLabel amountLabel       = null;
    protected JLabel reasonCodeLabel   = null;
    protected JLabel paidToLabel       = null;
    protected JLabel employeeIDLabel   = null;
    protected JLabel addressLine1Label = null;
    protected JLabel addressLine2Label = null;
    protected JLabel addressLine3Label = null;
    protected JLabel approvalCodeLabel = null;
    protected JLabel commentLabel      = null;

    protected CurrencyTextField        amountField       = null;
    protected ValidatingComboBox       reasonCodeField   = null;
    protected ConstrainedTextField     paidToField       = null;
    protected ConstrainedTextField     employeeIDField   = null;
    protected ConstrainedTextField     addressLine1Field = null;
    protected ConstrainedTextField     addressLine2Field = null;
    protected ConstrainedTextField     addressLine3Field = null;
    protected ValidatingComboBox       approvalCodeField = null;
    protected JScrollPane commentFieldWindow = null;
    protected ConstrainedTextAreaField commentField      = null;

    /**
     * Constructor.
     *
     */
    public EnterTillPayrollPayOutBean()
    {
        super();
        initialize();
    }

    /**
     * Called by the constructor to initialize labels
     * and fields.
     */
    protected void initialize()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();
        initLayout();
    }

    /**
     * Field initialization method.
     */
    protected void initializeFields()
    {
        amountField       = uiFactory.createCurrencyField           ("amountField");
        amountField.setNegativeAllowed(false);
        reasonCodeField   = uiFactory.createValidatingComboBox      ("reasonCodeField", "false", "20");
        paidToField       = uiFactory.createConstrainedField        ("paidToField",       "1", "30", "30");
        employeeIDField   = uiFactory.createAlphaNumericField		("employeeIDField",   "3", "10", false);
        addressLine1Field = uiFactory.createConstrainedField        ("addressLine1Field", "0", "60", "28");
        addressLine2Field = uiFactory.createConstrainedField        ("addressLine2Field", "0", "60", "28");
        addressLine3Field = uiFactory.createConstrainedField        ("addressLine3Field", "0", "60", "28");
        approvalCodeField = uiFactory.createValidatingComboBox      ("approvalCodeField", "false", "20");
        commentFieldWindow = uiFactory.createConstrainedTextAreaFieldPane
        ("commentFieldWindow", "4", "60", "35", "true", "true",
        JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
        JScrollPane.HORIZONTAL_SCROLLBAR_NEVER,
        true);

        commentFieldWindow.setPreferredSize(new Dimension(230, 100));
        commentField = (ConstrainedTextAreaField)commentFieldWindow.getViewport().getView();
    }

    /**
     * Label initialization method.
     */
    protected void initializeLabels()
    {
        amountLabel       = uiFactory.createLabel("amountLabel",       null, UI_LABEL);
        reasonCodeLabel   = uiFactory.createLabel("reasonCodeLabel",   null, UI_LABEL);
        paidToLabel       = uiFactory.createLabel("paidToLabel",       null, UI_LABEL);
        employeeIDLabel   = uiFactory.createLabel("employeeIDLabel",   null, UI_LABEL);
        addressLine1Label = uiFactory.createLabel("addressLine1Label", null, UI_LABEL);
        addressLine2Label = uiFactory.createLabel("addressLine2Label", null, UI_LABEL);
        addressLine3Label = uiFactory.createLabel("addressLine3Label", null, UI_LABEL);
        approvalCodeLabel = uiFactory.createLabel("approvalCodeLabel", null, UI_LABEL);
        commentLabel      = uiFactory.createLabel("commentLabel",      null, UI_LABEL);
    }

    /**
     * Layout initialization routine.
     */
    protected void initLayout()
    {
        JLabel[] labels =
        {
                amountLabel,
                reasonCodeLabel,
                paidToLabel,
                employeeIDLabel,
                addressLine1Label,
                addressLine2Label,
                addressLine3Label,
                approvalCodeLabel,
                commentLabel,
        };
        JComponent[] components =
        {
                amountField,
                reasonCodeField,
                paidToField,
                employeeIDField,
                addressLine1Field,
                addressLine2Field,
                addressLine3Field,
                approvalCodeField,
                commentFieldWindow,
        };
        setLayout(new GridBagLayout());
        for (int i = 0; i < labels.length; i++)
        {
            UIUtilities.layoutComponent(this, labels[i], components[i], 0, i, false);
        }
    }

    /**
     * Called every time the bean model needs to be updated.
     */
    public void updateModel()
    {
        if (beanModel instanceof EnterTillPayrollPayOutBeanModel)
        {
            EnterTillPayrollPayOutBeanModel model = (EnterTillPayrollPayOutBeanModel) beanModel;
            model.setAmount(amountField.getText());
            model.setSelectedReasonCode(reasonCodeField.getSelectedIndex());
            model.setPaidTo(paidToField.getText());
            model.setEmployeeID(employeeIDField.getText());
            model.setAddressLine(0, addressLine1Field.getText());
            model.setAddressLine(1, addressLine2Field.getText());
            model.setAddressLine(2, addressLine3Field.getText());
            model.setSelectedApprovalCodeIndex(approvalCodeField.getSelectedIndex());
            model.setComment(commentField.getText());
        }
    }

    /**
     * Set the labels according to the common text definitions.
     */
    public void updatePropertyFields()
    {
        amountLabel      .setText(retrieveText("AmountLabel",       amountLabel));
        reasonCodeLabel  .setText(retrieveText("ReasonCodeLabel",   reasonCodeLabel));
        paidToLabel      .setText(retrieveText("PaidToLabel",       paidToLabel));
        employeeIDLabel  .setText(retrieveText("EmployeeIDLabel",   employeeIDLabel));
        addressLine1Label.setText(retrieveText("AddressLine1Label", addressLine1Label));
        addressLine2Label.setText(retrieveText("AddressLine2Label", addressLine2Label));
        addressLine3Label.setText(retrieveText("AddressLine3Label", addressLine3Label));
        approvalCodeLabel.setText(retrieveText("ApprovalCodeLabel", approvalCodeLabel));
        commentLabel     .setText(retrieveText("CommentLabel",      commentLabel));

        amountField      .setLabel(amountLabel);
        reasonCodeField  .setLabel(reasonCodeLabel);
        paidToField      .setLabel(paidToLabel);
        employeeIDField  .setLabel(employeeIDLabel);
        addressLine1Field.setLabel(addressLine1Label);
        addressLine2Field.setLabel(addressLine2Label);
        addressLine3Field.setLabel(addressLine3Label);
        approvalCodeField.setLabel(approvalCodeLabel);
        commentField     .setLabel(commentLabel);
    }


    /**
     * Sets the bean model.
     */
    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set " +
                          "EnterTillPayrollPayOutBeanModel to null");
        }
        Object oldValue = beanModel;
        if (model instanceof EnterTillPayrollPayOutBeanModel)
        {
            beanModel = (EnterTillPayrollPayOutBeanModel)model;
            updateBean();
        }
    }

    /**
     * Updates the bean.
     */
    protected void updateBean()
    {
        if (beanModel instanceof EnterTillPayrollPayOutBeanModel)
        {
            EnterTillPayrollPayOutBeanModel model = (EnterTillPayrollPayOutBeanModel) beanModel;
            amountField      .setText(model.getAmount());
            addressLine1Field.setText(model.getAddressLine(0));
            addressLine2Field.setText(model.getAddressLine(1));
            addressLine3Field.setText(model.getAddressLine(2));
            if (model.getNumAddressLines() == 2)
            {
                addressLine3Field.getLabel().setVisible(false);
                addressLine3Field.setEditable(false);
                addressLine3Field.setRequestFocusEnabled(false);
                addressLine3Field.setVisible(false);
            }
            else
            {
                addressLine3Field.getLabel().setVisible(true);
                addressLine3Field.setEditable(true);
                addressLine3Field.setRequestFocusEnabled(true);
                addressLine3Field.setVisible(true);

            }



            paidToField    .setText(model.getPaidTo());
            employeeIDField.setText(model.getEmployeeID());
            commentField   .setText(model.getComment());

            Vector<String> reasonCodes = model.getReasonCodes();
            if (reasonCodes != null)
            {
                reasonCodeField.setModel(new ValidatingComboBoxModel(reasonCodes));

                if (model.getSelectedIndex() != CodeConstantsIfc.CODE_INTEGER_UNDEFINED)
                { // use selected value
                    setComboBoxModel(reasonCodes,reasonCodeField,model.getSelectedIndex());                }
                else
                { // use default value
                    setComboBoxModel(reasonCodes,reasonCodeField,model.getDefaultIndex());
                }
            }
            Vector<String> approvalCodes = model.getApprovalCodes();
            if (approvalCodes != null)
            {
                approvalCodeField.setModel(new ValidatingComboBoxModel(approvalCodes));

                if (model.getSelectedApprovalCodeIndex() != CodeConstantsIfc.CODE_INTEGER_UNDEFINED)
                { // use selected value
                    setComboBoxModel(approvalCodes,approvalCodeField,model.getSelectedApprovalCodeIndex());                }
                else
                { // use default value
                    setComboBoxModel(approvalCodes,approvalCodeField,model.getDefaultApprovalCodeIndex());
                }
            }
        }
    }

    /**
     * setVisible method to determine focus.
     */
    public void setVisible(boolean value)
    {
        super.setVisible(value);

        // Set the focus
        if (value && !errorFound())
        {
            setCurrentFocus(amountField);
        }
    }

    /**
     * Activates the model.
     */
    public void activate()
    {
        super.activate();
        amountField      .addFocusListener(this);
        paidToField      .addFocusListener(this);
        employeeIDField  .addFocusListener(this);
        addressLine1Field.addFocusListener(this);
        addressLine2Field.addFocusListener(this);
        addressLine3Field.addFocusListener(this);
        approvalCodeField.addFocusListener(this);
        commentField     .addFocusListener(this);
    }

    /**
     * Deactivates the model.
     */
    public void deactivate()
    {
        super.deactivate();
        amountField      .removeFocusListener(this);
        paidToField      .removeFocusListener(this);
        employeeIDField  .removeFocusListener(this);
        addressLine1Field.removeFocusListener(this);
        addressLine2Field.removeFocusListener(this);
        addressLine3Field.removeFocusListener(this);
        approvalCodeField.removeFocusListener(this);
        commentField     .removeFocusListener(this);

    }

    /**
     * Gets the base bean model.
     */
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Returns the revision number.
     */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * For testing.
     * @param args
     */
    public static void main(String[] args)
     {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new EnterTillPayInBean());
     }
}
