/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/TaxOverrideRateBean.java /main/14 2011/12/05 12:16:31 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    abondala  03/05/09 - get reasoncode text entries from the database, not
 *                         from the bundles.
 *
 * ===========================================================================
 * $Log:
 *    8    360Commerce 1.7         4/17/2007 8:08:25 PM   Ashok.Mondal    CR
 *         3966 : V7.2.2 merge to trunk.
 *    7    360Commerce 1.6         3/29/2007 7:35:11 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         7    .v8x      1.5.2.0     3/11/2007 2:22:03 PM   Brett J. Larsen
 *         CR 4530
 *         - default reason code not being displayed (except when it's the
 *         1st on in the list)
 *
 *         adding support for default reason code
 *    6    360Commerce 1.5         1/25/2006 4:11:50 PM   Brett J. Larsen merge
 *          7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *    5    360Commerce 1.4         1/22/2006 11:45:29 AM  Ron W. Haight
 *         removed references to com.ibm.math.BigDecimal
 *    4    360Commerce 1.3         12/13/2005 4:42:46 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:20 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:48 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:43 PM  Robert Pearse
 *:
 *    4    .v700     1.2.1.0     9/19/2005 18:01:22     Jason L. DeLeau Make
 *         sure CurrencyTextFields can have a blank default value.
 *    3    360Commerce1.2         3/31/2005 15:30:20     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:25:48     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:43     Robert Pearse
 *
 *    4    360Commerce1.3         12/13/2005 4:42:46 PM  Barry A. Pape
 *: TaxOverrideRateBean.java,v $
 *         Base-lining of 7.1_LA
 *    3    360Commerce1.2         3/31/2005 3:30:20 PM   Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:25:48 AM  Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:43 PM  Robert Pearse
 *:
 *    5    .v710     1.2.2.1     10/24/2005 14:20:53    Charles Suehs   Merged
 *         from .v700 to fix CR 3965.
 *    4    .v710     1.2.2.0     10/20/2005 18:27:13    Charles Suehs   Merge
 *         from TaxOverrideRateBean.java, Revision 1.2.1.0
 *    3    360Commerce1.2         3/31/2005 15:30:20     Robert Pearse
 *    2    360Commerce1.1         3/10/2005 10:25:48     Robert Pearse
 *    1    360Commerce1.0         2/11/2005 12:14:43     Robert Pearse
 *
 *   Revision 1.3  2004/03/16 17:15:18  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:26  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Dec 01 2003 14:33:26   baa
 * fix formating of decimal positions
 * Resolution for 3486: Decimal point appears on Trans Tax Override, req state must enter decimal point
 *
 *    Rev 1.1   Sep 16 2003 17:53:32   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:12:46   CSchellenger
 * Initial revision.
 *
 *    Rev 1.5   Apr 10 2003 12:37:30   bwf
 * Remove all instanceof UtilityManagerIfc and replace with UIUtlities.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.4   Feb 20 2003 16:55:20   HDyer
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
 *    Rev 1.0   Apr 29 2002 14:55:04   msg
 * Initial revision.
 *
 *    Rev 1.3   25 Apr 2002 18:52:34   pdd
 * Removed unnecessary BigDecimal instantiations.
 * Resolution for POS SCR-1610: Remove inefficient instantiations of BigDecimal
 *
 *    Rev 1.2   15 Apr 2002 09:36:30   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.1   25 Mar 2002 11:56:50   vxs
 * Replaced myModel.getValue().equals(...) with .compareTo(...)
 * Resolution for POS SCR-1475: Trans Tax % data field defaults to 0.000 after Invalid Data Notice, should be no defaults
 *
 *    Rev 1.0   Mar 18 2002 11:58:02   msg
 * Initial revision.
 *
 *    Rev 1.5   Mar 04 2002 14:15:42   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.4   Mar 01 2002 22:35:48   mpm
 * Made changes for modifyitem internationalization.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   Feb 05 2002 16:44:02   mpm
 * Modified to use IBM BigDecimal.
 * Resolution for POS SCR-1121: Employ IBM BigDecimal
 *
 *    Rev 1.2   Jan 22 2002 09:12:12   mpm
 * UI fixes
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// java imports
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.SwingConstants;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;
import java.math.BigDecimal;

//------------------------------------------------------------------------------
/**
    Tax override bean class. <P>
    @version $Revision: /main/14 $
**/
//------------------------------------------------------------------------------
public class TaxOverrideRateBean extends SelectionListBean
{
    /** Revision Number furnished by TeamConnection */
    public static final String revisionNumber = "$Revision: /main/14 $";

    /** default text for labels */
    public static String TAX_LABEL = "Override Tax %:";
    public static String REASON_LABEL = "Reason Code:";

    /** Field for TaxRate */
    protected PercentageTextField taxRateField;

    /** Label for Tax Rate field */
    protected JLabel taxRateLabel;


    //--------------------------------------------------------------------------
    /**
     *   Default constructor.
     */
    public TaxOverrideRateBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
     *  Configures the bean.
     */
    public void configure()
    {
        super.configure();

        setName("TaxOverrideRateBean");
        setLabelText(REASON_LABEL);
        setLabelTags(REASON_CODE_LABEL);
        beanModel = new DecimalWithReasonBeanModel();
    }

    //--------------------------------------------------------------------------
    /**
     *  Initialize the display components.
     */
    protected void initComponents()
    {
        super.initComponents();

        taxRateLabel = uiFactory.createLabel(TAX_LABEL, null, UI_LABEL);

        taxRateField = uiFactory.createPercentField("TaxRateField","true","0","100");
        taxRateField.setColumns(8);
        taxRateField.setHorizontalAlignment(SwingConstants.RIGHT);
        taxRateField.setDecimalLength(4);

    }

    //--------------------------------------------------------------------------
    /**
     *  Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this,
                                    new JLabel[]{taxRateLabel, choiceLabel},
                                    new JComponent[]{taxRateField, scrollPane});
    }

    //--------------------------------------------------------------------------
    /**
     *  Overrides JPanel setVisible() method to request focus.
     */
    //--------------------------------------------------------------------------
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);
        if (aFlag)
        {
            setCurrentFocus(taxRateField);
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
        taxRateField.addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     * deactivate any settings made by this bean to external entities
     */
    //--------------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        taxRateField.removeFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *  Update the bean if It's been changed
     *  This bean uses the DecimalWithReasonBeanModel.
     *  @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
     */
    //--------------------------------------------------------------------------
    protected void updateBean()
    {
        if(beanModel instanceof DecimalWithReasonBeanModel)
        {
            DecimalWithReasonBeanModel myModel =
                (DecimalWithReasonBeanModel)beanModel;

            if (myModel.getValue() == null || myModel.getValue().compareTo(BigDecimal.ZERO) == 0)
            {
                taxRateField.setText("");
            }
            else
            {
                BigDecimal newValue = myModel.getValue();
                newValue = newValue.movePointRight(2);
                newValue = newValue.setScale(4, BigDecimal.ROUND_HALF_UP);
                taxRateField.setDecimalValue(newValue);
            }

            // Populate the list model with the I18N strings
            POSListModel listModel =
                new POSListModel(myModel.getReasonCodes());
            choiceList.setModel(listModel);

            if(myModel.isSelected())
            {
                choiceList.setSelectedIndex(myModel.getSelectedIndex());
                choiceList.ensureIndexIsVisible(myModel.getSelectedIndex());
            }
            else
            {
                choiceList.setSelectedIndex(myModel.getDefaultIndex());
                choiceList.ensureIndexIsVisible(myModel.getDefaultIndex());
            }
        }
    }

    //--------------------------------------------------------------------------
    /**
     *  Update the Model with changes.
     *  @see oracle.retail.stores.pos.ui.beans.DecimalWithReasonBeanModel
     */
    //--------------------------------------------------------------------------
    public void updateModel()
    {
        if(beanModel instanceof DecimalWithReasonBeanModel)
        {
            DecimalWithReasonBeanModel myModel =
                (DecimalWithReasonBeanModel)beanModel;

            myModel.setValue(taxRateField.getDecimalValue().movePointLeft(2));
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

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        super.updatePropertyFields();
        taxRateLabel.setText(retrieveText("TaxOverrideRateLabel",
                                          taxRateLabel));
        taxRateField.setLabel(taxRateLabel);
    }                                   // end updatePropertyFields()


    //--------------------------------------------------------------------------
    /**
     *  Returns default display string.
     *  @return String representation of object
     */
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) +
                          "(Revision " + getRevisionNumber() +
                          ") @" + hashCode());
    }

    //--------------------------------------------------------------------------
    /**
     *  Returns the revision number of this class.
     *  @return String representation of revision number
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

        TaxOverrideRateBean bean = new TaxOverrideRateBean();
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}

