package activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.svilen.p8.R;

import callback.*;
import helper.*;
import serverRequests.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ClassActivity extends AppCompatActivity {

    /** context */
    private final Context context = this;

    /** Button used to create a class */
    private Button bCreateClass;

    /** Button used to create a class */
    private Button bEditClass;

    /** Button used to delete a class */
    private Button bDeleteClass;

    /** ListView containing classes */
    private ListView lvListClasses;

    /** ListView containing students */
    private ListView lvListStudents;

    /** ListView containing teachers */
    private ListView lvListTeachers;

    /** TextView that displays the title above lsListStudents */
    private TextView tvTitleListStudents;

    /** TextView that displays the title above the class editing block */
    private TextView tvTitleCRUDClass;

    /** TextView that displays the teacher's name */
    private TextView tvTeacherName;

    /** TextView that displays the teacher's e-mail address */
    private TextView tvTeacherEmail;

    /** EditText that displays the class name - can be edited */
    private EditText etClassName;

    /** Adapter for displaying classes in the classList ListView */
    private SimpleAdapter classListAdapter;

    /** Adapter for displaying students in the studentList ListView */
    private SimpleAdapter studentListAdapter;

    /** Adapter for displaying teachers in the teacherList ListView */
    private SimpleAdapter teacherListAdapter;

    /** ArrayList for storing classes - used by classListAdapter */
    private final List<Map<String, String>> classList = new ArrayList<>();

    /** ArrayList for storing students - used by studentListAdapter */
    private final List<Map<String, String>> studentList = new ArrayList<>();

    /** ArrayList for storing teachers - used by teacherListAdapter */
    private final List<Map<String, String>> teacherList = new ArrayList<>();

    /** String for storing the username of the user currently logged in */
    private String currentUserTeacherId;

    /** String for storing the frist name of the user currently logged in */
    private String currentUserFirstName;

    /**
     * String for storing the teacher id for the teacher that teaches the currently
     * selected class or, if selected, the teacher id belonging to the teacher clicked
     * from lvListTeachers
     */
    private String teacherTeacherId;

    /**
     * String for storing the name for the teacher that teaches the currently
     * selected class or, if selected, the name belonging to the teacher clicked
     * from lvListTeachers
     */
    private String teacherTeacherFullName;

    /**
     * String for storing the E-mail address for the teacher teaching the class last clicked
     * in lvListClasses
     */
    private String currentTeacherEmail;

    /** String for storing the class id for the class last clicked in lvListClasses */
    private String classClassId;

    /** String for storing the class name for the class last clicked in lvListClasses */
    private String classClassName;

    /** boolean for setting whether a new class is being created */
    private boolean newClass;

    /** boolean for setting whether changes have been made that might need to be saved */
    private boolean changed;

    /** boolean for setting whether the class name has been changed */
    private boolean nameChanged;

    /** boolean for setting whether the teacher details for a classs been changed */
    private boolean teacherChanged;

    /** boolean for setting whether fields are clear */
    private boolean clear;

    /**
     * boolean for storing whether the last classList was generated for all teachers as opposed to one
     * currently logged in
     */
    private boolean teacherClasses;

    /** int for storing the position of the item last clicked in lvListClasses */
    private int classListPosition;

    /** int for storing the position of the item last clicked in lvListTeachers */
    private int teacherListPosition;

    @Override
    /**
     * onCreate sets up the ui elements, populates them and describes what to do on click
     *
     * @param savedInstanceState
     */
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class);

        tvTitleListStudents = (TextView) findViewById(R.id.tvTitleListStudents);
        tvTitleCRUDClass = (TextView) findViewById(R.id.tvTitleCRUDClass);
        tvTeacherName = (TextView) findViewById(R.id.tvTeacherName);
        tvTeacherEmail = (TextView) findViewById(R.id.tvDialogTeacherEmail);

        /** EditText for searching a class */
        EditText etSearch = (EditText) findViewById(R.id.etSearch);
        /** EditText for searching a teacher */
        EditText etSearchTeacher = (EditText) findViewById(R.id.etSearchTeacher);
        etClassName = (EditText) findViewById(R.id.etClassName);

        lvListClasses = (ListView) findViewById(R.id.lvListClasses);
        lvListStudents = (ListView) findViewById(R.id.lvListStudents);
        lvListTeachers = (ListView) findViewById(R.id.lvListTeachers);

        /** Button used to start the process of creating a new user */
        Button bAddClass = (Button) findViewById(R.id.bAddClass);
        /** Button used to fetch all classes for the teacher currently logged in */
        Button bShowTeacherClasses = (Button) findViewById(R.id.bShowTeacherClasses);
        /** Button used to fetch all classes */
        Button bShowAllClasses = (Button) findViewById(R.id.bShowAllClasses);
        bCreateClass = (Button) findViewById(R.id.bCreateClass);
        bEditClass = (Button) findViewById(R.id.bEditClass);
        bDeleteClass = (Button) findViewById(R.id.bDeleteClass);

        /** create a new UserInfo to get information on the user currently logged in */
        UserInfo userinfo = new UserInfo(context);
        HashMap<String, String> user = userinfo.getUser();
        currentUserTeacherId = user.get("teacherId");
        currentUserFirstName = user.get("username");
        getTeacherClasses();
        setTeacherClasses();

        /**
         * create an adapter that will contain classes - displays their name,
         * teacher name and the number of students in each class
         */
        classListAdapter = new SimpleAdapter(this, classList,
                R.layout.listview_custom_item,
                new String[]{"className", "teacherFirstName", "teacherLastName", "NumOfStudents"},
                new int[]{R.id.clTvRow1, R.id.clTvRow2_1, R.id.clTvRow2_2, R.id.clTvRow3});
        /** assigns the classListAdapter to lvListClasses */
        lvListClasses.setAdapter(classListAdapter);
        /** set adapter to single choice mode */
        lvListClasses.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        /** create an adapter that will contain students - displays their full name */
        studentListAdapter = new SimpleAdapter(this, studentList,
                android.R.layout.simple_list_item_1,
                new String[]{"fullName"},
                new int[]{android.R.id.text1});
        /** assigns the studentListAdapter to lvListStudents */
        lvListStudents.setAdapter(studentListAdapter);
        /** set adapter to single choice mode */
        lvListStudents.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        /**
         * set the positions for both lists to -1 to start with new user creation ui elements
         * same for booleans
         */
        classListPosition = -1;
        teacherListPosition = -1;
        setNewClass(true);
        setChanged(false);
        getClassStudents(currentUserTeacherId, "");
        tvTitleListStudents.setText("All students for " + currentUserFirstName);

        /** create an adapter that will contain teachers - displays their full name */
        teacherListAdapter = new SimpleAdapter(this, teacherList,
                android.R.layout.simple_list_item_1,
                new String[]{"fullName"},
                new int[]{android.R.id.text1});
        /** assigns the teacherListAdapter to lvListTeachers */
        lvListTeachers.setAdapter(teacherListAdapter);
        /** set adapter to single choice mode */
        lvListTeachers.setChoiceMode(AbsListView.CHOICE_MODE_SINGLE);

        /** set button to display the user's first name */
        bShowTeacherClasses.setText("Show " + currentUserFirstName + "'s classes");

        /** make sure to start with all teachers in lvListTeachers */
        getAllTeachers();

        /** set addTextChangedListener to etSearch to allow for searching through lvListClasses */
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //TODO AUTO GENERATED METHOD
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                classListAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO AUTO GENERATED METHOD
            }
        });

        /** set addTextChangedListener to etSearch to allow for searching through lvListTeachers */
        etSearchTeacher.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //TODO AUTO GENERATED METHOD
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                teacherListAdapter.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {
                //TODO AUTO GENERATED METHOD
            }
        });

        /**
         * set OnItemSelectedListener to lvListClasses
         * define behaviour if class has been edited since last click
         */
        lvListClasses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                classListPosition = position;
                teacherListPosition = -1;
                if (changed) {
                    /** if changes have been made since last save */
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            if (dialogResponse) {
                                /** if user indicates changes must be saved */
                                if (newClass) {
                                    if (createClass(teacherListPosition)) {
                                        /** if saving using the creation a new class */
                                        clear = true;
                                        setChanged(false);
                                        setNewClass(false);
                                        /** reset list positions to zero */
                                        resetAdapter(lvListTeachers, teacherListAdapter);
                                        resetAdapter(lvListStudents, studentListAdapter);
                                        setContentPane(position, teacherListPosition);
                                        /** populate student list */
                                        getClassStudents("", classClassId);
                                        tvTitleListStudents.setText("students in class " + classClassName);
                                        Log.d("position", String.valueOf(position));
                                    } else {
                                        clear = false;
                                        /** reset list positions to zero */
                                        resetAdapter(lvListTeachers, teacherListAdapter);
                                        resetAdapter(lvListStudents, studentListAdapter);
                                        setContentPane(position, teacherListPosition);
                                        /** populate student list */
                                        getClassStudents("", classClassId);
                                        tvTitleListStudents.setText("students in class " + classClassName);
                                        Log.d("position", String.valueOf(position));
                                    }
                                }
                                if (changed) {
                                    if (updateClass()) {
                                        /** if saving using the updating of an existing user */
                                        clear = true;
                                        setChanged(false);
                                        setNewClass(false);
                                        tvTitleListStudents.setText("students in class " + classClassName);
                                        /** reset list positions to zero */
                                        resetAdapter(lvListTeachers, teacherListAdapter);
                                        resetAdapter(lvListStudents, studentListAdapter);
                                        setContentPane(position, teacherListPosition);
                                    } else {
                                        clear = false;
                                        /** reset list positions to zero */
                                        resetAdapter(lvListTeachers, teacherListAdapter);
                                        resetAdapter(lvListStudents, studentListAdapter);
                                        setContentPane(position, teacherListPosition);
                                        getClassStudents("", classClassId);
                                        tvTitleListStudents.setText("students in class " + classClassName);
                                        Log.d("position", String.valueOf(position));
                                    }
                                }
                            }
                            if (clear) {
                                /** if saving using the updating of an existing user */
                                resetAdapter(lvListTeachers, teacherListAdapter);
                                resetAdapter(lvListStudents, studentListAdapter);
                                setContentPane(position, teacherListPosition);
                                getClassStudents("", classClassId);
                                tvTitleListStudents.setText("students in class " + classClassName);
                                Log.d("position", String.valueOf(position));
                            }
                        }
                    });
                } else {
                    /** if user indicates changes must be discarded */
                    resetAdapter(lvListTeachers, teacherListAdapter);
                    resetAdapter(lvListStudents, studentListAdapter);
                    setContentPane(position, teacherListPosition);
                    getClassStudents("", classClassId);
                    tvTitleListStudents.setText("students in class " + classClassName);
                    Log.d("position", String.valueOf(position));
                    clear = true;
                }
            }
        });

        /**
         * set OnItemSelectedListener to lvListTeachers
         * set newClass -> true if no item has been selected in lvListClasses
         */
        lvListTeachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(classListPosition < 0) {
                    setNewClass(true);
                }
                setContentPane(classListPosition, position);
            }
        });

        /**
         * set OnItemSelectedListener to bAddClass
         * define behaviour if editTexts have been edited since last click
         * behaviour is the same as above but always sets new class after finishing
         */
        bAddClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classListPosition = -1;
                teacherListPosition = -1;
                if (changed) {
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            if (dialogResponse) {
                                if (newClass) {
                                    if (createClass(teacherListPosition)) {
                                        clear = true;
                                        setChanged(false);
                                        setNewClass(true);
                                        setContentPane(classListPosition, teacherListPosition);
                                        Log.d("teacherTeacherName", teacherTeacherFullName);

                                        resetAdapter(lvListClasses, classListAdapter);
                                        resetAdapter(lvListTeachers, teacherListAdapter);
                                    } else {
                                        clear = false;
                                        setNewClass(true);
                                        setContentPane(classListPosition, teacherListPosition);
                                        resetAdapter(lvListClasses, classListAdapter);
                                        resetAdapter(lvListTeachers, teacherListAdapter);
                                    }
                                }
                            } else {
                                clear = true;
                                setNewClass(true);
                                setContentPane(classListPosition, teacherListPosition);
                                resetAdapter(lvListClasses, classListAdapter);
                                resetAdapter(lvListTeachers, teacherListAdapter);
                            }
                        }
                    });
                } else {
                    setNewClass(true);
                    setContentPane(classListPosition, teacherListPosition);
                    resetAdapter(lvListClasses, classListAdapter);
                    resetAdapter(lvListTeachers, teacherListAdapter);
                }
            }
        });

        /**
         * set OnItemSelectedListener to bShowTeacherClasses
         * define behaviour if editTexts have been edited since last click
         * behaviour is the same as above
         */
        bShowTeacherClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classListPosition = -1;
                teacherListPosition = -1;
                setTeacherClasses();
                if (changed) {
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            if (dialogResponse) {
                                if (newClass) {
                                    if (createClass(teacherListPosition)) {
                                        clear = true;
                                        setChanged(false);
                                        setNewClass(true);
                                        getTeacherClasses();
                                        getClassStudents(currentUserTeacherId,"");
                                        resetAdapter(lvListClasses, classListAdapter);
                                        setContentPane(classListPosition, teacherListPosition);
                                        tvTitleListStudents.setText("All students for " + currentUserFirstName);
                                    } else {
                                        clear = false;
                                        setNewClass(true);
                                        getTeacherClasses();
                                        getClassStudents(currentUserTeacherId,"");
                                        resetAdapter(lvListClasses, classListAdapter);
                                        setContentPane(classListPosition, teacherListPosition);
                                        tvTitleListStudents.setText("All students for " + currentUserFirstName);
                                    }
                                }
                            } else {
                                clear = true;
                                setNewClass(true);
                                getTeacherClasses();
                                getClassStudents(currentUserTeacherId,"");
                                resetAdapter(lvListClasses, classListAdapter);
                                setContentPane(classListPosition, teacherListPosition);
                                tvTitleListStudents.setText("All students for " + currentUserFirstName);
                            }
                        }
                    });
                } else {
                    setNewClass(true);
                    getTeacherClasses();
                    getClassStudents(currentUserTeacherId,"");
                    resetAdapter(lvListClasses, classListAdapter);
                    setContentPane(classListPosition, teacherListPosition);
                    tvTitleListStudents.setText("All students for " + currentUserFirstName);
                }
            }
        });

        /**
         * set OnItemSelectedListener to bShowAllClasses
         * define behaviour if editTexts have been edited since last click
         * behaviour is the same as above
         */
        bShowAllClasses.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                classListPosition = -1;
                teacherListPosition = -1;
                if (changed) {
                    confirm(new DialogCallback() {
                        @Override
                        public void dialogResponse(boolean dialogResponse) {
                            if (dialogResponse) {
                                clear = true;
                                setChanged(false);
                                setNewClass(true);
                                getAllClasses();
                                getClassStudents("","");
                                resetAdapter(lvListClasses, classListAdapter);
                                setContentPane(classListPosition, teacherListPosition);
                                tvTitleListStudents.setText("Students for all classes");
                            } else {
                                setNewClass(true);
                                getAllClasses();
                                getClassStudents("","");
                                resetAdapter(lvListClasses, classListAdapter);
                                setContentPane(classListPosition, teacherListPosition);
                                tvTitleListStudents.setText("Students for all classes");
                            }
                        }
                    });
                } else {
                    setNewClass(true);
                    getAllClasses();
                    getClassStudents("","");
                    resetAdapter(lvListClasses, classListAdapter);
                    setContentPane(classListPosition, teacherListPosition);
                    tvTitleListStudents.setText("Students for all classes");
                }
            }
        });

        /** call createClass() on click, set changed to false, newClass to true */
        bCreateClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createClass(teacherListPosition);
                setChanged(false);
                setNewClass(true);
            }
        });

        /** call updateClass() on click, set changed to false, newClass to true */
        bEditClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateClass();
                setChanged(false);
                setNewClass(false);
            }
        });

        /** call deleteClass() on click, set changed to false, newClass to true */
        bDeleteClass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteClass();
                setChanged(false);
                setNewClass(true);
            }
        });

        /**
         * set OnItemSelectedListener to lvListTeachers
         * set teacherListPosition to match the position of the clicked item
         * and set the content pane accordingly
         */
        lvListTeachers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                lvListTeachers.setItemChecked(position, true);
                teacherListPosition = position;
                view.setSelected(true);
                Log.d("position", String.valueOf(position));
                setContentPane(classListPosition, position);
            }
        });

        /** set addTextChangedListener to etClassName for keeping track of changes sets nameChanged -> true if so */
        etClassName.addTextChangedListener(new TextWatcher() {

            /** required empty method */
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            /** checks if username entry has been changed but is not empty and is not the same as the currently stored username for this entry */
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = etClassName.getText().toString();
                if (!content.equals(classClassName) && !content.isEmpty()) {
                    setNameChanged(true);
                    checkChanged();
                    Log.d("etClassName changed", String.valueOf(changed));
                } else {
                    setNameChanged(false);
                    checkChanged();
                    Log.d("etClassName changed", String.valueOf(changed));
                }
            }

            /** required empty method */
            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });

        /** set addTextChangedListener to etClassName for keeping track of changes sets teacherChanged -> true if so */
        tvTeacherEmail.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                //Auto generated stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String content = tvTeacherEmail.getText().toString();
                Log.d("String teacherEmail", content);
                if (!content.equals(currentTeacherEmail) && !content.isEmpty()) {
                    setTeacherChanged(true);
                    checkChanged();
                    Log.d("tvTeacherMail changed", String.valueOf(changed));
                    Log.d("tvTeacherMail value", content);
                } else {
                    setTeacherChanged(false);
                    checkChanged();
                    Log.d("tvTeacherMail changed", String.valueOf(changed));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                //auto generated stub
            }
        });
    }

    /**
     * fills the class EditTexts and TextViews based on the entry selected in lvListClasses and lvListTeachers
     * empties the EditTexts and TextViews if no entry is selected
     *
     * @param classListPos the position of the selected item in lvListClasses
     * @param teacherListPos the position of the selected item in lvListTeachers
     */
    private void setContentPane(int classListPos, int teacherListPos) {

        /**
         * String for storing the e-mail address for the teacher that teaches the currently
         * selected class or, if selected, the e-mail address id belonging to the teacher clicked
         * from lvListTeachers
         */
        String teacherTeacherEmail;

        if (classListPos >= 0 && teacherListPos >= 0) {
            /**
             * if an entry has been selected from lvListClasses and lvListTeachers
             * set etClassName to take information from classListAdapter
             * set currentTeacherEmail from classListAdapter to allow checking for changes
             * and tvTeacherName and tvTeacherEmail from teacherListAdapter
             */

            Map<String, String> classData = (Map) classListAdapter.getItem(classListPos);
            currentTeacherEmail = classData.get("teacherEmail");

            classClassId = classData.get("classId");
            classClassName = classData.get("className");

            Map<String, String> teacherData = (Map) teacherListAdapter.getItem(teacherListPos);
            teacherTeacherId = teacherData.get("teacherId");
            teacherTeacherFullName = teacherData.get("fullName");
            teacherTeacherEmail = teacherData.get("email");

            etClassName.setText(classClassName);
            tvTeacherName.setText(teacherTeacherFullName);
            tvTeacherEmail.setText(teacherTeacherEmail);

            setNewClass(false);

        } else if (classListPos >= 0) {
            /**
             * if an entry has been selected from lvListClasses
             * set etClassName,  tvTeacherName and tvTeacherEmail to take information from classListAdapter
             * set currentTeacherEmail from classListAdapter to allow checking for changes
             */

            Map<String, String> classData = (Map) classListAdapter.getItem(classListPos);
            currentTeacherEmail = classData.get("teacherEmail");

            classClassId = classData.get("classId");
            classClassName = classData.get("className");

            teacherTeacherId = classData.get("teacherId");
            teacherTeacherFullName = (classData.get("teacherFirstName") + " " + classData.get("teacherLastName"));
            teacherTeacherEmail = classData.get("teacherEmail");

            etClassName.setText(classClassName);
            tvTeacherName.setText(teacherTeacherFullName);
            tvTeacherEmail.setText(teacherTeacherEmail);

            setNewClass(false);

        } else if (teacherListPos >= 0) {
            /**
             * if an entry has been selected from lvListTeachers
             * set tvTeacherName and tvTeacherEmail to take information from teacherListAdapter
             */
            Map<String, String> userData = (Map) teacherListAdapter.getItem(teacherListPos);

            if(etClassName.getText().equals(classClassName)) {
                /**
                 * if class name matches last selection through lvListClasses set null
                 * leave it otherwise, it's user input
                 */
                classClassName = null;
                etClassName.setText("");
            }

            teacherTeacherId = userData.get("teacherId");
            teacherTeacherFullName = userData.get("fullName");
            teacherTeacherEmail = userData.get("email");

            tvTeacherName.setText(teacherTeacherFullName);
            tvTeacherEmail.setText(teacherTeacherEmail);
        } else {
            /**
             * set all to null/empty if nothing's selected from both lists
             */
            classClassName = null;

            teacherTeacherId = null;
            teacherTeacherFullName = null;

            currentTeacherEmail = null;

            etClassName.setText("");
            tvTeacherName.setText("");
            tvTeacherEmail.setText("");
        }
    }

    /**
     * create a new class based on the content of etClassName and tvTeacherName and tvTeacherEmail
     *
     * @param teacherListPos the position of the teacher selected to teach the class from lvListTeachers
     * @return true if a new class has been created, else false
     */
    private boolean createClass(int teacherListPos) {
        if(teacherListPos >= 0 && ! etClassName.getText().toString().isEmpty()) {
            Log.d("teacherListPos", String.valueOf(teacherListPosition));
            Map<String, String> teacherData = (Map) teacherListAdapter.getItem(teacherListPos);
            String teacherId = teacherData.get("teacherId");
            String className = etClassName.getText().toString();
            if (!teacherId.equals("") && !className.equals("")) {
                /** checks if no fields are empty */
                new ClassTask(new Callback() {
                    @Override
                    public void asyncDone(HashMap<String, HashMap<String, String>> classes) {
                        /** start a new ClassTask to create a new class - get the id of the newly created class */
                        for (Map.Entry<String, HashMap<String, String>> classData : classes.entrySet()) {
                            classClassId = classData.getValue().get("lastClassId");
                            Log.d("lastClassId value", classData.getValue().get("lastClassId"));
                        }
                        classListAdapter.notifyDataSetChanged();
                        Log.d("dialogClassListAdapter", "successfully updated");
                    }
                }, context).executeTask("CREATE", "", teacherId, className);
                /** get classes and reset adapter to reset selection to zero */
                if(teacherClasses){
                    getTeacherClasses();
                } else {
                    getAllClasses();
                }
                resetAdapter(lvListClasses, classListAdapter);
                /** boolean used by a.o. lvListClasses onItemClickListener */
                return true;
            }
        } else {
            int duration = Toast.LENGTH_LONG;
            CharSequence alert = "Please fill all required fields";
            Toast toast = Toast.makeText(context, alert, duration);
            toast.show();
        }
        /** boolean used by a.o. lvListClasses onItemClickListener */
        return false;
    }

    /**
     * get the content of relevant EditTexts and update a class with this content
     *
     * @return true if the class has been updated
     */
    private boolean updateClass() {
       String className = etClassName.getText().toString();
        new ClassTask(new Callback() {
            /** start a new ClassTask to update the class using method UPDATE */
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> classes) {
                classListAdapter.notifyDataSetChanged();
                Log.d("classListAdapter", "successfully updated");
            }
        }, context).executeTask("UPDATE", classClassId, teacherTeacherId, className);
        classClassName = className;
        /** get all classes and reset adapter to reset selection to zero */
        if(teacherClasses){
            getTeacherClasses();
        } else {
            getAllClasses();
        }
        resetAdapter(lvListClasses, classListAdapter);
        return true;
    }

    /**
     * deletes a class from the database
     * throws a dialog alert for confirmation
     */
    private void deleteClass() {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("Are you sure you want to delete class " + classClassName + "?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        new ClassTask(new Callback() {
                            /** create a new ClassTask to delete the classs using method DELETE */
                            @Override
                            public void asyncDone(HashMap<String, HashMap<String, String>> classes) {
                            }
                        }, context).executeTask("DELETE", classClassId, "", "");
                        /** clear all EditTexts */
                        etClassName.setText("");
                        tvTeacherName.setText("");
                        tvTeacherEmail.setText("");
                        Log.d("classId", classClassId);
                        Log.d("classname", classClassName);
                        setNewClass(true);
                        setChanged(false);
                        bDeleteClass.setEnabled(false);
                        classClassName = null;
                        classClassId = null;
                    }
                })
                .setNegativeButton("No", null)
                .show();
        /** get all users and reset adapter to reset selection to zero */
        if(teacherClasses){
            getTeacherClasses();
        } else {
            getAllClasses();
        }
        resetAdapter(lvListClasses, classListAdapter);
    }

    /**
     * launch a ClassTask to fetch all classes for a the teacher currently logged in from the database and add them to classList
     */
    private void getTeacherClasses() {
        new ClassTask(new Callback() {
            /** execute a new ClassTask with method FETCH, takes the id from the logged in user as an argument  */
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> classes) {
                if (!classList.isEmpty()) {
                    classList.clear();
                }
                for (Map.Entry<String, HashMap<String, String>> classData : classes.entrySet()) {
                    /** get data from the returned HashMap and add it to ArrayList classList that can be used by classListAdapter */
                    Map<String, String> classInfo = new HashMap<>();
                    String classId = classData.getValue().get("classId");
                    String teacherId = classData.getValue().get("teacherId");
                    String className = classData.getValue().get("className");
                    String teacherFirstName = classData.getValue().get("teacherFirstName");
                    String teacherLastName = classData.getValue().get("teacherLastName");
                    String teacherEmail = classData.getValue().get("teacherEmail");
                    String numOfStudents = classData.getValue().get("numOfStudents");
                    classInfo.put("classId", classId);
                    classInfo.put("teacherId", teacherId);
                    classInfo.put("className", className);
                    classInfo.put("teacherFirstName", teacherFirstName);
                    classInfo.put("teacherLastName", teacherLastName);
                    classInfo.put("teacherFullName", teacherFirstName + " " + teacherLastName);
                    classInfo.put("teacherEmail", teacherEmail);
                    classInfo.put("NumOfStudents", "Number of students: "+ numOfStudents);
                    Log.d("getAllClasses result", String.valueOf(classInfo));
                    classList.add(classInfo);
                }
                classListAdapter.notifyDataSetChanged();
                Log.d("classList entries", String.valueOf(classList));
                Log.d("classListAdapter", "successfully updated");
            }
        }, context).executeTask("FETCH", "", currentUserTeacherId, "");
    }

    /**
     * launch a ClassTask to fetch all classes from the database and add them to classList
     *
     * procedure same as above
     */
    private void getAllClasses() {
        new ClassTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> classes) {
                if (!classList.isEmpty()) {
                    classList.clear();
                }
                classListAdapter.notifyDataSetChanged();
                for (Map.Entry<String, HashMap<String, String>> classData : classes.entrySet()) {
                    Map<String, String> classInfo = new HashMap<>();
                    String classId = classData.getValue().get("classId");
                    String teacherId = classData.getValue().get("teacherId");
                    String className = classData.getValue().get("className");
                    String teacherFirstName = classData.getValue().get("teacherFirstName");
                    String teacherLastName = classData.getValue().get("teacherLastName");
                    String teacherEmail = classData.getValue().get("teacherEmail");
                    String numOfStudents = classData.getValue().get("numOfStudents");
                    classInfo.put("classId", classId);
                    classInfo.put("teacherId", teacherId);
                    classInfo.put("className", className);
                    classInfo.put("teacherFirstName", teacherFirstName);
                    classInfo.put("teacherLastName", teacherLastName);
                    classInfo.put("teacherEmail", teacherEmail);
                    classInfo.put("NumOfStudents", "Number of students: "+ numOfStudents);
                    Log.d("getAllClasses result", String.valueOf(classInfo));
                    classList.add(classInfo);
                }
                classListAdapter.notifyDataSetChanged();
                Log.d("dialogClassListAdapter", "successfully updated");
            }
        }, context).executeTask("FETCH", "", "", "");
    }

    /**
     * sets the UI items to be enabled based on whether a new user is being created and
     * user information has been changed
     */
    private void setEnabledUiItems() {
        if (newClass) {
            /** if a new user is being created */
            tvTitleCRUDClass.setText(R.string.createClass);
            bCreateClass.setVisibility(View.VISIBLE);
            bEditClass.setVisibility(View.GONE);
            bDeleteClass.setVisibility(View.GONE);
            if (changed) {
                /** enable register button if changes have been made else disable */
                bEditClass.setEnabled(false);
                bDeleteClass.setEnabled(false);
                bCreateClass.setEnabled(true);
            } else {
                bEditClass.setEnabled(false);
                bDeleteClass.setEnabled(false);
                bCreateClass.setEnabled(false);
            }
        } else {
            /** else a user is being updated */
            tvTitleCRUDClass.setText("Edit class " + classClassName);
            bEditClass.setVisibility(View.VISIBLE);
            bDeleteClass.setVisibility(View.VISIBLE);
            bCreateClass.setVisibility(View.GONE);
            if (changed) {
                /** enable save button if changes have been made else disable */
                bEditClass.setEnabled(true);
                bDeleteClass.setEnabled(true);
                bCreateClass.setEnabled(false);
            } else {
                bEditClass.setEnabled(false);
                bDeleteClass.setEnabled(true);
                bCreateClass.setEnabled(false);
            }
        }
    }

    /**
     * sets whether a new class is being created and adapts the UI elements to that
     *
     * @param value the value to set the boolean to
     */
    private void setNewClass(boolean value) {
        newClass = value;
        setEnabledUiItems();
        Log.d("new class value", String.valueOf(newClass));
    }

    /**
     * sets whether a class' details have been changed since last button click and adapts the UI elements to that
     *
     * @param value the value to set the boolean to
     */
    private void setChanged(boolean value) {
        changed = value;
        setEnabledUiItems();
        Log.d("setChanged changed", String.valueOf(changed));
    }

    /**
     * sets whether the name of a class has been changed since last button click and adapts the UI elements to that
     *
     * @param value the value to set the boolean to
     */
    private void setNameChanged(boolean value) {
        nameChanged = value;
        setEnabledUiItems();
        Log.d("Changed name value", String.valueOf(changed));
    }

    /**
     * sets whether the teacher assigned to a class has been changed since last button click and adapts the UI elements to that
     *
     * @param value the value to set the boolean to
     */
    private void setTeacherChanged(boolean value) {
        teacherChanged = value;
        setEnabledUiItems();
        Log.d("Changed teacher value", String.valueOf(changed));
    }

    private void setTeacherClasses(){
        teacherClasses = true;
    }

    /**
     * check if changes class name or teacher details require the changed boolean to be updated
     */
    private void checkChanged() {
        if(teacherChanged && nameChanged) {
            setChanged(true);
            Log.d("CheckChanged Changed", String.valueOf(changed));
        } else if(teacherChanged || nameChanged) {
            setChanged(true);
            Log.d("CheckChanged Changed", String.valueOf(changed));
        } else {
            setChanged(false);
            Log.d("CheckChanged Changed", String.valueOf(changed));
        }
    }

    /**
     * creates a dialog asking the user is sure about discarding unsaved changes
     *
     * @param callback the callback interface used
     */
    private void confirm(final DialogCallback callback) {
        new AlertDialog.Builder(context)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Confirm")
                .setMessage("You have unsaved changes - save before continuing?")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.dialogResponse(true);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        callback.dialogResponse(false);
                    }
                })
                .show();
    }

    /**
     * launch a StudentTask to fetch all students for a specific class (when executed with a specific teacher id or student id
     * or fetch all students enrolled in a class when called with empty parameters
     *
     * procedure same as other get-methods
     */
    private void getClassStudents(String teacherId, String classId){
        new StudentTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> asyncResults) {
                if (!studentList.isEmpty()) {
                    studentList.clear();
                }
                for (Map.Entry<String, HashMap<String, String>> student : asyncResults.entrySet()) {
                    Map<String, String> studentInfo = new HashMap<>();
                    String studentId = student.getValue().get("studentId");
                    String studentName = student.getValue().get("firstname") + " " + student.getValue().get("lastname");
                    studentInfo.put("studentId",studentId);
                    studentInfo.put("fullName", studentName);
                    studentList.add(studentInfo);
                }
                if (studentList.isEmpty()){
                    Map<String, String> userInfo = new HashMap<>();
                    userInfo.put("fullName", "no students in this class");
                    studentList.add(userInfo);
                }
                studentListAdapter.notifyDataSetChanged();
            }
        }, context).execute(classId, teacherId);
    }

    /**
     * launch a UserTask and get all teachers
     *
     * procedure same as other get-methods
     */
    private void getAllTeachers(){
        new UserTask(new Callback() {
            @Override
            public void asyncDone(HashMap<String, HashMap<String, String>> users) {
                if (!teacherList.isEmpty()) {
                    teacherList.clear();
                }
                for (Map.Entry<String, HashMap<String, String>> user : users.entrySet()) {
                    Map<String, String> userInfo = new HashMap<>();
                    String userId = user.getValue().get("userId");
                    String teacherId = user.getValue().get("teacherId");
                    String firstName = user.getValue().get("firstName");
                    String lastName = user.getValue().get("lastName");
                    String fullName = (firstName + " " + lastName);
                    String email = user.getValue().get("email");
                    userInfo.put("userId", userId);
                    userInfo.put("teacherId", teacherId);
                    userInfo.put("firstName", lastName);
                    userInfo.put("lastName", lastName);
                    userInfo.put("fullName", fullName);
                    userInfo.put("email", email);
                    Log.d("full teacher name", fullName);
                    teacherList.add(userInfo);
                }
                teacherListAdapter.notifyDataSetChanged();
            }
        }, context).execute("FETCH", "teacher", "", "", "", "", "", "", "", "", "", "");
    }

    /**
     * re-assign an adapter to a listview
     * hacky method but the only way to clear selections in the list after list data is updated
     *
     * @param lv the ListView to which a new adapter is to be assigned
     * @param sa the adapter that the ListView is to be assigned to
     */
    private void resetAdapter(ListView lv, SimpleAdapter sa) {
        lv.setChoiceMode(AbsListView.CHOICE_MODE_NONE);
        lv.setAdapter(sa);
        lv.requestLayout();
    }
}