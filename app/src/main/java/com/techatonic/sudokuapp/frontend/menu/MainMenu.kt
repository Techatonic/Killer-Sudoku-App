package com.techatonic.sudokuapp.frontend.menu

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult
import com.google.firebase.auth.FirebaseAuth
import com.techatonic.sudokuapp.R
import com.techatonic.sudokuapp.backend.staticdata.User
import com.techatonic.sudokuapp.frontend.custom.PlaySudokuActivity
import kotlinx.android.synthetic.main.main_menu.*

class MainMenu : AppCompatActivity() {

    // Setup Firebase Auth
    private val signInLauncher = registerForActivityResult(FirebaseAuthUIActivityResultContract()){
            res -> this.onSignInResult(res)
    }
    private val providers = arrayListOf(
        AuthUI.IdpConfig.GoogleBuilder().build()
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_menu)

        playGameButton.setOnClickListener {
            startActivity(Intent(this, PlaySudokuActivity::class.java))
        }

        // Create and launch sign-in intent
        val signInIntent = AuthUI.getInstance().createSignInIntentBuilder().setAvailableProviders(providers).build()
        signInLauncher.launch(signInIntent)
    }

    private fun onSignInResult(result: FirebaseAuthUIAuthenticationResult){
        if(result.resultCode == RESULT_OK){
            // Successfully signed in
            val user = FirebaseAuth.getInstance().currentUser
            User.email = user?.email
        } else {
            sendErrorMessage()
        }
    }

    private fun sendErrorMessage(){
        AlertDialog.Builder(this)
            .setTitle("Sign In Failed")
            .setMessage("Sign in was unsuccessful. Please try again")
            .setPositiveButton("Ok") { dialog, _ -> dialog.cancel() }
            .setNegativeButton("Close app") { _, _ -> finish() }
    }

}