package com.example.assignment5.services;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.os.Parcelable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.Toast;

import com.example.assignment5.database.DataBaseHelper;
import com.example.assignment5.model.Student;
import com.example.assignment5.utilities.Constants;

import java.util.ArrayList;

public class BackgroundServices extends Service {
    private Student student;
    private DataBaseHelper dataBaseHelper;
    private Constants constants = new Constants();
    private ArrayList<Student> mArrayList = new ArrayList<>();

    public BackgroundServices() {

    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

    }
    //performing various database operations
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        boolean isSuccess = false;
        dataBaseHelper = new DataBaseHelper(getApplicationContext());
        if (intent.getStringExtra(constants.ACTION_KEY).equals(constants.ADD)) {
            String name = intent.getStringExtra(constants.NAME_KEY);
            String rollno = intent.getStringExtra(constants.ROLLNO_KEY);
            String cls = intent.getStringExtra(constants.CLASS_KEY);
            student = new Student(name, rollno, cls, constants.OLD_ROLL_NO);
            isSuccess = dataBaseHelper.insertData(student);
        } else if (intent.getStringExtra(constants.ACTION_KEY).equals(constants.EDIT)) {
            String name = intent.getStringExtra(constants.NAME_KEY);
            String rollno = intent.getStringExtra(constants.ROLLNO_KEY);
            String cls = intent.getStringExtra(constants.CLASS_KEY);
            String oldRollNumber = intent.getStringExtra(constants.OLDROLLNUMBER);
            student = new Student(name, rollno, cls, oldRollNumber);
            isSuccess = dataBaseHelper.updateData(student);
        } else if (intent.getStringExtra(constants.ACTION_KEY).equals(constants.DELETE)) {
            String rollno = intent.getStringExtra(constants.ROLLNO_KEY);
            isSuccess = dataBaseHelper.deleteData(rollno);
        }
        else if(intent.getStringExtra(constants.ACTION_KEY).equals(constants.READ_OPERATION))
        {
            mArrayList=dataBaseHelper.getListElements();
            isSuccess=true;
        }
        //sending broadcast
        intent.setAction(constants.BROADCAST_ACTION);
        String actionType = intent.getStringExtra(constants.ACTION_KEY);
        if (isSuccess) {
            intent.putExtra(constants.IS_SUCCESS,constants.TRUE);
            intent.putExtra(constants.ACTION_KEY,actionType);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
        } else if (isSuccess && actionType.equals(constants.READ_OPERATION)) {
            intent.putParcelableArrayListExtra(constants.ARRAY_LIST, mArrayList);
            intent.putExtra(constants.ACTION_KEY,actionType);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);

        }
        else if(!isSuccess)
        {
            intent.putExtra(constants.IS_SUCCESS,constants.FALSE);
            intent.putExtra(constants.ACTION_KEY,actionType);
            LocalBroadcastManager.getInstance(getBaseContext()).sendBroadcast(intent);
        }



        return START_NOT_STICKY;
    }
}
