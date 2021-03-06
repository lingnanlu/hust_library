package io.github.lingnanlu.hustlibrary.Views;

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
import io.github.lingnanlu.hustlibrary.R;

public class MainActivity
        extends AppCompatActivity
        implements AdapterView.OnItemSelectedListener, View.OnClickListener{

    private static final String TAG = "MainActivity";

    @Bind(R.id.searchBox)
    EditText mSearchBox;

    @Bind(R.id.searchImageButton)
    ImageButton mSearchImageButton;

    @Bind(R.id.catagorySpinner)
    Spinner mCatagorySelector;

//    @Bind(R.id.myToolbar)
//    Toolbar mToolbar;

    public static final String DATA_KEYWORD = "io.github.lingnanlu" +
            ".hustlibrary.keyword";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

//        setSupportActionBar(mToolbar);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter
                .createFromResource(this, R.array.category, android
                        .R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout
                .simple_spinner_dropdown_item);
        mCatagorySelector.setAdapter(adapter);
        mCatagorySelector.setOnItemSelectedListener(this);

        mSearchImageButton.setOnClickListener(this);

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view,
                               int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {

        Intent intent = new Intent(this,ItemListActivity.class);

        intent.putExtra(DATA_KEYWORD, mSearchBox.getText().toString());

        startActivity(intent);

    }
}
