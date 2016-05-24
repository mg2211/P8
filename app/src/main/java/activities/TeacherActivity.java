package activities;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;

import helper.*;

import com.example.svilen.p8.R;

import java.util.ArrayList;

public class TeacherActivity extends AppCompatActivity {

    private final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher);

        setButtons();

        Button bClasses = (Button) findViewById(R.id.bClasses);
        Button bAssignments = (Button) findViewById(R.id.bAssignments);
        Button bTexts = (Button) findViewById(R.id.bTexts);
        Button bUsers = (Button) findViewById(R.id.bUsers);
        Button bLogOut = (Button) findViewById(R.id.bLogOut);

        bClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ClassActivity.class);
                startActivity(intent);
            }
        });

        bAssignments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(TeacherActivity.this, AssignmentActivity.class);
                startActivity(intent);

            }
        });

        bTexts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, TextActivity.class);
                startActivity(intent);
            }
        });

        bUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, UserActivity.class);
                startActivity(intent);
                //new UserTask().execute("");
            }
        });


        bLogOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new UserInfo(context).logOut();
            }
        });

    }

    /*Method for setting the buttons to fill the entire height of the screen
    * equally sized*/
    private void setButtons(){
        ArrayList<Integer> buttonIds = new ArrayList<>();
        buttonIds.add(R.id.bClasses);
        buttonIds.add(R.id.bAssignments);
        buttonIds.add(R.id.bLogOut);
        buttonIds.add(R.id.bUsers);
        buttonIds.add(R.id.bTexts);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int height = size.y;

        for(int i = 0; i < buttonIds.size(); i++){
            Button button = (Button) findViewById(buttonIds.get(i));
            int buttonHeight = height/buttonIds.size();
            button.setHeight(buttonHeight);
        }
    }
    @Override
    public void onBackPressed() {
        Log.d("Back button pressed", " -Disabled");
    }

}