/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/modifytransaction/tax/ValidateCertificateIDSite.java /main/13 2011/12/05 12:16:20 cgreene Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   12/05/11 - updated from deprecated packages and used more
 *                         bigdecimal constants
 *    cgreene   09/02/11 - refactored method names around enciphered objects
 *    rrkohli   07/22/11 - nullpointer exception fix
 *    cgreene   05/26/10 - convert to oracle packaging
 *    cgreene   04/26/10 - XbranchMerge cgreene_tech43 from
 *                         st_rgbustores_techissueseatel_generic_branch
 *    cgreene   04/02/10 - remove deprecated LocaleContantsIfc and currencies
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 *    3    360Commerce 1.2         3/31/2005 4:30:41 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:39 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:15:27 PM  Robert Pearse   
 *
 *   Revision 1.5  2004/07/15 19:29:04  lzhao
 *   @scr 6237: add debug info for reproducing, should remove later.
 *
 *   Revision 1.4  2004/02/24 16:21:31  cdb
 *   @scr 0 Remove Deprecation warnings. Cleaned code.
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
 *    Rev 1.1   Jan 13 2004 16:55:54   lzhao
 * add requireCertificateInfo parameter to show/skip TaxExempt screen.
 * Resolution for 3655: Feature Enhancement:  Tax Exempt Enhancement
 * 
 *    Rev 1.0   Aug 29 2003 16:03:02   CSchellenger
 * Initial revision.
 * 
 *    Rev 1.1   Mar 26 2003 15:47:26   RSachdeva
 * Removed use of CodeEntry getCode() method
 * Resolution for POS SCR-2103: Remove uses of deprecated items in POS.
 * 
 *    Rev 1.0   Apr 29 2002 15:15:14   msg
 * Initial revision.
 * 
 *    Rev 1.0   Mar 18 2002 11:40:06   msg
 * Initial revision.
 * 
 *    Rev 1.1   30 Jan 2002 15:06:48   pjf
 * Change requested by Circuit City
 * Resolution for POS SCR-964: Don't replace TransactionTax instance in ValidateCertificateIDSite
 *
 *    Rev 1.0   Sep 21 2001 11:31:50   msg
 * Initial revision.
 *
 *    Rev 1.1   Sep 17 2001 13:09:56   msg
 * header update
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.modifytransaction.tax;

import java.util.Locale;

import oracle.retail.stores.common.utility.LocalizedCodeIfc;
import oracle.retail.stores.domain.utility.LocaleConstantsIfc;
import oracle.retail.stores.keystoreencryption.EncryptionServiceException;
import oracle.retail.stores.pos.services.common.CommonLetterIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.transaction.TransactionTaxIfc;
import oracle.retail.stores.domain.utility.CodeConstantsIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.foundation.factory.FoundationObjectFactory;
import oracle.retail.stores.foundation.manager.device.EncipheredDataIfc;
import oracle.retail.stores.foundation.manager.gui.UIModelIfc;
import oracle.retail.stores.foundation.manager.ifc.UIManagerIfc;
import oracle.retail.stores.foundation.tour.application.Letter;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.common.utility.LocaleMap;
import oracle.retail.stores.pos.services.PosSiteActionAdapter;
import oracle.retail.stores.pos.ui.POSUIManagerIfc;
import oracle.retail.stores.pos.ui.beans.StringWithReasonBeanModel;

/**
 * This site is traversed when a TaxExempt number is entered at the UI and the
 * Next button is pressed. This aisle will check the TaxExempt number.
 * 
 * @version $Revision: /main/13 $
 */
@SuppressWarnings("serial")
public class ValidateCertificateIDSite extends PosSiteActionAdapter
{

    /** revision number of this class */
    public static final String revisionNumber = "$Revision: /main/13 $";

    public static final int MINIMUM_LENGTH = 1;
    public static final int MAXIMUM_LENGTH = 15;

    /**
     * Sets the TaxExempt number in the cargo.
     * 
     * @param bus Service Bus
     */
    @Override
    public void arrive(BusIfc bus)
    {
        // get the POS UI manager
        POSUIManagerIfc ui = (POSUIManagerIfc)bus.getManager(UIManagerIfc.TYPE);
        // get cargo handle
        ModifyTransactionTaxCargo cargo = (ModifyTransactionTaxCargo) bus.getCargo();
        
        UIModelIfc model = ui.getModel(POSUIManagerIfc.TRANSACTION_TAX_EXEMPT);
        EncipheredDataIfc customerTaxCertificate = FoundationObjectFactory.getFactory()
                .createEncipheredDataInstance();
        String reason = "";
        CodeListIfc rcl    = null;
        Locale locale = LocaleMap.getLocale(LocaleConstantsIfc.USER_INTERFACE);
        if ( model instanceof StringWithReasonBeanModel )
        {
            logger.error("exempt: model instanceof StringWithReasonBeanModel");
            StringWithReasonBeanModel beanModel = (StringWithReasonBeanModel)model;
            if ( cargo.requireCertificateInfo() )
            {
                logger.error("exempt: cargo.requireCertificateInfo()");
                // retrieve certificate ID
                // id = beanModel.getValue();
                
               
                  try
                  {
                    customerTaxCertificate.initialize(beanModel.getValue().getBytes());
                  }
                  catch (EncryptionServiceException e)
                  {
                    logger.error("Could not encrypt text" + e.getLocalizedMessage());
                  }
                
                rcl = cargo.getLocalizedExemptReasonCodes();
                // get bean model
                beanModel.setReasonCodes(rcl.getTextEntries(locale));
                // retrieve reason code
                reason = beanModel.getSelectedReasonKey();
                
                sendLogMessage(bus, reason, customerTaxCertificate.getMaskedNumber());
            }
        }
        else
        {
            logger.error("exempt: model not instanceof StringWithReasonBeanModel");
            sendLogMessage(bus);
        }
        
        TransactionTaxIfc tax = setTaxExempt(cargo, rcl, reason, customerTaxCertificate.getEncryptedNumber());
        tax.setTaxExemptCertificate(customerTaxCertificate);
        
        cargo.setTransactionTax(tax);
        cargo.setDirtyFlag(true);

        // mail a letter
        String letter = null;
        if (cargo.getCustomerLinked())
        {
           letter = CommonLetterIfc.SUCCESS;
        }
        else
        {
           letter = CommonLetterIfc.CONTINUE;
        }
        bus.mail(new Letter(letter), BusIfc.CURRENT);
    }

    /**
     * set exempt code and id to tax object
     * @param cargo
     * @param rcl
     * @param reason
     * @param id
     * @return
     */
    private TransactionTaxIfc setTaxExempt(
        ModifyTransactionTaxCargo cargo,
        CodeListIfc rcl,
        String reason,
        String id)
    {
        //if model required do not let it get throuh this page.
        // set values in transaction tax object
        logger.error("reason in setTaxExempt = " + reason);
        TransactionTaxIfc tax = cargo.getTransactionTax();
        if (tax == null)
        {
            logger.error("tax == null, create a new one");
            tax = DomainGateway.getFactory().getTransactionTaxInstance();
        }
        
        LocalizedCodeIfc localizedCode = DomainGateway.getFactory().getLocalizedCode();
        if ( cargo.requireCertificateInfo() )
        {
        	if (rcl != null)
        	{
	            CodeEntryIfc reasonEntry = rcl.findListEntryByCode(reason);
	            if (reasonEntry != null)
	            {
                    localizedCode.setCode(reason);
                    localizedCode.setText(reasonEntry.getLocalizedText());
	            }
        	}
            else
            {
                localizedCode.setCode(CodeConstantsIfc.CODE_UNDEFINED);
            }
        } 
        
        tax.setTaxExempt(id, localizedCode);
        return tax;
    }
    
    /**
     * send message to log file
     * @param bus
     * @param reason
     * @param id
     */
    private void sendLogMessage(BusIfc bus, String reason, String id)
    {
        // log results
        String message =
                new String("***** ValidateCertificateIDSite arrive [" +
                           id +
                           "] reason [" +
                           reason +
                           "].");
            if (logger.isInfoEnabled()) logger.info( "" + message + "");
    }

    /**
     * send message to log file
     * @param bus
     */
    private void sendLogMessage(BusIfc bus)
    {
        // log results
        String message =
                new String("***** ValidateCertificateIDSite arrived. The certificate info does not required.");
        if (logger.isInfoEnabled()) logger.info( "" + message + "");
    }
}
