package net.wintastic.tipcalc;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

public class MainActivity extends ActionBarActivity implements View.OnClickListener {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        submitButtonClickListener();
        tipMethodsButtonClickListener();

        ratingChangeListener();

        addBillAmountValidatorListener();

        NumberPicker np = (NumberPicker) findViewById(R.id.tip_picker);
        np.setMaxValue(50);
        np.setMinValue(0);
        np.setValue(15);
        np.setWrapSelectorWheel(false);
    }

    public void submitButtonClickListener(){
        Button btnSubmit = (Button)findViewById(R.id.btnSubmit);
        btnSubmit.setOnClickListener(this);
    }

    public void tipMethodsButtonClickListener(){
        Button btnManual = (Button)findViewById(R.id.buttonManual);
        Button btnSuggested = (Button)findViewById(R.id.buttonSuggested);
        btnManual.setOnClickListener(this);
        btnSuggested.setOnClickListener(this);
    }

    public void ratingChangeListener(){
        RatingBar ratingBar = (RatingBar)findViewById(R.id.ratingBar);
        final TextView suggestedResult = (TextView)findViewById(R.id.suggestedResult);
        final NumberPicker tipPicker = (NumberPicker)findViewById(R.id.tip_picker);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                int val = (int) (10 + 2 * rating);
                suggestedResult.setText(val + "%");
                tipPicker.setValue(val);
            }
        });
    }

    public void addBillAmountValidatorListener() {
        final EditText billAmount = (EditText)findViewById(R.id.billAmount);
        billAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String text = billAmount.getText().toString();
                int decimalPosition = text.indexOf(".");
                if (decimalPosition >= 0) {
                    if (text.substring(decimalPosition).length() > 3) {
                        billAmount.setText(text.substring(0, decimalPosition + 3));
                        billAmount.setSelection(decimalPosition + 3);
                    }
                }
            }
        });
    }

    public void setDefaultTip() {
        NumberPicker tipPicker = (NumberPicker)findViewById(R.id.tip_picker);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String c = sharedPref.getString("pref_tip", "15");
        int tip = Integer.parseInt(c);

        tipPicker.setValue(tip);
    }

    @Override
    public void onClick(View v) {
        EditText etxtBillAmount = (EditText) findViewById(R.id.billAmount);
        EditText etxtNumberOfPeople = (EditText) findViewById(R.id.numberPeople);

        String billAmount = etxtBillAmount.getText().toString();
        String numPeople  = etxtNumberOfPeople.getText().toString();
        int numPeopleInt = Integer.parseInt(numPeople);

        NumberPicker np = (NumberPicker) findViewById(R.id.tip_picker);

        float tipPercent = (float)np.getValue() / 100;


        LinearLayout ratingLayout=(LinearLayout)this.findViewById(R.id.ratingLayout);
        LinearLayout tipPercentLayout=(LinearLayout)this.findViewById(R.id.tipPercentLayout);
        switch(v.getId()) {

            case R.id.btnSubmit:
                TextView t = (TextView) findViewById(R.id.warning_txt);
                TextView t2 = (TextView) findViewById(R.id.warning_txt2);
                if (billAmount.isEmpty()) {
                    t.setText(R.string.billAmountWarning);
                    etxtBillAmount.setBackgroundColor(0xFFFFD3D0);
                    // TextView billAmt = (TextView) findViewById(R.id.txtBillAmount);
                    //  billAmt.setTextColor(0xFF00FF00);
                }
                if (numPeople.equals("0"))
                {   t2.setText(R.string.numPeopleWarning2);
                    etxtNumberOfPeople.setBackgroundColor(0xFFFFD3D0);
                }
                if (numPeople.isEmpty()) {
                    t2.setText(R.string.numPeopleWarning);
                    etxtNumberOfPeople.setBackgroundColor(0xFFFFD3D0);
                    //   TextView numPayees = (TextView) findViewById(R.id.txtNumPayees);
                    //  numPayees.setTextColor(0xFF00FF00);
                }
                
                if (!numPeople.isEmpty() && !numPeople.equals("0") && !billAmount.isEmpty()) {
                    etxtNumberOfPeople.setBackgroundColor(0x00000000);
                    etxtBillAmount.setBackgroundColor(0x00000000);
                    t.setText(""); t2.setText("");
                    b_submitClick(Float.parseFloat(billAmount), Integer.parseInt(numPeople), tipPercent, "$"); SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
                    String c = sharedPref.getString("pref_currency", "$"); //TODO: Add a global var for currency character
                }
                break;
            case R.id.buttonManual:
                ratingLayout.setVisibility(LinearLayout.GONE);
                tipPercentLayout.setVisibility(LinearLayout.VISIBLE);
                break;
            case R.id.buttonSuggested:
                ratingLayout.setVisibility(LinearLayout.VISIBLE);
                tipPercentLayout.setVisibility(LinearLayout.GONE);
                break;
        }
    }

    public void b_submitClick(float billAmount, int numPeople, float tipPercent, String currencyString){
        Intent i = new Intent("net.wintastic.tipcalc.SummaryActivity");
        i.putExtra("billAmount", billAmount);
        i.putExtra("numPeople",  numPeople);
        i.putExtra("tipPercent", tipPercent);
        i.putExtra("currencyString", currencyString);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent("net.wintastic.tipcalc.SettingsActivity");
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setCurrency();
        setDefaultTip();
    }

    public void setCurrency() {
        TextView billAmount = (TextView)findViewById(R.id.labelBillAmount);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        String c = sharedPref.getString("pref_currency", "$");

        String billAmountText = getResources().getString(R.string.label_bill_amount);
        billAmount.setText(billAmountText + " (" + c + ")");
    }

}