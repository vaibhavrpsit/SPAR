/********************************************************************************
 *   
 *	Copyright (c) 2015  MAX India pvt Ltd    All Rights Reserved.
 *	
 *	Rev	1.0 	11-May-2017		Ashish Yadav			Changes for M-Coupon Issuance FES
 *
 ********************************************************************************/

package max.retail.stores.pos.services.tender.mcoupon;

import java.util.concurrent.Callable;

import com.capillary.solutions.landmark.api.TransactionApi;
import com.capillary.solutions.landmark.transaction.dto.TransactionRequest;
import com.capillary.solutions.landmark.transaction.dto.TransactionResponse;

public class MAXMcouponCapillaryService  implements Callable<TransactionResponse> {
	
	private TransactionRequest request;
	public MAXMcouponCapillaryService(TransactionRequest request){
		this.request=request;
	}
	
	
    @Override
    public TransactionResponse call() throws Exception {
       TransactionApi transactionApi = TransactionApi.getInstance();
       return transactionApi.add(request);
        
    }
}