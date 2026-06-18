
package com.example.campusplacementtracker;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import androidx.annotation.Nullable;
import java.util.ArrayList;

public class Database extends SQLiteOpenHelper {

    public Database(@Nullable Context context) {
        super(context, "campusplacement", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Table 1: users - stores login + student profile details
        String usersTable = "create table users(username text primary key, email text, password text, " +
                "fullname text, rollno text, branch text, cgpa text)";
        db.execSQL(usersTable);

        // Table 2: applications - stores every job application a student makes
        String applicationsTable = "create table applications(username text, company text, role text, " +
                "package text, eligibility text, applieddate text, interviewdate text, interviewtime text, status text)";
        db.execSQL(applicationsTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS applications");
        onCreate(db);
    }

    // ---------- USER / LOGIN / REGISTER METHODS ----------

    public void register(String username, String email, String password) {
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("email", email);
        cv.put("password", password);
        cv.put("fullname", "");
        cv.put("rollno", "");
        cv.put("branch", "");
        cv.put("cgpa", "");
        SQLiteDatabase db = getWritableDatabase();
        db.insert("users", null, cv);
        db.close();
    }

    public int login(String username, String password) {
        int result = 0;
        String[] args = {username, password};
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from users where username=? and password=?", args);
        if (c.moveToFirst()) {
            result = 1;
        }
        c.close();
        db.close();
        return result;
    }

    public boolean usernameExists(String username) {
        boolean exists = false;
        String[] args = {username};
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from users where username=?", args);
        if (c.moveToFirst()) {
            exists = true;
        }
        c.close();
        db.close();
        return exists;
    }

    // ---------- STUDENT PROFILE METHODS ----------

    public void updateProfile(String username, String fullname, String rollno, String branch, String cgpa) {
        ContentValues cv = new ContentValues();
        cv.put("fullname", fullname);
        cv.put("rollno", rollno);
        cv.put("branch", branch);
        cv.put("cgpa", cgpa);
        SQLiteDatabase db = getWritableDatabase();
        db.update("users", cv, "username=?", new String[]{username});
        db.close();
    }

    // returns: [fullname, rollno, branch, cgpa, email]
    public String[] getProfile(String username) {
        String[] profile = {"", "", "", "", ""};
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select fullname, rollno, branch, cgpa, email from users where username=?",
                new String[]{username});
        if (c.moveToFirst()) {
            profile[0] = c.getString(0);
            profile[1] = c.getString(1);
            profile[2] = c.getString(2);
            profile[3] = c.getString(3);
            profile[4] = c.getString(4);
        }
        c.close();
        db.close();
        return profile;
    }

    // ---------- APPLICATION METHODS ----------

    public void addApplication(String username, String company, String role, String pkg, String eligibility,
                               String appliedDate, String interviewDate, String interviewTime, String status) {
        ContentValues cv = new ContentValues();
        cv.put("username", username);
        cv.put("company", company);
        cv.put("role", role);
        cv.put("package", pkg);
        cv.put("eligibility", eligibility);
        cv.put("applieddate", appliedDate);
        cv.put("interviewdate", interviewDate);
        cv.put("interviewtime", interviewTime);
        cv.put("status", status);
        SQLiteDatabase db = getWritableDatabase();
        db.insert("applications", null, cv);
        db.close();
    }

    public boolean alreadyApplied(String username, String company) {
        boolean exists = false;
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select * from applications where username=? and company=?",
                new String[]{username, company});
        if (c.moveToFirst()) {
            exists = true;
        }
        c.close();
        db.close();
        return exists;
    }

    // Returns each application as a single string, fields separated by "|"
    // Order: company|role|package|eligibility|applieddate|interviewdate|interviewtime|status|rowid
    public ArrayList<String> getApplications(String username) {
        ArrayList<String> list = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor c = db.rawQuery("select rowid, company, role, package, eligibility, applieddate, " +
                        "interviewdate, interviewtime, status from applications where username=?",
                new String[]{username});
        if (c.moveToFirst()) {
            do {
                String row = c.getString(1) + "|" + c.getString(2) + "|" + c.getString(3) + "|" +
                        c.getString(4) + "|" + c.getString(5) + "|" + c.getString(6) + "|" +
                        c.getString(7) + "|" + c.getString(8) + "|" + c.getString(0);
                list.add(row);
            } while (c.moveToNext());
        }
        c.close();
        db.close();
        return list;
    }

    public void updateStatus(String rowId, String newStatus) {
        ContentValues cv = new ContentValues();
        cv.put("status", newStatus);
        SQLiteDatabase db = getWritableDatabase();
        db.update("applications", cv, "rowid=?", new String[]{rowId});
        db.close();
    }
}