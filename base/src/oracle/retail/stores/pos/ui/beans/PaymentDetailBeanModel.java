/* ===========================================================================
* Copyright (c) 1998, 2011, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/ui/beans/PaymentDetailBeanModel.java /rgbustores_13.4x_generic_branch/1 2011/05/05 14:06:57 mszekely Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/28/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/27/10 - convert to oracle packaging
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    cgreene   03/30/09 - implement printing of layaway location on receipt by
 *                         adding new location code to layaway object and
 *                         deprecating the old string
 *    abondala  02/27/09 - LayawayLocation and OrderLocation parameters are
 *                         changed to ReasonCodes.
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.ui.beans;

import java.util.Vector;

import oracle.retail.stores.commerceservices.common.currency.CurrencyIfc;
import oracle.retail.stores.domain.utility.CodeEntryIfc;
import oracle.retail.stores.domain.utility.CodeListIfc;
import oracle.retail.stores.domain.utility.EYSDate;
import oracle.retail.stores.foundation.utility.Util;

/**
 * This model is used by Payment Detail and Refund Detail beans.
 * 
 * @version $Revision: /rgbustores_13.4x_generic_branch/1 $
 * @see oracle.retail.stores.pos.ui.beans.PaymentDetailBean
 */
public class PaymentDetailBeanModel extends ReasonBeanModel
{
    private static final long serialVersionUID = -8330258630688272557L;

    /** Revision number supplied by source-code control system **/
    public static final String revisionNumber = "$Revision: /rgbustores_13.4x_generic_branch/1 $";

    /** Layaway transactionNumber */
    protected String layawayNumber;

    /** Flag used to determine if layaway new */
    protected boolean newLayawayFlag;

    /** Flag used to check if this is from delete service */
    protected boolean deleteLayawayFlag;

    /** Flag used to check if this is from pickup service */
    protected boolean pickupLayawayFlag;

    /** Customer name */
    protected String customerName;

    /** Date layaway expires */
    protected EYSDate expirationDate;

    /** Balance due */
    protected CurrencyIfc balanceDue;

    /** Amount paid prior to current payment */
    protected CurrencyIfc amountPaid;

    /** Layaway Fee */
    protected CurrencyIfc layawayFee;

    /** Layaway deletion fee */
    protected CurrencyIfc deletionFee;

    /** Amount of current payment */
    protected CurrencyIfc payment;

    /** Amount of minimum down payment */
    protected CurrencyIfc minimumDownPayment;

    /** Amount of minimum down payment + Layaway Fee */
    protected CurrencyIfc minimumPayment;

    /** @deprecated as of 13.1 use {@link #locationCode} instead */
    protected String location;

    /** Location where item is currently stored */
    protected CodeEntryIfc locationCode;

    /** Current status of layaway */
    protected int layawayStatus;

    /** Acceptable values for the location combo box */
    protected Vector<String> locationValues;

    /** Amount of refund due the layaway customer */
    protected CurrencyIfc refund;

    /** layaway locations from the reason codes list */
    protected CodeListIfc layawayLocationsList = null;

    /**
     * PaymentDetailBeanModel constructor.
     */
    public PaymentDetailBeanModel()
    {
        super();
    }

    /**
     * Gets the layawayNumber value.
     * 
     * @return The layawayNumber
     */
    public String getLayawayNumber()
    {
        return layawayNumber;
    }

    /**
     * Gets the newLayawayFlag value.
     * 
     * @return The newLayawayFlag.
     */
    public boolean isNewLayawayFlag()
    {
        return newLayawayFlag;
    }

    /**
     * Gets the deleteLayawayFlag value.
     * 
     * @return The deleteLayawayFlag.
     */
    public boolean isDeleteLayawayFlag()
    {
        return deleteLayawayFlag;
    }

    /**
     * Gets the pickupLayawayFlag value.
     * 
     * @return The pickupLayawayFlag.
     */
    public boolean isPickupLayawayFlag()
    {
        return pickupLayawayFlag;
    }

    /**
     * Gets the customerName value.
     * 
     * @return The customerName.
     */
    public String getCustomerName()
    {
        return customerName;
    }

    /**
     * Gets the expiration date value.
     * 
     * @return The expirationDate.
     */
    public EYSDate getExpirationDate()
    {
        return expirationDate;
    }

    /**
     * Gets the balance due value.
     * 
     * @return The balanceDue.
     */
    public CurrencyIfc getBalanceDue()
    {
        return balanceDue;
    }

    /**
     * Gets the amount paid value.
     * 
     * @return The amountPaid.
     */
    public CurrencyIfc getAmountPaid()
    {
        return amountPaid;
    }

    /**
     * Gets the layaway creation fee value.
     * 
     * @return The layawayFee.
     */
    public CurrencyIfc getLayawayFee()
    {
        return layawayFee;
    }

    /**
     * Gets the layaway deletion fee value.
     * 
     * @return The deletionFee.
     */
    public CurrencyIfc getDeletionFee()
    {
        return deletionFee;
    }

    /**
     * Gets the payment value.
     * 
     * @return The payment.
     */
    public CurrencyIfc getPayment()
    {
        return payment;
    }

    /**
     * Gets the minimum down payment value.
     * 
     * @return The minimumDownPayment.
     */
    public CurrencyIfc getMinimumDownPayment()
    {
        return minimumDownPayment;
    }

    /**
     * Gets the minimum payment value.
     * 
     * @return The minimumPayment.
     */
    public CurrencyIfc getMinimumPayment()
    {
        return minimumPayment;
    }

    /**
     * @deprecated as of 13.1 use {@link #getLocationCode()} instead
     */
    public String getLocation()
    {
        return location;
    }

    /**
     * Gets the location value.
     * 
     * @return The location.
     */
    public CodeEntryIfc getLocationCode()
    {
        return locationCode;
    }

    /**
     * Gets the refund amount value.
     * 
     * @return The refund.
     */
    public CurrencyIfc getRefund()
    {
        return refund;
    }

    /**
     * Gets the layaway status value.
     * 
     * @return The layawayStatus.
     */
    public int getLayawayStatus()
    {
        return layawayStatus;
    }

    /**
     * Gets the layaway locations
     * 
     * @return CodeListIfc
     */
    public CodeListIfc getLayawayLocationsList()
    {
        return layawayLocationsList;
    }

    /**
     * Sets the layaway number.
     * 
     * @param layaway number string.
     */
    public void setLayawayNumber(String number)
    {
        layawayNumber = number;
    }

    /**
     * Sets the newLayawayFlag value.
     * 
     * @param newLayawayFlag The new value for the newlayawayFlag.
     */
    public void setNewLayawayFlag(boolean newFlag)
    {
        newLayawayFlag = newFlag;
    }

    /**
     * Sets the deleteLayawayFlag value.
     * 
     * @param deleteLayawayFlag The new value for the deleteLayawayFlag.
     */
    public void setDeleteLayawayFlag(boolean deleteFlag)
    {
        deleteLayawayFlag = deleteFlag;
    }

    /**
     * Sets the pickupLayawayFlag value.
     * 
     * @param pickupLayawayFlag The new value for the pickupLayawayFlag.
     */
    public void setPickupLayawayFlag(boolean pickupFlag)
    {
        pickupLayawayFlag = pickupFlag;
    }

    /**
     * Sets the customer name value.
     * 
     * @param customerName The new value for the customerName.
     */
    public void setCustomerName(String name)
    {
        customerName = name;
    }

    /**
     * Sets the expiration date value.
     * 
     * @param expirationDate The new value for the expirationDate.
     */
    public void setExpirationDate(EYSDate date)
    {
        expirationDate = date;
    }

    /**
     * Sets the balance due value.
     * 
     * @param balanceDue The new value for the balanceDue.
     */
    public void setBalanceDue(CurrencyIfc balance)
    {
        balanceDue = balance;
    }

    /**
     * Sets the amount paid value.
     * 
     * @param amountPaid The new value for the amountPaid.
     */
    public void setAmountPaid(CurrencyIfc paid)
    {
        amountPaid = paid;
    }

    /**
     * Sets the layaway creation fee value.
     * 
     * @param layawayFee The new value for the layawayFee.
     */
    public void setLayawayFee(CurrencyIfc createFee)
    {
        layawayFee = createFee;
    }

    /**
     * Sets the layaway deletion fee value.
     * 
     * @param deletionFee The new value for the deletionFee.
     */
    public void setDeletionFee(CurrencyIfc deleteFee)
    {
        deletionFee = deleteFee;
    }

    /**
     * Sets the payment value.
     * 
     * @param payment The new value for the payment.
     */
    public void setPayment(CurrencyIfc paymentAmount)
    {
        payment = paymentAmount;
    }

    /**
     * Sets the minimum down payment value.
     * 
     * @param minimumDownPayment The new value for the minimumDownPayment.
     */
    public void setMinimumDownPayment(CurrencyIfc downPayment)
    {
        minimumDownPayment = downPayment;
    }

    /**
     * Sets the minimum payment value.
     * 
     * @param minimumPayment The new value for the minimumPayment.
     */
    public void setMinimumPayment(CurrencyIfc payment)
    {
        minimumPayment = payment;
    }

    /**
     * @deprecated as of 13.1 use {@link #setLocationCode(CodeEntryIfc)} instead
     */
    public void setLocation(String layawayLocation)
    {
        location = layawayLocation;
    }

    /**
     * Sets the location value.
     * 
     * @param location The new value for the location.
     */
    public void setLocationCode(CodeEntryIfc layawayLocation)
    {
        locationCode = layawayLocation;
    }

    /**
     * Sets the layaway Locations.
     * 
     * @param CodeListIfc
     */
    public void setLayawayLocationsList(CodeListIfc layawayLocationsList)
    {
        this.layawayLocationsList = layawayLocationsList;
    }

    /**
     * Sets the layawayStatus value.
     * 
     * @param layawayStatus int to set layawayStatus
     */
    public void setLayawayStatus(int status)
    {
        layawayStatus = status;
    }

    /**
     * Sets the refund amount value.
     * 
     * @param refundAmount CurrencyIfc refund amount
     */
    public void setRefund(CurrencyIfc refundAmount)
    {
        refund = refundAmount;
    }

    /**
     * Return string representation of the PaymentDetailBeanModel values.
     * 
     * @return PaymentDetailBeanModel values as a string
     */
    public String toString()
    {
        StringBuilder buf = new StringBuilder("PaymentDetailBeanModel{");
        /*
        buf.append("\n LayawayNumber = ").append(getLayawayNumber()).append(",");
        buf.append("\n New Layaway Flag = ").append(isNewLayawayFlag()).append(",");
        buf.append("\n Delete Layaway Flag = ").append(isDeleteLayawayFlag()).append(",");
        buf.append("\n Pickup Layaway Flag = ").append(isPickupLayawayFlag()).append(",");
        buf.append("\n Customer Name = ").append(getCustomerName()).append(",");
        buf.append("\n Expiration Date = ").append(getExpirationDate().toFormattedString()).append(",");
        buf.append("\n Balance Due = ").append(getBalanceDue().toString()).append(",");
        buf.append("\n Amount Paid = ").append(getAmountPaid().toString()).append(",");
        buf.append("\n Creation Fee = ").append(getLayawayFee().toString()).append(",");
        buf.append("\n Deletion Fee = ").append(getDeletionFee().toString()).append(",");
        buf.append("\n Payment = ").append(getPayment().toString()).append(",");
        buf.append("\n Minimum Down Payment = ").append(getMinimumDownPayment().toFormattedString()).append(",");
        buf.append("\n Minimum Payment = ").append(getMinimumPayment().toFormattedString()).append(",");
        buf.append("\n Location = ").append(getLocation()).append(",");
        buf.append("\n Layaway Status = ").append(getLayawayStatus()).append(",");
        buf.append("\n Refund = ").append(getRefund().toFormattedString()).append(" ");
        */
        buf.append("}\n");
        return buf.toString();
    }

    /**
     * Retrieves the revision number.
     * 
     * @return String representation of revision number
     */
    public String getRevisionNumber()
    {
        return (Util.parseRevisionNumber(revisionNumber));
    }
}
