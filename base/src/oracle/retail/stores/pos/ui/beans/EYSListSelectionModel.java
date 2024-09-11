/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/EYSListSelectionModel.java /main/16 2014/06/25 15:29:46 yiqzhao Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    yiqzhao   06/24/14 - enable disable pricing button based on the selected
 *                         items.
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:28:08 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:21:34 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:10:59 PM  Robert Pearse   
 *
 * Revision 1.4  2004/09/23 00:07:11  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.3  2004/03/16 17:15:17  build
 * Forcing head revision
 *
 * Revision 1.2  2004/03/08 19:51:32  jdeleau
 * @scr 0 Changes made according to code review results (Comments 
 * added or removed, in general).
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import javax.swing.DefaultListSelectionModel;
//---------------------------------------------------------------------
/**
 Class that fires a value changed event. This is used
 when the highlighted row changes without affecting the
 selection in EYSList. <P>
 It was altered at Gap so it could be extended.  Originally it was
 an inner class of EYSList. <P>
 @version $Revision: /main/16 $
**/
//---------------------------------------------------------------------
public class EYSListSelectionModel extends DefaultListSelectionModel
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 257397135786051770L;

    /*
     * No Item highlighted by default
     */
    
    private int highlightItem = EYSList.NO_SELECTION;
    /*
     * No Item previously hilighted by default
     */
    private int previousHighlightItem = EYSList.NO_SELECTION;
    
    /**
     * Get the currently highlighted item
     * @return highlightITem
     */
    public int getHighlightItem()
    {
        return highlightItem;
    }
   
    /**
     * Set the highlighted Item
     *  
     *  @param newValue The row to highlight
     */
    protected void setHighlightItem(int newValue)
    {
        int old = highlightItem;
        highlightItem = newValue;
        fireValueChanged(old, newValue);
    }
    
    /**
     * Get the previously highlighted item
     *  
     *  @return previously highlighed item
     */
    protected int getPreviousHighlightItem()
    {
        return previousHighlightItem;
    }
    
    /**
     * Set the previously highlighted item
     *  
     *  @param row Row that was previously highlighted
     */
    protected void setPreviousHighlightItem(int row)
    {
        previousHighlightItem = row;
    }
    
    /**
     * Set a range of items as being selected
     *  
     *  @param first beginning item in the range
     *  @param last ending item in the range
     */
    public void setSelectionInterval(int first, int last)
    {
        previousHighlightItem = highlightItem;
        highlightItem = last;
        super.setSelectionInterval(first, last);
    }
    
    /**
     * Add a discrete set of items to the
     * already selected list of items.
     *  
     *  @param first First item in the range to add
     *  @param last Last item in the range to add
     */
    public void addSelectionInterval(int first, int last)
    {
        previousHighlightItem = highlightItem;
        highlightItem = last;
        super.addSelectionInterval(first, last);
    }
}
