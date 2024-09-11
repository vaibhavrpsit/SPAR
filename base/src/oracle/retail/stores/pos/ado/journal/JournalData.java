/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/journal/JournalData.java /main/11 2013/09/05 10:36:15 abondala Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abondala  09/04/13 - initialize collections
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  3    360Commerce 1.2         3/31/2005 4:28:47 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:22:57 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:10 PM  Robert Pearse   
 *
 * Revision 1.4  2004/09/23 00:07:17  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 * 
 * Rev 1.0 Nov 04 2003 11:11:12 epd Initial revision.
 * 
 * Rev 1.0 Oct 17 2003 12:31:18 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.journal;

import java.util.HashMap;
import java.util.Map;

/**
 * Not all journal entries can be made from existing ADO objects which can be
 * made journalable. This class acts as a container for formatted String data
 * and implements the JournalableIfc as a convenient journal utility class.
 *  
 */
public class JournalData implements JournalableADOIfc
{
    // This id is used to tell
    // the compiler not to generate a
    // new serialVersionUID.
    //
    static final long serialVersionUID = 4520684448516513364L;

    /** The internal data map */
    Map data = new HashMap(0);

    /**
     * Method to use to store journal information. Use JournalConstants values
     * as 'key's.
     * 
     * @see JournalConstants
     * @param key
     *            The JournalConstant key value.
     * @param dataString
     *            The data to be journalled.
     */
    public void putJournalData(String key, String dataString)
    {
        data.put(key, dataString);
    }

    /*
     * (non-Javadoc)
     * 
     * @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
     */
    public Map getJournalMemento()
    {
        return data;
    }
}
