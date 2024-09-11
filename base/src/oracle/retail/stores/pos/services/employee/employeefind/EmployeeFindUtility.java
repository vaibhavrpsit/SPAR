/* ===================================================================================
* Copyright (c) 1998, 2012, Oracle and/or its affiliates. All rights reserved. 
 * ===================================================================================
 * $Header: rgbustores/applications/pos/src/oracle/retail/stores/pos/services/employee/employeefind/EmployeeFindUtility.java /main/2 2012/09/12 11:57:09 blarsen Exp $
 * ===================================================================================
 * NOTES
 * <other useful comments, qualifications, etc.>
 *
 * MODIFIED    (MM/DD/YY)
 *    cgreene   03/09/12 - add support for journalling queues by current
 *                         register
 *    hyin      02/25/11 - add journal method
 *    hyin      02/25/11 - create this class to hold common methods
 *
 * ===================================================================================
 */

package oracle.retail.stores.pos.services.employee.employeefind;

import org.apache.log4j.Logger;

import oracle.retail.stores.commerceservices.audit.AuditLoggerServiceIfc;
import oracle.retail.stores.commerceservices.audit.AuditLoggingUtils;
import oracle.retail.stores.commerceservices.audit.event.AuditLogEventEnum;
import oracle.retail.stores.commerceservices.audit.event.EmployeeEvent;
import oracle.retail.stores.domain.employee.EmployeeIfc;
import oracle.retail.stores.domain.financial.RegisterIfc;
import oracle.retail.stores.domain.financial.StoreStatusIfc;
import oracle.retail.stores.domain.store.WorkstationIfc;
import oracle.retail.stores.foundation.manager.ifc.JournalManagerIfc;
import oracle.retail.stores.foundation.tour.ifc.BusIfc;
import oracle.retail.stores.pos.services.employee.employeemain.EmployeeCargo;

/**
 * This is the utility class for EmployeeFind package
 */
public class EmployeeFindUtility {
    
    private static AuditLoggerServiceIfc auditService = AuditLoggingUtils.getAuditLogger();  
    
    /**
     * The logger to which log messages will be sent
     */
    protected static final Logger logger = Logger.getLogger(EmployeeFindUtility.class);
    
    
    /**
     * log an audit event based on parameters
     * 
     * @param success   audit event type
     * @param cargo     employee cargo 
     * @param eventId   event ID
     * @param originator  originator of event
     */
    public static void logAuditEvent(boolean success, EmployeeCargo cargo, AuditLogEventEnum eventId, String originator)
    {
        if (auditService == null)
        {
            logger.warn("Can not get AuditLogger!");
        }else
        {
            EmployeeEvent ev = (EmployeeEvent) AuditLoggingUtils.createLogEvent(
                    EmployeeEvent.class, eventId);

            RegisterIfc ri = cargo.getRegister();
            if (ri != null)
            {
                WorkstationIfc wi = ri.getWorkstation();
                if (wi != null)
                {
                    ev.setStoreId(wi.getStoreID());
                    ev.setRegisterNumber(wi.getWorkstationID());
                }
            }
            EmployeeIfc originalEmployee = cargo.getOriginalEmployee();
            if (originalEmployee != null)
            {
                ev.setEmployeeID(originalEmployee.getEmployeeID());
            }
            StoreStatusIfc ssi = cargo.getStoreStatus();
            if (ssi != null)
            {
                EmployeeIfc eis = ssi.getSignOnOperator();
                if (eis != null)
                {
                    ev.setUserId(eis.getLoginID());
                }
            }
            ev.setEventOriginator(originator);

            if (success)
            {
                auditService.logStatusSuccess(ev);
            }
            else
            {
                auditService.logStatusFailure(ev);
            }
            
        }
        
    }// End of logAuditEven method
    
    /**
     *  log an EJournal entry 
     * @param journalStr
     */
    public static void logEJEntry(BusIfc bus, String journalStr)
    {
        JournalManagerIfc journalMgr = (JournalManagerIfc)bus.getManager(JournalManagerIfc.TYPE);
        if (journalMgr != null)
        {
            journalMgr.journal(journalStr);
        }
        else 
        {
            logger.warn("No journal manager found.");
        }
        
    }

}
