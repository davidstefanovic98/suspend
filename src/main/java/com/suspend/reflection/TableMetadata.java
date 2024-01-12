package com.suspend.reflection;

import java.util.List;

public record TableMetadata(String tableName, List<ColumnMetadata> columns) {}
