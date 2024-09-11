/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SummaryReportBean.java /rgbustores_13.4x_generic_branch/2 2011/09/23 16:48:56 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   09/23/11 - disable the reg/till id field when chosing a store
 *                         summary
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/22/09 - translate date/time labels
 *    mkochumm  02/12/09 - use default locale for dates
 *
 * ===========================================================================
 * $Log:
 *    5    I18N_P2    1.3.1.0     1/2/2008 10:36:48 AM   Sandy Gu        Fix
 *         alphanumerice fields for I18N purpose
 *    4    360Commerce 1.3         4/26/2007 3:06:52 PM   Mathews Kochummen use
 *          locale appropriate date label
 *    3    360Commerce 1.2         3/31/2005 4:30:16 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:25:40 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:14:34 PM  Robert Pearse
 *
 *   Revision 1.8  2004/08/24 19:57:12  jdeleau
 *   @scr 6924 Change the max length of the till to 5, from 10.
 *
 *   Revision 1.7  2004/08/24 19:36:38  jdeleau
 *   @scr 6918 If the dates entered in order summary or summary report screens
 *   are not valid, make sure the invalid entries appear to the user after
 *   the error message has been read.
 *
 *   Revision 1.6  2004/08/24 15:02:17  jdeleau
 *   @scr 6910 Fix the displayable date from MM/dd/yyyy to MM/DD/YYYY
 *
 *   Revision 1.5  2004/06/15 23:26:23  cdb
 *   @scr 5412 Corrected null pointer exception caused by unnamed validating field.
 *
 *   Revision 1.4  2004/05/19 21:52:12  jdeleau
 *   @scr 5182 change JList to JComboBox, and make sure listener changes
 *   ActionListener instead of ListSelectionListener.
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
 *    Rev 1.1   Sep 16 2003 17:53:26   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:12:38   CSchellenger
 * Initial revision.
 *
 *    Rev 1.2   Aug 14 2002 21:22:02   baa
 * retrieve report types from the site
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.1   Aug 07 2002 19:34:26   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:52:00   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:36:26   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:57:54   msg
 * Initial revision.
 *
 *    Rev 1.3   Mar 07 2002 20:44:42   mpm
 * Externalized text for report UI screens.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This is the bean that renders Summary Report
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/2 $
 */
public class SummaryReportBean extends ValidatingBean
{
    private static final long serialVersionUID = 7189302539359533892L;

    // Revision number
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/2 $";

    // Constants for fields index
    protected static final int BUSN_DATE     = 0;
    protected static final int TYPE          = BUSN_DATE + 1;
    protected static final int TILL_REGISTER = TYPE + 1;
    protected static final int MAX_FIELDS    = TILL_REGISTER + 1; //add one because of 0 index!

    // Constants for field labels
    protected static String labelText[] =
    {
        "Business Day {0}:",
        "Type:",
        "Till or Register number:"
    };

    // Constants for field labels
    protected static String labelTags[] =
    {
        "BusinessDayLabel",
        "TypeLabel",
        "TillOrRegisterNumberLabel"
    };

    // Array of labels
    protected JLabel[] fieldLabels = new JLabel[MAX_FIELDS];
    // The bean's model
    protected SummaryReportBeanModel beanModel;
    // Business Date field
    protected EYSDateField busnDateField;
    // Till or Register number field
    protected AlphaNumericTextField tillRegNumField;
    // Type selection listener
    protected ListSelectionListener typeListener;

    /**
     * Listen for changes to the JComboBox dropdown and change
     * the required fields based on the selection.
     * @since 7.0
     * @deprecated as of 13.4. Use {@link #reportTypeSelectionListener} instead.
     */
    protected ActionListener actionListener;
    protected ItemListener reportTypeSelectionListener;

    /**
     * @since 7.0 ComboBox showing list of report types available.
     */
    protected ValidatingComboBox reportTypeCB;

    /**
     * @since 7.0 ComboBox model used
     */
    protected ValidatingComboBoxModel reportTypeModel = new ValidatingComboBoxModel();

    private boolean ignoreUpdate;

    /**
     * Default Constructor
     */
    public SummaryReportBean()
    {
        super();
    }

    /**
     * Configures the class.
     */
    @Override
    public void configure()
    {
        setName("SummaryReportBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        for(int cnt = 0; cnt < MAX_FIELDS; cnt++)
        {
            fieldLabels[cnt] = uiFactory.createLabel(labelTags[cnt], labelText[cnt] , null, UI_LABEL);
        }
        busnDateField = uiFactory.createEYSDateField("busnDateField");

        tillRegNumField = uiFactory.createAlphaNumericField("tillRegNumField", "1", "5", false);

        reportTypeCB = new ValidatingComboBox(reportTypeModel);
        reportTypeCB.setName("reportTypeCB");
        reportTypeCB.addItemListener(getReportTypeSelectionListener());
        UIUtilities.layoutDataPanel(this, fieldLabels,
                                    new JComponent[]
                                    {
                                        busnDateField,
                                        reportTypeCB, // used to be typeField, a JList
                                        tillRegNumField
                                    });
        setReportTypeFieldActive(reportTypeCB.getSelectedIndex() != 0);
    }

    /**
     * activate any settings made by this bean to external entities
     */
    @Override
    public void activate()
    {
        super.activate();
        busnDateField.addFocusListener(this);
        setCurrentFocus(busnDateField);
    }

    /**
     * deactivate any settings made by this bean to external entities
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        busnDateField.removeFocusListener(this);
    }

    /**
     * Update the model to reflect what is on the screen
     */
    @Override
    public void updateModel()
    {
        beanModel.setBusinessDate(busnDateField.getDate());
        beanModel.setSelectedType((String)reportTypeCB.getSelectedItem());
        beanModel.setTillRegNumber(tillRegNumField.getText());
    }

    /**
     * Sets the model for the current settings of this bean.
     * 
     * @param model the model for the current values of this bean
     */
    @Override
    public void setModel(UIModelIfc model)
    {
        if (model == null)
        {
            throw new NullPointerException("Attempt to set SummaryReportBeanModel to null");
        }
        if (model instanceof SummaryReportBeanModel)
        {
            beanModel = (SummaryReportBeanModel) model;
            updateBean();
        }
    }

    /**
     * Update the bean if the model's been changed
     */
    @Override
    protected void updateBean()
    {
        if(ignoreUpdate == false)
        {
            if (beanModel.getBusinessDate() == null)
            {
                busnDateField.setDate(busnDateField.getEYSDate());
            }
            else
            {
                busnDateField.setDate(beanModel.getBusinessDate());
            }

            ArrayList list = beanModel.getReportTypesModel();
            reportTypeModel.removeAllElements();
            for (int i = 0; i < list.size(); i++)
            {
                reportTypeModel.addElement(list.get(i));
            }
            tillRegNumField.setText(beanModel.getTillRegNumber());
        }
        String type = beanModel.getSelectedType();
        if (type.length() > 0)
        {
            reportTypeCB.setSelectedItem(type);

            setFieldRequired(busnDateField, true);
            if (type.equals(reportTypeModel.elementAt(0)))
            {
                setFieldRequired(tillRegNumField, false);
            }
            else
            {
                setFieldRequired(tillRegNumField, true);
            }
        }
        else
        {
            reportTypeCB.setSelectedIndex(0);
            setFieldRequired(tillRegNumField, false);
        }

        ignoreUpdate = false;
    }

    /**
     * Gets the POSBaseBeanModel associated with this bean.
     * 
     * @return POSBaseBeanModel beanModel
     */
    @Override
    public POSBaseBeanModel getPOSBaseBeanModel()
    {
        return beanModel;
    }

    /**
     * Gets the actionListener for the JComboBox containing the report types. If
     * the listener is not yet created, it is created at this time.
     * 
     * @return Action listener for the type field
     * @deprecated as of 13.4. Use {@link #getReportTypeSelectionListener()} instead.
     */
    protected ActionListener getActionListener()
    {
        if (actionListener == null)
        {
            actionListener = new ActionListener()
            {
                public void actionPerformed(ActionEvent e)
                {
                    if (reportTypeCB.getSelectedIndex() == 0)
                    {
                        setFieldRequired(tillRegNumField, false);
                    }
                    else
                    {
                        setFieldRequired(tillRegNumField, true);
                    }
                }
            };
        }

        return (actionListener);
    }

    /**
     * Gets a listener that will disable and enable the tillRegNumField as the
     * report type selection is changed.
     *
     * @return
     */
    protected ItemListener getReportTypeSelectionListener()
    {
        if (reportTypeSelectionListener == null)
        {
            reportTypeSelectionListener = new ItemListener()
            {
                @Override
                public void itemStateChanged(ItemEvent e)
                {
                    setReportTypeFieldActive(reportTypeCB.getSelectedIndex() != 0);
                }
            };
        }

        return reportTypeSelectionListener;
    }

    /**
     * The tillRegNumField disables when the store summary is selected because
     * we don't need that number to run a store summary report.
     *
     * @param active
     */
    private void setReportTypeFieldActive(boolean active)
    {
        if (!active)
        {
            tillRegNumField.setText("");
        }
        tillRegNumField.setEnabled(active);
        setFieldRequired(tillRegNumField, active);
    }

    /**
     * Updates property-based fields.
     */
    protected void updatePropertyFields()
    {
        for (int i = 0; i < MAX_FIELDS; i++)
        {
            fieldLabels[i].setText(retrieveText(labelTags[i], labelText[i]));
        }
        // Retrieve bundle text for labels
        String label = retrieveText(fieldLabels[BUSN_DATE].getText());
        String translatedLabel = getTranslatedDatePattern();
        fieldLabels[BUSN_DATE].setText(LocaleUtilities.formatComplexMessage(label, translatedLabel));

        // associate fields with labels
        busnDateField.setLabel(fieldLabels[BUSN_DATE]);
        reportTypeCB.setLabel(fieldLabels[TYPE]);
        tillRegNumField.setLabel(fieldLabels[TILL_REGISTER]);
    }

    /**
     * Show the error screen
     * 
     * @see oracle.retail.stores.pos.ui.beans.ValidatingBean#showErrorScreen()
     */
    public void showErrorScreen()
    {
        super.showErrorScreen();
        ignoreUpdate = true;
    }

    /**
     * Retrieves the Team Connection revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }

    /**
     * main entrypoint - starts the part when it is run as an application
     * 
     * @param args String[]
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();

        SummaryReportBeanModel beanModel = new SummaryReportBeanModel();
        beanModel.setBusinessDate(DomainGateway.getFactory().getEYSDateInstance());
        beanModel.setSelectedType(SummaryReportBeanModel.STORE);
        beanModel.setTillRegNumber("6660");

        SummaryReportBean bean = new SummaryReportBean();
        bean.configure();
        bean.setModel(beanModel);
        bean.activate();

        UIUtilities.doBeanTest(bean);
    }

}