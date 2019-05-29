package com.plethub.paaro.core.models;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.plethub.paaro.core.appservice.enums.PaymentMethod;
import com.plethub.paaro.core.appservice.enums.TransactionStatus;
import com.plethub.paaro.core.appservice.enums.WalletStatus;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

@Entity
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name = "transaction_type", discriminatorType = DiscriminatorType.STRING)
public class Transaction implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String toAccountNumber;

    private String toAccountName;

    @Enumerated(value = EnumType.STRING)
    @NotNull
    private TransactionStatus transactionStatus;

    @NotNull
    private String narration;

    @NotNull
    @OneToOne
    @JsonIgnoreProperties(value = {"password","base64Image"})
    private ManagedUser managedUser;

    @NotNull
    private String paaroReferenceId;

    private String thirdPartyReferenceId;

    @NotNull
    private BigDecimal actualAmount;

    private BigDecimal totalAmount;

    private BigDecimal chargeAmount;

    @Enumerated(value = EnumType.STRING)
    private WalletStatus walletStatus;

    /**
     * This is user rate provided while logging transaction
     */
    private Double userRate;

    @OneToOne
    @JsonIgnoreProperties(value = {"walletList","id"})
    private Currency currency;

    @OneToOne
    @JsonIgnoreProperties(value = {"walletList","id"})
    private Currency fromCurrency;

    @Enumerated(value = EnumType.STRING)
    private TransferTransactionType transferTransactionType;

    @Enumerated(value = EnumType.STRING)
    private PaymentMethod paymentMethod;

    private Long paymentReferenceId;

    @OneToOne
    @JsonIgnoreProperties(value = {"walletList","id"})
    private Currency toCurrency;

    /**
     * This is paaro rate. Must be greater or equal to the userRate and not less
     */
    @NotNull
    private Double exchangeRate;

    @NotNull
    private Date initiatedDate;

    @NotNull
    private Date lastUpdatedDate;

    @NotNull
    @OneToOne
    private Wallet wallet;

    @OneToOne
    private TransferRequestMap transferRequestMap;

    @OneToOne
    private Bank receivingBank;

    private String errorMessage;

    private String paymentRefNumber;

    private String paymentMessage;

    private Integer noOfPaymentTrial = 0;

    private Date dateFunded;

    @OneToOne
    private BankDeposit bankDeposit;

    @OneToOne
    private PaymentReference paymentReference;

    private boolean isSettled = false;

    private Date dateSettled;

    @OneToOne
    private ManagedUser settlementInitiatedBy;

    @OneToOne
    private ManagedUser settlementVerifiedOrDeclinedBy;

    private String sortCode;

    private Long transactionId;

    public String getToAccountNumber() {
        return toAccountNumber;
    }

    public void setToAccountNumber(String toAccountNumber) {
        this.toAccountNumber = toAccountNumber;
    }

    public TransactionStatus getTransactionStatus() {
        return transactionStatus;
    }

    public void setTransactionStatus(TransactionStatus transactionStatus) {
        this.transactionStatus = transactionStatus;
    }

    public String getNarration() {
        return narration;
    }

    public void setNarration(String narration) {
        this.narration = narration;
    }

    public String getPaaroReferenceId() {
        return paaroReferenceId;
    }

    public void setPaaroReferenceId(String paaroReferenceId) {
        this.paaroReferenceId = paaroReferenceId;
    }

    public String getThirdPartyReferenceId() {
        return thirdPartyReferenceId;
    }

    public void setThirdPartyReferenceId(String thirdPartyReferenceId) {
        this.thirdPartyReferenceId = thirdPartyReferenceId;
    }


    public BigDecimal getActualAmount() {
        return actualAmount;
    }

    public void setActualAmount(BigDecimal actualAmount) {
        this.actualAmount = actualAmount;
    }

    public Currency getFromCurrency() {
        return fromCurrency;
    }

    public void setFromCurrency(Currency fromCurrency) {
        this.fromCurrency = fromCurrency;
    }

    public Currency getToCurrency() {
        return toCurrency;
    }

    public void setToCurrency(Currency toCurrency) {
        this.toCurrency = toCurrency;
    }

    public Double getExchangeRate() {
        return exchangeRate;
    }

    public void setExchangeRate(Double exchangeRate) {
        this.exchangeRate = exchangeRate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ManagedUser getManagedUser() {
        return managedUser;
    }

    public void setManagedUser(ManagedUser managedUser) {
        this.managedUser = managedUser;
    }

    public Date getInitiatedDate() {
        return initiatedDate;
    }

    public void setInitiatedDate(Date initiatedDate) {
        this.initiatedDate = initiatedDate;
    }

    public Date getLastUpdatedDate() {
        return lastUpdatedDate;
    }

    public void setLastUpdatedDate(Date lastUpdatedDate) {
        this.lastUpdatedDate = lastUpdatedDate;
    }

    public Currency getCurrency() {
        return currency;
    }

    public void setCurrency(Currency currency) {
        this.currency = currency;
    }

    public Wallet getWallet() {
        return wallet;
    }

    public void setWallet(Wallet wallet) {
        this.wallet = wallet;
    }

    public TransferRequestMap getTransferRequestMap() {
        return transferRequestMap;
    }

    public void setTransferRequestMap(TransferRequestMap transferRequestMap) {
        this.transferRequestMap = transferRequestMap;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getToAccountName() {
        return toAccountName;
    }

    public void setToAccountName(String toAccountName) {
        this.toAccountName = toAccountName;
    }

    public TransferTransactionType getTransferTransactionType() {
        return transferTransactionType;
    }

    public void setTransferTransactionType(TransferTransactionType transferTransactionType) {
        this.transferTransactionType = transferTransactionType;
    }

    public Bank getReceivingBank() {
        return receivingBank;
    }

    public void setReceivingBank(Bank receivingBank) {
        this.receivingBank = receivingBank;
    }

    public Double getUserRate() {
        return userRate;
    }

    public void setUserRate(Double userRate) {
        this.userRate = userRate;
    }

    public BigDecimal getChargeAmount() {
        return chargeAmount;
    }

    public void setChargeAmount(BigDecimal chargeAmount) {
        this.chargeAmount = chargeAmount;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public WalletStatus getWalletStatus() {
        return walletStatus;
    }

    public void setWalletStatus(WalletStatus walletStatus) {
        this.walletStatus = walletStatus;
    }

    public String getPaymentRefNumber() {
        return paymentRefNumber;
    }

    public void setPaymentRefNumber(String paymentRefNumber) {
        this.paymentRefNumber = paymentRefNumber;
    }

    public String getPaymentMessage() {
        return paymentMessage;
    }

    public void setPaymentMessage(String paymentMessage) {
        this.paymentMessage = paymentMessage;
    }

    public Integer getNoOfPaymentTrial() {
        return noOfPaymentTrial;
    }

    public void setNoOfPaymentTrial(Integer noOfPaymentTrial) {
        this.noOfPaymentTrial = noOfPaymentTrial;
    }

    public Date getDateFunded() {
        return dateFunded;
    }

    public void setDateFunded(Date dateFunded) {
        this.dateFunded = dateFunded;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public Long getPaymentReferenceId() {
        return paymentReferenceId;
    }

    public void setPaymentReferenceId(Long paymentReferenceId) {
        this.paymentReferenceId = paymentReferenceId;
    }

    public BankDeposit getBankDeposit() {
        return bankDeposit;
    }

    public void setBankDeposit(BankDeposit bankDeposit) {
        this.bankDeposit = bankDeposit;
    }

    public PaymentReference getPaymentReference() {
        return paymentReference;
    }

    public void setPaymentReference(PaymentReference paymentReference) {
        this.paymentReference = paymentReference;
    }

    public Date getDateSettled() {
        return dateSettled;
    }

    public void setDateSettled(Date dateSettled) {
        this.dateSettled = dateSettled;
    }

    public ManagedUser getSettlementInitiatedBy() {
        return settlementInitiatedBy;
    }

    public void setSettlementInitiatedBy(ManagedUser settlementInitiatedBy) {
        this.settlementInitiatedBy = settlementInitiatedBy;
    }

    public ManagedUser getSettlementVerifiedOrDeclinedBy() {
        return settlementVerifiedOrDeclinedBy;
    }

    public void setSettlementVerifiedOrDeclinedBy(ManagedUser settlementVerifiedOrDeclinedBy) {
        this.settlementVerifiedOrDeclinedBy = settlementVerifiedOrDeclinedBy;
    }

    public boolean getSettled() {
        return isSettled;
    }

    public void setSettled(boolean settled) {
        isSettled = settled;
    }

    public String getSortCode() {
        return sortCode;
    }

    public void setSortCode(String sortCode) {
        this.sortCode = sortCode;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(Long transactionId) {
        this.transactionId = transactionId;
    }
}
