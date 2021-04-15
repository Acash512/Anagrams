package com.acash.anagrams;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;

public class MainActivity extends AppCompatActivity {

    private AnagramDictionary dictionary;
    private EditText etWord;
    private TextView tvGameStatus;
    private TextView tvResult;
    private FloatingActionButton fab;
    private String currentWord;
    private HashSet<String> anagrams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        AssetManager assetManager = getAssets();

        try {
            InputStream inputStream = assetManager.open("words.txt");
            dictionary = new AnagramDictionary(new InputStreamReader(inputStream));
        } catch (IOException e) {
            Toast.makeText(this, "Could not load Dictionary", Toast.LENGTH_SHORT).show();
        }

        etWord = findViewById(R.id.etWord);
        tvGameStatus = findViewById(R.id.tvGameStatus);
        tvResult = findViewById(R.id.tvResult);
        fab = findViewById(R.id.fab);

        etWord.setOnEditorActionListener((v, actionId, event) -> {
            boolean handled = false;
            if (actionId == EditorInfo.IME_ACTION_GO || (
                    actionId == EditorInfo.IME_NULL && event != null && event.getAction() == KeyEvent.ACTION_DOWN)) {
                processWord();
                handled = true;
            }
            return handled;
        });

        fab.setOnClickListener(v -> {
            if (currentWord == null) {
                currentWord = dictionary.pickGoodStarterWord();
                anagrams = dictionary.getAnagramsWithOneMoreLetter(currentWord);
                tvGameStatus.setText(getString(R.string.start_message,currentWord.toUpperCase(),currentWord));
                fab.setImageResource(R.drawable.question_mark);
                tvResult.setText("");
                etWord.setText("");
                etWord.setEnabled(true);
                etWord.requestFocus();
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.showSoftInput(etWord, InputMethodManager.SHOW_IMPLICIT);
            }else{
                etWord.setText(currentWord);
                etWord.setEnabled(false);
                fab.setImageResource(R.drawable.ic_baseline_play_arrow_24);
                currentWord = null;
                ArrayList<String> anagramsList = new ArrayList<>(anagrams);
                Collections.sort(anagramsList);
                tvResult.append(TextUtils.join("\n", anagramsList));
                tvGameStatus.append(" Hit 'Play' to start again");
            }
        });
    }

    private void processWord() {
        String word = etWord.getText().toString().trim().toLowerCase();

        if (word.length() == 0) {
            return;
        }

        String color = "#cc0029";

        if (anagrams.contains(word)) {
            anagrams.remove(word);
            color = "#00aa29";
        } else {
            word = "X " + word;
        }

        tvResult.append(Html.fromHtml(String.format("<font color=%s>%s</font><BR>", color, word)));
        etWord.setText("");
    }
}