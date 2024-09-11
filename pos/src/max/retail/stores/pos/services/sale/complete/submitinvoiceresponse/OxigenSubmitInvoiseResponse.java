
package max.retail.stores.pos.services.sale.complete.submitinvoiceresponse;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "requestHeader",
    "responseHeader",
    "uniqueTxnId"
})
@Generated("jsonschema2pojo")
public class OxigenSubmitInvoiseResponse {

    @JsonProperty("requestHeader")
    private RequestHeader requestHeader;
    @JsonProperty("responseHeader")
    private ResponseHeader responseHeader;
    @JsonProperty("uniqueTxnId")
    private String uniqueTxnId;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("requestHeader")
    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    @JsonProperty("requestHeader")
    public void setRequestHeader(RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
    }

    @JsonProperty("responseHeader")
    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    @JsonProperty("responseHeader")
    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    @JsonProperty("uniqueTxnId")
    public String getUniqueTxnId() {
        return uniqueTxnId;
    }

    @JsonProperty("uniqueTxnId")
    public void setUniqueTxnId(String uniqueTxnId) {
        this.uniqueTxnId = uniqueTxnId;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
