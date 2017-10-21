package ViewHolder;

import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.View;
import android.widget.TextView;

import osama.restaurantserver.Common.Common;
import Interface.ItemClickListner;
import osama.restaurantserver.R;


/**
 * Created by osama on 10/13/2017.
 */

public class OrderViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener , View.OnCreateContextMenuListener{
    public TextView txt_order_id,txt_order_status,txt_order_address,txt_order_phone;
    private ItemClickListner itemClickListner;
    public OrderViewHolder(View itemView) {
        super(itemView);
        txt_order_id = (TextView) itemView.findViewById(R.id.txt_order_id);
        txt_order_status = (TextView) itemView.findViewById(R.id.txt_order_status);
        txt_order_address = (TextView) itemView.findViewById(R.id.txt_order_address);
        txt_order_phone = (TextView) itemView.findViewById(R.id.txt_order_phone);
        itemView.setOnClickListener(this);
        itemView.setOnCreateContextMenuListener(this);
    }

    public void setItemClickListner(ItemClickListner itemClickListner){
        this.itemClickListner = itemClickListner;
    }
    @Override
    public void onClick(View view) {
        itemClickListner.onClick(view,getAdapterPosition(),false);
    }

    @Override
    public void onCreateContextMenu(ContextMenu contextMenu, View view, ContextMenu.ContextMenuInfo contextMenuInfo) {
        contextMenu.setHeaderTitle("Select The Action");
        contextMenu.add(0,0,getAdapterPosition(),Common.UPDATE);
        contextMenu.add(0,1,getAdapterPosition(),Common.DELETE);
    }
}
