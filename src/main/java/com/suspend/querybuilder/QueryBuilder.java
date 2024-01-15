package com.suspend.querybuilder;

import java.util.Map;

public interface QueryBuilder {

    QueryBuilder select(String... columns);

    QueryBuilder insert();

    QueryBuilder update();

    <T> QueryBuilder byId(T id);

    <T> QueryBuilder deleteById(T id);

    QueryBuilder where(String... whereParams);

    QueryBuilder where(Map<String, Object> params);

    String build();
}
