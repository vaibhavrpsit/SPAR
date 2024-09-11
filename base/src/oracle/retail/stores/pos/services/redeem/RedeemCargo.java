/* ===========================================================================
* Copyright (c) 2003, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/redeem/RedeemCargo.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:10 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/26/10 - convert to oracle packaging
 *    asinton   04/22/10 - Modified redeem tour to fix timeout issue.
 *    abondala  01/03/10 - update header date
 *
 * ===========================================================================
 * $Log:
 * 3    360Commerce 1.2         3/31/2005 4:29:36 PM   Robert Pearse   
 * 2    360Commerce 1.1         3/10/2005 10:24:35 AM  Robert Pearse   
 * 1    360Commerce 1.0         2/11/2005 12:13:35 PM  Robert Pearse   
 *
 *Revision 1.11  2004/05/21 20:27:59  crain
 *@scr 5108 Tender Redeem_Redeem Foreign Gift Certificate Receipt Incorrect
 *
 *Revision 1.10  2004/05/20 19:48:52  crain
 *@scr 5108 Tender Redeem_Redeem Foreign Gift Certificate Receipt Incorrect
 *
 *Revision 1.9  2004/04/13 19:02:22  lzhao
 *@scr 3872: gift card redeem.
 *
 *Revision 1.8  2004/04/12 18:37:47  blj
 *@scr 3872 - fixed a problem with validation occuring after foreign currency has been converted.
 *
 *Revision 1.7  2004/04/07 22:49:40  blj
 *@scr 3872 - fixed problems with foreign currency, fixed ui labels, redesigned to do validation and adding tender to transaction in separate sites.
 *
 *Revision 1.6  2004/04/02 16:26:22  blj
 *@scr 3872 - Fixed validation, database and training mode errors
 *
 *Revision 1.5  2004/03/25 23:01:23  lzhao
 *@scr #3872 Redeem Gift Card
 *
 *Revision 1.4  2004/03/22 23:59:08  lzhao
 *@scr 3872 - add gift card redeem (initial)
 *
 *Revision 1.3  2004/03/22 17:26:42  blj
 *@scr 3872 - added redeem security, receipt printing and saving redeem transactions.
 *
 *Revision 1.2  2004/03/08 23:36:26  blj
 *@scr 3872 - redeem tour sites and shuttles
 *
 *Revision 1.1  2004/02/26 04:48:54  blj
 *@scr 0 - redeem services has moved to _360commerce.  Redeem is now an ADO service.
 *
 *Revision 1.3  2004/02/12 16:51:41  mcs
 *Forcing head revision
 *
 *Revision 1.2  2004/02/11 21:52:30  rhafernik
 *@scr 0 Log4J conversion and code cleanup
 *
 *Revision 1.1.1.1  2004/02/11 01:04:19  cschellenger
 *updating to pvcs 360store-current
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.redeem;

// domain imports

import oracle.retail.stores.pos.ado.tender.TenderTypeEnum;
import oracle.retail.stores.pos.services.common.RetailTransactionCargoIfc;
import oracle.retail.stores.pos.services.tender.TenderCargo;
import oracle.retail.stores.domain.employee.RoleFunctionIfc;
import oracle.retail.stores.domain.utility.GiftCardIfc;

/**
 * Holds all cargo information relevant for Redeem service.
 */
public class RedeemCargo extends TenderCargo implements RetailTransactionCargoIfc
{                                                                               
    /**
     * serialVersionUID
     */
    private static final long serialVersionUID = 2208224017604604729L;

    /**
     * gift card reference
     */
    protected GiftCardIfc giftCard = null;

    /**
     * String version of redeem type selected
     */
    protected String redeemTypeSelected;

    /**
     * Foreign flag
     */
    protected boolean foreignFlag = false;

    /**
     * Inactive timeout flag.
     */
    protected boolean inactiveTimeout = false;

    /**
     * Returns the function ID whose access is to be checked.
     * @return int function ID 
     */
    public int getAccessFunctionID()
    {
        return RoleFunctionIfc.REDEEM;
    }

    /**
     * Returns the redeem type selected.
     * @return String representing redeem type selected.
     */
    public String getRedeemTypeSelected()
    {
        if (TenderTypeEnum.GIFT_CERT.toString().equals(redeemTypeSelected))
        {
            redeemTypeSelected = TenderTypeEnum.GIFT_CERT.getDescription();  
        }
        return redeemTypeSelected;
    }

    /**
     * Sets the redeem type selected
     * @param value String representing redeem type selected.
     */
    public void setRedeemTypeSelected(String value)
    {
        redeemTypeSelected = value;
    }

    /**
     * Get foreign flag
     * @return boolean foreign flag
     */
    public boolean isForeign()
    {
        return foreignFlag;
    }

    /**
     * Sets foreign flag
     * @param value boolean representing foreign flag
     */
    public void setForeign(boolean value)
    {
        foreignFlag = value;
    }

    /**
     * set gift card reference
     * @param value GiftCardIfc
     */
    public void setGiftCard(GiftCardIfc value)
    {
        giftCard = value;
    }

    /**
     * get gift card reference
     * @return GiftCardIfc
     */
    public GiftCardIfc getGiftCard()
    {
        return giftCard;
    }

    /**
     * Returns the inactiveTimeout value.
     * @return the inactiveTimeout
     */
    public boolean isInactiveTimeout()
    {
        return inactiveTimeout;
    }

    /**
     * Sets the inactiveTimeout value.
     * @param inactiveTimeout the inactiveTimeout to set
     */
    public void setInactiveTimeout(boolean inactiveTimeout)
    {
        this.inactiveTimeout = inactiveTimeout;
    }

    
}
