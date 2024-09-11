/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/HelpBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:55 mszekely Exp $
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
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:19 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:01 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:18 PM  Robert Pearse   
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
 *    Rev 1.0   Aug 29 2003 16:10:44   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Aug 14 2002 18:17:46   baa
 * format currency 
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.1   Jul 05 2002 17:58:50   baa
 * code conversion and reduce number of color settings
 * Resolution for POS SCR-1740: Code base Conversions
 * 
 *    Rev 1.0   Apr 29 2002 14:52:40   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:55:22   msg
 * Initial revision.
 * 
 *    Rev 1.4   Feb 23 2002 15:04:16   mpm
 * Re-started internationalization initiative.
 * Resolution for POS SCR-351: Internationalization
 *
 *    Rev 1.3   21 Feb 2002 13:44:28   baa
 * fix fonts for linux
 * Resolution for POS SCR-1369: *Required has the top part of word cut off
 *
 *    Rev 1.2   08 Feb 2002 18:52:34   baa
 * defect fix
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

// Java imports
import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JLabel;
import javax.swing.UIManager;

import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.pos.ui.UIUtilities;
//-------------------------------------------------------------------------
/**
 *  This bean is the presentation of the status area; it contains information
    such as the Cashier, Operator, and Customer Names; the online/offline
    state, time and register number.
 */
//-------------------------------------------------------------------------
public class HelpBean extends BaseBeanAdapter
{
    /** default help text */
    public static final String DEFAULT_HELP_TEXT = " Required Text";

    /** the property tag for the help text */
    public static final String HELP_TAG = "HelpLabel";

    /** label for the help text */
    protected JLabel  helpLabel = null;
    protected JLabel  star = null;
    /** the property tag for the help text */
    protected String  helpTag = HELP_TAG;

    //-------------------------------------------------------------------------
    /**
     *  Default constructor.
     */
    public HelpBean()
    {
        super();
    }

    //--------------------------------------------------------------------------
    /**
     * Configure the class.
     */
    public void configure()
    {
        setName("HelpBean");

        uiFactory.configureUIComponent(this, UI_PREFIX);
        setPreferredSize(uiFactory.getDimension("helpPanelDimension"));

        helpLabel = uiFactory.createLabel(DEFAULT_HELP_TEXT, null, UI_LABEL);
        helpLabel.setHorizontalAlignment(JLabel.LEFT);

        star = uiFactory.createLabel("*", null, UI_LABEL);
        Font newFont = UIManager.getFont("requiredFont");
        Color newColor = UIManager.getColor("requiredMark");

        star.setFont(newFont);
        star.setForeground(newColor);
        star.setHorizontalAlignment(JLabel.RIGHT);
        setLayout(new GridBagLayout());

        GridBagConstraints constraints = uiFactory.getConstraints("DataEntryBean");

        // layout top label and field
        constraints.gridy = 0;
        constraints.gridx = 3;
        constraints.insets = uiFactory.getInsets("defaultHelp");
        constraints.fill   = GridBagConstraints.HORIZONTAL;
        add(star, constraints);

        constraints.insets = uiFactory.getInsets("defaultHelp");
        constraints.gridx = 4;
        constraints.fill   = GridBagConstraints.NONE;
        add(helpLabel, constraints);

    }

    //-----------------------------------------------------------------------
    /**
     *  Sets the property tag used to lookup the help text.
     *  @param propValue a string property tag
     */
    public void setHelpTag(String propValue)
    {
        helpTag = propValue;
        updatePropertyFields();
    }

    //-----------------------------------------------------------------------
    /**
     *  Sets the help text.
     *  @param propValue a string to display
     */
    public void setHelpText(String propValue)
    {
        helpLabel.setText(propValue);
    }

    //---------------------------------------------------------------------------
    /**
     *    Overrides set model to do nothing.
     *    @param model the model to be shown.
     */
    public void setModel(UIModelIfc model)
    {
    }

    //--------------------------------------------------------------------------
    /**
     *  Updates the bean when the locale changes.
     */
    protected void updatePropertyFields()
    {
        setHelpText(retrieveText(helpTag, DEFAULT_HELP_TEXT));
    }

    //--------------------------------------------------------------------------
    /**
     *    Main entry point for testing.
     */
    public static void main(String[] args)
    {
        UIUtilities.setUpTest();
        HelpBean bean = new HelpBean();
        bean.configure();

        UIUtilities.doBeanTest(bean);
    }
}
