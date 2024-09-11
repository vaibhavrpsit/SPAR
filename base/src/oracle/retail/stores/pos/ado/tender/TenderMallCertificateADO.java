/* ===========================================================================
* Copyright (c) 1998, 2014, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ado/tender/TenderMallCertificateADO.java /main/16 2014/04/14 15:54:36 abhinavs Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    abhinavs  03/19/14 - Fix to validate Mall certificate if it has already
 *                         been tendered out
 *    abondala  09/04/13 - initialize collections
 *    vtemker   03/30/12 - Refactoring of getNumber() method of TenderCheck
 *                         class - returns sensitive data in byte[] instead of
 *                         String
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    vchengeg  11/07/08 - To fix BAT test failure
 *
 * ===========================================================================
 * $Log:
 *    4    360Commerce 1.3         12/13/2005 4:42:33 PM  Barry A. Pape
 *         Base-lining of 7.1_LA
 *    3    360Commerce 1.2         3/31/2005 4:30:25 PM   Robert Pearse   
 *    2    360Commerce 1.1         3/10/2005 10:26:01 AM  Robert Pearse   
 *    1    360Commerce 1.0         2/11/2005 12:14:55 PM  Robert Pearse   
 *
 *   Revision 1.3  2004/04/22 21:03:53  epd
 *   @scr 4513 Changed all toFormattedString() calls to getStringValue() calls
 *
 *   Revision 1.2  2004/02/12 16:47:55  mcs
 *   Forcing head revision
 *
 *   Revision 1.1.1.1  2004/02/11 01:04:11  cschellenger
 *   updating to pvcs 360store-current
 *
 *
 * 
 *    Rev 1.3   Feb 05 2004 13:46:38   rhafernik
 * log4j changes
 * 
 *    Rev 1.2   Jan 06 2004 11:23:04   epd
 * refactorings to remove unfriendly references to TenderHelper and DomainGateway
 * 
 *    Rev 1.1   Dec 29 2003 14:07:44   bwf
 * Updated.
 * 
 *    Rev 1.0   Dec 11 2003 13:08:02   bwf
 * Initial revision.
 * Resolution for 3538: Mall Certificate Tender
 * ===========================================================================
 */
package oracle.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Map;

import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.utility.EYSDomainIfc;
import oracle.retail.stores.domain.utility.GiftCertificateDocumentIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.factory.ADOFactoryComplex;
import oracle.retail.stores.pos.ado.factory.TenderUtilityFactoryIfc;
import oracle.retail.stores.pos.ado.journal.JournalConstants;

//--------------------------------------------------------------------------
/**
 This class carries with it the mall certificate rdo.
 $Revision: /main/16 $
 **/
//--------------------------------------------------------------------------
public class TenderMallCertificateADO extends AbstractTenderADO
{
	/**
     * 
     */
    private static final long serialVersionUID = -288106850462539568L;

    /** revision number **/
	public static final String revisionNumber = "$Revision: /main/16 $";
	
	/**
	 Mall gift certificate types
	 **/
	public final static String MALL_GC = TenderGiftCertificateIfc.MALL_GC;
	public final static String MALL_GC_AS_CHECK = TenderGiftCertificateIfc.MALL_GC_AS_CHECK;
	public final static String MALL_GC_AS_PO = TenderGiftCertificateIfc.MALL_GC_AS_PO;
	
	//----------------------------------------------------------------------
	/**
	 This is a no arg constructor.        
	 **/
	//----------------------------------------------------------------------
	protected TenderMallCertificateADO() {}
	
	//----------------------------------------------------------------------
	/**
	 This method creates the tenderRDO.        
	 @see oracle.retail.stores.pos.ado.tender.AbstractTenderADO#initializeTenderRDO()
	 **/
	//----------------------------------------------------------------------
	protected void initializeTenderRDO()
	{
		tenderRDO = DomainGateway.getFactory().getTenderGiftCertificateInstance();
		((TenderGiftCertificateIfc)tenderRDO).
		setTypeCode(TenderLineItemIfc.TENDER_TYPE_MALL_GIFT_CERTIFICATE);
	}
	
	//----------------------------------------------------------------------
	/**
	 This method gets the tender type.
	 @return
	 @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getTenderType()
	 **/
	//----------------------------------------------------------------------
	public TenderTypeEnum getTenderType()
	{
		return TenderTypeEnum.MALL_CERT;
	}
	
	//----------------------------------------------------------------------
	/**
	 This method validates the mall gift certificate.
	 @throws TenderException
	 @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#validate()
	 **/
	//----------------------------------------------------------------------
	public void validate() throws TenderException
	{
	    if(logger.isInfoEnabled())
        {
            logger.info("Validating mall certiciate information...");
        }

        try
        {
            //get the mall certificate validator
            CertificateValidatorIfc certificateValidator = createCertificateValidator();
            boolean isTransReentry = this.isTransactionReentryMode();
            certificateValidator.setTransactionReentryMode(isTransReentry);
            certificateValidator.lookupTenderedMallCertificate();
        }
        catch (TenderException te)
        {
            if (te.getErrorCode() == TenderErrorCodeEnum.MALL_CERTIFICATE_NUMBER_ALREADY_TENDERED)
            {
                // update tenderRDO after it was changed through RMI
                tenderRDO = (TenderLineItemIfc)te.getChangedObject();

                // rethrow the exception with new changed object.
                throw new TenderException("Certificate Tendered", TenderErrorCodeEnum.MALL_CERTIFICATE_NUMBER_ALREADY_TENDERED, te);

            }
            else if (te.getErrorCode() == TenderErrorCodeEnum.VALIDATION_OFFLINE)
            {
                logger.error("offline mode; adding gift certificate of the remaining tender amount", te);
            }
            else
            {
                throw te;
            }
        }
        catch (ADOException adoe)
        {
            logger.error("Unable to create ceritifcate validator: " + adoe);
        }

	}
	
	private CertificateValidatorIfc createCertificateValidator() throws ADOException
    {
        // Create the certificate validator.
        TenderUtilityFactoryIfc factory = (TenderUtilityFactoryIfc)ADOFactoryComplex.getFactory("factory.tenderutility");
        CertificateValidatorIfc certificateValidator = factory.createCertificateValidator((TenderCertificateIfc) tenderRDO);
        return certificateValidator;
    }

    //----------------------------------------------------------------------
	/**
	 This method gets the tender attributes.
	 @return
	 @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#getTenderAttributes()
	 **/
	//----------------------------------------------------------------------
	public HashMap getTenderAttributes()
	{
		HashMap map = new HashMap(4);
		map.put(TenderConstants.TENDER_TYPE, getTenderType());
		map.put(TenderConstants.AMOUNT, 
				getAmount().getStringValue());
		map.put(TenderConstants.NUMBER, new String(((TenderGiftCertificateIfc)tenderRDO).getNumber()));
		map.put(TenderConstants.CERTIFICATE_TYPE, ((TenderGiftCertificateIfc)tenderRDO).getCertificateType());
		return map;
	}
	
	//----------------------------------------------------------------------
	/**
	 This method sets the tender attributes.
	 @param tenderAttributes
	 @throws TenderException
	 @see oracle.retail.stores.pos.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
	 **/
	//----------------------------------------------------------------------
	public void setTenderAttributes(HashMap tenderAttributes) throws TenderException
	{
		((TenderGiftCertificateIfc)tenderRDO).
		setAmountTender(parseAmount((String)tenderAttributes.get(TenderConstants.AMOUNT)));
		((TenderGiftCertificateIfc)tenderRDO).
		setGiftCertificateNumber((String)tenderAttributes.get(TenderConstants.NUMBER));
		if (tenderAttributes.get(TenderConstants.CERTIFICATE_TYPE) != null)
		{
			((TenderGiftCertificateIfc)tenderRDO).setCertificateType(
					(String)tenderAttributes.get(TenderConstants.CERTIFICATE_TYPE));
		}
        if (tenderAttributes.get(TenderConstants.TRAINING_MODE) != null)
        {
            ((TenderCertificateIfc)tenderRDO).setTrainingMode(((Boolean)tenderAttributes.get(TenderConstants.TRAINING_MODE)).booleanValue());
        }
        if (tenderAttributes.get(TenderConstants.CERTIFICATE_DOCUMENT) != null)
        {
            ((TenderGiftCertificateIfc)tenderRDO).setDocument(((GiftCertificateDocumentIfc)tenderAttributes.get(TenderConstants.CERTIFICATE_DOCUMENT)));
        }
	}
	
	/**
	 * Indicates Mall Certificate is a NOT type of PAT Cash
	 * @return false
	 */
	public boolean isPATCash()
	{
		return false;
	}
	
	//----------------------------------------------------------------------
	/**
	 This gets the journal memento.
	 @return
	 @see oracle.retail.stores.ado.journal.JournalableADOIfc#getJournalMemento()
	 **/
	//----------------------------------------------------------------------
	public Map getJournalMemento()
	{
		Map memento = getTenderAttributes();
		// add tender descriptor
		memento.put(JournalConstants.DESCRIPTOR, getTenderType().toString());
		return memento;
	}
	
	//----------------------------------------------------------------------
	/**
	 This method converts from the legacy object to the ado.
	 @param rdo
	 @see oracle.retail.stores.pos.ado.ADOIfc#fromLegacy(oracle.retail.stores.domain.utility.EYSDomainIfc)
	 **/
	//----------------------------------------------------------------------
	public void fromLegacy(EYSDomainIfc rdo)
	{
		assert(rdo instanceof TenderGiftCertificateIfc);
		
		tenderRDO = (TenderGiftCertificateIfc)rdo;
	}
	
	//----------------------------------------------------------------------
	/**
	 This method converts to the legacy domain object.
	 @return
	 @see oracle.retail.stores.pos.ado.ADOIfc#toLegacy()
	 **/
	//----------------------------------------------------------------------
	public EYSDomainIfc toLegacy()
	{
		return tenderRDO;
	}
	
	//----------------------------------------------------------------------
	/**
	 This method converts to a specific legacy domain object.
	 @param type
	 @return
	 @see oracle.retail.stores.pos.ado.ADOIfc#toLegacy(java.lang.Class)
	 **/
	//----------------------------------------------------------------------
	public EYSDomainIfc toLegacy(Class type)
	{
		return toLegacy();
	}    
}
