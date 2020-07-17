package com.dtech.tenakatainterview.HelperClass;

import android.text.TextUtils;
import android.widget.EditText;

public class EditTextGetText {

    public String getText(EditText editText){

        String txt = editText.getText().toString();
        if (!TextUtils.isEmpty(txt)){
            return txt;
        }else {
            editText.setError("The field cannot be empty.");
            return null;
        }

    }


}
