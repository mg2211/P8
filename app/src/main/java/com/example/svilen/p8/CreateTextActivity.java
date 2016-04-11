package com.example.svilen.p8;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class CreateTextActivity extends AppCompatActivity {

    EditText etTextName;
    EditText etTextContent;
    Button bSave;
    Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_text);


        etTextName = (EditText) findViewById(R.id.etTextName);
        etTextContent = (EditText) findViewById(R.id.etTextContent);
        bSave = (Button) findViewById(R.id.bSave);

        bSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textName = etTextName.getText().toString();
                String textContent = etTextContent.getText().toString();

                if (!textName.equals("") && !textContent.equals("")){
                    new CreateTextTask(context).execute(textName, textContent);
                }else {
                    int duration = Toast.LENGTH_LONG;
                    CharSequence alert = "Please fill all required fields";
                    Toast toast = Toast.makeText(context, alert, duration);
                    toast.show();
                }

            }
        });


    }
}
