package ws.isak.audiocapturesas.ui;

import ws.isak.audiocapturesas.R;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;


/**
 * Created by isakherman on 7/19/16. .
 * test for a rec frag with only one button
 * solved placement of button creation and how to handle click behaviors
 *
 */
public class TestRecFrag extends Fragment implements View.OnClickListener{

    Button record;

    @Override
    public View onCreateView (LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_record_test, container, false);
        record = (Button) v.findViewById(R.id.recButton);
        record.setOnClickListener(this);
        return v;
    }

    @Override
    public void onClick (View v) {
        switch (v.getId()) {
            case R.id.recButton:
                Toast.makeText(getActivity(), "Record Button Pressed", Toast.LENGTH_LONG).show();
                break;
        }
    }
}