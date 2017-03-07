package com.example.steffen.practiceapp;

import android.content.Intent;
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
    private static final String KEY_ANSWERS_SHOWN = "answers_shown";
    private static final String KEY_CHEAT_TOKENS = "cheat_tokens";
    private static final int REQUEST_CODE_CHEAT = 0;
    private static final int INITIAL_CHEAT_TOKENS = 3;

    private View mInQuizLayout;
    private Button mTrueButton;
    private Button mFalseButton;
    private ImageButton mNextButton;
    private ImageButton mPreviousButton;
    private TextView mQuestionTextView;
    private Button mCheatButton;
    private TextView mCheatTokensTextView;

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
    private boolean[] mAnswersShown = new boolean[mQuestionBank.length];
    private int mCorrectAnswersCount = 0;
    private int mTotalAnswered = 0;
    private boolean mIsCheater = false;
    private int mCheatTokens = INITIAL_CHEAT_TOKENS;

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
                confirmShowAnswer();
            }
        });

        mCheatTokensTextView = (TextView)findViewById(R.id.cheat_tokens_text_view);

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
            mAnswersShown = savedInstanceState.getBooleanArray(KEY_ANSWERS_SHOWN);
            mCheatTokens = savedInstanceState.getInt(KEY_CHEAT_TOKENS);

            mCorrectAnswersCount = numberOfCorrectAnswers(mAnswersReceivedStates, mAnswersCorrect);
            mTotalAnswered = numberOfAnswers(mAnswersReceivedStates);
            mIsCheater = anyAnswersShown(mAnswersShown);
        }
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

        updateQuizUi();
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
        outState.putBooleanArray(KEY_ANSWERS_SHOWN, mAnswersShown);
        outState.putInt(KEY_CHEAT_TOKENS, mCheatTokens);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, "onActivityResult(int, int, Intent) called");
        if (resultCode != RESULT_OK) {
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_CHEAT:
                handleActivityResultForCheat(data);
                break;

            default:
                break;
        }
    }

    private void handleActivityResultForCheat(Intent data) {
        if (data == null) {
            return;
        }

        boolean answerShown = CheatActivity.wasAnswerShown(data);
        if (answerShown) {
            mAnswersShown[mCurrentIndex] = true;
            mIsCheater = true;
            --mCheatTokens;
        }
    }

    private void updateQuizUi() {
        boolean allAnswered = allQuestionsAnswered();
        if (!allAnswered) {
            int question = mQuestionBank[mCurrentIndex].getTextResId();
            mQuestionTextView.setText(question);

            boolean haveReceivedAnswer = mAnswersReceivedStates[mCurrentIndex];
            mTrueButton.setEnabled(!haveReceivedAnswer);
            mFalseButton.setEnabled(!haveReceivedAnswer);
            mCheatButton.setEnabled(!haveReceivedAnswer && (mAnswersShown[mCurrentIndex] || haveCheatTokens()));
            mCheatTokensTextView.setText(MessageFormat.format(getString(R.string.cheat_tokens), mCheatTokens));
        } else {
            int correctAnswers = mCorrectAnswersCount;
            int totalQuestions = mQuestionBank.length;

            String message;
            if (correctAnswers == totalQuestions) {
                int resId = mIsCheater ? R.string.result_all_correct_with_cheats : R.string.result_all_correct;
                message = getString(resId);
            } else {
                double correctRatio = (double) correctAnswers / (double) totalQuestions;
                int resId = mIsCheater ? R.string.result_partial_correct_with_cheats : R.string.result_partial_correct;
                message = MessageFormat.format(getString(resId), correctAnswers, totalQuestions, correctRatio);
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

    private boolean anyAnswersShown(boolean[] answersShown) {
        boolean result = false;
        for (int i = 0; i < answersShown.length; ++i) {
            if (answersShown[i]) {
                result = true;
                break;
            }
        }

        return result;
    }

    private boolean haveCheatTokens() {
        return mCheatTokens > 0;
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
            mAnswersShown[i] = false;
        }

        mTotalAnswered = 0;
        mCorrectAnswersCount = 0;
        mIsCheater = false;
        mCheatTokens = INITIAL_CHEAT_TOKENS;

        updateQuizUi();
    }

    private void confirmShowAnswer() {
        boolean answerIsTrue = mQuestionBank[mCurrentIndex].isAnswerTrue();
        boolean answerShown = mAnswersShown[mCurrentIndex];

        Intent intent = CheatActivity.newIntent(this, answerIsTrue, answerShown);
        startActivityForResult(intent, REQUEST_CODE_CHEAT);
    }
}
