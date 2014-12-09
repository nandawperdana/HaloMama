package it.fhab.aws.dynamodb.tablerow;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "Fhab-HaloMama-Production")
public class HaloMama extends DDBTableRow {	

	private String userNameTwitter;
	private String createdDate;
	private String emotionId;
	private Boolean seen;
	private Boolean deviceOS;
	private Boolean status;
	private String tweetId;
	private String questionId;
	private String avatarURL;
	private String description;
	
	@DynamoDBHashKey(attributeName = "UserNameTwitter")
	public String getUserNameTwitter() {
		return userNameTwitter;
	}
	public void setUserNameTwitter(String userNameTwitter) {
		this.userNameTwitter = userNameTwitter;
	}
	
	@DynamoDBRangeKey(attributeName = "CreatedDate")
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	@DynamoDBAttribute(attributeName = "EmotionId")
	public String getEmotionId() {
		return emotionId;
	}
	public void setEmotionId(String emotionId) {
		this.emotionId = emotionId;
	}
	
	@DynamoDBAttribute(attributeName = "Seen")
	public Boolean getSeen() {
		return seen;
	}
	public void setSeen(Boolean seen) {
		this.seen = seen;
	}
	
	@DynamoDBAttribute(attributeName = "DeviceOS")
	public Boolean getDeviceOS() {
		return deviceOS;
	}
	public void setDeviceOS(Boolean deviceOS) {
		this.deviceOS = deviceOS;
	}
	
	@DynamoDBAttribute(attributeName = "Status")
	public Boolean getStatus() {
		return status;
	}
	public void setStatus(Boolean status) {
		this.status = status;
	}
	
	@DynamoDBAttribute(attributeName = "TweetId")
	public String getTweetId() {
		return tweetId;
	}
	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}
	
	@DynamoDBAttribute(attributeName = "QuestionId")
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	
	@DynamoDBAttribute(attributeName = "avatarURL")
	public String getAvatarURL() {
		return avatarURL;
	}
	public void setAvatarURL(String avatarURL) {
		this.avatarURL = avatarURL;
	}
	
	@DynamoDBAttribute(attributeName = "description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
