package com.suspend.querybuilder;

import com.suspend.annotation.ManyToOne;

import javax.management.Query;
import java.util.Map;

public interface QueryBuilder {

    QueryBuilder select();

    QueryBuilder insert();

    QueryBuilder update();

    <T> QueryBuilder byId(T id);

    <T> QueryBuilder deleteById(T id);

    QueryBuilder where(String... whereParams);

    QueryBuilder where(Map<String, Object> params);

    QueryBuilder join();

    String build();
}
