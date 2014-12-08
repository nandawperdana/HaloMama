package com.nandanu.halomama.model;

import java.util.Random;

public class Constants {
	/**
	 * Register your here app https://dev.twitter.com/apps/new and get your
	 * consumer key and secret
	 * */
	// Preference Constants
	public static String TWITTER_CONSUMER_KEY = "MvmzHmV1Sld81CZyx9CEhA";
	public static String TWITTER_CONSUMER_SECRET = "W2yNRgnflsIl5r4ZofbSXY9kbEfvfyly1NZMv2dN9Sw";
	public static String PREFERENCE_NAME = "twitter_oauth";

	public static String TWITTER_ACCESS_TOKEN = "78867849-REFZbRdgcaAenMgv9SgHXwnLKfKwwgW5XTXuNwTMB";
	public static String TWITTER_ACCESS_TOKEN_SECRET = "Gazyr1zjZ57EU2y3lb9yDGRu9epibffYGZXDc54c";

	public static final String TWITTER_CALLBACK_URL = "http://www.google.com";

	// You should replace these values with your own
	// See the readme for details on what to fill in
	public static final String AWS_ACCOUNT_ID = "326820369316";
	public static final String COGNITO_POOL_ID = "us-east-1:d51cfebb-a83f-4d6b-9ad4-b44f2e6340ba";
	public static final String COGNITO_ROLE_AUTH = "arn:aws:iam::326820369316:role/Cognito_fhabBackendAuth_DefaultRole";
	public static final String COGNITO_ROLE_UNAUTH = "arn:aws:iam::326820369316:role/Cognito_fhabBackendUnauth_DefaultRole";

	// Note, the bucket will be created in all lower case letters
	// If you don't enter an all lower case title, any references you add
	// will need to be sanitized
	public static final String BUCKET_NAME = "YOUR_BUCKET_NAME";
	public static final String ACCOUNT_ID = "CHANGE_ME";
	public static final String IDENTITY_POOL_ID = "CHANGE_ME";
	public static final String UNAUTH_ROLE_ARN = "CHANGE_ME";

	// Note that spaces are not allowed in the table name
	public static final String TEST_TABLE_NAME = "CHANGE_ME";
}
