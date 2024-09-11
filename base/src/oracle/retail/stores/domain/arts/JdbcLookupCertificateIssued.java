/* ===========================================================================
* Copyright (c) 2007, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/modules/domain/src/oracle/retail/stores/domain/arts/JdbcLookupCertificateIssued.java /main/10 2012/04/02 10:35:16 vtemker Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    jswan     03/21/12 - Modified to support centralized gift certificate and
 *                         store credit.
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech75 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   03/30/10 - remove deprecated ARTSDatabaseIfcs and change
 *                         SQLException to DataException
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *  2    360Commerce 1.1         8/24/2007 6:05:47 PM   Alan N. Sinton  CR
 *       27256 Fix sql error.
 *  1    360Commerce 1.0         7/25/2007 4:46:17 PM   Alan N. Sinton  CR
 *       27675 Check to see that Gift Certificate number was not already
 *       issued.
 * $
 * ===========================================================================
 */
package oracle.retail.stores.domain.arts;

import oracle.retail.stores.common.sql.SQLSelectStatement;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.persistence.utility.ARTSDatabaseIfc;

/**
 * Jdbc operation to lookup issued gift certificates by it's certificate number.
 * $Revision: /main/10 $
 */
public class JdbcLookupCertificateIssued extends JdbcLookupCertificate implements ARTSDatabaseIfc
{
    private static final long serialVersionUID = 5340851449009782380L;

    /**
       Class constructor.
     */
    public JdbcLookupCertificateIssued()
    {
        setName("JdbcLookupCertificateIssued");
    }

    /**
     * Add the qualifiers to the sql object 
     * @param sql
     * @param giftCertificate
     */
    protected void getQualifiers(SQLSelectStatement sql, TenderGiftCertificateIfc giftCertificate)
    {
        // Qualifiers
        sql.addQualifier(FIELD_GIFT_CERTIFICATE_SERIAL_NUMBER
                + " = " + makeSafeString(new String(giftCertificate.getNumber())));
    }

}
