package com.ikea.myapp.models;

import com.ikea.myapp.R;

public enum ExpenseTypes {

    Flight(R.drawable.ic_airplane),
    Lodging(R.drawable.ic_bed_side),
    Rental(R.drawable.ic_car),
    PublicTransit(R.drawable.ic_public_transit),
    Food(R.drawable.ic_fastfood),
    Drinks(R.drawable.ic_drink),
    Sightseeing(R.drawable.ic_landmark),
    Activity(R.drawable.ic_ticket),
    Shopping(R.drawable.ic_shopping_bag),
    Gas(R.drawable.ic_gas_station),
    Groceries(R.drawable.ic_groceries_cart),
    Other(R.drawable.ic_receipt);

    private int image;

    ExpenseTypes(int image) {
        this.image = image;
    }

    public int getImage() {
        return image;
    }
}

