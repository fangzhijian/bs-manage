CREATE TABLE `tm_customer_service_chat_record_content` (
                                                           `id` bigint NOT NULL COMMENT '主键',
                                                           `content_type` tinyint NOT NULL COMMENT '会话类型 1-文字 2-图片 3-语音',
                                                           `content` varchar(2000) COLLATE utf8mb4_general_ci NOT NULL COMMENT '会话内容',
                                                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT='客服会话记录内容';