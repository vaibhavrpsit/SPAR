/* ===========================================================================
* Copyright (c) 2008, 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/receipt/FrankingReceiptParameterBean.java /main/2 2013/09/06 11:05:20 mkutiana Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    mkutiana  08/30/13 - Moving Echeck Authorization message text to
 *                         blueprint file
 *    rsnayak   04/12/12 - Franking Blueprint receipts
 *    rsnayak   04/05/12 - Bluprint receipts for Franking
 * ===========================================================================
 */
package oracle.retail.stores.pos.receipt;

import java.util.Locale;

import oracle.retail.stores.domain.stock.GiftCertificateItemIfc;
import oracle.retail.stores.domain.tender.TenderCheckIfc;
import oracle.retail.stores.domain.tender.TenderCoupon;
import oracle.retail.stores.domain.tender.TenderGiftCertificateIfc;
import oracle.retail.stores.domain.tender.TenderMailBankCheckIfc;
import oracle.retail.stores.domain.tender.TenderMoneyOrderIfc;
import oracle.retail.stores.domain.tender.TenderPurchaseOrderIfc;
import oracle.retail.stores.domain.tender.TenderStoreCreditIfc;
import oracle.retail.stores.domain.tender.TenderTravelersCheckIfc;

public class FrankingReceiptParameterBean extends ReceiptParameterBean implements FrankingReceiptParameterBeanIfc
{
    private static final long serialVersionUID = -1285338050461192815L;

    /**
     * The locale used to print the receipt.
     */
    protected Locale locale;

    /**
     * The locale used to print date and time on the receipt.
     */
    protected Locale defaultLocale;

    // Tender Store Credit
    private TenderStoreCreditIfc tenderStoreCredit = null;

    // Tender Gift Certificate
    private TenderGiftCertificateIfc tenderGiftCertificate = null;

    // Tender Purchase Order
    private TenderPurchaseOrderIfc tenderPurchaseOrder = null;

    // Gift Certificate Item
    private GiftCertificateItemIfc giftCertificateItem = null;

    // Tender Check
    private TenderCheckIfc tenderCheck = null;

    // Tender Money Order
    private TenderMoneyOrderIfc tenderMoneyOrder = null;

    // Tender Travelers Check
    private TenderTravelersCheckIfc tenderTravelersCheck = null;

    // Tender MailBank Check
    private TenderMailBankCheckIfc tenderMailBankCheck = null;

    // Tender Coupon
    private TenderCoupon tenderCoupon = null;

    // External Order Number
    private String orderNumber = null;

    // ----Flags for controlling bpt links -----

    private boolean isTenderStoreCredit = false;

    private boolean isTenderGiftCertificate = false;

    private boolean isTenderPurchaseOrder = false;

    private boolean isTenderCheck = false;

    private boolean isTenderMoneyOrder = false;

    private boolean isTenderTravelersCheck = false;

    private boolean isTenderBlankCheck = false;

    private boolean isMallCertCheck = false;

    private boolean isMallCertPo = false;

    private boolean isGiftCertificateItem = false;

    private boolean isVoidTenderCheck = false;

    private boolean isECheck = false;

    private boolean isTrainingMode = false;

    private boolean isTenderMailBankCheck = false;

    private boolean isTenderCoupon = false;

    // non-tender items
    private String bankAccntName = null;

    private String bankAccntNum = null;

    private String checkAuthcode = null;

    private String checkAuthMethod = null;

    private String idType = null;

    private String idState = null;

    private String purchaseOrderNumber = null;

    //Instant Credit reference Number
    private String referenceNumber = null;
    
    private String instantCreditResponse = null;

    /**
     * @return the isTenderPurchaseOrder
     */
    public boolean isTenderPurchaseOrder()
    {
        return isTenderPurchaseOrder;
    }

    /**
     * @param isTenderPurchaseOrder the isTenderPurchaseOrder to set
     */
    public void setTenderPurchaseOrder(boolean isTenderPurchaseOrder)
    {
        this.isTenderPurchaseOrder = isTenderPurchaseOrder;
    }

    /**
     * @return the isTenderCheck
     */
    public boolean isTenderCheck()
    {
        return isTenderCheck;
    }

    /**
     * @param isTenderCheck the isTenderCheck to set
     */
    public void setTenderCheck(boolean isTenderCheck)
    {
        this.isTenderCheck = isTenderCheck;
    }

    /**
     * @return the isTenderMoneyOrder
     */
    public boolean isTenderMoneyOrder()
    {
        return isTenderMoneyOrder;
    }

    /**
     * @param isTenderMoneyOrder the isTenderMoneyOrder to set
     */
    public void setTenderMoneyOrder(boolean isTenderMoneyOrder)
    {
        this.isTenderMoneyOrder = isTenderMoneyOrder;
    }

    /**
     * @return the isTenderTravelersCheck
     */
    public boolean isTenderTravelersCheck()
    {
        return isTenderTravelersCheck;
    }

    /**
     * @param isTenderTravelersCheck the isTenderTravelersCheck to set
     */
    public void setTenderTravelersCheck(boolean isTenderTravelersCheck)
    {
        this.isTenderTravelersCheck = isTenderTravelersCheck;
    }

    /**
     * @return the isTenderBlankCheck
     */
    public boolean isTenderBlankCheck()
    {
        return isTenderBlankCheck;
    }

    /**
     * @param isTenderBlankCheck the isTenderBlankCheck to set
     */
    public void setTenderBlankCheck(boolean isTenderBlankCheck)
    {
        this.isTenderBlankCheck = isTenderBlankCheck;
    }

    /**
     * @return the tenderGiftCertificate
     */
    public TenderGiftCertificateIfc getTenderGiftCertificate()
    {
        return tenderGiftCertificate;
    }

    /**
     * @param tenderGiftCertificate the tenderGiftCertificate to set
     */
    public void setTenderGiftCertificate(TenderGiftCertificateIfc tenderGiftCertificate)
    {
        this.tenderGiftCertificate = tenderGiftCertificate;
    }

    /**
     * @return the tenderStoreCredit
     */
    public TenderStoreCreditIfc getTenderStoreCredit()
    {
        return tenderStoreCredit;
    }

    /**
     * @param tenderStoreCredit the tenderStoreCredit to set
     */
    public void setTenderStoreCredit(TenderStoreCreditIfc tenderStoreCredit)
    {
        this.tenderStoreCredit = tenderStoreCredit;
    }

    /**
     * @return the locale
     */
    public Locale getLocale()
    {
        return locale;
    }

    /**
     * @param locale the locale to set
     */
    public void setLocale(Locale locale)
    {
        this.locale = locale;
    }

    /**
     * @return the defaultLocale
     */
    public Locale getDefaultLocale()
    {
        return defaultLocale;
    }

    /**
     * @param defaultLocale the defaultLocale to set
     */
    public void setDefaultLocale(Locale defaultLocale)
    {
        this.defaultLocale = defaultLocale;
    }

    /**
     * @param isTenderStoreCredit the isTenderStoreCredit to set
     */
    public void setTenderStoreCredit(boolean isTenderStoreCredit)
    {
        this.isTenderStoreCredit = isTenderStoreCredit;
    }

    /**
     * @return the isTenderStoreCredit
     */
    public boolean isTenderStoreCredit()
    {
        return isTenderStoreCredit;
    }

    /**
     * @param isTenderGiftCertificate the isTenderGiftCertificate to set
     */
    public void setTenderGiftCertificate(boolean isTenderGiftCertificate)
    {
        this.isTenderGiftCertificate = isTenderGiftCertificate;
    }

    /**
     * @return the isTenderGiftCertificate
     */
    public boolean isTenderGiftCertificate()
    {
        return isTenderGiftCertificate;
    }

    /**
     * @return the tenderPurchaseOrder
     */
    public TenderPurchaseOrderIfc getTenderPurchaseOrder()
    {
        return tenderPurchaseOrder;
    }

    /**
     * @param tenderPurchaseOrder the tenderPurchaseOrder to set
     */
    public void setTenderPurchaseOrder(TenderPurchaseOrderIfc tenderPurchaseOrder)
    {
        this.tenderPurchaseOrder = tenderPurchaseOrder;
        setPurchaseOrderNumber(new String(tenderPurchaseOrder.getNumber()));
    }

    /**
     * @return the giftCertificateItem
     */
    public GiftCertificateItemIfc getGiftCertificateItem()
    {
        return giftCertificateItem;
    }

    /**
     * @param giftCertificateItem the giftCertificateItem to set
     */
    public void setGiftCertificateItem(GiftCertificateItemIfc giftCertificateItem)
    {
        this.giftCertificateItem = giftCertificateItem;
    }

    /**
     * @return the tenderCheck
     */
    public TenderCheckIfc getTenderCheck()
    {
        return tenderCheck;
    }

    /**
     * @param tenderCheck the tenderCheck to set
     */
    public void setTenderCheck(TenderCheckIfc tenderCheck)
    {
        this.tenderCheck = tenderCheck;
    }

    /**
     * @return the tenderMoneyOrder
     */
    public TenderMoneyOrderIfc getTenderMoneyOrder()
    {
        return tenderMoneyOrder;
    }

    /**
     * @param tenderMoneyOrder the tenderMoneyOrder to set
     */
    public void setTenderMoneyOrder(TenderMoneyOrderIfc tenderMoneyOrder)
    {
        this.tenderMoneyOrder = tenderMoneyOrder;
    }

    /**
     * @return the tenderTravelersCheck
     */
    public TenderTravelersCheckIfc getTenderTravelersCheck()
    {
        return tenderTravelersCheck;
    }

    /**
     * @param tenderTravelersCheck the tenderTravelersCheck to set
     */
    public void setTenderTravelersCheck(TenderTravelersCheckIfc tenderTravelersCheck)
    {
        this.tenderTravelersCheck = tenderTravelersCheck;
    }

    /**
     * @return the tenderMailBankCheck
     */
    public TenderMailBankCheckIfc getTenderMailBankCheck()
    {
        return tenderMailBankCheck;
    }

    /**
     * @param tenderMailBankCheck the tenderMailBankCheck to set
     */
    public void setTenderMailBankCheck(TenderMailBankCheckIfc tenderMailBankCheck)
    {
        this.tenderMailBankCheck = tenderMailBankCheck;
    }

    /**
     * @param bankAccntName the bankAccntName to set
     */
    public void setBankAccntName(String bankAccntName)
    {
        this.bankAccntName = bankAccntName;
    }

    /**
     * @return the bankAccntName
     */
    public String getBankAccntName()
    {
        return bankAccntName;
    }

    /**
     * @param bankAccntNum the bankAccntNum to set
     */
    public void setBankAccntNum(String bankAccntNum)
    {
        this.bankAccntNum = bankAccntNum;
    }

    /**
     * @return the bankAccntNum
     */
    public String getBankAccntNum()
    {
        return bankAccntNum;
    }

    /**
     * @param checkAuth the checkAuth to set
     */
    public void setCheckAuthCode(String checkAuthcode)
    {
        this.checkAuthcode = checkAuthcode;
    }

    /**
     * @return the checkAuth
     */
    public String getCheckAuthCode()
    {
        return checkAuthcode;
    }

    /**
     * @param checkAuthMethod the checkAuthMethod to set
     */
    public void setCheckAuthMethod(String checkAuthMethod)
    {
        this.checkAuthMethod = checkAuthMethod;
    }

    /**
     * @return the checkAuthMethod
     */
    public String getCheckAuthMethod()
    {
        return checkAuthMethod;
    }

    /**
     * @param idType the idType to set
     */
    public void setIdType(String idType)
    {
        this.idType = idType;
    }

    /**
     * @return the idType
     */
    public String getIdType()
    {
        return idType;
    }

    /**
     * @param idState the idState to set
     */
    public void setIdState(String idState)
    {
        this.idState = idState;
    }

    /**
     * @return the idState
     */
    public String getIdState()
    {
        return idState;
    }

    /**
     * @param isMallCertCheck the isMallCertCheck to set
     */
    public void setMallCertCheck(boolean isMallCertCheck)
    {
        this.isMallCertCheck = isMallCertCheck;
    }

    /**
     * @return the isMallCertCheck
     */
    public boolean isMallCertCheck()
    {
        return isMallCertCheck;
    }

    /**
     * @param purchaseOrderNumber the purchaseOrderNumber to set
     */
    public void setPurchaseOrderNumber(String purchaseOrderNumber)
    {
        this.purchaseOrderNumber = purchaseOrderNumber;
    }

    /**
     * @return the purchaseOrderNumber
     */
    public String getPurchaseOrderNumber()
    {
        return purchaseOrderNumber;
    }

    /**
     * @param isMallCertPo the isMallCertPo to set
     */
    public void setMallCertPo(boolean isMallCertPo)
    {
        this.isMallCertPo = isMallCertPo;
    }

    /**
     * @return the isMallCertPo
     */
    public boolean isMallCertPo()
    {
        return isMallCertPo;
    }

    /**
     * @param giftCertificateItem
     */

    public void setGiftCertificateItemFlag(boolean giftCertificateItem)
    {
        this.isGiftCertificateItem = giftCertificateItem;
    }

    /**
     * @return isGiftCertificateItem
     */
    public boolean getGiftCertificateItemFlag()
    {

        return isGiftCertificateItem;
    }

    /**
     * @param isVoidTenderCheck the isVoidTenderCheck to set
     */
    public void setVoidTenderCheck(boolean isVoidTenderCheck)
    {
        this.isVoidTenderCheck = isVoidTenderCheck;
    }

    /**
     * @return the isVoidTenderCheck
     */
    public boolean isVoidTenderCheck()
    {
        return isVoidTenderCheck;
    }

    /**
     * @param isECheck the isECheck to set
     */
    public void setECheck(boolean isECheck)
    {
        this.isECheck = isECheck;
    }

    /**
     * @return the isECheck
     */
    public boolean isECheck()
    {
        return isECheck;
    }

    /**
     * @return the isTrainingMode
     */
    public boolean isTrainingMode()
    {
        return isTrainingMode;
    }

    /**
     * @param isTrainingMode the isTrainingMode to set
     */
    public void setTrainingMode(boolean isTrainingMode)
    {
        this.isTrainingMode = isTrainingMode;
    }

    /**
     * @param isTenderMailBankCheck the isTenderMailBankCheck to set
     */
    public void setTenderMailBankCheck(boolean isTenderMailBankCheck)
    {
        this.isTenderMailBankCheck = isTenderMailBankCheck;
    }

    /**
     * @return the isTenderMailBankCheck
     */
    public boolean isTenderMailBankCheck()
    {
        return isTenderMailBankCheck;
    }

    /**
     * @param tenderCoupon the tenderCoupon to set
     */
    public void setTenderCoupon(TenderCoupon tenderCoupon)
    {
        this.tenderCoupon = tenderCoupon;
    }

    /**
     * @return the tenderCoupon
     */
    public TenderCoupon getTenderCoupon()
    {
        return tenderCoupon;
    }

    /**
     * @param isTenderCoupon the isTenderCoupon to set
     */
    public void setTenderCoupon(boolean isTenderCoupon)
    {
        this.isTenderCoupon = isTenderCoupon;
    }

    /**
     * @return the isTenderCoupon
     */
    public boolean isTenderCoupon()
    {
        return isTenderCoupon;
    }
    
    /**
     * @param orderNumber the orderNumber to set
     */
    public void setOrderNumber(String orderNumber)
    {
        this.orderNumber = orderNumber;
    }

    /**
     * @return the orderNumber
     */
    public String getOrderNumber()
    {
        return orderNumber;
    }

    /**
     * @param referenceNumber the referenceNumber to set
     */
    public void setReferenceNumber(String referenceNumber)
    {
        this.referenceNumber = referenceNumber;
    }

    /**
     * @return the referenceNumber
     */
    public String getReferenceNumber()
    {
        return referenceNumber;
    }

    /**
     * @param instantCreditResponse the instantCreditResponse to set
     */
    public void setInstantCreditResponse(String instantCreditResponse)
    {
        this.instantCreditResponse = instantCreditResponse;
    }

    /**
     * @return the instantCreditResponse
     */
    public String getInstantCreditResponse()
    {
        return instantCreditResponse;
    }

}
