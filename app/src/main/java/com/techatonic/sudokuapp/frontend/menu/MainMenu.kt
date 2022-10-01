package com.techatonic.sudokuapp.frontend.menu

import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.techatonic.sudokuapp.R
import com.techatonic.sudokuapp.backend.staticdata.User
import com.techatonic.sudokuapp.frontend.custom.PlaySudokuActivity
import kotlinx.android.synthetic.main.main_menu.*

class MainMenu : AppCompatActivity() {

    lateinit var userSharedPreferences: SharedPreferences
    lateinit var usernameInputField: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.main_menu)

        userSharedPreferences = getSharedPreferences(getString(R.string.user_data_shared_preferences_key), MODE_PRIVATE)

        val username: String? = userSharedPreferences.getString(getString(R.string.username_key), null)

        if (username.isNullOrBlank()) {
            enterUsername()
            playGameButton.setOnClickListener { handleButtonClick() }
        } else {
            User.username = username
            playGameButton.setOnClickListener {
                startActivity(Intent(this, PlaySudokuActivity::class.java))
            }
        }
    }

    private fun enterUsername() {
        usernameInputField = findViewById(R.id.usernameInputField)
        usernameInputField.isEnabled = true
        usernameInputField.visibility = View.VISIBLE
    }

    private fun handleButtonClick() {
        val enteredUsername: String = usernameInputField.text.toString()
        if (enteredUsername == "") {
            return usernameFail("Username field cannot be empty")
        }

        val db = FirebaseFirestore.getInstance()
        val documentReference =
            db.collection("userdata").document("usernames")

        documentReference.get().addOnSuccessListener { document ->
            if (document.exists()) {
                val data = document.data
                Log.d(ContentValues.TAG, "DocumentSnapshot data: $data")
                val usernames: ArrayList<String>
                if (data != null && data["usernames"] != null) {
                    usernames = data["usernames"] as ArrayList<String>
                    if (!usernames.contains(enteredUsername)) {
                        usernameSuccess(enteredUsername)
                    } else {
                        usernameFail("Username has been taken. Please try again")
                    }
                } else {
                    usernameFail("Unable to retrieve usernames list. Please try again")
                }
            } else {
                Log.d(ContentValues.TAG, "No document found")
            }
        }
            .addOnFailureListener { println("FAILURE - UNABLE TO RETRIEVE DOCUMENT") }
            .addOnCanceledListener { println("FAILURE - UNABLE TO RETRIEVE DOCUMENT #1") }
    }

    private fun usernameFail(message: String) {
        usernameInputField.error = message
    }

    private fun usernameSuccess(username: String) {
        Toast.makeText(this, "Username created successfully", Toast.LENGTH_LONG).show()
        with(userSharedPreferences.edit()) {
            putString(getString(R.string.username_key), username)
            apply()
        }
        println(userSharedPreferences.all)
        findViewById<EditText>(R.id.usernameInputField).visibility = View.INVISIBLE

        addUsernameToDatabase(username)

        startActivity(Intent(this, PlaySudokuActivity::class.java))
    }

    private fun addUsernameToDatabase(username: String) {
        val db = FirebaseFirestore.getInstance()
        val documentReference =
            db.collection("userdata").document("usernames")

        documentReference.update("usernames", FieldValue.arrayUnion(username))
    }
}
