package android.app.printerapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

/**
 * Created by Shireenyu on 2017-11-05.
 */

public class add_details extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add_details_activity);

        // setupCancelButton();
    }

    private void setupCancelButton() {
        View view = findViewById(R.id.Cancel);
        if(view != null && view instanceof Button) {
            Button addBotton = (Button) findViewById(R.id.Cancel);

            addBotton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clearFields();
                }
            });
        }
    }

    private void clearFields(){
        int ids[] = {R.id.editText12, R.id.editText13, R.id.editText14, R.id.editText18};
        for(int id : ids){
            clearEditTextField(getEditTextField(id));
        }
    }
    private EditText getEditTextField(int id) {
        View view = findViewById(id);
        if(view instanceof EditText) {
           return (EditText) view;
        }
        return null;
    }

    private void clearEditTextField(EditText editText) {
        if(editText != null) {
            editText.setText("");
            editText.getEditableText().clear();
            editText.getText().clear();
        }
    }
}
