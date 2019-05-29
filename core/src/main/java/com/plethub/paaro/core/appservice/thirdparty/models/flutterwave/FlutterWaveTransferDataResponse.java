package com.plethub.paaro.core.appservice.thirdparty.models.flutterwave;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import java.math.BigDecimal;

public class FlutterWaveTransferDataResponse {

        @JsonProperty("id")
        private Integer id;

        @JsonProperty("account_number")
        private String accountNumber;

        @JsonProperty("bank_code")
        private String bankCode;

        @JsonProperty("fullname")
        private String fullname;

        @JsonProperty("date_created")
        private String dateCreated;

        @JsonProperty("currency")
        private String currency;

        @JsonProperty("amount")
        private BigDecimal amount;

        @JsonProperty("fee")
        private Double fee;

        @JsonProperty("status")
        private String status;

        @JsonProperty("reference")
        private String reference;

        @JsonProperty("meta")
        private Object meta;

        @JsonProperty("narration")
        private String narration;

        @JsonProperty("complete_message")
        private String completeMessage;

        @JsonProperty("requires_approval")
        private Integer requiresApproval;

        @JsonProperty("is_approved")
        private Integer isApproved;

        @JsonProperty("bank_name")
        private String bankName;

        @JsonProperty("id")
        public Integer getId() {
            return id;
        }

        @JsonProperty("id")
        public void setId(Integer id) {
            this.id = id;
        }

        @JsonProperty("account_number")
        public String getAccountNumber() {
            return accountNumber;
        }

        @JsonProperty("account_number")
        public void setAccountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
        }

        @JsonProperty("bank_code")
        public String getBankCode() {
            return bankCode;
        }

        @JsonProperty("bank_code")
        public void setBankCode(String bankCode) {
            this.bankCode = bankCode;
        }

        @JsonProperty("fullname")
        public String getFullname() {
            return fullname;
        }

        @JsonProperty("fullname")
        public void setFullname(String fullname) {
            this.fullname = fullname;
        }

        @JsonProperty("date_created")
        public String getDateCreated() {
            return dateCreated;
        }

        @JsonProperty("date_created")
        public void setDateCreated(String dateCreated) {
            this.dateCreated = dateCreated;
        }

        @JsonProperty("currency")
        public String getCurrency() {
            return currency;
        }

        @JsonProperty("currency")
        public void setCurrency(String currency) {
            this.currency = currency;
        }

        @JsonProperty("amount")
        public BigDecimal getAmount() {
            return amount;
        }

        @JsonProperty("amount")
        public void setAmount(BigDecimal amount) {
            this.amount = amount;
        }

        @JsonProperty("fee")
        public Double getFee() {
            return fee;
        }

        @JsonProperty("fee")
        public void setFee(Double fee) {
            this.fee = fee;
        }

        @JsonProperty("status")
        public String getStatus() {
            return status;
        }

        @JsonProperty("status")
        public void setStatus(String status) {
            this.status = status;
        }

        @JsonProperty("reference")
        public String getReference() {
            return reference;
        }

        @JsonProperty("reference")
        public void setReference(String reference) {
            this.reference = reference;
        }

        @JsonProperty("meta")
        public Object getMeta() {
            return meta;
        }

        @JsonProperty("meta")
        public void setMeta(Object meta) {
            this.meta = meta;
        }

        @JsonProperty("narration")
        public String getNarration() {
            return narration;
        }

        @JsonProperty("narration")
        public void setNarration(String narration) {
            this.narration = narration;
        }

        @JsonProperty("complete_message")
        public String getCompleteMessage() {
            return completeMessage;
        }

        @JsonProperty("complete_message")
        public void setCompleteMessage(String completeMessage) {
            this.completeMessage = completeMessage;
        }

        @JsonProperty("requires_approval")
        public Integer getRequiresApproval() {
            return requiresApproval;
        }

        @JsonProperty("requires_approval")
        public void setRequiresApproval(Integer requiresApproval) {
            this.requiresApproval = requiresApproval;
        }

        @JsonProperty("is_approved")
        public Integer getIsApproved() {
            return isApproved;
        }

        @JsonProperty("is_approved")
        public void setIsApproved(Integer isApproved) {
            this.isApproved = isApproved;
        }

        @JsonProperty("bank_name")
        public String getBankName() {
            return bankName;
        }

        @JsonProperty("bank_name")
        public void setBankName(String bankName) {
            this.bankName = bankName;
        }

    }
