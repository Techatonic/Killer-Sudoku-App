package com.techatonic.sudokuapp.frontend.menu

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.techatonic.sudokuapp.R
import com.techatonic.sudokuapp.frontend.custom.PlaySudokuActivity
import kotlinx.android.synthetic.main.main_menu.*

class MainMenu : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu);

        playGameButton.setOnClickListener {
            startActivity(Intent(this, PlaySudokuActivity::class.java))
        }
    }

}