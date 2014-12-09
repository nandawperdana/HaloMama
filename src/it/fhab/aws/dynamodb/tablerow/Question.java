package it.fhab.aws.dynamodb.tablerow;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;

public class Question {

	private String emotionId;
	private String createdDate;
	private String question;
	
	@DynamoDBHashKey(attributeName = "EmotionId")
	public void setEmotionId(String emotionId) {
		this.emotionId = emotionId;
	}
	public String getEmotionId() {
		return emotionId;
	}
	
	@DynamoDBRangeKey(attributeName = "CreatedDate")
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	@DynamoDBAttribute(attributeName = "question")
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
}
