-- ========================================================
-- 校园失物招领系统 - 真实全量演示测试数据脚本
-- 涵盖：测试学生账号、寻物启事、招领启事、实物图片、站内消息
-- ========================================================

USE `campus_lost_found`;

-- 1. 预置测试学生用户（密码统一为 admin123）
-- BCrypt: $2a$10$uOf7fywQO6WS4g0nbWXY/O1NHcACxU8ED7CsMUxMfmUJQ44xvvSxG
INSERT INTO `sys_user` (`id`, `student_id`, `username`, `password`, `nickname`, `avatar`, `phone`, `email`, `contact_info`, `role`, `status`)
VALUES
(2, '2023001', 'zhangsan', '$2a$10$uOf7fywQO6WS4g0nbWXY/O1NHcACxU8ED7CsMUxMfmUJQ44xvvSxG', '张晨同学 (计科)', NULL, '13811112222', 'zhangc@campus.edu.cn', 'QQ: 28391823', 'STUDENT', 1),
(3, '2023002', 'lisi',     '$2a$10$uOf7fywQO6WS4g0nbWXY/O1NHcACxU8ED7CsMUxMfmUJQ44xvvSxG', '李子萱 (信管)',     NULL, '13933334444', 'lizx@campus.edu.cn', '微信: lzx_campus', 'STUDENT', 1),
(4, '2023003', 'wangwu',   '$2a$10$uOf7fywQO6WS4g0nbWXY/O1NHcACxU8ED7CsMUxMfmUJQ44xvvSxG', '王浩同学 (软件)', NULL, '13755556666', 'wangh@campus.edu.cn', '电话: 13755556666', 'STUDENT', 1)
ON DUPLICATE KEY UPDATE `nickname` = VALUES(`nickname`), `password` = VALUES(`password`), `status` = 1;

-- 2. 清理旧测试数据以防重复
DELETE FROM `lf_item_image` WHERE `item_id` BETWEEN 101 AND 106 AND `item_type` = 'LOST';
DELETE FROM `lf_item_image` WHERE `item_id` BETWEEN 201 AND 206 AND `item_type` = 'FOUND';
DELETE FROM `lf_lost_item` WHERE `id` BETWEEN 101 AND 106;
DELETE FROM `lf_found_item` WHERE `id` BETWEEN 201 AND 206;

-- 3. 插入 6 条【最新寻物启事】
INSERT INTO `lf_lost_item` (`id`, `user_id`, `category_id`, `title`, `description`, `lost_location`, `lost_time`, `contact_info`, `status`, `reviewer_id`, `review_comment`, `reviewed_at`, `created_at`)
VALUES
(101, 2, 2, '【急寻】黑色联想小新Pro16笔记本与深蓝电脑包', 
 '2026年10月18日下午在二教402上完操作系统课程后遗落。电脑外壳右上角贴有小猫贴纸，深蓝色帆布电脑包内有一只罗技无线鼠标与黑色65W充电器。毕业设计所有代码与大作业笔记均在电脑中，非常重要，有好心同学拾得请务必联系我，当面必有酬谢！', 
 '第二教学楼 402 多媒体教室', '2026-10-18', '13811112222', 'APPROVED', 1, '信息真实合规，予以通过', NOW(), '2026-10-18 16:30:00'),

(102, 3, 1, '丢失浅蓝色卡套校园一卡通（电气学院 李同学）', 
 '卡套为浅蓝色硬壳塑料，内有一张校园一卡通（卡号尾号3821）以及一张蓝色地铁公交卡。饭点人流较多不慎掉落，拾得的同学请联系我或送交至一楼保卫处，万分感谢！', 
 '第三食堂一楼风味餐厅结算台附近', '2026-10-19', '13933334444', 'APPROVED', 1, '审核通过', NOW(), '2026-10-19 12:15:00'),

(103, 4, 4, '寻找北面黑色大容量双肩包（内有考研专业课笔记）', 
 'The North Face 黑色双肩背包，左侧网兜插有一把黑色折叠晴雨伞。包内装有《数据结构与算法》笔记本（蓝色封面）、透明文具袋、白色漫步者降噪耳机。笔记本上有半年复习心血，拾到必有重谢！', 
 '图书馆南馆三楼自习区（靠窗长桌）', '2026-10-17', '13755556666', 'APPROVED', 1, '审核通过', NOW(), '2026-10-17 21:40:00'),

(104, 2, 2, '遗失白色 AirPods Pro 蓝牙耳机（含皮卡丘保护套）', 
 '耳机充电仓套有黄色硅胶皮卡丘保护壳，挂绳处系有红色小铃铛。昨日晚跑休息时落在看台水泥台阶上，打开定位显示仍在东操附近，希望捡到的同学能够归还！', 
 '东区田径场看台南侧第二排', '2026-10-19', '13811112222', 'APPROVED', 1, '审核通过', NOW(), '2026-10-19 20:05:00'),

(105, 3, 7, '寻找黑色米家磨砂保温水杯（杯底有轻微凹痕）', 
 '纯黑色磨砂质感米家保温杯，容量 480ml，杯盖处为弹扣开关。杯身底部有一处轻微磕碰凹痕，上周五做实验离开时遗忘在 23 号电脑桌角。', 
 '文科实验楼 204 机房 23号机位', '2026-10-16', '13933334444', 'APPROVED', 1, '审核通过', NOW(), '2026-10-16 17:20:00'),

(106, 4, 6, '寻找尤尼克斯羽毛球拍（深蓝配白双支装球袋）', 
 'YONEX 疾光系列球拍一支，穿线磅数 26 磅，拍柄缠有紫色防滑吸汗带。装在原装深蓝色加厚单肩球袋中，袋内侧有名片插槽。', 
 '风雨操场羽毛球 3 号场地边休息长凳', '2026-10-18', '13755556666', 'APPROVED', 1, '审核通过', NOW(), '2026-10-18 19:10:00');

-- 4. 插入 6 条【最新招领信息】
INSERT INTO `lf_found_item` (`id`, `user_id`, `category_id`, `title`, `description`, `found_location`, `found_time`, `storage_location`, `contact_info`, `status`, `reviewer_id`, `review_comment`, `reviewed_at`, `created_at`)
VALUES
(201, 2, 2, '拾获 ThinkPad 65W Type-C 黑色电源适配器', 
 '在 105 阶梯教室第三排靠走道插座上拾得联想原装 65W 黑块充电器一个，附带 1.5 米双头 Type-C 编织数据线。成色很新，线材保存完好无破损。请失主凭笔记本型号或充电机身序列号核对认领！', 
 '第一教学楼 105 阶梯教室靠走道插座', '2026-10-19', '一教门卫值班室 101（向王大爷登记领取）', '13811112222', 'APPROVED', 1, '审核通过', NOW(), '2026-10-19 10:20:00'),

(202, 3, 5, '在敏行楼拾得一串钥匙（含门禁卡与小黄鸭挂件）', 
 '钥匙串共含有 3 把防盗门钥匙、1 枚蓝色水滴状宿舍门禁芯片卡，以及一个橡胶材质的立体小黄鸭挂件。失主请在系统发起认领并准确说明门禁卡背面编号后前来认领！', 
 '敏行楼 B 座二楼大厅休息沙发角落', '2026-10-19', '敏行楼保卫科前台服务柜（8:00-18:00）', '13933334444', 'APPROVED', 1, '审核通过', NOW(), '2026-10-19 14:10:00'),

(203, 4, 3, '捡到《高等数学（第七版）》上册及手写笔记夹', 
 '同济大学《高等数学》第七版绿色教材，书页内夹有数张手写考研极限与导数归纳笔记，字迹清秀认真。书名扉页留有铅笔字迹。请遗失的同学前往五楼服务台出示学号核对领取！', 
 '图书馆五楼考研专区 88 号自习桌', '2026-10-18', '图书馆五楼总咨询服务台', '13755556666', 'APPROVED', 1, '审核通过', NOW(), '2026-10-18 21:00:00'),

(204, 2, 2, '操场看台拾得黑色华为智能手环（带金属编织腕带）', 
 '黑色华为手环 8，表带为替换过的银黑色米兰尼斯金属磁吸腕带。目前手环尚有电量，屏幕设有密码锁。请失主点击下方申请认领并输入解锁密码核对！', 
 '西区田径场主席台后排长椅', '2026-10-17', '体育学院办公室 208（找辅导员陈老师）', '13811112222', 'APPROVED', 1, '审核通过', NOW(), '2026-10-17 18:30:00'),

(205, 3, 7, '食堂门口拾获透明磨砂双层雨伞（可爱小熊印花）', 
 '雨天在学二食堂门口伞架拾得，伞面为半透明磨砂 PVC 材质，带有棕色可爱小熊印花图案，木质弯柄手把，伞骨结实完好。', 
 '学二食堂正门伞架最右侧', '2026-10-19', '学二食堂一楼保洁阿姨工作间', '13933334444', 'APPROVED', 1, '审核通过', NOW(), '2026-10-19 13:00:00'),

(206, 4, 6, '操场副馆拾得尤尼克斯羽毛球训练多筒（已认领演示）', 
 '两筒已拆封的 YONEX 训练用羽毛球，装在透明塑料密封袋内，内附球拍吸汗带一卷。已于昨日由体育学院李同学出示购买记录核验认领完毕。', 
 '体育馆副馆羽毛球 6 号场', '2026-10-16', '已移交失主本人', '13755556666', 'CLAIMED', 1, '认领流程已完结', NOW(), '2026-10-16 11:00:00');

-- 5. 插入真实实物图片关联（多图展示、单图展示与大图预览支持）
INSERT INTO `lf_item_image` (`item_type`, `item_id`, `url`, `sort_order`, `created_at`)
VALUES
-- 寻物图片
('LOST', 101, '/uploads/sample/laptop.jpg', 1, NOW()),
('LOST', 101, '/uploads/sample/laptop_mouse.jpg', 2, NOW()),
('LOST', 102, '/uploads/sample/campus_card.jpg', 1, NOW()),
('LOST', 103, '/uploads/sample/backpack.jpg', 1, NOW()),
('LOST', 103, '/uploads/sample/backpack_inside.jpg', 2, NOW()),
('LOST', 104, '/uploads/sample/airpods.jpg', 1, NOW()),
('LOST', 105, '/uploads/sample/water_bottle.jpg', 1, NOW()),
-- 注意：106 号故意不配图片，用于展示系统“无图模式下的优雅占位图”

-- 招领图片
('FOUND', 201, '/uploads/sample/charger.jpg', 1, NOW()),
('FOUND', 202, '/uploads/sample/keys.jpg', 1, NOW()),
('FOUND', 202, '/uploads/sample/keys_duck.jpg', 2, NOW()),
('FOUND', 203, '/uploads/sample/math_book.jpg', 1, NOW()),
('FOUND', 204, '/uploads/sample/smart_band.jpg', 1, NOW()),
('FOUND', 205, '/uploads/sample/umbrella.jpg', 1, NOW());
-- 注意：206 号已认领，不配图片演示默认状态

-- 6. 插入示例站内通知消息
INSERT INTO `sys_message` (`user_id`, `title`, `content`, `type`, `related_id`, `related_type`, `is_read`, `created_at`)
VALUES
(2, '寻物启事审核通过通知', '您发布的【急寻】黑色联想小新Pro16笔记本与深蓝电脑包 已通过管理员审核，已推送到平台首页展示！', 'REVIEW_RESULT', 101, 'LOST_ITEM', 0, NOW()),
(3, '招领信息审核通过通知', '您拾得的 在敏行楼拾得一串钥匙 已通过管理员审核，等待失主联系认领。', 'REVIEW_RESULT', 202, 'FOUND_ITEM', 0, NOW()),
(4, '系统欢迎消息', '欢迎加入校园失物招领互助平台，请保管好个人物品，感谢您的善举！', 'SYSTEM', 0, 'LOST_ITEM', 1, NOW());
