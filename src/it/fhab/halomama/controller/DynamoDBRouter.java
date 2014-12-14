/*
 * Copyright 2010-2012 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 *  http://aws.amazon.com/apache2.0
 * 
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package it.fhab.halomama.controller;

import it.fhab.halomama.model.Constants;
import it.fhab.halomama.model.DDBTableRow;
import it.fhab.halomama.model.HaloMama;
import it.fhab.halomama.model.People;
import it.fhab.halomama.model.Question;

import java.util.ArrayList;
import java.util.Random;

import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeAction;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.AttributeValueUpdate;
import com.amazonaws.services.dynamodbv2.model.UpdateItemRequest;

public class DynamoDBRouter {

	private static final String TAG = "DynamoDBManager";
	private AmazonDynamoDBClient dDBClient;
	private AmazonClientManager amazonClientManager;
	private DynamoDBMapper mapper;

	// init class router
	public DynamoDBRouter(AmazonClientManager acm) {
		this.amazonClientManager = acm;
		this.dDBClient = acm.ddb();
		mapper = new DynamoDBMapper(dDBClient);
	}

	public void signUp(DDBTableRow dDBModel) {
		People p = (People) dDBModel;
		save(p);
	}

	public void signIn(DDBTableRow dDBModel) {
		People p = (People) dDBModel;
		save(p);
	}

	public void deactive(DDBTableRow dDBModel) {
		People p = (People) dDBModel;
		save(p);
	}

	public void postFhab(DDBTableRow dDBModel) {
		HaloMama hm = (HaloMama) dDBModel;
		save(hm);
	}

	public void postHaloMama(DDBTableRow dDBModel) {
		HaloMama hm = (HaloMama) dDBModel;
		save(hm);
	}

	public void deleteHaloMama(DDBTableRow dDBModel) {
		HaloMama hm = (HaloMama) dDBModel;
		save(hm);
	}

	public void overrideHaloMama(DDBTableRow dDBModel) {
		HaloMama hm = (HaloMama) dDBModel;
		save(hm);
	}

	public void incrementSeen(String userNameTwitter, String createdDate) {
		UpdateItemRequest upd = new UpdateItemRequest();

		upd.setTableName(Constants.TABLE_NAME);

		AttributeValue unt = new AttributeValue();
		unt.setS(userNameTwitter);
		AttributeValue cd = new AttributeValue();
		cd.setS(createdDate);

		upd.addKeyEntry(Constants.TAG_USERNAME, unt);
		upd.addKeyEntry(Constants.TAG_CREATED_DATE, cd);

		AttributeValue s = new AttributeValue();
		s.setN("1");

		AttributeValueUpdate seen = new AttributeValueUpdate(s,
				AttributeAction.ADD);

		upd.addAttributeUpdatesEntry(Constants.TAG_SEEN, seen);

		dDBClient.updateItem(upd);

	}

	public HaloMama getPopularHaloMama() {
		DynamoDBQueryExpression<HaloMama> queryExpression = new DynamoDBQueryExpression<HaloMama>();
		HaloMama hm = new HaloMama();
		hm.setStatus("ok");
		queryExpression.withHashKeyValues(hm);
		queryExpression.withIndexName("Status-Seen-index");
		queryExpression.setScanIndexForward(false);
		queryExpression.setLimit(10);

		queryExpression.withConsistentRead(false);

		try {
			PaginatedQueryList<HaloMama> result = mapper.query(HaloMama.class,
					queryExpression);

			if (!result.isEmpty()) {
				Random random = new Random();
				int randomNumber = random.nextInt((result.size() - 1) - 0) + 0;
				HaloMama res = result.get(randomNumber);
				return res;
			} else {
				return null;
			}
		} catch (AmazonServiceException ex) {
			amazonClientManager.wipeCredentialsOnAuthError(ex);
			return null;
		}
	}

	public HaloMama getLastHaloMama(String userNameTwitter) {
		DynamoDBQueryExpression<HaloMama> queryExpression = new DynamoDBQueryExpression<HaloMama>();
		HaloMama hm = new HaloMama();
		hm.setUserNameTwitter(userNameTwitter);
		queryExpression.withHashKeyValues(hm);
		queryExpression.setScanIndexForward(false);
		queryExpression.setLimit(5);

		try {
			PaginatedQueryList<HaloMama> result = mapper.query(HaloMama.class,
					queryExpression);
			hm = result.get(result.size() - 1);
			boolean first = true;
			for (HaloMama up : result) {
				if (!first && up.getStatus().equalsIgnoreCase("ok")) {
					up.prepareOverideHaloMama();
					overrideHaloMama(up);
				}
				first = false;
			}
			return hm;
		} catch (AmazonServiceException ex) {
			amazonClientManager.wipeCredentialsOnAuthError(ex);
			return null;
		}
	}

	/*
	 * save data
	 */
	private void save(DDBTableRow dDBModel) {
		// may be can be improved by received array of object, and iterating the
		// call
		try {
			Log.d(TAG, "Inserting users");
			mapper.save(dDBModel);
			Log.d(TAG, "Users inserted");
		} catch (AmazonServiceException ex) {
			Log.e(TAG, "Error inserting users");
			amazonClientManager.wipeCredentialsOnAuthError(ex);
		}
	}

	/*
	 * Scans the table and returns the list of users.
	 */
	public ArrayList<People> scanPeople() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		try {
			PaginatedScanList<People> result = mapper.scan(People.class,
					scanExpression);

			ArrayList<People> resultList = new ArrayList<People>();
			for (People up : result) {
				resultList.add(up);
				System.out.println(up.getCognitoId());
			}
			return resultList;

		} catch (AmazonServiceException ex) {
			amazonClientManager.wipeCredentialsOnAuthError(ex);
		}

		return null;
	}

	public Question queryQuestion() {
		DynamoDBQueryExpression<Question> queryExpression = new DynamoDBQueryExpression<Question>();
		Question q = new Question();
		Random random = new Random();
		int randomNumber = random.nextInt((8 - 1) - 0) + 0;
		q.setEmotionId("" + randomNumber);
		queryExpression.withHashKeyValues(q);
		try {
			PaginatedQueryList<Question> result = mapper.query(Question.class,
					queryExpression);

			if (result.isEmpty()) {
				return null;
			} else {
				if (result.size() == 1) {
					return result.get(0);
				} else {
					Random random2 = new Random();
					int randomNumber2 = random2
							.nextInt((result.size() - 1) - 0) + 0;
					return result.get(randomNumber2);
				}
			}
		} catch (AmazonServiceException ex) {
			amazonClientManager.wipeCredentialsOnAuthError(ex);
			return null;
		}
	}

	public ArrayList<Question> scanQuestion2() {
		DynamoDBScanExpression scanExpression = new DynamoDBScanExpression();
		try {
			PaginatedScanList<Question> result = mapper.scan(Question.class,
					scanExpression);

			ArrayList<Question> resultList = new ArrayList<Question>();
			for (Question up : result) {
				resultList.add(up);
				System.out.println(up.getQuestion());
			}
			return resultList;

		} catch (AmazonServiceException ex) {
			amazonClientManager.wipeCredentialsOnAuthError(ex);
		}

		return null;
	}

	/*
	 * Retrieves all of the attribute/value pairs for the specified user.
	 */
	public DDBTableRow getModel(DDBTableRow dDBModel) {
		try {
			DDBTableRow tableRowResult = mapper.load(DDBTableRow.class,
					dDBModel.getHashKey());

			return tableRowResult;

		} catch (AmazonServiceException ex) {
			amazonClientManager.wipeCredentialsOnAuthError(ex);
		}

		return null;
	}

}
