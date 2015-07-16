package com.andrewsosa.bounce;

import android.app.Activity;

/**
 * Created by andrewsosa on 6/27/15.
 */
public abstract class BounceActivity extends Activity {

    /** Start of user-defined activity results. */
    public static final int RESULT_FIRST_USER   = 1;
    public static final int RESULT_DELETE_TASK  = 2;
    public static final int RESULT_MISSING_TASK = 3;

}
