package bank.app.service.impl;

import bank.app.dto.TransactionRequestDto;
import bank.app.dto.TransactionResponseDto;
import bank.app.exeptions.BalanceException;
import bank.app.exeptions.TransactionNotFoundException;
import bank.app.exeptions.TransactionTypeException;
import bank.app.mapper.TransactionMapper;
import bank.app.model.entity.Account;
import bank.app.model.entity.Transaction;
import bank.app.model.entity.TransactionType;
import bank.app.model.enums.TransactionStatus;
import bank.app.repository.AccountRepository;
import bank.app.repository.TransactionRepository;
import bank.app.repository.TransactionTypeRepository;
import bank.app.service.AccountService;
import bank.app.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionTypeRepository transactionTypeRepository;
    private final AccountService accountService;
    private final AccountRepository accountRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Transaction getTransactionById(Long id){
        if(id == null){
            throw new IllegalArgumentException("Transaction id can not be null");
        }
        return transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with id " + id + " not founded") );
    }

    @Override
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    @Override
    public void delete(Long id) {
        Transaction transaction = transactionRepository.findById(id)
                .orElseThrow(() -> new TransactionNotFoundException("Transaction with id " + id + " not found "));
        transactionRepository.deleteById(id);
    }


    @Override
    public List<TransactionResponseDto> getTransactionsByAccountId(Long accountId) {
        List<Transaction> listTransactions = transactionRepository.findBySenderIdOrReceiverId(accountId, accountId);
        return transactionMapper.adjustedAmountsInTransactions(listTransactions,accountId);
    }

    @Override
    public List<TransactionResponseDto> getTransactionsLastMonthByAccountId(Long accountId) {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(30);
        return transactionMapper.adjustedAmountsInTransactions(
                transactionRepository.findBySenderIdOrReceiverIdAndTransactionDateBetween(accountId,accountId, startDate, endDate),
                accountId);
    }

    @Override
    public Transaction addNewTransaction(TransactionRequestDto transactionRequestDto) {

        Account sender = accountService.getAccountById(transactionRequestDto.getSender());
        Account receiver = accountService.getAccountById(transactionRequestDto.getReceiver());
        Account bank = accountService.getBankAccount();

        accountService.checkAccount(bank);
        accountService.checkAccount(sender);
        accountService.checkAccount(receiver);

        TransactionType transactionType = transactionTypeRepository.findByTransactionTypeName(transactionRequestDto.getTransactionType())
                .orElseThrow(() ->
                        new TransactionTypeException(String.format("Transaction Type %s not found", transactionRequestDto.getTransactionType())));


        Transaction transaction = new Transaction(sender, receiver, transactionRequestDto.getAmount(),
                transactionRequestDto.getComment(), TransactionStatus.INITIALIZED, transactionType);

        transaction.setTransactionStatus(TransactionStatus.PROCESSING);
        transaction.setFee(transactionType.getTransactionFee()* transactionRequestDto.getAmount()/100);
        transactionRepository.save(transaction);

        double totalSum = transactionRequestDto.getAmount() + transaction.getFee();

        if (totalSum > sender.getBalance()) {
            transaction.setTransactionStatus(TransactionStatus.FAILED);
            transaction.setComment(transactionRequestDto.getComment() + " - Not enough balance");
            transactionRepository.save(transaction);  // Save the failed transaction
            throw new BalanceException("Not enough balance on account " + transactionRequestDto.getSender());
        }

        sender.setBalance(sender.getBalance() - totalSum);
        receiver.setBalance(receiver.getBalance() + transactionRequestDto.getAmount());
        bank.setBalance(bank.getBalance() + transaction.getFee());

        transaction.setTransactionStatus(TransactionStatus.COMPLETED);
        Transaction savedTransaction = transactionRepository.save(transaction);

        accountRepository.save(sender);
        accountRepository.save(receiver);
        accountRepository.save(bank);

        return savedTransaction;
    }
}
