package it.fhab.halomama.model;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "Fhab-People")
public class People extends DDBTableRow {

	private String cognitoId;
	private String createdDate;
	private String userNameTwitter;
	private String fullName;
	private String lastLogin;
	private String deviceOS;
	private String status;

	public People() {

	}

	public People(String cognitoId, String deviceOS) {
		this.cognitoId = cognitoId;
		this.deviceOS = deviceOS;
	}

	public void prepareSignUp(String userNameTwitter, String fullName, String createdDate) {
		this.userNameTwitter = userNameTwitter;
		this.fullName = fullName;
		this.createdDate = createdDate;
		this.lastLogin = "" + System.currentTimeMillis();
	}

	public void prepareSignIn(String userNameTwitter) {
		this.userNameTwitter = userNameTwitter;
		this.lastLogin = "" + System.currentTimeMillis();
	}

	public void prepareDeactivate(String userNameTwitter, String fullName) {
		this.userNameTwitter = userNameTwitter;
		this.lastLogin = "" + System.currentTimeMillis();
		this.status = "deactivate";
	}

	@DynamoDBHashKey(attributeName = Constants.TAG_COGNITO_ID)
	public String getCognitoId() {
		return cognitoId;
	}

	public void setCognitoId(String cognitoId) {
		this.cognitoId = cognitoId;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_CREATED_DATE)
	public String getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}

	@DynamoDBRangeKey(attributeName = Constants.TAG_USERNAME)
	public String getUserNameTwitter() {
		return userNameTwitter;
	}

	public void setUserNameTwitter(String userNameTwitter) {
		this.userNameTwitter = userNameTwitter;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_FULLNAME)
	public String getFullName() {
		return fullName;
	}

	public void setFullName(String fullName) {
		this.fullName = fullName;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_LASTLOGIN)
	public String getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_DEVICE_OS)
	public String getDeviceOS() {
		return deviceOS;
	}

	public void setDeviceOS(String deviceOS) {
		this.deviceOS = deviceOS;
	}

	@DynamoDBAttribute(attributeName = Constants.TAG_STATUS)
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
