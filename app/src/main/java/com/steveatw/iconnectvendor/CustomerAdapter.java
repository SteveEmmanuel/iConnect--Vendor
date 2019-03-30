package com.steveatw.iconnectvendor;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

public class CustomerAdapter extends RecyclerView.Adapter<CustomerAdapter.MyViewHolder>
        implements Filterable {
    private Context context;
    private List<Customer> customerList;
    private List<Customer> customerListFiltered;
    private CustomerAdapterListener listener;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name, phone, email;

        public MyViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.name);
            phone = view.findViewById(R.id.phone);
            email = view.findViewById(R.id.email);

            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    // send selected contact in callback
                    listener.onCustomerSelected(customerListFiltered.get(getAdapterPosition()));
                }
            });
        }
    }


    public CustomerAdapter(Context context, List<Customer> customerList, CustomerAdapterListener listener) {
        this.context = context;
        this.listener = listener;
        this.customerList = customerList;
        this.customerListFiltered = customerList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.customer_row_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position) {
        final Customer contact = customerListFiltered.get(position);
        holder.name.setText(contact.getName());
        holder.phone.setText(contact.getPhone());

    }

    @Override
    public int getItemCount() {
        return customerListFiltered.size();
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence charSequence) {
                String charString = charSequence.toString();
                if (charString.isEmpty()) {
                    customerListFiltered = customerList;
                } else {
                    List<Customer> filteredList = new ArrayList<>();
                    for (Customer row : customerList) {

                        // name match condition. this might differ depending on your requirement
                        // here we are looking for name or phone number match
                        if (row.getName().toLowerCase().contains(charString.toLowerCase()) || row.getPhone().contains(charSequence)) {
                            filteredList.add(row);
                        }
                    }

                    customerListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = customerListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
                customerListFiltered = (ArrayList<Customer>) filterResults.values;
                notifyDataSetChanged();
            }
        };
    }

    public interface CustomerAdapterListener {
        void onCustomerSelected(Customer customer);
    }
}