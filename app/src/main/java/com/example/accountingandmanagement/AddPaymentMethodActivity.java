package com.example.accountingandmanagement;

import static com.example.accountingandmanagement.TransactionAdapter.settings;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.flexbox.FlexDirection;
import com.google.android.flexbox.FlexWrap;
import com.google.android.flexbox.FlexboxLayoutManager;
import com.google.android.flexbox.JustifyContent;

import java.util.ArrayList;

public class AddPaymentMethodActivity extends AppCompatActivity {

    ArrayList<NavigationBarItem> navigationBarItems = new ArrayList<>();
    NavigationBarAdapter adapter ;
    DatabaseHelper db;
    String paymentMethod;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_pament_method);

        db = new DatabaseHelper(this);

        int passedPosition = getIntent().getIntExtra("SELECTED_POS", -1);

        Button btn_back_add_pament_method = findViewById(R.id.btn_back_add_pament_method);
        ListView lv_active_pament_methods = findViewById(R.id.lv_active_pament_methods);
        RecyclerView rv_transaction_payment_method = findViewById(R.id.rv_transaction_payment_method);
        Button btn_add_pament_method = findViewById(R.id.btn_add_pament_method);

        ArrayList<String> paymentMethodList = Settings.stringToArray(settings.getPaymentMethodList());

        ArrayList<String> more_actions = new ArrayList<>();
        more_actions.add("Edit");
        more_actions.add("Delete");

        for (int i = 0; i < paymentMethodList.size(); i++){
            if (!paymentMethodList.get(i).equals("Show More")) {
                if (i == passedPosition){
                    navigationBarItems.add(new NavigationBarItem(paymentMethodList.get(i), "", R.drawable.ic_action_radio_button_checked, true, more_actions, true));
                } else {
                    navigationBarItems.add(new NavigationBarItem(paymentMethodList.get(i), "", R.drawable.ic_action_radio_button_unchecked, true, more_actions, false));
                }
            }
        }

        adapter = new NavigationBarAdapter(this,navigationBarItems);

        lv_active_pament_methods.setAdapter(adapter);

        lv_active_pament_methods.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                // Because we wrote it out fully, Java knows 100% that 'position' is an int!

                // 1. Loop through and unselect everything
                for (int i = 0; i < adapter.getCount(); i++) {
                    // We use getItem() and cast it, because your list is private inside the adapter
                    NavigationBarItem item = (NavigationBarItem) adapter.getItem(i);
                    if (item.isSelcted()) {
                        item.setSelcted(false);
                    }
                }

                // 2. Select the specific item that was clicked using the 'position'
                NavigationBarItem clickedItem = (NavigationBarItem) adapter.getItem(position);
                clickedItem.setSelcted(true);

                // 3. Refresh the screen
                adapter.notifyDataSetChanged();
            }
        });

        FlexboxLayoutManager flexboxLayoutManager = new FlexboxLayoutManager(this);

        // Wrap items to next line
        flexboxLayoutManager.setFlexWrap(FlexWrap.WRAP);

        // Horizontal direction
        flexboxLayoutManager.setFlexDirection(FlexDirection.ROW);

        // Align items nicely
        flexboxLayoutManager.setJustifyContent(JustifyContent.FLEX_START);

        ArrayList<String> paymentMethodListSUG = new ArrayList<>();
        paymentMethodListSUG.add("Bank");
        paymentMethodListSUG.add("Cash");
        paymentMethodListSUG.add("Cried cared");
        paymentMethodListSUG.add("Other");
        PaymentMethodAdapter paymentMethodAdapter = new PaymentMethodAdapter(this, paymentMethodList, new PaymentMethodAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                paymentMethod = paymentMethodList.get(position);

                ArrayList<String> arrayList = Settings.stringToArray(settings.getPaymentMethodList());

                ArrayList<String> more_actions = new ArrayList<>();
                more_actions.add("Edit");
                more_actions.add("Delete");

                navigationBarItems.clear();
                for (String paymentMethod:arrayList){
                    if (!paymentMethod.equals("Show More")) {
                        navigationBarItems.add(new NavigationBarItem(paymentMethod, "", R.drawable.ic_action_radio_button_unchecked, true, more_actions, false));
                    }
                }
                String  paymentMethodList = Settings.arrayToString(arrayList);
                settings.setPaymentMethodList(paymentMethodList);

                db.updateSettings(settings.getNameV(),settings.getTypeV(),settings.getNotesV(),settings.getTimeV(),settings.getUserId(),settings.getPaymentMethodList(),settings.getTypeList());

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onShowMoreClick(int currentSelectedPosition) {

            }
        });

        rv_transaction_payment_method.setLayoutManager(flexboxLayoutManager);

        rv_transaction_payment_method.setAdapter(paymentMethodAdapter);

        btn_add_pament_method.setOnClickListener( e -> {
            showCustomDialog("Add payment method","");
        });

        btn_back_add_pament_method.setOnClickListener( e -> {
            setResult(Activity.RESULT_OK);
            finish();
        });


    }

    public void showCustomDialog(String title, String editTextText) {
        final Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.add_payment_method_dialog);

        // make background transparent
        dialog.getWindow().setBackgroundDrawable(new android.graphics.drawable.ColorDrawable(android.graphics.Color.TRANSPARENT));

        // Access views within the dialog
        TextView tv_add_payment_method_title = dialog.findViewById(R.id.tv_add_payment_method_title);
        EditText et_add_payment_method_text = dialog.findViewById(R.id.et_add_payment_method_text);
        Button btn_cancel_add_payment_method = dialog.findViewById(R.id.btn_cancel_payment_method);
        Button btn_save_add_payment_method = dialog.findViewById(R.id.btn_save_payment_method);

        tv_add_payment_method_title.setText(title);
        if (editTextText != null || !editTextText.equals("")) {
            et_add_payment_method_text.setText(editTextText);
        }

        btn_save_add_payment_method.setOnClickListener( e -> {

            ArrayList<String> arrayList = Settings.stringToArray(settings.getPaymentMethodList());
            if (et_add_payment_method_text != null || !et_add_payment_method_text.equals("")) {

                if (!et_add_payment_method_text.getText().equals("")) {
                    arrayList.add(String.valueOf(et_add_payment_method_text.getText()));
                }

                ArrayList<String> more_actions = new ArrayList<>();
                more_actions.add("Edit");
                more_actions.add("Delete");

                navigationBarItems.clear();
                for (String paymentMethod:arrayList){
                    if (!paymentMethod.equals("Show More")) {
                        navigationBarItems.add(new NavigationBarItem(paymentMethod, "", R.drawable.ic_action_radio_button_unchecked, true, more_actions, false));
                    }
                }
                String  paymentMethodList = Settings.arrayToString(arrayList);
                settings.setPaymentMethodList(paymentMethodList);

                db.updateSettings(settings.getNameV(),settings.getTypeV(),settings.getNotesV(),settings.getTimeV(),settings.getUserId(),settings.getPaymentMethodList(),settings.getTypeList());

                adapter.notifyDataSetChanged();

                dialog.dismiss();
            }

        });

        btn_cancel_add_payment_method.setOnClickListener( e -> {dialog.dismiss();});

        dialog.show();
    }
}

/*
btn_add_inc.setOnClickListener { _ ->
            val inflater = LayoutInflater.from(this)
            val view = inflater.inflate(R.layout.dialog_input, null)

            view.btn_rearrang.isVisible = false
            view.et_dialog_input_type.isVisible = false
            view.et_dialog_input_desc.isVisible = true
            view.et_dialog_input_desc.isVisible = true
            view.et_dialog_input_number.isVisible = true
            view.tv_dialog_input_title.text = "الدخل"
            val builder = AlertDialog.Builder(this)
            builder.setView(view)

            val dialog = builder.create()
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            val cal = Calendar.getInstance()
            view.btn_dialog_input_date.setOnClickListener {
                DatePickerDialog(
                    this,
                    this,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }

            view.btn_dialog_input_cansel.setOnClickListener {
                dialog.cancel()
            }

            view.btn_dialog_input_save.setOnClickListener {
                var isNotEmpty = view.et_dialog_input_number.text.isNotEmpty() && view.et_dialog_input_desc.text.isNotEmpty()

                if (isNotEmpty) {
                    if (time == ""){
                        time = "${cal.get(Calendar.DAY_OF_MONTH)}/${cal.get(Calendar.MONTH)+1}/${cal.get(Calendar.YEAR)}"
                    }

                    db.add_inc(
                        view.et_dialog_input_number.text.toString().toDouble(),
                        view.et_dialog_input_desc.text.toString(),
                        time
                    )
                    db.database_show(TABLE_NAME_INC)
                    db.SetMonyCount()
                    time = ""
                    tv_money.text = "₪$money"
                    list_view_Inc.adapter = CustomAdapterInc(this, array_inc)
                    Toast.makeText(this, "تم حفظ البيانات", Toast.LENGTH_SHORT).show()
                    dialog.cancel()
                    log()
                } else
                    Toast.makeText(this, "ادخل البيانات المطلوبة!", Toast.LENGTH_SHORT).show()
            }

            view.btn_rearrang.setOnClickListener {
                Toast.makeText(this, "btn_rearrang", Toast.LENGTH_SHORT).show()
            }
        }
*/ // this is the ktilne code for dialog