/********************************************************************************
 *   
 *	Copyright (c) 2016 - 2017 MAX Hypermarket, Inc    All Rights Reserved.
 *	Rev 2.1 	May 24, 2024			Kamlesh Pant		Store Credit OTP:
 *  Rev 2.0     May 15,2023             Kumar Vaibhav       Chnages for CN lock
 *  Rev 1.0     Dec 20, 2016	        Ashish Yadav		Changes for StoreCredit FES
 *
 ********************************************************************************/
 
package max.retail.stores.pos.ado.tender;

import java.math.BigDecimal;
import java.util.HashMap;

import org.apache.log4j.Logger;

import max.retail.stores.domain.tender.MAXTenderStoreCredit;
import max.retail.stores.domain.tender.MAXTenderStoreCreditIfc;
import max.retail.stores.domain.utility.MAXStoreCredit;
import max.retail.stores.domain.utility.MAXStoreCreditIfc;
import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderAlternateCurrencyIfc;
import oracle.retail.stores.domain.tender.TenderCertificateIfc;
import oracle.retail.stores.domain.tender.TenderLineItemIfc;
import oracle.retail.stores.domain.tender.TenderStoreCredit;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.domain.utility.EntryMethod;
import oracle.retail.stores.domain.utility.StoreCreditIfc;
import oracle.retail.stores.pos.ado.ADOException;
import oracle.retail.stores.pos.ado.tender.CertificateTypeEnum;
import oracle.retail.stores.pos.ado.tender.CertificateValidatorIfc;
import oracle.retail.stores.pos.ado.tender.TenderADOIfc;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;
import oracle.retail.stores.pos.ado.tender.TenderStoreCreditADO;
import oracle.retail.stores.pos.ado.utility.UtilityIfc;
import oracle.retail.stores.pos.services.tender.tdo.TenderTDOConstants;

//-------------------------------------------------------------------------
/**
    @author Himanshu

**/
//-------------------------------------------------------------------------
public class MAXTenderStoreCreditADO extends TenderStoreCreditADO
{
    /**
	 * 
	 */
	private static final long serialVersionUID = 5613581084692144317L;
	/**
     * The logger to which log messages will be sent.
     */
    protected static transient Logger logger = Logger.getLogger(MAXTenderStoreCreditADO.class);

    /**
     * No-arg constructor It is intended that the tender factory instantiate this
     */
    protected MAXTenderStoreCreditADO()
    {
    }
    
    //-------------------------------------------------------------------------
    /**      
     * MFL Customizations
     * Initialize tender RDO
     * Added by Himanshu
    **/
    //-------------------------------------------------------------------------
    protected void initializeTenderRDO() {
		tenderRDO = DomainGateway.getFactory().getTenderStoreCreditInstance();
	}

    //-------------------------------------------------------------------------
    /**      
     * MFL Customizations
     * Get Tender attributes
     * Added by Himanshu
    **/
    //-------------------------------------------------------------------------
    public HashMap getTenderAttributes()
    {
    	HashMap map = super.getTenderAttributes();
        map.put(MAXTenderConstantsIfc.STORE_CREDIT_VALIDATED,(new Boolean(((MAXTenderStoreCreditIfc) tenderRDO).isStrCrdtValidated())));
        map.put(MAXTenderConstants.mobileNumber, ((MAXTenderStoreCreditIfc) tenderRDO).getSCmobileNumber());
		               
        return map;
    }

    //-------------------------------------------------------------------------
    /**      
     * MFL Customizations
     * Sets Tender attributes
     * Added by Himanshu
    **/
    //-------------------------------------------------------------------------
    public void setTenderAttributes(HashMap tenderAttributes) throws TenderException
    {
    	//super.setTenderAttributes(tenderAttributes);
    	BigDecimal conversionRate = null;
    	
    	CurrencyIfc amount = parseAmount((String) tenderAttributes.get(TenderConstants.AMOUNT));
        tenderRDO.setAmountTender(amount);
        ((TenderStoreCreditIfc) tenderRDO).setAmount(amount);
        ((TenderStoreCreditIfc) tenderRDO).setStoreCreditID((String) tenderAttributes.get(TenderConstants.NUMBER));
        ((TenderStoreCreditIfc) tenderRDO).setStoreNumber((String) tenderAttributes.get(TenderConstants.STORE_NUMBER));
        
        ((MAXTenderStoreCreditIfc) tenderRDO).setSCmobileNumber((String) tenderAttributes.get(MAXTenderConstants.mobileNumber));
        if (tenderAttributes.get(TenderConstants.ISSUE_DATE) != null)
        {
            ((TenderCertificateIfc) tenderRDO).setIssueDateAsString(
                (String) tenderAttributes.get(TenderConstants.ISSUE_DATE));
        }
        if (tenderAttributes.get(TenderConstants.CERTIFICATE_TYPE) != null)
        {
            ((TenderCertificateIfc) tenderRDO).setCertificateType(
                ((CertificateTypeEnum) tenderAttributes.get(TenderConstants.CERTIFICATE_TYPE)).toString());
        }
        if (tenderAttributes.get(TenderConstants.ENTRY_METHOD) != null)
        {
 // Changes starts for code merging(commenting below line)
           // ((TenderStoreCreditIfc) tenderRDO).setEntryMethod((String) tenderAttributes.get(TenderConstants.ENTRY_METHOD));
        	((MAXTenderStoreCreditIfc) tenderRDO).setEntryMethod(
        			(EntryMethod) tenderAttributes.get(TenderConstants.ENTRY_METHOD));
 // Changes ends for code merging
        }
        if (tenderAttributes.get(MAXTenderConstantsIfc.STORE_CREDIT_EXPIRED) != null)
        {
            EYSDate expDate = (EYSDate) tenderAttributes.get(MAXTenderConstantsIfc.STORE_CREDIT_EXPIRED);
            
            
            ((TenderStoreCreditIfc) tenderRDO).setExpirationDate(expDate);
        }
        ((TenderStoreCreditIfc) tenderRDO).setFirstName((String) tenderAttributes.get(TenderConstants.FIRST_NAME));
        ((TenderStoreCreditIfc) tenderRDO).setLastName((String) tenderAttributes.get(TenderConstants.LAST_NAME));
        // Changes starts ofr code merging(commenting below line asper MAX because below codes are replaced in 14)
       /* if (tenderAttributes.get(TenderConstants.ID_TYPE) instanceof String)
        {
// Changes starts for code merging(commenting below line)
           // ((TenderStoreCreditIfc) tenderRDO).setIdType((String) tenderAttributes.get(TenderConstants.ID_TYPE));
        	((MAXTenderStoreCreditIfc) tenderRDO).setIdType((String) tenderAttributes.get(TenderConstants.ID_TYPE));
// Changes ends for code merging 	
        }*/
        // Changes ends for code merging

        if (tenderAttributes.get(TenderConstants.STATE) != null)
        {
            ((TenderStoreCreditIfc) tenderRDO).setState(
                    (String) tenderAttributes.get(TenderConstants.STATE));
            StoreCreditIfc storeCredit = ((TenderStoreCreditIfc)tenderRDO).getStoreCredit();
            if (storeCredit != null)
            {
                if (((TenderStoreCreditIfc)tenderRDO).getState().equals(TenderCertificateIfc.REDEEMED))
                {
                    storeCredit.setStatus(StoreCreditIfc.REDEEMED);
                }
                else
                {
                    storeCredit.setStatus(StoreCreditIfc.ISSUED);
                }
            }
        }

        CurrencyIfc alternateAmount = (CurrencyIfc)tenderAttributes.get(TenderTDOConstants.ALTERNATE_CURRENCY);
        if (alternateAmount != null)
        {
            ((TenderAlternateCurrencyIfc)tenderRDO).setAlternateCurrencyTendered(alternateAmount);
        }

        conversionRate = (BigDecimal) tenderAttributes.get(TenderConstants.CONVERSION_RATE);
    	Boolean strCrdtFlag = null;
    	
    	if (tenderAttributes.get(MAXTenderConstantsIfc.STORE_CREDIT_VALIDATED)!= null)
    	{
    		strCrdtFlag = (Boolean)tenderAttributes.get(MAXTenderConstantsIfc.STORE_CREDIT_VALIDATED);
    	   ((MAXTenderStoreCreditIfc)tenderRDO).setStrCrdtValidated(strCrdtFlag.booleanValue());
    	}
    	
        
    }
   
    /**
     * A testTender must be validated before it can be added to the group. This
     * provides the basic implementation for that validation.
     */
    // Changes start for Rev 1.0 (Ashish : Store credit)
    protected void validate(TenderADOIfc testTender) throws TenderException
    {
        testTender.validate();
    }
    public void validate() throws TenderException
    {
        if(logger.isInfoEnabled())
        {
            logger.info("Validating store credit information...");
        }

        try
        {
            //CertificateValidator certificateValidator = new CertificateValidator((TenderCertificateIfc) tenderRDO);
            CertificateValidatorIfc certificateValidator = createCertificateValidator();
            certificateValidator.setTransactionReentryMode(this.isTransactionReentryMode());
            UtilityIfc utility = getUtility();
            
            // POS Generates the store credit number, don't perform the check digit validation.
            String preprintedStoreCredit = utility.getParameterValue("PrePrintedStoreCredit", "N");
            if (preprintedStoreCredit.equals("Y"))
            {
                certificateValidator.validateNumber();
            }

            // If the database validation is turned on, lookup the store credit. 
            String validateStoreCredit = utility.getParameterValue("ValidateStoreCredit", "N");
            if (validateStoreCredit.equals("Y") && !isIssueStoreCredit())
            {
                // Lookup the store credit.
                certificateValidator.lookupCertificate();
                
                // If any part of the lookup validation fails, the validator throws an exception and we don't 
                // get this far.
                
                // Cast the tenderRDO, 
                TenderStoreCreditIfc originalRDO = (TenderStoreCreditIfc)tenderRDO;
                
                // Get the RDO retrieved from the validator
                TenderCertificateIfc retrievedTenderRDO = certificateValidator.getTenderRDO();
                // Set status from the original RDO on the retrieved RDO                retrievedTenderRDO.getBaseDocument().setStatus(originalRDO.getBaseDocument().getStatus());
                // Use the retrieved RDO, which contains additional useful information.
                retrievedTenderRDO.getBaseDocument().setStatus(originalRDO.getBaseDocument().getStatus());
                tenderRDO = retrievedTenderRDO;
                // Update the map.
                getTenderAttributes();
                
              //Added by Vaibhav LS Credit note code merging start  Rev 2.0
                if (certificateValidator instanceof MAXCertificateValidatorIfc) {
					tenderRDO.setAmountTender(((MAXCertificateValidatorIfc) certificateValidator).getAmount());
					((TenderStoreCredit) tenderRDO).setAmount(((MAXCertificateValidatorIfc) certificateValidator).getAmount());
					((TenderStoreCredit) tenderRDO).setExpirationDate(((MAXCertificateValidatorIfc) certificateValidator).getExpirationDate());

					//Change for Rev 1.2:Starts
					//akanksha for bug 9576
					/*					if(((LSIPLCertificateValidatorIfc)certificateValidator).getStoreCreditStatus().equalsIgnoreCase("BLOCK")){
						((LSIPLTenderStoreCredit) tenderRDO).setStoreCreditStatus(((LSIPLCertificateValidatorIfc)certificateValidator).getStoreCreditStatus());
					}*/
					((MAXTenderStoreCredit) tenderRDO).setStoreCreditStatus(((MAXTenderStoreCredit) tenderRDO).getStoreCreditStatus());
					//Change for Rev 1.5:Starts
					((MAXTenderStoreCredit) tenderRDO).setStoreCreditLock(((MAXStoreCreditIfc)((MAXTenderStoreCredit) tenderRDO).getStoreCredit()).isStoreCreditLock());
					//Change for Rev 1.5:Ends
					//Change for Rev 1.2:Ends
					((MAXTenderStoreCredit) tenderRDO).setSCmobileNumber(((MAXStoreCreditIfc)((MAXTenderStoreCredit) tenderRDO).getStoreCredit()).getSCmobileNumber());
				}
                //end  Rev 2.0
            }
        }
        catch (TenderException te)
        {
            if (te.getErrorCode() == TenderErrorCodeEnum.CERTIFICATE_TENDERED)
            {
                // update tenderRDO after it was changed through RMI
                tenderRDO = (TenderLineItemIfc)te.getChangedObject();

                // rethrow the exception with the changed object
                throw new TenderException("Certificate Tendered", TenderErrorCodeEnum.CERTIFICATE_TENDERED, te);

            }
            else
            {
                throw te;
            }
        }
        catch (ADOException ae)
        {
            // Log the exception, more thought should be put into
            // this to handle this exceptiuon beyond just logging it.
            // Perhaps it makes sense to throw a different exception
            // and nest this one...
            //
            logger.error("An error occurred while validating store credit: " + ae);
//            ae.printStackTrace();
        }
    }
 // Changes ends for Rev 1.0 (Ashish : Store credit)
    
  //Rev 2.1 Starts 
  	String mobileNumber;

  	public void setMobileNumber(String mobileNumber) {

  		MAXTenderStoreCreditIfc tsc = (MAXTenderStoreCreditIfc) tenderRDO;

  		MAXStoreCreditIfc sc =  (MAXStoreCreditIfc) tsc.getStoreCredit();

  		tsc.setSCmobileNumber(mobileNumber);

  		if(sc!=null)
  		{
  			sc.setSCmobileNumber(mobileNumber);
  		}

  	}
  	public String getMobileNumber() {
  		return mobileNumber;
  	}
  	//Rev 2.1 Ends 

}
