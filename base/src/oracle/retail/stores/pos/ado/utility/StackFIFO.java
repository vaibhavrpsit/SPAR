/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/utility/StackFIFO.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:41 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:30:09 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:25:27 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:22 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/02/12 16:47:58  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Dec 22 2003 09:39:42   epd
 * bug fixes found by unit test
 * 
 *    Rev 1.0   Dec 17 2003 14:47:00   epd
 * Initial revision.
 *   
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.utility;
import java.util.ArrayList;
import java.util.EmptyStackException;

/**
 *  A First-In First-Out stack implementaion.  This class has the same API 
 * as {@link java.util.Stack}, which is a LIFO stack implementation.
 */
public class StackFIFO
{
    /** Container for data */
    protected ArrayList dataList = new ArrayList();
    
    /**
     * Tests to see if stack is empty.
     * @return
     */    
    public boolean empty()
    {
        boolean result = (dataList.size() == 0) ? true : false;
        return result;
    }
    
    /**
     * Returns the item at the top of the stack without removing from the stack.
     * @return
     * @throws EmptyStackException
     */
    public synchronized Object peek()
    throws EmptyStackException
    {
        if (empty())
        {
            throw new EmptyStackException();
        }

        return dataList.get(0);
    }
    
    /**
     * Returns and removes the top item on the stack.
     * @return
     * @throws EmptyStackException
     */
    public synchronized Object pop()
    throws EmptyStackException
    {
        if (empty())
        {
            throw new EmptyStackException();
        }
        Object lastItem = dataList.get(0);
        dataList.remove(0);
        return lastItem;
    }
    
    /**
     * Returns the 1-based position where an object is on this stack. 
     * If the object o occurs as an item in this stack, this method returns 
     * the distance from the top of the stack of the occurrence nearest 
     * the top of the stack; the topmost item on the stack is considered 
     * to be at distance 1. The equals method is used to compare o to the 
     * items in this stack.      
     * @param arg
     * @return
     */
    public synchronized int search(Object arg)
    {
        int result = -1;
        for (int i=0; i<dataList.size(); i++)
        {
            Object testItem = dataList.get(i);
            if (arg.equals(testItem))
            {
                // calculate the 1-based index of the last item
                // in the List (the top item on the stack)
                result = i + 1;
                break;
            }
        }
        return result;
    }
    
    /**
     * Pushes an item onto the stack.
     * @param arg
     * @return
     */
    public Object push(Object arg)
    {
        dataList.add(arg);
        return arg;
    }
}
