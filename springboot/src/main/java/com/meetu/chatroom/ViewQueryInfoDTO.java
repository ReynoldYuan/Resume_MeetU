package com.meetu.chatroom;

import lombok.Data;

@Data
public class ViewQueryInfoDTO {
    private String userId;
    private String type;
    // 添加其他需要的字段
    // 例如：
    // private String queryParam;
    // private Integer pageNum;
    // private Integer pageSize;
}