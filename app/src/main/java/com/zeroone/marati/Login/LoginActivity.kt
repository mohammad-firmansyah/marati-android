package com.zeroone.marati.Login

import android.content.Intent
import android.content.IntentSender
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.activity.viewModels
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.zeroone.marati.R
import com.zeroone.marati.databinding.ActivityLoginBinding
import com.zeroone.marati.Home.HomeActivity
import com.zeroone.marati.core.ui.PreferenceManager
import com.zeroone.marati.core.ui.ViewModelFactory
import com.zeroone.marati.dataStore

class LoginActivity : AppCompatActivity() {

    private lateinit var binding : ActivityLoginBinding
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest
    private lateinit var  viewModel: LoginViewModel
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        when (requestCode) {
            REQ_ONE_TAP -> {
                try {
                    val credential = oneTapClient.getSignInCredentialFromIntent(data)
                    val idToken = credential.googleIdToken
                    val username = credential.id
                    val password = credential.password
                    when {
                        idToken != null -> {
                            // Got an ID token from Google. Use it to authenticate
                            // with your backend.
                            Log.d(TAG, idToken)
                        }
                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                            Log.d(TAG, "Got password.")
                        }
                        else -> {
                            // Shouldn't happen.
                            Log.d(TAG, "No ID token or password!")
                        }
                    }
                } catch (e: ApiException) {
                    Log.d(TAG,e.message.toString())
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        val pref = PreferenceManager.getInstance(dataStore)
        val factory: ViewModelFactory = ViewModelFactory.getInstance(this,pref)
        viewModel = ViewModelProvider(this,factory).get(LoginViewModel::class.java)

        // one tap ui google setup
        oneTapClient = Identity.getSignInClient(this)
        signInRequest = BeginSignInRequest.builder()
            .setPasswordRequestOptions(BeginSignInRequest.PasswordRequestOptions.builder()
                .setSupported(true)
                .build())
            .setGoogleIdTokenRequestOptions(
                BeginSignInRequest.GoogleIdTokenRequestOptions.builder()
                    .setSupported(true)
                    // Your server's client ID, not your Android client ID.
                    .setServerClientId(getString(R.string.oauth_google))
                    // Only show accounts previously used to sign in.
                    .setFilterByAuthorizedAccounts(false)
                    .build())
            // Automatically sign in when exactly one credential is retrieved.
            .setAutoSelectEnabled(true)
            .build()



        binding.btnGoogle.setOnClickListener{
            oneTapClient.beginSignIn(signInRequest)
                .addOnSuccessListener(this) { result ->
                    try {
                        startIntentSenderForResult(
                            result.pendingIntent.intentSender, REQ_ONE_TAP,
                            null, 0, 0, 0, null)
                    } catch (e: IntentSender.SendIntentException) {
                        Log.e(TAG, "Couldn't start One Tap UI: ${e.localizedMessage}")
                    }
                }
                .addOnFailureListener(this) { e ->
                    // No saved credentials found. Launch the One Tap sign-up flow, or
                    // do nothing and continue presenting the signed-out UI.
                    Log.d(TAG, e.localizedMessage)
                }
        }

        authListener()
        submitListener()
        liveDataListener()
    }

    private fun liveDataListener() {
        viewModel.errorMessage.observe(this){
            binding.button.text = "Login"
            binding.button.isEnabled = true
            Snackbar.make(binding.root,it,Snackbar.LENGTH_LONG).show()
        }

        viewModel.user.observe(this){
            viewModel.setToken(it.accessToken)
            nextPage()
        }
    }

    private fun nextPage() {
        binding.button.text = "Login"
        binding.button.isEnabled = true
        finish()
        startActivity(Intent(this@LoginActivity, HomeActivity::class.java))
    }

    private fun submitListener() {

        binding.button.setOnClickListener{
            if(binding.email.text.toString() != "" && binding.password.text.toString() != "" && binding.emailContainer.helperText == null && binding.passwordContainer.helperText==null){
                binding.button.isEnabled = false
                binding.button.text = "Loading..."
                viewModel.login(binding.email.text.toString(),binding.password.text.toString())
            }
        }
    }

    private fun authListener() {
        binding.email.setOnFocusChangeListener{ _,focused ->
            if(!focused){
                val emailText = binding.email.text.toString()
                if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
                    binding.email.background = getDrawable(R.drawable.background_inp_err)
                    binding.emailContainer.helperText = "Invalid Email Address"
                } else{
                    binding.email.background = getDrawable(R.drawable.background_inp)
                    binding.emailContainer.helperText = ""

                }
            }
        }

        binding.email.addTextChangedListener {
            val emailText = binding.email.text.toString()
            if(!Patterns.EMAIL_ADDRESS.matcher(emailText).matches()){
                binding.email.background = getDrawable(R.drawable.background_inp_err)
                binding.emailContainer.helperText = "Invalid Email Address"
            } else{
                binding.email.background = getDrawable(R.drawable.background_inp)
                binding.emailContainer.helperText = ""

            }
        }

        binding.password.setOnFocusChangeListener{ _,focused ->
            if(!focused){
                val passwordText = binding.password.text.toString()
                if(passwordText.length < 8){
                    binding.password.background = getDrawable(R.drawable.background_inp_err)
                    binding.passwordContainer.helperText = "Min Password 8 Chars"
                } else{
                    binding.password.background = getDrawable(R.drawable.background_inp)
                    binding.passwordContainer.helperText = ""

                }
            }
        }

        binding.password.addTextChangedListener {
            val passwordText = binding.password.text.toString()
            if(passwordText.length < 8){
                binding.password.background = getDrawable(R.drawable.background_inp_err)
                binding.passwordContainer.helperText = "Min Password 8 Chars"
            } else{
                binding.password.background = getDrawable(R.drawable.background_inp)
                binding.passwordContainer.helperText = ""
            }
        }
    }


    companion object {
        val TAG = "LoginActivity"
        val REQ_ONE_TAP = 2
    }
}