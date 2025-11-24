/* ============================================
   LẤY ROLE IDs
============================================= */
SET @ROLE_ADMIN_ID = (SELECT id FROM roles WHERE name = 'ADMIN' LIMIT 1);
SET @ROLE_RECRUITER_ID = (SELECT id FROM roles WHERE name = 'RECRUITER' LIMIT 1);
SET @ROLE_RENTER_ID = (SELECT id FROM roles WHERE name = 'RENTER' LIMIT 1);
SET @ROLE_VISITER_ID = (SELECT id FROM roles WHERE name = 'VISITER' LIMIT 1);

/* ============================================
   LẤY PERMISSION IDs
============================================= */
SET @CAN_VIEW_ABOUT_PAGE = (SELECT id FROM permissions WHERE name = 'CAN_VIEW_ABOUT_PAGE' LIMIT 1);
SET @CAN_VIEW_SYSTEM_DESIGN_PAGE = (SELECT id FROM permissions WHERE name = 'CAN_VIEW_SYSTEM_DESIGN_PAGE' LIMIT 1);
SET @CAN_VIEW_ROOM_PAGE = (SELECT id FROM permissions WHERE name = 'CAN_VIEW_ROOM_PAGE' LIMIT 1);
SET @CAN_VIEW_STORE_PAGE = (SELECT id FROM permissions WHERE name = 'CAN_VIEW_STORE_PAGE' LIMIT 1);
SET @CAN_VIEW_MANAGEMENT_PAGE = (SELECT id FROM permissions WHERE name = 'CAN_VIEW_MANAGEMENT_PAGE' LIMIT 1);

SET @CAN_ASSIGN_ROLE = (SELECT id FROM permissions WHERE name = 'CAN_ASSIGN_ROLE' LIMIT 1);
SET @CAN_CREATE_USER = (SELECT id FROM permissions WHERE name = 'CAN_CREATE_USER' LIMIT 1);
SET @CAN_UPDATE_USER = (SELECT id FROM permissions WHERE name = 'CAN_UPDATE_USER' LIMIT 1);
SET @CAN_DELETE_USER = (SELECT id FROM permissions WHERE name = 'CAN_DELETE_USER' LIMIT 1);

/* ============================================
   ADMIN: có tất cả permissions
============================================= */
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
    (@ROLE_ADMIN_ID, @CAN_VIEW_ABOUT_PAGE),
    (@ROLE_ADMIN_ID, @CAN_VIEW_SYSTEM_DESIGN_PAGE),
    (@ROLE_ADMIN_ID, @CAN_VIEW_ROOM_PAGE),
    (@ROLE_ADMIN_ID, @CAN_VIEW_STORE_PAGE),
    (@ROLE_ADMIN_ID, @CAN_VIEW_MANAGEMENT_PAGE),
    (@ROLE_ADMIN_ID, @CAN_ASSIGN_ROLE),
    (@ROLE_ADMIN_ID, @CAN_CREATE_USER),
    (@ROLE_ADMIN_ID, @CAN_UPDATE_USER),
    (@ROLE_ADMIN_ID, @CAN_DELETE_USER);

/* ============================================
   RECRUITER: tất cả permissions TRỪ CAN_CREATE_USER
============================================= */
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
    (@ROLE_RECRUITER_ID, @CAN_VIEW_ABOUT_PAGE),
    (@ROLE_RECRUITER_ID, @CAN_VIEW_SYSTEM_DESIGN_PAGE),
    (@ROLE_RECRUITER_ID, @CAN_VIEW_ROOM_PAGE),
    (@ROLE_RECRUITER_ID, @CAN_VIEW_STORE_PAGE),
    (@ROLE_RECRUITER_ID, @CAN_VIEW_MANAGEMENT_PAGE),
    (@ROLE_RECRUITER_ID, @CAN_ASSIGN_ROLE),
    (@ROLE_RECRUITER_ID, @CAN_UPDATE_USER),
    (@ROLE_RECRUITER_ID, @CAN_DELETE_USER);

/* ============================================
   RENTER: chỉ CAN_VIEW_ROOM_PAGE
============================================= */
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
    (@ROLE_RENTER_ID, @CAN_VIEW_ROOM_PAGE);

/* ============================================
   VISITER: CAN_VIEW_ABOUT_PAGE + CAN_VIEW_SYSTEM_DESIGN_PAGE
============================================= */
INSERT IGNORE INTO role_permissions (role_id, permission_id) VALUES
    (@ROLE_VISITER_ID, @CAN_VIEW_ABOUT_PAGE),
    (@ROLE_VISITER_ID, @CAN_VIEW_SYSTEM_DESIGN_PAGE);
