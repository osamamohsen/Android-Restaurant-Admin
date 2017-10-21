package osama.restaurantserver;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.jaredrummler.materialspinner.MaterialSpinner;

import java.util.HashMap;
import java.util.Map;

import firebaseNotification.MySingleton;
import osama.restaurantserver.Common.Common;
import osama.restaurantserver.Common.CommonLocation;
import Interface.ItemClickListner;
import Models.Request;
import ViewHolder.OrderViewHolder;
import info.hoang8f.widget.FButton;

public class OrderStatus extends AppCompatActivity {

    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MaterialSpinner spinner;
    FButton btnYes,btnNo;
    FirebaseRecyclerAdapter<Request, OrderViewHolder> adapter;
    FirebaseDatabase firebase;
    DatabaseReference requests;
    String app_server_url = "http://192.168.1.106/Mobile/PushNotification/send_notifications.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_status);

        //Init
        recyclerView = (RecyclerView) findViewById(R.id.list_orders);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        //Firebase
        firebase = FirebaseDatabase.getInstance();
        requests = firebase.getReference("Requests");
        loadOrderList();
    }


    private void loadOrderList() {
        adapter = new FirebaseRecyclerAdapter<Request, OrderViewHolder>
                (
                        Request.class,
                        R.layout.order_layout,
                        OrderViewHolder.class,
                        requests
                ) {
            @Override
            protected void populateViewHolder(OrderViewHolder viewHolder, final Request model, int position) {
                viewHolder.txt_order_id.setText(adapter.getRef(position).getKey());
                viewHolder.txt_order_address.setText(model.getAddress());
                viewHolder.txt_order_phone.setText(model.getPhone());
                viewHolder.txt_order_status.setText(Common.convertCodeToStatus(model.getStatus()));

                viewHolder.setItemClickListner(new ItemClickListner() {
                    @Override
                    public void onClick(View view, int position, boolean isLongClick) {
                        if(!CommonLocation.IsEnableGPS(OrderStatus.this)){
                            CommonLocation.displayPromptForEnablingGPS(OrderStatus.this);
                        }else{
                            Intent trackOrder = new Intent(OrderStatus.this,TrackingOrder.class);
                            Common.current_request = model;
                            startActivity(trackOrder);
                        }
                    }
                });
            }
        };

        adapter.notifyDataSetChanged();
        recyclerView.setAdapter(adapter);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        if(item.getTitle().equals(Common.UPDATE)){
            //adapter.getRef(item.getOrder()).getKey()
            showUpdateDialog(adapter.getRef(item.getOrder()).getKey(),adapter.getItem(item.getOrder()));
        }
        if(item.getTitle().equals(Common.DELETE)){
            deleteDialog(adapter.getRef(item.getOrder()).getKey());
        }
        return super.onContextItemSelected(item);
    }

    public void send_notification( final String title , final String message){
        Log.d("onClick","Started");
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        final String token = sharedPreferences.getString(getString(R.string.FCM_TOKEN),"");
        StringRequest stringRequest = new StringRequest(StringRequest.Method.POST, app_server_url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("Volley",response);
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> params = new HashMap<String, String>();
                params.put("fcm_token",token);
                params.put("title",title);
                params.put("message",message);
                return params;
            }
        };
        MySingleton.getInstance(OrderStatus.this).addToRequestque(stringRequest);
    }


    private void showUpdateDialog(final String key, final Request item) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Update Order");
        alertDialog.setMessage("Please choose status");

        LayoutInflater inflater = this.getLayoutInflater();
        final View view = inflater.inflate(R.layout.update_order_layout,null);

        spinner = (MaterialSpinner) view.findViewById(R.id.statusSpinner);
        spinner.setItems("Placed","On my way","Shipped");
        alertDialog.setView(view);
        final String localKey = key;
        alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                send_notification("Restaurant","Your order is " + spinner.getItems().get(spinner.getSelectedIndex()).toString());
                item.setStatus(String.valueOf(spinner.getSelectedIndex()));
                requests.child(localKey).setValue(item);
//                requests.child(key).setValue(item);
            }
        });

        alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });

        alertDialog.show();

    }

    private void deleteDialog(final String key) {
        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(OrderStatus.this);
        alertDialog.setTitle("Delete Order");
        alertDialog.setMessage("Are you sure you want delete order?");
        LayoutInflater inflater = this.getLayoutInflater();
        View add_menu_inflator = inflater.inflate(R.layout.is_sure,null);

        alertDialog.setView(add_menu_inflator);
        alertDialog.setIcon(R.drawable.cart);

        btnYes = (FButton) add_menu_inflator.findViewById(R.id.btnYes);
        btnNo = (FButton) add_menu_inflator.findViewById(R.id.btnNo);

        final AlertDialog dialog = alertDialog.create();
        btnYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                requests.child(key).removeValue();
                Toast.makeText(OrderStatus.this, "deleted !!!!", Toast.LENGTH_SHORT).show();
            }
        });

        btnNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }
}
