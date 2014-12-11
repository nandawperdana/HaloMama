package it.fhab.aws.dynamodb.tablerow;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "Fhab-Questions")
public class Question {	
	
	private String emotionId;
	private String createdDate;
	private String question;
	
//	public People(){
//		
//	}
	
//	public People (String cognitoId, String deviceOS){
//		this.cognitoId = cognitoId;
//		this.deviceOS = deviceOS;
//	}

	public void signUp(String userNameTwitter, String fullName){
		this.question = userNameTwitter;
//		this.fullName = fullName;
		this.createdDate = ""+System.currentTimeMillis();
//		this.lastLogin = ""+System.currentTimeMillis();
	}
	
	public void signIn(String userNameTwitter, String fullName){
		this.question = userNameTwitter;
//		this.lastLogin = ""+System.currentTimeMillis();
	}
	
	public void deactivate(String userNameTwitter, String fullName){
		this.question = userNameTwitter;
//		this.lastLogin = ""+System.currentTimeMillis();
//		this.status = "deactivate";
	}
	
	@DynamoDBHashKey(attributeName = "EmotionId")
	public String getEmotionId() {
		return emotionId;
	}
	public void setEmotionId(String cognitoId) {
		this.emotionId = cognitoId;
	}
	
	@DynamoDBAttribute(attributeName = "Question")
	public String getQuestion() {
		return createdDate;
	}
	public void setQuestion(String question) {
		this.createdDate = question;
	}
	
	@DynamoDBRangeKey(attributeName = "CreatedDate")
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
}
