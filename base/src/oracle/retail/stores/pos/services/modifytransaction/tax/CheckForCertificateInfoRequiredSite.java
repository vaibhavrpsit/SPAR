/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/CheckForCertificateInfoRequiredSite.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:32 mszekely Exp $
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
 *    3    360Commerce 1.2         3/31/2005 4:27:24 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:20:07 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:09:55 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/02/12 16:51:17  mcs
 *   Forcing head revision
 *
 *   Revision 1.2  2004/02/11 21:51:37  rhafernik
 *   @scr 0 Log4J conversion and code cleanup
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:18  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.1   Jan 20 2004 10:25:02   lzhao
 * use  CommonLetterIfc
 * Resolution for 3655: Feature Enhancement:  Tax Exempt Enhancement
 * 
 *    Rev 1.0   Jan 13 2004 16:58:00   lzhao
 * Initial revision.
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;

import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.foundation.manager.ifc.ParameterManagerIfc;
import oracle.retail.stores.foundation.manager.parameter.ParameterException;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;

//--------------------------------------------------------------------------
/**
    ##Check tax exempt parameter, RequireCertificateInfo.##
    <p>
    @version $Revision: /rgbustores_13.4x_generic_branch/1 $
**/
//--------------------------------------------------------------------------
public class CheckForCertificateInfoRequiredSite extends PosSiteActionAdapter
{

    /**
       revision number 
    **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /**
       The parameter indicates to show certification no and reason code dialog
    **/
    public static final String REQUIRE_CERTIFICATE_INFO = "RequireCertificateInfo";

    //----------------------------------------------------------------------
    /**
       This method drives to different sites based on parameters setting. <P>
       @param  bus     Service Bus
    **/
    //----------------------------------------------------------------------
    public void arrive(BusIfc bus)
    {
        
        // get the POS UI manager
        ParameterManagerIfc pm =
            (ParameterManagerIfc) bus.getManager(ParameterManagerIfc.TYPE);
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo)bus.getCargo();
        
        try
        {
            if (pm.getStringValue(REQUIRE_CERTIFICATE_INFO).equalsIgnoreCase("Y"))
            {
                cargo.setRequireCertificateInfo(true);
            }
            else
            {
                cargo.setRequireCertificateInfo(false);
            }
        }
        catch (ParameterException e)
        {
            if (logger.isInfoEnabled()) logger.info(
                "CheckTaxExemptPmtSite.arive(), cannot find NeedCertificateID parameter.");
        }
        
        if ( cargo.requireCertificateInfo() )
        {
            bus.mail(CommonLetterIfc.CONTINUE, BusIfc.CURRENT);
        }
        else
        {
            bus.mail(CommonLetterIfc.NEXT, BusIfc.CURRENT);
        }
    }
}


