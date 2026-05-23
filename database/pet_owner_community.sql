/*
 Navicat Premium Data Transfer

 Source Server         : localhost_3306
 Source Server Type    : MySQL
 Source Server Version : 80044 (8.0.44)
 Source Host           : localhost:3306
 Source Schema         : pet_owner_community

 Target Server Type    : MySQL
 Target Server Version : 80044 (8.0.44)
 File Encoding         : 65001

 Date: 23/05/2026 14:08:24
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for comment
-- ----------------------------
DROP TABLE IF EXISTS `comment`;
CREATE TABLE `comment`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '评论ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '评论用户ID',
  `content` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '评论内容',
  `parent_id` bigint NULL DEFAULT 0 COMMENT '父评论ID（0表示一级评论）',
  `reply_user_id` bigint NULL DEFAULT NULL COMMENT '被回复用户ID',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常 0删除',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `is_author` tinyint NULL DEFAULT 0 COMMENT '是否作者评论',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_comment_user`(`user_id` ASC) USING BTREE,
  INDEX `fk_comment_post`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_comment_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_comment_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 110 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_bin COMMENT = '评论表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment
-- ----------------------------
INSERT INTO `comment` VALUES (1, 1, 9, '说得太对了，我家猫刚做完驱虫，铲屎官们千万别偷懒！', 0, NULL, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (2, 1, 11, '第一次养宠，请问幼猫几个月可以开始驱虫呀？', 0, NULL, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (3, 1, 13, '回复楼上：一般满6周龄就可以开始驱虫了，遵医嘱就好', 2, 11, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (4, 2, 9, '夏天遛狗真的得选早晚，上次中午出门我家狗都热得吐舌头了', 0, NULL, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (5, 2, 13, '冰垫+凉席是必备！我家猫夏天天天瘫在上面', 0, NULL, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (6, 3, 11, '换毛季家里全是猫毛，每天用吸尘器吸三次都没用', 0, NULL, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (7, 3, 9, '可以试试喂点鱼油，我家猫吃了之后掉毛真的少了很多', 0, NULL, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (8, 3, 13, '回复楼上：鱼油有什么推荐吗？怕踩雷', 7, 9, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (9, 4, 11, '我家短毛猫冬天必须穿衣服，不然缩成一团不肯动', 0, NULL, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (10, 4, 9, '冬天别让宠物睡地板，垫个厚毯子或者猫窝会好很多', 0, NULL, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (13, 6, 9, '天气好的时候遛狗真的治愈！毛孩子也开心', 0, NULL, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (14, 7, 9, '推荐用无香低敏的宠物专用沐浴露，人用的千万不能用！', 0, NULL, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (15, 7, 11, '我家狗狗皮肤敏感，用的是燕麦配方的沐浴露，温和不刺激', 0, NULL, 0, 1, '2026-04-13 16:18:39', 0);
INSERT INTO `comment` VALUES (16, 32, 9, '这个帖子写得非常棒，受益匪浅！', 0, NULL, 12, 1, '2026-04-17 14:03:32', 1);
INSERT INTO `comment` VALUES (17, 32, 11, '支持作者，观点很有道理！', 0, NULL, 8, 1, '2026-04-17 14:03:32', 0);
INSERT INTO `comment` VALUES (18, 32, 13, '请问这个知识点有更详细的资料吗？', 0, NULL, 5, 1, '2026-04-17 14:03:32', 0);
INSERT INTO `comment` VALUES (19, 32, 9, '感谢分享，学到很多新知识～', 0, NULL, 20, 1, '2026-04-17 14:03:32', 0);
INSERT INTO `comment` VALUES (20, 32, 11, '确实很棒，我也觉得很实用', 16, 9, 3, 1, '2026-04-17 14:03:32', 0);
INSERT INTO `comment` VALUES (21, 32, 13, '同意，内容非常干货', 19, 9, 2, 1, '2026-04-17 14:03:32', 0);
INSERT INTO `comment` VALUES (22, 32, 9, '谢谢支持！一起进步', 17, 11, 6, 1, '2026-04-17 14:03:32', 1);
INSERT INTO `comment` VALUES (23, 32, 13, '完全赞同，逻辑清晰', 17, 11, 4, 1, '2026-04-17 14:03:32', 0);
INSERT INTO `comment` VALUES (24, 32, 9, '有的，我可以分享给你', 18, 13, 8, 1, '2026-04-17 14:03:32', 1);
INSERT INTO `comment` VALUES (25, 32, 11, '同求资料，谢谢楼主', 18, 13, 9, 1, '2026-04-17 14:03:32', 0);
INSERT INTO `comment` VALUES (26, 32, 11, '期待楼主后续更新更多内容', 0, NULL, 15, 1, '2026-04-17 14:03:32', 0);
INSERT INTO `comment` VALUES (27, 32, 13, '已经收藏，反复学习', 0, NULL, 11, 1, '2026-04-17 14:03:32', 0);
INSERT INTO `comment` VALUES (28, 32, 9, '这篇帖子解决了我的困惑', 0, NULL, 19, 1, '2026-04-17 14:03:32', 0);
INSERT INTO `comment` VALUES (29, 32, 18, '没解决我的', 28, 9, 0, 1, '2026-04-17 15:40:38', 0);
INSERT INTO `comment` VALUES (30, 32, 11, 'id11的用户评论了32的帖子', 0, NULL, 1, 1, '2026-04-21 09:07:04', 0);
INSERT INTO `comment` VALUES (31, 32, 9, 'id9的用户回复了id11的评论', 30, 11, 0, 1, '2026-04-21 09:07:36', 0);
INSERT INTO `comment` VALUES (32, 32, 13, 'id13的用户回复了id为9的评论', 30, 9, 0, 1, '2026-04-21 09:08:04', 0);
INSERT INTO `comment` VALUES (36, 32, 9, '测试', 0, 0, 0, 1, '2026-04-21 11:26:31', 1);
INSERT INTO `comment` VALUES (37, 32, 9, '测试', 36, 9, 0, 1, '2026-04-21 11:26:39', 1);
INSERT INTO `comment` VALUES (38, 32, 9, '测试2', 36, 9, 0, 1, '2026-04-21 11:26:51', 1);
INSERT INTO `comment` VALUES (39, 32, 11, '新增评论', 0, 0, 0, 1, '2026-04-21 11:30:31', 1);
INSERT INTO `comment` VALUES (40, 32, 9, '你好呀', 39, 11, 0, 1, '2026-04-21 11:33:47', 1);
INSERT INTO `comment` VALUES (41, 32, 11, '我回复了', 39, 9, 0, 1, '2026-04-21 11:34:03', 1);
INSERT INTO `comment` VALUES (42, 32, 9, '我又回复你了', 39, 11, 0, 1, '2026-04-21 11:35:06', 1);
INSERT INTO `comment` VALUES (43, 32, 9, 'haolo', 39, 11, 0, 1, '2026-04-21 11:35:19', 1);
INSERT INTO `comment` VALUES (47, 32, 9, '你好呀', 39, 11, 0, 1, '2026-04-21 11:44:13', 1);
INSERT INTO `comment` VALUES (48, 32, 9, '哈哈哈', 39, 11, 0, 1, '2026-04-21 11:45:46', 1);
INSERT INTO `comment` VALUES (49, 32, 9, '你厉害', 39, 11, 0, 1, '2026-04-21 12:40:26', 1);
INSERT INTO `comment` VALUES (50, 32, 9, '大家好', 0, 0, 0, 1, '2026-04-21 12:45:40', 1);
INSERT INTO `comment` VALUES (51, 32, 9, '你和所得对', 30, 11, 0, 1, '2026-04-21 13:47:32', 1);
INSERT INTO `comment` VALUES (52, 32, 9, '你说的也和你对', 30, 13, 0, 1, '2026-04-21 13:47:39', 1);
INSERT INTO `comment` VALUES (53, 32, 9, '你真厉害', 28, 18, 0, 1, '2026-04-21 13:58:26', 1);
INSERT INTO `comment` VALUES (73, 32, 9, '第一', 0, 0, 0, 1, '2026-04-21 14:34:58', 1);
INSERT INTO `comment` VALUES (74, 32, 9, '测试三', 36, 9, 0, 1, '2026-04-21 15:25:15', 1);
INSERT INTO `comment` VALUES (75, 32, 9, '第二', 0, 0, 0, 1, '2026-04-21 15:25:25', 1);
INSERT INTO `comment` VALUES (76, 32, 9, '耶耶', 75, 9, 0, 1, '2026-04-21 15:25:33', 1);
INSERT INTO `comment` VALUES (77, 32, 9, '棒棒', 75, 9, 0, 1, '2026-04-21 15:25:42', 1);
INSERT INTO `comment` VALUES (81, 32, 9, '第六', 75, 9, 0, 1, '2026-04-21 15:29:53', 1);
INSERT INTO `comment` VALUES (82, 32, 9, '第十', 75, 9, 0, 1, '2026-04-21 15:30:01', 1);
INSERT INTO `comment` VALUES (83, 32, 9, '我也学到咯', 19, 9, 0, 1, '2026-04-21 15:30:51', 1);
INSERT INTO `comment` VALUES (85, 31, 9, 'nb', 0, 0, 0, 1, '2026-04-21 16:33:27', 0);
INSERT INTO `comment` VALUES (86, 32, 11, '你的帖子不错', 0, 0, 0, 1, '2026-04-22 11:05:30', 0);
INSERT INTO `comment` VALUES (88, 32, 11, '厉害厉害', 0, 0, 0, 1, '2026-04-22 11:08:39', 0);
INSERT INTO `comment` VALUES (89, 32, 11, '你真厉害', 0, 0, 0, 1, '2026-04-22 11:14:57', 0);
INSERT INTO `comment` VALUES (90, 32, 11, '太可爱了', 0, 0, 1, 1, '2026-04-22 11:15:56', 0);
INSERT INTO `comment` VALUES (91, 32, 11, '太可爱了好喜欢', 0, 0, 0, 1, '2026-04-22 13:05:24', 0);
INSERT INTO `comment` VALUES (93, 34, 9, '厉害厉害', 0, 0, 0, 1, '2026-04-23 09:01:36', 0);
INSERT INTO `comment` VALUES (94, 31, 9, '太可爱了好喜欢', 85, 9, 0, 1, '2026-04-23 16:47:22', 0);
INSERT INTO `comment` VALUES (95, 31, 9, 'hhhh', 0, 0, 0, 1, '2026-04-23 16:47:30', 0);
INSERT INTO `comment` VALUES (97, 32, 9, '我也这么觉得', 91, 11, 0, 1, '2026-04-24 11:47:40', 1);
INSERT INTO `comment` VALUES (98, 32, 9, '谢谢', 90, 11, 0, 1, '2026-04-24 14:45:00', 1);
INSERT INTO `comment` VALUES (100, 77, 11, '这条鱼好可爱', 0, 0, 1, 1, '2026-04-27 18:02:04', 0);
INSERT INTO `comment` VALUES (101, 77, 9, '我也这么觉得', 100, 11, 0, 1, '2026-04-27 18:02:24', 1);
INSERT INTO `comment` VALUES (102, 31, 9, 'zs最nb', 0, 0, 0, 1, '2026-04-27 18:44:18', 0);
INSERT INTO `comment` VALUES (103, 30, 23, '🙏', 0, 0, 0, 1, '2026-04-27 18:53:41', 0);
INSERT INTO `comment` VALUES (104, 84, 9, '给我尝尝', 0, 0, 0, 1, '2026-04-27 19:04:29', 0);
INSERT INTO `comment` VALUES (105, 84, 11, '我也要吃', 0, 0, 0, 1, '2026-04-27 19:10:47', 0);
INSERT INTO `comment` VALUES (106, 84, 24, '嘿嘿嘿嘿歪歪我来啦嘻嘻嘻', 0, 0, 1, 1, '2026-04-27 19:23:43', 0);
INSERT INTO `comment` VALUES (107, 84, 24, '你吃的什么 芸山季吗', 0, 0, 1, 1, '2026-04-27 19:24:25', 0);
INSERT INTO `comment` VALUES (108, 84, 9, '这是什么', 107, 24, 0, 1, '2026-04-27 20:35:03', 0);
INSERT INTO `comment` VALUES (109, 86, 9, '我也不想上课', 0, 0, 0, 1, '2026-04-28 10:30:27', 0);

-- ----------------------------
-- Table structure for comment_like
-- ----------------------------
DROP TABLE IF EXISTS `comment_like`;
CREATE TABLE `comment_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint NOT NULL COMMENT '点赞用户ID',
  `comment_id` bigint NOT NULL COMMENT '评论ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_comment`(`user_id` ASC, `comment_id` ASC) USING BTREE,
  INDEX `fk_cl_comment`(`comment_id` ASC) USING BTREE,
  CONSTRAINT `fk_cl_comment` FOREIGN KEY (`comment_id`) REFERENCES `comment` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_cl_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 43 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_bin COMMENT = '评论点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comment_like
-- ----------------------------
INSERT INTO `comment_like` VALUES (24, 9, 30, '2026-04-21 10:37:53');
INSERT INTO `comment_like` VALUES (25, 9, 28, '2026-04-21 12:56:42');
INSERT INTO `comment_like` VALUES (30, 11, 24, '2026-04-22 10:36:26');
INSERT INTO `comment_like` VALUES (39, 9, 90, '2026-04-24 14:45:04');
INSERT INTO `comment_like` VALUES (40, 11, 100, '2026-04-27 18:02:08');
INSERT INTO `comment_like` VALUES (41, 24, 106, '2026-04-27 19:23:45');
INSERT INTO `comment_like` VALUES (42, 9, 107, '2026-04-27 20:34:42');

-- ----------------------------
-- Table structure for favorite
-- ----------------------------
DROP TABLE IF EXISTS `favorite`;
CREATE TABLE `favorite`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '收藏ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '收藏时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_fav`(`user_id` ASC, `post_id` ASC) USING BTREE,
  INDEX `fk_fav_post`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_fav_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_fav_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 122 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_bin COMMENT = '帖子收藏表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of favorite
-- ----------------------------
INSERT INTO `favorite` VALUES (12, 11, 31, '2026-04-17 13:58:21');
INSERT INTO `favorite` VALUES (38, 9, 32, '2026-04-23 23:08:06');
INSERT INTO `favorite` VALUES (44, 11, 77, '2026-04-27 18:26:29');
INSERT INTO `favorite` VALUES (45, 23, 30, '2026-04-27 18:54:35');
INSERT INTO `favorite` VALUES (82, 9, 84, '2026-05-23 13:17:51');
INSERT INTO `favorite` VALUES (121, 9, 86, '2026-05-23 13:24:11');

-- ----------------------------
-- Table structure for follow
-- ----------------------------
DROP TABLE IF EXISTS `follow`;
CREATE TABLE `follow`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `user_id` bigint NOT NULL COMMENT '用户ID（谁发起关注）',
  `follow_user_id` bigint NOT NULL COMMENT '被关注用户ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '关注时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_follow`(`user_id` ASC, `follow_user_id` ASC) USING BTREE,
  INDEX `fk_follow_user2`(`follow_user_id` ASC) USING BTREE,
  CONSTRAINT `fk_follow_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `fk_follow_user2` FOREIGN KEY (`follow_user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 331 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_bin COMMENT = '用户关注表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of follow
-- ----------------------------
INSERT INTO `follow` VALUES (76, 11, 13, '2026-04-23 15:14:31');
INSERT INTO `follow` VALUES (77, 11, 18, '2026-04-23 15:14:33');
INSERT INTO `follow` VALUES (229, 11, 9, '2026-04-23 16:48:12');
INSERT INTO `follow` VALUES (318, 9, 11, '2026-04-27 18:53:38');
INSERT INTO `follow` VALUES (321, 9, 10, '2026-04-27 18:53:48');
INSERT INTO `follow` VALUES (322, 9, 23, '2026-04-27 19:05:00');
INSERT INTO `follow` VALUES (323, 11, 23, '2026-04-27 19:09:06');
INSERT INTO `follow` VALUES (324, 24, 23, '2026-04-27 19:24:34');
INSERT INTO `follow` VALUES (330, 9, 24, '2026-05-23 13:12:31');

-- ----------------------------
-- Table structure for notification
-- ----------------------------
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '通知ID',
  `user_id` bigint NOT NULL COMMENT '被操作的用户ID(接收消息）',
  `from_user_id` bigint NOT NULL COMMENT '发起操作的用户ID',
  `type` tinyint NOT NULL COMMENT '操作类型：1=帖子点赞 2=评论点赞 3=帖子评论 4=回复评论 5=关注 6=收藏',
  `post_id` bigint NULL DEFAULT NULL COMMENT '关联帖子ID',
  `comment_id` bigint NULL DEFAULT NULL COMMENT '关联评论ID',
  `content` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '通知内容',
  `is_read` tinyint NULL DEFAULT 0 COMMENT '是否已读：0未读 1已读',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `unique_key` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin GENERATED ALWAYS AS ((case when (`type` in (1,5)) then concat(`user_id`,`from_user_id`,`post_id`,`type`) when (`type` = 2) then concat(`user_id`,`from_user_id`,`post_id`,`comment_id`,`type`) when (`type` = 6) then concat(`user_id`,`from_user_id`,`type`) else NULL end)) VIRTUAL NULL,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_notify_final`(`unique_key` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 675 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_bin COMMENT = '通知表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notification
-- ----------------------------
INSERT INTO `notification` VALUES (1, 13, 9, 1, 7, 0, '用户9 赞了你的帖子', 0, '2026-04-14 14:48:59', DEFAULT);
INSERT INTO `notification` VALUES (2, 10, 9, 5, 0, 0, '用户9 关注了你', 0, '2026-04-27 18:53:48', DEFAULT);
INSERT INTO `notification` VALUES (3, 13, 9, 6, 7, 0, '用户9 收藏了你的帖子', 0, '2026-04-14 09:44:27', DEFAULT);
INSERT INTO `notification` VALUES (9, 11, 9, 1, 31, 0, '用户9 赞了你的帖子', 1, '2026-04-27 17:51:14', DEFAULT);
INSERT INTO `notification` VALUES (10, 11, 9, 6, 31, 0, '用户9 收藏了你的帖子', 1, '2026-04-27 17:51:17', DEFAULT);
INSERT INTO `notification` VALUES (11, 11, 9, 1, 30, 0, '用户9 赞了你的帖子', 1, '2026-04-27 17:51:33', DEFAULT);
INSERT INTO `notification` VALUES (18, 9, 11, 1, 32, 0, '用户11 赞了你的帖子', 1, '2026-04-24 14:50:05', DEFAULT);
INSERT INTO `notification` VALUES (19, 9, 11, 6, 32, 0, '用户11 收藏了你的帖子', 1, '2026-04-27 18:26:29', DEFAULT);
INSERT INTO `notification` VALUES (20, 13, 9, 2, 32, 0, '用户9 赞了你的评论', 0, '2026-04-17 15:42:12', DEFAULT);
INSERT INTO `notification` VALUES (27, 11, 9, 1, 29, 0, '用户9 赞了你的帖子', 1, '2026-04-23 23:00:27', DEFAULT);
INSERT INTO `notification` VALUES (28, 11, 9, 2, 32, 0, '用户9 赞了你的评论', 1, '2026-04-24 14:45:04', DEFAULT);
INSERT INTO `notification` VALUES (30, 11, 9, 4, 32, 42, '用户9 回复了你的评论', 1, '2026-04-21 11:35:06', DEFAULT);
INSERT INTO `notification` VALUES (31, 11, 9, 4, 32, 43, '用户9 回复了你的评论', 1, '2026-04-21 11:35:19', DEFAULT);
INSERT INTO `notification` VALUES (32, 11, 9, 4, 32, 47, '用户9 回复了你的评论', 1, '2026-04-21 11:44:13', DEFAULT);
INSERT INTO `notification` VALUES (33, 11, 9, 4, 32, 48, '用户9 回复了你的评论', 1, '2026-04-21 11:45:46', DEFAULT);
INSERT INTO `notification` VALUES (35, 11, 9, 4, 32, 49, '用户9 回复了你的评论', 1, '2026-04-21 12:40:26', DEFAULT);
INSERT INTO `notification` VALUES (36, 11, 9, 4, 32, 51, '用户9 回复了你的评论', 1, '2026-04-21 13:47:32', DEFAULT);
INSERT INTO `notification` VALUES (37, 11, 9, 4, 32, 52, '用户9 回复了你的评论', 1, '2026-04-21 13:47:39', DEFAULT);
INSERT INTO `notification` VALUES (39, 11, 9, 3, 31, 54, '用户9 评论了你的帖子', 1, '2026-04-21 13:58:45', DEFAULT);
INSERT INTO `notification` VALUES (40, 11, 9, 3, 31, 56, '用户9 评论了你的帖子', 1, '2026-04-21 13:58:58', DEFAULT);
INSERT INTO `notification` VALUES (41, 11, 9, 3, 31, 57, '用户9 评论了你的帖子', 1, '2026-04-21 13:59:15', DEFAULT);
INSERT INTO `notification` VALUES (42, 11, 9, 3, 31, 59, '用户9 评论了你的帖子', 1, '2026-04-21 13:59:41', DEFAULT);
INSERT INTO `notification` VALUES (43, 11, 9, 3, 31, 60, '用户9 评论了你的帖子', 1, '2026-04-21 13:59:54', DEFAULT);
INSERT INTO `notification` VALUES (44, 11, 9, 3, 31, 64, '用户9 评论了你的帖子', 1, '2026-04-21 14:00:43', DEFAULT);
INSERT INTO `notification` VALUES (45, 11, 9, 3, 31, 65, '用户9 评论了你的帖子', 1, '2026-04-21 14:00:45', DEFAULT);
INSERT INTO `notification` VALUES (46, 11, 9, 3, 31, 78, '用户9 评论了你的帖子', 1, '2026-04-21 15:26:00', DEFAULT);
INSERT INTO `notification` VALUES (47, 11, 9, 3, 31, 79, '用户9 评论了你的帖子', 1, '2026-04-21 15:26:05', DEFAULT);
INSERT INTO `notification` VALUES (48, 11, 9, 3, 31, 80, '用户9 评论了你的帖子', 1, '2026-04-21 15:26:09', DEFAULT);
INSERT INTO `notification` VALUES (49, 11, 9, 3, 31, 85, '用户9 评论了你的帖子', 1, '2026-04-21 16:33:27', DEFAULT);
INSERT INTO `notification` VALUES (63, 9, 11, 2, 32, 0, '用户11 赞了你的评论', 1, '2026-04-22 10:36:26', DEFAULT);
INSERT INTO `notification` VALUES (70, 9, 11, 3, 32, 86, '用户11 评论了你的帖子', 1, '2026-04-22 11:05:30', DEFAULT);
INSERT INTO `notification` VALUES (71, 9, 11, 3, 32, 88, '用户11 评论了你的帖子', 1, '2026-04-22 11:08:39', DEFAULT);
INSERT INTO `notification` VALUES (75, 9, 11, 3, 32, 89, '用户11 评论了你的帖子你真厉害', 1, '2026-04-22 11:14:57', DEFAULT);
INSERT INTO `notification` VALUES (76, 9, 11, 3, 32, 90, '用户11 评论了你的帖子 : nb', 1, '2026-04-22 11:15:56', DEFAULT);
INSERT INTO `notification` VALUES (125, 9, 11, 3, 32, 91, '用户11 评论了你的帖子 : 真厉害', 1, '2026-04-22 13:05:24', DEFAULT);
INSERT INTO `notification` VALUES (126, 9, 11, 3, 32, 92, '用户11 评论了你的帖子 : 阿迪斯记录反馈觉得撒库拉飞机立卡就撒付款了电视剧啊放假十六大会计法卡拉胶利丰达久啊索拉卡飞机算啦打卡机', 1, '2026-04-22 13:05:34', DEFAULT);
INSERT INTO `notification` VALUES (129, 10, 9, 1, 34, 0, '用户9 赞了你的帖子', 0, '2026-04-23 09:00:06', DEFAULT);
INSERT INTO `notification` VALUES (130, 10, 9, 6, 34, 0, '用户9 收藏了你的帖子', 0, '2026-04-23 23:02:55', DEFAULT);
INSERT INTO `notification` VALUES (132, 10, 9, 3, 34, 93, '用户9 评论了你的帖子 : 厉害厉害', 0, '2026-04-23 09:01:36', DEFAULT);
INSERT INTO `notification` VALUES (140, 11, 9, 5, 0, 0, '用户9 关注了你', 1, '2026-04-27 18:53:38', DEFAULT);
INSERT INTO `notification` VALUES (153, 9, 11, 5, 0, 0, '用户11 关注了你', 1, '2026-04-23 16:48:12', DEFAULT);
INSERT INTO `notification` VALUES (217, 13, 11, 5, 0, 0, '用户11 关注了你', 0, '2026-04-23 15:14:31', DEFAULT);
INSERT INTO `notification` VALUES (218, 18, 11, 5, 0, 0, '用户11 关注了你', 0, '2026-04-23 15:14:33', DEFAULT);
INSERT INTO `notification` VALUES (219, 13, 9, 5, 0, 0, '用户9 关注了你', 0, '2026-04-23 21:39:21', DEFAULT);
INSERT INTO `notification` VALUES (220, 18, 9, 5, 0, 0, '用户9 关注了你', 0, '2026-04-23 21:39:19', DEFAULT);
INSERT INTO `notification` VALUES (382, 10, 9, 1, 24, 0, '用户9 赞了你的帖子', 0, '2026-04-23 16:46:54', DEFAULT);
INSERT INTO `notification` VALUES (384, 11, 9, 3, 31, 94, '用户9 评论了你的帖子 : 厉害厉害', 1, '2026-04-23 16:47:22', DEFAULT);
INSERT INTO `notification` VALUES (385, 11, 9, 3, 31, 95, '用户9 评论了你的帖子 : hhhh', 1, '2026-04-23 16:47:30', DEFAULT);
INSERT INTO `notification` VALUES (429, 11, 9, 1, 18, 0, '用户9 赞了你的帖子', 1, '2026-04-23 22:15:50', DEFAULT);
INSERT INTO `notification` VALUES (512, 11, 9, 4, 32, 97, '用户9 回复了你的评论', 1, '2026-04-24 11:47:40', DEFAULT);
INSERT INTO `notification` VALUES (515, 11, 9, 4, 32, 98, '用户9 回复了你的评论', 1, '2026-04-24 14:45:00', DEFAULT);
INSERT INTO `notification` VALUES (520, 11, 9, 1, 17, 0, '用户9 赞了你的帖子', 1, '2026-04-26 14:35:20', DEFAULT);
INSERT INTO `notification` VALUES (526, 9, 11, 1, 82, 0, '用户11 赞了你的帖子', 1, '2026-04-27 17:52:50', DEFAULT);
INSERT INTO `notification` VALUES (527, 9, 11, 1, 77, 0, '用户11 赞了你的帖子', 1, '2026-04-27 18:01:50', DEFAULT);
INSERT INTO `notification` VALUES (528, 9, 11, 3, 77, 100, '用户11 评论了你的帖子 : 这条鱼好可爱', 1, '2026-04-27 18:02:04', DEFAULT);
INSERT INTO `notification` VALUES (529, 11, 9, 4, 77, 101, '用户9 回复了你的评论', 1, '2026-04-27 18:02:24', DEFAULT);
INSERT INTO `notification` VALUES (531, 11, 9, 3, 31, 102, '用户9 评论了你的帖子 : zs最nb', 1, '2026-04-27 18:44:18', DEFAULT);
INSERT INTO `notification` VALUES (538, 11, 23, 3, 30, 103, '用户23 评论了你的帖子 : 🙏', 1, '2026-04-27 18:53:41', DEFAULT);
INSERT INTO `notification` VALUES (542, 11, 23, 1, 30, 0, '用户23 赞了你的帖子', 1, '2026-04-27 18:54:32', DEFAULT);
INSERT INTO `notification` VALUES (543, 11, 23, 6, 30, 0, '用户23 收藏了你的帖子', 1, '2026-04-27 18:54:35', DEFAULT);
INSERT INTO `notification` VALUES (544, 23, 9, 3, 84, 104, '用户9 评论了你的帖子 : 给我尝尝', 0, '2026-04-27 19:04:29', DEFAULT);
INSERT INTO `notification` VALUES (545, 23, 9, 1, 84, 0, '用户9 赞了你的帖子', 0, '2026-05-23 13:24:36', DEFAULT);
INSERT INTO `notification` VALUES (546, 23, 9, 6, 84, 0, '用户9 收藏了你的帖子', 0, '2026-05-23 13:17:51', DEFAULT);
INSERT INTO `notification` VALUES (547, 23, 9, 5, 0, 0, '用户9 关注了你', 0, '2026-04-27 19:05:00', DEFAULT);
INSERT INTO `notification` VALUES (548, 23, 11, 5, 0, 0, '用户11 关注了你', 0, '2026-04-27 19:09:06', DEFAULT);
INSERT INTO `notification` VALUES (549, 23, 11, 3, 84, 105, '用户11 评论了你的帖子 : 我也要吃', 0, '2026-04-27 19:10:47', DEFAULT);
INSERT INTO `notification` VALUES (550, 23, 24, 1, 84, 0, '用户24 赞了你的帖子', 0, '2026-04-27 19:23:06', DEFAULT);
INSERT INTO `notification` VALUES (551, 23, 24, 3, 84, 106, '用户24 评论了你的帖子 : 嘿嘿嘿嘿歪歪我来啦嘻嘻嘻', 0, '2026-04-27 19:23:43', DEFAULT);
INSERT INTO `notification` VALUES (552, 23, 24, 3, 84, 107, '用户24 评论了你的帖子 : 你吃的什么 芸山季吗', 0, '2026-04-27 19:24:25', DEFAULT);
INSERT INTO `notification` VALUES (553, 23, 24, 5, 0, 0, '用户24 关注了你', 0, '2026-04-27 19:24:34', DEFAULT);
INSERT INTO `notification` VALUES (554, 24, 9, 5, 0, 0, '用户9 关注了你', 0, '2026-05-23 13:12:31', DEFAULT);
INSERT INTO `notification` VALUES (555, 24, 9, 2, 84, 0, '用户9 赞了你的评论', 1, '2026-04-27 20:34:42', DEFAULT);
INSERT INTO `notification` VALUES (556, 23, 9, 3, 84, 108, '用户9 评论了你的帖子 : 这是什么', 0, '2026-04-27 20:35:03', DEFAULT);
INSERT INTO `notification` VALUES (557, 24, 9, 4, 84, 108, '用户9 回复了你的评论', 1, '2026-04-27 20:35:03', DEFAULT);
INSERT INTO `notification` VALUES (558, 24, 9, 1, 86, 0, '用户9 赞了你的帖子', 0, '2026-05-23 13:24:10', DEFAULT);
INSERT INTO `notification` VALUES (559, 24, 9, 3, 86, 109, '用户9 评论了你的帖子 : 我也不想上课', 0, '2026-04-28 10:30:27', DEFAULT);
INSERT INTO `notification` VALUES (586, 24, 9, 6, 86, 0, '用户9 收藏了你的帖子', 0, '2026-05-23 13:24:11', DEFAULT);

-- ----------------------------
-- Table structure for pet
-- ----------------------------
DROP TABLE IF EXISTS `pet`;
CREATE TABLE `pet`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '宠物ID',
  `user_id` bigint NOT NULL COMMENT '所属用户ID',
  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '宠物名字',
  `type` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '宠物类型：狗/猫/其他',
  `birthday` date NULL DEFAULT NULL COMMENT '宠物生日',
  `age` int NULL DEFAULT 0 COMMENT '宠物年龄',
  `avatar` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '宠物头像',
  `info` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '宠物介绍',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_pet_user`(`user_id` ASC) USING BTREE,
  CONSTRAINT `fk_pet_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_bin COMMENT = '宠物信息表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of pet
-- ----------------------------
INSERT INTO `pet` VALUES (1, 9, '泡泡', '金鱼', '2023-05-20', 2, 'https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/pet_Images/aded2a7b66ea404facbdff13489af88a.webp', '一条圆滚滚的金鱼，尾巴像裙摆一样飘逸，游起来的时候会吐好多小泡泡，看着它游水就很解压。', '2026-04-13 16:15:22');
INSERT INTO `pet` VALUES (3, 11, '煤球', '猫', '2024-01-15', 1, 'https://avatar.com/cat2.jpg', '黑猫，胆子小，喜欢躲在沙发底下', '2026-04-13 16:15:22');
INSERT INTO `pet` VALUES (4, 11, '小白', '其他', '2023-08-08', 2, 'https://avatar.com/rabbit.jpg', '垂耳兔，很温顺，不爱动', '2026-04-13 16:15:22');
INSERT INTO `pet` VALUES (5, 13, '旺仔', '狗', '2021-12-25', 4, 'https://avatar.com/dog2.jpg', '金毛，性格温柔，会握手坐下', '2026-04-13 16:15:22');
INSERT INTO `pet` VALUES (6, 9, '瓜子', '仓鼠', '2022-03-15', 4, 'https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/pet_Images/62713b4f55904e878fe3ccf45d44a97d.jpg', '一只银狐仓鼠，是个 “囤货小能手”，颊囊里永远塞满了粮食，跑轮的时候会 “哒哒哒” 地跑个不停。', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (7, 9, '啾啾', '鸟类', '2021-09-20', 5, 'https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/pet_Images/b86374ca748a4902b130d27f2ead5dc8.webp', '活泼的虎皮鹦鹉，会学主人吹口哨，每天早上都会准时在笼子里唱歌，是家里的 “小闹钟”。', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (9, 9, '泡芙', '猫咪', '2020-12-05', 5, 'https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/pet_Images/8e2e4208238b49c1b829af52af101633.jpg', '布偶小母猫，毛软得像云朵，眼睛像蓝宝石，虽然有点怕生人，但熟了之后会主动蹭手要摸摸。', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (10, 9, '糯米', '兔子', '2022-08-18', 3, 'https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/pet_Images/985298fa84564700bc21db990cfaf5be.jpg', '一只白乎乎的垂耳兔，耳朵垂下来的时候软萌到犯规，最爱啃提摩西草和胡萝卜，吃东西的时候腮帮子鼓鼓的。', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (11, 10, '煤球', '猫', '2021-07-01', 5, 'https://picsum.photos/id/1079/200/200', '纯黑田园猫，傲娇又护主，晚上爱跑酷', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (12, 10, '旺财', '狗', '2020-11-11', 6, 'https://picsum.photos/id/1025/200/200', '金毛，暖男一枚，出门从不乱叫', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (13, 10, '绵绵', '其他', '2022-05-20', 4, 'https://picsum.photos/id/1062/200/200', '仓鼠，爱囤粮，圆滚滚的很可爱', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (14, 10, '年糕', '猫', '2023-02-14', 3, 'https://picsum.photos/id/451/200/200', '橘猫，能吃能睡，体重直线上升', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (15, 10, '来福', '狗', '2019-04-25', 7, 'https://picsum.photos/id/1059/200/200', '中华田园犬，忠诚护家，不挑食好养活', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (16, 11, '豆豆', '猫', '2021-02-03', 5, 'https://picsum.photos/id/1069/200/200', '美短虎斑，活泼好动，爱追激光笔', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (17, 11, '乐乐', '狗', '2022-08-08', 4, 'https://picsum.photos/id/1082/200/200', '柯基，爱撒娇，出门必被摸肚子', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (18, 11, '花花', '猫', '2020-09-10', 6, 'https://picsum.photos/id/1078/200/200', '布偶猫，颜值天花板，性格软萌粘人', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (19, 11, '球球', '其他', '2023-01-01', 3, 'https://picsum.photos/id/1083/200/200', '金丝熊，白天睡觉晚上蹦迪，跑轮声很大', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (20, 11, '朵朵', '狗', '2021-05-15', 5, 'https://picsum.photos/id/1060/200/200', '泰迪，聪明又粘人，会握手会坐下', '2026-04-14 16:18:13');
INSERT INTO `pet` VALUES (22, 9, '年糕', '狗狗', '2026-04-10', 2, 'https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/pet_Images/cb36ae7d02e848faa2c5912c7fc6fa5c.webp', '一只粘人又热情的小土狗，最爱跟主人出门散步，跑起来像个小肉团子，每次回家都会摇着尾巴扑过来，治愈感拉满。', '2026-04-24 13:21:46');

-- ----------------------------
-- Table structure for post
-- ----------------------------
DROP TABLE IF EXISTS `post`;
CREATE TABLE `post`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '帖子ID',
  `user_id` bigint NOT NULL COMMENT '发布用户ID',
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL COMMENT '帖子内容（文字）',
  `images` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL COMMENT '图片列表(JSON数组)',
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT 'normal' COMMENT '类型：normal/宠物分享/心理倾诉',
  `tags` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '标签（如：焦虑/猫咪/日常）',
  `view_count` int NULL DEFAULT 0 COMMENT '浏览量',
  `like_count` int NULL DEFAULT 0 COMMENT '点赞数',
  `favorite_count` int NULL DEFAULT 0 COMMENT '收藏数',
  `comment_count` int NULL DEFAULT 0 COMMENT '评论数',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常 0删除',
  `is_top` tinyint NULL DEFAULT 0 COMMENT '是否置顶',
  `is_anonymous` tinyint NULL DEFAULT 0 COMMENT '是否匿名（心理模块用）',
  `is_official` tinyint NULL DEFAULT 0 COMMENT '是否官方文章：0普通帖子 1官方文章',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  `title` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '标题',
  `season` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '季节：spring 夏季 summer 秋季 autumn 冬季 winter',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `fk_post_user`(`user_id` ASC) USING BTREE,
  INDEX `idx_post_create_time_id`(`create_time` ASC, `id` ASC) USING BTREE,
  INDEX `idx_post_status`(`status` ASC) USING BTREE,
  CONSTRAINT `fk_post_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 88 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_bin COMMENT = '帖子表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post
-- ----------------------------
INSERT INTO `post` VALUES (1, 10, '春季推驱虫：春季寄生虫活跃，建议每月体外驱虫、每3个月体内驱虫，保护宠物健康春季推驱虫：春季寄生虫活跃，建议每月体外驱虫、每3个月体内驱虫，保护宠物健康春季推驱虫：春季寄生虫活跃，建议每月体外驱虫、每3个月体内驱虫，保护宠物健康春季推驱虫：春季寄生虫活跃，建议每月体外驱虫、每3个月体内驱虫，保护宠物健康春季推驱虫：春季寄生虫活跃，建议每月体外驱虫、每3个月体内驱虫，保护宠物健康', '', '宠物分享', NULL, 1, 0, 0, 0, 1, 0, 0, 1, '2026-04-13 16:13:26', '2026-05-23 13:13:47', '1', 'spring');
INSERT INTO `post` VALUES (2, 10, '夏季推防暑：避免正午遛宠、提供充足饮水、常备冰垫，防止宠物中暑', '', '宠物分享', NULL, 0, 0, 0, 0, 1, 0, 0, 1, '2026-04-13 16:13:26', '2026-04-24 13:42:31', '2', 'summer');
INSERT INTO `post` VALUES (3, 10, '秋季推换毛护理：每天梳毛、补充鱼油、保持环境清洁，减少掉毛与毛球', '', '宠物分享', NULL, 0, 0, 0, 0, 1, 0, 0, 1, '2026-04-13 16:13:26', '2026-04-24 13:42:29', '3', 'autumn');
INSERT INTO `post` VALUES (4, 10, '冬季推保暖措施：短毛宠穿保暖衣、避免睡冰凉地面、夜间关好门窗防风', '', '宠物分享', NULL, 0, 0, 0, 0, 1, 0, 0, 1, '2026-04-13 16:13:26', '2026-04-24 13:42:29', '4', 'winter');
INSERT INTO `post` VALUES (6, 11, '今日遛狗日常，阳光超好～', '', 'normal', NULL, 0, 0, 0, 0, 1, 0, 0, 0, '2026-04-13 16:13:33', '2026-04-24 13:42:37', '6', NULL);
INSERT INTO `post` VALUES (7, 13, '求推荐好用的宠物沐浴露', '', '宠物分享', NULL, 2, 0, 0, 0, 1, 0, 0, 0, '2026-04-13 16:13:33', '2026-04-24 13:42:31', '7', NULL);
INSERT INTO `post` VALUES (8, 9, '00000000f', '', NULL, NULL, 0, 0, 0, 0, 1, 0, 0, 0, '2026-04-14 14:53:46', '2026-04-24 13:42:27', '大红', NULL);
INSERT INTO `post` VALUES (10, 9, '最近压力好大，经常失眠，有没有一样的朋友？', '', '心理倾诉', '焦虑,压力,失眠', 0, 0, 0, 0, 1, 0, 1, 0, '2026-04-14 15:08:24', '2026-04-24 13:42:27', '压力大到失眠，求安慰', NULL);
INSERT INTO `post` VALUES (11, 9, '情绪低落了很久，不知道怎么调节', '', '心理倾诉', '情绪低落,抑郁情绪', 0, 0, 0, 0, 1, 0, 1, 0, '2026-04-14 15:08:24', '2026-04-24 13:42:28', '情绪低落该怎么自我调节', NULL);
INSERT INTO `post` VALUES (12, 9, '分享一个缓解焦虑的小方法：深呼吸+冥想', '', '心理倾诉', '焦虑,缓解方法', 0, 0, 0, 0, 1, 0, 0, 0, '2026-04-14 15:08:24', '2026-04-24 13:42:26', '分享一个我亲测有效的缓解焦虑方法', NULL);
INSERT INTO `post` VALUES (13, 10, '我家猫咪今天终于肯让我摸啦！太开心了', '[\"cat1.jpg\"]', '宠物分享', '猫咪,可爱,宠物日常', 0, 0, 0, 0, 1, 0, 0, 1, '2026-04-14 15:08:24', '2026-04-16 16:20:33', '我家粘人小猫咪上线', 'winter');
INSERT INTO `post` VALUES (14, 10, '狗狗陪我度过了最难熬的日子，感恩有你', '[\"dog1.jpg\"]', '宠物分享', '狗狗,治愈,陪伴', 0, 0, 0, 0, 1, 0, 0, 1, '2026-04-14 15:08:24', '2026-04-16 16:20:30', '我的狗狗是我的精神支柱', 'autumn');
INSERT INTO `post` VALUES (15, 10, '新手养猫必看！这些坑千万别踩', '', '宠物分享', '养猫攻略,新手', 0, 0, 0, 0, 1, 0, 0, 1, '2026-04-14 15:08:24', '2026-04-24 13:42:25', '新手养猫攻略｜避坑指南', 'autumn');
INSERT INTO `post` VALUES (16, 10, '宠物真的能治愈一切不开心', '[\"pet_happy.jpg\"]', '宠物分享', '治愈,宠物', 0, 0, 0, 0, 1, 0, 0, 1, '2026-04-14 15:08:24', '2026-04-16 16:20:27', '被我的小宝贝治愈的每一天', 'summer');
INSERT INTO `post` VALUES (17, 11, '今天天气很好，心情也跟着变好了', '', 'normal', '日常,心情', 0, 1, 0, 0, 1, 0, 0, 0, '2026-04-14 15:08:24', '2026-04-26 14:35:20', '今日份好心情', NULL);
INSERT INTO `post` VALUES (18, 11, '社区官方提醒：文明发言，互相尊重', '', 'normal', '社区公告,官方', 1, 0, 0, 0, 1, 0, 0, 0, '2026-04-14 15:08:24', '2026-04-24 13:42:46', '【社区公告】文明交流公约', '');
INSERT INTO `post` VALUES (19, 11, '分享一首治愈的歌，心情不好可以听听', '', 'normal', '治愈,音乐', 0, 0, 0, 0, 1, 0, 0, 0, '2026-04-14 15:08:24', '2026-04-24 13:42:23', '治愈歌单分享', NULL);
INSERT INTO `post` VALUES (20, 11, '记录生活中的小美好', '[\"life1.jpg\"]', 'normal', '生活记录', 0, 0, 0, 0, 1, 0, 0, 0, '2026-04-14 15:08:24', '2026-04-14 15:08:24', '生活碎片记录', NULL);
INSERT INTO `post` VALUES (21, 9, '最近压力好大，经常失眠，有没有一样的朋友？', '[\"https://picsum.photos/id/237/600/400\"]', '心理倾诉', '焦虑,压力,失眠', 0, 0, 0, 0, 1, 0, 1, 0, '2026-04-14 16:21:46', '2026-04-14 16:21:46', '压力大到失眠，求安慰', NULL);
INSERT INTO `post` VALUES (22, 9, '情绪低落了很久，不知道怎么调节，连我家猫都不怎么理我了', '[\"https://picsum.photos/id/40/600/400\"]', '心理倾诉', '情绪低落,抑郁情绪,宠物陪伴', 2, 0, 0, 0, 1, 0, 1, 0, '2026-04-14 16:21:46', '2026-04-27 19:10:12', '情绪低落时，连猫都在安慰我', NULL);
INSERT INTO `post` VALUES (23, 9, '分享一个缓解焦虑的小方法：深呼吸+冥想，亲测有效', '[\"https://picsum.photos/id/1005/600/400\"]', '心理倾诉', '焦虑,缓解方法,冥想', 2, 0, 0, 1, 1, 0, 0, 0, '2026-04-14 16:21:46', '2026-04-21 16:04:17', '亲测有效的焦虑缓解方法', NULL);
INSERT INTO `post` VALUES (24, 10, '我家煤球今天终于肯让我抱了！开心到原地起飞！', '[\"https://picsum.photos/id/1079/600/400\"]', '宠物分享', '猫咪,可爱,宠物日常', 1, 1, 0, 0, 1, 0, 0, 1, '2026-04-14 16:21:46', '2026-04-23 23:06:36', '高冷煤球终于肯贴贴了！', 'summer');
INSERT INTO `post` VALUES (25, 10, '我的金毛旺财，是我情绪崩溃时唯一的依靠', '[\"https://picsum.photos/id/1025/600/400\"]', '宠物分享', '狗狗,治愈,陪伴', 0, 0, 0, 0, 1, 0, 0, 1, '2026-04-14 16:21:46', '2026-04-16 16:20:24', '被金毛治愈的每一天', 'summer');
INSERT INTO `post` VALUES (26, 10, '新手养猫必看！这些坑千万别踩，血泪教训！', '[\"https://picsum.photos/id/451/600/400\"]', '宠物分享', '养猫攻略,新手,避坑', 0, 0, 0, 0, 1, 0, 0, 1, '2026-04-14 16:21:46', '2026-04-16 16:20:20', '新手养猫避坑指南', 'spring');
INSERT INTO `post` VALUES (27, 10, '仓鼠绵绵的囤粮现场，粮仓都快被它搬空了', '[\"https://picsum.photos/id/1062/600/400\"]', '宠物分享', '仓鼠,可爱,日常', 0, 0, 0, 100, 1, 0, 0, 1, '2026-04-14 16:21:46', '2026-04-16 16:47:45', '小仓鼠的囤粮日记', 'spring');
INSERT INTO `post` VALUES (28, 11, '今天天气很好，带着我家柯基乐乐出门散步，心情都变好了', '[\"https://picsum.photos/id/1082/600/400\"]', 'normal', '日常,宠物,心情', 1, 0, 0, 0, 1, 0, 0, 0, '2026-04-14 16:21:46', '2026-04-24 12:50:37', '被阳光和柯基治愈的周末', NULL);
INSERT INTO `post` VALUES (29, 11, '【官方公告】社区文明交流公约，一起守护温暖的小空间', '[\"https://picsum.photos/id/1015/600/400\"]', 'normal', '社区公告,官方,文明交流', 1, 1, 0, 0, 1, 0, 0, 0, '2026-04-14 16:21:46', '2026-04-23 23:00:27', '【社区公告】文明交流公约', '');
INSERT INTO `post` VALUES (30, 11, '分享一首治愈的歌，心情不好的时候就循环播放', '[\"https://picsum.photos/id/1019/600/400\"]', 'normal', '治愈,音乐,情绪调节', 9, 1, 1, 1, 1, 0, 0, 0, '2026-04-14 16:21:46', '2026-04-27 19:05:39', '私藏治愈歌单分享', NULL);
INSERT INTO `post` VALUES (31, 11, '记录我家布偶花花的神仙颜值，谁懂啊！记录我家布偶花花的神仙颜值，谁懂啊！记录我家布偶花花的神仙颜值，谁懂啊！', '[\"https://vcg00.cfp.cn/creative/vcg/800/new/VCG211124576228.jpg\", \"https://vcg00.cfp.cn/creative/vcg/800/new/VCG41N1163321740.jpg\"]', 'normal', '宠物,布偶猫,颜值', 61, 1, 1, 14, 1, 0, 0, 0, '2026-04-14 16:21:46', '2026-04-27 18:44:18', '布偶猫的颜值暴击！', NULL);
INSERT INTO `post` VALUES (32, 9, '分享一下我家萌宠', '[\"https://vcg00.cfp.cn/creative/vcg/800/new/VCG41N923200682.jpg\", \"https://vcg02.cfp.cn/creative/vcg/800/new/VCG41N1367599532.jpg\"]', 'normal', '', 143, 2, 1, 48, 1, 0, 0, 0, '2026-04-16 09:46:06', '2026-05-23 13:13:26', '看看我家狗狗', NULL);
INSERT INTO `post` VALUES (34, 10, '春季帖子', '[\"https://vcg00.cfp.cn/creative/vcg/800/new/VCG41N923200682.jpg\"]', NULL, NULL, 12, 0, 0, 1, 1, 0, 0, 1, '2026-04-16 09:47:46', '2026-04-23 20:58:37', NULL, 'spring');
INSERT INTO `post` VALUES (76, 9, 'oss', '[\"https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/pet_Images/1acdb311950f435395758f602b550c44.jpg\"]', 'normal', '日常', 9, 0, 0, 0, 1, 0, 0, 0, '2026-04-26 12:56:08', '2026-05-23 13:13:22', '测试oss', '');
INSERT INTO `post` VALUES (77, 9, 'dsafdklsaj', '[\"https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/post_Images/4af78b114efa4e759b4a04a4ce5dc333.webp\",\"https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/post_Images/decc514dbb7e4685a10fd012b0536d97.jpg\",\"https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/post_Images/43e250ad911e4a159501b17495fc9f5d.png\"]', 'normal', '日常', 9, 1, 1, 2, 1, 0, 0, 0, '2026-04-26 13:03:23', '2026-04-30 22:12:07', 'osososos', '');
INSERT INTO `post` VALUES (78, 9, '达萨罗咖啡机上课的垃圾理发', '', 'normal', '日常', 9, 0, 0, 0, 1, 0, 0, 0, '2026-04-26 13:27:43', '2026-04-27 18:51:59', '非建档立卡数据库反馈', '');
INSERT INTO `post` VALUES (82, 9, '1321231', '[\"https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/post_Images/d99857b803dd4c178facf0fa6b787201.webp\",\"https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/post_Images/f450f5089c0f4f1e953a46e204aa248b.jpg\",\"https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/post_Images/f4b1666f80d243a1a8894263706c799e.webp\"]', 'normal', '猫咪', 15, 2, 0, 0, 1, 0, 0, 0, '2026-04-26 13:39:31', '2026-05-23 13:25:56', '1421313', '');
INSERT INTO `post` VALUES (84, 23, '好吃😋', '[\"https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/post_Images/252e9ca5b4224c83b447fd04bc323bda.jpg\"]', 'normal', '日常,美食', 12, 2, 1, 5, 1, 0, 0, 0, '2026-04-27 18:55:35', '2026-05-23 13:25:48', '嗨咯', '');
INSERT INTO `post` VALUES (86, 24, '不想上课', '[\"https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/post_Images/845aa619731348b59ea5352de1be43fe.jpeg\"]', 'normal', '日常', 9, 1, 1, 1, 1, 0, 0, 0, '2026-04-27 20:39:59', '2026-05-23 13:25:45', '不想上课', '');

-- ----------------------------
-- Table structure for post_like
-- ----------------------------
DROP TABLE IF EXISTS `post_like`;
CREATE TABLE `post_like`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '点赞ID',
  `user_id` bigint NOT NULL COMMENT '用户ID',
  `post_id` bigint NOT NULL COMMENT '帖子ID',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '点赞时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_user_post`(`user_id` ASC, `post_id` ASC) USING BTREE,
  INDEX `fk_pl_post`(`post_id` ASC) USING BTREE,
  CONSTRAINT `fk_pl_post` FOREIGN KEY (`post_id`) REFERENCES `post` (`id`) ON DELETE CASCADE ON UPDATE RESTRICT,
  CONSTRAINT `fk_pl_user` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 234 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_bin COMMENT = '帖子点赞表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of post_like
-- ----------------------------
INSERT INTO `post_like` VALUES (34, 11, 31, '2026-04-17 13:58:21');
INSERT INTO `post_like` VALUES (145, 9, 24, '2026-04-23 16:46:54');
INSERT INTO `post_like` VALUES (149, 9, 29, '2026-04-23 23:00:27');
INSERT INTO `post_like` VALUES (181, 9, 32, '2026-04-24 12:41:23');
INSERT INTO `post_like` VALUES (184, 11, 32, '2026-04-24 14:50:05');
INSERT INTO `post_like` VALUES (188, 9, 17, '2026-04-26 14:35:20');
INSERT INTO `post_like` VALUES (192, 11, 82, '2026-04-27 17:52:50');
INSERT INTO `post_like` VALUES (193, 11, 77, '2026-04-27 18:01:50');
INSERT INTO `post_like` VALUES (194, 23, 30, '2026-04-27 18:54:32');
INSERT INTO `post_like` VALUES (196, 24, 84, '2026-04-27 19:23:06');
INSERT INTO `post_like` VALUES (225, 9, 86, '2026-05-23 13:24:10');
INSERT INTO `post_like` VALUES (231, 9, 84, '2026-05-23 13:24:36');
INSERT INTO `post_like` VALUES (233, 9, 82, '2026-05-23 13:25:33');

-- ----------------------------
-- Table structure for user
-- ----------------------------
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user`  (
  `id` bigint NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '用户名（唯一）',
  `password` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NOT NULL COMMENT '密码（加密）',
  `nickname` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '昵称',
  `avatar` varchar(512) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '头像URL',
  `signature` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '个性签名',
  `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '手机号',
  `email` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '邮箱',
  `role` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT 'user' COMMENT '角色：user/admin',
  `status` tinyint NULL DEFAULT 1 COMMENT '状态：1正常 0禁用',
  `level` int NULL DEFAULT 1 COMMENT '用户等级',
  `points` int NULL DEFAULT 0 COMMENT '积分',
  `follow_count` int NULL DEFAULT 0 COMMENT '关注数',
  `fans_count` int NULL DEFAULT 0 COMMENT '粉丝数',
  `like_count` int NULL DEFAULT 0 COMMENT '获赞总数',
  `mood_status` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '当前情绪（开心/焦虑/低落）',
  `mental_tag` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '心理标签',
  `last_login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `last_login_ip` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_bin NULL DEFAULT NULL COMMENT '最后登录IP',
  `create_time` datetime NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `uk_username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 25 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_bin COMMENT = '用户表' ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user
-- ----------------------------
INSERT INTO `user` VALUES (1, 'admin', '$2a$10$ddAV/m6qF9moOevuwXG8t.t4RVqJUyh9R1UaiUqGIREGo3q.EerQ.', '管理员', 'https://avatar.com/admin.jpg', '宠物社区管理员', '13800138000', 'admin@pet.com', 'admin', 1, 10, 1000, 0, 2, 0, '开心', '无', '2026-04-08 10:00:00', '127.0.0.1', '2026-04-08 11:44:26', '2026-04-24 11:29:27');
INSERT INTO `user` VALUES (9, 'lx', '$2a$10$ddAV/m6qF9moOevuwXG8t.t4RVqJUyh9R1UaiUqGIREGo3q.EerQ.', '阳光小猫', 'https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/user_Images/805d207e32a04a3685426d846a9deedd.jpg', '这个世界偶尔薄凉，内心要学会温暖', '', '', 'user', 1, 1, 0, 4, 1, 9, '平静', '宠物达人', NULL, NULL, '2026-04-08 16:23:54', '2026-05-23 13:25:33');
INSERT INTO `user` VALUES (10, 'yc', '$2a$10$ddAV/m6qF9moOevuwXG8t.t4RVqJUyh9R1UaiUqGIREGo3q.EerQ.', '管理员1', 'https://avatar.com/admin.jpg', NULL, NULL, NULL, 'admin', 1, 1, 0, 0, 1, 1, NULL, NULL, NULL, NULL, '2026-04-08 19:06:50', '2026-04-27 18:53:48');
INSERT INTO `user` VALUES (11, 'zs', '$2a$10$ddAV/m6qF9moOevuwXG8t.t4RVqJUyh9R1UaiUqGIREGo3q.EerQ.', '用户11', 'https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/user_Images/1a32a49fb8754d7a8c871b8aec9584c8.jpg', '随便写点', '', '', 'user', 1, 1, 0, 4, 1, 4, '开心', '宠物达人', NULL, NULL, '2026-04-09 10:16:28', '2026-04-27 19:09:06');
INSERT INTO `user` VALUES (13, 'yy', '$2a$10$ddAV/m6qF9moOevuwXG8t.t4RVqJUyh9R1UaiUqGIREGo3q.EerQ.', '用户用户13', 'https://img.shetu66.com/2023/06/26/1687770031227597.png', NULL, NULL, NULL, 'user', 1, 1, 0, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-04-10 11:04:23', '2026-04-24 11:29:24');
INSERT INTO `user` VALUES (18, 'ls', '$2a$10$ddAV/m6qF9moOevuwXG8t.t4RVqJUyh9R1UaiUqGIREGo3q.EerQ.', '用户KweZSEC7xSdQe6QT', 'https://avatar.com/admin.jpg', NULL, NULL, NULL, 'user', 1, 1, 0, 0, 1, 0, NULL, NULL, NULL, NULL, '2026-04-15 10:46:09', '2026-04-24 11:29:25');
INSERT INTO `user` VALUES (22, 'xxx', '$2a$10$ddAV/m6qF9moOevuwXG8t.t4RVqJUyh9R1UaiUqGIREGo3q.EerQ.', '用户N8GcVPLYN7jHRA72', NULL, NULL, NULL, NULL, 'user', 1, 1, 0, 0, 0, 0, NULL, NULL, NULL, NULL, '2026-04-24 11:28:47', '2026-04-24 11:28:47');
INSERT INTO `user` VALUES (23, '13712125041', '$2a$10$DmB0BbJhcTPN886uz64ZjeOlfiuIZACBwZ/tBipl4kckSI5Z7AyF.', '歪歪', 'https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/user_Images/ec13f61d2bb84380b96cb0f97ee79905.jpg', '', '', '', 'user', 1, 1, 0, 0, 3, 2, '', '', NULL, NULL, '2026-04-27 18:53:03', '2026-05-23 13:24:36');
INSERT INTO `user` VALUES (24, '18938201123', '$2a$10$HLAzlPXDSwBou9Lf3QD91uyM9sHvNgASKOoaykFtHRvIWFG1VHgf.', '歪dog', 'https://pet-owner-community.oss-cn-guangzhou.aliyuncs.com/user_Images/a2347ac34689417fb2396a2a27c3dd57.jpeg', '我最帅', '', '', 'user', 1, 1, 0, 1, 1, 1, '', '', NULL, NULL, '2026-04-27 19:22:30', '2026-05-23 13:24:10');

SET FOREIGN_KEY_CHECKS = 1;
