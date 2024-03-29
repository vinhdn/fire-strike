package zame.game.purchase

import com.android.billingclient.api.Purchase

/**
 * Return subscription for the provided SKU, if it exists.
 */
fun subscriptionForSku(subscriptions: List<SubscriptionStatus>?, sku: String): SubscriptionStatus? {
    subscriptions?.let {
        for (subscription in it) {
            if (subscription.sku == sku) {
                return subscription
            } else {
                // Do nothing.
            }
        }
    }
    // User does not have the subscription.
    return null
}

/**
 * Return purchase for the provided SKU, if it exists.
 */
fun purchaseForSku(purchases: List<Purchase>?, sku: String): Purchase? {
    purchases?.let {
        for (purchase in it) {
            if (purchase.skus[0] == sku) {
                return purchase
            } else {
                // Do nothing.
            }
        }
    }
    return null
}

/*
 * This will return true if the Google Play Billing APIs have a record for the subscription.
 * This will not always match the server's record of the subscription for this app user.
 *
 * Example: App user buys the subscription on a different device with a different Google
 * account. The server will show that this app user has the subscription, even if the
 * Google account on this device has not purchased the subscription.
 * In this example, the method will return false.
 *
 * Example: The app user changes by signing out and signing into the app with a different
 * email address. The server will show that this app user does not have the subscription,
 * even if the Google account on this device has purchased the subscription.
 * In this example, the method will return true.
 */
fun deviceHasGooglePlaySubscription(purchases: List<Purchase>?, sku: String) =
        purchaseForSku(purchases, sku) != null

/**
 * This will return true if the server has a record for the subscription.
 * Sometimes this will return true even if the Google Play Billing APIs return false.
 *
 * For local purchases that are rejected by the server, this app attaches the field
 * subAlreadyOwned=true to the subscription object. This means that whenever
 * [deviceHasGooglePlaySubscription] returns true, and the server has processed all purchase tokens,
 * we also expect this method to return true.
 *
 * Example: App user buys the subscription on a different device with a different Google
 * account. The server will show that this app user has the subscription, even if the
 * Google account on this device has not purchased the subscription.
 * In this example, the method will return true, even though [deviceHasGooglePlaySubscription]
 * will return false.
 *
 * Example: The app user changes by signing out and signing into the app with a different
 * email address. The server will show that this app user does not have the subscription,
 * by returning an API response indicating that it is ALREADY_OWNED.
 * even if the Google account on this device has purchased the subscription.
 * In this example, the method will return true. This is the same as the result from
 * [deviceHasGooglePlaySubscription].
 */
fun serverHasSubscription(subscriptions: List<SubscriptionStatus>?, sku: String) =
        subscriptionForSku(subscriptions, sku) != null

/**
 * Returns true if the grace period option should be shown.
 */
fun isGracePeriod(subscription: SubscriptionStatus?) =
        subscription != null &&
                subscription.isEntitlementActive &&
                subscription.isGracePeriod &&
                !subscription.subAlreadyOwned

/**
 * Returns true if the subscription restore option should be shown.
 */
fun isSubscriptionRestore(subscription: SubscriptionStatus?) =
        subscription != null &&
                subscription.isEntitlementActive &&
                !subscription.willRenew &&
                !subscription.subAlreadyOwned

/**
 * Returns true if the basic content should be shown.
 */
fun isWeeklySub(subscription: SubscriptionStatus?) =
        subscription != null &&
                subscription.isEntitlementActive &&
                Config.weeklySku == subscription.sku &&
                !subscription.subAlreadyOwned

/**
 * Returns true if premium content should be shown.
 */
fun isMonthlySub(subscription: SubscriptionStatus?) =
        subscription != null &&
                subscription.isEntitlementActive &&
                Config.monthSku == subscription.sku &&
                !subscription.subAlreadyOwned

/**
 * Returns true if account hold should be shown.
 */
fun isAccountHold(subscription: SubscriptionStatus?) =
        subscription != null &&
                !subscription.isEntitlementActive &&
                subscription.isAccountHold &&
                !subscription.subAlreadyOwned

/**
 * Returns true if account pause should be shown.
 */
fun isPaused(subscription: SubscriptionStatus?) =
        subscription != null &&
                !subscription.isEntitlementActive &&
                subscription.isPaused &&
                !subscription.subAlreadyOwned

/**
 * Returns true if the subscription is already owned and requires a transfer to this account.
 */
fun isTransferRequired(subscription: SubscriptionStatus?) =
        subscription != null && subscription.subAlreadyOwned