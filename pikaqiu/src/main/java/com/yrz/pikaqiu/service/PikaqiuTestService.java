package com.yrz.pikaqiu.service;

import com.yrz.pikaqiu.mapper.ds1.PikaqiuTestMapper;
import com.yrz.pikaqiu.mapper.ds2.SoulTestMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PikaqiuTestService {

    @Autowired
    private PikaqiuTestMapper pikaqiuTestMapper;

    @Autowired
    private SoulTestMapper soulTestMapper;

    @Transactional(rollbackFor = Exception.class)
    public void insert(){
        pikaqiuTestMapper.insertOne();
        soulTestMapper.insertOne();
    }
}
