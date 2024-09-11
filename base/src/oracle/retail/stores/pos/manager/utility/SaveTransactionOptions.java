package oracle.retail.stores.pos.manager.utility;
/* ===========================================================================
* Copyright (c) 2013, Oracle and/or its affiliates. All rights reserved.
 * ===========================================================================
 * $$Log:$$
 * ===========================================================================
 */

/**
 * Used by the ExtTransactionUtilityManager for various options used
 * in saving the retail transactions. The options provide a finer grained
 * support for the archiving operations.
 * 
 * @author rhaight
 * @since 14.0
 */
public class SaveTransactionOptions {

    /** Name of the archive to create for inprocess and verifications */
    protected String archiveName = null;

    /** Flag to require verification of the RetailTransaction after persisting */
    protected boolean verify = false;
    
    /** Flag to allow the DataTransaction to be queued in DataManager */
    protected boolean allowQueueing = true;
    
    /** Flag to delete the inprocess transaction */
    protected boolean deleteInprocess = true;
    
    /** Flag to journal transaction after persisting */
    protected boolean journalAtEnd = true;
    
    /**
     * Default constructor for SaveTransactionOptions
     */
    public SaveTransactionOptions()
    {
        
    }

    /**
     * Alternate constructor for SaveTransactionOptions to specify paramters
     * at construction
     * @param archiveName name of the archive to create
     * @param verify flag to force verification of transaction persistence
     * @param allowQueueing flag to allow a queued DataTransaction to persist 
     * @param deleteInprocess flag to delete inprocess transaction archives
     * @param journalAtEnd flag to journal transaction after persisting
     */
    public SaveTransactionOptions(String archiveName, boolean verify, boolean allowQueueing, 
                                  boolean deleteInprocess, boolean journalAtEnd)
    {
        this.archiveName = archiveName;
        this.verify = verify;
        this.allowQueueing = allowQueueing;
        this.deleteInprocess = deleteInprocess;
        this.journalAtEnd = journalAtEnd;
    }

    /**
     * 
     * @return true if transaction is to be verified
     */
    public boolean isVerify() {
        return verify;
    }


    /**
     * 
     * @param verify true causes the transaction to be verified during persistence
     */
    public void setVerify(boolean verify) {
        this.verify = verify;
    }


    /**
     * 
     * @return true to allow a queued DataTransaction for persistence
     */
    public boolean isAllowQueueing() {
        return allowQueueing;
    }


    /**
     * 
     * @param allowQueueing true to allow a queued DataTransaction for persistence
     */
    public void setAllowQueueing(boolean allowQueueing) {
        this.allowQueueing = allowQueueing;
    }
    
    /**
     * @param delete flag to delete the inprocess transaction
     */
    public void setDeleteInprocess(boolean delete)
    {
        deleteInprocess = delete;
    }
    
    /**
     * 
     * @return true to delete inprocess tranactions from archive
     */
    public boolean isDeleteInProcess()
    {
        return deleteInprocess;
    }

    /**
     * 
     * @param name of the archive to create
     */
    public void setArchiveName(String name)
    {
        archiveName = name;
    }
    
    /**
     * 
     * @return name of the archive to create
     */
    public String getArchiveName()
    {
        return archiveName;
    }
    
    /**
     * Sets flag to indicate that the transaction should be journaled
     * after persisting
     * 
     * @param value
     */
    public void setJournalAtEnd(boolean value)
    {
        journalAtEnd = value;
    }
    
    /**
     * 
     * @return true if transaction is to be journaled after persisting
     */
    public boolean isJournalAtEnd()
    {
        return journalAtEnd;
    }
}
