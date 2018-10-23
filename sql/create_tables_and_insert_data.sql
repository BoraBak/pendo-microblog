#create 3 tables: user, post, rate
CREATE TABLE `post` (
  `id` varchar(255) NOT NULL,
  `user_id` varchar(255) DEFAULT NULL,
  `text` varchar(1023) DEFAULT NULL,
  `score` int(11) DEFAULT NULL,
  `create_time` datetime DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `rate` (
  `post_id` varchar(255) NOT NULL,
  `user_id` varchar(255) NOT NULL,
  `is_rated` tinyint(1) DEFAULT NULL,
  PRIMARY KEY (`post_id`,`user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
  `id` varchar(255) NOT NULL,
  `name` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

#insert to table `user`
INSERT INTO `user` (`id`,`name`) VALUES ('user1','oneUser');
INSERT INTO `user`(`id`,`name`) VALUES ('user2','twoUser');
INSERT INTO `user`(`id`,`name`) VALUES ('user3','threeUser');
INSERT INTO `user`(`id`,`name`) VALUES ('user4','fourUser');
INSERT INTO `user`(`id`,`name`) VALUES ('user5','fiveUser');

#insert to table `post`
INSERT INTO `post`(`id`, `user_id`, `text`, `score`, `create_time`) VALUES ('post1','user1','myText1','4','2018-10-22 13:16:45');
INSERT INTO `post`(`id`, `user_id`, `text`, `score`, `create_time`) VALUES ('post2','user1','myText2','5','2018-10-23 13:16:45');
INSERT INTO `post`(`id`, `user_id`, `text`, `score`, `create_time`) VALUES ('post3','user2','myText3','2','2018-10-24 13:16:45');
INSERT INTO `post`(`id`, `user_id`, `text`, `score`, `create_time`) VALUES ('post4','user2','myText4','8','2018-10-24 13:16:45');
INSERT INTO `post`(`id`, `user_id`, `text`, `score`, `create_time`) VALUES ('post5','user3','myText5','3','2018-10-22 13:16:45');

#insert to table `rate`
INSERT INTO `rate`(`post_id`, `user_id`, `is_rated`) VALUES ('post1', 'user1', '1');
INSERT INTO `rate`(`post_id`, `user_id`, `is_rated`) VALUES ('post2', 'user1', '1');
INSERT INTO `rate`(`post_id`, `user_id`, `is_rated`) VALUES ('post3', 'user2', '0');
INSERT INTO `rate`(`post_id`, `user_id`, `is_rated`) VALUES ('post4', 'user2', '0');
INSERT INTO `rate`(`post_id`, `user_id`, `is_rated`) VALUES ('post5', 'user3', '0');
