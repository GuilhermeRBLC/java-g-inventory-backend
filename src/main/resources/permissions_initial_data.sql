-- Initialize permissions types data
INSERT IGNORE INTO tb_permission (description, created) VALUES ("VIEW_USERS", now()), ("EDIT_USERS", now()), ("DELETE_USERS", now());
INSERT IGNORE INTO tb_permission (description, created) VALUES ("VIEW_PRODUCTS", now()), ("EDIT_PRODUCTS", now()), ("DELETE_PRODUCTS", now());
INSERT IGNORE INTO tb_permission (description, created) VALUES ("VIEW_INPUTS", now()), ("EDIT_INPUTS", now()), ("DELETE_INPUTS", now());
INSERT IGNORE INTO tb_permission (description, created) VALUES ("VIEW_OUTPUTS", now()), ("EDIT_OUTPUTS", now()), ("DELETE_OUTPUTS", now());
INSERT IGNORE INTO tb_permission (description, created) VALUES ("GENERATE_REPORTS", now());
INSERT IGNORE INTO tb_permission (description, created) VALUES ("EDIT_CONFIGURATIONS", now());

-- Initialize configurations data
INSERT IGNORE INTO tb_configuration (alias, stored, created) VALUES ("COMPANY_NAME", "G Inventory", now());
INSERT IGNORE INTO tb_configuration (alias, stored, created) VALUES ("COMPANY_LOGO", "", now());
INSERT IGNORE INTO tb_configuration (alias, stored, created) VALUES ("ALERT_EMAIL", "admin@mail.com", now());
