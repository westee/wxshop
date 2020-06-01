CREATE TABLE GOODS
(
    ID          BIGINT PRIMARY KEY AUTO_INCREMENT,
    SHOP_ID     BIGINT,
    NAME        VARCHAR(100),
    DESCRIPTION VARCHAR(1024),
    DETAILS     TEXT,
    IMG_URL     VARCHAR(1024),
    PRICE       BIGINT,     -- 单位分
    STOCK       INT       NOT NULL DEFAULT 0,
    STATUS      VARCHAR(16), -- 'ok' 正常 'deleted' 已经删除
    CREATED_AT  TIMESTAMP NOT NULL DEFAULT NOW(),
    UPDATED_AT  TIMESTAMP NOT NULL DEFAULT NOW()
);

INSERT INTO GOODS(ID, SHOP_ID, NAME, DESCRIPTION, DETAILS, IMG_URL, PRICE, STOCK, STATUS)
VALUES (1, 1, 'goods1', 'desc1', 'details1', 'url1', 100, 5, 'ok');
INSERT INTO GOODS(ID, SHOP_ID, NAME, DESCRIPTION, DETAILS, IMG_URL, PRICE, STOCK, STATUS)
VALUES (2, 1, 'goods2', 'desc2', 'details1', 'url1', 100, 5, 'ok');
INSERT INTO GOODS(ID, SHOP_ID, NAME, DESCRIPTION, DETAILS, IMG_URL, PRICE, STOCK, STATUS)
VALUES (3, 2, 'goods3', 'desc2', 'details1', 'url1', 100, 5, 'ok');
INSERT INTO GOODS(ID, SHOP_ID, NAME, DESCRIPTION, DETAILS, IMG_URL, PRICE, STOCK, STATUS)
VALUES (4, 2, 'goods4', 'desc2', 'details1', 'url1', 100, 5, 'ok');
INSERT INTO GOODS(ID, SHOP_ID, NAME, DESCRIPTION, DETAILS, IMG_URL, PRICE, STOCK, STATUS)
VALUES (5, 2, 'goods5', 'desc2', 'details1', 'url1', 200, 5, 'ok');