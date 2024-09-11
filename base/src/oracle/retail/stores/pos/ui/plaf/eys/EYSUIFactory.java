/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/eys/EYSUIFactory.java /main/12 2014/01/13 13:13:50 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   01/10/14 - rename JButton methods
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    ddbaker   11/20/08 - Updates for clipping problems
 *
 * ===========================================================================
 * $Log:
 *   3    360Commerce 1.2         3/31/2005 4:28:09 PM   Robert Pearse   
 *   2    360Commerce 1.1         3/10/2005 10:21:36 AM  Robert Pearse   
 *   1    360Commerce 1.0         2/11/2005 12:11:01 PM  Robert Pearse   
 *  $
 *  Revision 1.3  2004/02/12 16:52:14  mcs
 *  Forcing head revision
 *
 *  Revision 1.2  2004/02/11 21:52:29  rhafernik
 *  @scr 0 Log4J conversion and code cleanup
 *
 *  Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *  updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.2   Nov 25 2003 13:35:40   nrao
 * Code Review Changes. Removed PostalTextField method because PostalTextField is not being used anymore.
 * 
 *    Rev 1.1   Oct 31 2003 14:17:32   nrao
 * Added PostalTextField for Instant Credit Enrollment.
 * 
 *    Rev 1.0   Aug 29 2003 16:13:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   23 Jul 2003 00:48:52   baa
 * add EYSbutton to factory
 * 
 *    Rev 1.0   Apr 29 2002 14:46:26   msg
 * Initial revision.
 * 
 *    Rev 1.2   16 Apr 2002 16:42:58   baa
 * paint disable background on square btns
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 *    Rev 1.1   10 Apr 2002 14:00:02   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 *    Rev 1.0   Mar 18 2002 11:58:54   msg
 * Initial revision.
 *
 *    Rev 1.1   13 Mar 2002 23:46:30   baa
 * fix painting problems
 * Resolution for POS SCR-1343: Split tender using Cash causes half of Tender Options to flash
 *
 *    Rev 1.0   Jan 19 2002 11:05:04   mpm
 * Initial revision.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.eys;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import javax.swing.Icon;
import javax.swing.JButton;

import oracle.retail.stores.pos.ui.beans.EYSButton;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * Extends UIFactory for POS specific ui features.
 * 
 * @version $Revision: /main/12 $
 */
public class EYSUIFactory extends UIFactory
{
    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /main/12 $";

    /**
     * Default constructor.
     */
    public EYSUIFactory()
    {
        super();
    }

    /**
     * Overrides UIFactory.createButton to set the button size based on default
     * dimensions.
     * 
     * @param label the display text for the button
     * @param icon an icon for the button
     * @param prefix the lookup identifier to use in configuration
     * @return a configures button
     */
    public EYSButton createEYSButton(String label, Icon icon, String prefix, boolean traversable)
    {
        EYSButton button = super.createEYSButton(label, icon, prefix, traversable);
        configureButton(button, prefix);
        return button;
    }

    /**
     * Overrides UIFactory.createButton to set the button size based on default
     * dimensions.
     * 
     * @param label the display text for the button
     * @param icon an icon for the button
     * @param prefix the lookup identifier to use in configuration
     * @return a configures button
     */
    public JButton createJButton(String label, Icon icon, String prefix)
    {
        JButton button = super.createJButton(label, icon, prefix);
        configureButton(button, prefix);
        return button;
    }

    /**
     * Set the button size based on default dimensions.
     * 
     * @param button the button
     */
    public void configureButton(JButton button, String prefix)
    {
        button.setHorizontalTextPosition(JButton.CENTER);
        button.setVerticalTextPosition(JButton.CENTER);

        // get the button size from the look and feel
        Dimension size = getDimension(prefix + ".size");
        button.setName(prefix);

        button.setMinimumSize(size);
        button.setPreferredSize(size);
        button.setBorderPainted(true);
        button.setRolloverEnabled(false);
    }

    /**
     * Overrides UIFactory.getConstraints to provide bean-specific constraints
     * objects for ButtonBars, dataEntry beans, and Renderers.
     * 
     * @param prefix the name of the constraint
     */
    public GridBagConstraints getConstraints(String prefix)
    {
        GridBagConstraints constraints = new GridBagConstraints();

        if (prefix.equals("ButtonBar"))
        {
            constraints.fill = GridBagConstraints.VERTICAL;
            constraints.weightx = 1.0;
            constraints.weighty = 1.0;
            constraints.insets = new Insets(5, 5, 5, 5);
        }
        else if (prefix.equals("DataEntryBean"))
        {
            constraints.gridx = 0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.NORTHWEST;
        }
        else if (prefix.equals("Renderer"))
        {
            constraints.gridy = 0;
            constraints.fill = GridBagConstraints.HORIZONTAL;
            constraints.anchor = GridBagConstraints.NORTHWEST;
            constraints.insets = getInsets("emptyInsets");
        }
        return constraints;
    }
}
