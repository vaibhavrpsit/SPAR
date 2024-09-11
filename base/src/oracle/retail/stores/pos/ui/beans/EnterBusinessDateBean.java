/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EnterBusinessDateBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:40 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   10/27/10 - do not add fill if addWeight is false
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    acadar    04/22/09 - translate date/time labels
 *    acadar    04/21/09 - additional changes for label translation
 *    acadar    04/21/09 - addtional changes to return default
 *    acadar    04/21/09 - provide mechanism to translate the date/time pattern
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:28:00 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:21:24 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:10:53 PM  Robert Pearse
 *
 *   Revision 1.5  2004/07/27 17:59:57  jdeleau
 *   @scr 5143 Fix the error message that appeared for invalid business date.
 *   The bean was using the error message from the validateBean
 *   superclass rather than the one specified in the requirements.
 *
 *   Revision 1.4  2004/07/17 19:21:23  jdeleau
 *   @scr 5624 Make sure errors are focused on the beans, if an error is found
 *   during validation.
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
 *    Rev 1.1   Sep 25 2003 15:36:10   dcobb
 * Migrate to JDK 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 *
 *    Rev 1.0   Aug 29 2003 16:10:30   CSchellenger
 * Initial revision.
 *
 *    Rev 1.1   Aug 07 2002 19:34:18   baa
 * remove hard coded date formats
 * Resolution for POS SCR-1740: Code base Conversions
 *
 *    Rev 1.0   Apr 29 2002 14:50:16   msg
 * Initial revision.
 *
 *    Rev 1.1   15 Apr 2002 09:34:34   baa
 * make call to setLabel() from the updatePropertyFields() method
 * Resolution for POS SCR-1599: Field name labels on dialog screens use default text instead of text from bundles
 *
 *    Rev 1.0   Mar 18 2002 11:53:08   msg
 * Initial revision.
 *
 *    Rev 1.2   Mar 08 2002 16:06:10   mpm
 * Externalized text for business date UI screens.
 * Resolution for POS SCR-351: Internationalization
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.JComponent;
import javax.swing.JLabel;

import oracle.retail.stores.domain.utility.LocaleUtilities;
import oracle.retail.stores.foundation.manager.gui.UIException;
import oracle.retail.stores.foundation.manager.gui.UISubsystem;
import oracle.retail.stores.foundation.utility.Util;
import oracle.retail.stores.foundation.utility.config.ConfigurationException;
import oracle.retail.stores.pos.ui.DialogScreensIfc;
import oracle.retail.stores.pos.ui.POSJFCUISubsystem;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.UIUtilities;

/**
 * This bean takes input of the business date.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class EnterBusinessDateBean extends ValidatingBean
{
    private static final long serialVersionUID = 4579694992642735237L;

    /** revision number supplied by source-code-control system */
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** label for the business date field */
    protected JLabel businessDateLabel;

    /** business date entry field */
    protected EYSDateField businessDateField;

    /** business date field label string */
    protected static final String labelText = "Business Date:";

    /** business date field label string */
    protected static final String labelTag = "BusinessDateLabel";

    /**
     * Default constructor.
     */
    public EnterBusinessDateBean()
    {
        super();
    }

    /**
     * Configures the class.
     */
    @Override
    public void configure()
    {
        setName("EnterBusinessDateBean");
        uiFactory.configureUIComponent(this, UI_PREFIX);

        initComponents();
        initLayout();
    }

    /**
     * Initializes this bean's components.
     */
    protected void initComponents()
    {
        businessDateLabel = uiFactory.createLabel(labelText, labelText, null, UI_LABEL);
        businessDateField = uiFactory.createEYSDateField("BusinessDateField");
    }

    /**
     * Initializes this bean's layout and lays out the components.
     */
    protected void initLayout()
    {
        UIUtilities.layoutDataPanel(this,
                new JLabel[] { businessDateLabel },
                new JComponent[] { businessDateField },
                false);
    }

    /**
     * Populates model with data from the bean.
     */
    @Override
    public void updateModel()
    {
        if (beanModel instanceof EnterBusinessDateBeanModel)
        {
            EnterBusinessDateBeanModel model = (EnterBusinessDateBeanModel) beanModel;

            // update business date
            if (businessDateField.isInputValid())
            {
                model.setBusinessDate(businessDateField.getEYSDate());
            }
            // if date invalid, restore original business date value
            else
            {
                model.setBusinessDate(model.getNextBusinessDate());
            }
        }
    }

    /**
     * Updates the information displayed on the screen with the model's data.
     */
    @Override
    protected void updateBean()
    {
        initLayout();
        if (beanModel instanceof EnterBusinessDateBeanModel)
        {
            EnterBusinessDateBeanModel model = (EnterBusinessDateBeanModel) beanModel;
            businessDateField.setDate(model.getBusinessDate());
        }
    }

    /**
     * Override ValidatingBean setVisible() to request focus.
     * 
     * @param aFlag indicates if the component should be visible or not.
     */
    @Override
    public void setVisible(boolean aFlag)
    {
        super.setVisible(aFlag);

        if (aFlag && !errorFound())
        {
            setCurrentFocus(businessDateField);
            businessDateField.selectAll();
        }
    }

    /**
     * Activates this bean.
     */
    @Override
    public void activate()
    {
        super.activate();
        businessDateField.addFocusListener(this);
    }

    /**
     * Deactivates this bean.
     */
    @Override
    public void deactivate()
    {
        super.deactivate();
        businessDateField.removeFocusListener(this);
    }

    /**
     * Updates property-based fields.
     **/
    @Override
    protected void updatePropertyFields()
    {
        if (businessDateLabel != null)
        {
            // Retrieve bundle text for labels
            String bdLabel = retrieveText("BusinessDateLabel", labelTag);

            String translatedLabel = getTranslatedDatePattern();
            businessDateLabel.setText(LocaleUtilities.formatComplexMessage(bdLabel, translatedLabel));

            // associate labels with fields
            businessDateField.setLabel(businessDateLabel);
        }
    }

    /**
     * Returns default display string.
     * 
     * @return String representation of object
     */
    @Override
    public String toString()
    {
        return new String("Class: " + Util.getSimpleClassName(this.getClass()) + "(Revision " + getRevisionNumber()
                + ") @" + hashCode());
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

    protected void showErrorScreen()
    {
        // Set up the bean model and show the screen.
        errorFound = true;
        updateModel();
        DialogBeanModel dialogModel = new DialogBeanModel();
        POSJFCUISubsystem ui = (POSJFCUISubsystem) UISubsystem.getInstance();
        dialogModel.setResourceID("InvalidBusinessDate");
        dialogModel.setType(DialogScreensIfc.ERROR);
        dialogModel.setFormModel(getPOSBaseBeanModel());
        dialogModel.setFormScreenSpecName(ui.getCurrentScreenSpecName());
        dialogModel.setUiGeneratedError(true);
        try
        {
            ui.showScreen(POSUIManagerIfc.DIALOG_TEMPLATE, dialogModel);
        }
        catch (UIException uie)
        {
            logger.warn(uie);
        }
        catch (ConfigurationException ce)
        {
            logger.warn(ce);
        }
    }

    /**
     * Main entrypoint for testing.
     * 
     * @param args a string array of parameters
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        UIUtilities.doBeanTest(new EnterBusinessDateBean());
    }
}