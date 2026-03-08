package com.jpmc.midascore.component;

import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Balance;
import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class TransactionListener {
    private final DatabaseConduit databaseConduit;

    public TransactionListener(DatabaseConduit databaseConduit) {
        this.databaseConduit = databaseConduit;
    }


    @KafkaListener(topics="${general.kafka-topic}", groupId = "g1")
    public void listen(Transaction transaction) {
//        System.out.println("Transaction: " + transaction);
        UserRecord sender=databaseConduit.findUser(transaction.getSenderId());
        UserRecord receiver=databaseConduit.findUser(transaction.getRecipientId());
        if(sender==null || receiver==null) return;
        if(sender.getBalance() < transaction.getAmount()) return;

        RestTemplate restTemplate = new RestTemplate();

        Balance incentive = restTemplate.postForObject(
                "http://localhost:8080/incentive",
                transaction,
                Balance.class
        );

        float incentiveAmount = incentive.getAmount();

        sender.setBalance(sender.getBalance() - transaction.getAmount());
        receiver.setBalance(receiver.getBalance() + transaction.getAmount()+incentiveAmount);
        databaseConduit.save(sender);
        databaseConduit.save(receiver);

        TransactionRecord transactionRecord=new TransactionRecord(sender,receiver, transaction.getAmount(),incentiveAmount);
        databaseConduit.save(transactionRecord);

    }
}
