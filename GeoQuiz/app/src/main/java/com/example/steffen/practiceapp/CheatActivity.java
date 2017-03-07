package com.example.steffen.practiceapp;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.Intent;
import android.icu.text.MessageFormat;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    private static final String KEY_SHOW_ANSWER = "show_answer";
    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.steffen.practiceapp.answer_is_true";
    private static final String EXTRA_ANSWER_SHOWN = "com.example.steffen.practiceapp.answer_shown";

    private boolean mAnswerIsTrue;
    private boolean mShowAnswer;

    private TextView mDiscourageCheatTextView;
    private Button mShowAnswerButton;
    private TextView mAnswerTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cheat);

        mDiscourageCheatTextView = (TextView)findViewById(R.id.discourage_cheat_text_view);
        mAnswerTextView = (TextView)findViewById(R.id.answer_text_view);

        mShowAnswerButton = (Button)findViewById(R.id.show_answer_button);
        mShowAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAnswer();
            }
        });

        mAnswerIsTrue = getIntent().getBooleanExtra(EXTRA_ANSWER_IS_TRUE, false);
        mShowAnswer = getIntent().getBooleanExtra(EXTRA_ANSWER_SHOWN, false);

        if (savedInstanceState != null) {
            mShowAnswer = savedInstanceState.getBoolean(KEY_SHOW_ANSWER, mShowAnswer);
            if (mShowAnswer) {
                setAnswerShownResult(true);
            }
        }

        updateUi(true);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(KEY_SHOW_ANSWER, mShowAnswer);
    }

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, boolean answerShown) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_ANSWER_SHOWN, answerShown);
        return intent;
    }

    public static boolean wasAnswerShown(Intent result) {
        return result.getBooleanExtra(EXTRA_ANSWER_SHOWN, false);
    }

    private void setAnswerShownResult(boolean isAnswerShown) {
        Intent data = new Intent();
        data.putExtra(EXTRA_ANSWER_SHOWN, isAnswerShown);
        setResult(RESULT_OK, data);
    }

    private void updateUi(boolean initial) {
        if (mShowAnswer) {
            String answer = getString(mAnswerIsTrue ? R.string.cheat_answer_true : R.string.cheat_answer_false);
            mAnswerTextView.setText(MessageFormat.format(getString(R.string.cheat_answer), answer));
        }

        if (initial) {
            mDiscourageCheatTextView.setVisibility(mShowAnswer ? View.GONE : View.VISIBLE);
            mShowAnswerButton.setVisibility(mShowAnswer ? View.GONE : View.VISIBLE);
            mAnswerTextView.setVisibility(mShowAnswer ? View.VISIBLE : View.GONE);
            return;
        }

        if (mShowAnswer) {
            if (mDiscourageCheatTextView.getVisibility() == View.VISIBLE) {
                mDiscourageCheatTextView.setVisibility(View.GONE);
            }

            mAnswerTextView.setVisibility(View.VISIBLE);

            if (mShowAnswerButton.getVisibility() == View.VISIBLE) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    int cx = mShowAnswerButton.getWidth() / 2;
                    int cy = mShowAnswerButton.getHeight() / 2;
                    float radius = mShowAnswerButton.getWidth() / 2;
                    Animator anim = ViewAnimationUtils.createCircularReveal(mShowAnswerButton, cx, cy, radius, 0);
                    anim.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            super.onAnimationEnd(animation);
                            mShowAnswerButton.setVisibility(View.GONE);
                        }
                    });
                    anim.start();
                } else {
                    mShowAnswerButton.setVisibility(View.GONE);
                }
            }
        } else {
            mDiscourageCheatTextView.setVisibility(View.VISIBLE);
            mShowAnswerButton.setVisibility(View.VISIBLE);
            mAnswerTextView.setVisibility(View.GONE);
        }
    }

    private void showAnswer() {
        setAnswerShownResult(true);
        mShowAnswer = true;
        updateUi(false);
    }
}
