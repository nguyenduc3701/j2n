# Flowable – Keycloak Authorization Setup (v24)

## 1. Create Realm

- Go to **Keycloak Admin Console**
- Click **Create Realm**
  - **Realm name**: `Flowable`
  - Click \*\*Create`

---

## 2. Create Client: Flowable UI

- Navigate to: **Clients → Create client → General settings**

  - **Client ID**: `flowable-ui`
  - **Client Name**: `Flowable UI`
  - **Client type**: OpenID Connect
  - Click **Next**

- Navigate to: **Clients → Create client → Capability config**

  - **Client authentication**: `ON`
  - Click **Next**

- Navigate to: **Clients → Create client → Login settings**
  - **Root URL**: `http://localhost:6660`
  - **Home URL**: `http://localhost:6660/flowable-ui`
  - **Valid Redirect URIs**:
    - `http://localhost:8080/*`
    - `http://localhost:8081/*`
    - `http://localhost:6060/*`
  - **Web Origins**:
    - `*`
  - Click **Save**

---

## 3. Realm Settings

- Navigate to **Realm Settings**
- Set:
  - **Frontend URL**: `http://localhost:8081/auth`

---

## 4. Create Realm Roles

Navigate to: **Realm Roles → Create Role**

Create the following roles:

- `access-modeler`
- `access-admin`
- `access-idm`
- `access-task`
- `access-rest-api`

---

## 5. Create Groups

Navigate to: **Groups → Create group**

Create groups:

- `Admin`
- `Taskers`
- `Modelers`
- `RestApi`

---

## 6. Assign Roles to Groups

### Group: Admin

- `access-admin`
- `access-modeler`
- `access-task`
- `access-rest-api`
- `access-idm`

### Group: Taskers

- `access-task`

### Group: Modelers

- `access-modeler`

### Group: RestApi

- `access-rest-api`

---

## 7. Create Users

- Navigate to **Users → Create new user**
- Fill basic information
- Save

### Set Credentials

- Go to **Credentials**
- Set password
- Disable **Temporary**

### Assign Group

- Navigate to **Groups → Create group**
- **Group name**: `Admin`, `Taskers`, `Modelers`, `RestApi`

---

## 8. User Federation – LDAP Configuration

### 8.1 Add LDAP Provider

Navigate to: **User Federation → Add new provider → ldap**

- **Name**: `OpenLDAP`
- **Connection URL**: `ldap://openldap:389`
- **Bind Type**: `simple`
- **Bind DN**: `cn=admin,dc=flowable,dc=com`
- **Bind Credential**: `admin_password`
- **Edit Mode**: `READ_ONLY`

---

### 8.2 LDAP User Settings

- **Users DN**: `ou=users,dc=flowable,dc=com`
- **Username LDAP Attribute**: `uid`
- **RDN LDAP Attribute**: `uid`
- **UUID LDAP Attribute**: `entryUUID`
- **User Object Classes**:
  - `inetOrgPerson`
  - `organizationalPerson`
  - `top`
- **Trust Email**: `ON`

---

## 9. LDAP Group Mapper

Navigate to: **User Federation → OpenLDAP → Mappers → Create Mapper**

- **Name**: `group-ldap-mapper`
- **Mapper Type**: `group-ldap-mapper`
- **LDAP Groups DN**: `ou=groups,dc=flowable,dc=com`
- **Group Name LDAP Attribute**: `cn`
- **Group Object Classes**: `groupOfNames`
- **Membership LDAP Attribute**: `member`
- **Membership Attribute Type**: `DN`
- **Membership User LDAP Attribute**: `uid`
- **Mode**: `READ_ONLY`
- **Member-Of LDAP Attribute**: `memberOf`

---

## 10. Configure Client Scopes – flowable-ui-dedicated

Navigate to:  
**Clients → flowable-ui → Client Scopes → flowable-ui-dedicated**

### Add Mapper – Group Membership

- Click **Add mapper → By configuration**
- Select **Group Membership**
  - **Token Claim Name**: `userGroups`
  - **Full group path**: `OFF`
- Save

---

### Add Mapper – User Realm Role

- Click **Add mapper → By configuration**
- Select **User Realm Role**
  - **Token Claim Name**: `roles`
- Save

---
