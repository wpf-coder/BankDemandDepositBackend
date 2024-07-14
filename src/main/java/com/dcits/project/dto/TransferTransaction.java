package com.dcits.project.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class TransferTransaction {
    private String payerCardId;
    private String payerCardPassword;
    private String payeeName;
    private String payeeCardId;
    private BigDecimal amount;

    @Override
    public String toString() {
        return "TransferTransaction{" +
                "payerCardId='" + payerCardId + '\'' +
                ", payerCardPassword='" + payerCardPassword + '\'' +
                ", payeeName='" + payeeName + '\'' +
                ", payeeCardId='" + payeeCardId + '\'' +
                ", amount=" + amount +
                '}';
    }

    public boolean checkNotNull(){
        return payeeCardId!=null && payerCardId !=null
                && payeeName!=null && payerCardPassword !=null
                && amount!=null;
    }
}
