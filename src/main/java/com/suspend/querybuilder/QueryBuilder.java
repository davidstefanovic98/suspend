package com.suspend.querybuilder;

public interface QueryBuilder {

    QueryBuilder select();

    QueryBuilder insert();

//    QueryBuilder delete();

    QueryBuilder update();

    String build();
}
