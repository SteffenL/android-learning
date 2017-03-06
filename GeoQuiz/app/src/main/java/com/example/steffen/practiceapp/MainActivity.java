package com.example.steffen.practiceapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.text.MessageFormat;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static final String KEY_INDEX = "index";
    private static final String KEY_ANSWERS_CORRECT = "answers_correct";
    private static final String KEY_ANSWERS_RECEIVED_STATES = "answers_received_states";

    private View mInQuizLayout;
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionTextView;
    private Button mCheatButton;

    private View mPostQuizLayout;
    private TextView mVerdictTextView;
    private Button mRestartButton;

    private Question[] mQuestionBank = new Question[] {
        new Question(R.string.question_sushi, true),
        new Question(R.string.question_tea, false),
        new Question(R.string.question_mouth, true),
        new Question(R.string.question_true, false),
        new Question(R.string.question_piracy_bad, true),
        new Question(R.string.question_piracy_good, true)
    };

    private int mCurrentIndex = 0;
    private boolean[] mAnswersReceivedStates = new boolean[mQuestionBank.length];
    private boolean[] mAnswersCorrect = new boolean[mQuestionBank.length];
    private int mCorrectAnswersCount = 0;
    private int mTotalAnswered = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate(Bundle) called");
        setContentView(R.layout.activity_main);

        // In-quiz layout

        mInQuizLayout = findViewById(R.id.in_quiz_layout);

        mQuestionTextView = (TextView)findViewById(R.id.question_text_view);
        mQuestionTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        mTrueButton = (Button)findViewById(R.id.true_button);
        mTrueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(true);
            }
        });

        mFalseButton = (Button)findViewById(R.id.false_button);
        mFalseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkAnswer(false);
            }
        });

        mPreviousButton = (ImageButton)findViewById(R.id.previous_button);
        mPreviousButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                previousQuestion();
            }
        });

        mNextButton = (ImageButton)findViewById(R.id.next_button);
        mNextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                nextQuestion();
            }
        });

        mCheatButton = (Button)findViewById(R.id.cheat_button);
        mCheatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmCheat();
            }
        });

        // Post-quiz layout

        mPostQuizLayout = findViewById(R.id.post_quiz_layout);

        mRestartButton = (Button)findViewById(R.id.restart_button);
        mRestartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                restartQuiz();
            }
        });

        mVerdictTextView = (TextView)findViewById(R.id.verdict_text_view);

        if (savedInstanceState != null) {
            mCurrentIndex = savedInstanceState.getInt(KEY_INDEX);
            mAnswersReceivedStates = savedInstanceState.getBooleanArray(KEY_ANSWERS_RECEIVED_STATES);
            mAnswersCorrect = savedInstanceState.getBooleanArray(KEY_ANSWERS_CORRECT);
            mCorrectAnswersCount = numberOfCorrectAnswers(mAnswersReceivedStates, mAnswersCorrect);
            mTotalAnswered = numberOfAnswers(mAnswersReceivedStates);
        }

        updateQuizUi();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume() called");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause() called");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState(Bundle) called");
        outState.putInt(KEY_INDEX, mCurrentIndex);
        outState.putBooleanArray(KEY_ANSWERS_RECEIVED_STATES, mAnswersReceivedStates);
        outState.putBooleanArray(KEY_ANSWERS_CORRECT, mAnswersCorrect);
    }

    private void updateQuizUi() {
        boolean allAnswered = allQuestionsAnswered();
        if (!allAnswered) {
            int question = mQuestionBank[mCurrentIndex].getTextResId();
            mQuestionTextView.setText(question);

            boolean haveReceivedAnswer = mAnswersReceivedStates[mCurrentIndex];
            mTrueButton.setEnabled(!haveReceivedAnswer);
            mFalseButton.setEnabled(!haveReceivedAnswer);
            mCheatButton.setEnabled(!haveReceivedAnswer);
        } else {
            int correctAnswers = mCorrectAnswersCount;
            int totalQuestions = mQuestionBank.length;

            String message;
            if (correctAnswers == totalQuestions) {
                message = getString(R.string.result_all_correct);
            } else {
                double correctRatio = (double) correctAnswers / (double) totalQuestions;
                message = MessageFormat.format(getString(R.string.result_partial_correct), correctAnswers, totalQuestions, correctRatio);
            }

            mVerdictTextView.setText(message);
        }

        mInQuizLayout.setVisibility(allAnswered ? View.GONE : View.VISIBLE);
        mPostQuizLayout.setVisibility(allAnswered ? View.VISIBLE : View.GONE);
    }

    private void checkAnswer(boolean userPressedTrue) {
        boolean answerIsCorrect = userPressedTrue == mQuestionBank[mCurrentIndex].isAnswerTrue();
        mAnswersReceivedStates[mCurrentIndex] = true;
        mAnswersCorrect[mCurrentIndex] = answerIsCorrect;
        ++mTotalAnswered;

        if (answerIsCorrect) {
            ++mCorrectAnswersCount;
        }

        updateQuizUi();
    }

    private boolean allQuestionsAnswered() {
        return mTotalAnswered == mQuestionBank.length;
    }

    private int numberOfAnswers(boolean[] states) {
        int result = 0;
        for (boolean state : states) {
            if (state) {
                ++result;
            }
        }

        return result;
    }

    private int numberOfCorrectAnswers(boolean[] states, boolean[] answersCorrect) {
        int result = 0;
        for (int i = 0; i < states.length; ++i) {
            if (states[i] && answersCorrect[i]) {
                ++result;
            }
        }

        return result;
    }

    private void nextQuestion() {
        mCurrentIndex = (mCurrentIndex + 1) % mQuestionBank.length;
        updateQuizUi();
    }

    private void previousQuestion() {
        mCurrentIndex = (mQuestionBank.length + mCurrentIndex - 1) % mQuestionBank.length;
        updateQuizUi();
    }

    private void restartQuiz() {
        mCurrentIndex = 0;
        for (int i = 0; i < mQuestionBank.length; ++i) {
            mAnswersReceivedStates[i] = false;
            mAnswersCorrect[i] = false;
        }

        mTotalAnswered = 0;
        mCorrectAnswersCount = 0;

        updateQuizUi();
    }

    private void confirmCheat() {

    }
}
