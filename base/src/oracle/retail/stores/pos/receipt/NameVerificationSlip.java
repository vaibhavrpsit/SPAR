/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/NameVerificationSlip.java /main/13 2011/02/27 20:37:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/18/11 - refactor printing for switching character sets
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   11/13/08 - configure print beans into Spring context
 *
 * ===========================================================================
 * $Log:
 * 4    360Commerce 1.3         4/30/2007 7:01:38 PM   Alan N. Sinton  CR 26485
 *       - Merge from v12.0_temp.
 * 3    360Commerce 1.2         3/31/2005 4:29:07 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:23:39 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:12:44 PM  Robert Pearse   
 *
 *Revision 1.4  2004/04/01 00:11:34  cdb
 *@scr 4206 Corrected some header foul ups caused by Eclipse auto formatting.
 *
 *Revision 1.3  2004/02/12 16:48:43  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:34:38  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 *updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Nov 21 2003 15:06:44   nrao
 * Added class Javadoc.
 * 
 *    Rev 1.2   Nov 20 2003 18:21:18   nrao
 * Added Log tag
 * Added Revision 1.3  2004/02/12 16:48:43  mcs
 * Added Forcing head revision
 * Added
 * Added Revision 1.2  2004/02/11 21:34:38  rhafernik
 * Added @scr 0 Log4J conversion and code cleanup
 * Added
 * Added Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
 * Added updating to pvcs 360store-current
 * Added
 * Added and changed Javadoc.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;


/**
 * Prints out the verification slip if Ingenico device is unavailable
 * 
 * @version $Revision: /main/13 $
 */
public class NameVerificationSlip extends PrintableDocumentParameterBean
{
    private static final long serialVersionUID = -35412663329084923L;

    /**
     * revision number supplied by source-code-control system
     */
    public static final String revisionNumber = "$Revision: /main/13 $";

    protected String first = null;
    protected String last = null;

    /**
     * Use only for serialization.
     */
    public NameVerificationSlip()
    {
        this(null, null);
    }

    /**
     * Constructor
     */
    public NameVerificationSlip(String first, String last)
    {
        this.first = first;
        this.last = last;
        setDocumentType(ReceiptTypeConstantsIfc.NAME_VERIFICATION);
    }

    /**
     * @return the first name
     */
    public String getFirstName()
    {
        return first;
    }

    /**
     * @return the last name
     */
    public String getLastName()
    {
        return last;
    }

    /**
     * @param first the first to set
     */
    public void setFirstName(String first)
    {
        this.first = first;
    }

    /**
     * @param last the last to set
     */
    public void setLastName(String last)
    {
        this.last = last;
    }
}
