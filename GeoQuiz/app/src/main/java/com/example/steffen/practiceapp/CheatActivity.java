package com.example.steffen.practiceapp;

import android.content.Context;
import android.content.Intent;
import android.icu.text.MessageFormat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class CheatActivity extends AppCompatActivity {
    private static final String EXTRA_ANSWER_IS_TRUE = "com.example.steffen.practiceapp.answer_is_true";
    private static final String EXTRA_ALREADY_CHEATED = "com.example.steffen.practiceapp.already_cheated";

    private boolean mAnswerIsTrue;
    private boolean mHasCheated;

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
        mHasCheated = getIntent().getBooleanExtra(EXTRA_ALREADY_CHEATED, false);

        updateUi();
    }

    public static Intent newIntent(Context packageContext, boolean answerIsTrue, boolean alreadyCheated) {
        Intent intent = new Intent(packageContext, CheatActivity.class);
        intent.putExtra(EXTRA_ANSWER_IS_TRUE, answerIsTrue);
        intent.putExtra(EXTRA_ALREADY_CHEATED, alreadyCheated);
        return intent;
    }

    private void updateUi() {
        mDiscourageCheatTextView.setVisibility(mHasCheated ? View.GONE : View.VISIBLE);
        mShowAnswerButton.setVisibility(mHasCheated ? View.GONE : View.VISIBLE);
        mAnswerTextView.setVisibility(mHasCheated ? View.VISIBLE : View.GONE);

        if (mHasCheated) {
            String answer = getString(mAnswerIsTrue ? R.string.cheat_answer_true : R.string.cheat_answer_false);
            mAnswerTextView.setText(MessageFormat.format(getString(R.string.cheat_answer), answer));
        }
    }

    private void showAnswer() {
        mHasCheated = true;
        updateUi();
    }
}
