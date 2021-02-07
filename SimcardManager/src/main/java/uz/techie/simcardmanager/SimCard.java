package uz.techie.simcardmanager;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.telecom.PhoneAccountHandle;
import android.telecom.TelecomManager;
import android.telephony.SubscriptionInfo;
import android.telephony.SubscriptionManager;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class SimCard extends AppCompatActivity {
    private String title = "Choose Simcard";
    private Context context;

    public SimCard(Context context) {
        this.context = context;
        checkPermissions();
    }




    private void checkPermissions(){
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE)
                + ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.READ_PHONE_STATE}, 1);
        }

        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
        }
    }


    public void setTitle(String title){
        this.title = title;
    }

    public void applyUssd(final String ussd) {

        if (simCarrierNames(context).size() > 1 && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String sim1 = simCarrierNames(context).get(0);
            String sim2 = simCarrierNames(context).get(1);


            AlertDialog.Builder builder = new AlertDialog.Builder(context, R.style.dialog_rounded);
            View view = View.inflate(context, R.layout.calling_dialog, null);
            builder.setView(view);
            AlertDialog dialog = builder.create();
            dialog.show();

            TextView tvSim1 = dialog.findViewById(R.id.calling_dialog_sim1);
            TextView tvSim2 = dialog.findViewById(R.id.calling_dialog_sim2);
            TextView tvTitle = dialog.findViewById(R.id.calling_dialog_title);

            tvTitle.setText(title);
            tvSim1.setText(sim1);
            tvSim2.setText(sim2);

            tvSim1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    callingAboveVersionM(1, ussd);
                }
            });
            tvSim2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    callingAboveVersionM(2, ussd);
                }
            });

        } else {
            callingBelowVersionM(ussd);
        }


    }

    private void callingAboveVersionM(int simSlotPosition, String ussd) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            TelecomManager telecomManager = (TelecomManager) context.getSystemService(Context.TELECOM_SERVICE);
            if (ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED) {

                Uri uri = null;
                if (ussd.contains("#")) {
                    ussd = ussd.replace("#", Uri.encode("#"));
                    uri = Uri.parse("tel:" + ussd + Uri.encode("#"));
                }
                else {
                    uri = Uri.parse("tel:" + ussd + Uri.encode("#"));
                }

                Intent intent = new Intent("android.intent.action.CALL");
                intent.setData(uri);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.putExtra("com.android.phone.force.slot", true);

                List<PhoneAccountHandle> phoneAccountHandleList = telecomManager.getCallCapablePhoneAccounts();

                if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 0 && simSlotPosition == 1) {
                    intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(0));
                } else if (phoneAccountHandleList != null && phoneAccountHandleList.size() > 1 && simSlotPosition == 2) {
                    intent.putExtra("android.telecom.extra.PHONE_ACCOUNT_HANDLE", phoneAccountHandleList.get(1));
                }

                if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
                }
                else {
                    context.startActivity(intent);
                }

            } else {
                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
            }
        }


    }

    private List<String> simCarrierNames(Context context) {
        List<String> carrierNames = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            try {
                final String permission = Manifest.permission.READ_PHONE_STATE;
                if (ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED) {
                    final List<SubscriptionInfo> subList;
                    subList = SubscriptionManager.from(context).getActiveSubscriptionInfoList();
                    for (int i = 0; i < subList.size(); i++) {
                        carrierNames.add(subList.get(i).getCarrierName() + "");
                    }
                } else {
                    ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.READ_PHONE_STATE}, 1);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return carrierNames;
    }


    private void callingBelowVersionM(String ussd) {
        Uri uri = null;
        if (ussd.contains("#")) {
            ussd = ussd.replace("#", Uri.encode("#"));
            uri = Uri.parse("tel:" + ussd + Uri.encode("#"));
        }
        else if (ussd.contains("*")){
            uri = Uri.parse("tel:" + ussd + Uri.encode("#"));
        }
        else {
            uri = Uri.parse("tel:" + ussd);
        }

        Intent intent = new Intent("android.intent.action.CALL");
        intent.setData(uri);intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, 1);
        } else {
            context.startActivity(intent);
        }
    }





}
