/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderUtilityFactory.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:05:42 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:27 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:05 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:58 PM  Robert Pearse   
 *
 *   Revision 1.2  2004/04/06 20:45:10  blj
 *   @scr 4301 - cleaned up javadoc's.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import oracle.retail.stores.pos.ado.factory.TenderUtilityFactoryIfc;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;

/**
 * This is a tender utility factory for creating tender
 * utility classes.
 */
public class TenderUtilityFactory implements TenderUtilityFactoryIfc
{
    /**
     * Creates a certificate validator instance.
     * @return certificateValidator
     */
    public CertificateValidatorIfc createCertificateValidator(TenderCertificateIfc tenderRDO)
    {
        CertificateValidatorIfc certificateValidator = new CertificateValidator(tenderRDO);
        return certificateValidator;
    }
}
