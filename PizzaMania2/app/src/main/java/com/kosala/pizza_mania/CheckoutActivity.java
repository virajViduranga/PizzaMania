package com.kosala.pizza_mania;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.kosala.pizza_mania.utils.CartDatabaseHelper;

public class CheckoutActivity extends AppCompatActivity {

    private RadioButton rbCOD, rbOnline, rbCard;
    private RadioGroup rgPayment;
    private MaterialCardView cardCOD, cardOnline, cardCardPayment;
    private LinearLayout llCardDetails;
    private EditText etCardName, etCardNumber, etExpiry, etCVV;
    private Button btnPay;
    private CartDatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);

        rgPayment = findViewById(R.id.rgPaymentMethod);
        rbCOD = findViewById(R.id.rbCOD);
        rbOnline = findViewById(R.id.rbOnline);
        rbCard = findViewById(R.id.rbCard);

        cardCOD = findViewById(R.id.cardCOD);
        cardOnline = findViewById(R.id.cardOnline);
        cardCardPayment = findViewById(R.id.cardCardPayment);

        llCardDetails = findViewById(R.id.llCardDetails);
        etCardName = findViewById(R.id.etCardName);
        etCardNumber = findViewById(R.id.etCardNumber);
        etExpiry = findViewById(R.id.etExpiry);
        etCVV = findViewById(R.id.etCVV);
        btnPay = findViewById(R.id.btnPay);

        dbHelper = new CartDatabaseHelper(this);

        // Click listeners for payment methods
        cardCOD.setOnClickListener(v -> rgPayment.check(R.id.rbCOD));
        cardOnline.setOnClickListener(v -> rgPayment.check(R.id.rbOnline));
        cardCardPayment.setOnClickListener(v -> rgPayment.check(R.id.rbCard));

        rbCOD.setOnClickListener(v -> rgPayment.check(R.id.rbCOD));
        rbOnline.setOnClickListener(v -> rgPayment.check(R.id.rbOnline));
        rbCard.setOnClickListener(v -> rgPayment.check(R.id.rbCard));

        rgPayment.setOnCheckedChangeListener((group, checkedId) -> {
            boolean showCardDetails = checkedId == R.id.rbCard;
            llCardDetails.setVisibility(showCardDetails ? View.VISIBLE : View.GONE);

            int highlightColor = Color.parseColor("#FF5722");
            setCardHighlight(cardCOD, checkedId == R.id.rbCOD, highlightColor);
            setCardHighlight(cardOnline, checkedId == R.id.rbOnline, highlightColor);
            setCardHighlight(cardCardPayment, checkedId == R.id.rbCard, highlightColor);
        });

        btnPay.setOnClickListener(v -> processPayment());
    }

    private void setCardHighlight(MaterialCardView card, boolean checked, int color) {
        if (card == null) return;
        if (checked) {
            card.setStrokeWidth(dpToPx(2));
            card.setStrokeColor(color);
        } else {
            card.setStrokeWidth(0);
            card.setStrokeColor(Color.TRANSPARENT);
        }
    }

    private int dpToPx(int dp) {
        return (int) TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, getResources().getDisplayMetrics());
    }

    private void processPayment() {
        int checkedId = rgPayment.getCheckedRadioButtonId();
        if (checkedId == -1) {
            Toast.makeText(this, "Select payment method!", Toast.LENGTH_SHORT).show();
            return;
        }

        if (checkedId == R.id.rbCard) {
            String name = etCardName.getText().toString().trim();
            String card = etCardNumber.getText().toString().trim().replaceAll("\\s+", "");
            String expiry = etExpiry.getText().toString().trim();
            String cvv = etCVV.getText().toString().trim();

            if (name.isEmpty() || card.isEmpty() || expiry.isEmpty() || cvv.isEmpty()) {
                Toast.makeText(this, "Enter all card details!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (card.length() < 12 || card.length() > 19 || !card.matches("\\d+")) {
                Toast.makeText(this, "Invalid card number!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!expiry.matches("(0[1-9]|1[0-2])\\/\\d{2}")) {
                Toast.makeText(this, "Invalid expiry format (MM/YY)", Toast.LENGTH_SHORT).show();
                return;
            }

            if (cvv.length() < 3 || cvv.length() > 4 || !cvv.matches("\\d+")) {
                Toast.makeText(this, "Invalid CVV!", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        // ✅ Now open map screen
        Intent intent = new Intent(this, LocationSelectActivity.class);
        startActivity(intent);
    }
}
