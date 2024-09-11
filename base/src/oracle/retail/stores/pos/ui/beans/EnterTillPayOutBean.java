/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EnterTillPayOutBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:50 mszekely Exp $
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
 *    sgu       03/24/09 - remove invocations to deprecated reason code api
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *
 * ===========================================================================
 * $Log:
 *    7    .v8x      1.4.1.1     3/11/2007 7:25:41 PM   Brett J. Larsen CR 4530
 *          - default code list values not being displayed
 *
 *         added support for displaying default code list values
 *    6    .v8x      1.4.1.0     2/12/2007 3:51:16 PM   Charles D. Baker CR
 *         25091 - Correct fix to externalization problem.
 *    5    360Commerce1.4         12/14/2006 4:03:47 PM  Charles D. Baker CR
 *         20208 - merged fixes from 7.2.2 for till pay in, pay out behaviors.
 *    4    360Commerce1.3         7/28/2006 6:06:38 PM   Brett J. Larsen CR
 *         4530 - default reason code fix
 *         v7x->360Commerce merge
 *    3    360Commerce1.2         3/31/2005 4:28:05 PM   Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:21:28 AM  Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:10:55 PM  Robert Pearse
 *
 *
 *    4    .v7x      1.2.1.0     6/23/2006 4:54:19 AM   Dinesh Gautam   CR
 *         4530: Fix for reason code
 *
 *   Revision 1.10.2.1  2004/10/25 20:05:29  cdb
 *   @scr 7481 Updated Payout UI Beans to disallow negative amounts.
 *
 *   Revision 1.10  2004/09/27 19:56:07  jdeleau
 *   @scr 7249 If no selected index is available in the dropdown
 *   combo box, default to the first item in the list.
 *
 *   Revision 1.9  2004/08/25 15:16:50  lzhao
 *   @scr 6956: fix the problem of using reasoncode index set to approval code.
 *
 *   Revision 1.8  2004/08/23 16:15:58  cdb
 *   @scr 4204 Removed tab characters
 *
 *   Revision 1.7  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
 *
 *   Revision 1.6  2004/07/06 20:33:44  jeffp
 *   @scr 5508 Removed employee id field according to pay in and pay out design
 *
 *   Revision 1.5  2004/06/02 15:23:02  dfierling
 *   @scr 5287 - fixed comment box over resizing issue.
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

public class EnterTillPayOutBean extends ValidatingBean
{
    /**
        Revision number.
    */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    protected JLabel amountLabel       = null;
    protected JLabel reasonCodeLabel   = null;
    protected JLabel paidToLabel       = null;
    //protected JLabel employeeIDLabel   = null;
    protected JLabel addressLine1Label = null;
    protected JLabel addressLine2Label = null;
    protected JLabel addressLine3Label = null;
    protected JLabel approvalCodeLabel = null;
    protected JLabel commentLabel      = null;

    protected CurrencyTextField        amountField       = null;
    protected ValidatingComboBox       reasonCodeField   = null;
    protected ConstrainedTextField     paidToField       = null;
    protected ConstrainedTextField     addressLine1Field = null;
    protected ConstrainedTextField     addressLine2Field = null;
    protected ConstrainedTextField     addressLine3Field = null;
    protected ValidatingComboBox       approvalCodeField = null;
    protected ConstrainedTextAreaField commentField      = null;

    protected JScrollPane commentFieldPane = null;

    public EnterTillPayOutBean()
    {
        super();
        initialize();
    }

    protected void initialize()
    {
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initializeFields();
        initializeLabels();
        initLayout();
    }

    protected void initializeFields()
    {
        amountField       = uiFactory.createCurrencyField           ("amountField");
        amountField.setNegativeAllowed(false);
        reasonCodeField   = uiFactory.createValidatingComboBox      ("reasonCodeField", "false", "20");
        paidToField       = uiFactory.createConstrainedField        ("paidToField",       "1", "30", "30");
        addressLine1Field = uiFactory.createConstrainedField        ("addressLine1Field", "1", "60", "28");
        addressLine2Field = uiFactory.createConstrainedField        ("addressLine2Field", "1", "60", "28");
        addressLine3Field = uiFactory.createConstrainedField        ("addressLine3Field", "1", "60", "28");
        approvalCodeField = uiFactory.createValidatingComboBox      ("approvalCodeField", "false", "20");

        commentFieldPane = uiFactory.createConstrainedTextAreaFieldPane ("commentField",
                                                      "4", "60", "35", "FALSE", "TRUE",
                                                      JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED,
                                                      JScrollPane.HORIZONTAL_SCROLLBAR_NEVER, true);

        commentFieldPane.setName("commentField");
        commentFieldPane.setPreferredSize(new Dimension(230, 100));

        commentField = (ConstrainedTextAreaField)commentFieldPane.getViewport().getView();
    }
    protected void initializeLabels()
    {
        amountLabel       = uiFactory.createLabel("amountLabel",       null, UI_LABEL);
        reasonCodeLabel   = uiFactory.createLabel("reasonCodeLabel",   null, UI_LABEL);
        paidToLabel       = uiFactory.createLabel("paidToLabel",       null, UI_LABEL);
        addressLine1Label = uiFactory.createLabel("addressLine1Label", null, UI_LABEL);
        addressLine2Label = uiFactory.createLabel("addressLine2Label", null, UI_LABEL);
        addressLine3Label = uiFactory.createLabel("addressLine3Label", null, UI_LABEL);
        approvalCodeLabel = uiFactory.createLabel("approvalCodeLabel", null, UI_LABEL);
        commentLabel      = uiFactory.createLabel("commentLabel",      null, UI_LABEL);
    }
    protected void initLayout()
    {
        JLabel[] labels =
        {
                amountLabel,
                reasonCodeLabel,
                paidToLabel,
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
                addressLine1Field,
                addressLine2Field,
                addressLine3Field,
                approvalCodeField,
                commentFieldPane,
        };
        setLayout(new GridBagLayout());
        for (int i = 0; i < labels.length; i++)
        {
            UIUtilities.layoutComponent(this, labels[i], components[i], 0, i, false);
        }
    }

    public void updateModel()
    {
        if (beanModel instanceof EnterTillPayOutBeanModel)
        {
            EnterTillPayOutBeanModel model = (EnterTillPayOutBeanModel) beanModel;
            model.setAmount(amountField.getText());
            model.setSelectedReasonCode(reasonCodeField.getSelectedIndex());
            model.setPaidTo(paidToField.getText());
            model.setAddressLine(0, addressLine1Field.getText());
            model.setAddressLine(1, addressLine2Field.getText());
            model.setAddressLine(2, addressLine3Field.getText());
            model.setSelectedApprovalCodeIndex(approvalCodeField.getSelectedIndex());
            model.setComment(commentField.getText());
        }
    }

    public void updatePropertyFields()
    {
        amountLabel      .setText(retrieveText("AmountLabel",       amountLabel));
        reasonCodeLabel  .setText(retrieveText("ReasonCodeLabel",   reasonCodeLabel));
        paidToLabel      .setText(retrieveText("PaidToLabel",       paidToLabel));
        addressLine1Label.setText(retrieveText("AddressLine1Label", addressLine1Label));
        addressLine2Label.setText(retrieveText("AddressLine2Label", addressLine2Label));
        addressLine3Label.setText(retrieveText("AddressLine3Label", addressLine3Label));
        approvalCodeLabel.setText(retrieveText("ApprovalCodeLabel", approvalCodeLabel));
        commentLabel     .setText(retrieveText("CommentLabel",      commentLabel));

        amountField      .setLabel(amountLabel);
        reasonCodeField  .setLabel(reasonCodeLabel);
        paidToField      .setLabel(paidToLabel);
        addressLine1Field.setLabel(addressLine1Label);
        addressLine2Field.setLabel(addressLine2Label);
        addressLine3Field.setLabel(addressLine3Label);
        approvalCodeField.setLabel(approvalCodeLabel);
        commentField     .setLabel(commentLabel);
    }



    public void setModel(UIModelIfc model)
    {
        if(model==null)
        {
            throw new NullPointerException("Attempt to set " +
                          "EnterTillPayOutBeanModel to null");
        }
        Object oldValue = beanModel;
        if (model instanceof EnterTillPayOutBeanModel)
        {
            beanModel = (EnterTillPayOutBeanModel)model;
            updateBean();
        }
    }

    protected void updateBean()
    {
        if (beanModel instanceof EnterTillPayOutBeanModel)
        {
            EnterTillPayOutBeanModel model = (EnterTillPayOutBeanModel) beanModel;
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
            //employeeIDField.setText(model.getEmployeeID());
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
            Vector approvalCodes = model.getApprovalCodes();
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

    //------------------------------------------------------------------------
    /**
     * Overrides the inherited setVisible() to set the focus on the reply area.
       @param value boolean
    */
    //------------------------------------------------------------------------
    public void setVisible(boolean value)
    {
        super.setVisible(value);

        // Set the focus
        if (value && !errorFound())
        {
            setCurrentFocus(amountField);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Activates this model.
     */
    //--------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        amountField      .addFocusListener(this);
        paidToField      .addFocusListener(this);
        addressLine1Field.addFocusListener(this);
        addressLine2Field.addFocusListener(this);
        addressLine3Field.addFocusListener(this);
        approvalCodeField.addFocusListener(this);
        commentField     .addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *  Deactivates this model.
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        amountField      .removeFocusListener(this);
        paidToField      .removeFocusListener(this);
        addressLine1Field.removeFocusListener(this);
        addressLine2Field.removeFocusListener(this);
        addressLine3Field.removeFocusListener(this);
        approvalCodeField.removeFocusListener(this);
        commentField     .removeFocusListener(this);

    }

    //-----------------------------------------------------------------------
    /**
       Gets the POSBaseBeanModel associated with this model.
       @return the POSBaseBeanModel associated with this model.
    */
    //-----------------------------------------------------------------------
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    //----------------------------------------------------------------------
    /**
        Returns the revision number of the class.
        <P>
        @param none
        @return String representation of revision number
    **/
    //----------------------------------------------------------------------
      public String getRevisionNumber()
    {                                   // begin getRevisionNumber()
        // return string
        return(Util.parseRevisionNumber(revisionNumber));
    }                                   // end getRevisionNumber()

    //--------------------------------------------------------------------------
    /**
     *    Main entry point for testing.
     *    @param args string arguments
     */
     public static void main(String[] args)
     {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new EnterTillPayInBean());
     }
}
