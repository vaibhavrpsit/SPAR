/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/ItemTaxOnOffBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
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
 *    4    360Commerce 1.3         3/29/2007 6:33:00 PM   Michael Boyd    CR
 *         26172 - v8x merge to trunk
 *
 *         4    .v8x      1.2.1.0     3/11/2007 2:43:55 PM   Brett J. Larsen
 *         CR 4530
 *         - added support for using the model's default reason value (rather
 *         than using the selected value)
 *    3    360Commerce 1.2         3/31/2005 3:28:34 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:22:33 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:11:42 PM  Robert Pearse
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
 *    Rev 1.1   Sep 16 2003 17:52:40   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:11:00   CSchellenger
 * Initial revision.
 *
 *    Rev 1.4   Apr 10 2003 13:08:50   bwf
 * Remove all instanceof UtilityManagerIfc and replace with UIUtlities.
 * Resolution for 1866: I18n Database  support
 *
 *    Rev 1.3   Feb 14 2003 15:01:54   HDyer
 * Populate UI list with localized strings. Use list index rather than selected string since that no longer refers to the string code map key.
 * Resolution for POS SCR-2035: I18n Reason Code support
 *
 *    Rev 1.2   Aug 14 2002 18:17:52   baa
 * format currency
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   27 Jun 2002 10:57:08   jbp
 * removed dirty model
 * Resolution for POS SCR-1626: Pricing Feature
 *
 *    Rev 1.0   Apr 29 2002 14:52:50   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:35:22   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:55:48   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 04 2002 14:15:38   mpm
 * Added internationalization to parameteruicfg screens.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.2   Mar 01 2002 22:35:44   mpm
 * Made changes for modifyitem internationalization.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.Dimension;
import java.util.Properties;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;

import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.POSListModel;
import oracle.retail.stores.pos.ui.UIUtilities;

//---------------------------------------------------------------------
/**
 *  This Bean displays a Tax On/Off field and a Reason Code Form. <P>
 *  It uses the BooleanWithReasonBeanModel.
 *  @see oracle.retail.stores.pos.ui.beans.BooleanWithReasonBeanModel
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
//---------------------------------------------------------------------
public class ItemTaxOnOffBean extends SelectionListBean
{
    /** Revision Number supplied by TeamConnection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** default text for labels */
    public static String TAX_LABEL = "Tax:";
    public static String REASON_LABEL = "Reason Code:";

    /** ON/OFF combo model for tax list */
    protected OnOffComboModel comboModel;

    /** Fills the bottom area */
    protected JLabel taxableLabel;

    /** Container for the TaxableList */
    protected JList taxableList;

    /** Scrollpane for the Taxable list */
    protected JScrollPane taxableScrollPane;

    //---------------------------------------------------------------------
    /**
     *  Constructor
     */
    public ItemTaxOnOffBean()
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

        setName("ItemTaxOnOffBean");
        setLabelText(REASON_LABEL);
        setLabelTags(SelectionListBean.REASON_CODE_LABEL);
        beanModel = new BooleanWithReasonBeanModel();
    }

    //--------------------------------------------------------------------------
    /**
     *    Initialize the display components.
     */
    protected void initComponents()
    {
        super.initComponents();

        taxableLabel = uiFactory.createLabel(TAX_LABEL, null, UI_LABEL);

        taxableList = new JList();
        taxableList.setName("TaxableList");
        taxableList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        comboModel = new OnOffComboModel();
        taxableList.setModel(comboModel);

        taxableScrollPane = new JScrollPane();
        taxableScrollPane.setPreferredSize(new Dimension(60, 60));
        taxableScrollPane.setMinimumSize(new Dimension(40, 60));
        taxableScrollPane.setViewportView(taxableList);
        taxableScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);

    }

    //--------------------------------------------------------------------------
    /**
     *    Initializes the layout and lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this,
                                    new JLabel[]{taxableLabel, choiceLabel},
                                    new JComponent[]{taxableScrollPane, scrollPane});
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
        if (value)
        {
            setCurrentFocus(taxableList);
        }
    }

    //--------------------------------------------------------------------------
    /**
     *   Activates this bean.
     */
    public void activate()
    {
        super.activate();
        taxableList.addFocusListener(this);
    }

    //--------------------------------------------------------------------------
    /**
     *   Deactivates this bean.
     */
    public void deactivate()
    {
        super.deactivate();
        taxableList.removeFocusListener(this);
    }

    //---------------------------------------------------------------------
    /**
     *  This method updates the model
     */
    public void updateModel()
    {
        if(beanModel instanceof BooleanWithReasonBeanModel)
        {
            BooleanWithReasonBeanModel myModel =
                (BooleanWithReasonBeanModel)beanModel;

            // get the text and parse it
            String taxString=(String)taxableList.getSelectedValue();
            if(taxString==null) taxString=comboModel.getDefaultValue();
            myModel.setValue(comboModel.valueOf(taxString));
            myModel.setSelected(false);
            int reasonIndex = choiceList.getSelectedIndex();
            if (reasonIndex >= 0)
            {
                myModel.setSelectedReasonCode(reasonIndex);
                myModel.setSelected(true);
            }
        }
    }

    //---------------------------------------------------------------------
    /**
     *  Updates the bean after being set using setModel.
     *  @see setModel(UIModelIfc model)
     */
    protected void updateBean()
    {
        if(beanModel instanceof BooleanWithReasonBeanModel)
        {
          BooleanWithReasonBeanModel myModel =
                (BooleanWithReasonBeanModel)beanModel;

            taxableList.setSelectedValue(comboModel.valueOf(myModel.getValue()),false);
            // Populate the list model with the I18N strings
            POSListModel listModel = new POSListModel(myModel.getReasonCodes());
            choiceList.setModel(listModel);
            if(myModel.isSelected())
            {
                // Set selected index on the list to what was selected before.
                // Note, the model knows the key, but not the displayed localized
                // value, so we set what was selected previously by using the index
                choiceList.setSelectedIndex(myModel.getSelectedIndex());
                // and then make sure that part of the scrollpane is visible
                choiceList.ensureIndexIsVisible(myModel.getSelectedIndex());
            }
            else
            {
                choiceList.setSelectedIndex(myModel.getDefaultIndex());
                choiceList.ensureIndexIsVisible(myModel.getDefaultIndex());
            }
        }
    }

    //---------------------------------------------------------------------
    /**
     *  Set the properties to be used by this bean
        @param props the propeties object
     */
    public void setProps(Properties props)
    {
        super.setProps(props);
        comboModel.setProps(props);
    }

    //---------------------------------------------------------------------
    /**
       Updates property-based fields.
    **/
    //---------------------------------------------------------------------
    protected void updatePropertyFields()
    {                                   // begin updatePropertyFields()
        super.updatePropertyFields();
        taxableLabel.setText(retrieveText("TaxOnOffLabel",
                                          taxableLabel));
    }                                   // end updatePropertyFields()

    //---------------------------------------------------------------------
    /**
     *   Returns default display string. <P>
     *  @return String representation of object
     */
    public String toString()
    {
        String strResult = new String("Class: ItemTaxOnOffBean (Revision " +
                                      getRevisionNumber() + ") @" +
                                      hashCode());
        return(strResult);
    }

    //---------------------------------------------------------------------
    /**
     *  Retrieves the Team Connection revision number. <P>
     *  @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
     *  main entrypoint - starts the part when it is run as an application
     *  @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(java.lang.String[] args)
    {
        UIUtilities.setUpTest();

        ItemTaxOnOffBean bean = new ItemTaxOnOffBean();
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }
}
