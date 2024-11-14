package bank.app.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class TransactionResponseDto {
        private final long transactionId;
        private final Long sender;
        private final Long receiver;
        private final double amount;
        private final String comment;
        private final String transactionTime;
        private final String transactionStatus;
        private final String  transactionType;

    public TransactionResponseDto(long transactionId, Long sender, Long receiver, double amount, String comment,
                                  String transactionTime, String transactionStatus, String transactionType) {
        this.transactionId = transactionId;
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.comment = comment;
        this.transactionTime = transactionTime;
        this.transactionStatus = transactionStatus;
        this.transactionType = transactionType;
    }



}
