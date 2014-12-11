package it.fhab.aws.dynamodb.tablerow;

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBAttribute;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBHashKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBRangeKey;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBTable;

@DynamoDBTable(tableName = "Fhab-People")
public class People {	
	
	private String cognitoId;
	private String createdDate;
	private String userNameTwitter;
	private String fullName;
	private String lastLogin;
	private String deviceOS;
	private String status;
	
//	public People(){
//		
//	}
	
//	public People (String cognitoId, String deviceOS){
//		this.cognitoId = cognitoId;
//		this.deviceOS = deviceOS;
//	}

	public void signUp(String userNameTwitter, String fullName){
		this.userNameTwitter = userNameTwitter;
		this.fullName = fullName;
		this.createdDate = ""+System.currentTimeMillis();
		this.lastLogin = ""+System.currentTimeMillis();
	}
	
	public void signIn(String userNameTwitter, String fullName){
		this.userNameTwitter = userNameTwitter;
		this.lastLogin = ""+System.currentTimeMillis();
	}
	
	public void deactivate(String userNameTwitter, String fullName){
		this.userNameTwitter = userNameTwitter;
		this.lastLogin = ""+System.currentTimeMillis();
		this.status = "deactivate";
	}
	
	@DynamoDBHashKey(attributeName = "CognitoId")
	public String getCognitoId() {
		return cognitoId;
	}
	public void setCognitoId(String cognitoId) {
		this.cognitoId = cognitoId;
	}
	
	@DynamoDBAttribute(attributeName = "CreatedDate")
	public String getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(String createdDate) {
		this.createdDate = createdDate;
	}
	
	@DynamoDBRangeKey(attributeName = "UserNameTwitter")
	public String getUserNameTwitter() {
		return userNameTwitter;
	}
	public void setUserNameTwitter(String userNameTwitter) {
		this.userNameTwitter = userNameTwitter;
	}
	
	@DynamoDBAttribute(attributeName = "FullName")
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	@DynamoDBAttribute(attributeName = "LastLogin")
	public String getLastLogin() {
		return lastLogin;
	}
	public void setLastLogin(String lastLogin) {
		this.lastLogin = lastLogin;
	}
	
	@DynamoDBAttribute(attributeName = "DeviceOS")
	public String getDeviceOS() {
		return deviceOS;
	}
	public void setDeviceOS(String deviceOS) {
		this.deviceOS = deviceOS;
	}
	
	@DynamoDBAttribute(attributeName = "Status")
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
}
