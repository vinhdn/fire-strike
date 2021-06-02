package zame.game.purchase

import android.app.Activity
import com.android.billingclient.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class InAppBilling(private val activity: Activity, private val idPurchase: String) {

    companion object {
        lateinit var base64EncodedPublicKey: String
    }

    private var billingClient: BillingClient? = null
    private var purchaseSku: SkuDetails? = null
    var isPurchased = false
    private var currentPurchase: Purchase? = null

    var purchasedSuccessListener: ((Boolean) -> Unit)? = null
    var getSkuDetailsSuccessListener: ((List<SkuDetails>?) -> Unit)? = null

    private val purchasesUpdateListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            // To be implemented in a later section.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                purchases?.firstOrNull {it.purchaseState == Purchase.PurchaseState.PURCHASED}?.let {
                    handlePurchase(it)
                }
            }
        }

    init {
        billingClient = BillingClient.newBuilder(activity)
            .setListener(purchasesUpdateListener)
            .enablePendingPurchases()
            .build()
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    GlobalScope.launch {
                        querySkuDetails()
                        queryListPurchase()
                    }
                } else {
                    vipUpdated()
                }
            }

            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
                vipUpdated()
            }
        })
    }

    suspend fun querySkuDetails() {
        val skuList = ArrayList<String>()
        skuList.add(idPurchase)
        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        withContext(Dispatchers.IO) {
            billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
                // Process the result.
                GlobalScope.launch(Dispatchers.Main) {
                    purchaseSku = skuDetailsList?.firstOrNull()
                    onListSkuResult(skuDetailsList)
                }
            }
        }
    }

    private fun vipUpdated() {}

    suspend fun queryListPurchase() {
        withContext(Dispatchers.IO) {
            val purchase =
                billingClient?.queryPurchases(BillingClient.SkuType.INAPP)?.purchasesList?.firstOrNull { it.purchaseState == Purchase.PurchaseState.PURCHASED }
            purchase?.let {
                handlePurchase(it)
            } ?: GlobalScope.launch(Dispatchers.Main) { vipUpdated() }
        }
    }

    fun purchase() {
        val vipSku = purchaseSku ?: run {
            purchasedSuccessListener?.invoke(false)
            return
        }
        // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
        val flowParams = BillingFlowParams.newBuilder()
            .setSkuDetails(vipSku)
            .build()
        val responseCode = billingClient?.launchBillingFlow(activity, flowParams)?.responseCode
    }

    fun consume() {
        if(isPurchased && currentPurchase != null) {
            val consumeParams =
                ConsumeParams.newBuilder()
                    .setPurchaseToken(currentPurchase!!.purchaseToken)
                    .build()

            billingClient?.consumeAsync(consumeParams) { billingResult, _ ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    GlobalScope.launch {
                        queryListPurchase()
                    }
                }
                vipUpdated()
            }
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if(Security.verifyPurchase(purchase.originalJson, purchase.signature)) {
            currentPurchase = purchase
            isPurchased = true
            purchasedSuccessListener?.invoke(true)
        }
    }

    private fun onListSkuResult(skus: List<SkuDetails>?) {
        getSkuDetailsSuccessListener?.invoke(skus)
    }
}