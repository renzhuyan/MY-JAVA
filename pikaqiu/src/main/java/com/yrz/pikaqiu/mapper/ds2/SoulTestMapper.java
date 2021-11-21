package com.yrz.pikaqiu.mapper.ds2;

import org.apache.ibatis.annotations.Insert;

public interface SoulTestMapper {

    @Insert("insert into soul_test values ('4')")
    void insertOne();
}
