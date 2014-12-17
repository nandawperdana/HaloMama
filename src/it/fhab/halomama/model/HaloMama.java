package it.fhab.halomama.model;

import java.io.Serializable;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBIndexRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = Constants.TABLE_NAME)
public class HaloMama extends DDBTableRow implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
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

	public HaloMama() {

	}

	// public HaloMama(HaloMamaStatusSeenIndex hmSSI) {
	// this.createdDate = hmSSI.getCreatedDate();
	// this.userNameTwitter = hmSSI.getUserNameTwitter();
	//
	// }

	public HaloMama(String userNameTwitter, String deviceOS) {
		this.userNameTwitter = userNameTwitter;
		this.deviceOS = deviceOS;
	}

	public String preparePostFhab(int emotionId, String createdDate) {
		this.emotionId = emotionId;
		this.createdDate = createdDate;

		return createdDate;
	}

	public void preparePostHaloMama(String createdDate, String avatarURL,
			String tweetId, int questionId, String description, String videoType) {
		this.createdDate = createdDate;
		this.avatarURL = avatarURL;
		this.tweetId = tweetId;
		this.questionId = questionId;
		this.description = description;
		this.status = "ok";
		this.videoType = videoType;
	}

	public void prepareDeleteHaloMama() {
		this.status = "delete";
	}

	public void prepareOverideHaloMama() {
		this.status = "overided";
	}

	@DynamoDBHashKey(attributeName = Constants.TAG_USERNAME)
	public String getUserNameTwitter() {
		return userNameTwitter;
	}

	public void setUserNameTwitter(String userNameTwitter) {
		this.userNameTwitter = userNameTwitter;
	}

	@DynamoDBRangeKey(attributeName = Constants.TAG_CREATED_DATE)
	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_EMOTION_ID)
	public int getEmotionId() {
		return emotionId;
	}

	public void setEmotionId(int emotionId) {
		this.emotionId = emotionId;
	}

	@DynamoDBIndexRangeKey(attributeName = Constants.TAG_SEEN, globalSecondaryIndexName = "Status-Seen-index")
	public int getSeen() {
		return seen;
	}

	public void setSeen(int seen) {
		this.seen = seen;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_DEVICE_OS)
	public String getDeviceOS() {
		return deviceOS;
	}

	public void setDeviceOS(String deviceOS) {
		this.deviceOS = deviceOS;
	}

	@DynamoDBIndexHashKey(attributeName = Constants.TAG_STATUS, globalSecondaryIndexName = "Status-Seen-index")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_TWEET_ID)
	public String getTweetId() {
		return tweetId;
	}

	public void setTweetId(String tweetId) {
		this.tweetId = tweetId;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_VIDEO_TYPE)
	public String getVideoType() {
		return videoType;
	}

	public void setVideoType(String videoType) {
		this.videoType = videoType;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_QUESTION_ID)
	public int getQuestionId() {
		return questionId;
	}

	public void setQuestionId(int questionId) {
		this.questionId = questionId;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_AVATAR_URL)
	public String getAvatarURL() {
		return avatarURL;
	}

	public void setAvatarURL(String avatarURL) {
		this.avatarURL = avatarURL;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_DESCRIPTION)
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

}
