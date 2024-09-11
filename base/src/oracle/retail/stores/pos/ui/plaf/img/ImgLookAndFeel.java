/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/plaf/img/ImgLookAndFeel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:21 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:22:04 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:11:22 PM  Robert Pearse   
 *
 *Revision 1.4  2004/09/23 00:07:18  kmcbride
 *@scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 *Revision 1.3  2004/02/12 16:52:16  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:52:29  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:28   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.2   Jul 15 2003 10:12:02   baa
 * change border on panels and move gifs to external locale jar
 * Resolution for 2152: Organize locale sensitive Files
 * 
 *    Rev 1.1   Apr 03 2003 14:32:52   jgs
 * Changing the conduit script to allow the resource manager to read the images changed the timing of drawing the screen.  This change allows the app a little more time to get it done.
 * Resolution for 2101: Remove uses of  foundation constant  EMPTY_STRING
 * 
 *    Rev 1.0   Apr 29 2002 14:45:34   msg
 * Initial revision.
 * 
 *    Rev 1.1   10 Apr 2002 14:00:06   baa
 * make code compliant with coding guidelines
 * Resolution for POS SCR-1590: PLAF code does not meet the coding standards
 *
 *    Rev 1.0   Mar 18 2002 11:58:56   msg
 * Initial revision.
 *
 *    Rev 1.3   25 Feb 2002 14:44:38   baa
 * fix borders on image plaf
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 *
 *    Rev 1.2   08 Feb 2002 18:52:46   baa
 * defect fix
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.plaf.img;

// java imports
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.plaf.IconUIResource;
import javax.swing.plaf.InsetsUIResource;

import oracle.retail.stores.pos.ui.UIUtilities;
import oracle.retail.stores.pos.ui.plaf.eys.EYSLookAndFeel;

//------------------------------------------------------------------------------
/**
 *  Implements The 360Commerce Image Look and Feel. The ui
 *  defaults for this LAF are loaded from the imageplaf property file. This
 *  allows ui elements like colors, fonts, borders, etc. to be
 *  modified without recompiling the application.
 *  @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 **/
//------------------------------------------------------------------------------
public class ImgLookAndFeel extends EYSLookAndFeel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 2569872080209322793L;

    /** revision number supplied by PVCS **/
    public static String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    //--------------------------------------------------------------------------
    /**
     *    Gets the name of this look and feel.
     *    @return the name of this look and feel
     */
    //--------------------------------------------------------------------------
    public String getName() {
        return "Image";
    }

    //--------------------------------------------------------------------------
    /**
     *    Gets the id of this look and feel.
     *    @return this look and feel's id
     */
    //--------------------------------------------------------------------------
    public String getID() {
        return "Image";
    }

    //--------------------------------------------------------------------------
    /**
     *    Gets this look and feel's description.
     *    @return the description
     */
    //--------------------------------------------------------------------------
    public String getDescription() {
        return "The 360Store(tm) Image Look and Feel";
    }


    //--------------------------------------------------------------------------
    /**
     *    Returns true if this is the native look and feel for
     *    the current platform.
     *    @return always returns false for the Image L&F
     */
    //--------------------------------------------------------------------------
    public boolean isNativeLookAndFeel() {
        return false;
    }


    //--------------------------------------------------------------------------
    /**
     *    Returns true if this L&F is supported by the current platform.
     *    @return always returns true for the 360Store L&F
     */
    //--------------------------------------------------------------------------
    public boolean isSupportedLookAndFeel() {
        return true;
    }

    //--------------------------------------------------------------------------
    /**
     * Initialize the uiClassID to ImageComponentUI mapping.
     * The JComponent classes define their own uiClassID constants
     * (see AbstractComponent.getUIClassID).  This table must
     * map those constants to a BasicComponentUI class of the
     * appropriate type.
     */
    //--------------------------------------------------------------------------
    protected void initClassDefaults(UIDefaults table)
    {
        super.initClassDefaults(table);

    // the package name of the new plaf
           String packageName = "oracle.retail.stores.pos.ui.plaf.img.";

    // create mappings that override BasicUI objects
        Object[] uiDefaults =
        {
            "PanelUI",        packageName + "ImgPanelUI",
            "ButtonUI",       packageName + "ImgButtonUI",
            "EYSScrollBarUI", packageName + "ImgScrollBarUI"
        };
        table.putDefaults(uiDefaults);
    }


    //--------------------------------------------------------------------------
    /*
     *    Initializes the default characteristics for ui components. This
     *    sets display properties for standard components as well as 360Store
     *    beans.
     *    @param table the UIDefaults table
     */
    //--------------------------------------------------------------------------
    protected void initComponentDefaults(UIDefaults table)
    {
        super.initComponentDefaults(table);
        Object localPressedIcon     =  new IconUIResource(new ImageIcon(UIUtilities.getImage("icons/LocalPressedIcon.gif",null)));
         Object localActiveIcon     =  new IconUIResource(new ImageIcon(UIUtilities.getImage("icons/LocalActiveIcon.gif",null)));
         Object localDisabledIcon   =  new IconUIResource(new ImageIcon(UIUtilities.getImage("icons/LocalDisabledIcon.gif",null)));
         Object globalPressedIcon   =  new IconUIResource(new ImageIcon(UIUtilities.getImage("icons/GlobalPressedIcon.gif",null)));
         Object globalActiveIcon    =  new IconUIResource(new ImageIcon(UIUtilities.getImage("icons/GlobalActiveIcon.gif",null)));
         Object globalDisabledIcon  =  new IconUIResource(new ImageIcon(UIUtilities.getImage("icons/GlobalDisabledIcon.gif",null)));
         Object scrollUpIcon        =  new IconUIResource(new ImageIcon(UIUtilities.getImage("icons/ScrollUpIcon.gif",null)));
         Object scrollDownIcon      =  new IconUIResource(new ImageIcon(UIUtilities.getImage("icons/ScrollDownIcon.gif",null)));   
        
        Object waterMark           =  new IconUIResource(new ImageIcon(UIUtilities.getImage("watermark_800x600_lantana.gif",null)));

    // override component defaults
        Object[] defaults =
        {
             "HorizontalButton.icon", globalActiveIcon,
            "HorizontalButton.disabledIcon", globalDisabledIcon,
            "HorizontalButton.pressedIcon", globalPressedIcon,
             "DialogButton.icon", globalActiveIcon,
            "DialogButton.disabledIcon", globalDisabledIcon,
            "DialogButton.pressedIcon", globalPressedIcon,
            "VerticalButton.icon", localActiveIcon,
            "VerticalButton.disabledIcon", localDisabledIcon,
            "VerticalButton.pressedIcon", localPressedIcon,
            "ScrollDownButton.icon", scrollDownIcon,
            "ScrollDownButton.disabledIcon", scrollDownIcon,
            "ScrollDownButton.pressedIcon", scrollDownIcon,
            "ScrollUpButton.icon", scrollUpIcon,
            "ScrollUpButton.disabledIcon", scrollUpIcon,
            "ScrollUpButton.pressedIcon", scrollUpIcon,
            "ImageAppFrame.beanBackground", waterMark,
            "GlobalButton.gapInsets", new InsetsUIResource(0, 0, 5, 0)

        };
        table.putDefaults(defaults);
    }
}
