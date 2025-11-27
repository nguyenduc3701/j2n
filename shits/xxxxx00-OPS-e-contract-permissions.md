# E-Contract Authorization Configuration

## 1. Create Resources

### 1.1 Resource: `e_contract`
- Path: Clients > OPS > Authorization > Resources > Create resource
    - **Name**: `e_contract`
    - **Scopes**:
        - `view`
    - Click **Save**e

---

### 1.2 Resource: `e_contract:request`
- Path: Clients > OPS > Authorization > Resources > Create resource
    - **Name**: `e_contract:request`
    - **Scopes**:
        - `update`
        - `create`
        - `edit`
        - `delete`
    - Click **Save**

---

## 2. Create Permissions

### 2.1 Permission: `CAN_VIEW_E_CONTRACT`
- Path: Clients > OPS > Authorization > Create permissions > Create scope-based Permission
    - **Name**: `CAN_VIEW_E_CONTRACT`
    - **Resource**: `e_contract`
    - **Scope**: `view`
    - **Policies**: `Loan Adjudicator Policy`,`Operation Policy`,`Credit Risk Policy`,`Report Policy`,`Lending Product Manager Policy`,`TechOps Policy`
    - **Decision strategy**: Affirmative
    - Click **Save**

---

### 2.2 Permission: `CAN_SEARCH_E_CONTRACT_REQUEST`
- Path: Clients > OPS > Authorization > Create permissions > Create scope-based Permission
    - **Name**: `CAN_SEARCH_E_CONTRACT_REQUEST`
    - **Resource**: `e_contract:request`
    - **Scope**: `search`
    - **Policies**:`Loan Adjudicator Policy`,`Operation Policy`,`Credit Risk Policy`,`Report Policy`,`Lending Product Manager Policy`,`TechOps Policy`
    - **Decision strategy**: Affirmative
    - Click **Save**

---

### 2.3 Permission: `CAN_CREATE_E_CONTRACT_REQUEST`
- Path: Clients > OPS > Authorization > Create permissions > Create scope-based Permission
    - **Name**: `CAN_CREATE_E_CONTRACT_REQUEST`
    - **Resource**: `e_contract:request`
    - **Scope**: `create`
    - **Policies**:`Operation Policy`
    - **Decision strategy**: Affirmative
    - Click **Save**

---

### 2.4 Permission: `CAN_UPDATE_E_CONTRACT_REQUEST`
- Path: Clients > OPS > Authorization > Create permissions > Create scope-based Permission
    - **Name**: `CAN_UPDATE_E_CONTRACT_REQUEST`
    - **Resource**: `e_contract:request`
    - **Scope**: `update`
    - **Policies**: `Operation Policy`
    - **Decision strategy**: Affirmative
    - Click **Save**

---

### 2.5 Permission: `CAN_DELETE_E_CONTRACT_REQUEST`
- Path: Clients > OPS > Authorization > Create permissions > Create scope-based Permission
    - **Name**: `CAN_DELETE_E_CONTRACT_REQUEST`
    - **Resource**: `e_contract:request`
    - **Scope**: `delete`
    - **Policies**: `Operation Policy`
    - **Decision strategy**: Affirmative
    - Click **Save**
