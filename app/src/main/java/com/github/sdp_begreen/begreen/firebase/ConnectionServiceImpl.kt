package com.github.sdp_begreen.begreen.firebase

import android.util.Log
import com.github.sdp_begreen.begreen.FirebaseRef
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import org.koin.java.KoinJavaComponent.inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

object ConnectionServiceImpl : ConnectionService {

    private val dbRefs by inject<FirebaseRef>(FirebaseRef::class.java)
    private val connectedRef = dbRefs.database.getReference(".info/connected")

    override suspend fun getConnectionStatus(): Boolean = suspendCoroutine {
        connectedRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                it.resume(snapshot.getValue(Boolean::class.java) ?: false)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Listener was cancelled")
            }
        })
    }

    override suspend fun getFlowConnectionStatus(): Flow<Boolean> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                Log.d("Send Post Offline Service", "connected")
                trySend(snapshot.getValue(Boolean::class.java) ?: false)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.d("TAG", "Listener was cancelled")
            }
        }
        connectedRef.addValueEventListener(listener)

        awaitClose {
            connectedRef.removeEventListener(listener)
        }
    }
}