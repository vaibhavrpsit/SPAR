/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/behavior/LayawayListAdapter.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:07:00 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/28/10 - updating deprecated names
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *3    360Commerce 1.2         3/31/2005 4:28:50 PM   Robert Pearse   
 *2    360Commerce 1.1         3/10/2005 10:23:02 AM  Robert Pearse   
 *1    360Commerce 1.0         2/11/2005 12:12:16 PM  Robert Pearse   
 *
 Revision 1.4  2004/09/27 16:02:38  jdeleau
 @scr 7251 Make sure to fire the correct response, for an EYSList
 and check for selectedRow instead of selectedIndex.
 *
 Revision 1.3  2004/02/12 16:52:12  mcs
 Forcing head revision
 *
 Revision 1.2  2004/02/11 21:52:29  rhafernik
 @scr 0 Log4J conversion and code cleanup
 *
 Revision 1.1.1.1  2004/02/11 01:04:23  cschellenger
 updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.0   Aug 29 2003 16:13:16   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.0   Apr 29 2002 14:47:10   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:58:30   msg
 * Initial revision.
 * 
 *    Rev 1.1   Jan 19 2002 10:32:52   mpm
 * Initial implementation of pluggable-look-and-feel user interface.
 * Resolution for POS SCR-798: Implement pluggable-look-and-feel user interface
 * 
 *    Rev 1.0   Sep 21 2001 11:33:48   msg
 * Initial revision.
 * 
 *    Rev 1.1   Sep 17 2001 13:18:20   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.behavior;

import javax.swing.JList;

import oracle.retail.stores.domain.financial.LayawayConstantsIfc;
import oracle.retail.stores.domain.financial.LayawaySummaryEntryIfc;
import oracle.retail.stores.pos.ui.beans.EYSList;

/**
 *      An adapter that connects the LayoutBean with the navigation
 *  button bar. If the list has no selection, the button bar will
 *  disable it's buttons.
 *      @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 */
public class LayawayListAdapter extends AbstractListAdapter
{
    /** revision number supplied by Team Connection */
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";
    
    /** string value to disable buttons */
    public static String NO_SELECTION = 
        "Payment[false],Pickup[false],Delete[false]";
    
    /** string value to enable buttons */
    public static String SELECTION = 
        "Payment[true],Pickup[true],Delete[true]";
    
    /** string value to enable buttons */
    public static String DELETE_SELECTION = 
        "Payment[false],Pickup[false],Delete[true]";
    
    /**
     *  Builds a button state string based on the selections in the list.
     *  @param list the JList that triggered the event
     *  @return String representing button states, one of NO_SELECTION, SELECTION,
     *  or DELETE_SELECTION
     */
    public String determineButtonState(JList list)
    {
        int selection = list.getSelectedIndex();
        if(list instanceof EYSList)
        {
            selection = ((EYSList)list).getSelectedRow();
        }
        
        // default is to disable buttons
        String result = NO_SELECTION;
        
        // check for a valid selection
        if(selection != -1)
        {
            LayawaySummaryEntryIfc layaway = 
                (LayawaySummaryEntryIfc)list.getModel().getElementAt(selection);
            
            // if the layaway status is NEW or ACTIVE, enable the buttons
            if(layaway.getStatus() == LayawayConstantsIfc.STATUS_NEW ||
                    layaway.getStatus() == LayawayConstantsIfc.STATUS_ACTIVE)
            {
                result = SELECTION;
            }
            else if (layaway.getStatus() == LayawayConstantsIfc.STATUS_EXPIRED)
            {
                result = DELETE_SELECTION;
            }
        }
        return result;
    }
    
    /**
     *  Returns a string representation of this object.
     *  @return String representation of object
     */
    public String toString()
    {                                   
        String strResult = new String("Class:  LayawayListAdapter (Revision " +
                getRevisionNumber() +
                ")" + hashCode());
        
        return(strResult);
    }                                   
    
    /**
     *  Returns the revision number of the class.
     *  @return String representation of revision number
     */
    public String getRevisionNumber()
    {                                   
        return(revisionNumber);
    }                                   
}
