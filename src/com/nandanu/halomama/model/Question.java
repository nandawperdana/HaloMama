package com.nandanu.halomama.model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "Fhab-Questions")
public class Question {

	private String emotionId;
	private String createdDate;
	private String question;

	@DynamoDBHashKey(attributeName = Constants.TAG_EMOTION_ID)
	public String getEmotionId() {
		return emotionId;
	}

	public void setEmotionId(String cognitoId) {
		this.emotionId = cognitoId;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_QUESTION)
	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	@DynamoDBRangeKey(attributeName = Constants.TAG_CREATED_DATE)
	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

}
