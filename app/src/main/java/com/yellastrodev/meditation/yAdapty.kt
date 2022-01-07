package com.yellastrodev.meditation

import android.app.Activity
import android.content.Context
import android.util.Log
import com.adapty.Adapty
import com.adapty.errors.AdaptyErrorCode
import com.adapty.models.PeriodUnit
import com.adapty.models.ProductModel

class yAdapty(val fCtx: Context, val fUserId: String) {
    lateinit var fProduct : ProductModel ;
    init{
        Adapty.activate(fCtx.applicationContext, yConst.adaptykey, fUserId);
    }

    fun getSubsc(fClb: yClb){
        Log.i(yConst.TAG,"get subs")

        Adapty.getPaywalls { paywalls, products, error ->
            if (error == null) {
                if(products !=null){
                    Log.i(yConst.TAG,"get subs: "+ products.toString())
                    Log.i(yConst.TAG,"paywals: "+paywalls.toString())
                    val fPaywal= paywalls!![0]
                    fProduct = fPaywal.products[0];
                    val fPeriod = fProduct.subscriptionPeriod!!.unit;
                    var fPeriodMsg : String;
                    Adapty.logShowPaywall(paywalls!![0])

                    if(fPeriod==PeriodUnit.M)
                        fPeriodMsg = "месяц"
                    else
                        fPeriodMsg = "7 дней"
                    fClb.run(
                            " "+fProduct.localizedPrice+" / "+
                    fPeriodMsg);

                }else{
                    Log.i(yConst.TAG,"404")

                    fClb.run("e404");
                }
            }else{
                Log.i(yConst.TAG,"error "+error.stackTraceToString())

                fClb.run("e "+error.stackTraceToString());
            }

        }
    }


fun makePursh(activity : Activity, fClb : yClb){
    Log.i(yConst.TAG,"purshaise")
    Log.i(yConst.TAG, fProduct.toString())
    Adapty.makePurchase(activity, fProduct) { purchaserInfo, purchaseToken, googleValidationResult, product, error ->
        if (error == null) {
            Log.i(yConst.TAG,purchaserInfo.toString())

            if (purchaserInfo?.accessLevels?.get("premium")?.isActive == true) {
                // grant access to premium features
                fClb.run("ok");
                Log.i(yConst.TAG,"purshaise res ok")

                return@makePurchase;
            }
            fClb.run("no");
            Log.i(yConst.TAG,"purshaise no")

            return@makePurchase;
        }
        fClb.run(error.toString());
        Log.i(yConst.TAG,error.stackTraceToString())

    }
}

fun checkSubs(fClb : yClb){
    Log.i(yConst.TAG,"check subs")

    Adapty.getPurchaserInfo { purchaserInfo, error ->
        if (error == null) {
            if (purchaserInfo != null) {
                Log.i(yConst.TAG,purchaserInfo.toString())

                if (purchaserInfo.accessLevels["premium"]?.isActive == true) {
                    fClb.run("ok");
                    return@getPurchaserInfo;
                }
            }
        }
        if (error != null) {

            Log.i(yConst.TAG, error!!.stackTraceToString())
            MainActivity.sendAnality("subs_check_error_"+
                    error.adaptyErrorCode,fCtx)
        }

        fClb.run("no");
    }
}
}