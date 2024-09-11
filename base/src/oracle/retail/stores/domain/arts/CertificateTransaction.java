/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/CertificateTransaction.java /main/14 2012/03/27 10:57:12 jswan Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    5    360Commerce 1.4         7/25/2007 4:45:17 PM   Alan N. Sinton  CR
 *         27675 Check if Gift Certificate number was already issued.
 *    4    360Commerce 1.3         7/24/2006 8:27:40 PM   Keith L. Lesikar
 *         Merge effort.
 *    3    360Commerce 1.2         3/31/2005 4:27:22 PM   Robert Pearse
 *    2    360Commerce 1.1         3/10/2005 10:20:02 AM  Robert Pearse
 *    1    360Commerce 1.0         2/11/2005 12:09:51 PM  Robert Pearse
 *
 *   Revision 1.13  2004/07/26 19:48:09  jriggins
 *   @scr 6470 Added logic to support dates in transaction IDs and exception handling with regard to checking for post voided gift certificates
 *
 *   Revision 1.12  2004/07/17 21:08:00  jriggins
 *   @scr 6026 Added logic for checking to see if the transaction for an issued gift certificate has been post voided
 *
 *   Revision 1.11  2004/06/17 16:26:14  blj
 *   @scr 5678 - code cleanup
 *
 *   Revision 1.10  2004/04/26 22:17:25  crain
 *   @scr 4553 Redeem Gift Certificate
 *
 *   Revision 1.9  2004/04/21 22:08:46  epd
 *   @scr 4513 Fixing database code for certificate validation
 *
 *   Revision 1.8  2004/04/21 15:01:22  blj
 *   @scr 3872 - fixed a problem with redeem certificate validation.
 *
 *   Revision 1.6  2004/04/09 16:55:45  cdb
 *   @scr 4302 Removed double semicolon warnings.
 *
 *   Revision 1.5  2004/02/17 17:57:36  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.4  2004/02/17 16:18:45  rhafernik
 *   @scr 0 log4j conversion
 *
 *   Revision 1.3  2004/02/12 17:13:13  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 23:25:22  bwf
 *   @scr 0 Organize imports.
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:26  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 *
 *    Rev 1.2   Dec 12 2003 11:24:30   blj
 * modified for storecredit and to implement the TenderCertiticateIfc
 *
 *    Rev 1.1   Dec 05 2003 15:54:50   sfl
 * Added Jdbc operation for store credit lookup
 * Resolution for 3421: Tender redesign
 *
 *    Rev 1.0   Nov 20 2003 14:58:48   crain
 * Initial revision.
 * Resolution for 3421: Tender redesign
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.AbstractTenderDocumentIfc;
import oracle.retail.stores.domain.utility.GiftCertificateDocumentIfc;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.foundation.manager.data.DataAction;
import oracle.retail.stores.foundation.manager.data.DataException;
import oracle.retail.stores.foundation.manager.data.DataTransaction;
import oracle.retail.stores.foundation.manager.ifc.data.DataActionIfc;

import org.apache.log4j.Logger;

//------------------------------------------------------------------------------
/**
    Implements the certificate lookup operations
**/
//------------------------------------------------------------------------------
public class CertificateTransaction extends DataTransaction
{
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 9147609850138330541L;

    /**
        The logger to which log messages will be sent.
    **/
    private static Logger logger = Logger.getLogger(oracle.retail.stores.domain.arts.CertificateTransaction.class);

    /**
       revision number of this class
    **/
    public static String revisionNumber = "$Revision: /main/14 $";

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
    **/
    //---------------------------------------------------------------------
    public CertificateTransaction()
    {
        super("CertificateTransaction");
    }

    //---------------------------------------------------------------------
    /**
       Class constructor. <P>
       @param name transaction name
    **/
    //---------------------------------------------------------------------
    public CertificateTransaction(String name)
    {
        super(name);
    }

    //---------------------------------------------------------------------
    /**
       Lookup a gift certificate in the database.
       @param  certificate TenderCertificateIfc certificate to look for
       @exception  DataException when an error occurs
       @return TenderCertificateIfc
    **/
    //---------------------------------------------------------------------
    public TenderCertificateIfc readCertificate(TenderCertificateIfc certificate) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "CertificateTransaction.readCertificate");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();

        if (certificate instanceof TenderStoreCreditIfc)
        {
            da.setDataOperationName("LookupStoreCredit");
        }
        else if (certificate instanceof TenderGiftCertificateIfc)
        {
            da.setDataOperationName("LookupCertificate");
        }
        else
        {
            if (logger.isDebugEnabled()) logger.debug(
                    "Unknown Certificate: " + certificate);
        }

        da.setDataObject(certificate);
        dataActions[0] = da;
        setDataActions(dataActions);
        AbstractTenderDocumentIfc document = 
            (AbstractTenderDocumentIfc)getDataManager().execute(this);

        if (certificate instanceof TenderStoreCreditIfc)
        {
            ((TenderStoreCreditIfc) certificate).setStoreCredit((StoreCreditIfc)document);
        }
        else if (certificate instanceof TenderGiftCertificateIfc)
        {
            ((TenderGiftCertificateIfc) certificate).
                setDocument((GiftCertificateDocumentIfc)document);
        }

        if (logger.isDebugEnabled()) logger.debug(
                    "CertificateTransaction.readCertificate");

        return certificate;
    }
    //---------------------------------------------------------------------
    /**
     Check if certificate has been tender redeemed.
     @param  certificate TenderCertificateIfc certificate to look for
     @exception  DataException when an error occurs
     @return TenderCertificateIfc
     @deprecated in 14.0; all callers removed
     **/
    //---------------------------------------------------------------------
    public TenderCertificateIfc checkIfRedeemed(TenderCertificateIfc certificate) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
        "CertificateTransaction.checkIfRedeemed");

        TenderCertificateIfc tender = readCertificate(certificate);

        if (logger.isDebugEnabled()) logger.debug(
        "CertificateTransaction.checkIfRedeemed");

        return tender;
    }

    /**
     * Checks the post voided transaction table to see if the transaction in which
     * the provided certificate was issued has been post voided.
     *
     *  @param certificate the certificate to check
     *  @param inTrainingMode flag for the training mode setting
     *  @deprecated in 14.0; all callers removed
     */
    public TenderCertificateIfc checkIfPostVoided(TenderCertificateIfc certificate,
                                                  boolean inTrainingMode) throws DataException
    {
        return readCertificate(certificate);
    }

    /**
     * Reads the database for the Gift Certificate by it's number
     *
     * @param certificate
     * @return
     * @throws DataException
     */
    public TenderGiftCertificateIfc readGiftCertificateIssued(TenderGiftCertificateIfc certificate) throws DataException
    {
        // set data action sand execute
        if (logger.isDebugEnabled()) logger.debug("CertificateTransaction.readGiftCertificateIssued");

        // set data actions and execute
        DataActionIfc[] dataActions = new DataActionIfc[1];
        DataAction da = new DataAction();
        
        da.setDataOperationName("ReadGiftCertificateIssued");
        da.setDataObject(certificate);
        dataActions[0] = da;
        setDataActions(dataActions);
        AbstractTenderDocumentIfc document = 
            (AbstractTenderDocumentIfc)getDataManager().execute(this);
        
        certificate.setDocument((GiftCertificateDocumentIfc)document);
        
        if (logger.isDebugEnabled()) logger.debug("CertificateTransaction.readGiftCertificateIssued");
        
        return certificate;
    }

    //---------------------------------------------------------------------
    /**
       Lookup a store credit in the database.
       @param  certificate TenderCertificateIfc storecredit to look for
       @exception  DataException if store credit already exists or when error occurs
       @return TenderCertificateIfc
       @deprecated in 14.0; all callers removed
    **/
    //---------------------------------------------------------------------
    public TenderCertificateIfc readStoreCredit(TenderCertificateIfc certificate) throws DataException
    {
        if (logger.isDebugEnabled()) logger.debug(
                     "CertificateTransaction.readStoreCredit");

        TenderCertificateIfc tender = readCertificate(certificate);

        if (logger.isDebugEnabled()) logger.debug(
                    "CertificateTransaction.readStoreCredit");

        return tender;
    }
}
