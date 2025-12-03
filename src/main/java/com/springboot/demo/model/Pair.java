package com.springboot.demo.model;

import lombok.Data;

/**
 *
 * @author fangzhijian
 * @Desc
 * @date 2025/10/11 10:31
 */
@Data
public class Pair<K,T> {

    private K key;
    private T value;

    public Pair(K key, T value) {
        this.key = key;
        this.value = value;
    }
}
