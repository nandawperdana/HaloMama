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

package it.fhab.aws;

import it.fhab.aws.dynamodb.tablerow.DDBTableRow;
import it.fhab.aws.dynamodb.tablerow.People;

import java.util.ArrayList;

import android.util.Log;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBScanExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedScanList;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;

public class DynamoDBRouter {

	private static final String TAG = "DynamoDBManager";
	private AmazonDynamoDBClient dDBClient;
	private AmazonClientManager amazonClientManager;
	private DynamoDBMapper mapper;
	
//	init class router
	public DynamoDBRouter(AmazonClientManager acm){
		this.amazonClientManager = acm;
		this.dDBClient = acm.ddb();
		mapper = new DynamoDBMapper(dDBClient);
	}

	public void signUp(DDBTableRow dDBModel){
		People p = (People) dDBModel;
		save(p);
	}
	
	/*
	 * Inserts ten users with userNo from 1 to 10 and random names.
	 */
	private void save(DDBTableRow dDBModel) {
	//may be can be improved by received array of object, and iterating the call
		try {
			Log.d(TAG, "Inserting users");
			mapper.save(dDBModel);
			Log.d(TAG, "Users inserted");
		} catch (AmazonServiceException ex) {
			Log.e(TAG, "Error inserting users");
			amazonClientManager
					.wipeCredentialsOnAuthError(ex);
		}
	}

	/*
	 * Scans the table and returns the list of users.
	 */
	public ArrayList<DDBTableRow> getModels(DDBTableRow dDBModel) {
		DynamoDBScanExpression scanExpression = dDBModel.getScanExpression();
		try {
			PaginatedScanList<DDBTableRow> result = mapper.scan(
					DDBTableRow.class, scanExpression);

			ArrayList<DDBTableRow> resultList = new ArrayList<DDBTableRow>();
			for (DDBTableRow up : result) {
				resultList.add(up);
			}
			return resultList;

		} catch (AmazonServiceException ex) {
			amazonClientManager
					.wipeCredentialsOnAuthError(ex);
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
			amazonClientManager
					.wipeCredentialsOnAuthError(ex);
		}

		return null;
	}
	
//	private enum DynamoDBRouterType {
//		GET_TABLE_STATUS, CREATE_TABLE, INSERT_USER, LIST_USERS, CLEAN_UP
//	}
//	
//	private class DynamoDBRouterTaskResult {
//		private DynamoDBRouterType taskType;
//		private DDBTableRow ddbtableRow;
//		public DynamoDBRouterType getTaskType() {
//			return taskType;
//		}
//		public void setTaskType(DynamoDBRouterType taskType) {
//			this.taskType = taskType;
//		}
//		public DDBTableRow getDdbtableRow() {
//			return ddbtableRow;
//		}
//		public void setDdbtableRow(DDBTableRow ddbtableRow) {
//			this.ddbtableRow = ddbtableRow;
//		}
//	}
//	
//	
//	//sample call async
//	private class DynamoDBRouterTask extends
//	AsyncTask<DynamoDBRouterType, Void, DynamoDBRouterTaskResult> {
//
//		protected DynamoDBRouterTaskResult doInBackground(
//				DynamoDBRouterType... types) {
//			DynamoDBRouterTaskResult result = new DynamoDBRouterTaskResult();
//			result.setTaskType(types[0]);
//		
//			if (types[0] == DynamoDBRouterType.CREATE_TABLE) {
//				HaloMama hm = new HaloMama();
//				save(hm);
//			}else{
//				HaloMama hm = new HaloMama();
//				save(hm);
//			}
//		
//			return result;
//		}
//		
//		protected void onPostExecute(DynamoDBRouterTaskResult result) {
//		
//			if (result.getTaskType() == DynamoDBRouterType.CREATE_TABLE) {
//				//do something with activity
//				// result.
//			} else if (result.getTaskType() == DynamoDBRouterType.LIST_USERS) {
//				
//				//do something with activity
//				// resut.
//			}
//		}
//	}

}
