/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/CustomerSelectOrAddBean.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:56 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:38 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:42 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:10:24 PM  Robert Pearse   
 *
 *   Revision 1.4  2004/03/16 17:15:22  build
 *   Forcing head revision
 *
 *   Revision 1.3  2004/03/16 17:15:17  build
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 20:56:27  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:21  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Sep 10 2003 15:34:08   dcobb
 * Migrate to JVM 1.4.1
 * Resolution for 3361: New Feature:  JVM 1.4.1_03 (Windows) Migration
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;
//java imports
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

import javax.swing.KeyStroke;
import javax.swing.SwingConstants;

import oracle.retail.stores.foundation.utility.Util;

//--------------------------------------------------------------------------
/**
    Subclass of CustomerSelectBean contains the navigation logic inside the
    Customer table.
    @version $KW=@(#); $Ver; $EKW;
    @deprecated as of release 5.0.0
**/
//--------------------------------------------------------------------------
public class CustomerSelectOrAddBean extends CustomerSelectBean

{
    /**
        revision number
    **/
    public static final String revisionNumber = "$KW=@(#); $Ver; $EKW;";

    /**
       fields for keystroke actions
    **/
    protected String keyUpActionName        = new String("UpKey");
    protected String keyDownActionName      = new String("DownKey");
    protected String keyPageUpActionName    = new String("PageUpKey");
    protected String keyPageDownActionName  = new String("PageDownKey");
    protected KeyStroke upStroke            = KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0);
    protected KeyStroke downStroke          = KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0);
    protected KeyStroke pageUpStroke        = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_UP, 0);
    protected KeyStroke pageDownStroke      = KeyStroke.getKeyStroke(KeyEvent.VK_PAGE_DOWN, 0);
    protected QueryKeyAction qKeyAction     = new QueryKeyAction();
    protected MatchKeyAction mKeyAction     = new MatchKeyAction();

    //---------------------------------------------------------------------
    /**
        Listener for QueryKeyActions
     */
    //---------------------------------------------------------------------
    protected class QueryKeyAction implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {
            int pos = ( evt.getActionCommand().equals(keyUpActionName) )
                ? (list.getModel().getSize() - 1)
                : 0;

            list.clearSelection();
            list.revalidate();
            list.setSelectedIndex(pos);
            list.ensureIndexIsVisible(pos);
            list.requestFocusInWindow();
        }
    }
    //---------------------------------------------------------------------
    /**
        Listener for MatchKeyActions
     */
    //---------------------------------------------------------------------
    protected class MatchKeyAction implements ActionListener
    {
        public void actionPerformed(ActionEvent evt)
        {


            int rowCnt = list.getModel().getSize();
            int curPos = list.getSelectedIndex();
            if(evt.getActionCommand().equals(keyUpActionName))
            {
                move(rowCnt, curPos, true);
            }
            else if(evt.getActionCommand().equals(keyDownActionName))
            {
                move(rowCnt, curPos, false);
            }
            else if(evt.getActionCommand().equals(keyPageUpActionName))
            {
                page(rowCnt, curPos, true);
            }
            else if(evt.getActionCommand().equals(keyPageDownActionName))
            {
                page(rowCnt, curPos, false);
            }
            else
            {
            // logit
            }
        }
        //---------------------------------------------------------------------
        /**
            Moves the cursor inside a list one row at a time.
            @param rowCnt number of rows
            @param curPos the cursor position
            @param isUp
         */
        //---------------------------------------------------------------------
        protected void move(int rowCnt, int curPos, boolean isUp)
        {
            if((isUp && curPos == 0) || (!isUp && curPos == (rowCnt - 1)) || curPos < 0)
            {
                list.setSelectedIndex(0);
                list.requestFocusInWindow();
            }
            else
            {
                list.setSelectedIndex(curPos);
            }

        }
        //---------------------------------------------------------------------
        /**
            Moves the cursor up or down a page.
            @param ml, the match list
            @param ql the query list
            @param rowCnt number of rows
            @param curPos the cursor position
            @isUp
         */
        //---------------------------------------------------------------------
        protected void page(int rowCnt, int curPos, boolean isUp)
        {
            if((isUp && curPos == 0) ||
                (!isUp && curPos == (rowCnt - 1)) ||
                curPos < 0)
            {
                list.setSelectedIndex(0);
                list.requestFocusInWindow();
            }
            else
            {
                int blockInc = list.getScrollableBlockIncrement(
                    list.getVisibleRect(),
                    SwingConstants.VERTICAL,
                    1);

                Point curPnt = list.indexToLocation(curPos);

                if (isUp)
                {
                    curPnt.y -= blockInc;
                }
                else
                {
                    curPnt.y += blockInc;
                }

                int newIndex = list.locationToIndex(curPnt);

                if (newIndex < 0) // invalid
                {
                    list.ensureIndexIsVisible(0);
                    list.setSelectedIndex(0);
                    list.requestFocusInWindow();
                }
                else
                {
                    list.ensureIndexIsVisible(newIndex);
                    list.setSelectedIndex(newIndex);
                }
            }
        }
    }
    //---------------------------------------------------------------------
    /**
     * This method sets the focus to the proper component
     */
    //---------------------------------------------------------------------
    public void activate()
    {
        super.activate();

        list.registerKeyboardAction(qKeyAction, keyUpActionName, upStroke, WHEN_FOCUSED);
        list.registerKeyboardAction(qKeyAction, keyUpActionName, pageUpStroke, WHEN_FOCUSED);
        list.registerKeyboardAction(qKeyAction, keyDownActionName, downStroke, WHEN_FOCUSED);
        list.registerKeyboardAction(qKeyAction, keyDownActionName, pageDownStroke, WHEN_FOCUSED);

        list.requestFocusInWindow();

    }

    //---------------------------------------------------------------------
    /**
        Deactivates the screen.
     */
    //---------------------------------------------------------------------
    public void deactivate()
    {
        super.deactivate();
        //JList qList = list;
        //JList mList = topList;

        list.unregisterKeyboardAction(upStroke);
        list.unregisterKeyboardAction(pageUpStroke);
        list.unregisterKeyboardAction(downStroke);
        list.unregisterKeyboardAction(pageDownStroke);

        //enableQuery(false);
    }

    //---------------------------------------------------------------------
    /**
       Retrieves the Team Connection revision number. <P>
       @return String representation of revision number
    */
    //---------------------------------------------------------------------
    public String getRevisionNumber()
    {
        return(Util.parseRevisionNumber(revisionNumber));
    }

    //---------------------------------------------------------------------
    /**
     * main entrypoint - starts the part when it is run as an application
     * @param args java.lang.String[]
     */
    //---------------------------------------------------------------------
    public static void main(String[] args)
    {
            java.awt.Frame frame = new java.awt.Frame();
            CustomerSelectOrAddBean aCustomerSelectBean = new CustomerSelectOrAddBean();
            frame.add("Center", aCustomerSelectBean);
            frame.setSize(aCustomerSelectBean.getSize());
            frame.setVisible(true);
    }
}
