package bank.app.service.impl;

import bank.app.model.entity.Transaction;
import bank.app.repository.TransactionRepository;
import bank.app.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
    @Autowired
    private TransactionRepository transactionRepository;

    @Override
    public List<Transaction> getTransactionsByAccountId(Long accountId) {
        List<Transaction> transactions = transactionRepository.findBySenderIdOrReceiverId(accountId, accountId);
        return transactions;
    }

    @Override
    public List<Transaction> getTransactionsLastMonthByAccountId(Long accountId) {

        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);
        return transactionRepository.findBySenderIdOrReceiverIdAndTransactionDateBetween(accountId,accountId, startDate, endDate);
    }

}