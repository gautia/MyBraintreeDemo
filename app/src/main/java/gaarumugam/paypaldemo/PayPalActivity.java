package gaarumugam.paypaldemo;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Toast;

import com.braintreepayments.api.BraintreeFragment;
import com.braintreepayments.api.PayPal;
import com.braintreepayments.api.exceptions.BraintreeError;
import com.braintreepayments.api.exceptions.ErrorWithResponse;
import com.braintreepayments.api.exceptions.InvalidArgumentException;
import com.braintreepayments.api.interfaces.BraintreeCancelListener;
import com.braintreepayments.api.interfaces.BraintreeErrorListener;
import com.braintreepayments.api.interfaces.PaymentMethodNonceCreatedListener;
import com.braintreepayments.api.models.PayPalRequest;
import com.braintreepayments.api.models.PaymentMethodNonce;

/**
 * Created by gaarumugam on 11/11/2017.
 */

public class PayPalActivity extends  AppCompatActivity implements PaymentMethodNonceCreatedListener
        ,BraintreeErrorListener,BraintreeCancelListener {

    private String mAuthorization = "";
    protected BraintreeFragment mBraintreeFragment;
    ProgressDialog pDialog_tokenization;
    Button launchPayment;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.paypal_activity);
        launchPayment = (Button)findViewById(R.id.paypal_single_payment_button);
        launchPayment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSinglePayment();
            }
        });

    }



    private void launchSinglePayment(){
        try {
            mAuthorization = "eyJ2ZXJzaW9uIjoyLCJhdXRob3JpemF0aW9uRmluZ2VycHJpbnQiOiJiNjIwOTg1NDI4OGIxOWMyNDQzNDAwYWZhZmRmYjNjMjQwNGJlMWM3NmY3NWE0ZjBhYjcyMzYwMzU1NmRmYjFlfGNsaWVudF9pZD1jbGllbnRfaWQkc2FuZGJveCQ0ZHByYmZjNnBoNTk1Y2NqXHUwMDI2Y3JlYXRlZF9hdD0yMDE4LTAzLTIxVDEwOjUxOjI4LjY0MTU2ODc0NiswMDAwXHUwMDI2bWVyY2hhbnRfaWQ9YzNueHE1d3ptOHh5eHBydCIsImNvbmZpZ1VybCI6Imh0dHBzOi8vYXBpLnNhbmRib3guYnJhaW50cmVlZ2F0ZXdheS5jb206NDQzL21lcmNoYW50cy9jM254cTV3em04eHl4cHJ0L2NsaWVudF9hcGkvdjEvY29uZmlndXJhdGlvbiIsImNoYWxsZW5nZXMiOltdLCJlbnZpcm9ubWVudCI6InNhbmRib3giLCJjbGllbnRBcGlVcmwiOiJodHRwczovL2FwaS5zYW5kYm94LmJyYWludHJlZWdhdGV3YXkuY29tOjQ0My9tZXJjaGFudHMvYzNueHE1d3ptOHh5eHBydC9jbGllbnRfYXBpIiwiYXNzZXRzVXJsIjoiaHR0cHM6Ly9hc3NldHMuYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhdXRoVXJsIjoiaHR0cHM6Ly9hdXRoLnZlbm1vLnNhbmRib3guYnJhaW50cmVlZ2F0ZXdheS5jb20iLCJhbmFseXRpY3MiOnsidXJsIjoiaHR0cHM6Ly9jbGllbnQtYW5hbHl0aWNzLnNhbmRib3guYnJhaW50cmVlZ2F0ZXdheS5jb20vYzNueHE1d3ptOHh5eHBydCJ9LCJ0aHJlZURTZWN1cmVFbmFibGVkIjpmYWxzZSwicGF5cGFsRW5hYmxlZCI6dHJ1ZSwicGF5cGFsIjp7ImRpc3BsYXlOYW1lIjoidGVzdCBmYWNpbGl0YXRvcidzIFRlc3QgU3RvcmUiLCJjbGllbnRJZCI6IkFlNy1GU1hNeHcxQkhNUmhOa3V3djY1S2Y1eXk0R040TV81eFFHbExidE51eFRmSEd3eUIyakpmWlEzenBKQ01KNGtTQ1A3Z19vOWVCeWR4IiwicHJpdmFjeVVybCI6Imh0dHBzOi8vZXhhbXBsZS5jb20iLCJ1c2VyQWdyZWVtZW50VXJsIjoiaHR0cHM6Ly9leGFtcGxlLmNvbSIsImJhc2VVcmwiOiJodHRwczovL2Fzc2V0cy5icmFpbnRyZWVnYXRld2F5LmNvbSIsImFzc2V0c1VybCI6Imh0dHBzOi8vY2hlY2tvdXQucGF5cGFsLmNvbSIsImRpcmVjdEJhc2VVcmwiOm51bGwsImFsbG93SHR0cCI6dHJ1ZSwiZW52aXJvbm1lbnROb05ldHdvcmsiOmZhbHNlLCJlbnZpcm9ubWVudCI6Im9mZmxpbmUiLCJ1bnZldHRlZE1lcmNoYW50IjpmYWxzZSwiYnJhaW50cmVlQ2xpZW50SWQiOiJtYXN0ZXJjbGllbnQzIiwiYmlsbGluZ0FncmVlbWVudHNFbmFibGVkIjp0cnVlLCJtZXJjaGFudEFjY291bnRJZCI6IlVTRCIsImN1cnJlbmN5SXNvQ29kZSI6IlVTRCJ9LCJtZXJjaGFudElkIjoiYzNueHE1d3ptOHh5eHBydCIsInZlbm1vIjoib2ZmIn0=";
            mBraintreeFragment = BraintreeFragment.newInstance(this, mAuthorization);
            PayPal.requestOneTimePayment(mBraintreeFragment, getPayPalRequest("1.00"));

            // mBraintreeFragment is ready to use!
        } catch (InvalidArgumentException e) {
            e.printStackTrace();
            // There was an issue with your authorization string.
        }
    }



    @Override
    public void onPaymentMethodNonceCreated(PaymentMethodNonce paymentMethodNonce) {

        // Send this nonce to your server
        String nonce = paymentMethodNonce.getNonce();
        Toast.makeText(this, "Nonce :-" + nonce, Toast.LENGTH_SHORT).show();

    }

    private PayPalRequest getPayPalRequest(@Nullable String amount) {
        PayPalRequest request = new PayPalRequest(amount);

        request.displayName(Settings.getPayPalDisplayName(this));

        String landingPageType = Settings.getPayPalLandingPageType(this);
        if (getString(R.string.paypal_landing_page_type_billing).equals(landingPageType)) {
            request.landingPageType(PayPalRequest.LANDING_PAGE_TYPE_BILLING);
        } else if (getString(R.string.paypal_landing_page_type_login).equals(landingPageType)) {
            request.landingPageType(PayPalRequest.LANDING_PAGE_TYPE_LOGIN);
        }

        String intentType = Settings.getPayPalIntentType(this);
        if (intentType.equals(getString(R.string.paypal_intent_authorize))) {
            request.intent(PayPalRequest.INTENT_AUTHORIZE);
        } else if (intentType.equals(getString(R.string.paypal_intent_order))) {
            request.intent(PayPalRequest.INTENT_ORDER);
        } else if (intentType.equals(getString(R.string.paypal_intent_sale))) {
            request.intent(PayPalRequest.INTENT_SALE);
        }

        if (Settings.isPayPalUseractionCommitEnabled(this)) {
            request.userAction(PayPalRequest.USER_ACTION_COMMIT);
        }

        if (Settings.isPayPalCreditOffered(this)) {
            request.offerCredit(true);
        }

        return request;
    }

  /*  @Override
    public void onConfigurationFetched(Configuration configuration) {
        if (Settings.shouldCollectDeviceData(this)) {
            DataCollector.collectDeviceData(mBraintreeFragment, new BraintreeResponseListener<String>() {
                @Override
                public void onResponse(String deviceData) {
                    mDeviceData = deviceData;
                }
            });
        }
    }*/

    @Override
    public void onError(Exception error) {

        Toast.makeText(this, "Error:-"+ error.getMessage(), Toast.LENGTH_LONG).show();

        if (error instanceof ErrorWithResponse) {
            ErrorWithResponse errorWithResponse = (ErrorWithResponse) error;

            BraintreeError cardErrors = errorWithResponse.errorFor("creditCard");
            if (cardErrors != null) {
                // There is an issue with the credit card.
                BraintreeError expirationMonthError = cardErrors.errorFor("expirationMonth");
                if (expirationMonthError != null) {
                    // There is an issue with the expiration month.
                    //  setErrorMessage(expirationMonthError.getMessage());
                    Toast.makeText(this, "Error:-"+ expirationMonthError.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public void onCancel(int requestCode) {
        // Use this to handle a canceled activity, if the given requestCode is important.
        // You may want to use this callback to hide loading indicators, and prepare your UI for input
        Toast.makeText(this, "Transaction Cancelled RequestCode :- "+ requestCode,Toast.LENGTH_LONG);
    }
}
