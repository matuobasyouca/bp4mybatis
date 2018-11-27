CREATE TABLE `WantBuying` (
`id` int(11) NOT NULL AUTO_INCREMENT,
 `dealerAccountId` INT NULL,
`startBudget` DOUBLE NULL,
`endBudget` DOUBLE NULL,
`carAge` INT NULL,
`seriesIds` VARCHAR(255) NULL,
`gears` VARCHAR(255) NULL,
`modelTypes` VARCHAR(255) NULL,
`miles` DOUBLE NULL,
`startLiter` DOUBLE NULL,
`endLiter` DOUBLE NULL,
`wantBuyTitle` VARCHAR(255) NULL,
`remark` VARCHAR(510) NULL,
`state` INT NULL,
`overTime` DATETIME NULL,
`answerNum` INT NULL,
`lockNum` INT NULL,
 `createTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
`updateTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
 PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

CREATE TABLE `BuyingContactRecord` (
`id` int(11) NOT NULL AUTO_INCREMENT,
 `wantBuyingId` INT NULL,
`recordType` INT NULL,
`dealerAccountId` INT NULL,
`remark` VARCHAR(510) NULL,
`imageUrls` VARCHAR(255) NULL,
`carTitle` VARCHAR(255) NULL,
`registeDate` VARCHAR(255) NULL,
`watchMiles` DOUBLE NULL,
`gears` VARCHAR(255) NULL,
 `createTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
`updateTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
 PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

ALTER TABLE `WantBuying`
ADD INDEX `dealerAccountId` (`dealerAccountId`) USING BTREE ;

ALTER TABLE `BuyingContactRecord`
ADD INDEX `wantBuyingId` (`wantBuyingId`) USING BTREE ,
ADD INDEX `recordType` (`recordType`) USING BTREE ,
ADD INDEX `dealerAccountId` (`dealerAccountId`) USING BTREE ;

CREATE TABLE `LockBuyingCar` (
`id` int(11) NOT NULL AUTO_INCREMENT,
 `sellDemandId` INT NULL,
`wantBuyingId` INT NULL,
 `createTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
`updateTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
 PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

ALTER TABLE `Certification`
ADD COLUMN `carInfoDesc`  varchar(255) NULL;

INSERT INTO `SystemCode` (codeType,codeName,codeValue,codeShowName) VALUES ('ConfigCode', 'wantBuyOverDay', '7',  '求购过期天数');
INSERT INTO `SystemCode` (codeType,codeName,codeValue,codeShowName) VALUES ('WantBuyPercent', 'brandPercent', '40',  '品牌占比-只能整型');
INSERT INTO `SystemCode` (codeType,codeName,codeValue,codeShowName) VALUES ('WantBuyPercent', 'carAgePercent', '30',  '车龄占比-只能整型');
INSERT INTO `SystemCode` (codeType,codeName,codeValue,codeShowName) VALUES ('WantBuyPercent', 'pricePercent', '30',  '预算占比-只能整型');
INSERT INTO `SystemCode` (codeType,codeName,codeValue,codeShowName) VALUES ('WantBuyPercent', 'joinScore', '90',  '成功加入匹配分数');
INSERT INTO `SystemCode` (codeType,codeName,codeValue,codeShowName) VALUES ('WantBuyPercent', 'selectNum', '5',  '取匹配成功的数目');
-- Cm_Aq4k0tpCW5eAxTuXYbtUC7qNQhiNyrxuu0iACya4
INSERT INTO `SystemCode` (codeType,codeName,codeValue,codeShowName) VALUES ('TempMsgId', 'wantBuyingAnswer', 'vF9shIjCJ_YWcNeOgN9VQQKk35XdyLTPc4shfLFrWKs',  '求购留言发送模版消息');
INSERT INTO SystemCode(codeType,codeName,codeValue,codeShowName)VALUES('TempMsgId','auctionFinish','e5jrDZ_zNbr0KUMtp6Dscym6AkdTonyLfpJbP89NO7A ','拍卖结束发送模板消息');


-- 配置
INSERT INTO SystemCode (codeType, codeName, codeValue, codeShowName, codeFiter) VALUES ('ConfigCode', 'dealerCarRamCount', '1000', '存放在内存的车源条数', '1');
INSERT INTO SystemCode (codeType, codeName, codeValue, codeShowName, codeFiter) VALUES ('ConfigCode', 'dealerCarEffectDay', '45', '车源有效天数', '1');
INSERT INTO SystemCode (codeType, codeName, codeValue, codeShowName, codeFiter) VALUES ('ConfigCode', 'auctionRecordInserTimes', '2', '拍卖出价次数限制', '1');

-- 权限
INSERT INTO SystemCode (codeType, codeName, codeValue, codeShowName, codeFiter, codeDesc) VALUES ('PermissionRule', ',DealerMan,Auction,', ',Auction,', '用户对拍卖的权限控制', '1', '{me.a}.dealerAccountId={uid}');
INSERT INTO SystemCode (codeType, codeName, codeValue, codeShowName, codeFiter, codeDesc) VALUES ('PermissionRule', ',DealerMan,WantBuying,', ',WantBuying,', '用户对求购的权限控制', '1', '{me.a}.dealerAccountId={uid}');

-- 颜色code
INSERT INTO `SystemCode` (`id`, `codeType`, `codeName`, `codeValue`, `codeStatus`, `codeShowName`, `codeFiter`, `codeSort`, `codeParentValue`, `codeDesc`, `createTime`, `updateTime`) VALUES (null, 'CarColorType', 'white', '1', NULL, '白色', 0, 0, NULL, '#ffffff', '2016-1-1 00:00:00', '2016-1-1 00:00:00');
INSERT INTO `SystemCode` (`id`, `codeType`, `codeName`, `codeValue`, `codeStatus`, `codeShowName`, `codeFiter`, `codeSort`, `codeParentValue`, `codeDesc`, `createTime`, `updateTime`) VALUES (null, 'CarColorType', 'black', '2', NULL, '黑色', 0, 0, NULL, '#000000', '2016-1-1 00:00:00', '2016-1-1 00:00:00');
INSERT INTO `SystemCode` (`id`, `codeType`, `codeName`, `codeValue`, `codeStatus`, `codeShowName`, `codeFiter`, `codeSort`, `codeParentValue`, `codeDesc`, `createTime`, `updateTime`) VALUES (null, 'CarColorType', 'silveryGrey', '3', NULL, '银灰色', 0, 0, NULL, '#cfcfcf', '2016-1-1 00:00:00', '2016-1-1 00:00:00');
INSERT INTO `SystemCode` (`id`, `codeType`, `codeName`, `codeValue`, `codeStatus`, `codeShowName`, `codeFiter`, `codeSort`, `codeParentValue`, `codeDesc`, `createTime`, `updateTime`) VALUES (null, 'CarColorType', 'red', '4', NULL, '红色', 0, 0, NULL, '#e13031', '2016-1-1 00:00:00', '2016-1-1 00:00:00');
INSERT INTO `SystemCode` (`id`, `codeType`, `codeName`, `codeValue`, `codeStatus`, `codeShowName`, `codeFiter`, `codeSort`, `codeParentValue`, `codeDesc`, `createTime`, `updateTime`) VALUES (null, 'CarColorType', 'blue', '5', NULL, '蓝色', 0, 0, NULL, '#2490f2', '2016-1-1 00:00:00', '2016-1-1 00:00:00');
INSERT INTO `SystemCode` (`id`, `codeType`, `codeName`, `codeValue`, `codeStatus`, `codeShowName`, `codeFiter`, `codeSort`, `codeParentValue`, `codeDesc`, `createTime`, `updateTime`) VALUES (null, 'CarColorType', 'brown', '6', NULL, '棕褐色', 0, 0, NULL, '#673234', '2016-1-1 00:00:00', '2016-1-1 00:00:00');
INSERT INTO `SystemCode` (`id`, `codeType`, `codeName`, `codeValue`, `codeStatus`, `codeShowName`, `codeFiter`, `codeSort`, `codeParentValue`, `codeDesc`, `createTime`, `updateTime`) VALUES (null, 'CarColorType', 'golden', '7', NULL, '金色', 0, 0, NULL, '#e0b268', '2016-1-1 00:00:00', '2016-1-1 00:00:00');
INSERT INTO `SystemCode` (`id`, `codeType`, `codeName`, `codeValue`, `codeStatus`, `codeShowName`, `codeFiter`, `codeSort`, `codeParentValue`, `codeDesc`, `createTime`, `updateTime`) VALUES (null, 'CarColorType', 'orange', '8', NULL, '橙色', 0, 0, NULL, '#f87b0a', '2016-1-1 00:00:00', '2016-1-1 00:00:00');
INSERT INTO `SystemCode` (`id`, `codeType`, `codeName`, `codeValue`, `codeStatus`, `codeShowName`, `codeFiter`, `codeSort`, `codeParentValue`, `codeDesc`, `createTime`, `updateTime`) VALUES (null, 'CarColorType', 'yellow', '9', NULL, '黄色', 0, 0, NULL, '#fff327', '2016-1-1 00:00:00', '2016-1-1 00:00:00');
INSERT INTO `SystemCode` (`id`, `codeType`, `codeName`, `codeValue`, `codeStatus`, `codeShowName`, `codeFiter`, `codeSort`, `codeParentValue`, `codeDesc`, `createTime`, `updateTime`) VALUES (null, 'CarColorType', 'purple', '10', NULL, '紫色', 0, 0, NULL, '#d100fb', '2016-1-1 00:00:00', '2016-1-1 00:00:00');
INSERT INTO `SystemCode` (`id`, `codeType`, `codeName`, `codeValue`, `codeStatus`, `codeShowName`, `codeFiter`, `codeSort`, `codeParentValue`, `codeDesc`, `createTime`, `updateTime`) VALUES (null, 'CarColorType', 'green', '11', NULL, '绿色', 0, 0, NULL, '#3cc42b', '2016-1-1 00:00:00', '2016-1-1 00:00:00');

-- Certification
ALTER TABLE Certification ADD carColor int DEFAULT NULL AFTER operationType;
ALTER TABLE Certification ADD yearCheckExpireDate varchar(255) DEFAULT NULL AFTER carColor;
ALTER TABLE Certification ADD forceInsureExpireDate varchar(255) DEFAULT NULL AFTER yearCheckExpireDate;
ALTER TABLE Certification ADD comInsureExpireDate varchar(255) DEFAULT NULL AFTER forceInsureExpireDate;

DROP TABLE Auction ;
CREATE TABLE `Auction` (
`id` int(11) NOT NULL AUTO_INCREMENT,
`auctionCode` VARCHAR(255) NULL,
`demandId` INT NULL,
`employeeId` INT NULL,
`customerId` INT NULL,
`dealerAccountId` INT NULL,
`title` VARCHAR(255) NULL,
`startTime` DATETIME NULL,
`endTime` DATETIME NULL,
`startPrice` DOUBLE NULL,
`maxPrice` DOUBLE NULL,
`state` INT NULL,
`viewTimes` INT NULL,
`auctionTimes` INT NULL,
`shareTiems` INT NULL,
 `createTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
`updateTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
 PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


DROP TABLE AuctionRecord ;
CREATE TABLE `AuctionRecord` (
`id` int(11) NOT NULL AUTO_INCREMENT,
 `auctionId` INT NULL,
`dealerAccountId` INT NULL,
`biddingPrice` DOUBLE NULL,
`biddingTime` DATETIME NULL,
`commission` DOUBLE NULL,
`togetherPrice` DOUBLE NULL,
 `createTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
`updateTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
 PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;


DROP TABLE AuctionViewRecord ;
CREATE TABLE `AuctionViewRecord` (
`id` int(11) NOT NULL AUTO_INCREMENT,
 `auctionId` INT NULL,
`dealerAccountId` INT NULL,
`viewTimes` INT NULL,
 `createTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
`updateTime` datetime NOT NULL DEFAULT '2016-1-1 00:00:00',
 PRIMARY KEY (`id`)) ENGINE=InnoDB DEFAULT CHARSET=utf8 COLLATE=utf8_bin;

-- DealerAccount
ALTER TABLE DealerAccount ADD receiveMsgState int DEFAULT 1 AFTER restFunds;

-- 消息模板SQL
-- 测试：8fBf6CopX7cpd8zs5uErUeTS5VsyvRZpKxdEj7HLMqc
INSERT INTO SystemCode(codeType,codeName,codeValue,codeShowName)VALUES('TempMsgId','auctionStart','1DCGejqH3lK-B6BZssY0paiwHgUePbFihldn-OEyWzM','拍卖开始发送消息模板');
-- 测试：wUIgiy1nH9rLYGuV5W7mqMrAXROsUK3T07smRpgbT0E
INSERT INTO SystemCode(codeType,codeName,codeValue,codeShowName)VALUES('TempMsgId','auctionBidding','t5BMTgcKJ9PDBY8qFSxwh3iyKlsjdqBw3EAH8ulD8ZM','拍卖出价发送消息模板');

ALTER TABLE `CallingLog`
ADD COLUMN `callingArgs`  varchar(255) NULL AFTER `link`,
ADD COLUMN `callingType`  varchar(255) NULL AFTER `callingArgs`;














