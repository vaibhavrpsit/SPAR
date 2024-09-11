/* ===========================================================================
* Copyright (c) 2008, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/TempShoppingPass.java /main/17 2011/02/27 20:37:37 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   02/18/11 - refactor printing for switching character sets
 *    acadar    06/10/10 - use default locale for currency display
 *    acadar    06/09/10 - XbranchMerge acadar_tech30 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   05/11/10 - convert Base64 from axis
 *    cgreene   05/11/10 - updating deprecated names
 *    cgreene   04/28/10 - updating deprecated names
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *    cgreene   11/13/08 - configure print beans into Spring context
 *    cgreene   10/22/08 - convert to printing tempshoppingpass blueprint
 *    cgreene   10/22/08 - convert to blueprint data object
 *    cgreene   09/19/08 - updated with changes per FindBugs findings
 *    cgreene   09/11/08 - update header
 *
 * ===========================================================================
  $Log:
   14   360Commerce 1.13        2/27/2008 4:23:23 PM   Deepti Sharma   changed
        text from 360Commerce to Oracle
   13   360Commerce 1.12        11/28/2007 9:58:50 AM  Tony Zgarba     FR2:
        Migrated existing sun.misc.Base64Encoder/Decoder usage to supported
        classes.
   12   360Commerce 1.11        11/27/2007 12:32:24 PM Alan N. Sinton  CR
        29661: Encrypting, masking and hashing account numbers for House
        Account.
   11   360Commerce 1.10        7/16/2007 11:02:24 AM  Ashok.Mondal    CR 27309
         :Correcting leading space format on temporary shopping pass receipt.
   10   360Commerce 1.9         7/9/2007 11:20:29 AM   Mathews Kochummen use
        locale format date
   9    360Commerce 1.8         6/12/2007 8:48:11 PM   Anda D. Cadar   SCR
        27207: Receipt changes -  proper alignment for amounts
   8    360Commerce 1.7         5/23/2007 3:22:22 PM   Mathews Kochummen format
         for locales
   7    360Commerce 1.6         4/30/2007 7:01:38 PM   Alan N. Sinton  CR 26485
         - Merge from v12.0_temp.
   6    360Commerce 1.5         2/6/2007 2:44:38 PM    Edward B. Thorne Merge
        from TempShoppingPass.java, Revision 1.2.1.0
   5    360Commerce 1.4         12/29/2006 12:18:34 PM Keith L. Lesikar
        modified.
   4    360Commerce 1.3         12/11/2006 12:17:47 PM Keith L. Lesikar
        Rebranding defect.
   3    360Commerce 1.2         3/31/2005 4:30:21 PM   Robert Pearse
   2    360Commerce 1.1         3/10/2005 10:25:52 AM  Robert Pearse
   1    360Commerce 1.0         2/11/2005 12:14:47 PM  Robert Pearse
  $
  Revision 1.8  2004/08/23 16:15:58  cdb
  @scr 4204 Removed tab characters

  Revision 1.7  2004/07/14 18:47:08  epd
  @scr 5955 Addressed issues with Utility class by making constructor protected and changing all usages to use factory method rather than direct instantiation

  Revision 1.6  2004/05/31 19:38:20  dfierling
  @scr 4558 - Print formatting changes

  Revision 1.5  2004/05/07 15:26:37  tfritz
  @scr 4558 Fixed the problem where it was not checking the
  TempShoppingPassBarcodeDisplay parameter for a temp shopping pass

  Revision 1.4  2004/03/02 17:16:14  nrao
  @scr 3581 Removed spaces from string before printing barcode.

  Revision 1.3  2004/02/12 16:48:44  mcs
  Forcing head revision

  Revision 1.2  2004/02/11 21:34:39  rhafernik
  @scr 0 Log4J conversion and code cleanup

  Revision 1.1.1.1  2004/02/11 01:04:13  cschellenger
  updating to pvcs 360store-current


 *
 *    Rev 1.12   Jan 15 2004 15:33:10   nrao
 * Added transaction information to the Temporary Shopping Pass.
 *
 *    Rev 1.11   Jan 12 2004 17:57:54   nrao
 * Rework for Temporary Shopping Pass.
 *
 *    Rev 1.10   Dec 26 2003 11:38:10   nrao
 * Fixed printing of default parameter value if state is not selected.
 *
 *    Rev 1.9   Dec 24 2003 15:19:58   nrao
 * Added condition to prevent printing of blank lines on the temporary shopping pass.
 *
 *    Rev 1.8   Dec 23 2003 10:26:04   nrao
 * Added functionality for Display Periodic Interest Rate parameter.
 *
 *    Rev 1.7   Nov 25 2003 16:07:24   nrao
 * added checkNull while printing barcode.
 *
 *    Rev 1.6   Nov 21 2003 15:10:16   nrao
 * Added class javadoc.
 *
 *    Rev 1.5   Nov 20 2003 17:49:26   nrao
 * Printing account number as a bar code instead of displaying the number.
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import oracle.retail.stores.domain.transaction.TransactionIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.InstantCreditIfc;
import oracle.retail.stores.foundation.manager.ifc.KeyStoreEncryptionManagerIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;

import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;

/**
 * Prints out the temporary shopping pass after instant credit enrollment or one
 * day shopping pass from House Account Options.
 * 
 * @version $Revision: /main/17 $
 */
public class TempShoppingPass extends PrintableDocumentParameterBean
{
    private static final long serialVersionUID = -4869461832396601739L;
    /** The logger to which log messages will be sent. */
    protected static final Logger logger = Logger.getLogger(TempShoppingPass.class);

    protected InstantCreditIfc card = null;
    protected TransactionIfc transaction = null;
    protected EYSDate expDate = null;
    protected boolean trainingMode = false;
    protected boolean oneDayTempPass = false;

    /**
     * Constructor
     */
    public TempShoppingPass()
    {
        this(null, null, null, false, false);
    }

    /**
     * Constructor
     */
    public TempShoppingPass(InstantCreditIfc card, TransactionIfc transaction,
            EYSDate expDate, boolean trainingMode, boolean oneDayTempPass)
    {
        this.card = card;
        this.transaction = transaction;
        this.expDate = expDate;
        this.trainingMode = trainingMode;
        this.oneDayTempPass = oneDayTempPass;
        setDocumentType(ReceiptTypeConstantsIfc.TEMPSHOPPINGPASS);
    }

    /**
     * @return the card
     */
    public InstantCreditIfc getCard()
    {
        return card;
    }

    /**
     * @return the transaction
     */
    public TransactionIfc getEnrollmentTransaction()
    {
        return transaction;
    }

    /**
     * @return the expDate
     */
    public EYSDate getExpDate()
    {
        return expDate;
    }

    /**
     * @return the trainingMode
     */
    public boolean isTrainingMode()
    {
        return trainingMode;
    }

    /**
     * @return the oneDayTempPass
     */
    public boolean isOneDayTempPass()
    {
        return oneDayTempPass;
    }

    /**
     * @param card the card to set
     */
    public void setInstantCredit(InstantCreditIfc card)
    {
        this.card = card;
    }

    /**
     * @param transaction
     */
    public void setEnrollmentTransaction(TransactionIfc transaction)
    {
        this.transaction = transaction;
    }

    /**
     * @param expDate the expDate to set
     */
    public void setExpDate(EYSDate expDate)
    {
        this.expDate = expDate;
    }

    /**
     * @param trainingMode the trainingMode to set
     */
    public void setTrainingMode(boolean trainingMode)
    {
        this.trainingMode = trainingMode;
    }

    /**
     * @param oneDayTempPass the oneDayTempPass to set
     */
    public void setOneDayTempPass(boolean oneDayTempPass)
    {
        this.oneDayTempPass = oneDayTempPass;
    }

    /**
     * @return the decrypted card number.
     */
    public String getCardNumber()
    {
        KeyStoreEncryptionManagerIfc encryptionManager = (KeyStoreEncryptionManagerIfc)Gateway.getDispatcher()
                .getManager(KeyStoreEncryptionManagerIfc.TYPE);
        String num = "..." + card.getEncipheredCardData().getLastFourAcctNumber();
        try
        {
            num = new String(encryptionManager.decrypt(Base64.decodeBase64(card.getAccountNumber().getBytes())));
        }
        catch (Exception e)
        {
            logger.warn("Could not decrypt account number", e);
        }
        return num;
    }

    /**
     * @return the decrypted card number formatted with spaces.
     */
    public String getFormattedCardNumber()
    {
        StringBuffer cardNum = new StringBuffer(getCardNumber());
        if (cardNum.length() > 12)
        {
            cardNum.insert(12, " ");
            cardNum.insert(8, " ");
            cardNum.insert(4, " ");
        }
        return cardNum.toString();
    }
}
