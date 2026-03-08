package com.jpmc.midascore.entity;


import jakarta.persistence.Entity;
import jakarta.persistence.*;

@Entity
public class TransactionRecord {

    @Id
    @GeneratedValue()
    private Long id;

    @ManyToOne
    private UserRecord sender;
    @ManyToOne
    private UserRecord receiver;

    private float amount,incentive;

    public TransactionRecord() {}

    public TransactionRecord(UserRecord sender, UserRecord receiver, float amount, float incentive) {
        this.sender = sender;
        this.receiver = receiver;
        this.amount = amount;
        this.incentive = incentive;
    }
    public Long getSender() {return this.sender.getId();}
    public Long getReceiver() {return this.receiver.getId();}
    public float getAmount() {return this.amount;}
    public float getIncentive() {return this.incentive;}

}
