
package max.retail.stores.pos.services.tender.oxigenwallet.debitresponse;

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
    "creditNoteAmount",
    "bonusCashAmount",
    "giftCardAmount",
    "cashbackAmount",
    "ecashAmount"
})
@Generated("jsonschema2pojo")
public class AppliedDetails {

    @JsonProperty("creditNoteAmount")
    private Integer creditNoteAmount;
    @JsonProperty("bonusCashAmount")
    private Integer bonusCashAmount;
    @JsonProperty("giftCardAmount")
    private Integer giftCardAmount;
    @JsonProperty("cashbackAmount")
    private Integer cashbackAmount;
    @JsonProperty("ecashAmount")
    private Integer ecashAmount;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("creditNoteAmount")
    public Integer getCreditNoteAmount() {
        return creditNoteAmount;
    }

    @JsonProperty("creditNoteAmount")
    public void setCreditNoteAmount(Integer creditNoteAmount) {
        this.creditNoteAmount = creditNoteAmount;
    }

    @JsonProperty("bonusCashAmount")
    public Integer getBonusCashAmount() {
        return bonusCashAmount;
    }

    @JsonProperty("bonusCashAmount")
    public void setBonusCashAmount(Integer bonusCashAmount) {
        this.bonusCashAmount = bonusCashAmount;
    }

    @JsonProperty("giftCardAmount")
    public Integer getGiftCardAmount() {
        return giftCardAmount;
    }

    @JsonProperty("giftCardAmount")
    public void setGiftCardAmount(Integer giftCardAmount) {
        this.giftCardAmount = giftCardAmount;
    }

    @JsonProperty("cashbackAmount")
    public Integer getCashbackAmount() {
        return cashbackAmount;
    }

    @JsonProperty("cashbackAmount")
    public void setCashbackAmount(Integer cashbackAmount) {
        this.cashbackAmount = cashbackAmount;
    }

    @JsonProperty("ecashAmount")
    public Integer getEcashAmount() {
        return ecashAmount;
    }

    @JsonProperty("ecashAmount")
    public void setEcashAmount(Integer ecashAmount) {
        this.ecashAmount = ecashAmount;
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
