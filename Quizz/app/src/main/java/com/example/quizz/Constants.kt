package com.example.quizz

object Constants {
    const val USER_NAME: String = "username"
    const val TOTAL_QUESTION: String = "ttq"

    const val CORRECT_ANSWER: String = "correctanswer"


    fun getQuestion(): ArrayList<Question> {


        val questionList = ArrayList<Question>()
        val que1 = Question(
            1, "Đây là ai?",
            R.drawable.cr7, "Cr7", "Messi", "Neymar",
            "Mbappe",

            1
        );
        val que2 = Question(
            1, "Đây là ai?",
            R.drawable.m10, "Asufati", "Messi", "Mount",
            "Torres",

            2
        )
        val que3 = Question(
            1, "Đây là ai?",
            R.drawable.njr, "Halland", "KDB", "Neymar",
            "Berlingham",

            3
        )
        val que4 = Question(
            1, "Đây là ai?",
            R.drawable.km, "Sane", "Pogba", "Saka",
            "Mbappe",

            4
        )

        questionList.add(que1);
        questionList.add(que2);
        questionList.add(que3);
        questionList.add(que4);
        return questionList;
    }
}