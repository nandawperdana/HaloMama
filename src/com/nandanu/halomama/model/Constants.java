package com.nandanu.halomama.model;

import com.amazonaws.regions.Regions;

public class Constants {
	/**
	 * Register your here app https://dev.twitter.com/apps/new and get your
	 * consumer key and secret
	 * */
	// Preference Constants
	// public static String TWITTER_CONSUMER_KEY = "gtKIdPEwHcHfZPYHszOYDnWIx";
	// public static String TWITTER_CONSUMER_SECRET =
	// "Rz4Wi3z1SiTkkldJmfUfOjyS09WiQ0jY7RXAeibfByxumdiryr";
	public static String TWITTER_CONSUMER_KEY = "gtKIdPEwHcHfZPYHszOYDnWIx";
	public static String TWITTER_CONSUMER_SECRET = "Rz4Wi3z1SiTkkldJmfUfOjyS09WiQ0jY7RXAeibfByxumdiryr";
	public static String PREFERENCE_NAME = "twitter_oauth";

	// public static String TWITTER_ACCESS_TOKEN =
	// "2836304648-zPC3xjentjb1o7BhWcAkVqd2IE6XxlXZV4t6utK";
	// public static String TWITTER_ACCESS_TOKEN_SECRET =
	// "AJyPvkIJmFyo4OOueYKqxKR1HEsuSygGREPR8HhEPRMYu";
	public static String TWITTER_ACCESS_TOKEN = "2836304648-zPC3xjentjb1o7BhWcAkVqd2IE6XxlXZV4t6utK";
	public static String TWITTER_ACCESS_TOKEN_SECRET = "AJyPvkIJmFyo4OOueYKqxKR1HEsuSygGREPR8HhEPRMYu";

//	public static final String TWITTER_CALLBACK_URL = "http://www.google.com";

	// twitter shared preferences
	public static final String TAG_TWITTER_CONSUMER_KEY = "CONSUMER_KEY";
	public static final String TAG_TWITTER_CONSUMER_SECRET = "CONSUMER_SECRET";
	public static final String TAG_TWITTER_ACCESS_TOKEN = "ACCESS_TOKEN";
	public static final String TAG_TWITTER_ACCESS_TOKEN_SECRET = "ACCESS_TOKEN_SECRET";
	public static final String TAG_TWITTER_FULLNAME = "FULLNAME";
	public static final String TAG_TWITTER_USERNAME = "USERNAME";
	public static final String TAG_TWITTER_IMG_URL = "IMG_URL";
	public static final String TAG_TWITTER_STATUS_ID = "STATUS_ID";
	public static final String TAG_TWITTER_RETWEET = "RETWEET";

	// You should replace these values with your own
	// See the readme for details on what to fill in
	public static final String AWS_ACCOUNT_ID = "326820369316";
	public static final String COGNITO_POOL_ID = "us-east-1:d51cfebb-a83f-4d6b-9ad4-b44f2e6340ba";
	public static final String COGNITO_ROLE_AUTH = "arn:aws:iam::326820369316:role/Cognito_fhabBackendAuth_DefaultRole";
	public static final String COGNITO_ROLE_UNAUTH = "arn:aws:iam::326820369316:role/Cognito_fhabBackendUnauth_DefaultRole";
	public static final String BUCKET_NAME = "fhab-store-data-us";
	public static final String TABLE_NAME = "Fhab-HaloMama-Production";

	public static final Regions COGNITO_REGION = Regions.US_EAST_1;
	public static final Regions DDB_REGION = Regions.US_EAST_1;

	// DB
	public static final String TAG_COGNITO_ID = "CognitoId";
	public static final String TAG_AVATAR_URL = "avatarURL";
	public static final String TAG_CREATED_DATE = "CreatedDate";
	public static final String TAG_DESCRIPTION = "Description";
	public static final String TAG_DEVICE_OS = "DeviceOS";
	public static final String TAG_EMOTION_ID = "EmotionId";
	public static final String TAG_QUESTION_ID = "QuestionId";
	public static final String TAG_QUESTION = "Question";
	public static final String TAG_SEEN = "Seen";
	public static final String TAG_STATUS = "Status";
	public static final String TAG_TWEET_ID = "TweetId";
	public static final String TAG_USERNAME = "UserNameTwitter";
	public static final String TAG_FULLNAME = "FullName";
	public static final String TAG_LASTLOGIN = "LastLogin";
	public static final String TAG_VIDEO_TYPE = "VideoType";
}
