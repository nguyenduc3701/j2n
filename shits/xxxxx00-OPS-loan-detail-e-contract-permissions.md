# E-Contract Authorization Configuration

## 1. Create scopes

- `e-contract:view-sensitive-data`: _Clients_ > _OPS_ > _Authorization_ > _Scopes_ > _Create authorization scope_
  - In _Name_, input `e-contract:view-sensitive-data` then click _Save_
- `e-contract:cancel`: _Clients_ > _OPS_ > _Authorization_ > _Scopes_ > _Create authorization scope_
  - In _Name_, input `e-contract:cancel` then click _Save_
- `e-contract:disable`: _Clients_ > _OPS_ > _Authorization_ > _Scopes_ > _Create authorization scope_
  - In _Name_, input `e-contract:disable` then click _Save_
- `e-contract:re-create`: _Clients_ > _OPS_ > _Authorization_ > _Scopes_ > _Create authorization scope_
  - In _Name_, input `e-contract:re-create` then click _Save_
- `e-contract:sync`: _Clients_ > _OPS_ > _Authorization_ > _Scopes_ > _Create authorization scope_
  - In _Name_, input `e-contract:sync` then click _Save_
- `e-contract:view`: _Clients_ > _OPS_ > _Authorization_ > _Scopes_ > _Create authorization scope_
  - In _Name_, input `e-contract:view` then click _Save_
- `e-contract:manage`: _Clients_ > _OPS_ > _Authorization_ > _Scopes_ > _Create authorization scope_
  - In _Name_, input `e-contract:manage` then click _Save_

---

## 2. Update resource

- Update `loan-detail` resource: _Clients_ > _OPS_ > _Authorization_ > _Resources_ > _loan-detail_ > _Edit_

  - In _Scopes_, add the following newly created scopes:
    - `e-contract:view-sensitive-data`
    - `e-contract:cancel`
    - `e-contract:disable`
    - `e-contract:re-create`
    - `e-contract:sync`
    - `e-contract:view`
  - Click _Save_

  - Update `rate_profile` resource: _Clients_ > _OPS_ > _Authorization_ > _Resources_ > _rate_profile_ > _Edit_
  - In _Scopes_, add the following newly created scopes:
    - `e-contract:manage`
  - Click _Save_

---

## 3. Create permissions

- `CAN_VIEW_E_CONTRACT_LOAN_DETAIL`: _Clients_ > _OPS_ > _Authorization_ > _Create permissions_ > _Create scope-based Permission_

  - In _Name_, input `CAN_VIEW_E_CONTRACT_LOAN_DETAIL`
  - In _Resource_, add `loan-detail`
  - In _Scopes_, add `e-contract:view`
  - In _Policies_, add `Shop Staff Policy`, `Shop Manager Policy`, `ASM Policy`, `Report Policy`, `Operation Policy`, `Portfolio Manager Policy`, `Loan Adjudicator Policy`, `Telesale Policy`, `Collection Policy`, `Lending Product Manager Policy`, `Finance Manager Policy`, `TechOps Policy`, `Credit Risk Policy`
  - In _Decision strategy_, select `Affirmative`
  - Click _Save_

- `CAN_VIEW_CONTRACT_E_CONTRACT_LOAN_DETAIL`: _Clients_ > _OPS_ > _Authorization_ > _Create permissions_ > _Create scope-based Permission_

  - In _Name_, input `CAN_VIEW_CONTRACT_E_CONTRACT_LOAN_DETAIL`
  - In _Resource_, add `loan-detail`
  - In _Scopes_, add `e-contract:view-sensitive-data`
  - In _Policies_, add `Shop Staff Policy`, `Shop Manager Policy`, `ASM Policy`, `Report Policy`, `Operation Policy`, `Portfolio Manager Policy`, `Loan Adjudicator Policy`, `Telesale Policy`, `Collection Policy`, `Lending Product Manager Policy`, `Finance Manager Policy`, `TechOps Policy`, `Credit Risk Policy`
  - In _Decision strategy_, select `Affirmative`
  - Click _Save_

- `CAN_SYNC_E_CONTRACT_LOAN_DETAIL`: _Clients_ > _OPS_ > _Authorization_ > _Create permissions_ > _Create scope-based Permission_

  - In _Name_, input `CAN_SYNC_E_CONTRACT_LOAN_DETAIL`
  - In _Resource_, add `loan-detail`
  - In _Scopes_, add `e-contract:sync`
  - In _Policies_, add `Shop Staff Policy`, `Shop Manager Policy`, `ASM Policy`, `Report Policy`, `Operation Policy`, `Portfolio Manager Policy`, `Loan Adjudicator Policy`, `Telesale Policy`, `Collection Policy`, `Lending Product Manager Policy`, `Finance Manager Policy`, `TechOps Policy`, `Credit Risk Policy`
  - In _Decision strategy_, select `Affirmative`
  - Click _Save_

- `CAN_RE_CREATE_E_CONTRACT_LOAN_DETAIL`: _Clients_ > _OPS_ > _Authorization_ > _Create permissions_ > _Create scope-based Permission_

  - In _Name_, input `CAN_RE_CREATE_E_CONTRACT_LOAN_DETAIL`
  - In _Resource_, add `loan-detail`
  - In _Scopes_, add `e-contract:re-create`
  - In _Policies_, add `Shop Staff Policy`, `Shop Manager Policy`, `Operation Policy`, `TechOps Policy`
  - In _Decision strategy_, select `Affirmative`
  - Click _Save_

- `CAN_DISABLE_E_CONTRACT_LOAN_DETAIL`: _Clients_ > _OPS_ > _Authorization_ > _Create permissions_ > _Create scope-based Permission_

  - In _Name_, input `CAN_DISABLE_E_CONTRACT_LOAN_DETAIL`
  - In _Resource_, add `loan-detail`
  - In _Scopes_, add `e-contract:disable`
  - In _Policies_, add `Operation Policy`, `TechOps Policy`
  - In _Decision strategy_, select `Affirmative`
  - Click _Save_

- `CAN_CANCEL_E_CONTRACT_LOAN_DETAIL`: _Clients_ > _OPS_ > _Authorization_ > _Create permissions_ > _Create scope-based Permission_

  - In _Name_, input `CAN_CANCEL_E_CONTRACT_LOAN_DETAIL`
  - In _Resource_, add `loan-detail`
  - In _Scopes_, add `e-contract:cancel`
  - In _Policies_, add `Operation Policy`, `TechOps Policy`
  - In _Decision strategy_, select `Affirmative`
  - Click _Save_

- `CAN_MANAGE_E_CONTRACT_RATE_PROFILE`: _Clients_ > _OPS_ > _Authorization_ > _Create permissions_ > _Create scope-based Permission_
  - In _Name_, input `CAN_MANAGE_E_CONTRACT_RATE_PROFILE`
  - In _Resource_, add `rate_profile`
  - In _Scopes_, add `e-contract:manage`
  - In _Policies_, add `Lending Product Manager Policy`
  - In _Decision strategy_, select `Affirmative`
  - Click _Save_
