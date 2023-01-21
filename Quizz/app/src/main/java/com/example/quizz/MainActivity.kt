package com.example.quizz

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val startBtn: AppCompatButton = findViewById(R.id.startBtn)
        val etName: EditText = findViewById(R.id.et_name)
        startBtn.setOnClickListener {
            if(etName.text.isEmpty()){
                println("alert thong bao")
                Toast.makeText(this, "Please enter your name", Toast.LENGTH_LONG).show()
            }else{
                val intent = Intent(this, QuizzQuestionsActivity::class.java)
                intent.putExtra(Constants.USER_NAME, etName.text.toString())


                startActivity(intent)




            }

        }


    }
}