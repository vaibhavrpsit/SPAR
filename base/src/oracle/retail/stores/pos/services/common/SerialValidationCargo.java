/* ===========================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===========================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/common/SerialValidationCargo.java /main/5 2012/09/12 11:57:09 blarsen Exp $
 * ===========================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   05/08/12 - formatting
 *    cgreene   05/26/10 - convert to oracle packaging
 *    abondala  01/03/10 - update header date
 *    nkgautam  12/15/09 - Cargo class for Serial Validation tour
 *
 * ===========================================================================
 */
package oracle.retail.stores.pos.services.common;

import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.lineitem.SaleReturnLineItemIfc;
import oracle.retail.stores.domain.transaction.SearchCriteriaIfc;
import oracle.retail.stores.foundation.tour.ifc.CargoIfc;

/**
 * Cargo class for Serial Validation
 * 
 * @author nkgautam
 */
public class SerialValidationCargo implements CargoIfc
{
    /**
     * A line item to check
     */
    protected SaleReturnLineItemIfc lineItem;
    /**
     * the financial data for the register
     */
    protected RegisterIfc register;
    /**
     * boolean to indicate whether response needs to be processed or passed on
     * as in case of IMEI
     */
    protected boolean processValidationResult = true;
    /**
     * Response Object
     */
    protected SerialValidationResponseIfc response;
    /**
     * The item that we are looking for
     */
    protected SearchCriteriaIfc criteria = null;

    /**
     * @return boolean
     */
    public boolean isProcessValidationResult()
    {
        return processValidationResult;
    }

    /**
     * Sets the processValidationResult boolean
     * 
     * @param processValidationResult
     */
    public void setProcessValidationResult(boolean processValidationResult)
    {
        this.processValidationResult = processValidationResult;
    }

    /**
     * Returns the line item.
     * 
     * @return The line item
     */
    public SaleReturnLineItemIfc getLineItem()
    {
        return lineItem;
    }

    /**
     * Sets the line item.
     * 
     * @param flag to indicate if item is a kit header
     */
    public void setLineItem(SaleReturnLineItemIfc lineItem)
    {
        this.lineItem = lineItem;
    }

    /**
     * Returns the Register.
     */
    public RegisterIfc getRegister()
    {
        return register;
    }

    /**
     * Sets the register object
     * 
     * @param register
     */
    public void setRegister(RegisterIfc register)
    {
        this.register = register;
    }

    /**
     * Gets the Serial Validation Response
     * 
     * @return SerialValidationResponseIfc
     */
    public SerialValidationResponseIfc getResponse()
    {
        return response;
    }

    /**
     * Sets the Serial Validation Response
     * 
     * @param response
     */
    public void setResponse(SerialValidationResponseIfc response)
    {
        this.response = response;
    }

    /**
     * Gets the SearchCriteria Condition
     * 
     * @return
     */
    public SearchCriteriaIfc getCriteria()
    {
        return criteria;
    }

    /**
     * Sets the current Search Criteria
     * 
     * @param criteria
     */
    public void setCriteria(SearchCriteriaIfc criteria)
    {
        this.criteria = criteria;
    }
}