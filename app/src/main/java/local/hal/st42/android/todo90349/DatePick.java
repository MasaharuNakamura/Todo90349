package local.hal.st42.android.todo90349;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import java.util.Calendar;


public class DatePick extends DialogFragment implements
        DatePickerDialog.OnDateSetListener{

    @Override
    @NonNull
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle extras = getArguments();
        int year = (int)extras.getLong("year");
        int month = (int)extras.getLong("month");
        int day = (int)extras.getLong("day");
        final Calendar c = Calendar.getInstance();

        return new DatePickerDialog(getActivity(),
                (ToDoEditActivity)getActivity(),  year, month, day);
    }

    @Override
    public void onDateSet(android.widget.DatePicker view, int year,
                          int monthOfYear, int dayOfMonth) {
    }

}