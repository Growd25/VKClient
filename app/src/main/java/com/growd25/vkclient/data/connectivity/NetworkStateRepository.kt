package com.growd25.vkclient.data.connectivity

import android.annotation.SuppressLint
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkRequest
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.subjects.PublishSubject
import javax.inject.Inject
import javax.inject.Singleton

@SuppressLint("CheckResult")
@Singleton
class NetworkStateRepository @Inject constructor(
    private val connectivityManager: ConnectivityManager,
    private val networkRequest: NetworkRequest
) {

    private val networkStateSubject = PublishSubject.create<Boolean>()

    fun observeNetworkState(): Observable<Boolean> = networkStateSubject

    init {
        createNetworkStateObservable().subscribe(networkStateSubject::onNext)
    }

    private fun createNetworkStateObservable(): Flowable<Boolean> =
        Flowable.create(
            { emitter ->
                val networkCallback = object : ConnectivityManager.NetworkCallback() {
                    override fun onAvailable(network: Network) {
                        emitter.onNext(true)
                    }

                    override fun onLost(network: Network) {
                        emitter.onNext(false)
                    }
                }
                connectivityManager.registerNetworkCallback(networkRequest, networkCallback)
                emitter.setCancellable {
                    connectivityManager.unregisterNetworkCallback(networkCallback)
                }
            },
            BackpressureStrategy.LATEST
        )

}
