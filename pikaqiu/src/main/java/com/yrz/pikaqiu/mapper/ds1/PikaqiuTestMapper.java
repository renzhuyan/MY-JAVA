package com.yrz.pikaqiu.mapper.ds1;

import org.apache.ibatis.annotations.Insert;

public interface PikaqiuTestMapper {
    @Insert("insert into druid_test values ('4','4')")
    void insertOne();
}
