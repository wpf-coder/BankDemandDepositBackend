-- 虚表的drop
drop table if exists demand_interest;
drop table if exists transaction_flow;
drop table if exists settlement_interest_log;
drop table if exists card;
-- 以上均依赖card
drop table if exists ebank_user;        -- 依赖user 用户表

-- 以下是依赖 网点 branch 的表
drop table if exists ledger_balance;                -- 这个还依赖 科目 subject 表
drop table if exists ledger_balance_backup;
drop table if exists ledger_flow;

drop table if exists teller;
drop table if exists ledger_record;

-- ----------------------------
-- Table structure for user
-- ----------------------------
drop table if exists  user;
CREATE TABLE user  (
  user_id                         char(22)        NOT NULL comment '身份证',
  user_name                       varchar(20)     NOT NULL,
  user_phone                      char(15)        NOT NULL,
  user_address                    varchar(255)    NOT NULL,
  user_email                      varchar(50),
  user_ebank_state                tinyint         NOT NULL default 1 comment '1未开通、2开通',
  user_mobile_bank_state          tinyint         NOT NULL default 1 comment '1未开通、2开通',
  user_foreign_exchange_state    tinyint         NOT NULL  default 1 comment '1未开通、2开通',
  PRIMARY KEY (user_id),
  unique index (user_id)
);

-- ----------------------------
-- Table structure for ebank_user
-- ----------------------------

CREATE TABLE ebank_user  (
    ebank_user_id                   char(22)         NOT NULL,
    ebank_user_password             varchar(32)         NOT NULL,
    ebank_user_state                tinyint             NOT NULL comment '0 是未登录，1是已登录',

    PRIMARY KEY (ebank_user_id),
    INDEX user_id(ebank_user_id),
    CONSTRAINT ebank_link_user FOREIGN KEY (ebank_user_id) REFERENCES user (user_id)
           ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for card
-- ----------------------------

CREATE TABLE    card  (
  card_id                         char(22)        NOT NULL,
  card_user_id                    char(22)        NOT NULL,
  card_password                   varchar(32)     NOT NULL,
  card_balance                    decimal(17, 2)  NOT NULL,
  card_state                      tinyint         NOT NULL comment '1 是正常，2是久悬',
  card_last_time                  timestamp       NOT NULL,

  PRIMARY KEY (card_id),
  INDEX user_id(card_user_id),
  CONSTRAINT card_link_user FOREIGN KEY (card_user_id) REFERENCES user (user_id)
);




-- ----------------------------
-- Table structure for branch
-- ----------------------------
drop table if exists branch;
CREATE TABLE branch(
    branch_id                       char(20)            not null ,
    branch_name                     varchar(150)        not null,
    branch_address                  varchar(255)        not null,
    branch_phone                    char(15)            not null,
    PRIMARY KEY (branch_id)
);


-- ----------------------------
-- Table structure for teller
-- ----------------------------

CREATE TABLE teller(
    teller_id                       char(20)            not null ,
    teller_password                 varchar(32)         not null,
    teller_branch_id                char(20)         not null,

    PRIMARY KEY (teller_id),
    CONSTRAINT teller_link_branch foreign key (teller_branch_id) references branch(branch_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- -----------------------------
-- table structure of subject
-- ------------------------------
drop table if exists subject;
create table subject(
  subject_serial                    varchar(6)      not null,
  subject_name                      varchar(25)     not null,
  subject_load_relationship         tinyint         not null        comment '0是借、1是贷',

  primary key (subject_serial)
);

-- ----------------------------
-- Table structure for ledger_record
-- ----------------------------

CREATE TABLE ledger_record(
    ledger_record_serial                   int             not null auto_increment,
    ledger_record_branch_id                char(20)        not null,
    ledge_record_subject_serial            varchar(6)      not null default '1001',
    ledger_record_account_time             datetime       not null,

    ledger_record_begin_debtor             DECIMAL(17,2)   not null,
    ledger_record_begin_credit             DECIMAL(17,2)   not null,

    ledger_record_interim_debit	    	decimal(17,2)	not null,
    ledger_record_interim_credit	        decimal(17,2)	not null,

    ledger_record_end_debtor               DECIMAL(17,2)   not null,
    ledger_record_end_credit               DECIMAL(17,2)   not null,

    primary key (ledger_record_serial),
    index(ledger_record_account_time),
    constraint ledger_record_link_branch foreign key (ledger_record_branch_id) references branch(branch_id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

-- ----------------------------
-- Table structure for demand_interest
-- ----------------------------
drop table if exists interest_rate;
CREATE TABLE interest_rate (
    interest_rate_serial	            int	            not null auto_increment,
    interest_rate_code	                varchar(16)	    not null,
    interest_rate_effective_time		date	        not null,
    interest_rate_expiration_time	    date	        not null,
    interest_rate_type	                varchar(4)	    not null
                               comment 'C 是活期，其他暂时不处理',
    interest_rate_base_time		        varchar(4)	    not null default '360'
                               comment '利率的基数，默认360天',
    interest_rate_value		            decimal(13,7)	not null
                               comment '利率值',
    interest_rate_update_time	        datetime	    not null,
    interest_rate_update_operator		varchar(32)	    not null
                               comment '上一次更新的人，默认admitNO:1',
    index(interest_rate_code,interest_rate_effective_time),
    PRIMARY KEY (interest_rate_serial)
);

-- ----------------------------
-- Table structure for settlement_interest_log
-- ----------------------------

CREATE TABLE settlement_interest_log (
    settlement_interest_log_serial                int  auto_increment      NOT NULL,
    settlement_interest_log_time                  datetime        NOT NULL
                                       comment '此次结息的时间',
    settlement_interest_log_card_id               char(22)        NOT NULL,
    settlement_interest_log_interest              decimal(17,2)   NOT NULL,
    settlement_interest_log_cumulative_interest		decimal(17,3)	not null,
    settlement_interest_log_last_time		            datetime	    not null,
    index(settlement_interest_log_card_id,settlement_interest_log_time),
    constraint settlement_interest_log_link_card foreign key (settlement_interest_log_card_id) references card(card_id)
        on delete cascade on update cascade ,
    PRIMARY KEY (settlement_interest_log_serial)
);

-- ----------------------------
-- Table structure for daily_interest_rate
-- ----------------------------

CREATE TABLE demand_interest (
  demand_interest_card_id                 char(22)        NOT NULL,
  demand_interest_cumulative_interest     decimal(17,2)   NOT NULL,
  demand_interest_last_count_time         datetime       NOT NULL,
  demand_interest_last_settle_time        datetime       NOT NULL,
  constraint demand_interest_link_card foreign key (demand_interest_card_id) references card(card_id)
      on delete cascade on update cascade ,
  PRIMARY KEY (demand_interest_card_id)
);


-- 交易流水表

create table transaction_flow(
    transaction_flow_serial		                int	                not null auto_increment                comment '自增ID，流水记录编号',
    transaction_serial                          varchar(36)         not null        comment '对应交易的编号',

    transaction_flow_time		                timestamp	        not null        comment '发生时间',
    transaction_flow_branch_id		            char(20)	        not null        comment '交易发生的网点号',

    transaction_flow_type		                tinyint	            not null        comment '业务类型，以主体的角度',
    transaction_flow_amount	                    decimal(17,2)	    not null        comment '发生金额	',
    transaction_flow_subject_card_balance	            decimal(17,2)       not null        comment '主体交易后的余额	',
    transaction_flow_subject_card_number		char(22)	        not null        comment '主体的银行（卡）账户',

    transaction_flow_counterparty_bank_card_number   char(22)	                comment '交易对象银行（卡）账户',

    transaction_flow_account_time               date                    not null ,

    index(transaction_flow_time),
    -- 联合主键
    primary key(transaction_flow_serial),
    constraint transaction_flow_subject_card_link_card foreign key (transaction_flow_subject_card_number) references card(card_id),
    constraint transaction_flow_counterparty_link_card foreign key (transaction_flow_counterparty_bank_card_number) references card(card_id),
    constraint transaction_flow_link_branch foreign key (transaction_flow_branch_id) references branch(branch_id)
                             on delete cascade on update cascade
);


drop table if exists bank_card_serial;
create table bank_card_serial(
    bank_card_serial_prefix	                char(8)	            not null
                             comment '对应银行卡前缀',
    bank_card_serial_issue_bank		        varchar(30)	        not null
                             comment '发卡行的代号',
    bank_card_serial_relationship		    varchar(4)	        not null
                             comment '本卡与本行的关系,I是本行卡的开头，E是外行',

    primary key (bank_card_serial_prefix),
    unique index(bank_card_serial_prefix)
);

drop table if exists system_information;
create table system_information(
    system_serial               varchar(4)          not null ,
    system_time                 datetime            not null,

    primary key (system_serial)
);

-- 总账余额表
create table ledger_balance(
    ledger_balance_subject_serial           varchar(6)          not null ,
    ledger_balance_balance                  decimal(17,2)       not null ,
    ledger_balance_branch                   char(20)            not null ,
    ledger_balance_state                    tinyint             not null comment '1 是正常，2是关闭' ,
    ledger_balance_account                  varchar(15)         not null default '000000000000000',
    ledger_balance_account_description      varchar(60)         not null default '默认账户' ,

    primary key (ledger_balance_subject_serial,ledger_balance_branch),
    constraint ledger_balance_link_to_branch foreign key (ledger_balance_branch) references branch(branch_id),
    constraint ledger_balance_link_to_subject foreign key (ledger_balance_subject_serial) references subject(subject_serial)
);

-- 总账余额备份表
create table ledger_balance_backup(
    ledger_balance_backup_subject_serial           varchar(6)          not null ,
    ledger_balance_backup_balance                  decimal(17,2)       not null ,
    ledger_balance_backup_branch                   char(20)            not null ,
    ledger_balance_backup_state                    tinyint             not null comment '1 是正常，2是关闭' ,
    ledger_balance_backup_account                  varchar(15)         not null default '000000000000000',
    ledger_balance_backup_account_description      varchar(60)         not null default '默认账户' ,
    ledger_balance_backup_date                     date                not null,

    index(ledger_balance_backup_date),
    primary key (ledger_balance_backup_date,ledger_balance_backup_subject_serial,ledger_balance_backup_branch),
    constraint ledger_balance_backup_link_to_branch foreign key (ledger_balance_backup_branch) references branch(branch_id),
    constraint ledger_balance_backup_link_to_subject foreign key (ledger_balance_backup_subject_serial) references subject(subject_serial)
);

-- 总账流水表
create table ledger_flow(
    ledger_flow_serial              int             not null auto_increment,
    ledger_flow_subject_serial      varchar(6)      not null ,
    ledger_flow_load_relationship   tinyint         not null ,
    ledger_flow_transaction_serial  varchar(36)     not null ,
    ledger_flow_amount              decimal(17,2)   not null ,
    ledger_flow_branch_id           char(20)        not null ,
    ledger_flow_date                date            not null ,

    primary key (ledger_flow_serial),
    constraint ledger_flow_link_to_branch    foreign key (ledger_flow_branch_id) references branch(branch_id),
    constraint ledger_flow_link_to_subject    foreign key (ledger_flow_subject_serial) references subject(subject_serial),
    index(ledger_flow_date)
);

-- initial origin data

-- initial system_information
insert into system_information(system_serial, system_time)
values
    ('YSU',now());

-- initial bank account information

insert into user (user_id, user_name, user_phone, user_address, user_ebank_state, user_mobile_bank_state, user_foreign_exchange_state)
values
    ('FFFFFFFFFFFFFFFFFF','self bank','22222222222','燕山大学',1,1,1),
    ('123456789012345678','judy','11111111111','燕山大学',2,1,1);

insert into ebank_user(ebank_user_id, ebank_user_password, ebank_user_state)
values
    ('123456789012345678','e10adc3949ba59abbe56e057f20f883e',0);

insert into card (card_id, card_user_id, card_password, card_balance,
                    card_state, card_last_time)
values
    ('8888880000000000000','FFFFFFFFFFFFFFFFFF','123456',5000000000,1,'2021-9-8'),
    ('8888880000000000001','123456789012345678','e10adc3949ba59abbe56e057f20f883e',1000000,1,'2021-9-1');

-- initial bank net
insert into branch (branch_id, branch_name, branch_address, branch_phone)
values
       ('YSU-BANK','燕山大学总行','燕山大学','1111111111'),
       ('NETWORK-BANK','网上银行网点','web','1111111111');

insert into teller (teller_id, teller_password, teller_branch_id)
values ('root','e10adc3949ba59abbe56e057f20f883e','YSU-BANK');

insert into bank_card_serial(bank_card_serial_prefix, bank_card_serial_issue_bank,
                            bank_card_serial_relationship)
values ('888888','燕山大学-软件学院民生银行','I');

insert into ledger_record(ledger_record_branch_id,ledger_record_account_time,
                        ledger_record_begin_debtor, ledger_record_begin_credit,
                        ledger_record_interim_debit,ledger_record_interim_credit,
                      ledger_record_end_debtor,ledger_record_end_credit)
values ('YSU-BANK',date_add(now(),interval -1 day),5000000000,0,0,0,5000000000,0);

-- initial subject table
insert into subject(subject_serial, subject_name, subject_load_relationship)
values
    ('1001','现金总账',0),
    ('215001','客户活期存款账户',1),
    ('260001','应付利息',1),
    ('640002','利息支出',0);



-- initial interest rate table
insert into interest_rate(interest_rate_code, interest_rate_effective_time,
                            interest_rate_expiration_time,  interest_rate_type,
                            interest_rate_base_time, interest_rate_value,
                            interest_rate_update_time, interest_rate_update_operator)
values ('DEMAND_RATE','2021-9-1','2040-9-1','C','360',0.035,'2021-8-30','root');




