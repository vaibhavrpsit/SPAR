/* ===========================================================================
* Copyright (c) 1998, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/journal/JournalActionEnum.java /main/13 2013/10/29 14:06:50 rhaight Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    rhaight   10/29/13 - Code extensibility changes for JournalActionEnum and
 *                         TenderTypeEnum
 *    abondala  09/04/13 - initialize collections
 *    blarsen   07/12/11 - Added reversal enum type.
 *    npoola    11/29/10 - added the new constant AUTHORIZATION_DECLINE for
 *                         offline authorization scenario
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  6    360Commerce 1.5         9/20/2007 12:09:12 PM  Rohit Sachdeva  28813:
 *       Initial Bulk Migration for Java 5 Source/Binary Compatibility of All
 *       Products
 *  5    360Commerce 1.4         1/25/2006 4:11:28 PM   Brett J. Larsen merge
 *       7.1.1 changes (aka. 7.0.3 fixes) into 360Commerce view
 *  4    360Commerce 1.3         12/13/2005 4:42:31 PM  Barry A. Pape
 *       Base-lining of 7.1_LA
 *  3    360Commerce 1.2         3/31/2005 4:28:47 PM   Robert Pearse   
 *  2    360Commerce 1.1         3/10/2005 10:22:57 AM  Robert Pearse   
 *  1    360Commerce 1.0         2/11/2005 12:12:10 PM  Robert Pearse   
 *:
 *  4    .v710     1.2.2.0     9/21/2005 13:39:49     Brendan W. Farrell
 *       Initial Check in merge 67.
 *  3    360Commerce1.2         3/31/2005 15:28:47     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:22:57     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:12:10     Robert Pearse
 *
 *  4    .v700     1.2.3.0     10/31/2005 18:01:35    Deepanshu       CR 6102:
 *       Updated code to save the tax information in EJournal when a Returned
 *       transaction is post void transaction
 *  3    360Commerce1.2         3/31/2005 15:28:47     Robert Pearse
 *  2    360Commerce1.1         3/10/2005 10:22:57     Robert Pearse
 *  1    360Commerce1.0         2/11/2005 12:12:10     Robert Pearse
 *
 * Revision 1.7  2004/09/30 18:21:50  lzhao
 * @scr add orignal transaction type and id in journal message of void transaction
 *
 * Revision 1.6  2004/09/23 00:07:17  kmcbride
 * @scr 7211: Adding static serialVersionUIDs to all POS Serializable objects, minus the JComponents
 *
 * Revision 1.5  2004/05/20 22:54:58  cdb
 * @scr 4204 Removed tabs from code base again.
 *
 * Revision 1.4  2004/05/14 19:04:33  lzhao
 * @scr 4553: Redeem Journal
 *
 * Revision 1.3  2004/04/08 20:33:02  cdb
 * @scr 4206 Cleaned up class headers for logs and revisions.
 *
 *
 * Rev 1.0 Nov 04 2003 11:11:10 epd Initial revision.
 *
 * Rev 1.2 Oct 30 2003 20:32:56 epd added new action for authorization
 *
 * Rev 1.1 Oct 21 2003 17:43:06 epd added journalling of deleted tenders
 *
 * Rev 1.0 Oct 17 2003 12:31:18 epd Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.journal;

import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;

import oracle.retail.stores.common.context.BeanLocator;
import oracle.retail.stores.pos.ado.utility.TypesafeEnumIfc;

/**
 * New journal actions are added in the application.properties file using the 
 * ExtendedJournalAction key. The new values are placed in a comma delimited list. The
 * new JournalActionEnums are created during the static initialization of the JournalActionEnum class
 * 
 */
public class JournalActionEnum implements TypesafeEnumIfc
{

    /** Serial Version ID */
    static final long serialVersionUID = 9170372525865550781L;

    /** Log4J Logger */
    protected static Logger logger = Logger.getLogger(JournalActionEnum.class);
    /** The internal representation of an enumeration instance */
    private String enumer;

     /** The map containing the singleton enumeration instances */
    protected static final HashMap<String, JournalActionEnum> map;

    /** Spring loaded JournalHelper */
    protected static JournalHelperIfc jrnlHelper;

    /**
     * Initialization block to load the JournalActionHelper bean. The Journal Helper bean supports the configuration
     * of additional Journal Action Enumerations. These extended actions are specified in the ApplicationContext.xml
     * file for the bean "application_EJournalHelper". The extended actions are placed in the JournalActionEnum map. 
     * Creation of extended constants should use the JournalActionEnum.makeEnumFromString() method to obtain a reference
     * to the extended enumeration instances. The class declaring the extended action enumerations
     * does not extend the JournalActionEnum class.
     */
    static
    {
        try
        {
           jrnlHelper = (JournalHelperIfc)BeanLocator.getApplicationBean(JournalHelperIfc.JOURNAL_HELPER_BEAN);

           map = new HashMap<String, JournalActionEnum>();
        
        
        List<String> extEnumActions = jrnlHelper.getExtendedJournalActionEnums();

        if (logger.isDebugEnabled())
        {
            if (extEnumActions.isEmpty())
            {
                logger.debug("No extended Journal Enums to add");
            }
            else
            {
                logger.debug("Extended Journal Enums being added");
            }
        }

        for (String extEnum : extEnumActions)
        {
            new JournalActionEnum(extEnum);
        }
        }
        catch (Throwable eth)
        {
             logger.fatal("Failed to initialize the JournalActionEnum class", eth);
             throw eth;
        }
    }
    
    //================================================================================
    // Put base product Journal Action Enum defintions here
    //================================================================================
    
    public static final JournalActionEnum CREATE =
        new JournalActionEnum("Create");
    public static final JournalActionEnum ORIG_TRANS =
        new JournalActionEnum("OrigTrans");
    public static final JournalActionEnum VOID_REASON_CODE =
        new JournalActionEnum("VoidReasonCode");
    public static final JournalActionEnum GIFT_CARD_INQUIRY =
        new JournalActionEnum("GiftCardInquiry");
    public static final JournalActionEnum CANCEL =
        new JournalActionEnum("Cancel");
    public static final JournalActionEnum ADD = new JournalActionEnum("Add");
    public static final JournalActionEnum DEFAULT =
        new JournalActionEnum("Default");
    public static final JournalActionEnum TOTAL =
        new JournalActionEnum("Total");
    public static final JournalActionEnum TENDER_TOTAL =
        new JournalActionEnum("TenderTotal");
    public static final JournalActionEnum VOID_TOTAL =
        new JournalActionEnum("VoidTotal");
    public static final JournalActionEnum VOID = new JournalActionEnum("Void");
    public static final JournalActionEnum OVERRIDE =
        new JournalActionEnum("Override");
    public static final JournalActionEnum DELETE =
        new JournalActionEnum("Delete");
    public static final JournalActionEnum AUTHORIZATION =
        new JournalActionEnum("Authorization");
    public static final JournalActionEnum AUTHORIZATION_DECLINED =
        new JournalActionEnum("AuthorizationDeclined");
    public static final JournalActionEnum REDEEM =
        new JournalActionEnum("REDEEM");
    public static final JournalActionEnum ORIG_TOTAL =
        new JournalActionEnum("OrigTotal");
    public static final JournalActionEnum IRS_CUSTOMER =
        new JournalActionEnum("IRSCustomer");
    public static final JournalActionEnum REVERSAL =
        new JournalActionEnum("Reversal");
    
    //================================================================================
    // End Product Journal Action Enum declarations
    //================================================================================
    
    
    public static HashMap getJournalEnumerations()
    {
        return map;
    }
        
    
    private JournalActionEnum(String action)
    {
        this.enumer = action;
        map.put(enumer, this);
    }

      
    
    /** get internal representation */
    public String toString()
    {
        return enumer;
    }

    /** factory method. May return null */
    public static JournalActionEnum makeEnumFromString(String enumer)
    {
        return (JournalActionEnum) map.get(enumer);
    }

    /** fix deserialization */
    public Object readResolve() throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }

    /**
     * Forward to internal representation of enumer.
     */
    public int hashCode()
    {
        return enumer.hashCode();
    }
    
    
    
    
    
    // TODO  REMOVE AFTER VERIFICATION
    //-------
  /*  
    private JournalActionEnum(String action)
    {
        this.enumer = action;
        map.put(enumer, this);
    }
*/
    /** get internal representation */
/*  
  public String toString()
    {
        return enumer;
    }
*/
    /** factory method. May return null */
/* 
 public static JournalActionEnum makeEnumFromString(String enumer)
    {
        return (JournalActionEnum) map.get(enumer);
    }
*/
    /** fix deserialization */
/*  
  public Object readResolve() throws java.io.ObjectStreamException
    {
        return map.get(enumer);
    }
*/
    /**
     * Forward to internal representation of enumer.
     */
  /*
  public int hashCode()
    {
        return enumer.hashCode();
    }
 */   
}
