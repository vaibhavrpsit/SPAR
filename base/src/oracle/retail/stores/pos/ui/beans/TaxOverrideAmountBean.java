/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TaxOverrideAmountBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:59 mszekely Exp $
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
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         3/29/2007 7:34:33 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         4    .v8x      1.2.1.0     3/11/2007 12:51:02 PM  Brett J. Larsen
 *         CR 4530
 *         - adding support for a default value
 *    3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:48 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:43 PM  Robert Pearse
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.1   Sep 16 2003 17:53:28   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:12:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Apr 10 2003 12:57:08   bwf
 * Remove all instanceof UtilityManagerIfc and replace with UIUtlities.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.4   Feb 20 2003 16:55:22   HDyer
 * Updated to display localized reason codes, and use the selected index rather than the selected string.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.3   Sep 18 2002 17:15:34   baa
 * country/state changes
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.2   Aug 14 2002 18:19:00   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   27 Jun 2002 10:57:10   jbp
 * removed dirty model
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Apr 29 2002 14:55:02   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:36:30   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:58:02   msg
 * Initial revision.
 *
 *    Rev 1.5   Mar 04 2002 14:15:42   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   Mar 01 2002 22:35:46   mpm
 * Made changes for modifyitem internationalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   Feb 05 2002 16:44:00   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.2   Jan 22 2002 09:12:12   mpm
 * UI fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

//--------------------------------------------------------------------------
/**
 *  Tax override bean class. <P>
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//--------------------------------------------------------------------------
public class TaxOverrideAmountBean extends SelectionListBean
{
    /** Revision Number furnished by TeamConnection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** default text for labels */
    public static String TAX_LABEL = "Override Tax:";
    public static String REASON_LABEL = "Reason Code:";

    /** Tax amount field */
    protected CurrencyTextField taxAmountField;

    /** Label for TaxAmount field */
    protected JLabel taxAmountLabel;

    //--------------------------------------------------------------------------
    /**
     *   Default constructor.
     */
    public TaxOverrideAmountBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initialize the class.
     */
    public void configure()
    {
        super.configure();

        setName("TaxOverrideAmountBean");
        setLabelText(REASON_LABEL);
        setLabelTags(SelectionListBean.REASON_CODE_LABEL);
        beanModel = new DecimalWithReasonBeanModel();
    }

    //--------------------------------------------------------------------------
    /**
     *    Overrides JPanel setVisible() method to request focus.
     */
    //--------------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag)
        {
            setCurrentFocus(taxAmountField);
        }
    }

    //--------------------------------------------------------------------------
    /**
     * activate any settings made by this bean to external entities
     */
    //--------------------------------------------------------------------------
    public void activate()
    {
        super.activate();
        taxAmountField.addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     * deactivate any settings made by this bean to external entities
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        taxAmountField.removeFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *    Initialize the display components.
     */
    protected void initComponents()
    {
        super.initComponents();

        taxAmountLabel = uiFactory.createLabel(TAX_LABEL, null, UI_LABEL);

        taxAmountField = uiFactory.createCurrencyField("TaxAmountField");
        taxAmountField.setHorizontalAlignment(JTextField.RIGHT);
    }

    //--------------------------------------------------------------------------
    /**
     *    Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this,
                                    new JLabel[]{taxAmountLabel, choiceLabel},
                                    new JComponent[]{taxAmountField, scrollPane});
    }

    //--------------------------------------------------------------------------
    /**
     *    Update the Model with changes.
     *    @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
     */
    public void updateModel()
    {
        if(beanModel instanceof DecimalWithReasonBeanModel)
        {
            DecimalWithReasonBeanModel myModel =
                (DecimalWithReasonBeanModel)beanModel;

            myModel.setValue(taxAmountField.getDecimalValue());
            myModel.setSelected(false);
            // get reason from list
            int reasonIndex = choiceList.getSelectedIndex();
            // if reason exists, set selection
            if (reasonIndex >= 0)
            {
                myModel.setSelectedReasonCode(reasonIndex);
                myModel.setSelected(true);
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Update the bean if It's been changed
     *  This bean uses the DecimalWithReasonBeanModel.
     *  @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
     */
    protected void updateBean()
    {
        if(beanModel instanceof DecimalWithReasonBeanModel)
        {
            DecimalWithReasonBeanModel myModel =
                (DecimalWithReasonBeanModel)beanModel;

            taxAmountField.setDecimalValue(myModel.getValue());

            // Populate the list model with the I18N strings
            POSListModel listModel = new POSListModel(myModel.getReasonCodes());
            choiceList.setModel(listModel);

            if(myModel.isSelected())
            {
                choiceList.setSelectedIndex(myModel.getSelectedIndex());
                choiceList.ensureIndexIsVisible(myModel.getSelectedIndex());
            }
            else
            {
                choiceList.setSelectedIndex(myModel.getDefaultIndex());
            }
        }
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        super.updatePropertyFields();
        taxAmountLabel.setText(retrieveText("TaxOverrideAmountLabel",
                                             taxAmountLabel));
        taxAmountField.setLabel(taxAmountLabel);
    }                                   // end updatePropertyFields()

    //--------------------------------------------------------------------------
    /**
     *    Returns default display string.
     *    @return String representation of object
     */
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) +
                          "(Revision " + getRevisionNumber() +
                          ") @" + hashCode());
    }

    //--------------------------------------------------------------------------
    /**
     *    Returns the revision number of this class.
     *    @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        // return string
        return(Util.parseRevisionNumber(this.revisionNumber));
    }

    //-------------------------------------------------------------------------
    /**
     *  main entrypoint - starts the part when it is run as an application
     *  @param args java.lang.String[]
     */
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        TaxOverrideAmountBean bean = new TaxOverrideAmountBean();
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}

