package com.socialsirius.messenger.sirius_sdk_impl.scenario

import android.util.Log
import com.sirius.library.agent.aries_rfc.feature_0048_trust_ping.Ping
import com.sirius.library.agent.listener.Event
import com.sirius.library.messaging.Message
import com.sirius.library.mobile.scenario.BaseScenario

import com.socialsirius.messenger.sirius_sdk_impl.SDKUseCase
import kotlin.reflect.KClass

class PingScenarioImpl(val sdkUseCase: SDKUseCase) : BaseScenario() {
    override fun initMessages(): List<KClass<out Message>> {
        return listOf(Ping::class)
    }

    override fun start(event: Event): Pair<Boolean, String?> {
        Log.d("mylog2090","PingScenarioImpl event="+event.messageObj)
        val pingMessageId = event.message()?.getId()
        sdkUseCase.sendTrustPingMessageForPairwise(event.pairwise?.their?.did ?: "", pingMessageId)
        return Pair(true, null)
    }

    override fun onScenarioStart(id: String) {

    }

    override fun onScenarioEnd(id: String, success: Boolean, error: String?) {

    }
}