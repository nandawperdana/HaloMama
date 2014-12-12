package it.fhab.aws.dynamodb.tablerow;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "Fhab-HaloMama-Production")
public class HaloMama extends DDBTableRow {	

	private String userNameTwitter;
	private String createdDate;
	private int emotionId;
	private int seen;
	private String deviceOS;
	private String status;
	private String tweetId;
	private int questionId;
	private String avatarURL;
	private String description;
	private String videoType;
	
	public HaloMama(){
		
	}
	
	public HaloMama (HaloMamaStatusSeenIndex hmSSI){
		this.createdDate = hmSSI.getCreatedDate();
		this.userNameTwitter = hmSSI.getUserNameTwitter();
		
	}
	
	public HaloMama (String userNameTwitter, String deviceOS){
		this.userNameTwitter = userNameTwitter;
		this.deviceOS = deviceOS;
	}
	
	public String preparePostFhab(int emotionId){
		this.emotionId = emotionId;
		this.createdDate = ""+System.currentTimeMillis();
				
		return createdDate;		
	}
	
	public void preparePostHaloMama(String createdDate, String avatarURL, String tweetId, int questionId, String description, String videoType){
		this.createdDate = createdDate;
	    this.avatarURL = avatarURL;
	    this.tweetId = tweetId;
	    this.questionId = questionId;
	    this.description = description;
	    this.status = "ok";
	    this.videoType = videoType;
	}
	
	public void prepareDeleteHaloMama(){
		this.status = "delete";
	}

	public void prepareOverideHaloMama(){
		this.status= "overided"; 
	}
	
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
	public int getEmotionId() {
		return emotionId;
	}
	public void setEmotionId(int emotionId) {
		this.emotionId = emotionId;
	}
	
	@DynamoDBIndexRangeKey(attributeName = "Seen", globalSecondaryIndexName = "Status-Seen-index")
	public int getSeen() {
		return seen;
	}
	public void setSeen(int seen) {
		this.seen = seen;
	}
	
	@DynamoDBAttribute(attributeName = "DeviceOS")
	public String getDeviceOS() {
		return deviceOS;
	}
	public void setDeviceOS(String deviceOS) {
		this.deviceOS = deviceOS;
	}
	
	@DynamoDBIndexHashKey(attributeName = "Status", globalSecondaryIndexName = "Status-Seen-index")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@DynamoDBAttribute(attributeName = "TweetId")
	public String getTweetId() {
		return tweetId;
	}
	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}
	
	@DynamoDBAttribute(attributeName = "VideoType")
	public String getVideoType() {
		return videoType;
	}
	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}
	
	@DynamoDBAttribute(attributeName = "QuestionId")
	public int getQuestionId() {
		return questionId;
	}
	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}
	
	@DynamoDBAttribute(attributeName = "avatarURL")
	public String getAvatarURL() {
		return avatarURL;
	}
	public void setAvatarURL(String avatarURL) {
		this.avatarURL = avatarURL;
	}
	
	@DynamoDBAttribute(attributeName = "Description")
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
}
