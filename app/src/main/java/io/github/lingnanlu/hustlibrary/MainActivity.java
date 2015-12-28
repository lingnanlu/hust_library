package io.github.lingnanlu.hustlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements
        AdapterView.OnItemSelectedListener,
        View.OnClickListener{

    private static final String TAG = "MainActivity";
    public static final String EXTRA_KEYWORD = "io.github.lingnanlu.hustlibrary.keyword";

    @Bind(R.id.et_searchBox)
    EditText mSearchBox;

    @Bind(R.id.iBtn_searchButton)
    ImageButton mSearchImageButton;

    @Bind(R.id.spinner_searchBy)
    Spinner mCatagorySelector;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.category,
                android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCatagorySelector.setAdapter(adapter);
        mCatagorySelector.setOnItemSelectedListener(this);

        mSearchImageButton.setOnClickListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this,BookAbstractsActivity.class);
        intent.putExtra(EXTRA_KEYWORD, mSearchBox.getText().toString());
        startActivity(intent);

    }
}
