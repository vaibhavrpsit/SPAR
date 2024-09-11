/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/AbstractListAdapter.java /main/11 2013/09/06 14:08:53 rahravin Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rahravin  08/27/13 - Enable delete in split tender
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:27:07 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:19:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:20 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:52:12  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:52:29  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:06   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:46:30   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:58:16   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:44   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:34:00   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:18:26   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import oracle.retail.stores.foundation.manager.gui.UIAdapter;
import oracle.retail.stores.pos.ui.beans.GlobalNavigationButtonBean;
import oracle.retail.stores.pos.ui.beans.NavigationButtonBean;

//------------------------------------------------------------------------------
/**
 *      Abstract adapter that connects a ListBean with a navigation button 
 *  bean. The selection in the list determines the enabled state of
 *  the buttons in the button bean.
 *      @version $Revision: /main/11 $
 */
//------------------------------------------------------------------------------
public abstract class AbstractListAdapter extends UIAdapter
                                          implements ListSelectionListener
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /main/11 $";
    
//------------------------------------------------------------------------------
/**
 *  Enables or disables the navigation buttons when a selection
 *  event occurs.
 *  @param theEvent a list selection event
 */
    public void valueChanged(ListSelectionEvent theEvent)
    {
        if(theEvent != null)
        {
            JList theList = (JList)theEvent.getSource();
            String stateString = determineButtonState(theList);
            // if the target is a navigation bar, set the button states
                   if(targetBean instanceof NavigationButtonBean)
            {
                ((NavigationButtonBean)targetBean).setButtonStates(stateString);
            }
            // if the target is a global navigation bar, set the button states
            if (targetBean instanceof GlobalNavigationButtonBean)
            {
                ((GlobalNavigationButtonBean)targetBean).setButtonStates(stateString);
            }
        }
    }
    
//------------------------------------------------------------------------------
/**
 *  Builds a button state string based on the selections in the list.
 *  Subclasses must implement this method.
 *  @param list the JList that triggered the event
 */
    public abstract String determineButtonState(JList list);
    
}
