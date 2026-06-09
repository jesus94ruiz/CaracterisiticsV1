package com.jera.caracterisiticsv1.repository

import android.content.Context
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    @ApplicationContext private val context: Context,
    private val firebaseAuth: FirebaseAuth
) {

    private val _currentUser = MutableStateFlow<FirebaseUser?>(firebaseAuth.currentUser)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    val isLoggedIn: Boolean get() = firebaseAuth.currentUser != null

    init {
        // Escuchar cambios de estado de autenticación
        firebaseAuth.addAuthStateListener { auth ->
            _currentUser.value = auth.currentUser
        }
    }

    fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(WEB_CLIENT_ID)
            .requestEmail()
            .requestProfile()
            .build()
        return GoogleSignIn.getClient(context, gso)
    }

    /**
     * Autentica con Firebase usando el idToken obtenido de Google Sign-In.
     * Devuelve el FirebaseUser resultante o lanza una excepción.
     */
    suspend fun firebaseSignInWithGoogle(idToken: String): FirebaseUser {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val result = firebaseAuth.signInWithCredential(credential).await()
        return result.user ?: throw IllegalStateException("Sign-in falló: usuario nulo")
    }

    fun signOut() {
        firebaseAuth.signOut()
        getGoogleSignInClient().signOut()
    }

    fun getCurrentUid(): String? = firebaseAuth.currentUser?.uid

    companion object {
        const val WEB_CLIENT_ID = "410714250313-ae4nl1q987gq9gldc6js7vmahneglugv.apps.googleusercontent.com"
    }
}
