/* ===========================================================================
* Copyright (c) 2011, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/SlidingPopupMenu.java /main/4 2012/12/06 09:57:45 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/06/12 - update to be able to slight to the right
 *    cgreene   09/20/12 - Popupmenu implmentation round 2
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPopupMenu;
import javax.swing.Timer;

import org.apache.log4j.Logger;

/**
 * A JPopupMenu that animates its appearance from behind its invoker.
 *
 * @author cgreene
 * @since 14.0
 */
public class SlidingPopupMenu extends JPopupMenu
{
    private static final long serialVersionUID = -1145641839176921059L;
    /** Debug logger. */
    private static final Logger logger = Logger.getLogger(SlidingPopupMenu.class);

    protected boolean slidingLeft = true;
    protected int menuStartPos;
    protected int animationIncrement = 8; 
    protected Timer menuAnimationTimer = new Timer(1, new MenuAnimationListener());

    /**
     * Default constructor. Marks this popup as invisible and the border is not
     * painted.
     */
    public SlidingPopupMenu()
    {
        setOpaque(false);
        setLayout(null);
        setBorderPainted(false);
    }

    /* (non-Javadoc)
     * @see java.awt.Container#add(java.awt.Component)
     */
    @Override
    public Component add(Component child)
    {
        child.setVisible(false);
        return super.add(child);
    }

    /**
     * Call this method to show the popup. Sets the size of the popup to the
     * invoker's parent's size, places the menu at the Y coordinate of the
     * button, and starts the animation.
     *
     * @see JPopupMenu#show(Component, int, int)
     */
    public void show(Component invoker)
    {
        Dimension d = invoker.getSize();
        setPreferredSize(d);
        slidingLeft = invoker.getLocationOnScreen().x > d.width;
        menuStartPos = (slidingLeft)? d.width : 0;
        int x = (slidingLeft)? -d.width : d.width;
        super.show(invoker, x, 0);
        menuAnimationTimer.start();
    }

    /* (non-Javadoc)
     * @see javax.swing.JPopupMenu#menuSelectionChanged(boolean)
     */
    @Override
    public void menuSelectionChanged(boolean isIncluded)
    {
        if (logger.isDebugEnabled())
        {
            logger.debug("isIncluded=" + isIncluded + ", name="+getInvoker().getName());
        }
        if (!isIncluded && getInvoker() instanceof NavigationButtonBean)// if popping up a submenu, the invoker will be a NavButtonBean. See 
        {
            NavigationButtonBean bean = (NavigationButtonBean)getInvoker();
            // do not disappear the menu upon selection 
            isIncluded = bean.isShowingMenu();
            if (isIncluded && logger.isDebugEnabled())
            {
                logger.debug("Menu selection was not included, but changed to true because of submenu.");
            }
        }
        super.menuSelectionChanged(isIncluded);
    }

    /**
     * @return the animationIncrement
     */
    public int getAnimationIncrement()
    {
        return animationIncrement;
    }

    /**
     * @param animationIncrement the animationIncrement to set
     */
    public void setAnimationIncrement(int animationIncrement)
    {
        this.animationIncrement = animationIncrement;
    }

    // -------------------------------------------------------------------------
    protected class MenuAnimationListener implements ActionListener
    {

        /* (non-Javadoc)
         * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
         */
        @Override
        public void actionPerformed(ActionEvent e)
        {
            if (getComponentCount() < 1)
            {
                return;
            }

            // slide child across popup
            Component child = getComponent(0);
            if (!child.isVisible())
            {
                setLayout(null);
                Dimension d = getSize();
                child.setBounds((slidingLeft)? d.width : -d.width, 0, d.width, d.height);
                child.setVisible(true);
            }
            else
            {
                Rectangle bounds = child.getBounds();
                if (slidingLeft)
                {
                    if (bounds.x > menuStartPos - bounds.width)
                    {
                        int x = Math.max(0, bounds.x - getAnimationIncrement());
                        bounds.x = x;
                        child.setBounds(bounds);
                        validate();
                        return;
                    }
                }
                else
                {
                    if (bounds.x < 0)
                    {
                        int x = Math.min(bounds.x + getAnimationIncrement(), 0);
                        bounds.x = x;
                        child.setBounds(bounds);
                        validate();
                        return;
                    }
                }

                menuAnimationTimer.stop();
            }            
        }
    }
}