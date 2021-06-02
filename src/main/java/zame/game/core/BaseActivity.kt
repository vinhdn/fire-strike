package zame.game.core

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.SkuDetails
import kotlinx.android.synthetic.main.layout_buy_diamond.*
import zame.game.App
import zame.game.R
import zame.game.core.util.PreferencesHelper
import zame.game.core.util.toast
import zame.game.purchase.BillingClientLifecycle
import zame.game.purchase.Config
import java.util.*

/**
 * Created by vinhdn on 07-Mar-18.
 */
abstract class BaseActivity : AppCompatActivity() {

    protected var plan01Sku: String = ""
    protected var plan02Sku: String = ""
    protected var plan03Sku: String = ""
    protected var plan01Token: String = ""
    protected var plan02Token: String = ""
    private var listSku: List<SkuDetails>? = listOf()
    private var mapSkuDetails: Map<String, SkuDetails> = mapOf()

    private lateinit var billingClientLifecycle: BillingClientLifecycle

    open val coinsListener: (Int) -> Unit = {}
    protected var isPlan01Used = false
    protected var isPlan02Used = false
    open protected fun isPurchaseUse(): Boolean {
        return false
    }

    private fun rewardSuccess(coinIncrease: Int = 60) {
        var coin = getCoins()
        coin += coinIncrease
        setCoins(coin)
        toast("+$coinIncrease Coins")
    }

    fun getCoins(): Int {
        return PreferencesHelper.shared.getIntValue("Coins", 0) ?: 0
    }

    fun setCoins(coins: Int) {
        PreferencesHelper.shared.putValue("Coins", coins)
        coinsListener(coins)
    }

    private fun increaseTodayCoins() {
        if(isPlan01Used || isPlan02Used) {
            val today = Calendar.getInstance()
            val todayStr = "${today.get(Calendar.YEAR)}-${today.get(Calendar.MONTH)}-${today.get(Calendar.DAY_OF_MONTH)}"
            if(!PreferencesHelper.shared.getBooleanValue(todayStr, false)) {
                val coins = getCoins()
                setCoins(coins + 300)
                PreferencesHelper.shared.putValue(todayStr, true)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (isPurchaseUse()) {
            billingClientLifecycle = (application as App).billingClientLifecycle
            lifecycle.addObserver(billingClientLifecycle)

            // Register purchases when they change.
            billingClientLifecycle.purchaseUpdateEvent.observe(this, Observer {
                it?.forEach { purchase ->
                    if (purchase.skus.firstOrNull() == Config.weeklySku) {
                        isPlan01Used = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                        increaseTodayCoins()
                    } else if (purchase.skus.firstOrNull() == Config.monthSku) {
                        isPlan02Used = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                        increaseTodayCoins()
                    }
                }
            })
            billingClientLifecycle.skusWithSkuDetails.observe(this, Observer {
                mapSkuDetails = it
            })
            billingClientLifecycle.purchases.observe(this, Observer {
                it?.forEach { purchase ->
                    if (purchase.skus.firstOrNull() == Config.weeklySku) {
                        isPlan01Used = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                    } else if (purchase.skus.firstOrNull() == Config.monthSku) {
                        isPlan02Used = purchase.purchaseState == Purchase.PurchaseState.PURCHASED
                    }
                }
                increaseTodayCoins()
            })
        }
    }

    private var action: (() -> Unit)? = {}

    override fun onDestroy() {
        super.onDestroy()
        action = null
    }

    fun showDialogBuyDiamond() {
        val dialog = zame.game.core.util.showDialog(this, R.layout.layout_buy_diamond) { dia ->
            dia.btnCancel.setOnClickListener {
                dia.dismiss()
            }
            mapSkuDetails[Config.weeklySku]?.let { sku ->
                dia.tvPrice500.text = sku.price
            }
            mapSkuDetails[Config.monthSku]?.let { sku ->
                dia.tvPrice1500.text = sku.price
            }
            if (!isPlan01Used) {
                dia.btnBuy500.setOnClickListener {
                    val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(mapSkuDetails[Config.weeklySku]
                                    ?: return@setOnClickListener)
                            .build()
                    billingClientLifecycle.launchBillingFlow(this, flowParams)
                    dia.dismiss()
                }
            } else {
                dia.btnBuy500.text = "USED"
                dia.btnBuy500.setBackgroundResource(R.drawable.shape_rounded_red)
                dia.btnBuy500.setOnClickListener(null)
            }
            if (!isPlan02Used) {
                dia.btnBuy1500.setOnClickListener {
                    val flowParams = BillingFlowParams.newBuilder()
                            .setSkuDetails(mapSkuDetails[Config.monthSku]
                                    ?: return@setOnClickListener)
                            .build()
                    billingClientLifecycle.launchBillingFlow(this, flowParams)
                    dia.dismiss()
                }
            } else {
                dia.btnBuy1500.text = "USED"
                dia.btnBuy500.setBackgroundResource(R.drawable.shape_rounded_red)
                dia.btnBuy1500.setOnClickListener(null)
            }
        }
        dialog.show()
    }

    private fun launchMarket() {
        val uri = Uri.parse("market://details?id=$packageName")
        val myAppLinkToMarket = Intent(Intent.ACTION_VIEW, uri)
        try {
            startActivity(myAppLinkToMarket)
        } catch (e: ActivityNotFoundException) {
        }
    }

    fun isVIPUser(): Boolean {
        return isPlan01Used || isPlan02Used
    }
}