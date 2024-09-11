
package max.retail.stores.domain;

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
    "walletDetails",
    "responseHeader",
    "requestHeader"
})
@Generated("jsonschema2pojo")
public class MAXOxigenGetOtpResponse {

    @JsonProperty("walletDetails")
    private Object walletDetails;
    @JsonProperty("responseHeader")
    private ResponseHeader responseHeader;
    @JsonProperty("requestHeader")
    private RequestHeader requestHeader;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("walletDetails")
    public Object getWalletDetails() {
        return walletDetails;
    }

    @JsonProperty("walletDetails")
    public void setWalletDetails(Object walletDetails) {
        this.walletDetails = walletDetails;
    }

    @JsonProperty("responseHeader")
    public ResponseHeader getResponseHeader() {
        return responseHeader;
    }

    @JsonProperty("responseHeader")
    public void setResponseHeader(ResponseHeader responseHeader) {
        this.responseHeader = responseHeader;
    }

    @JsonProperty("requestHeader")
    public RequestHeader getRequestHeader() {
        return requestHeader;
    }

    @JsonProperty("requestHeader")
    public void setRequestHeader(RequestHeader requestHeader) {
        this.requestHeader = requestHeader;
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
