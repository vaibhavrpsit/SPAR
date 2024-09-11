/* ===========================================================================
* Copyright (c) 2008, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EYSButton.java /main/19 2012/12/14 11:29:40 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/14/12 - Button image and plaf loading updates
 *    cgreene   12/05/12 - add javadoc
 *    cgreene   10/17/12 - tweak implementation of search field with icon
 *    cgreene   10/15/12 - implement buttons that can use images to paint
 *                         background
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   12/16/09 - formatted
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:28:08 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:21:33 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:10:59 PM  Robert Pearse   
 *
 *Revision 1.4  2004/03/16 17:15:22  build
 *Forcing head revision
 *
 *Revision 1.3  2004/03/16 17:15:17  build
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 20:56:27  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:22  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:10:32   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   23 Jul 2003 00:45:42   baa
 * add overwrite traversable method.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Image;

import javax.swing.Icon;
import javax.swing.JButton;

/**
 * This class allows to overwrite standard button behavior.
 */
public class EYSButton extends JButton
{
    private static final long serialVersionUID = 4808245602514752443L;

    /** revision number supplied for PVCS */
    public static final String revisionNumber = "$Revision: /main/19 $";

    /** Button UI name. Equals "EYSButtonUI". */
    public static final String uiClassID = "EYSButtonUI";

    /**
     * @deprecated as of 14.0. Use {@link #isFocusable()} instead.
     */
    protected boolean focusTraversable = false;

    /** If true, this button should render an arrow for presenting a popup menu. */
    protected boolean menu;

    /** If true, this button has caused a sub-menu to display. */
    protected boolean menuDisplayed;

    /** If set, this button should be rendered as this image. */
    protected Image imageUp;
    /** The image to render this button when pressed. */
    protected Image imageDown;
    /** The image to render this button when disabled. */
    protected Image imageDisabled;

    /**
     * Constructor.
     */
    public EYSButton(String text)
    {
        this(text, null, false);
    }

    /**
     * Constructor.
     */
    public EYSButton(String label, Icon icon)
    {
        this(label, icon, false);
    }

    /**
     * Constructor.
     */
    public EYSButton(String label, Icon icon, boolean focusable)
    {
        super(label, icon);
        setFocusable(focusable);
    }

    /* (non-Javadoc)
     * @see javax.swing.JButton#getUIClassID()
     */
    @Override
    public String getUIClassID()
    {
        return uiClassID;
    }

    /**
     * If true, the UI should draw an indication on the button that a menu will
     * be presented when this button is pressed.
     * 
     * @return the menu
     */
    public boolean isMenu()
    {
        return menu;
    }

    /**
     * If true, the UI should draw an indication on the button that a menu will
     * be presented when this button is pressed.
     * 
     * @param menu the menu to set
     */
    public void setMenu(boolean menu)
    {
        this.menu = menu;
    }

    /**
     * If true, this button has caused a sub-menu to display.
     *
     * @return the menuDisplayed
     */
    public boolean isMenuDisplayed()
    {
        return menuDisplayed;
    }

    /**
     * If true, this button has caused a sub-menu to display.
     *
     * @param menuDisplayed the menuDisplayed to set
     */
    public void setMenuDisplayed(boolean menuDisplayed)
    {
        this.menuDisplayed = menuDisplayed;
    }

    /**
     * Returns true if the UI is to paint the button as an image.
     *
     * @return true if the UI is to paint the button as an image.
     */
    public boolean isBackgroundImage()
    {
        return (getImageUp() != null);
    }

    /**
     * If set, this button should be rendered as this image.
     *
     * @return the imageUp
     */
    public Image getImageUp()
    {
        return imageUp;
    }

    /**
     * If set, this button should be rendered as this image.
     *
     * @param imageUp the imageUp to set
     */
    public void setImageUp(Image imageUp)
    {
        this.imageUp = imageUp;
    }

    /**
     * The image to render this button when pressed.
     *
     * @return the imageDown
     */
    public Image getImageDown()
    {
        return imageDown;
    }

    /**
     * The image to render this button when pressed.
     *
     * @param imageDown the imageDown to set
     */
    public void setImageDown(Image imageDown)
    {
        this.imageDown = imageDown;
    }

    /**
     * The image to render this button when disabled.
     *
     * @return the imageDisabled
     */
    public Image getImageDisabled()
    {
        return imageDisabled;
    }

    /**
     * The image to render this button when disabled.
     *
     * @param imageDisabled the imageDisabled to set
     */
    public void setImageDisabled(Image imageDisabled)
    {
        this.imageDisabled = imageDisabled;
    }
}
