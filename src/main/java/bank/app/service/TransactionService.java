package bank.app.service;

import bank.app.dto.TransactionDto;
import bank.app.model.entity.Transaction;

import java.util.List;

public interface TransactionService {
    List<Transaction> getTransactionsByAccountId(Long accountId);
    List<Transaction> getTransactionsLastMonthByAccountId(Long accountId);
}
