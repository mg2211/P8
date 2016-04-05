package com.example.svilen.p8;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.lang3.StringUtils;

public class AddTextActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_text);

        Button addText = (Button) findViewById(R.id.addText);
        Button calc = (Button) findViewById(R.id.calc);
        final EditText text = (EditText) findViewById(R.id.text);
        final TextView complexity = (TextView) findViewById(R.id.complexity);

        calc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("called", "yes");

                String inputText = text.getText().toString();
                String cleanText;

                int P = StringUtils.countMatches(inputText, ".");
                P = P+StringUtils.countMatches(inputText, "?");
                P = P+StringUtils.countMatches(inputText, "!");
                P = P+StringUtils.countMatches(inputText, ":");
                P = P+StringUtils.countMatches(inputText, ";");

                cleanText = inputText.replaceAll("/./", "");
                cleanText.replaceAll("/?/", "");
                cleanText.replaceAll("/!/","");
                cleanText.replaceAll("/:/","");
                cleanText.replaceAll("/;/","");

                String[] words = cleanText.split("\\s+");
                int O = words.length;
                if(O > 0 && P > 0) {
                    int L = 0;
                    for (int i = 0; i < words.length; i++) {
                        if (words[i].length() > 6) {
                            L++;
                        }
                    }


                    Log.d("O", String.valueOf(O));
                    Log.d("P", String.valueOf(P));
                    Log.d("L", String.valueOf(L));
                    double lix = (O / P) + (L * 100 / O);
                    //complexity.setText(String.valueOf(lix));
                    Log.d("lix: ",String.valueOf(lix));
                } else {
                    int duration = Toast.LENGTH_LONG;
                    CharSequence alert = "At least one punctuation mark and one word is needed for the calculation";
                    Toast toast = Toast.makeText(getApplicationContext(), alert, duration);
                    toast.show();
                }
            }
        });

        addText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });


    }
}
