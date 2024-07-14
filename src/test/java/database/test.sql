SELECT branch_id as subject_balance_branch,
		subject_serial as ledger_balance_subject_serial
from branch,subject;

select base.*,ledger_balance_account, ledger_balance_balance, ledger_balance_state,  ledger_balance_account_description
from (SELECT branch.branch_id as subject_balance_branch,
		subject.subject_serial as ledger_balance_subject_serial
		from branch,subject) as base left join ledger_balance
				on base.ledger_balance_subject_serial = ledger_balance.ledger_balance_subject_serial;

-- 查询每个机构的每一个总账的今日总账流水信息。
select *
from (
         select base.*,ledger_balance_account, ledger_balance_balance, ledger_balance_state, ledger_balance_account_description
         from
             (SELECT branch.branch_id as ledger_balance_branch,
                     subject.subject_serial as ledger_balance_subject_serial
              from branch,subject
             ) as base left join ledger_balance
                                 on base.ledger_balance_subject_serial = ledger_balance.ledger_balance_subject_serial
                                     and base.ledger_balance_branch = ledger_balance.ledger_balance_branch
     )as bb left join ledger_flow as f
                      on datediff(f.ledger_flow_date,'') <= 1
                      and bb.ledger_balance_branch = f.ledger_flow_branch_id
                          and bb.ledger_balance_subject_serial = f.ledger_flow_subject_serial;