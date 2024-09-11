
package max.retail.stores.ws.gstin;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each 
 * Java content interface and Java element interface 
 * generated in the lsipl.retail.stores.ws.gstin package. 
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

    private final static QName _SaveInvoiceDetails_QNAME = new QName("http://gstin.ws.stores.retail.lsipl/", "saveInvoiceDetails");
    private final static QName _SaveInvoiceDetailsResponse_QNAME = new QName("http://gstin.ws.stores.retail.lsipl/", "saveInvoiceDetailsResponse");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: lsipl.retail.stores.ws.gstin
     * 
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link SaveInvoiceDetails }
     * 
     */
    public SaveInvoiceDetails createSaveInvoiceDetails() {
        return new SaveInvoiceDetails();
    }

    /**
     * Create an instance of {@link SaveInvoiceDetailsResponse }
     * 
     */
    public SaveInvoiceDetailsResponse createSaveInvoiceDetailsResponse() {
        return new SaveInvoiceDetailsResponse();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveInvoiceDetails }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gstin.ws.stores.retail.lsipl/", name = "saveInvoiceDetails")
    public JAXBElement<SaveInvoiceDetails> createSaveInvoiceDetails(SaveInvoiceDetails value) {
        return new JAXBElement<SaveInvoiceDetails>(_SaveInvoiceDetails_QNAME, SaveInvoiceDetails.class, null, value);
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link SaveInvoiceDetailsResponse }{@code >}}
     * 
     */
    @XmlElementDecl(namespace = "http://gstin.ws.stores.retail.lsipl/", name = "saveInvoiceDetailsResponse")
    public JAXBElement<SaveInvoiceDetailsResponse> createSaveInvoiceDetailsResponse(SaveInvoiceDetailsResponse value) {
        return new JAXBElement<SaveInvoiceDetailsResponse>(_SaveInvoiceDetailsResponse_QNAME, SaveInvoiceDetailsResponse.class, null, value);
    }

}
