package com.zeroone.marati.register

import android.content.Intent
import android.content.IntentSender
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.util.Patterns
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.common.api.ApiException
import com.google.android.material.snackbar.Snackbar
import com.zeroone.marati.R
import com.zeroone.marati.core.ui.PreferenceManager
import com.zeroone.marati.core.ui.ViewModelFactory
import com.zeroone.marati.dataStore
import com.zeroone.marati.databinding.ActivityRegisterBinding
import com.zeroone.marati.home.HomeActivity
import com.zeroone.marati.login.LoginActivity
import maes.tech.intentanim.CustomIntent

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding
    private lateinit var viewModel: RegisterViewModel
    private lateinit var oneTapClient: SignInClient
    private lateinit var signInRequest: BeginSignInRequest

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
                            viewModel.socialLogin(idToken)
                        }
                        password != null -> {
                            // Got a saved username and password. Use them to authenticate
                            // with your backend.
                        }
                        else -> {
                            // Shouldn't happen.
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
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val pref = PreferenceManager.getInstance(dataStore)
        viewModel = ViewModelProvider(this,ViewModelFactory(this,pref)).get(RegisterViewModel::class.java)
        window.statusBarColor = Color.parseColor("#000000")


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
        viewModelListener()
    }

    private fun viewModelListener() {
        viewModel.errorMessage.observe(this){
            binding.button.text = "Register"
            binding.button.isEnabled = true
            Snackbar.make(binding.root,it, Snackbar.LENGTH_LONG).show()
        }

        viewModel.user.observe(this){
            viewModel.setToken(it.accessToken)
            viewModel.setUserId(it.id)
            nextPage()
        }
    }

    private fun nextPage() {
        binding.button.text = "Register"
        binding.button.isEnabled = true
        val intent = Intent(this@RegisterActivity, HomeActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        CustomIntent.customType(this,"left-to-right")
        finish()
    }

    private fun submitListener() {

        binding.button.setOnClickListener{
            if(binding.name.text.toString() != "" && binding.email.text.toString() != ""
                && binding.password.text.toString() != ""
                && binding.emailContainer.helperText == null
                && binding.passwordContainer.helperText==null
                && binding.nameContainer.helperText == null){
                binding.button.isEnabled = false
                binding.button.text = "Loading..."
                viewModel.register(binding.email.text.toString(),binding.password.text.toString(),binding.name.text.toString())
            }
        }
    }

    private fun authListener() {
        binding.name.setOnFocusChangeListener{ _,focused ->
            if(!focused){
                val nameText = binding.name.text.toString()
                if(nameText.length < 7){
                    binding.name.background = getDrawable(R.drawable.background_inp_err)
                    binding.nameContainer.helperText = "Min 6 Chars"
                } else{
                    binding.name.background = getDrawable(R.drawable.background_inp)
                    binding.nameContainer.helperText = ""
                }
            }
        }

        binding.name.addTextChangedListener {
            val nameText = binding.name.text.toString()
            if(nameText.length < 7){
                binding.name.background = getDrawable(R.drawable.background_inp_err)
                binding.nameContainer.helperText = "Min 6 Chars"
            } else{
                binding.name.background = getDrawable(R.drawable.background_inp)
                binding.nameContainer.helperText = ""
            }
        }

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
        val TAG = "RegisterActivity"
        val REQ_ONE_TAP = 2
    }
}