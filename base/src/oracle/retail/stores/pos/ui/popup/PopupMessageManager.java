/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/popup/PopupMessageManager.java /main/9 2011/12/05 12:16:24 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED (MM/DD/YY)
 *    cgreen 12/05/11 - updated from deprecated packages and used more
 *                      bigdecimal constants
 *    cgreen 05/26/10 - convert to oracle packaging
 *    cgreen 04/26/10 - XbranchMerge cgreene_tech43 from
 *                      st_rgbustores_techissueseatel_generic_branch
 *    cgreen 04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abonda 01/03/10 - update header date
 *    cgreen 04/01/09 - refactored cash under warning to animate from left side
 *    mdecam 02/09/09 - Refactored to use non-deprecated methods.
 *    nkgaut 09/24/08 - A new class for PopUpMessages for Cash Drawer Warnings
 * 
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.popup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;

import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.ui.plaf.UIFactory;

/**
 * A manager of JPopups that display for each string message added to this.
 * This default implementation is for the popups to be animated to grow in size
 * until they are fully sized.
 * 
 * @since 13.1
 */
public class PopupMessageManager implements PopupMessageManagerIfc, ActionListener
{
    private static final long serialVersionUID = -8319901690436806622L;

    /** Key to configured property for animation delay. */
    public static final String KEY_ANIMATION_DELAY = "CashDrawerWarning.AnimationDelay";
    /** Key to configured property for initial delay. */
    public static final String KEY_INITIAL_DELAY = "CashDrawerWarning.InitialDelay";
    /** Key to configured property for message life time. */
    public static final String KEY_LIFETIME = "CashDrawerWarning.LifeTime";

    /** The default pause between animations of the sliding. */
    public static final int ANIMATION_DELAY = 7;
    /** The default number of milliseconds before the animation begins. */
    public static final int INITIAL_DELAY = 500;
    /** The default number of milliseconds that the message should exist before sliding out. */
    public static final int LIFETIME = 6000;

    /** Key to the client property of when the message was created. */
    public static final String CREATED_PROPERTY = "CREATED_PROPERTY";
    /** Key to the client property of where the message is placed. */
    public static final String LOCATION_PROPERTY = "LOCATION_PROPERTY";

    /** Color to paint popup background. */
    protected static Color backgroundColor;
    /** Color to paint popup border. */
    protected static Color secondaryColor;

    /** The animation timer. */
    protected Timer slideTimer;
    /** The fifo list of pending messages. */
    protected List<String> pendingMessages;
    /** The fifo list of displayed messages. */
    protected List<JPopupMenu> displayedMessages;
    /** The component that will be the parent of the popup. */
    protected Component owner;

    /** The pause between animations of the sliding. */
    protected int animationDelay;
    /** Pause before start of displaying message */
    protected int initialDelay;
    /** LifeTime of popup message. */
    protected long lifeTime;

    /**
     * Initialize UI colors that will not change.
     */
    static void init()
    {
        // example colorVal = "#8b1c62" /* maroon */
        String color = "";
        if (backgroundColor == null)
        {
            color = UIFactory.getInstance().getUIProperty("Color.attention",
                    LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            if (!"".equals(color))
            {
                backgroundColor = Color.decode(color);
            }
        }
        if (secondaryColor == null)
        {
            color = UIFactory.getInstance().getUIProperty("Color.accent",
                    LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE));
            if (!"".equals(color))
            {
                secondaryColor = Color.decode(color);
            }
        }
    }

    /**
     * Constructor
     */
    public PopupMessageManager()
    {
        pendingMessages = new ArrayList<String>(1);
        displayedMessages = new ArrayList<JPopupMenu>(1);
        slideTimer = new Timer(animationDelay, this);
        slideTimer.setInitialDelay(initialDelay);
        slideTimer.setCoalesce(true);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.popup.PopupMessageManagerIfc#getOwner()
     */
    public Component getOwner()
    {
        return owner;
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.popup.PopupMessageManagerIfc#setOwner(java.awt.Component)
     */
    public void setOwner(Component owner)
    {
        this.owner = owner;
    }

    /**
     * @return the animationDelay
     */
    public int getAnimationDelay()
    {
        return animationDelay;
    }

    /**
     * @return the lifeTime
     */
    public long getLifeTime()
    {
        return lifeTime;
    }

    /**
     * @return the initialDelay
     */
    public int getInitialDelay()
    {
        return initialDelay;
    }

    /**
     * @param animationDelay the animationDelay to set
     */
    public void setAnimationDelay(int animationDelay)
    {
        this.animationDelay = animationDelay;
    }

    /**
     * @param lifeTime the lifeTime to set
     */
    public void setLifeTime(long lifeTime)
    {
        this.lifeTime = lifeTime;
    }

    /**
     * @param initialDelay the initialDelay to set
     */
    public void setInitialDelay(int initialDelay)
    {
        this.initialDelay = initialDelay;
    }

    /**
     * Returns true if the popup has reached the point where it stops animating
     * and slides back.
     * 
     * @param popup
     * @return
     */
    protected boolean hasReachedApex(JPopupMenu popup)
    {
        Dimension d = popup.getSize();
        int targetWidth = popup.getComponent(0).getPreferredSize().width + 5;
        return (d.width >= targetWidth);
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.popup.PopupMessageManagerIfc#hasMessage()
     */
    public synchronized boolean hasMessage()
    {
        return !pendingMessages.isEmpty();
    }

    /* (non-Javadoc)
     * @see oracle.retail.stores.pos.ui.popup.PopupMessageManagerIfc#addMessage(java.lang.String)
     */
    public synchronized void addMessage(String message)
    {
        if (getOwner() == null)
            throw new IllegalStateException("Owner of popup must be non-null to display a message");

        pendingMessages.add(message);
        slideTimer.start();
    }

    /**
     * Responds to the actions spawned by the timer.
     * 
     * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
     */
    public void actionPerformed(ActionEvent e)
    {
        boolean stopTimer = true;

        // create a label for any pending messages
        if (displayedMessages.size() == 0 && hasMessage())
        {
            String text = pendingMessages.remove(0);
            displayedMessages.add(createPopup(text));
        }

        // loop through and animate the labels
        for (Iterator<JPopupMenu> iter = displayedMessages.iterator(); iter.hasNext();)
        {
            JPopupMenu popup = iter.next();

            // check if visible
            if (!popup.isVisible())
            {
                iter.remove();
                if (iter.hasNext())
                {
                    continue;
                }
                // else
                break;
            }
            // a message is still visible, so keep going
            stopTimer = false;

            // determine time alive
            long born = (Long)popup.getClientProperty(CREATED_PROPERTY);
            long timeAlive = System.currentTimeMillis() - born;

            // at the top, pause until life is over
            if (hasReachedApex(popup) && timeAlive < LIFETIME)
            {
                continue;
            }

            // resize width of popup
            Dimension d = popup.getSize();
            if (timeAlive < LIFETIME)
            {
                d.width++;
            }
            else
            {
                d.width--;              
            }
            popup.setPopupSize(d);

            // if is past is initial location, then remove the message
            if (d.width <= 0)
            {
                popup.setVisible(false);
                iter.remove();
            }
        }

        if (stopTimer)
        {
            slideTimer.stop();
        }
    }

    /**
     * Create a popup to display the specified message and make it visible
     * 
     * @param text
     * @return
     */
    protected JPopupMenu createPopup(String text)
    {
        init();
        JLabel label = new JLabel(text);
        label.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));

        final JPopupMenu popup = new JPopupMenu();
        label.addMouseListener(new MouseAdapter()
        {
            public void mousePressed(MouseEvent e)
            {
                popup.setVisible(false);
            }
        });
        popup.setLayout(new BorderLayout());
        popup.setBackground(backgroundColor);
        popup.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createEmptyBorder(1,1,1,1),
                BorderFactory.createLineBorder(secondaryColor, 1)));
        popup.add(label);
        // mark when the message was created
        popup.putClientProperty(CREATED_PROPERTY, System.currentTimeMillis());
        popup.setPreferredSize(new Dimension(0, getOwner().getHeight()));
        // show the popup
        popup.show(getOwner(), 0, 0);
        return popup;
    }
}
