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

import com.amazonaws.regions.Regions;

public class Constants {
	
    public static final String ACCOUNT_ID = "326820369316";
    public static final String IDENTITY_POOL_ID = "us-east-1:d51cfebb-a83f-4d6b-9ad4-b44f2e6340ba";
    public static final String UNAUTH_ROLE_ARN = "arn:aws:iam::326820369316:role/Cognito_fhabBackendUnauth_DefaultRole";
    public static final Regions COGNITO_REGION = Regions.US_EAST_1;	
    public static final Regions DDB_REGION = Regions.US_EAST_1;
    
}
