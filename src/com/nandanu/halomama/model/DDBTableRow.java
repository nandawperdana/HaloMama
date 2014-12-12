package com.nandanu.halomama.model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;

public abstract class DDBTableRow {
	protected String tableName;
	protected String hashKey;
	protected String rangeKey;
	protected DynamoDBScanExpression scanExpression;
	protected DynamoDBQueryExpression<?> queryExpression;

	public String getHashKey() {
		return hashKey;
	}

	public String getRangeKey() {
		return rangeKey;
	}

	public String getTableName() {
		return tableName;
	}

	public DynamoDBQueryExpression<?> getQueryExpression() {
		return queryExpression;
	}

	public DynamoDBScanExpression getScanExpression() {
		return scanExpression;
	}
}
