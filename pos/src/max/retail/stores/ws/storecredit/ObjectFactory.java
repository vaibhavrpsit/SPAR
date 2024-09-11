
package max.retail.stores.ws.storecredit;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the max.retail.stores.ws.storecredit package. 
 * <p>An ObjectFactory allows you to programatically 
 * construct new instances of the Java representation 
 * for XML content. The Java representation of XML 
 * content can consist of schema derived interfaces 
 * and classes representing the binding of schema 
 * type definitions, element declarations and model 
 * groups.  Factory methods for each of these are 
 * provided in this class.
 * 
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _TriggerStoreCreditInOldCOResponse_QNAME = new QName("http://storecredit.ws.stores.retail.max/", "triggerStoreCreditInOldCOResponse");
    private final static QName _TriggerStoreCreditInOldCO_QNAME = new QName("http://storecredit.ws.stores.retail.max/", "triggerStoreCreditInOldCO");
    private final static QName _UpdateStoreCreditLockStatusResponse_QNAME = new QName("http://storecredit.ws.stores.retail.max/", "updateStoreCreditLockStatusResponse");
    private final static QName _GetStoreCreditDetails_QNAME = new QName("http://storecredit.ws.stores.retail.max/", "getStoreCreditDetails");
    private final static QName _UpdateStoreCreditLockStatus_QNAME = new QName("http://storecredit.ws.stores.retail.max/", "updateStoreCreditLockStatus");
    private final static QName _GetStoreCreditDetailsResponse_QNAME = new QName("http://storecredit.ws.stores.retail.max/", "getStoreCreditDetailsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: max.retail.stores.ws.storecredit
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link UpdateStoreCreditLockStatus }
     * 
     */
    public UpdateStoreCreditLockStatus createUpdateStoreCreditLockStatus() {
        return new UpdateStoreCreditLockStatus();
    }

    /**
     * Create an instance of {@link GetStoreCreditDetailsResponse }
     * 
     */
    public GetStoreCreditDetailsResponse createGetStoreCreditDetailsResponse() {
        return new GetStoreCreditDetailsResponse();
    }

    /**
     * Create an instance of {@link GetStoreCreditDetails }
     * 
     */
    public GetStoreCreditDetails createGetStoreCreditDetails() {
        return new GetStoreCreditDetails();
    }

    /**
     * Create an instance of {@link TriggerStoreCreditInOldCO }
     * 
     */
    public TriggerStoreCreditInOldCO createTriggerStoreCreditInOldCO() {
        return new TriggerStoreCreditInOldCO();
    }

    /**
     * Create an instance of {@link TriggerStoreCreditInOldCOResponse }
     * 
     */
    public TriggerStoreCreditInOldCOResponse createTriggerStoreCreditInOldCOResponse() {
        return new TriggerStoreCreditInOldCOResponse();
    }

    /**
     * Create an instance of {@link UpdateStoreCreditLockStatusResponse }
     * 
     */
    public UpdateStoreCreditLockStatusResponse createUpdateStoreCreditLockStatusResponse() {
        return new UpdateStoreCreditLockStatusResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TriggerStoreCreditInOldCOResponse }{@code >}}
     * 
     */
    /*@XmlElementDecl(namespace = "http://storecredit.ws.stores.retail.max/", name = "triggerStoreCreditInOldCOResponse")
    public JAXBElement<TriggerStoreCreditInOldCOResponse> createTriggerStoreCreditInOldCOResponse(TriggerStoreCreditInOldCOResponse value) {
        return new JAXBElement<TriggerStoreCreditInOldCOResponse>(_TriggerStoreCreditInOldCOResponse_QNAME, TriggerStoreCreditInOldCOResponse.class, null, value);
    }*/

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link TriggerStoreCreditInOldCO }{@code >}}
     * 
     */
  /*  @XmlElementDecl(namespace = "http://storecredit.ws.stores.retail.max/", name = "triggerStoreCreditInOldCO")
    public JAXBElement<TriggerStoreCreditInOldCO> createTriggerStoreCreditInOldCO(TriggerStoreCreditInOldCO value) {
        return new JAXBElement<TriggerStoreCreditInOldCO>(_TriggerStoreCreditInOldCO_QNAME, TriggerStoreCreditInOldCO.class, null, value);
    }8?

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateStoreCreditLockStatusResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://storecredit.ws.stores.retail.max/", name = "updateStoreCreditLockStatusResponse")
    public JAXBElement<UpdateStoreCreditLockStatusResponse> createUpdateStoreCreditLockStatusResponse(UpdateStoreCreditLockStatusResponse value) {
        return new JAXBElement<UpdateStoreCreditLockStatusResponse>(_UpdateStoreCreditLockStatusResponse_QNAME, UpdateStoreCreditLockStatusResponse.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStoreCreditDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://storecredit.ws.stores.retail.max/", name = "getStoreCreditDetails")
    public JAXBElement<GetStoreCreditDetails> createGetStoreCreditDetails(GetStoreCreditDetails value) {
        return new JAXBElement<GetStoreCreditDetails>(_GetStoreCreditDetails_QNAME, GetStoreCreditDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link UpdateStoreCreditLockStatus }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://storecredit.ws.stores.retail.max/", name = "updateStoreCreditLockStatus")
    public JAXBElement<UpdateStoreCreditLockStatus> createUpdateStoreCreditLockStatus(UpdateStoreCreditLockStatus value) {
        return new JAXBElement<UpdateStoreCreditLockStatus>(_UpdateStoreCreditLockStatus_QNAME, UpdateStoreCreditLockStatus.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link GetStoreCreditDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://storecredit.ws.stores.retail.max/", name = "getStoreCreditDetailsResponse")
    public JAXBElement<GetStoreCreditDetailsResponse> createGetStoreCreditDetailsResponse(GetStoreCreditDetailsResponse value) {
        return new JAXBElement<GetStoreCreditDetailsResponse>(_GetStoreCreditDetailsResponse_QNAME, GetStoreCreditDetailsResponse.class, null, value);
    }

}
