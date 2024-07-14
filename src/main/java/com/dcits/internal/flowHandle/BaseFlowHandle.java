package com.dcits.internal.flowHandle;

import com.dcits.project.pojo.BankCardSerial;
import com.dcits.project.pojo.Card;
import com.dcits.project.pojo.LedgerFlow;
import com.dcits.project.pojo.TransactionFlow;
import com.dcits.project.service.TransactionFlowService;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;


public abstract class BaseFlowHandle {
    public static final int deposit 	= 1;
    public static final int withdrawal = 2;			//取款
    public static final int remittance = 4;			//汇款
    public static final int receive_Payment = 8;	//收款  collection
    public static final int transfer = remittance | receive_Payment;
    public static final int interest_Settlement = 16;	//利息结算

    /**
     * 获取一笔交易的唯一编号，注意一笔交易的参与者（目前只有两人）的流水记录
     * 交易编号一定要相同。
     * @how:
     *      使用 UUID工具类的 random UUID算法生成，生成字符数为 36
     * @return:
     *       java.lang.String
     **/
    protected static String getUniqueSerialOfTransaction(){
        return UUID.randomUUID().toString();
    }


    /**
     * 获取本行的现金账户。该功能目前只是模拟，且仅提供存取款、结息使用。
     * @how:
     *      目前只返回 静态返回唯一值。
     * @return:
     *       java.lang.String
     **/
    protected static String getCashCardNumber(){
        return "8888880000000000000";
    }

    /**
     * 获得网上银行的网点号
     * @how:
     *      现在仅静态返回。
     * @return:
     *       java.lang.String
     **/
    protected static String getOnlineOutlet(){
        return "NETWORK-BANK";
    }





    /**
     * 根据type参数获取 该类族的实例
     * @how:
     * @return:
     *       com.dcits.internal.transaction.BaseTransaction
    **/
    static public BaseFlowHandle getInstance(int type){
        BaseFlowHandle baseTransaction = null;
        switch (type){
            //汇款
            case BaseFlowHandle.remittance:{
                baseTransaction = new RemittanceFlowHandle();
                break;
            }
            //收款
            case BaseFlowHandle.receive_Payment:{
                baseTransaction = new CollectionFlowHandle();
                break;
            }
            //存钱
            case BaseFlowHandle.deposit:{
                baseTransaction = new DepositFlowHandle();
                break;
            }
            //取钱
            case BaseFlowHandle.withdrawal:{
                baseTransaction = new WithdrawalFlowHandle();
                break;
            }
            case BaseFlowHandle.interest_Settlement:{
                baseTransaction = new InterestSettlementFlowHandle();
                break;
            }
            default:{
                throw new IllegalArgumentException("没有该类型的实例");
            }
        }
        return baseTransaction.setType(type);
    }

    protected BaseFlowHandle(){
        transactionFlow = new TransactionFlow();
        ledgerFlow = new LedgerFlow();
    }

    protected TransactionFlow transactionFlow;
    protected LedgerFlow ledgerFlow;

    protected java.sql.Date accountDate;

    //用于存储流水的依赖。
    protected TransactionFlowService service;



    /**
     * 根据配置，记录交易流水。调用方注意要开启事务。
     * @how:
     *          1、调用 logHouseholdFlow 来生成分户流水；该过程并不能改变流水信息
     *          2、最后生成总账流水
     * @return:
     *       boolean
    **/
    public TransactionFlow recordFlow(TransactionFlowService flowService){
        return this.recordFlow(flowService,flowService.getSystemDate());
    }

    public TransactionFlow recordFlow(TransactionFlowService flowService,Date accountDate){
        this.accountDate = new java.sql.Date(accountDate.getTime());
        this.service = flowService;

        //validation
        validateCommonProperty();
        validateSpecialProperty();

        //填充一部分字段
        filledCommonProperty();
        filledSpecialProperty();

        //记录流水
        logHouseholdFlow();
        logLedgerFlow();

        return transactionFlow;
    }


    //*********************默认实现方法区**********************//
    /**
     * 检查一些通用的必须字段。例如金额，主体的余额、银行卡卡号。
     * @Notice:
     *        交易编号、时间、网点号、对象账号 均可能有默认值。
     *        交易编号默认填充。
     * @return:
     *       void
    **/
    protected void validateCommonProperty(){
        if(transactionFlow.getAmount()==null)
            throw new FlowHandleException("amount of transaction must be provided!");
        if(transactionFlow.getSubjectCardBalance() == null)
            throw new FlowHandleException("flow subject's balance is null");
        if(!Card.validateCardId(transactionFlow.getSubjectCardNumber()))
            throw new FlowHandleException("card number of subject is null or format illegal");

    }

    /**
     * 填充一些通用的空字段。
     * @how:
     *      目前填充 交易编号、交易的会计时间。 总账流水的 会计时间 以及 交易编码
     * @return:
     *       void
    **/
    protected void filledCommonProperty(){
        if(transactionFlow.getTransactionSerial() == null){
            transactionFlow.setTransactionSerial(BaseFlowHandle.getUniqueSerialOfTransaction());
            ledgerFlow.setTransactionSerial(transactionFlow.getTransactionSerial());
        }
        ledgerFlow.setDate(accountDate);
        transactionFlow.setAccountDate(accountDate);
    }

    protected void insertLedgerFlow(){
        this.service.addOneLedgerFlow(this.ledgerFlow);
    }

    //*********************子类要实现的方法**********************//
    /**
     * 子类按照交易的特殊性，检验transactionFlow中是否提供必要的字段信息。
     * @return:
     *       void
    **/
    abstract protected void validateSpecialProperty();

    /**
     * 子类按照交易的特殊性，以默认值填充transactionFlow中的空字段。
     * @return:
     *       void
    **/
    abstract protected void filledSpecialProperty();

    /**
     * 记录分户流水，当前默认在 检验字段与填充默认字段后，直接记录分户流水。
     * 若子类有特殊的行为，那么需要重写。
     * @return:
     *       void
    **/
    protected void logHouseholdFlow(){
        this.service.addOneTransactionFlow(transactionFlow);
    }

    /**
     * 记录总账流水。
     * @how:
     * @return:
     *       void
    **/
    abstract protected void logLedgerFlow();



    //*****************************配置本次交易的信息************************//
    public BaseFlowHandle changeType(int type){
        BaseFlowHandle _new = BaseFlowHandle.getInstance(type);
        this.transactionFlow.setType(type);
        _new.transactionFlow = this.transactionFlow;
        _new.ledgerFlow = this.ledgerFlow;
        this.transactionFlow = null;
        this.ledgerFlow = null;
        return _new;
    }

    protected BaseFlowHandle setType(int type) {
        transactionFlow.setType(type);
        return this;
    }

    public BaseFlowHandle setTime(Timestamp time) {
        transactionFlow.setTime(time);
        return this;
    }

    public BaseFlowHandle setTime(Date time) {
        transactionFlow.setTime(new Timestamp(time.getTime()));
        return this;
    }

    public BaseFlowHandle setBranchId(String branchId) {
        transactionFlow.setBranchId(branchId);
        ledgerFlow.setBranchId(branchId);
        return this;
    }

    public BaseFlowHandle setSubjectCardNumber(String subjectCardNumber) {
        transactionFlow.setSubjectCardNumber(subjectCardNumber);
        return this;
    }

    public BaseFlowHandle setSubjectBalance(BigDecimal amount){
        transactionFlow.setSubjectCardBalance(amount);
        return this;
    }

    public BaseFlowHandle setAmount(BigDecimal amount) {
        transactionFlow.setAmount(amount);
        ledgerFlow.setAmount(amount);
        return this;
    }

    public BaseFlowHandle setCounterpartyCardNumber(String counterpartyCardNumber) {
        transactionFlow.setCounterpartyCardNumber(counterpartyCardNumber);
        return this;
    }

    //********************内部配置总账流水******************//
    protected BaseFlowHandle setAccountSerial(String subjectSerial) {
        ledgerFlow.setSubjectSerial(subjectSerial);
        return this;
    }

    protected BaseFlowHandle setLoadRelationship(int loadRelationship) {
        ledgerFlow.setLoadRelationship(loadRelationship);
        return this;
    }



}
