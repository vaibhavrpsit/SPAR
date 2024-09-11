/* ===========================================================================
* Copyright (c) 2007, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/BytesContent.java /rgbustores_13.4x_generic_branch/1 2011/04/19 10:15:37 mchellap Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mchellap  04/19/11 - XbranchMerge mchellap_bug-12356218 from main
 *    mchellap  04/15/11 - BUG#12356218 Overriding cached data
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    jswan     06/26/09 - Fix issues swiping card when looking up transactions
 *                         with credit card.
 *
 * ===========================================================================
 * $Log:
 *  1    360Commerce 1.0         11/13/2007 2:40:38 PM  Jack G. Swan    Added
 *       to support retrieving card numbers from UI as a byte array instead of
 *        a String object.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.io.Serializable;
import java.util.Vector;

import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Position;
import javax.swing.text.Segment;
import javax.swing.undo.AbstractUndoableEdit;
import javax.swing.undo.CannotRedoException;
import javax.swing.undo.CannotUndoException;
import javax.swing.undo.UndoableEdit;

//-------------------------------------------------------------------------
/**
   This field allows input to be valid if it meets max and min lenght
   requirements.
*/
//-------------------------------------------------------------------------
public class BytesContent implements AbstractDocument.Content, Serializable
{
    /**  */
    private static final long serialVersionUID = -1265381632108264687L;

    // Where the string goes.
    protected int offset;
    // Length of the string.
    protected int length;
    // An array of instances of UndoPosRef for the Positions in the
    // range that was removed, valid before undo.
    protected Vector posRefs;
    protected byte[] bytes;

    // Private and transient data members
    private static final char[] empty = new char[0];
    private char[] data;
    private int count;
    transient Vector marks;

    /**
     * Creates a new StringContent object.  Initial size defaults to 10.
     */
    public BytesContent()
    {
        this(10);
    }

    /**
     * Creates a new StringContent object, with the initial
     * size specified.  If the length is < 1, a size of 1 is used.
     *
     * @param initialLength the initial size
     */
    public BytesContent(int initialLength)
    {
        if (initialLength < 1) {
            initialLength = 1;
        }
        data = new char[initialLength];
        data[0] = '\n';
        count = 1;
    }

    /**
     * Returns the length of the content.
     *
     * @return the length >= 1
     * @see AbstractDocument.Content#length
     */
    public int length() {
    return count;
    }

    /**
     * Inserts a string into the content.
     *
     * @param where the starting position >= 0 && < length()
     * @param str the non-null string to insert
     * @return an UndoableEdit object for undoing
     * @exception BadLocationException if the specified position is invalid
     * @see AbstractDocument.Content#insertString
     */
    public UndoableEdit insertString(int where, String str) throws BadLocationException
    {
        if (where >= count || where < 0)
        {
            throw new BadLocationException("Invalid location", count);
        }
        char[] chars = str.toCharArray();
        replace(where, 0, chars, 0, chars.length);
        if (marks != null)
        {
            updateMarksForInsert(where, str.length());
        }
        return new InsertUndo(where, str.length());
    }

    /**
     * Removes part of the content.  where + nitems must be < length().
     *
     * @param where the starting position >= 0
     * @param nitems the number of characters to remove >= 0
     * @return an UndoableEdit object for undoing
     * @exception BadLocationException if the specified position is invalid
     * @see AbstractDocument.Content#remove
     */
    public UndoableEdit remove(int where, int nitems) throws BadLocationException
    {
        if (where + nitems >= count)
        {
            throw new BadLocationException("Invalid range", count);
        }

        // The getString() method is invalid in this class; it thows an exception
        // String removedString = getString(where, nitems);
        String removedString = "";
        UndoableEdit edit = new RemoveUndo(where, removedString);
        replace(where, nitems, empty, 0, 0);
        if (marks != null)
        {
            updateMarksForRemove(where, nitems);
        }
        return edit;
    }

    /**
     * Retrieves a portion of the content.  where + len must be <= length().
     *
     * @param where the starting position >= 0
     * @param len the length to retrieve >= 0
     * @return a string representing the content; may be empty
     * @exception BadLocationException if the specified position is invalid
     * @see AbstractDocument.Content#getString
     */
    public String getString(int where, int len) throws BadLocationException
    {
        if (where + len > count)
        {
            throw new BadLocationException("Invalid range", count);
        }
        //return new String(data, where, len);
        return new String();
    }

    /**
     * Retrieves a portion of the content.  where + len must be <= length()
     *
     * @param where the starting position >= 0
     * @param len the number of characters to retrieve >= 0
     * @param chars the Segment object to return the characters in
     * @exception BadLocationException if the specified position is invalid
     * @see AbstractDocument.Content#getChars
     */
    public void getChars(int where, int len, Segment chars) throws BadLocationException
    {
        //throw new BadLocationException("The method getChars() not valid in BytesContent.", 0);
        if (where + len > count)
        {
            throw new BadLocationException("Invalid location", count);
        }
        chars.array = data;
        chars.offset = where;
        chars.count = len;
    }

    /**
     * Creates a position within the content that will
     * track change as the content is mutated.
     *
     * @param offset the offset to create a position for >= 0
     * @return the position
     * @exception BadLocationException if the specified position is invalid
     */
    public Position createPosition(int offset) throws BadLocationException
    {
        // some small documents won't have any sticky positions
        // at all, so the buffer is created lazily.
        if (marks == null)
        {
            marks = new Vector();
        }
        return new StickyPosition(offset);
    }

    // --- local methods ---------------------------------------

    /**
     * Replaces some of the characters in the array
     * @param offset  offset into the array to start the replace
     * @param length  number of characters to remove
     * @param replArray replacement array
     * @param replOffset offset into the replacement array
     * @param replLength number of character to use from the
     *   replacement array.
     */
    void replace(int offset, int length,
         char[] replArray, int replOffset, int replLength)
    {
        int delta = replLength - length;
        int src = offset + length;
        int nmove = count - src;
        int dest = src + delta;
        if ((count + delta) >= data.length)
        {
            // need to grow the array
            int newLength = Math.max(2*data.length, count + delta);
            char[] newData = new char[newLength];
            System.arraycopy(data, 0, newData, 0, offset);
            System.arraycopy(replArray, replOffset, newData, offset, replLength);
            System.arraycopy(data, src, newData, dest, nmove);
            data = newData;
        }
        else
        {
            // patch the existing array
            System.arraycopy(data, src, data, dest, nmove);
            System.arraycopy(replArray, replOffset, data, offset, replLength);
        }
        count = count + delta;
    }

    void resize(int ncount)
    {
        char[] ndata = new char[ncount];
        System.arraycopy(data, 0, ndata, 0, Math.min(ncount, count));
        data = ndata;
    }

    synchronized void updateMarksForInsert(int offset, int length)
    {
        if (offset == 0)
        {
            // zero is a special case where we update only
            // marks after it.
            offset = 1;
        }
        int n = marks.size();
        for (int i = 0; i < n; i++)
        {
            PosRec mark = (PosRec) marks.elementAt(i);
            if (mark.unused)
            {
                // this record is no longer used, get rid of it
                marks.removeElementAt(i);
                i -= 1;
                n -= 1;
            }
            else if (mark.offset >= offset)
            {
                mark.offset += length;
            }
        }
    }

    synchronized void updateMarksForRemove(int offset, int length)
    {
        int n = marks.size();
        for (int i = 0; i < n; i++)
        {
            PosRec mark = (PosRec) marks.elementAt(i);
            if (mark.unused)
            {
                // this record is no longer used, get rid of it
                marks.removeElementAt(i);
                i -= 1;
                n -= 1;
            }
            else if (mark.offset >= (offset + length))
            {
                mark.offset -= length;
            }
            else if (mark.offset >= offset)
            {
                mark.offset = offset;
            }
        }
    }

    /**
     * Returns a Vector containing instances of UndoPosRef for the
     * Positions in the range
     * <code>offset</code> to <code>offset</code> + <code>length</code>.
     * If <code>v</code> is not null the matching Positions are placed in
     * there. The vector with the resulting Positions are returned.
     * <p>
     * This is meant for internal usage, and is generally not of interest
     * to subclasses.
     *
     * @param v the Vector to use, with a new one created on null
     * @param offset the starting offset >= 0
     * @param length the length >= 0
     * @return the set of instances
     */
    protected Vector getPositionsInRange(Vector v, int offset,
                              int length)
    {
        int n = marks.size();
        int end = offset + length;
        Vector placeIn = (v == null) ? new Vector() : v;
        for (int i = 0; i < n; i++)
        {
            PosRec mark = (PosRec) marks.elementAt(i);
            if (mark.unused)
            {
                // this record is no longer used, get rid of it
                marks.removeElementAt(i);
                i -= 1;
                n -= 1;
            }
            else if(mark.offset >= offset && mark.offset <= end)
            {
                placeIn.addElement(new UndoPosRef(mark));
            }
        }
        return placeIn;
    }

    /**
     * Resets the location for all the UndoPosRef instances
     * in <code>positions</code>.
     * <p>
     * This is meant for internal usage, and is generally not of interest
     * to subclasses.
     *
     * @param positions the positions of the instances
     */
    protected void updateUndoPositions(Vector positions) {
    for(int counter = positions.size() - 1; counter >= 0; counter--)
    {
        UndoPosRef ref = (UndoPosRef)positions.elementAt(counter);
        // Check if the Position is still valid.
        if(ref.rec.unused)
        {
            positions.removeElementAt(counter);
        }
        else
        ref.resetLocation();
        }
    }

    //---------------------------------------------------------------------
    /**
       Gets data content which has been stored in bytes.
       @return byte array
    */
    //---------------------------------------------------------------------
    public byte[] getTextBytes(int where, int len) throws BadLocationException
    {
        if (where + len > count)
        {
            throw new BadLocationException("Invalid location", count);
        }
        if (count > 1)
        {
            bytes = new byte[len];
            for(int i = where; i < len; i++)
            {
                bytes[i] = (byte)data[i];
            }
        }
        else
        {
            bytes = null;
        }

        return bytes;
    }

    //---------------------------------------------------------------------
    /**
       Gets data content which has been stored in bytes.
       @return byte array
    */
    //---------------------------------------------------------------------
    public void clearTextBytes()
    {
        for (int i = 0; i < data.length; i++)
        {
            data[i] = ' ';
        }
        data[0] = '\n';
        count = 1;
        if (bytes != null)
        {
            for (int i = 0; i < bytes.length; i++)
            {
                bytes[i] = ' ';
            }
        }
    }

    /**
     * holds the data for a mark... separately from
     * the real mark so that the real mark can be
     * collected if there are no more references to
     * it.... the update table holds only a reference
     * to this grungy thing.
     */
    final class PosRec
    {
        int offset;
        boolean unused;
        PosRec(int offset)
        {
            this.offset = offset;
        }
    }

    /**
     * This really wants to be a weak reference but
     * in 1.1 we don't have a 100% pure solution for
     * this... so this class trys to hack a solution
     * to causing the marks to be collected.
     */
    final class StickyPosition implements Position
    {
        PosRec rec;

        StickyPosition(int offset)
        {
            rec = new PosRec(offset);
            marks.addElement(rec);
        }

        public int getOffset()
        {
            return rec.offset;
        }

        protected void finalize() throws Throwable
        {
            // schedule the record to be removed later
            // on another thread.
            rec.unused = true;
        }

        public String toString()
        {
            return Integer.toString(getOffset());
        }

    }

    /**
     * Used to hold a reference to a Position that is being reset as the
     * result of removing from the content.
     */
    final class UndoPosRef
    {
        /** Location to reset to when resetLocatino is invoked. */
        protected int undoLocation;
        /** Position to reset offset. */
        protected PosRec rec;
        UndoPosRef(PosRec rec)
        {
            this.rec = rec;
            this.undoLocation = rec.offset;
        }

        /**
         * Resets the location of the Position to the offset when the
         * receiver was instantiated.
         */
        protected void resetLocation()
        {
            rec.offset = undoLocation;
        }
    }

    /**
     * UnoableEdit created for inserts.
     */
    class InsertUndo extends AbstractUndoableEdit
    {
        // Where the string goes.
        protected int offset;
        // Length of the string.
        protected int length;
        // The string that was inserted. To cut down on space needed this
        // will only be valid after an undo.
        //protected String string;
        // An array of instances of UndoPosRef for the Positions in the
        // range that was removed, valid after undo.
        protected Vector posRefs;

        protected InsertUndo(int offset, int length)
        {
            super();
            this.offset = offset;
            this.length = length;
        }

        public void undo() throws CannotUndoException
        {
            super.undo();
            try
            {
                synchronized(BytesContent.this)
                {
                    // Get the Positions in the range being removed.
                    if(marks != null)
                    posRefs = getPositionsInRange(null, offset, length);

                    // Removed; the getString method is not valid;
                    //string = getString(offset, length);

                    remove(offset, length);
                }
            }
            catch (BadLocationException bl)
            {
              throw new CannotUndoException();
            }
        }

        public void redo() throws CannotRedoException
        {
            super.redo();
            try
            {
                synchronized(BytesContent.this)
                {
                    // Removed; the getString is not valid; it does appear that it is
                    // possible to perfrom a redo from the POS ui anyway.
                    // insertString(offset, string);
                    // string = null;

                    insertString(offset, "");

                    // Update the Positions that were in the range removed.
                    if(posRefs != null)
                    {
                        updateUndoPositions(posRefs);
                        posRefs = null;
                    }
                }
            }
            catch (BadLocationException bl)
            {
                throw new CannotRedoException();
            }
        }

    }


    /**
     * UndoableEdit created for removes.
     */
    class RemoveUndo extends AbstractUndoableEdit
    {
        // Where the string goes.
        protected int offset;
        // Length of the string.
        protected int length;
        // The string that was inserted. This will be null after an undo.
        protected String string;
        // An array of instances of UndoPosRef for the Positions in the
        // range that was removed, valid before undo.
        protected Vector posRefs;

        protected RemoveUndo(int offset, String string)
        {
            super();
            this.offset = offset;
            this.string = string;
            this.length = string.length();
            if(marks != null)
            posRefs = getPositionsInRange(null, offset, length);
        }

        public void undo() throws CannotUndoException
        {
            super.undo();
            try
            {
                synchronized(BytesContent.this)
                {
                    insertString(offset, string);
                    // Update the Positions that were in the range removed.
                    if(posRefs != null)
                    {
                        updateUndoPositions(posRefs);
                        posRefs = null;
                    }
                    string = null;
                }
            }
            catch (BadLocationException bl)
            {
                throw new CannotUndoException();
            }
        }

        public void redo() throws CannotRedoException
        {
            super.redo();
            try
            {
                synchronized(BytesContent.this)
                {
                    string = getString(offset, length);
                    // Get the Positions in the range being removed.
                    if(marks != null)
                        posRefs = getPositionsInRange(null, offset, length);
                    remove(offset, length);
                }
            }
            catch (BadLocationException bl)
            {
                throw new CannotRedoException();
            }
        }
    }
}
