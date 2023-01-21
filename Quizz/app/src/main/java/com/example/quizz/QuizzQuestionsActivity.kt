package com.example.quizz

import android.content.Intent
import android.graphics.Color
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import androidx.core.content.ContextCompat

class QuizzQuestionsActivity : AppCompatActivity(), View.OnClickListener {
    private var mUserName: String? = null
    private var mCorrectAnswer: Int = 0


    private var mCurPos: Int = 1
    private var mQuestionList: ArrayList<Question>? = null
    private var mSelectedOptId: Int = 0


    private var processBar: ProgressBar? = null
    private var tvProgress: TextView? = null
    private var tvQuestion: TextView? = null

    private var ivImage: ImageView? = null
    private var tvOptionOne: TextView? = null
    private var tvOptionTwo: TextView? = null
    private var tvOptionThree: TextView? = null
    private var tvOptionFour: TextView? = null
    private var btnSubmit: Button? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quizz_questions)

        mUserName = intent.getStringExtra(Constants.USER_NAME)


        processBar = findViewById(R.id.progressBar)
        tvProgress = findViewById(R.id.tv_process)
        tvQuestion = findViewById(R.id.tv_question)
        ivImage = findViewById(R.id.iv_image)

        tvOptionOne = findViewById(R.id.tv_opt1)
        tvOptionTwo = findViewById(R.id.tv_opt2)
        tvOptionThree = findViewById(R.id.tv_opt3)
        tvOptionFour = findViewById(R.id.tv_opt4)

        btnSubmit = findViewById(R.id.btnSubmit)

        tvOptionOne?.setOnClickListener(this)
        tvOptionTwo?.setOnClickListener(this)
        tvOptionThree?.setOnClickListener(this)
        tvOptionFour?.setOnClickListener(this)

        btnSubmit?.setOnClickListener(this)



        mQuestionList = Constants.getQuestion()

        setQuestion()


    }

    private fun setQuestion() {
        mSelectedOptId = 0
        defaultOptionsView()
        print("size ${mQuestionList?.size}")
        val ques: Question = mQuestionList!![mCurPos - 1]
        ivImage?.setImageResource(ques.image)


        processBar?.progress = mCurPos
        tvProgress?.text = "$mCurPos/${processBar?.max}"
        tvQuestion?.text = ques.question


        tvOptionOne?.text = ques.optionOne
        tvOptionTwo?.text = ques.optionTwo
        tvOptionThree?.text = ques.optionThree
        tvOptionFour?.text = ques.optionFour

        if (mCurPos == mQuestionList!!.size) {

            btnSubmit?.text = "HOÀN THÀNH"
        } else {
            btnSubmit?.text = "KIỂM TRA"

        }
    }

    private fun defaultOptionsView() {

        val options = ArrayList<TextView>()
        tvOptionOne?.let {
            options.add(0, it)
        }
        tvOptionTwo?.let {
            options.add(1, it)
        }
        tvOptionThree?.let {
            options.add(2, it)
        }
        tvOptionFour?.let {
            options.add(3, it)
        }

        for (option in options) {
            option.setTextColor(Color.parseColor("#7A8089"))
            option.typeface = Typeface.DEFAULT
            option.background = ContextCompat.getDrawable(
                this,
                R.drawable.default_btn
            )
        }
    }

    private fun selectedOptView(tv: TextView, selectedOptNum: Int) {


        defaultOptionsView()

        mSelectedOptId = selectedOptNum
        tv.setTextColor(Color.parseColor("#3F51B5"))
        tv.setTypeface(tv.typeface, Typeface.BOLD)


        tv.background = ContextCompat.getDrawable(
            this,
            R.drawable.selected_btn
        )


    }

    override fun onClick(p0: View?) {
        when (p0?.id) {
            R.id.tv_opt1 -> {
                tvOptionOne?.let {
                    selectedOptView(it, 1)
                }
            }
            R.id.tv_opt2 -> {
                tvOptionTwo?.let {
                    selectedOptView(it, 2)
                }
            }
            R.id.tv_opt3 -> {
                tvOptionThree?.let {
                    selectedOptView(it, 3)
                }
            }
            R.id.tv_opt4 -> {
                tvOptionFour?.let {
                    selectedOptView(it, 4)
                }
            }
            R.id.btnSubmit -> {
                println("Selected id $mSelectedOptId")
                if (mSelectedOptId == 11) {
                    mCurPos++
                    println("-------------------->>>>>")
                    when {
                        mCurPos <= mQuestionList!!.size -> {
                            println("--------------------////")
                            setQuestion()
                        }
                        else -> {
                            val intent = Intent(this, ResultActivity::class.java)

                            intent.putExtra(Constants.USER_NAME, mUserName)
                            intent.putExtra(Constants.CORRECT_ANSWER, mCorrectAnswer)
                            intent.putExtra(Constants.TOTAL_QUESTION, mQuestionList?.size)

                            startActivity(intent)
                            finish()
                        }
                    }
                } else if (mSelectedOptId == 0) {
                    println("Please select answer")
                } else {
                    val question = mQuestionList?.get(mCurPos - 1)
                    if (question!!.correctAnswer != mSelectedOptId) {
                        answerView(mSelectedOptId, R.drawable.incorrect_btn)
                    } else {
                        mCorrectAnswer++;
                    }
                    answerView(question.correctAnswer, R.drawable.correct_btn)

                    if (mCurPos < mQuestionList!!.size) {

                        println("CURRENT POS ${mCurPos} GO TO NEXT QUESTION")
                        btnSubmit?.text = "CÂU KẾ TIẾP"

                    }

                    mSelectedOptId = 11

                }

            }
        }


    }

    private fun answerView(answer: Int, drawableView: Int) {
        when (answer) {
            1 -> {
                tvOptionOne?.background = ContextCompat.getDrawable(this, drawableView)
            }
            2 -> {
                tvOptionTwo?.background = ContextCompat.getDrawable(this, drawableView)
            }
            3 -> {
                tvOptionThree?.background = ContextCompat.getDrawable(this, drawableView)
            }
            4 -> {
                tvOptionFour?.background = ContextCompat.getDrawable(this, drawableView)
            }
        }

    }
}