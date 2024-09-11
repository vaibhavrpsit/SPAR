
package max.retail.stores.pos.ado.tender;

import java.util.HashMap;
import java.util.Properties;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.DomainGateway;
import oracle.retail.stores.domain.tender.TenderCouponIfc;
import oracle.retail.stores.foundation.tour.gate.Gateway;
import oracle.retail.stores.pos.ado.tender.CouponTypeEnum;
import oracle.retail.stores.pos.ado.tender.TenderConstants;
import oracle.retail.stores.pos.ado.tender.TenderCouponADO;
import oracle.retail.stores.pos.ado.tender.TenderErrorCodeEnum;
import oracle.retail.stores.pos.ado.tender.TenderException;

/**
 *  
 */
public class MAXTenderCouponADO extends TenderCouponADO
{
    /**
     *  our logger
     **/
    protected transient Logger logger = Logger.getLogger(MAXTenderCouponADO.class);

    /**
     * This class is instantiated by the tender factory
     */
    protected MAXTenderCouponADO() {}

   
    
    /* (non-Javadoc)
     * @see com._360commerce.ado.tender.TenderADOIfc#getTenderAttributes()
     */
    public HashMap getTenderAttributes()
    {
        HashMap map = new HashMap();
        map.put(TenderConstants.TENDER_TYPE, getTenderType());
        map.put(TenderConstants.AMOUNT, 
                getAmount().getStringValue());
        map.put(TenderConstants.COUPON_NUMBER, ((TenderCouponIfc)tenderRDO).getCouponNumber());
        map.put(TenderConstants.COUPON_TYPE, (CouponTypeEnum.COUPON_TYPE_MANUFACTURER.getCouponTypeDescriptor()));
        map.put(TenderConstants.ENTRY_METHOD, ((TenderCouponIfc)tenderRDO).getEntryMethod());
                
        return map;
    }
    
    /* (non-Javadoc)
     * @see com._360commerce.ado.tender.TenderADOIfc#setTenderAttributes(java.util.HashMap)
     */
  
    /**  
     * Attempts to calculate the coupon amount based on the coupon number
     * @throws TenderException
     */
    public void calculateCouponAmount()
    throws TenderException    
    {
        if(logger.isDebugEnabled())
        {
            logger.debug("Calculating coupon amount...");   
        }
        
        String couponNumber = (String)getTenderAttributes().get(TenderConstants.COUPON_NUMBER);
        
            // Determine the coupon's face value based on 2 character coupon code.
            CurrencyIfc amount = DomainGateway.getBaseCurrencyInstance((String)getTenderAttributes().get(TenderConstants.AMOUNT));
            if (amount.signum() == CurrencyIfc.ZERO)
            {
                // coupon amount is zero; manual input of amount is needed
                throw new TenderException("Manual input for coupon amount needed", TenderErrorCodeEnum.MANUAL_INPUT);
            }
            else
            {
                if(logger.isDebugEnabled())
                {
                    logger.debug("Setting tender amount to: " + amount);   
                }

                ((TenderCouponIfc)tenderRDO).setAmountTender(amount);
            }
        }
       
    
    protected String getCouponValue(String couponCode)
    {
        // converting to Integer and back to ensure proper String format
        final String code = new Integer(couponCode).toString();
        final String propName = "couponValue." + code;
        Properties props = Gateway.getProperties("couponvalues.properties");
        String value = (String)props.get(propName);

        if(logger.isDebugEnabled())
        {
            logger.debug("Returning coupon value: " + value + " for couponCode: " + couponCode);   
        }

        return value;
    }
    
}
